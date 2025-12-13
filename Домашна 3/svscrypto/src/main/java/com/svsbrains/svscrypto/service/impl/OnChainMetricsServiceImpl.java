package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.dto.EstimateNewsDto;
import com.svsbrains.svscrypto.model.dto.OnChainMetricsDto;
import com.svsbrains.svscrypto.model.dto.OnChainSentimentDto;
import com.svsbrains.svscrypto.service.OnChainService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OnChainMetricsServiceImpl implements OnChainService {
    private final RestTemplate restTemplate;

    private final String GET_ALL_METRICS = "http://localhost:8000/metrics";
    private final String GET_SENTIMENT_INDICATOR = "http://localhost:8000/get-indicator-onchain";
    private final String GET_NEWS = "http://localhost:8000/estimate-news";

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
}
