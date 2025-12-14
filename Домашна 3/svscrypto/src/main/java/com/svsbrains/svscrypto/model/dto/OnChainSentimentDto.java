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
}
