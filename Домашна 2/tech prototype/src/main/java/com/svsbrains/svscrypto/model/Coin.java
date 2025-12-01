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
    @Id
    @Column(unique = true)
    private String symbol;
    @OneToMany
    private List<DailyData> dailyData;
    @OneToMany
    private List<MarketData> marketData;
}
