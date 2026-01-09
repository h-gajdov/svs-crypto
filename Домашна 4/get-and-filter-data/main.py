from pipelines.DataPipeline import *
import time


timer_start = time.time()

pipeline = Pipeline()
pipeline.execute([])
timer_end = time.time()

print(f"Time passed: {timer_end - timer_start} seconds")
