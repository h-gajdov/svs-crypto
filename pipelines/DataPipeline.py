from filters.Filter import *
from filters.Filter1 import *
from filters.Filter2 import *
from filters.Filter3 import *
import time

class Pipeline:
    def __init__(self):
        self.filters = []
        self.filters.append(Top1000CoinsFilter())
        self.filters.append(GetDataForCoinsFilter())
        self.filters.append(FillDatabaseFilter())

    def execute(self, data):
        start_time = time.time()
        for filter in self.filters:
            data = filter.process(data)

        end_time = time.time()
        elapsed_time = end_time - start_time
        print(f"All filters done {elapsed_time:.4f}")
        return data