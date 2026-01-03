package com.svsbrains.svscrypto.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Coin {
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public List<DailyData> getDailyData() {
        return dailyData;
    }

    public void setDailyData(List<DailyData> dailyData) {
        this.dailyData = dailyData;
    }

    public List<MarketData> getMarketData() {
        return marketData;
    }

    public void setMarketData(List<MarketData> marketData) {
        this.marketData = marketData;
    }

    @Id
    @Column(unique = true)
    private String symbol;
    @OneToMany
    private List<DailyData> dailyData;
    @OneToMany
    private List<MarketData> marketData;
}
