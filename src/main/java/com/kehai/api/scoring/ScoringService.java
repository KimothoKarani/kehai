package com.kehai.api.scoring;

import com.kehai.api.common.exception.ResourceNotFoundException;
import com.kehai.api.common.security.TenantContext;
import com.kehai.api.customer.Customer;
import com.kehai.api.customer.CustomerRepository;
import com.kehai.api.scoring.dto.RiskScoreResponse;
import com.kehai.api.scoring.features.FeatureEngineeringService;
import com.kehai.api.scoring.features.FeatureVector;
import com.kehai.api.scoring.model.LogisticRegressionModel;
import com.kehai.api.scoring.model.ModelPrediction;
import com.kehai.api.tenant.Tenant;
import com.kehai.api.tenant.TenantRepository;
import com.kehai.api.narrative.NarrativeGenerator;
import com.kehai.api.narrative.NarrativeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Service
public class ScoringService {

    public static final Logger log = LoggerFactory.getLogger(ScoringService.class);

    private final TenantRepository tenantRepository;
    private final CustomerRepository customerRepository;
    private final FeatureEngineeringService featureService;
    private final LogisticRegressionModel model;
    private final ChurnRiskScoreRepository scoreRepository;
    private final ScoringProperties properties;
    private final NarrativeGenerator narrativeGenerator;


    public ScoringService(TenantRepository tenantRepository,
                          CustomerRepository customerRepository,
                          FeatureEngineeringService featureService,
                          LogisticRegressionModel model,
                          ChurnRiskScoreRepository scoreRepository,
                          ScoringProperties properties, NarrativeGenerator narrativeGenerator) {
        this.tenantRepository = tenantRepository;
        this.customerRepository = customerRepository;
        this.featureService = featureService;
        this.model = model;
        this.scoreRepository = scoreRepository;
        this.properties = properties;
        this.narrativeGenerator = narrativeGenerator;
    }

    /** HTTP-facing: resolves customer from externalId, returns full response DTO. */
    @Transactional
    public RiskScoreResponse scoreCustomer(String externalId) {
        Tenant tenant = currentTenant();
        Customer customer = customerRepository
                .findByTenantAndExternalId(tenant, externalId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", externalId
                ));

        ScoringResult result = scoreCustomer(customer);
        return RiskScoreResponse.from(result.score(), result.prediction().contributions());
    }

    /** Internal/batch-facing: scores a Customer directly. Returns saved entity + prediction. */
    @Transactional
    public ScoringResult scoreCustomer(Customer customer) {
        FeatureVector vector = featureService.computeFor(customer);
        ModelPrediction prediction = model.predict(vector);

        NarrativeResult narrative = narrativeGenerator.generate(prediction.score(), vector);

        ChurnRiskScore score = new ChurnRiskScore();
        score.setTenant(customer.getTenant());
        score.setCustomer(customer);
        score.setScore(BigDecimal.valueOf(prediction.score()).setScale(4, RoundingMode.HALF_UP));
        score.setTimeHorizonDays(properties.getTimeHorizonDays());
        score.setRiskTier(toRiskTier(prediction.score()));
        score.setNarrative(narrative.narrative());
        score.setRecommendedAction(narrative.recommendedAction());
        score.setScoredAt(Instant.now());
        score.setModelVersion(prediction.modelVersion());

        ChurnRiskScore saved = scoreRepository.save(score);

        log.debug("Scored customer={} tenant={} score={} tier={}",
                customer.getExternalId(), customer.getTenant().getSlug(),
                saved.getScore(), saved.getRiskTier());

        return new ScoringResult(saved, prediction);

    }

    @Transactional(readOnly = true)
    public RiskScoreResponse getLatestScore(String externalId) {
        Tenant tenant = currentTenant();
        Customer customer = customerRepository.findByTenantAndExternalId(tenant, externalId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", externalId));

        ChurnRiskScore score = scoreRepository
                .findTopByCustomerOrderByScoredAtDesc(customer)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ChurnRiskScore for customer", externalId
                ));
        return RiskScoreResponse.from(score);
    }

    private Tenant currentTenant() {
        String slug = TenantContext.getTenantId();
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", slug));
    }


    private ChurnRiskScore.RiskTier toRiskTier(double score) {
        double high = properties.getRiskThresholds().getHigh();
        double medium = properties.getRiskThresholds().getMedium();
        if (score >= high) return ChurnRiskScore.RiskTier.HIGH;
        if (score >= medium) return ChurnRiskScore.RiskTier.MEDIUM;
        return ChurnRiskScore.RiskTier.LOW;
    }
}
