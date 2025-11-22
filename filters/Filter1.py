# from coinpaprika import client as Coinpaprika
from concurrent.futures import ThreadPoolExecutor, as_completed
from filters.Filter import *
import pandas as pd
import requests
import re
from bs4 import BeautifulSoup


class Top1000CoinsFilter(Filter):
    # Gets extra coins just to have spare after filtering we can remove the spare if coins > 1000
    URLs = [
        "https://www.coingecko.com/?items=300",
        "https://www.coingecko.com/?page=2&items=300",
        "https://www.coingecko.com/?page=3&items=300",
        "https://www.coingecko.com/?page=4&items=300",
        "https://www.coingecko.com/?page=5&items=300",
        # "https://www.coingecko.com/?page=5&items=200",
        # "https://www.coingecko.com/?page=6&items=200",
    ]

    @staticmethod
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

    def get_top_coins(self):
        all_coins = []

        with ThreadPoolExecutor(max_workers=len(self.URLs)) as executor:
            futures = [executor.submit(self.fetch_url, url) for url in self.URLs]

            for future in as_completed(futures):
                coins = future.result()
                all_coins.extend(coins)

        return all_coins

    def filter_liquid_coins(self, coins, min_liquidity=1_000_000):
        filtered = []
        for c in coins:
            volume = c.get("volume", 0)
            if volume >= min_liquidity:
                filtered.append(c)
        return filtered

    def process(self, data):
        # coins1 = Coinpaprika.Client().coins()
        # df1 = pd.DataFrame(coins1)
        # df1.drop(columns=["rank", "is_new", "is_active", "type"], inplace=True)
        coins = self.get_top_coins()
        liquid_coins = self.filter_liquid_coins(coins)
        print(len(liquid_coins))
        df = pd.DataFrame(liquid_coins)
        # df = df[["ticker"]]

        # df = df[df["is_active"] == True]  # only active coins - ova nz kako da go proverime so yfinance
        df = df.drop_duplicates(subset="symbol")  # drop duplicate symbols
        df = df.head(1000)
        print(df.info())
        # df["symbol"] = df["ticker"].apply(lambda text: re.split("-", text)[0])
        # df_intersection = df[df["symbol"].isin(df1["symbol"])]
        # df.to_csv("./test.csv")
        # print(df.info())
        return df
