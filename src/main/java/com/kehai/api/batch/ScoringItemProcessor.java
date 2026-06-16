package com.kehai.api.batch;


import com.kehai.api.customer.Customer;
import com.kehai.api.scoring.ChurnRiskScore;
import com.kehai.api.scoring.ScoringResult;
import com.kehai.api.scoring.ScoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ScoringItemProcessor implements ItemProcessor<Customer, ChurnRiskScore> {

    private static final Logger log = LoggerFactory.getLogger(ScoringItemProcessor.class);

    private final ScoringService scoringService;

    public ScoringItemProcessor(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @Override
    public ChurnRiskScore process(@NonNull Customer customer) throws Exception {
        try {
            ScoringResult result = scoringService.scoreCustomer(customer);
            return result.score();
        } catch (Exception ex) {
            // Re-throw so Spring Batch's skip handler can act on it.
            // We log here to keep the per-customer failure context.
            log.warn("Failed to score customer={} tenant={}: {}",
                    customer.getExternalId(),
                    customer.getTenant().getSlug(),
                    ex.getMessage());
            throw ex;
        }
    }
}
