package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.dto.SymbolAnalysisResponse;
import com.svsbrains.svscrypto.repository.TechnicalAnalysisRepository;
import com.svsbrains.svscrypto.service.TechnicalAnalysisService;
import org.springframework.stereotype.Service;

import java.util.Map;
import org.springframework.web.client.RestTemplate;

@Service
public class TechnicalAnalysisServiceImpl implements TechnicalAnalysisService {

    private final TechnicalAnalysisRepository repository;
    private final RestTemplate restTemplate;

    public TechnicalAnalysisServiceImpl(TechnicalAnalysisRepository repository, RestTemplate restTemplate) {
        this.repository = repository;
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getTechnicalAnalysis(String symbol) {
        String url = "http://localhost:8000/analysis/" + symbol;
        return restTemplate.getForObject(url, Map.class);
    }

    public SymbolAnalysisResponse analyzeSymbol(String symbol) {
        return repository.analyzeSymbol(symbol.toUpperCase());
    }

    public Map<String, Object> analyzeAll() {
        return repository.analyzeAll();
    }
}
