# to run: uvicorn main:app --reload --port 8000
import numpy as np

from sentiment.sentiment_analysis import *
from onchain.onchain_metrics import *
from fastapi import FastAPI

app = FastAPI()


@app.get("/check-connection")
def check_connection():
    return {"status": "FastAPI is running"}


@app.get("/get-news/{symbol}")
def get_news(symbol):
    news = get_sentiment(symbol + ',' + get_coin_id(symbol)['coin_id'])
    return {"symbol": symbol, "news": news}


@app.get("/estimate-news/{symbol}")
def estimate_news_for_symbol(symbol):
    news = get_sentiment(symbol + ',' + get_coin_id(symbol)['coin_id'])
    tensor, sentiment = estimate_sentiment(news)
    return {
        "symbol": symbol,
        "news": news,
        "probability": tensor,
        "sentiment": sentiment
    }


@app.get('/address-count/{symbol}')
def address_count(symbol, daysBefore=1):
    return get_address_count(symbol, daysBefore)


@app.get("/transactions-count/{symbol}")
def transactions_count(symbol, daysBefore=1):
    return get_transactions_count(symbol, daysBefore)


@app.get("/whale-movements")
def whale_movements(number_of_alerts=5):
    return get_whale_movements(number_of_alerts)


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
def hash_rate(symbol, daysBefore=1):
    return get_hash_rate(symbol, daysBefore)


@app.get("/mvrv-ratio/{symbol}")
def mvrv_ratio(symbol, daysBefore=1):
    return get_mvrv_ratio(symbol, daysBefore)


@app.get("/exchange-flow/{symbol}")
def exchange_flow(symbol):
    return get_exchange_flow(symbol)


@app.get("/metrics/{symbol}")
def all_metrics(symbol):  # gets all latest metrics
    return get_all_metrics(symbol)


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


@app.get('/get-indicator-onchain/{symbol}')
def combine_onchain_and_sentiment(symbol):
    news = get_sentiment(symbol + ',' + get_coin_id(symbol)['coin_id'])
    prob, label = estimate_sentiment(news)
    sentiment_score = prob if label == "positive" else -prob
    m = get_all_metrics(symbol)

    addr = log_normalize(m.get("AdrActCnt"), scale=1_000_000)
    tx = log_normalize(m.get("TxCnt"), scale=1_000_000)
    hash_r = log_normalize(m.get("HashRate"), scale=2_000_000_000)
    tvl = log_normalize(m.get("tvl"), scale=50_000_000_000)
    nvt = inverse_log_normalize(m.get("nvt"), scale=100)
    mvrv = inverse_log_normalize(m.get("CapMVRVCur"), scale=5)
    exch = normalize_exchange_flow(m.get("exchange_flow"), scale=20_000_000_000)

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

    signal = (
        "BUY" if final_score > 0.75 else
        "NEUTRAL" if final_score > 0.45 else
        "SELL"
    )

    return {
        "symbol": symbol,

        "sentiment": {
            "label": label,
            "probability": prob,
            "score": sentiment_score
        },

        "onchain_normalized": {
            "active_addresses": addr,
            "transactions": tx,
            "hashrate": hash_r,
            "tvl": tvl,
            "nvt": nvt,
            "mvrv": mvrv,
            "exchange_flows": exch
        },

        "weights": weights,

        "onchain_score": float(onchain_score),
        "combined_score": float(final_score),
        "signal": signal,

        "metric_contributions": {
            "addr": addr * weights["active_addresses"],
            "tx": tx * weights["transactions"],
            "hashrate": hash_r * weights["hashrate"],
            "tvl": tvl * weights["tvl"],
            "nvt": nvt * weights["nvt"],
            "mvrv": mvrv * weights["mvrv"],
            "exchange_flows": exch * weights["exchange_flows"]
        }
    }