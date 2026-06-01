package com.kehai.api.ingestion;

import com.kehai.api.common.exception.ResourceNotFoundException;
import com.kehai.api.common.security.TenantContext;
import com.kehai.api.customer.Customer;
import com.kehai.api.customer.CustomerRepository;
import com.kehai.api.ingestion.dto.EventRequest;
import com.kehai.api.ingestion.dto.EventResponse;
import com.kehai.api.tenant.Tenant;
import com.kehai.api.tenant.TenantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventIngestionService {
    private static final Logger log = LoggerFactory.getLogger(EventIngestionService.class);

    private final TenantRepository tenantRepository;
    private final CustomerRepository customerRepository;
    private final BehavioralEventRepository eventRepository;

    public EventIngestionService(TenantRepository tenantRepository,
                                 CustomerRepository customerRepository,
                                 BehavioralEventRepository eventRepository) {
        this.tenantRepository = tenantRepository;
        this.customerRepository = customerRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventResponse ingest(EventRequest request) {
        Tenant tenant = currentTenant();
        Customer customer = findOrCreateCustomer(tenant, request.externalCustomerId());

        BehavioralEvent event = new BehavioralEvent();
        event.setTenant(tenant);
        event.setCustomer(customer);
        event.setEventType(request.eventType());
        event.setOccurredAt(request.occurredAt());
        event.setProperties(request.properties());

        BehavioralEvent saved = eventRepository.save(event);

        log.info("Ingested event {} for tenant={} customer={} type={}",
                saved.getId(), tenant.getSlug(),
                customer.getExternalId(), saved.getEventType());

        return EventResponse.accepted(saved.getId(), saved.getReceivedAt());
    }

    private Tenant currentTenant() {
        String slug = TenantContext.getTenantId();
        return tenantRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", slug));
    }

    private Customer findOrCreateCustomer(Tenant tenant, String externalId) {
        return customerRepository.findByTenantAndExternalId(tenant, externalId)
                .orElseGet(() -> createCustomer(tenant, externalId));
    }

    private Customer createCustomer(Tenant tenant, String externalId) {
        Customer customer = new Customer();
        customer.setTenant(tenant);
        customer.setExternalId(externalId);
        customer.setStatus(Customer.Status.ACTIVE);
        Customer saved = customerRepository.save(customer);

        log.info("Auto-created customer {} for tenant={}",
                externalId, tenant.getSlug());

        return saved;
    }
}
