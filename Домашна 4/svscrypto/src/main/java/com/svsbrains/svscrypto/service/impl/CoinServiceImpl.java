package com.svsbrains.svscrypto.service.impl;

import com.svsbrains.svscrypto.model.Coin;
import com.svsbrains.svscrypto.repository.CoinRepository;
import com.svsbrains.svscrypto.service.CoinService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoinServiceImpl implements CoinService {
    private final CoinRepository coinRepository;

    public CoinServiceImpl(CoinRepository coinRepository) {
        this.coinRepository = coinRepository;
    }

    @Override
    @Cacheable("dailyDataAllSymbols")
    public List<String> getAllSymbols() {
        return coinRepository.findAllSymbols();
    }
}
