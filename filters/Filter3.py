from filters.Filter import *
import time
import psycopg2

class FillDatabaseFilter(Filter):

    def __init__(self):
        self.conn = psycopg2.connect(
            host="localhost",
            database="mydb",
            user="docker",
            password="docker",
        )
        self.cur = self.conn.cursor()

    def process(self, data):
        start_time = time.time()  
        try:
            data_to_insert = [
                (row['symbol'], row['timestamp'], row['open'], row['high'], row['low'], row['close'], row['volume'])
                for index, row in data.iterrows()
            ]
            self.cur.executemany("""INSERT INTO market_data 
                                    (symbol, timestamp, open, high, low, close, volume) 
                                    VALUES (%s, %s, %s, %s, %s, %s, %s)""", data_to_insert)

            self.conn.commit()
        except Exception as e:
            self.conn.rollback()
            print(f"Error occurred: {e}")
        finally:
            self.cur.close()
            self.conn.close()

        end_time = time.time()  
        elapsed_time = end_time - start_time 
        print(f"Filter 3 finish in {elapsed_time:.4f} seconds.")
        return data
