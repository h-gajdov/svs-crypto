from filters.Filter import *

import psycopg2

conn = psycopg2.connect(
    host="localhost",
    database="mydb",
    user="docker",
    password="docker",
)
cur = conn.cursor()

class FillDatabaseFilter(Filter):
    def process(self, data):
        for index, row in data.iterrows():
            cur.execute(f"""INSERT INTO market_data (symbol, timestamp, open, high, low, close, volume) VALUES
                            ('{row['symbol']}', {row['timestamp']}, {row['open']}, {row['high']}, {row['low']}, {row['close']}, {row['volume']})""")

        conn.commit()
        cur.close()
        conn.close()
        return data