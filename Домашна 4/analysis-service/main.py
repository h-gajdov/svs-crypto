# to run: uvicorn main:app --reload --port 8000

import numpy as np
from fastapi import FastAPI
from cachetools import TTLCache, cached

from sentiment.sentiment_analysis import *
from onchain.onchain_metrics import *
from lstm.lstm import *
from technicalAnalysis.analysis import *

app = FastAPI()

metrics_cache = TTLCache(maxsize=500, ttl=600)# 10 min
news_cache = TTLCache(maxsize=300, ttl=1800)# 30 min
news_sentiment_cache = TTLCache(maxsize=300, ttl=1800)
analysis_cache = TTLCache(maxsize=300, ttl=300)# 5 min
onchain_indicator_cache = TTLCache(maxsize=200, ttl=300)# 5 min
whale_cache = TTLCache(maxsize=100, ttl=120)# 2 min
exchange_cache = TTLCache(maxsize=100, ttl=120)# 2 min

@cached(exchange_cache)
def cached_exchange_flow(symbol):
    return get_exchange_flow(symbol)

@cached(metrics_cache)
def cached_all_metrics(symbol):
    return get_all_metrics(symbol)

@cached(news_cache)
def cached_news(symbol):
    return get_sentiment(symbol + ',' + get_coin_id(symbol)['coin_id'])

@cached(news_sentiment_cache)
def cached_news_sentiment(symbol):
    news = cached_news(symbol)
    prob, sentiment = estimate_sentiment(news)
    return prob, sentiment

@cached(analysis_cache)
def cached_analyze_symbol(symbol):
    return analyze_symbol(symbol)

@cached(whale_cache)
def cached_whale_movements(n):
    return get_whale_movements(n)

@app.get("/technicalAnalysis/all")
def analyze_all():
    return analyze_all_cryptos()

@app.get("/analysis/{symbol}")
def get_analyze_symbol(symbol):
    return cached_analyze_symbol(symbol)

@app.get("/identificators/{symbol}")
def get_indentificators(symbol):
    return get_symbol_indicators(symbol)

@app.get("/check-connection")
def check_connection():
    return {"status": "FastAPI is running"}

@app.get("/get-news/{symbol}")
def get_news(symbol):
    return {"symbol": symbol, "news": cached_news(symbol)}

@app.get("/estimate-news/{symbol}")
def estimate_news_for_symbol(symbol):
    news = cached_news(symbol)
    tensor, sentiment = cached_news_sentiment(symbol)
    return {
        "symbol": symbol,
        "news": news,
        "probability": tensor,
        "sentiment": sentiment
    }

@app.get('/address-count/{symbol}')
def address_count(symbol, daysBefore = 1):
    return get_address_count(symbol, daysBefore)

@app.get("/transactions-count/{symbol}")
def transactions_count(symbol, daysBefore = 1):
    return get_transactions_count(symbol, daysBefore)

@app.get("/whale-movements")
def whale_movements(number_of_alerts = 5):
    return cached_whale_movements(number_of_alerts)

@app.get("/nvt/{symbol}")
def nvt(symbol):
    return get_nvt(symbol)

@app.get("/tvl/{symbol}")
def tvl(symbol):
    return get_tvl(symbol)

@app.get("/coin-id/{symbol}")
def coin_id(symbol):
    return get_coin_id(symbol)

@app.get("/hash-rate/{symbol}")
def hash_rate(symbol, daysBefore = 1):
    return get_hash_rate(symbol, daysBefore)

@app.get("/mvrv-ratio/{symbol}")
def mvrv_ratio(symbol, daysBefore = 1):
    return get_mvrv_ratio(symbol, daysBefore)

@app.get("/exchange-flow/{symbol}")
def exchange_flow(symbol):
    return cached_exchange_flow(symbol)

@app.get("/metrics/{symbol}")
def all_metrics(symbol):
    return cached_all_metrics(symbol)

def safe_float(x):
    try:
        return float(x)
    except:
        return 0.0

def log_normalize(x, scale=1):
    x = max(safe_float(x), 0)
    return np.log1p(x) / np.log1p(scale)

def inverse_log_normalize(x, scale=1):
    x = max(safe_float(x), 0)
    return 1 - (np.log1p(x) / np.log1p(scale))

def normalize_exchange_flow(data, scale=1e9):
    try:
        flows = [safe_float(d["netflow"]) for d in data["data"]]
        avg_flow = flows[0]
        return log_normalize(-avg_flow, scale)
    except:
        return 0.5

@cached(onchain_indicator_cache)
def cached_combined_indicator(symbol):
    news = cached_news(symbol)
    prob, label = estimate_sentiment(news)
    sentiment_score = prob if label == "positive" else -prob

    m = cached_all_metrics(symbol)

    addr_raw = m.get("AdrActCnt")
    tx_raw = m.get("TxCnt")
    hash_raw = m.get("HashRate")
    tvl_raw = m.get("tvl")
    nvt_raw = m.get("nvt")
    mvrv_raw = m.get("CapMVRVCur")
    exch_raw = m.get("exchange_flow")

    addr = log_normalize(addr_raw, scale=1_000_000)
    tx = log_normalize(tx_raw, scale=1_000_000)
    hash_r = log_normalize(hash_raw, scale=2_000_000_000)
    tvl = log_normalize(tvl_raw, scale=50_000_000_000)
    nvt = inverse_log_normalize(nvt_raw, scale=100)
    mvrv = inverse_log_normalize(mvrv_raw, scale=5)
    exch = normalize_exchange_flow(exch_raw, scale=20_000_000_000)

    weights = {
        "active_addresses": 0.10,
        "transactions": 0.05,
        "hashrate": 0.20,
        "tvl": 0.05,
        "nvt": 0.25,
        "mvrv": 0.20,
        "exchange_flows": 0.15
    }

    onchain_score = (
        addr * weights["active_addresses"] +
        tx * weights["transactions"] +
        hash_r * weights["hashrate"] +
        tvl * weights["tvl"] +
        nvt * weights["nvt"] +
        mvrv * weights["mvrv"] +
        exch * weights["exchange_flows"]
    )

    final_score = 0.75 * onchain_score + 0.25 * sentiment_score

    signal = "BUY" if final_score > 0.75 else "NEUTRAL" if final_score > 0.45 else "SELL"

    return {
        "symbol": symbol,
        "onchain_score": float(onchain_score),
        "combined_score": float(final_score),
        "signal": signal
    }

@app.get('/get-indicator-onchain/{symbol}')
def combine_onchain_and_sentiment(symbol):
    return cached_combined_indicator(symbol)

@app.get("/api/predict/{symbol}", response_model=PredictionResponse)
def get_predict_price(symbol):
    return predict_price(symbol)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)