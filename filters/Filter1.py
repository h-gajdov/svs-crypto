# from coinpaprika import client as Coinpaprika
from concurrent.futures import ThreadPoolExecutor, as_completed
from filters.Filter import *
import pandas as pd
import requests
import re


class Top1000CoinsFilter(Filter):
    # Gets extra coins just to have spare after filtering we can remove the spare if coins > 1000
    URLs = [
        "https://query1.finance.yahoo.com/v1/finance/screener/predefined/saved?count=250&formatted=true&scrIds=ALL_CRYPTOCURRENCIES_US&sortField=intradaymarketcap&sortType=desc&start=0&useRecordsResponse=true&fields=ticker%2ClogoUrl%2Csymbol%2ClongName%2Csparkline%2CshortName%2CregularMarketPrice%2CregularMarketChange%2CregularMarketChangePercent%2CmarketCap%2CregularMarketVolume%2Cvolume24Hr%2CvolumeAllCurrencies%2CcirculatingSupply%2CfiftyTwoWeekChangePercent%2CfiftyTwoWeekRange&lang=en-US&region=US",
        "https://query1.finance.yahoo.com/v1/finance/screener/predefined/saved?count=250&formatted=true&scrIds=ALL_CRYPTOCURRENCIES_US&sortField=intradaymarketcap&sortType=desc&start=250&useRecordsResponse=true&fields=ticker%2ClogoUrl%2Csymbol%2ClongName%2Csparkline%2CshortName%2CregularMarketPrice%2CregularMarketChange%2CregularMarketChangePercent%2CmarketCap%2CregularMarketVolume%2Cvolume24Hr%2CvolumeAllCurrencies%2CcirculatingSupply%2CfiftyTwoWeekChangePercent%2CfiftyTwoWeekRange&lang=en-US&region=US",
        "https://query1.finance.yahoo.com/v1/finance/screener/predefined/saved?count=250&formatted=true&scrIds=ALL_CRYPTOCURRENCIES_US&sortField=intradaymarketcap&sortType=desc&start=500&useRecordsResponse=true&fields=ticker%2ClogoUrl%2Csymbol%2ClongName%2Csparkline%2CshortName%2CregularMarketPrice%2CregularMarketChange%2CregularMarketChangePercent%2CmarketCap%2CregularMarketVolume%2Cvolume24Hr%2CvolumeAllCurrencies%2CcirculatingSupply%2CfiftyTwoWeekChangePercent%2CfiftyTwoWeekRange&lang=en-US&region=US",
        "https://query1.finance.yahoo.com/v1/finance/screener/predefined/saved?count=250&formatted=true&scrIds=ALL_CRYPTOCURRENCIES_US&sortField=intradaymarketcap&sortType=desc&start=750&useRecordsResponse=true&fields=ticker%2ClogoUrl%2Csymbol%2ClongName%2Csparkline%2CshortName%2CregularMarketPrice%2CregularMarketChange%2CregularMarketChangePercent%2CmarketCap%2CregularMarketVolume%2Cvolume24Hr%2CvolumeAllCurrencies%2CcirculatingSupply%2CfiftyTwoWeekChangePercent%2CfiftyTwoWeekRange&lang=en-US&region=US",
        "https://query1.finance.yahoo.com/v1/finance/screener/predefined/saved?count=250&formatted=true&scrIds=ALL_CRYPTOCURRENCIES_US&sortField=intradaymarketcap&sortType=desc&start=1000&useRecordsResponse=true&fields=ticker%2ClogoUrl%2Csymbol%2ClongName%2Csparkline%2CshortName%2CregularMarketPrice%2CregularMarketChange%2CregularMarketChangePercent%2CmarketCap%2CregularMarketVolume%2Cvolume24Hr%2CvolumeAllCurrencies%2CcirculatingSupply%2CfiftyTwoWeekChangePercent%2CfiftyTwoWeekRange&lang=en-US&region=US",
        "https://query1.finance.yahoo.com/v1/finance/screener/predefined/saved?count=250&formatted=true&scrIds=ALL_CRYPTOCURRENCIES_US&sortField=intradaymarketcap&sortType=desc&start=1250&useRecordsResponse=true&fields=ticker%2ClogoUrl%2Csymbol%2ClongName%2Csparkline%2CshortName%2CregularMarketPrice%2CregularMarketChange%2CregularMarketChangePercent%2CmarketCap%2CregularMarketVolume%2Cvolume24Hr%2CvolumeAllCurrencies%2CcirculatingSupply%2CfiftyTwoWeekChangePercent%2CfiftyTwoWeekRange&lang=en-US&region=US",
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
            resp.raise_for_status()
            data = resp.json()
            return data["finance"]["result"][0]["records"]
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
            volume = c.get("volume24Hr", {}).get("raw", 0)
            if volume >= min_liquidity:
                filtered.append(c)
        return filtered

    def process(self, data):
        # coins1 = Coinpaprika.Client().coins()
        # df1 = pd.DataFrame(coins1)
        # df1.drop(columns=["rank", "is_new", "is_active", "type"], inplace=True)
        coins = self.get_top_coins()
        print(len(coins))
        liquid_coins = self.filter_liquid_coins(coins)
        df = pd.DataFrame(liquid_coins)
        df = df[["ticker"]]

        # df = df[df["is_active"] == True]  # only active coins - ova nz kako da go proverime so yfinance
        df = df.drop_duplicates(subset="ticker")  # drop duplicate symbols
        df = df.head(1000)
        df["symbol"] = df["ticker"].apply(lambda text: re.split("-", text)[0])
        # df_intersection = df[df["symbol"].isin(df1["symbol"])]
        # print(df.info())
        return df
