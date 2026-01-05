package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.client.AnalysisEngineClient;
import com.svsbrains.svscrypto.client.AnalysisEngineEndpoints;
import com.svsbrains.svscrypto.model.dto.PredictionResponseDto;
import com.svsbrains.svscrypto.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PythonLSTMPredictionService implements PredictionService {
    private final AnalysisEngineClient client;
    private final AnalysisEngineEndpoints endpoints;

    @Override
    public PredictionResponseDto predictPrice(String symbol) {
        return client.get(
                endpoints.predictionEndpoint(symbol),
                PredictionResponseDto.class
        );
    }
}
