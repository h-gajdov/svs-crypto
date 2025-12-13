import os
import torch
import requests

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

def get_news_from_newsdataio(symbol):
    url = f'https://newsdata.io/api/1/latest?apikey=pub_14c587b00eb94d3f857fcb9e99d558fd&qInTitle={symbol}&language=en&video=0'
    response = requests.get(url)
    news = response.json()['results']

    news_raw = [{
        'author': ev['creator'],
        'headline': ev['title'],
        'content': '', #content is available only for paid users
        'created_at': ev['pubDate'].replace(' ', 'T'),
        'image': ev['source_icon'],
        'source': ev['source_name'],
        'summary': ev['description'],
        'url': ev['link']
        } for ev in news]
    return news_raw

def get_news_from_alpaca(symbol, daysBefore=3):
    today, three_days_prior = get_dates(daysBefore)
    news = api.get_news(
        symbol=f"{symbol}/USD", start=three_days_prior, end=today
    )

    news_raw = [{
        'author': [ev.__dict__['_raw']['author']],
        'headline': ev.__dict__['_raw']['headline'],
        'content': ev.__dict__['_raw']['content'],
        'created_at': ev.__dict__['_raw']['created_at'],
        'image': ev.__dict__['_raw']['images'][1]['url'],
        'source': ev.__dict__['_raw']['source'],
        'summary': ev.__dict__['_raw']['summary'],
        'url': ev.__dict__['_raw']['url']
        } for ev in news]
    return news_raw

def get_sentiment(symbol, daysBefore=3):
    result = []
    result.extend(get_news_from_alpaca(symbol, daysBefore))
    result.extend(get_news_from_newsdataio(symbol))
    return result

def estimate_sentiment(news):
    if news and isinstance(news[0], dict):
        news = [
            # (n.get('headline', '') + ' ' + n.get('content', '') + ' ' + n.get('summary', '')).strip()
            (n.get('headline', '')).strip() #use headline only because trainmoing is very slow
            for n in news
        ]

    print(news)

    if news:
        tokens = tokenizer(news, return_tensors="pt", padding=True, truncation=True).to(device)

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
    news = get_sentiment("BTC,bitcoin")
    tensor, sentiment = estimate_sentiment(news)
    print(news)
    print(tensor, sentiment)

    #test with predefined news
    test_sentences = [
        "Investors are pessimistic about the upcoming earnings report.",
        "The stock price surged after negative market news.",
        "Analysts are worried about the declining economic indicators.",
        "There is excitement over the new product launch.",
        "Market uncertainty continues to affect trading decisions."
    ]

    tensor, sentiment = estimate_sentiment(test_sentences)
    print(tensor, sentiment)