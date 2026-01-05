package com.svsbrains.svscrypto.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * A client for communicating with the Analysis Service.
 * <p>
 * This class provides utility methods to perform GET requests to the Analysis Service endpoints.
 * It wraps the Spring {@link RestTemplate}.
 * </p>
 * <p>
 * Provides two overloads of the {@code get} method:
 * <ul>
 *     <li>For simple object types (using {@link Class} reference)</li>
 *     <li>For generic or parameterized types (using {@link ParameterizedTypeReference})</li>
 * </ul>
 * </p>
 */
@Component
@RequiredArgsConstructor
public class AnalysisEngineClient {

    /** Spring RestTemplate used to perform HTTP requests */
    private final RestTemplate restTemplate;

    /**
     * Performs a GET request to the specified URL and maps the response to the provided class type.
     *
     * @param url the full URL to request
     * @param responseType the class type to map the response to
     * @param <T> the type of the response
     * @return an instance of {@code T} containing the response, or {@code null} if an error occurs
     */
    public <T> T get(String url, Class<T> responseType) {
        try {
            return restTemplate.getForObject(url, responseType);
        } catch (Exception e) {
            System.err.println("Communication error with python: " + e.getMessage());
            return null;
        }
    }

    /**
     * Performs a GET request to the specified URL and maps the response to the provided
     * parameterized type reference. Useful for collections or generic types.
     *
     * @param url the full URL to request
     * @param typeRef the parameterized type reference for the response
     * @param <T> the type of the response
     * @return an instance of {@code T} containing the response, or {@code null} if an error occurs
     */
    public <T> T get(String url, ParameterizedTypeReference<T> typeRef) {
        try {
            return restTemplate
                    .exchange(url, HttpMethod.GET, null, typeRef)
                    .getBody();
        } catch (Exception e) {
            System.err.println("Communication error with python: " + e.getMessage());
            return null;
        }
    }
}