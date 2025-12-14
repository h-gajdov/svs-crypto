package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.dto.SymbolAnalysisResponse;

import java.util.Map;

public interface TechnicalAnalysisService {
    public SymbolAnalysisResponse analyzeSymbol(String symbol);
    public Map<String, Object> analyzeAll();
    public Map<String, Object> getTechnicalAnalysis(String symbol);
}
