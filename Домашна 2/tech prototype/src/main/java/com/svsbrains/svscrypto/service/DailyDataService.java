package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.DailyData;

import java.util.List;
import java.util.Optional;

public interface DailyDataService {
    Optional<DailyData> getBySymbol(String symbol);
    List<DailyData> getTopByPrice(int k);
    List<DailyData> getTopNew(int k);
    List<DailyData> getTopByGain(int k);
    List<DailyData> getTopByVolume(int k);
}
