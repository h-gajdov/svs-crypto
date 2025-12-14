package com.svsbrains.svscrypto.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class MetricsDto {
    @JsonProperty("RMSE")
    private Double rmse;

    @JsonProperty("MAPE")
    private Double mape;

    @JsonProperty("R2")
    private Double r2;
}
