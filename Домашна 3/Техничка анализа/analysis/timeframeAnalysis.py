from analysis.generateSignals import *

def timeframe_analysis(df, timeframe):
    df_tf = df.resample(timeframe).agg({
        "open": "first",
        "high": "max",
        "low": "min",
        "close": "last",
        "volume": "sum"
    }).dropna()

    df_tf = add_indicators(df_tf)
    return generate_signals(df_tf)
