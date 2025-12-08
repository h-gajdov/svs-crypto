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

api = REST(base_url=BASE_URL, key_id=ALPACA_API_KEY, secret_key=ALPACA_API_SECRET)

device = "cuda:0" if torch.cuda.is_available() else "cpu"
tokenizer = AutoTokenizer.from_pretrained("ProsusAI/finbert")
model = AutoModelForSequenceClassification.from_pretrained(
    "ProsusAI/finbert",
    trust_remote_code=True,
).to(device)
labels = ["positive", "negative"]

def get_dates(daysBefore=3):
    today = datetime.now() 
    three_days_prior = today - timedelta(days=daysBefore)
    return today.strftime("%Y-%m-%d"), three_days_prior.strftime("%Y-%m-%d")

def get_sentiment(symbol, daysBefore=3):
    today, three_days_prior = get_dates(daysBefore)
    news = api.get_news(
        symbol=f"{symbol}/USD", start=three_days_prior, end=today
    )
    news = [ev.__dict__["_raw"]["headline"] for ev in news]
    return news

def estimate_sentiment(news):
    if news:
        tokens = tokenizer(news, return_tensors="pt", padding=True).to(device)

        logits = model(tokens["input_ids"], attention_mask=tokens["attention_mask"])["logits"]
        logits = torch.sum(logits, dim=0)

        binary_logits = logits[:2]
        probs = torch.nn.functional.softmax(binary_logits, dim=-1)

        idx = torch.argmax(probs)
        sentiment = labels[idx]
        probability = probs[idx].item()

        return probability, sentiment
    else:
        return 0, "negative"

if __name__ == "__main__":
    #test with fresh news
    news = get_sentiment("XRP")
    tensor, sentiment = estimate_sentiment(news)
    print(news)
    print(tensor, sentiment)

    #test with predefined news
    test_sentences = [
        "Investors are optimistic about the upcoming earnings report.",
        "The stock price surged after positive market news.",
        "Analysts are worried about the declining economic indicators.",
        "There is excitement over the new product launch.",
        "Market uncertainty continues to affect trading decisions."
    ]

    tensor, sentiment = estimate_sentiment(test_sentences)
    print(tensor, sentiment)