package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.User;

import java.util.List;
import java.util.Map;

public interface DetailedCoinViewService {

    Map<String, Object> buildDetailedView(String symbol);
    Map<String, Object> getPlotData(String symbol, String time, String field);

}
