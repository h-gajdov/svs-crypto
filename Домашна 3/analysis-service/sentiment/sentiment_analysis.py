import os
import torch

from dotenv import load_dotenv
from alpaca_trade_api import REST
from datetime import datetime, timedelta
from transformers import AutoTokenizer, AutoModelForSequenceClassification

dotenv_path = os.path.abspath(os.path.join(os.getcwd(), "..", "..", ".env"))
load_dotenv(dotenv_path)

ALPACA_API_KEY = os.getenv("ALPACA_API_KEY")
ALPACA_API_SECRET = os.getenv("ALPACA_API_SECRET")
BASE_URL = 'https://paper-api.alpaca.markets/v2'

alpaca_rest = REST(base_url=BASE_URL, key_id=ALPACA_API_KEY, secret_key=ALPACA_API_SECRET)

def get_dates(daysBefore=3):
    today = datetime.now() 
    three_days_prior = today - timedelta(days=daysBefore)
    return today.strftime("%Y-%m-%d"), three_days_prior.strftime("%Y-%m-%d")

def get_sentiment(symbol, daysBefore=3):
    today, three_days_prior = get_dates(daysBefore)
    news = alpaca_rest.get_news(
        symbol=f"{symbol}/USD", start=three_days_prior, end=today
    )
    news = [ev.__dict__["_raw"]["headline"] for ev in news]
    return news

if __name__ == "__main__":
    news = get_sentiment("BTC")
    print(news)