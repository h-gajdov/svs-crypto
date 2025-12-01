package com.svsbrains.svscrypto.repository;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DailyDataRepository extends JpaRepository<DailyData, Long> {
    Optional<DailyData> findBySymbol(String symbol);

    @Query("SELECT d FROM DailyData d ORDER BY d.last_price DESC")
    List<DailyData> findTopPrices(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT d FROM DailyData d " +
            "ORDER BY " +
            "CASE " +
            "  WHEN d.low_24h IS NULL OR d.low_24h = 0 THEN 0 " +
            "  WHEN d.high_24h IS NULL OR d.high_24h = 0 THEN 0 " +
            "  WHEN d.last_price = d.high_24h THEN ((d.last_price - d.low_24h) / d.low_24h) " +
            "  ELSE ((d.last_price - d.high_24h) / d.high_24h) " +
            "END DESC")
    List<DailyData> findTopGains(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT d FROM DailyData d ORDER BY d.volume_24h DESC")
    List<DailyData> findTopVolumes(org.springframework.data.domain.Pageable pageable);

    @Query("""
        SELECT 
            1 + (SELECT COUNT(d2) 
                 FROM DailyData d2 
                 WHERE d2.last_price > d.last_price)
        FROM DailyData d
        WHERE d.symbol = :symbol
    """)
    Integer findRankBySymbol(@Param("symbol") String symbol);
}
