package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.dto.SymbolIndicators;

public interface IndicatorsService {
    public SymbolIndicators getIndicators(String symbol);
}
