package com.svsbrains.svscrypto.model.dto;

import java.util.List;
import java.util.Map;

public class AllAnalysisResponse {
    private Map<String, Map<String, List<String>>> results;

    public Map<String, Map<String, List<String>>> getResults() {
        return results;
    }

    public void setResults(Map<String, Map<String, List<String>>> results) {
        this.results = results;
    }
}
