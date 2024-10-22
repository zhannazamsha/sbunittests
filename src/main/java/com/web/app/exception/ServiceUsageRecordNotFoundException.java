package com.web.app.exception;

public class ServiceUsageRecordNotFoundException extends RuntimeException {

    public ServiceUsageRecordNotFoundException(String message) {
        super(message);
    }
}
