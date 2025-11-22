from coinpaprika import client as Coinpaprika
from concurrent.futures import ThreadPoolExecutor, as_completed
from filters.Filter import *

from db_controller.db import Database

import pandas as pd
import requests
import re
from bs4 import BeautifulSoup

URLs = [
"https://www.coingecko.com/?items=300",
"https://www.coingecko.com/?page=2&items=300",
"https://www.coingecko.com/?page=3&items=300",
"https://www.coingecko.com/?page=4&items=300",
"https://www.coingecko.com/?page=5&items=300",
"https://www.coingecko.com/?page=6&items=300",
# "https://www.coingecko.com/?page=5&items=200",
# "https://www.coingecko.com/?page=6&items=200",
]

def fetch_url(url):
    try:
        HEADERS = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                          "AppleWebKit/537.36 (KHTML, like Gecko) "
                          "Chrome/120.0.0.0 Safari/537.36"
        }
        resp = requests.get(url, headers=HEADERS, timeout=10)
        bs = BeautifulSoup(resp.text, "html.parser")
        data = []
        rows = bs.select("table.gecko-homepage-coin-table > tbody > tr")
        for row in rows:
            cells = row.select("td")
            index = int(cells[1].text)
            symbol = cells[2].select_one("a > div > div > div").text.strip()
            vol = float(cells[9].select_one("span").text[1:].replace(",", ""))
            data.append({"index": index, "symbol": symbol, "volume": vol})
        return data
    except Exception as e:
        print(f"[ERROR] Failed to fetch {url}: {e}")
        return []

def get_top_coins():
    all_coins = []

    with ThreadPoolExecutor(max_workers=len(URLs)) as executor:
        futures = [executor.submit(fetch_url, url) for url in URLs]

        for future in as_completed(futures):
            coins = future.result()
            all_coins.extend(coins)

    return all_coins

def filter_liquid_coins(coins, min_liquidity=1_000_000):
    filtered = []
    for c in coins:
        volume = c.get("volume", 0)
        if volume >= min_liquidity:
            filtered.append(c)
    return filtered

def get_symbols_from_coingecko():
    coins = get_top_coins()
    liquid_coins = filter_liquid_coins(coins)
    print(len(liquid_coins))
    df = pd.DataFrame(liquid_coins)
    df = df.drop_duplicates(subset="symbol")  # drop duplicate symbols
    df = df.head(1200)
    return df

def get_symbols_from_coinpaprika():
    client = Coinpaprika.Client()
    df = pd.DataFrame(client.coins())

    df = df[df["is_active"] == True] #only active coinst
    df = df.drop_duplicates(subset="symbol") #drop duplicate symbols

    return df[['symbol']].head(1100)

def get_symbols_from_db(db):
    rows = db.fetchall('SELECT DISTINCT symbol FROM market_data;')
    df = pd.DataFrame(rows)
    return df

class Top1000CoinsFilter(Filter):
    def __init__(self):
        self.db = Database()

    def process(self, data):
        if self.db.is_empty():
            return get_symbols_from_coingecko()
        else:
            return get_symbols_from_db(self.db)
