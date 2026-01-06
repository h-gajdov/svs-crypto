package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.dto.SymbolAnalysisResponse;

import java.util.Map;

public interface TechnicalAnalysisService {
    SymbolAnalysisResponse analyzeSymbol(String symbol);
    Map<String, Object> analyzeAll();
    Map<String, Object> getTechnicalAnalysis(String symbol);
}
