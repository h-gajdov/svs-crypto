package com.svsbrains.svscrypto.util;

import org.springframework.http.ResponseEntity;

/**
 * Utility class for building standard API responses in controllers.
 */
public class ResponseUtils {
    /**
     * Returns a ResponseEntity with HTTP 200 OK if the data is not null,
     * otherwise returns HTTP 503 Service Unavailable.
     *
     * @param data the response body
     * @param <T>  type of the response
     * @return ResponseEntity containing the data or HTTP 503
     */
    public static <T> ResponseEntity<T> respondOrServiceUnavailable(T data) {
        return data != null ? ResponseEntity.ok(data) : ResponseEntity.status(503).build();
    }
}
