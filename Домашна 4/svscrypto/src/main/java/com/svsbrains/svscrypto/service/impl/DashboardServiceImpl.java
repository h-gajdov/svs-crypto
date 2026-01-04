package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.DailyData;
import com.svsbrains.svscrypto.service.DailyDataService;
import com.svsbrains.svscrypto.service.DashboardService;
import com.svsbrains.svscrypto.service.MarketDataService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.svsbrains.svscrypto.model.MarketData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private static final int TOP_COUNT = 3;
    private static final int SPARKLINE_DAYS = 7;
    private static final int THREE_MONTHS_DAYS = 90;

    private final DailyDataService dailyDataService;
    private final MarketDataService marketDataService;

    public DashboardServiceImpl(DailyDataService dailyDataService,
                                MarketDataService marketDataService) {
        this.dailyDataService = dailyDataService;
        this.marketDataService = marketDataService;
    }

    @Override
    public Page<DailyData> getDashboardMarketCap(int page, int size) {
        Page<DailyData> result =
                dailyDataService.getTopByMarketCap(page, size);

        enrichWithChanges(result.getContent());
        return result;
    }

    @Override
    public List<DailyData> getTopByPrice() {
        return dailyDataService.getTopByPrice(TOP_COUNT);
    }

    @Override
    public List<DailyData> getTopByVolume() {
        return dailyDataService.getTopByVolume(TOP_COUNT);
    }

    @Override
    public List<DailyData> getTopByGain() {
        return dailyDataService.getTopByGain(TOP_COUNT);
    }

    @Override
    public List<DailyData> getTopNew() {
        return dailyDataService.getTopNew(TOP_COUNT);
    }

    @Override
    public Map<String, List<Double>> getSparklineData(Page<DailyData> coins) {
        Map<String, List<Double>> result = new HashMap<>();

        for (DailyData coin : coins) {
            List<Double> prices =
                    marketDataService.getKDaysDataOfSymbol(coin.getSymbol(), SPARKLINE_DAYS)
                            .stream()
                            .map(MarketData::getOpen)
                            .toList();

            result.put(coin.getSymbol(), prices);
        }
        return result;
    }


    private void enrichWithChanges(List<DailyData> coins) {
        for (DailyData coin : coins) {
            double monthly =
                    dailyDataService.getMonthlyChange(coin.getSymbol());

            MarketData threeMonthsBefore =
                    marketDataService
                            .getKDaysDataOfSymbol(coin.getSymbol(), THREE_MONTHS_DAYS)
                            .getLast();

            double threeMonthChange =
                    dailyDataService.getChangeFromMarketData(
                            coin.getSymbol(), threeMonthsBefore);

            coin.setMonthlyChange(monthly);
            coin.setThreeMonthsChange(threeMonthChange);
        }
    }
}

