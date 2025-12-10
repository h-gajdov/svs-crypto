from analysis.indicators import *

def generate_signals(df):
    row = df.iloc[-1]
    signals = []

    if row["RSI"] < 30:
        signals.append("BUY (RSI oversold)")
    elif row["RSI"] > 70:
        signals.append("SELL (RSI overbought)")

    if row["MACD"] > row["MACD_signal"]:
        signals.append("BUY (MACD bullish)")
    else:
        signals.append("SELL (MACD bearish)")

    if row["STOCH_K"] < 20:
        signals.append("BUY (Stoch oversold)")
    elif row["STOCH_K"] > 80:
        signals.append("SELL (Stoch overbought)")

    if row["close"] > row["EMA_20"]:
        signals.append("BUY (Price > EMA20)")
    else:
        signals.append("SELL (Price < EMA20)")

    if row["close"] < row["BB_lower"]:
        signals.append("BUY (Below BB)")
    elif row["close"] > row["BB_upper"]:
        signals.append("SELL (Above BB)")

    return signals

def calculate_signals(df):
    df = add_indicators(df)
    return generate_signals(df)

