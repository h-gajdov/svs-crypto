package com.svsbrains.svscrypto.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class SymbolAnalysisResponse {

    private String symbol;

    @JsonProperty("1D")
    private List<IndicatorSignalDto> oneDay;

    @JsonProperty("1W")
    private List<IndicatorSignalDto> oneWeek;

    @JsonProperty("1M")
    private List<IndicatorSignalDto> oneMonth;
}

class IndicatorSignalDto {
    @JsonProperty("indicator")
    private String indicator;

    @JsonProperty("signal")
    private String signal;
}