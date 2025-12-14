package com.svsbrains.svscrypto.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class WhaleMovementDto {
    @JsonProperty("time")
    private String time;

    @JsonProperty("text")
    private String text;

    @JsonProperty("amounts")
    private List<WhaleAmountDto> amounts;
}

@Data
@NoArgsConstructor
class WhaleAmountDto {
    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("value_usd")
    private Double valueUsd;
}
