package com.svsbrains.svscrypto.repository;

import com.svsbrains.svscrypto.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {
    @Query("SELECT distinct symbol from DailyData")
    public List<String> findAllSymbols();
}
