import os
from datetime import datetime, timedelta
from typing import List, Dict, Tuple

import torch
import requests
import feedparser
from dotenv import load_dotenv
from alpaca_trade_api import REST
from transformers import AutoTokenizer, AutoModelForSequenceClassification

load_dotenv('../../.env')

ALPACA_API_KEY = os.getenv("ALPACA_API_KEY")
ALPACA_API_SECRET = os.getenv("ALPACA_API_SECRET")
NEWS_DATA_API_KEY = os.getenv('NEWS_DATA_API_KEY')
BASE_URL = 'https://paper-api.alpaca.markets/v2'

RSS_FEEDS = [
    "https://cointelegraph.com/rss",
    "https://www.coindesk.com/arc/outboundfeeds/rss/",
    "https://decrypt.co/feed",
    "https://bitcoinmagazine.com/feed"
]

api = REST(base_url=BASE_URL, key_id=ALPACA_API_KEY, secret_key=ALPACA_API_SECRET)

device = "cuda:0" if torch.cuda.is_available() else "cpu"
tokenizer = AutoTokenizer.from_pretrained("ProsusAI/finbert")
model = AutoModelForSequenceClassification.from_pretrained(
    "ProsusAI/finbert",
    trust_remote_code=True,
).to(device)
labels = ["positive", "negative"]

def get_dates(days_before: int = 3) -> Tuple[str, str]:
    """Return (today, days_before) in YYYY-MM-DD format."""
    today = datetime.now()
    past = today - timedelta(days=days_before)
    return today.strftime("%Y-%m-%d"), past.strftime("%Y-%m-%d")


def build_news_entry(entry: dict) -> Dict:
    """Normalize news entry dict across different sources."""
    return {
        "author": entry.get("author") or [""],
        "headline": entry.get("headline", ""),
        "content": entry.get("content", ""),
        "created_at": entry.get("created_at", str(datetime.now())),
        "image": entry.get("image", ""),
        "source": entry.get("source", ""),
        "summary": entry.get("summary", ""),
        "url": entry.get("url", "")
    }

def get_news_from_newsdataio(symbol: str) -> List[Dict]:
    """Fetch news from NewsData.io API."""
    url = f"https://newsdata.io/api/1/latest?apikey={NEWS_DATA_API_KEY}&qInTitle={symbol}&language=en&video=0"
    try:
        data = requests.get(url, timeout=5).json()
    except (requests.RequestException, ValueError):
        return []

    if data.get('status') == 'error':
        return []

    news = data.get("results", [])
    result = []
    for ev in news:
        if not isinstance(ev, dict):
            continue
        result.append(build_news_entry({
            "author": ev.get("creator") or [""],
            "headline": ev.get("title", ""),
            "content": "",
            "created_at": (ev.get("pubDate") or "").replace(" ", "T"),
            "image": ev.get("source_icon", ""),
            "source": ev.get("source_name", ""),
            "summary": ev.get("description", ""),
            "url": ev.get("link", "")
        }))
    return result

def get_news_from_alpaca(symbol: str, days_before: int = 3) -> List[Dict]:
    """Fetch news from Alpaca API."""
    today, past = get_dates(days_before)
    news = api.get_news(symbol=f"{symbol}/USD", start=past, end=today)
    result = []
    for ev in news:
        raw = ev._raw
        image_url = raw.get("images")[1]["url"] if raw.get("images") else ""
        result.append(build_news_entry({
            "author": [raw.get("author", "")],
            "headline": raw.get("headline", ""),
            "content": raw.get("content", ""),
            "created_at": raw.get("created_at", ""),
            "image": image_url,
            "source": raw.get("source", ""),
            "summary": raw.get("summary", ""),
            "url": raw.get("url", "")
        }))
    return result

def get_news_from_rss(symbol: str) -> List[Dict]:
    """Fetch news from RSS feeds that match the symbol."""
    search_terms = [s.strip().lower() for s in symbol.split(',')]
    aggregated_news = []

    for url in RSS_FEEDS:
        try:
            feed = feedparser.parse(url)
            for entry in feed.entries:
                text = (entry.title + " " + getattr(entry, 'summary', getattr(entry, 'description', ''))).lower()
                if any(term in text for term in search_terms):
                    published_date = getattr(entry, 'published', str(datetime.now()))
                    aggregated_news.append(build_news_entry({
                        "author": [getattr(entry, 'author', 'Unknown')],
                        "headline": entry.title,
                        "content": getattr(entry, 'summary', getattr(entry, 'description', '')),
                        "created_at": published_date,
                        "image": "",
                        "source": feed.feed.get('title', 'RSS Source'),
                        "summary": getattr(entry, 'summary', ''),
                        "url": entry.link
                    }))
        except Exception as e:
            print(f"Failed to parse RSS {url}: {e}")
            continue

    return aggregated_news

def get_sentiment(symbol: str, days_before: int = 3) -> List[Dict]:
    """Aggregate news from multiple sources."""
    news = []
    news.extend(get_news_from_alpaca(symbol, days_before))
    news.extend(get_news_from_newsdataio(symbol))
    news.extend(get_news_from_rss(symbol))
    return news

def estimate_sentiment(news: List[str]) -> Tuple[float, str]:
    """Estimate sentiment from a list of headlines or news text."""
    if news and isinstance(news[0], dict):
        news = [n.get('headline', '').strip() for n in news]

    if not news:
        return 0, "negative"

    tokens = tokenizer(news, return_tensors="pt", padding=True, truncation=True).to(device)
    logits = model(tokens["input_ids"], attention_mask=tokens["attention_mask"])["logits"]
    logits_sum = torch.sum(logits, dim=0)[:2]
    probs = torch.nn.functional.softmax(logits_sum, dim=-1)

    idx = torch.argmax(probs)
    return probs[idx].item(), labels[idx]

if __name__ == "__main__":
    # test with fresh news
    news = get_sentiment("BTC,bitcoin")
    tensor, sentiment = estimate_sentiment(news)
    print(news)
    print(tensor, sentiment)

    test_sentences = [
        "Investors are pessimistic about the upcoming earnings report.",
        "The stock price surged after negative market news.",
        "Analysts are worried about the declining economic indicators.",
        "There is excitement over the new product launch.",
        "Market uncertainty continues to affect trading decisions."
    ]

    tensor, sentiment = estimate_sentiment(test_sentences)
    print(tensor, sentiment)
