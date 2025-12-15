import os
import torch
import requests

from dotenv import load_dotenv
from alpaca_trade_api import REST
from datetime import datetime, timedelta
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import feedparser

load_dotenv('../../.env')

RSS_FEEDS = [
    "https://cointelegraph.com/rss",
    "https://www.coindesk.com/arc/outboundfeeds/rss/",
    "https://decrypt.co/feed",
    "https://bitcoinmagazine.com/feed"
]

ALPACA_API_KEY = os.getenv("ALPACA_API_KEY")
ALPACA_API_SECRET = os.getenv("ALPACA_API_SECRET")
NEWS_DATA_API_KEY = os.getenv('NEWS_DATA_API_KEY')
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
    url = (
        f"https://newsdata.io/api/1/latest"
        f"?apikey={NEWS_DATA_API_KEY}&qInTitle={symbol}&language=en&video=0"
    )

    try:
        response = requests.get(url, timeout=5)
        response.raise_for_status()
        data = response.json()
    except (requests.RequestException, ValueError):
        return []

    news = data.get("results")
    if not isinstance(news, list):
        return []

    if data['status'] == 'error':
        return []

    news_raw = []
    for ev in news:
        if not isinstance(ev, dict):
            continue

        news_raw.append({
            "author": ev.get("creator") or [""],
            "headline": ev.get("title") or "",
            "content": "",  # paid-only field
            "created_at": (ev.get("pubDate") or "").replace(" ", "T"),
            "image": ev.get("source_icon") or "",
            "source": ev.get("source_name") or "",
            "summary": ev.get("description") or "",
            "url": ev.get("link") or ""
        })

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
        'image': ev.__dict__['_raw']['images'][1]['url'] if ev.__dict__['_raw']['images'] else '',
        'source': ev.__dict__['_raw']['source'],
        'summary': ev.__dict__['_raw']['summary'],
        'url': ev.__dict__['_raw']['url']
    } for ev in news]
    return news_raw


def get_news_from_rss(symbol):
    aggregated_news = []

    search_terms = [s.strip().lower() for s in symbol.split(',')]

    for url in RSS_FEEDS:
        try:
            feed = feedparser.parse(url)
            for entry in feed.entries:
                content_text = (
                        entry.title + " " + getattr(entry, 'summary', getattr(entry, 'description', ''))).lower()
                if any(term in content_text for term in search_terms):
                    published_date = getattr(entry, 'published', str(datetime.now()))

                    aggregated_news.append({
                        'author': [getattr(entry, 'author', 'Unknown')],
                        'headline': entry.title,
                        'content': getattr(entry, 'summary', getattr(entry, 'description', '')),
                        'created_at': published_date,
                        'image': '',
                        'source': feed.feed.get('title', 'RSS Source'),
                        'summary': getattr(entry, 'summary', ''),
                        'url': entry.link
                    })

        except Exception as e:
            print(f"Failed to parse RSS {url}: {e}")
            continue

    return aggregated_news


def get_sentiment(symbol, daysBefore=3):
    result = []
    result.extend(get_news_from_alpaca(symbol, daysBefore))
    result.extend(get_news_from_newsdataio(symbol))
    result.extend(get_news_from_rss(symbol))
    return result


def estimate_sentiment(news):
    if news and isinstance(news[0], dict):
        news = [
            # (n.get('headline', '') + ' ' + n.get('content', '') + ' ' + n.get('summary', '')).strip()
            (n.get('headline', '')).strip()  # use headline only because trainmoing is very slow
            for n in news
        ]

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
    # test with fresh news
    news = get_sentiment("BTC,bitcoin")
    tensor, sentiment = estimate_sentiment(news)
    print(news)
    print(tensor, sentiment)

    # test with predefined news
    test_sentences = [
        "Investors are pessimistic about the upcoming earnings report.",
        "The stock price surged after negative market news.",
        "Analysts are worried about the declining economic indicators.",
        "There is excitement over the new product launch.",
        "Market uncertainty continues to affect trading decisions."
    ]

    tensor, sentiment = estimate_sentiment(test_sentences)
    print(tensor, sentiment)
