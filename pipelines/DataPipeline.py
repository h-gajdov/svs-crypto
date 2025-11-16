from filters.Filter import *
from filters.Filter1 import *
from filters.Filter2 import *
from filters.Filter3 import *

class Pipeline:
    def __init__(self):
        self.filters = []
        self.filters.append(Top1000CoinsFilter())
        self.filters.append(GetDataForCoinsFilter())
        self.filters.append(FillDatabaseFilter())

    def execute(self, data):
        for filter in self.filters:
            data = filter.process(data)
        return data