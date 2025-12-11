import ta
import numpy as np

def add_indicators(df):
    if len(df) >= 30:
        df["RSI"] = ta.momentum.RSIIndicator(df["close"]).rsi()
        macd = ta.trend.MACD(df["close"])
        df["MACD"] = macd.macd()
        df["MACD_signal"] = macd.macd_signal()
        stoch = ta.momentum.StochasticOscillator(df["high"], df["low"], df["close"])
        df["STOCH_K"] = stoch.stoch()
        df["STOCH_D"] = stoch.stoch_signal()
        df["ADX"] = ta.trend.ADXIndicator(df["high"], df["low"], df["close"]).adx()
        df["CCI"] = ta.trend.CCIIndicator(df["high"], df["low"], df["close"]).cci()
        df["SMA_20"] = df["close"].rolling(20).mean()
        df["EMA_20"] = df["close"].ewm(span=20).mean()
        df["WMA_20"] = ta.trend.WMAIndicator(df["close"], window=20).wma()
        bb = ta.volatility.BollingerBands(df["close"])
        df["BB_middle"] = bb.bollinger_mavg()
        df["BB_upper"] = bb.bollinger_hband()
        df["BB_lower"] = bb.bollinger_lband()
        df["VMA_20"] = df["volume"].rolling(20).mean()
    else:
        df["RSI"] = np.nan
        df["MACD"] = np.nan
        df["MACD_signal"] =  np.nan
        df["STOCH_K"] =  np.nan
        df["STOCH_D"] =  np.nan
        df["ADX"] = np.nan
        df["CCI"] = np.nan
        df["SMA_20"] = np.nan
        df["EMA_20"] = np.nan
        df["WMA_20"] = np.nan
        df["BB_middle"] = np.nan
        df["BB_upper"] = np.nan
        df["BB_lower"] = np.nan
        df["VMA_20"] = np.nan

    return df

