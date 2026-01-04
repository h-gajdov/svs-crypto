package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.dto.*;
import com.svsbrains.svscrypto.service.OnChainService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class OnChainMetricsServiceImpl implements OnChainService {
    private final RestTemplate restTemplate;

    private final String GET_ALL_METRICS = "http://localhost:8000/metrics";
    private final String GET_SENTIMENT_INDICATOR = "http://localhost:8000/get-indicator-onchain";
    private final String GET_NEWS = "http://localhost:8000/estimate-news";
    private final String GET_WHALE_MOVEMENTS = "http://localhost:8000/whale-movements";
    private final String GET_EXCHANGE_FLOW = "http://localhost:8000/exchange-flow";

    public OnChainMetricsServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public OnChainMetricsDto getAllMetrics(String symbol) {
        try {
            String url = String.format("%s/%s", GET_ALL_METRICS, symbol);
            ResponseEntity<OnChainMetricsDto> response =
                    restTemplate.getForEntity(url, OnChainMetricsDto.class);

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Communication error with python: " + e.getMessage());
            return null;
        }
    }

    @Override
    public OnChainSentimentDto getSentimentFromMetrics(String symbol) {
        try {
            String url = String.format("%s/%s", GET_SENTIMENT_INDICATOR, symbol);
            ResponseEntity<OnChainSentimentDto> response =
                    restTemplate.getForEntity(url, OnChainSentimentDto.class);

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Communication error with python: " + e.getMessage());
            return null;
        }
    }

    @Override
    public EstimateNewsDto estimateNews(String symbol) {
        try {
            String url = String.format("%s/%s", GET_NEWS, symbol);
            ResponseEntity<EstimateNewsDto> response =
                    restTemplate.getForEntity(url, EstimateNewsDto.class);

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Communication error with python: " + e.getMessage());
            return null;
        }
    }

    @Override
    public ExchangeFlowDto getExchangeFlows(String symbol) {
        try {
            String url = String.format("%s/%s", GET_EXCHANGE_FLOW, symbol);
            ResponseEntity<ExchangeFlowDto> response =
                    restTemplate.getForEntity(url, ExchangeFlowDto.class);

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Communication error with python: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<WhaleMovementDto> getWhaleMovements() {
        try {
            ResponseEntity<List<WhaleMovementDto>> response =
                    restTemplate.exchange(
                            GET_WHALE_MOVEMENTS,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<>() {}
                    );

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Communication error with python: " + e.getMessage());
            return List.of();
        }
    }
}
