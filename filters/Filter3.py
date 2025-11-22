from filters.Filter import *
from db_controller.db import Database
import time
import psycopg2
from io import StringIO

class FillDatabaseFilter(Filter):

    def __init__(self):
        self.db = Database()

    def process(self, data):
        if self.db.is_empty():
            return self.fill_empty_database(data)
        else:
            return self.update_database(data)

    def fill_empty_database(self, data):
        start_time = time.time()
        try:
            csv_buffer = StringIO()
            data.to_csv(csv_buffer, index=False, header=True)
            csv_buffer.seek(0)

            # Copy the CSV into the table
            self.db.copy_expert(
                """COPY market_data(symbol, timestamp, open, high, low, close, volume)
                   FROM STDIN WITH CSV HEADER""",
                csv_buffer)

            self.db.commit()
        except Exception as e:
            self.db.roll_back()
            print(f"Error occurred: {e}")
        finally:
            self.db.close()

        end_time = time.time()
        elapsed_time = end_time - start_time
        print(f"Filter 3 finished in {elapsed_time:.4f} seconds.")
        return data

    # duplicates safe function
    def update_database(self, data):
        start_time = time.time()
        try:
            csv_buffer = StringIO()
            data.to_csv(csv_buffer, index=False, header=True)
            csv_buffer.seek(0)

            # Copy to temporary table
            self.db.execute("TRUNCATE TABLE market_data_staging")
            self.db.copy_expert("""
                COPY market_data_staging(symbol, timestamp, open, high, low, close, volume)
                FROM STDIN WITH CSV HEADER
            """, csv_buffer)

            # Deal with duplicates
            self.db.execute("""
                INSERT INTO market_data(symbol, timestamp, open, high, low, close, volume)
                SELECT *
                FROM market_data_staging
                ON CONFLICT (symbol, timestamp) DO NOTHING;
            """)

            self.db.commit()
        except Exception as e:
            self.db.roll_back()
            print(f"Error: {e}")
        finally:
            self.db.close()

        print(f"Filter 3 finished in {time.time() - start_time:.4f} seconds.")
        return data