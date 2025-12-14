package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.dto.SymbolIndicatorsDto;
import com.svsbrains.svscrypto.service.IndicatorsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class IndicatorServiceImpl implements IndicatorsService{

    private final RestTemplate restTemplate = new RestTemplate();
    private final String  GET_INDICATORS = "http://localhost:8000/identificators/";

    public SymbolIndicatorsDto getIndicators(String symbol) {
        try {
            String url = String.format("%s/%s", GET_INDICATORS, symbol);
            ResponseEntity<SymbolIndicatorsDto> response =
                    restTemplate.getForEntity(url, SymbolIndicatorsDto.class);

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Communication error with python: " + e.getMessage());
            return null;
        }
    }
}
