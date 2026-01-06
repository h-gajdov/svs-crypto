from fastapi import FastAPI

from lstm.lstm import predict_price, PredictionResponse
from onchain.onchain_metrics import get_whale_movements
from services import (
    SentimentService,
    OnChainService,
    TechnicalAnalysisService
)
from technicalAnalysis.analysis import analyze_all_cryptos, get_symbol_indicators

app = FastAPI(title="Crypto Analytics API")

@app.get("/check-connection")
def health_check():
    """Health check endpoint."""
    return {"status": "FastAPI is running"}

@app.get("/technicalAnalysis/all")
def technical_analysis_all():
    """Returns technical analysis for all supported cryptocurrencies."""
    return analyze_all_cryptos()

@app.get("/analysis/{symbol}")
def technical_analysis(symbol: str):
    """Returns technical analysis for a specific symbol."""
    return TechnicalAnalysisService.analyze_symbol(symbol)

@app.get("/identificators/{symbol}")
def identifiers(symbol: str):
    """Returns symbol identifiers."""
    return get_symbol_indicators(symbol)

@app.get("/get-news/{symbol}")
def get_news(symbol: str):
    """Returns cached news for a symbol."""
    return {
        "symbol": symbol,
        "news": SentimentService.get_news(symbol)
    }

@app.get("/estimate-news/{symbol}")
def estimate_news(symbol: str):
    """Returns news sentiment estimation."""
    news = SentimentService.get_news(symbol)
    probability, sentiment = SentimentService.get_sentiment_score(symbol)
    return {
        "symbol": symbol,
        "news": news,
        "probability": probability,
        "sentiment": sentiment
    }

@app.get("/whale-movements")
def whale_movements(alerts: int = 5):
    """Returns recent whale movements."""
    return get_whale_movements(alerts)

@app.get("/metrics/{symbol}")
def metrics(symbol: str):
    """Returns all cached on-chain metrics."""
    return OnChainService.get_metrics(symbol)

@app.get("/exchange-flow/{symbol}")
def exchange_flow(symbol):
    return OnChainService.get_exchange_flow(symbol)

@app.get("/get-indicator-onchain/{symbol}")
def combined_indicator(symbol: str):
    """Returns combined on-chain & sentiment indicator."""
    return OnChainService.get_combined_indicator(symbol)

@app.get("/api/predict/{symbol}", response_model=PredictionResponse)
def predict(symbol: str):
    """Predicts future price using LSTM."""
    return predict_price(symbol)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)