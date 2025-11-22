import os

from filters.Filter import *
from filters.Filter1 import *
from filters.Filter2 import *
from filters.Filter3 import *
import queue
import threading
import time
from dotenv import load_dotenv
load_dotenv()

class Pipeline:
    def __init__(self):
        self.filters = []
        self.filters.append(Top1000CoinsFilter())
        self.filters.append(GetDataForCoinsFilter())
        self.filters.append(FillDatabaseFilter())
        self.shared_queue = queue.Queue(maxsize=int(os.getenv('BATCH_SIZE', 50)))

    def execute(self, data):
        start_time = time.time()
        symbols_data = self.filters[0].process(data)
        print(f"Filter 1 finished. Found {len(symbols_data)} symbols.")

        # 2. Start Filter 3 (Consumer) in a separate thread
        consumer_thread = threading.Thread(
            target=self.filters[2].consume_stream,
            args=(self.shared_queue,)
        )
        consumer_thread.start()

        # 3. Run Filter 2 (Producer) in the main thread
        # It will feed the queue
        self.filters[1].process_stream(symbols_data, self.shared_queue)

        # 4. Wait for Filter 3 to finish (it will finish when queue is empty + sentinel received)
        consumer_thread.join()

        end_time = time.time()
        elapsed_time = end_time - start_time
        print(f"All filters done {elapsed_time:.4f}")
        return data