from concurrent.futures import ThreadPoolExecutor, as_completed

from filters.Filter import *
from datetime import datetime

import pandas as pd
import requests

THREADS_COUNT = 10

class GetDataForCoinsFilter(Filter):
    def process(self, data):
        result_dfs = []

        with ThreadPoolExecutor(max_workers=THREADS_COUNT) as executor:
            ohlcv = [executor.submit(GetDataForCoinsFilter.get_daily_ohlcv, sym, "USD") for sym in data["symbol"]]

            for future in as_completed(ohlcv):
                df = future.result()
                if not df.empty:
                    result_dfs.append(df)

        final_df = pd.concat(result_dfs, ignore_index=True)
        print(final_df.head())
        return final_df

    @staticmethod
    def get_daily_ohlcv(symbol, currency, start_year=2015):
        print(f"Fetching symbol: {symbol}")
        url = "https://min-api.cryptocompare.com/data/v2/histoday"

        all_data = []
        to_ts = None  # no timestamp means fetch most recent first

        # run until we reach year <= start_year
        while True:
            params = {
                "fsym": symbol,
                "tsym": currency,
                "limit": 2000,
                "api_key": ""
            }
            if to_ts:
                params["toTs"] = to_ts

            r = requests.get(url, params=params)
            data_json = r.json()

            if "Data" not in data_json or "Data" not in data_json["Data"]:
                print(f"No data for {symbol}: {data_json}")
                return pd.DataFrame()
            data = data_json["Data"]["Data"]

            if not data:
                break

            all_data.extend(data)

            # earliest timestamp from this batch
            earliest_ts = data[0]["time"]
            earliest_year = datetime.utcfromtimestamp(earliest_ts).year

            # print(f"Fetched batch: earliest {earliest_year}")

            if earliest_year <= start_year:
                break

            # request older candles next
            to_ts = earliest_ts - 1

        # remove duplicates when merging
        all_data = {d["time"]: d for d in all_data}.values()

        # convert timestamps to UTC datetime
        # for d in all_data:
        #     d["utc"] = datetime.utcfromtimestamp(d["time"]).strftime("%Y-%m-%d %H:%M:%S")

        # sort by time
        # all_data = sorted(all_data, key=lambda x: x["time"])
        print(f"Fetched symbol: {symbol}")
        return pd.DataFrame(all_data)