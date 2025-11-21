from coinpaprika import client as Coinpaprika
from filters.Filter import *

from db_controller.db import Database

import pandas as pd

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
            return get_symbols_from_coinpaprika()
        else:
            return get_symbols_from_db(self.db)