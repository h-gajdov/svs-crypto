from technicalAnalysis.generateSignals import *
from technicalAnalysis.getSymbols import *
from technicalAnalysis.indicators import *
from technicalAnalysis.timeframeAnalysis import *

def analyze_all_cryptos():
    symbols = get_all_symbols()
    results = {}

    for symbol in symbols:
        df = load_data(symbol)
        df = add_indicators(df)

        signals_1D = calculate_signals(df)
        signals_1W = timeframe_analysis(df, "1W")
        signals_1M = timeframe_analysis(df, "1M")

        results[symbol] = {
            "1D": signals_1D,
            "1W": signals_1W,
            "1M": signals_1M
        }

    return results

def analyze_symbol(symbol):
    df = load_data(symbol)
    df = add_indicators(df)

    return {
        "symbol": symbol,
        "1D": calculate_signals(df),
        "1W": timeframe_analysis(df, "1W"),
        "1M": timeframe_analysis(df, "1M")
    }