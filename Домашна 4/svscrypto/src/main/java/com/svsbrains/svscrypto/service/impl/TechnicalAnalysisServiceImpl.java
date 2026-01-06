package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.client.AnalysisEngineClient;
import com.svsbrains.svscrypto.client.AnalysisEngineEndpoints;
import com.svsbrains.svscrypto.model.dto.SymbolAnalysisResponse;
import com.svsbrains.svscrypto.service.TechnicalAnalysisService;
import org.springframework.stereotype.Service;

@Service
public class TechnicalAnalysisServiceImpl implements TechnicalAnalysisService {

    private final AnalysisEngineEndpoints endpoints;
    private final AnalysisEngineClient client;

    public TechnicalAnalysisServiceImpl(AnalysisEngineEndpoints endpoints, AnalysisEngineClient client) {
        this.endpoints = endpoints;
        this.client = client;
    }

    @Override
    public SymbolAnalysisResponse getTechnicalAnalysis(String symbol) {
        return client.get(
                endpoints.analyzeSymbolEndpoint(symbol),
                SymbolAnalysisResponse.class
        );
    }
}
