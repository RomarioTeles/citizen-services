package io.github.romarioteles.citizenservices.service.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ServiceNotFoundException extends RuntimeException {

    public ServiceNotFoundException(Long id) {
        super("Service not found with id: " + id);
    }
}
