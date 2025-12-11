package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.dto.PredictionResponseDto;

public interface PredictionService {
    public PredictionResponseDto predictPrice(String symbol);
}
