package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.repository.DailyDataRepository;
import com.svsbrains.svscrypto.repository.MarketDataRepository;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DailyDataServiceImpl implements DailyDataService {
    private final DailyDataRepository dailyDataRepository;
    private final MarketDataService marketDataService;

    public DailyDataServiceImpl(DailyDataRepository dailyDataRepository, MarketDataService marketDataService) {
        this.dailyDataRepository = dailyDataRepository;
        this.marketDataService = marketDataService;
    }

    @Override
    public Optional<DailyData> getBySymbol(String symbol) {
        return dailyDataRepository.findBySymbol(symbol);
    }

    @Override
    public List<DailyData> getTopByPrice(int k) {
        return dailyDataRepository.findTopPrices(PageRequest.of(0, k));
    }

    @Override
    public List<DailyData> getTopNew(int k) {
        List<MarketData> tmp = marketDataService.getAllFirstTimestamp().subList(0, k);
        List<DailyData> result = new ArrayList<>();
        tmp.forEach(md -> result.add(getBySymbol(md.getSymbol()).get()));
        return result;
    }

    @Override
    public List<DailyData> getTopByGain(int k) {
        return dailyDataRepository.findTopGains(PageRequest.of(0, k));
    }

    @Override
    public List<DailyData> getTopByVolume(int k) {
        return dailyDataRepository.findTopVolumes(PageRequest.of(0, k));
    }

    @Override
    public double getMonthlyChange(String symbol) {
        DailyData coin = getBySymbol(symbol).get();
        MarketData monthBefore = marketDataService.getMonthDataOfSymbol(symbol).getLast();
        return coin.getChangePercent(monthBefore.getLow());
    }

    @Override
    public double getChangeFromMarketData(String symbol, MarketData marketData) {
        DailyData coin = getBySymbol(symbol).get();
        return coin.getChangePercent(marketData.getLow());
    }

    @Override
    public int getRankOfSymbol(String symbol) {
        return dailyDataRepository.findRankBySymbol(symbol);
    }
}
