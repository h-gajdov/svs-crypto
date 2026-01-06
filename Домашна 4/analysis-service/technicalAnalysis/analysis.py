from technicalAnalysis.generateSignals import *
from technicalAnalysis.getSymbols import *
from technicalAnalysis.indicators import *
from technicalAnalysis.timeframeAnalysis import *

from technicalAnalysis.strategies import TechnicalAnalyzer
import pandas as pd

def analyze_all_cryptos():
    symbols = get_all_symbols()
    results = {}

    analyzer = TechnicalAnalyzer()

    for symbol in symbols:
        df = load_data(symbol)

        if df.empty:
            continue

        results[symbol] = {
            "1D": analyzer.analyze(df),
            "1W": timeframe_analysis(df, "1W"),
            "1M": timeframe_analysis(df, "1M")
        }

    return results


def analyze_symbol(symbol):
    df = load_data(symbol)
    analyzer = TechnicalAnalyzer()

    return {
        "symbol": symbol,
        "1D": analyzer.analyze(df),
        "1W": timeframe_analysis(df, "1W"),
        "1M": timeframe_analysis(df, "1M")
    }


def get_symbol_indicators(symbol):
    df = load_data(symbol)
    if df.empty:
        return {}

    analyzer = TechnicalAnalyzer()
    for strategy in analyzer.strategies:
        df = strategy.calculate(df)

    last_row = df.iloc[-1]
    indicator_values = {
        "symbol": symbol,
        "close": float(last_row["close"])
    }
    for col in df.columns:
        if col not in ['open', 'high', 'low', 'close', 'volume']:
            val = last_row[col]
            indicator_values[col] = float(val) if not pd.isna(val) else None

    return indicator_values


