import os
import time
import pandas as pd
from io import StringIO
from dotenv import load_dotenv

from filters.Filter import *
from db_controller.db import Database

load_dotenv()

BATCH_SIZE = int(os.getenv("BATCH_SIZE", 50))


class FillDatabaseFilter(Filter):

    def __init__(self):
        # We don't open the DB here to avoid connection timeouts
        # while waiting for the pipeline to start.
        pass

    def consume_stream(self, in_queue):
        self.db = Database()
        daily_data = []
        buffer = []

        is_fresh_db = self.db.is_empty()
        mode_name = "FAST LOAD" if is_fresh_db else "SAFE UPDATE"

        print(f"Filter 3: Connected. Mode: [{mode_name}]. Waiting for data...")
        start_time = time.time()

        try:
            while True:
                # Get dataframe from queue
                df_item = in_queue.get()

                # --- SENTINEL CHECK (End of Stream) ---
                if df_item is None:
                    if buffer:
                        self._flush_batch(self.db, buffer, is_fresh_db)
                    break

                if df_item['type'] == 'daily':
                    daily_data.append(df_item['data'])
                    continue

                # Add to buffer
                buffer.append(df_item['data'])

                # --- BATCH FLUSH ---
                if len(buffer) >= BATCH_SIZE:
                    self._flush_batch(self.db, buffer, is_fresh_db)
                    buffer = []  # Clear buffer

                in_queue.task_done()

            #When finished write daily data
            self._write_daily_data(self.db, daily_data)
        except Exception as e:
            print(f"Filter 3 Critical Error: {e}")
        finally:
            self.db.close()
            total_time = time.time() - start_time
            print(f"Filter 3 finished in {total_time:.4f} seconds.")

    def _flush_batch(self, db, buffer, is_fresh_db):
        """Prepares the batch and delegates to the correct write method"""
        if not buffer:
            return

        try:
            # Merge buffer into one DataFrame
            batch_df = pd.concat(buffer, ignore_index=True)
            batch_df = batch_df.ffill()  # Handle NaNs

            # Delegate to the specific strategy
            if is_fresh_db:
                self._write_batch_fast(db, batch_df)
            else:
                self._write_batch_safe(db, batch_df)

            # print(f"Filter 3: Wrote batch of {len(buffer)} items.")

        except Exception as e:
            db.roll_back()
            print(f"Filter 3 Batch Error: {e}")

    def _write_batch_fast(self, db, data):
        csv_buffer = StringIO()
        data.to_csv(csv_buffer, index=False, header=True)
        csv_buffer.seek(0)

        db.copy_expert(
            """COPY market_data(symbol, timestamp, open, high, low, close, volume)
               FROM STDIN WITH CSV HEADER""",
            csv_buffer
        )
        db.commit()

    def _write_batch_safe(self, db, data):
        csv_buffer = StringIO()
        data.to_csv(csv_buffer, index=False, header=True)
        csv_buffer.seek(0)

        # 1. Clear Staging
        db.execute("TRUNCATE TABLE market_data_staging")

        # 2. Copy to Staging
        db.copy_expert("""
            COPY market_data_staging(symbol, timestamp, open, high, low, close, volume)
            FROM STDIN WITH CSV HEADER
        """, csv_buffer)

        # 3. Insert from Staging to Main with Conflict Handling
        db.execute("""
            INSERT INTO market_data(symbol, timestamp, open, high, low, close, volume)
            SELECT symbol, timestamp, open, high, low, close, volume
            FROM market_data_staging
            ON CONFLICT (symbol, timestamp) DO NOTHING;
        """)

        db.commit()

    def _write_daily_data(self, db, data):
        df = pd.concat(data, ignore_index=True)

        symbols_in_batch = df['symbol'].unique().tolist()
        
        # Delete outdated data for symbols in the db and insert fresh data
        placeholders = ','.join(f"'{s}'" for s in symbols_in_batch)
        db.execute(f"DELETE FROM daily_data WHERE symbol IN ({placeholders})")
        db.commit()

        csv_buffer = StringIO()
        df.to_csv(csv_buffer, index=False, header=True)
        csv_buffer.seek(0)

        db.copy_expert(
            """COPY daily_data(symbol, timestamp, last_price, volume_24h, high_24h, low_24h)
               FROM STDIN WITH CSV HEADER""",
            csv_buffer
        )
        db.commit()
