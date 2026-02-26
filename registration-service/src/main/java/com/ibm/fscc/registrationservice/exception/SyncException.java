package com.ibm.fscc.registrationservice.exception;

/**
 * Exception thrown when there is a synchronization issue between Kafka and
 * MongoDB.
 * This typically occurs when a message is successfully processed in one system
 * but fails in the other, leading to data inconsistency.
 */
public class SyncException extends RuntimeException {

    public SyncException(String message) {
        super(message);
    }

    public SyncException(String message, Throwable cause) {
        super(message, cause);
    }
}
