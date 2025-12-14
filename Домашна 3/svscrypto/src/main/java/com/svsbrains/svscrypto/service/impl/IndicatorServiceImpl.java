package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.dto.SymbolIndicators;
import com.svsbrains.svscrypto.service.IndicatorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class IndicatorServiceImpl implements IndicatorsService{

    private final RestTemplate restTemplate = new RestTemplate();

    public SymbolIndicators getIndicators(String symbol) {
        String url = "http://localhost:8000/identificators/" + symbol;
        return restTemplate.getForObject(url, SymbolIndicators.class);
    }
}
