package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.MarketData;

import java.util.List;
import java.util.Optional;

public interface MarketDataService {

    List<MarketData> getBySymbol(String symbol);

    MarketData getById(Long id);

    List<MarketData> getAllFirstTimestamp();

    Optional<MarketData> getFirstTimestamp(String symbol);
}
