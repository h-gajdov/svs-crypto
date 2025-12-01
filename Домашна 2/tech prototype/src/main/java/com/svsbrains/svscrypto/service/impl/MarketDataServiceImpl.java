package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.repository.DailyDataRepository;
import com.svsbrains.svscrypto.repository.MarketDataRepository;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Override
    public List<MarketData> getMonthDataOfSymbol(String symbol) {
        Pageable limit = PageRequest.of(0, 30);
        return marketDataRepository.findLastNDaysBySymbol(symbol, limit);
    }

    @Override
    public List<MarketData> getKDaysDataOfSymbol(String symbol, int k) {
        Pageable limit = PageRequest.of(0, k);
        return marketDataRepository.findLastNDaysBySymbol(symbol, limit);
    }

    @Override
    public Optional<MarketData> getAllTimeHigh(String symbol) {
        return marketDataRepository.findFirstBySymbolOrderByHighDesc(symbol);
    }

    @Override
    public Optional<MarketData> getAllTimeLow(String symbol) {
        return marketDataRepository.findFirstBySymbolOrderByLowAsc(symbol);
    }
}