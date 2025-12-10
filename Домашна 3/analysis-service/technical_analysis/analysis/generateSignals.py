from analysis.indicators import *

def generate_signals(df):
    row = df.iloc[-1]
    signals = []

    if row["RSI"] < 30:
        signals.append("BUY (RSI oversold)")
    elif row["RSI"] > 70:
        signals.append("SELL (RSI overbought)")
    else:
        signals.append("HOLD (RSI neutral)")

    if row["MACD"] > row["MACD_signal"]:
        signals.append("BUY (MACD bullish)")
    elif row["MACD"] < row["MACD_signal"]:
        signals.append("SELL (MACD bearish)")
    else:
        signals.append("HOLD (MACD neutral)")

    if row["STOCH_K"] < 20:
        signals.append("BUY (Stoch oversold)")
    elif row["STOCH_K"] > 80:
        signals.append("SELL (Stoch overbought)")
    else:
        signals.append("HOLD (Stoch neutral)")

    if row["close"] > row["EMA_20"]:
        signals.append("BUY (Price > EMA20)")
    elif row["close"] < row["EMA_20"]:
        signals.append("SELL (Price < EMA20)")
    else:
        signals.append("HOLD (Price = EMA20)")

    if row["close"] < row["BB_lower"]:
        signals.append("BUY (Below BB)")
    elif row["close"] > row["BB_upper"]:
        signals.append("SELL (Above BB)")
    else:
        signals.append("HOLD (Inside BB)")

    return signals


def calculate_signals(df):
    df = add_indicators(df)
    return generate_signals(df)
