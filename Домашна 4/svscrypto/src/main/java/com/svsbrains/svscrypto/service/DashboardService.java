package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.DailyData;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DashboardService {

    Page<DailyData> getDashboardMarketCap(int page, int size);

    List<DailyData> getTopByPrice();
    List<DailyData> getTopByVolume();
    List<DailyData> getTopByGain();
    List<DailyData> getTopNew();

    Map<String, List<Double>> getSparklineData(Page<DailyData> coins);
}
