package com.svsbrains.svscrypto.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class EstimateNewsDto {
    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("probability")
    private Double probability;

    @JsonProperty("sentiment")
    private String sentiment;

    @JsonProperty("news")
    private List<NewsDto> news;
}

@Data
@NoArgsConstructor
class NewsDto {
    @JsonProperty("author")
    private List<String> author;

    @JsonProperty("headline")
    private String headline;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("image")
    private String image;

    @JsonProperty("source")
    private String source;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("url")
    private String url;
}
