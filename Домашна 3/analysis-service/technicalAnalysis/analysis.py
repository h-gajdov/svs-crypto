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

def get_symbol_indicators(symbol):
    df = load_data(symbol)
    df = add_indicators(df)

    if df.empty:
        return {}

    def get_last_value(column):
        if column in df.columns:
            col = df[column].dropna()
            return float(col.iloc[-1]) if not col.empty else None
        return None

    return {
        "symbol": symbol,
        "close": get_last_value("close"),
        "RSI": get_last_value("RSI"),
        "MACD": get_last_value("MACD"),
        "MACD_signal": get_last_value("MACD_signal"),
        "STOCH_K": get_last_value("STOCH_K"),
        "STOCH_D": get_last_value("STOCH_D"),
        "ADX": get_last_value("ADX"),
        "CCI": get_last_value("CCI"),
        "SMA_20": get_last_value("SMA_20"),
        "EMA_20": get_last_value("EMA_20"),
        "WMA_20": get_last_value("WMA_20"),
        "BB_middle": get_last_value("BB_middle"),
        "BB_upper": get_last_value("BB_upper"),
        "BB_lower": get_last_value("BB_lower"),
        "VMA_20": get_last_value("VMA_20")
    }


