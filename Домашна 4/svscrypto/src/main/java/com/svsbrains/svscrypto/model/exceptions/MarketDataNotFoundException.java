package com.svsbrains.svscrypto.model.exceptions;

public class MarketDataNotFoundException extends RuntimeException {
    public MarketDataNotFoundException(String symbol) {
        super("Market Data not found for symbol: " + symbol);
    }

    public MarketDataNotFoundException(Long id) {
        super("Market Data not found for id: " + id);
    }
}
