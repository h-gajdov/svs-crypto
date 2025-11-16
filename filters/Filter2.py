from concurrent.futures import ThreadPoolExecutor, as_completed

from filters.Filter import *

import requests
import pandas as pd
import requests

THREADS_COUNT = 10

class GetDataForCoinsFilter(Filter):
    def process(self, data):
        result_dfs = []

        with ThreadPoolExecutor(max_workers=THREADS_COUNT) as executor:
            ohlcv = [executor.submit(GetDataForCoinsFilter.get_daily_ohlcv, sym, "USD") for sym in data["symbol"][:1]] #[:1] means take only the first coin to take all coins just delete [:1]

            for future in as_completed(ohlcv):
                df = future.result()
                if not df.empty:
                    result_dfs.append(df)

        final_df = pd.concat(result_dfs, ignore_index=True)
        final_df.fillna(method='bfill', inplace=True) #sometimes some rows are NaN
        print(final_df.tail())
        print(f"Length: {len(final_df)}")
        return final_df

    @staticmethod
    def parse_data_to_df(data, symbol):
        result = data['chart']['result'][0]
        quote = result['indicators']['quote'][0]

        df = pd.DataFrame({
            'symbol': symbol,
            # 'utc': pd.to_datetime(result['timestamp'], unit='s'),
            'timestamp': result['timestamp'],
            'open': quote['open'],
            'high': quote['high'],
            'low': quote['low'],
            'close': quote['close'],
            'volume': quote['volume']
        })

        return df

    @staticmethod
    def get_daily_ohlcv(symbol, currency="USD", start_year=2015):
        print(f"Fetching symbol: {symbol}...")
        url = f'https://query1.finance.yahoo.com/v8/finance/chart/{symbol}-{currency}?events=capitalGain%7Cdiv%7Csplit&formatted=true&includeAdjustedClose=true&interval=1d&period1=1420070400&period2=1763254946&symbol=BTC-USD&userYfid=true&lang=en-US&region=US'

        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
            "Accept-Language": "en-US,en;q=0.9",
            "Accept-Encoding": "gzip, deflate, br",
            "Connection": "keep-alive"
        }

        session = requests.Session()
        resp = session.get(url, headers=headers)
        data = resp.json()

        if resp.status_code != 200:
            print(f"Error fetching {symbol}: HTTP {resp.status_code}")
            return pd.DataFrame()

        print(f"Fetched symbol: {symbol}!")
        return GetDataForCoinsFilter.parse_data_to_df(data, symbol)