#to run: uvicorn main:app --reload --port 8000
from sentiment.sentiment_analysis import *
from onchain.onchain_metrics import  *
from fastapi import FastAPI

app = FastAPI()

@app.get("/check-connection")
def check_connection():
    return {"status": "FastAPI is running"}

@app.get("/get-news/{symbol}")
def get_news(symbol):
    news = get_sentiment(symbol)
    return {"symbol": symbol, "news": news}

@app.get("/estimate-news/{symbol}")
def estimate_news_for_symbol(symbol):
    news = get_sentiment(symbol)
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
def transactions_count(symbol, daysBefore = 1):
    return get_transactions_count(symbol, daysBefore)

@app.get("/whale-movements")
def whale_movements(number_of_alerts = 5):
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
def hash_rate(symbol, daysBefore = 1):
    return get_hash_rate(symbol, daysBefore)

@app.get("/mvrv-ratio/{symbol}")
def mvrv_ratio(symbol, daysBefore = 1):
    return get_mvrv_ratio(symbol, daysBefore)

@app.get("/exchange-flow/{symbol}")
def exchange_flow(symbol):
    return get_exchange_flow(symbol)

@app.get("/metrics/{symbol}")
def all_metrics(symbol): #gets all latest metrics
    return get_all_metrics(symbol)