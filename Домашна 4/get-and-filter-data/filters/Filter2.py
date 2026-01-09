import os
import re
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime, date, timezone, time as dt_time
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from filters.Filter import *
from db_controller.db import Database

import pandas as pd
import requests
import time
from dotenv import load_dotenv
load_dotenv()

DEFAULT_TIMESTAMP = int(os.getenv('FILTER2_DEFAULT_TIMESTAMP', 1420070400))
THREADS_COUNT = int(os.getenv('FILTER2_THREAD_COUNT', 30))

def get_session_with_retries():
    """Return a requests session with retry logic."""
    session = requests.Session()
    retries = Retry(
        total=5,
        backoff_factor=1,  # waits 1s, 2s, 4s, 8s...
        status_forcelist=[429, 500, 502, 503, 504],
        allowed_methods=["HEAD", "GET", "OPTIONS"]
    )
    adapter = HTTPAdapter(max_retries=retries)
    session.mount("https://", adapter)
    session.mount("http://", adapter)
    return session

def get_midnight_utc_timestamp(dateGiven=None):
    """Return timestamp of midnight UTC for a given date or today."""
    dt = dateGiven or datetime.now(timezone.utc).date()
    midnight = datetime.combine(dt, dt_time(0, 0, 0, tzinfo=timezone.utc))
    return int(midnight.timestamp())

class GetDataForCoinsFilter(Filter):
    """Filter for fetching daily OHLCV and market data for coins."""

    def __init__(self):
        self.db = Database()

    def process(self, data):
        """Fetch daily OHLCV data for given symbols from last timestamp to today."""
        result_dfs = []

        last_fetched_timestamp = self.db.fetchone("SELECT EXTRACT(EPOCH FROM DATE_TRUNC('day', TO_TIMESTAMP(MAX(timestamp))))::BIGINT AS last_timestamp FROM market_data;")['last_timestamp']
        start_timestamp = last_fetched_timestamp or DEFAULT_TIMESTAMP
        end_timestamp = get_midnight_utc_timestamp()

        if end_timestamp == start_timestamp:
            print("Data is up to date!")
            return pd.DataFrame() 

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

    def process_stream(self, data, out_queue):

        self.db = Database()
        end_timestamp = get_midnight_utc_timestamp()

        # Fetch one last timestamp per symbol
        # Multiplication and division by 86400 is done to
        # normalize the timestamp to midnight
        # With this each day has a single consistent timestamp, even if the database
        # returns a non-midnight time for the latest data point.
        rows = self.db.fetchall("""
            SELECT DISTINCT ON (symbol)
                symbol,
                (timestamp / 86400) * 86400 AS last_timestamp 
            FROM market_data
            ORDER BY symbol, timestamp DESC;
        """)
        last_ts_map = {row['symbol']: row['last_timestamp'] for row in rows}

        with ThreadPoolExecutor(max_workers=THREADS_COUNT) as executor:
            tasks = []
            for row in data.itertuples(index=False):
                sym = row.symbol
                cap = row.market_cap
                coin = {'symbol': sym, 'market_cap': cap}
                start_timestamp = last_ts_map.get(coin['symbol'], DEFAULT_TIMESTAMP)

                tasks.append(
                    executor.submit(
                        GetDataForCoinsFilter.get_daily_ohlcv,
                        coin, "USD", start_timestamp, end_timestamp
                    )
                )

            for future in as_completed(tasks):
                df, daily_df = future.result()
                if not df.empty:
                    out_queue.put({'type': 'ohlcv', 'data': df})
                if daily_df is not None and not daily_df.empty:
                    out_queue.put({'type': 'daily', 'data': daily_df})

        out_queue.put(None)

    @staticmethod
    def parse_data_to_df(data, coin):
        symbol = coin['symbol']
        market_cap = coin['market_cap']

        result = data['chart']['result'][0]
        quote = result['indicators']['quote'][0]
        meta = result['meta']

        if 'timestamp' not in result or not quote:
            return pd.DataFrame(), pd.DataFrame()

        df = pd.DataFrame({
            'symbol': symbol,
            'timestamp': result['timestamp'],
            'open': quote['open'],
            'high': quote['high'],
            'low': quote['low'],
            'close': quote['close'],
            'volume': quote['volume']
        })

        daily_df = pd.DataFrame({
            'symbol': [symbol],
            'name': [re.sub(r'\bUSD\b', '', str(meta.get('longName', symbol))).strip()],
            'timestamp': [meta.get('regularMarketTime', 0)],
            'market_cap': [market_cap],
            'last_price': [meta.get('regularMarketPrice', 0)],
            'volume_24h': [meta.get('regularMarketVolume', 0)],
            'high_24h': [meta.get('regularMarketDayHigh', 0)],
            'low_24h': [meta.get('regularMarketDayLow', 0)]
        })

        # print(daily_df)
        return df, daily_df

    @staticmethod
    def get_daily_ohlcv(coin, currency="USD", start_timestamp=DEFAULT_TIMESTAMP, end_timestamp=int(time.time())): #default start_timestamp is 01.01.2015 00:00:00
        symbol = coin['symbol']

        print(f"Fetching symbol: {symbol}...")
        url = f'https://query1.finance.yahoo.com/v8/finance/chart/{symbol}-{currency}?events=capitalGain%7Cdiv%7Csplit&formatted=true&includeAdjustedClose=true&interval=1d&period1={start_timestamp}&period2={end_timestamp}&symbol=BTC-USD&userYfid=true&lang=en-US&region=US'

        headers = {
            "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64)",
            "Accept-Language": "en-US,en;q=0.9",
            "Accept-Encoding": "gzip, deflate, br",
            "Connection": "keep-alive"
        }

        session = get_session_with_retries()
        try:
            resp = session.get(url, headers=headers, timeout=10)
            resp.raise_for_status()
            data = resp.json()
        except requests.exceptions.RequestException as e:
            print(f"Failed to fetch {symbol}: {e}")
            return pd.DataFrame(), pd.DataFrame()

        print(f"Fetched symbol: {symbol}!")
        return GetDataForCoinsFilter.parse_data_to_df(data, coin)