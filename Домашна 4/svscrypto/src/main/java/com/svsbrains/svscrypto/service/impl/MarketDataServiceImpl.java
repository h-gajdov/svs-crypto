package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.model.exceptions.MarketDataNotFoundException;
import com.svsbrains.svscrypto.repository.DailyDataRepository;
import com.svsbrains.svscrypto.repository.MarketDataRepository;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class MarketDataServiceImpl implements MarketDataService {
    private final MarketDataRepository marketDataRepository;

    public MarketDataServiceImpl(MarketDataRepository marketDataRepository) {
        this.marketDataRepository = marketDataRepository;
    }

    @Override
    public List<MarketData> getBySymbol(String symbol) {
        return marketDataRepository.findBySymbol(symbol);
    }

    @Override
    public List<MarketData> getAllFirstTimestamp() {
        return marketDataRepository.findAllSymbolsFirstTimestamp();
    }

    @Override
    public List<MarketData> getKDaysDataOfSymbol(String symbol, int k) {
        Pageable limit = PageRequest.of(0, k);
        return marketDataRepository.findLastNDaysBySymbol(symbol, limit);
    }

    @Override
    public MarketData getAllTimeHigh(String symbol) {
        return marketDataRepository.findFirstBySymbolOrderByHighDesc(symbol).orElseThrow(() -> new MarketDataNotFoundException(symbol));
    }

    @Override
    public MarketData getAllTimeLow(String symbol) {
        return marketDataRepository.findFirstBySymbolOrderByLowAsc(symbol).orElseThrow(() -> new MarketDataNotFoundException(symbol));
    }

    @Override
    public List<List<MarketData>> getMarketDataForSymbols(List<String> symbols, int days) {
        long timestamp = calculateTimestamp(days);

        return symbols.stream()
                .map(this::getBySymbol)
                .map(list -> list.stream()
                        .filter(m -> m.getTimestamp() >= timestamp)
                        .toList())
                .toList();
    }

    private long calculateTimestamp(int days) {
        return LocalDate.now()
                .minusDays(days)
                .atStartOfDay(ZoneOffset.UTC)
                .toEpochSecond();
    }
}