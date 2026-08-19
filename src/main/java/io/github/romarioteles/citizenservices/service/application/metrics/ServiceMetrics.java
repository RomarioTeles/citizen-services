package io.github.romarioteles.citizenservices.service.application.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class ServiceMetrics {

    private final Counter created;
    private final Counter consulted;
    private final Counter notFound;

    public ServiceMetrics(MeterRegistry registry) {
        this.created   = Counter.builder("citizen.services.registrations").register(registry);
        this.consulted = Counter.builder("citizen.services.consulted").register(registry);
        this.notFound  = Counter.builder("citizen.services.not_found").register(registry);
    }

    public void incrementCreated()   { created.increment(); }
    public void incrementConsulted() { consulted.increment(); }
    public void incrementNotFound()  { notFound.increment(); }
}
