package com.svsbrains.svscrypto.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OnChainSentimentDto {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("sentiment")
    private SentimentDto sentiment;

    @JsonProperty("onchain_raw")
    private OnChainRawDto onchainRaw;

    @JsonProperty("onchain_score")
    private Double onchainScore;

    @JsonProperty("combined_score")
    private Double combinedScore;

    @JsonProperty("signal")
    private String signal;

    @Data
    @NoArgsConstructor
    public static class SentimentDto {

        @JsonProperty("label")
        private String label;

        @JsonProperty("probability")
        private Double probability;

        @JsonProperty("score")
        private Double score;
    }

    @Data
    @NoArgsConstructor
    public static class OnChainRawDto {

        @JsonProperty("active_addresses")
        private Double activeAddresses;

        @JsonProperty("transactions")
        private Double transactions;

        @JsonProperty("hashrate")
        private Double hashrate;

        @JsonProperty("tvl")
        private Double tvl;

        @JsonProperty("nvt")
        private Double nvt;

        @JsonProperty("mvrv")
        private Double mvrv;
    }

    @Data
    @NoArgsConstructor
    public static class OnChainNormalizedDto {

        @JsonProperty("active_addresses")
        private Double activeAddresses;

        @JsonProperty("transactions")
        private Double transactions;

        @JsonProperty("hashrate")
        private Double hashrate;

        @JsonProperty("tvl")
        private Double tvl;

        @JsonProperty("nvt")
        private Double nvt;

        @JsonProperty("mvrv")
        private Double mvrv;
    }

    @Data
    @NoArgsConstructor
    public static class MetricContributionsDto {

        @JsonProperty("addr")
        private Double addr;

        @JsonProperty("tx")
        private Double tx;

        @JsonProperty("hashrate")
        private Double hashrate;

        @JsonProperty("tvl")
        private Double tvl;

        @JsonProperty("nvt")
        private Double nvt;

        @JsonProperty("mvrv")
        private Double mvrv;
    }
}
