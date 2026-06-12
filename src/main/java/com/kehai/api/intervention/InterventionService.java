package com.kehai.api.intervention;

import com.kehai.api.common.exception.ResourceNotFoundException;
import com.kehai.api.common.security.TenantContext;
import com.kehai.api.intervention.dto.InterventionListItem;
import com.kehai.api.intervention.dto.InterventionListResponse;
import com.kehai.api.scoring.ChurnRiskScore;
import com.kehai.api.scoring.ChurnRiskScoreRepository;
import com.kehai.api.tenant.Tenant;
import com.kehai.api.tenant.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class InterventionService {

    private static final Logger log = LoggerFactory.getLogger(InterventionService.class);

    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 500;

    private final TenantRepository tenantRepository;
    private final ChurnRiskScoreRepository scoreRepository;

    public InterventionService(TenantRepository tenantRepository, ChurnRiskScoreRepository scoreRepository) {
        this.tenantRepository = tenantRepository;
        this.scoreRepository = scoreRepository;
    }

    @Transactional(readOnly = true)
    public InterventionListResponse getInterventionList(
            ChurnRiskScore.RiskTier tier, Integer requestedLimit
    ) {
        Tenant tenant = currentTenant();
        int limit = resolveLimit(requestedLimit);
        Pageable pageable = PageRequest.of(0, limit);

        List<ChurnRiskScore> scores = scoreRepository
                .findLatestScoresByTenantAndTier(tenant, tier, pageable);

        List<InterventionListItem> items = scores.stream()
                .map(InterventionListItem::from)
                .toList();

        log.info("Intervention list: tenant={} tier={} returned={}",
                tenant.getSlug(), tier, items.size());

        return new InterventionListResponse(
                Instant.now(),
                items.size(),
                tier,
                items
        );
    }

    private int resolveLimit(Integer requested) {
        if (requested == null || requested <= 0) return DEFAULT_LIMIT;
        return Math.min(requested, MAX_LIMIT);
    }

    private Tenant currentTenant() {
        String slug = TenantContext.getTenantId();
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", slug));
    }
}
