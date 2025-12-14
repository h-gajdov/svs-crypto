package com.svsbrains.svscrypto.repository;

import com.svsbrains.svscrypto.model.dto.SymbolAnalysisResponse;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Repository
public class TechnicalAnalysisRepository {

    private static final String BASE_URL = "http://localhost:8000";
    private final RestTemplate restTemplate = new RestTemplate();

    public SymbolAnalysisResponse analyzeSymbol(String symbol) {
        return restTemplate.getForObject(
                BASE_URL + "/analysis/" + symbol,
                SymbolAnalysisResponse.class
        );
    }

    public Map<String, Object> analyzeAll() {
        return restTemplate.getForObject(
                BASE_URL + "/technicalAnalysis/all",
                Map.class
        );
    }
}

