package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.dto.SymbolIndicatorsDto;

public interface IndicatorsService {
    public SymbolIndicatorsDto getIndicators(String symbol);
}
