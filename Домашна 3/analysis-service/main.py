#to run: uvicorn main:app --reload --port 8000
from fastapi import FastAPI

app = FastAPI()

@app.get("/check-connection")
def check_connection():
    return {"status": "FastAPI is running"}

@app.get("/get-news/{symbol}")
def get_news(symbol):
    return {"symbol": symbol, "status": "ok"}