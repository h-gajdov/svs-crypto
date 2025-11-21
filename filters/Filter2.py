from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime, date, timezone, time as dt_time

from filters.Filter import *
from db_controller.db import Database

import pandas as pd
import requests
import time

THREADS_COUNT = 40

class GetDataForCoinsFilter(Filter):
    def __init__(self):
        self.db = Database()

    def process(self, data):
        result_dfs = []

        last_fetched_timestamp = self.db.fetchone("SELECT EXTRACT(EPOCH FROM DATE_TRUNC('day', TO_TIMESTAMP(MAX(timestamp))))::BIGINT AS last_timestamp FROM market_data;")['last_timestamp']
        start_timestamp = last_fetched_timestamp if last_fetched_timestamp else 1420070400

        midnight_utc = datetime.combine(date.today(), dt_time(0, 0, 0, tzinfo=timezone.utc))
        end_timestamp = int(midnight_utc.timestamp()) #timestamp of today's date at 00:00

        if end_timestamp == start_timestamp:
            print("Data is up to date!")
            return pd.DataFrame() #dont fetch data just return empty data frame

        with ThreadPoolExecutor(max_workers=THREADS_COUNT) as executor:
            ohlcv = [executor.submit(GetDataForCoinsFilter.get_daily_ohlcv, sym, "USD", start_timestamp, end_timestamp) for sym in data["symbol"]] #[:1] means take only the first coin to take all coins just delete [:1]

            for future in as_completed(ohlcv):
                df = future.result()
                if not df.empty:
                    result_dfs.append(df)

        final_df = pd.concat(result_dfs, ignore_index=True)
        final_df = final_df.ffill() #sometimes some rows are NaN
        print(final_df.tail())
        print(f"Length: {len(final_df)}")
        return final_df

    @staticmethod
    def parse_data_to_df(data, symbol):
        result = data['chart']['result'][0]
        quote = result['indicators']['quote'][0]

        if 'timestamp' not in result:
            return pd.DataFrame()

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
    def get_daily_ohlcv(symbol, currency="USD", start_timestamp=1420070400, end_timestamp=int(time.time())): #default start_timestamp is 01.01.2015 00:00:00
        print(f"Fetching symbol: {symbol}...")
        url = f'https://query1.finance.yahoo.com/v8/finance/chart/{symbol}-{currency}?events=capitalGain%7Cdiv%7Csplit&formatted=true&includeAdjustedClose=true&interval=1d&period1={start_timestamp}&period2={end_timestamp}&symbol=BTC-USD&userYfid=true&lang=en-US&region=US'

        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
            "Accept-Language": "en-US,en;q=0.9",
            "Accept-Encoding": "gzip, deflate, br",
            "Connection": "keep-alive"
        }

        session = requests.Session()
        resp = session.get(url, headers=headers)
        data = resp.json()

        if resp.status_code != 200 or not data:
            print(f"Error fetching {symbol}: HTTP {resp.status_code}")
            return pd.DataFrame()

        print(f"Fetched symbol: {symbol}!")
        return GetDataForCoinsFilter.parse_data_to_df(data, symbol)