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

@app.get('/get-exchange-flow/{symbol}')
def exchange_flow(symbol):
    return get_exchange_flow(symbol)