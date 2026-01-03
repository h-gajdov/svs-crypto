package com.svsbrains.svscrypto.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PredictionResponseDto {
    private String symbol;

    @JsonProperty("predicted_next_close_price")
    private Double predictedNextPrice;

    private MetricsDto metrics;

    @JsonProperty("training_info")
    private String trainingInfo;
}


