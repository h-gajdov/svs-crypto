package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.MarketData;
import org.yaml.snakeyaml.error.Mark;

import java.util.List;
import java.util.Optional;

public interface MarketDataService {

    List<MarketData> getBySymbol(String symbol);

    MarketData getById(Long id);

    List<MarketData> getAllFirstTimestamp();

    Optional<MarketData> getFirstTimestamp(String symbol);

    List<MarketData> getMonthDataOfSymbol(String symbol);

    List<MarketData> getKDaysDataOfSymbol(String symbol, int k);

    Optional<MarketData> getAllTimeHigh(String symbol);

    Optional<MarketData> getAllTimeLow(String symbol);
}
