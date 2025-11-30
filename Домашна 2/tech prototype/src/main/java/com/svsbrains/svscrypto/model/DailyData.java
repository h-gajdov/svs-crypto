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

    @ManyToOne
    private Coin coin;
    private Long timestamp;
    private Double last_price;
    private Double volume_24h;
    private Double high_24h;
    private Double low_24h;

}
