from coinpaprika import client as Coinpaprika
from filters.Filter import *
import pandas as pd

class Top1000CoinsFilter(Filter):
    def process(self, data):
        client = Coinpaprika.Client()
        df = pd.DataFrame(client.coins())

        return df[df["is_active"] == True].head(1000)