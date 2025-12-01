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

    public String getFormattedLastPrice() {
        if (last_price >= 1_000_000_000) {
            return String.format("%.2fB", last_price / 1_000_000_000);
        } else if (last_price >= 1_000_000) {
            return String.format("%.2fM", last_price / 1_000_000);
        } else if (last_price >= 1_000) {
            return String.format("%.2fK", last_price / 1_000);
        } else {
            return String.format("%.2f", last_price);
        }
    }

    public double getChangePercent() {
        if (low_24h == null || low_24h == 0) return 0;
        if (high_24h == null || high_24h == 0) return 0;

        if(last_price.equals(high_24h)) {
            return ((last_price - low_24h) / low_24h) * 100;
        } else {
            return ((last_price - high_24h) / high_24h) * 100;
        }
    }

    public String getFormattedChange() {
        double number = getChangePercent();
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
