package com.svsbrains.svscrypto.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ExchangeFlowDto {
    @JsonProperty("data")
    private List<ExchangeFlowTimeframe> data;
}

@Data
@NoArgsConstructor
class ExchangeFlowTimeframe {
    @JsonProperty("timeframe")
    private String timeframe;

    @JsonProperty("netflow")
    private Double netflow;
}