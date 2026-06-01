package com.kehai.api.common.exception;

public class ResourceNotFoundException extends KehaiException {

    public ResourceNotFoundException(String resourceType, String identifier) {
        super(String.format("%s not found: %s", resourceType, identifier));
    }
}
