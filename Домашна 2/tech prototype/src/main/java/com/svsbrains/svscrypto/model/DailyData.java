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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    private String symbol;

    @ManyToOne
    private Coin coin;
    private Long timestamp;
    private Double last_price;
    private Double volume_24h;
    private Double high_24h;
    private Double low_24h;

    private Double monthlyChange;
    private Double threeMonthsChange;

    public String getFormattedLastPrice() {
        return formatNumber(last_price);
    }

    public String getFormattedMonthlyChange() {
        if(monthlyChange == null) return "0";
        return formatNumber(monthlyChange);
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

    public double getChangePercent(Double number) {
        return ((last_price - number) / number) * 100;
    }

    public String getFormattedChange() {
        return formatNumber(getChangePercent());
    }

    public String getFormattedVolume() {
        return formatNumber(volume_24h);
    }

    private String formatNumber(double number) {
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
