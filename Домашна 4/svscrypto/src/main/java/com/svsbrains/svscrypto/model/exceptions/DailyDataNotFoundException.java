package com.svsbrains.svscrypto.model.exceptions;

public class DailyDataNotFoundException extends RuntimeException {
    public DailyDataNotFoundException(String symbol) {
        super("Daily data not found for symbol: " + symbol);
    }
}
