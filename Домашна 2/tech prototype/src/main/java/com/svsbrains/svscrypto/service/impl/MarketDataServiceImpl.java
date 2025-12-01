package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.repository.DailyDataRepository;
import com.svsbrains.svscrypto.repository.MarketDataRepository;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MarketDataServiceImpl implements MarketDataService {
    private final MarketDataRepository marketDataRepository;

    public MarketDataServiceImpl(MarketDataRepository marketDataRepository, DailyDataRepository dailyDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }

    @Override
    public List<MarketData> getBySymbol(String symbol) {
        return marketDataRepository.findBySymbol(symbol);
    }

    @Override
    public MarketData getById(Long id) {
        return marketDataRepository.findById(id).orElse(null);
    }

    @Override
    public List<MarketData> getAllFirstTimestamp() {
        return marketDataRepository.findAllSymbolsFirstTimestamp();
    }

    @Override
    public Optional<MarketData> getFirstTimestamp(String symbol) {
        return marketDataRepository.findFirstBySymbolOrderByTimestampAsc(symbol);
    }
}