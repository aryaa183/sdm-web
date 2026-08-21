package com.aryaa.sdm.exception;

/** Thrown when a requested region, hospital, patient, or bill doesn't exist. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
