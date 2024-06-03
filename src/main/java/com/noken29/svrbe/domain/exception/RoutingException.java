package com.noken29.svrbe.domain.exception;

import lombok.Getter;

@Getter
public class RoutingException extends IllegalStateException {

    private final Long customerId;

    public RoutingException(String message, Long customerId) {
        super(message);
        this.customerId = customerId;
    }
}
