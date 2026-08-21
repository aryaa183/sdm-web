package com.aryaa.sdm.exception;

/** Thrown when an operation is requested against invalid domain state, e.g. billing a patient who was never admitted. */
public class InvalidOperationException extends RuntimeException {
    public InvalidOperationException(String message) {
        super(message);
    }
}
