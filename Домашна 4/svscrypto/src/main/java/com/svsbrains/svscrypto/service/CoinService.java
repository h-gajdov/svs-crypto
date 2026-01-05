package com.svsbrains.svscrypto.service;

import com.svsbrains.svscrypto.model.Coin;

import java.util.List;

/**
 * Service for operations related to {@code Coin} entities.
 */
public interface CoinService {

    /**
     * Retrieves all available cryptocurrency symbols.
     *
     * <p>
     * Each symbol uniquely identifies a cryptocurrency (e.g. {@code BTC}, {@code ETH}).
     * The returned list contains only symbol values and not full coin objects.
     * </p>
     *
     * @return a list of cryptocurrency symbols; never {@code null},
     *         but may be empty if no symbols are available
     */
    List<String> getAllSymbols();
}