package com.svsbrains.svscrypto.repository;

import com.svsbrains.svscrypto.model.Coin;
import com.svsbrains.svscrypto.model.MarketData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MarketDataRepository extends JpaRepository<MarketData, Long> {

    List<MarketData> findByCoin(Coin coin);

    List<MarketData> findBySymbol(String symbol);

    List<MarketData> findByTimestampBetween(Long start, Long end);

    List<MarketData> findByCoinSymbolAndTimestampBetween(String symbol, Long start, Long end);

    @Query(value = "SELECT m.* FROM market_data m " +
            "INNER JOIN (" +
            "   SELECT symbol, MAX(timestamp) AS max_ts " +
            "   FROM market_data " +
            "   GROUP BY symbol" +
            ") latest ON m.symbol = latest.symbol AND m.timestamp = latest.max_ts",
            nativeQuery = true)
    List<MarketData> findAllSymbolsFirstTimestamp();

    Optional<MarketData> findFirstBySymbolOrderByTimestampAsc(String symbol);

    @Query(value = "SELECT * FROM market_data WHERE symbol = :symbol ORDER BY timestamp DESC", nativeQuery = true)
    List<MarketData> findLastNDaysBySymbol(@Param("symbol") String symbol, Pageable pageable);

    Optional<MarketData> findFirstBySymbolOrderByLowAsc(String symbol);

    Optional<MarketData> findFirstBySymbolOrderByHighDesc(String symbol);
}