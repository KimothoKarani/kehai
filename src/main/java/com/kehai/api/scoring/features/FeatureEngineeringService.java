package com.kehai.api.scoring.features;

import com.kehai.api.common.exception.ResourceNotFoundException;
import com.kehai.api.common.security.TenantContext;
import com.kehai.api.customer.Customer;
import com.kehai.api.customer.CustomerRepository;
import com.kehai.api.ingestion.BehavioralEvent;
import com.kehai.api.ingestion.BehavioralEventRepository;
import com.kehai.api.tenant.Tenant;
import com.kehai.api.tenant.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class FeatureEngineeringService {

    private static final Logger log = LoggerFactory.getLogger(FeatureEngineeringService.class);

    // We always look back this far to ensure even long-horizon
    // features (engagement_trend_slope over 90 days) have data
    private static final int LOOKBACK_DAYS = 90;

    private final TenantRepository tenantRepository;
    private final CustomerRepository customerRepository;
    private final BehavioralEventRepository eventRepository;
    private final FeatureVectorRepository vectorRepository;
    private final List<FeatureComputer> computers;

    public FeatureEngineeringService(TenantRepository tenantRepository,
                                     CustomerRepository customerRepository,
                                     BehavioralEventRepository eventRepository,
                                     FeatureVectorRepository vectorRepository,
                                     List<FeatureComputer> computers) {
        this.tenantRepository = tenantRepository;
        this.customerRepository = customerRepository;
        this.eventRepository = eventRepository;
        this.vectorRepository = vectorRepository;
        this.computers = computers;

        log.info("FeatureEngineeringService initialized with {} computers: {}",
                computers.size(),
                computers.stream().map(c -> c.getClass().getSimpleName()).toList());
    }

    @Transactional
    public FeatureVector computeFor(String externalCustomerId) {
        Tenant tenant = currentTenant();
        Customer customer = customerRepository
                .findByTenantAndExternalId(tenant, externalCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", externalCustomerId));

        Instant now = Instant.now();
        Instant since = now.minus(LOOKBACK_DAYS, ChronoUnit.DAYS);

        List<BehavioralEvent> events = eventRepository
                .findByCustomerAndOccurredAtAfterOrderByOccurredAtDesc(customer, since);

        FeatureVector vector = new FeatureVector();
        vector.setTenant(tenant);
        vector.setCustomer(customer);
        vector.setComputedAt(now);

        // Each computer mutates the vector with its contribution
        for (FeatureComputer computer : computers) {
            computer.compute(vector, events, now);
        }

        FeatureVector saved = vectorRepository.save(vector);

        log.info("Computed feature vector {} for customer={} tenant={} ({} events analyzed)",
                saved.getId(), externalCustomerId, tenant.getSlug(), events.size());

        return saved;
    }

    @Transactional(readOnly = true)
    public FeatureVector getLatest(String externalCustomerId) {
        Tenant tenant = currentTenant();
        Customer customer = customerRepository
                .findByTenantAndExternalId(tenant, externalCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer", externalCustomerId
                ));

        return vectorRepository.findTopByCustomerOrderByComputedAtDesc(customer)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FeatureVector for customer", externalCustomerId
                ));
    }


    private Tenant currentTenant() {
        String slug =   TenantContext.getTenantId();
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", slug));
    }

}
