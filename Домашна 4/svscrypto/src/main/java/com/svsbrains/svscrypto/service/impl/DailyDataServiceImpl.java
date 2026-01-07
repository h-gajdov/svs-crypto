package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.model.MarketData;
import com.svsbrains.svscrypto.model.exceptions.DailyDataNotFoundException;
import com.svsbrains.svscrypto.repository.DailyDataRepository;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DailyDataServiceImpl implements DailyDataService {
    private final DailyDataRepository dailyDataRepository;
    private final MarketDataService marketDataService;

    /**
     * Helper function to get top K number of entries from a query
     *
     * @param k the number of top entries to retrieve
     * @return a {@link PageRequest} configured to fetch the first {@code k} results
     * */
    private PageRequest getTopKEntries(int k) {
        return PageRequest.of(0, k);
    }

    public DailyDataServiceImpl(DailyDataRepository dailyDataRepository, MarketDataService marketDataService) {
        this.dailyDataRepository = dailyDataRepository;
        this.marketDataService = marketDataService;
    }

    @Override
    public DailyData getBySymbol(String symbol) {
        return dailyDataRepository.findBySymbol(symbol).orElseThrow(() -> new DailyDataNotFoundException(symbol));
    }

    @Override
    public Page<DailyData> getTopByMarketCap(int pageNum, int pageSize) {
        return dailyDataRepository.findTopMarketCap(PageRequest.of(pageNum, pageSize));
    }

    @Override
    public List<DailyData> getTopByPrice(int k) {
        return dailyDataRepository.findTopPrices(getTopKEntries(k));
    }

    @Override
    public List<DailyData> getTopNew(int k) {
        List<MarketData> firstTimestampForSymbols = marketDataService.getAllFirstTimestamp();
        List<MarketData> latestMarketData = firstTimestampForSymbols.subList(0, Math.min(k, firstTimestampForSymbols.size())); //Use of min to prevent IndexOutOfBoundsException
        List<DailyData> result = new ArrayList<>();
        latestMarketData.forEach(md -> result.add(getBySymbol(md.getSymbol())));
        return result;
    }

    @Override
    public List<DailyData> getTopByGain(int k) {
        return dailyDataRepository.findTopGains(getTopKEntries(k));
    }

    @Override
    public List<DailyData> getTopByVolume(int k) {
        return dailyDataRepository.findTopVolumes(getTopKEntries(k));
    }

    @Override
    public double getMonthlyChange(String symbol) {
        DailyData coin = getBySymbol(symbol);
        MarketData monthBefore = marketDataService.getKDaysDataOfSymbol(symbol, 30).getLast();
        return coin.getChangePercent(monthBefore.getLow());
    }

    @Override
    public double getChangeFromMarketData(String symbol, MarketData marketData) {
        DailyData coin = getBySymbol(symbol);
        return coin.getChangePercent(marketData.getLow());
    }

    @Override
    public int getRankOfSymbol(String symbol) {
        return dailyDataRepository.findRankBySymbol(symbol);
    }

    @Override
    public void setCoinsChanges(List<DailyData> coins) {
        coins.forEach(coin -> {
            double monthlyChange = getMonthlyChange(coin.getSymbol());
            MarketData threeMonthsBefore = marketDataService.getKDaysDataOfSymbol(coin.getSymbol(), 90).getLast();
            double threeMonthsChange = getChangeFromMarketData(coin.getSymbol(), threeMonthsBefore);
            coin.setMonthlyChange(monthlyChange);
            coin.setThreeMonthsChange(threeMonthsChange);
        });
    }

    @Override
    public Map<String, List<Double>> getSparklineData(List<DailyData> coins) {
        Map<String, List<Double>> sparklineData = new HashMap<>();
        for (DailyData coin : coins) {
            List<MarketData> weekly = marketDataService.getKDaysDataOfSymbol(coin.getSymbol(), 7);
            List<Double> res = weekly.stream().map(MarketData::getOpen).toList();
            sparklineData.put(coin.getSymbol(), res);
        }
        return sparklineData;
    }

    @Override
    @Cacheable("dailyDataAllCoins")
    public List<DailyData> getCoinsBySymbols(List<String> symbols) {
        return symbols.stream().map(this::getBySymbol).toList();
    }
}
