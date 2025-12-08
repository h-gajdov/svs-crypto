package com.svsbrains.svscrypto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "daily_data")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyData {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Double getLast_price() {
        return last_price;
    }

    public void setLast_price(Double last_price) {
        this.last_price = last_price;
    }

    public Double getVolume_24h() {
        return volume_24h;
    }

    public void setVolume_24h(Double volume_24h) {
        this.volume_24h = volume_24h;
    }

    public Double getHigh_24h() {
        return high_24h;
    }

    public void setHigh_24h(Double high_24h) {
        this.high_24h = high_24h;
    }

    public Double getLow_24h() {
        return low_24h;
    }

    public void setLow_24h(Double low_24h) {
        this.low_24h = low_24h;
    }

    public Double getMonthlyChange() {
        return monthlyChange;
    }

    public void setMonthlyChange(Double monthlyChange) {
        this.monthlyChange = monthlyChange;
    }

    public Double getThreeMonthsChange() {
        return threeMonthsChange;
    }

    public void setThreeMonthsChange(Double threeMonthsChange) {
        this.threeMonthsChange = threeMonthsChange;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    private String symbol;
    private String name;

    @ManyToOne
    private Coin coin;
    private Long timestamp;
    private Double market_cap;
    private Double last_price;
    private Double volume_24h;
    private Double high_24h;
    private Double low_24h;

    private Double weeklyChange;
    private Double monthlyChange;
    private Double threeMonthsChange;

    public String getFormattedLastPrice() {
        return formatNumber(last_price);
    }

    public String getFormattedMonthlyChange() {
        if(monthlyChange == null) return "0";
        return formatNumber(monthlyChange);
    }

    public String getFormattedWeeklyChange() {
        if(weeklyChange == null) return "0";
        return formatNumber(weeklyChange);
    }

    public String getFormattedHigh() {
        return formatNumber(high_24h);
    }

    public String getFormattedLow() {
        return formatNumber(low_24h);
    }

    public String getFormattedThreeMonthsChange() {
        if(threeMonthsChange == null) return "0";
        return formatNumber(threeMonthsChange);
    }

    public double getChangePercent() {
        if (low_24h == null || low_24h == 0) return 0;
        if (high_24h == null || high_24h == 0) return 0;

        if(last_price.equals(high_24h)) {
            return getChangePercent(low_24h);
        } else {
            return getChangePercent(high_24h);
        }
    }

    public String getFormattedHighLowRate() {
        return formatNumber(((high_24h - low_24h) / low_24h) * 100);
    }

    public double getChangePercent(Double number) {
        return ((last_price - number) / number) * 100;
    }

    public String getFormattedChange() {
        return formatNumber(getChangePercent());
    }

    public String getFormattedVolume() {
        return formatNumber(volume_24h);
    }

    public static String formatNumber(double number) {
        if (number >= 1_000_000_000) {
            return String.format("%.2fB", number / 1_000_000_000);
        } else if (number >= 1_000_000) {
            return String.format("%.2fM", number / 1_000_000);
        } else if (number >= 1_000) {
            return String.format("%.2fK", number / 1_000);
        } else {
            return String.format("%.2f", number);
        }
    }
}
