package com.svsbrains.svscrypto.model.dto;

import lombok.Data;

@Data
public class SymbolIndicators {

    private String symbol;
    private Double close;

    private Double RSI;
    private Double MACD;
    private Double MACD_signal;

    private Double STOCH_K;
    private Double STOCH_D;

    private Double ADX;
    private Double CCI;

    private Double SMA_20;
    private Double EMA_20;
    private Double WMA_20;

    private Double BB_middle;
    private Double BB_upper;
    private Double BB_lower;

    private Double VMA_20;
}
