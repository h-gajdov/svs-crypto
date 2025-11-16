from filters.Filter import *
import time
import psycopg2
from io import StringIO

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
            csv_buffer = StringIO()
            data.to_csv(csv_buffer, index=False, header=True)
            csv_buffer.seek(0)

            # Copy the CSV into the table
            self.cur.copy_expert(
                """COPY market_data(symbol, timestamp, open, high, low, close, volume)
                   FROM STDIN WITH CSV HEADER""",
                csv_buffer
            )

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

    # duplicates safe function
    def process_duplicate_safe(self, data):
        start_time = time.time()
        try:
            required_cols = ['symbol', 'timestamp', 'open', 'high', 'low', 'close', 'volume']
            data = data[required_cols]

            #temporary table for dealing with duplicates
            self.cur.execute("""
                CREATE TEMP TABLE tmp_market_data (
                    symbol TEXT,
                    timestamp BIGINT,
                    open NUMERIC,
                    high NUMERIC,
                    low NUMERIC,
                    close NUMERIC,
                    volume NUMERIC
                ) ON COMMIT DROP;
            """)

            csv_buffer = StringIO()
            data.to_csv(csv_buffer, index=False, header=True)
            csv_buffer.seek(0)

            self.cur.copy_expert(
                "COPY tmp_market_data(symbol, timestamp, open, high, low, close, volume) FROM STDIN WITH CSV HEADER",
                csv_buffer
            )

            self.cur.execute("""
                INSERT INTO market_data (symbol, timestamp, open, high, low, close, volume)
                SELECT * FROM tmp_market_data
                ON CONFLICT (symbol, timestamp) DO NOTHING
            """)

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