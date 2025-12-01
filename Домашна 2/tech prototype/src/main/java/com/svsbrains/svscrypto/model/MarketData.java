package com.svsbrains.svscrypto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "market_data")
public class MarketData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    @ManyToOne
    private Coin coin;
    private Long timestamp;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;

    public Long getId() {
        return id;
    }

    public String getSymbol() {
        return symbol;
    }

    public Coin getCoin() {
        return coin;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Double getOpen() {
        return open;
    }

    public Double getHigh() {
        return high;
    }

    public Double getLow() {
        return low;
    }

    public Double getClose() {
        return close;
    }

    public Double getVolume() {
        return volume;
    }

    public String getFormattedHigh() {
        return DailyData.formatNumber(high);
    }

    public String getFormattedLow() {
        return DailyData.formatNumber(low);
    }
}
