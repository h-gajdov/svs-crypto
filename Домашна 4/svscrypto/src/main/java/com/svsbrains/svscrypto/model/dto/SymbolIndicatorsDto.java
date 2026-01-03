package com.svsbrains.svscrypto.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SymbolIndicatorsDto {

    @JsonProperty("close")
    private Double close;

    @JsonProperty("RSI")
    private Double RSI;
    @JsonProperty("MACD")
    private Double MACD;
    @JsonProperty("MACD_signal")
    private Double MACD_signal;

    @JsonProperty("STOCH_K")
    private Double STOCH_K;
    @JsonProperty("STOCH_D")
    private Double STOCH_D;

    @JsonProperty("ADX")
    private Double ADX;
    @JsonProperty("CCI")
    private Double CCI;

    @JsonProperty("SMA_20")
    private Double SMA_20;
    @JsonProperty("EMA_20")
    private Double EMA_20;
    @JsonProperty("WMA_20")
    private Double WMA_20;

    @JsonProperty("BB_middle")
    private Double BB_middle;
    @JsonProperty("BB_upper")
    private Double BB_upper;
    @JsonProperty("BB_lower")
    private Double BB_lower;

    @JsonProperty("VMA_20")
    private Double VMA_20;
}
