from coinpaprika import client as Coinpaprika
from filters.Filter import *
import pandas as pd

class Top1000CoinsFilter(Filter):
    def process(self, data):
        client = Coinpaprika.Client()
        df = pd.DataFrame(client.coins())

        df = df[df["is_active"] == True] #only active coinst
        df = df.drop_duplicates(subset="symbol") #drop duplicate symbols

        return df.head(1000)