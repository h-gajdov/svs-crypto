package com.svsbrains.svscrypto.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OnChainMetricsDto {
    @JsonProperty("asset")
    private String asset;

    @JsonProperty("AdrActCnt")
    private Double activeAdresses;

    @JsonProperty("TxCnt")
    private Double transactionsCount;

    @JsonProperty("HashRate")
    private Double hashRate;

    @JsonProperty("nvt")
    private Double nvt;

    @JsonProperty("tvl")
    private Double tvl;

    @JsonProperty("CapMVRVCur")
    private Double mvrv;

    @JsonProperty("exchange_flow")
    private ExchangeFlowDto exchangeFlow;
}
