package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.dto.OnChainSentimentDto;
import com.svsbrains.svscrypto.model.dto.PredictionResponseDto;
import com.svsbrains.svscrypto.service.OnChainSentimentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OnChainSentimentServiceImpl implements OnChainSentimentService {
    private final RestTemplate restTemplate;

    private final String GET_ALL_METRICS_URL = "http://localhost:8000/get-indicator-onchain";
    private final String GET_NEWS_URL = "http://localhost:8000/estimate-news";

    public OnChainSentimentServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public OnChainSentimentDto getAllMetrics(String symbol) {
        try {
            String url = String.format("%s/%s", GET_ALL_METRICS_URL, symbol);
            ResponseEntity<OnChainSentimentDto> response =
                    restTemplate.getForEntity(url, OnChainSentimentDto.class);

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Communication error with python: " + e.getMessage());
            return null;
        }
    }
}
