package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.client.AnalysisEngineClient;
import com.svsbrains.svscrypto.client.AnalysisEngineEndpoints;
import com.svsbrains.svscrypto.model.dto.SymbolIndicatorsDto;
import com.svsbrains.svscrypto.service.IndicatorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndicatorServiceImpl implements IndicatorsService {
    private final AnalysisEngineClient client;
    private final AnalysisEngineEndpoints endpoints;

    @Override
    public SymbolIndicatorsDto getIndicators(String symbol) {
        return client.get(
                endpoints.indicatorsEndpoint(symbol),
                SymbolIndicatorsDto.class
        );
    }
}