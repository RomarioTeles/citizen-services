package io.github.romarioteles.citizenservices.service.repository;

import io.github.romarioteles.citizenservices.service.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {
}
