from technicalAnalysis.strategies import TechnicalAnalyzer


def timeframe_analysis(df, timeframe):
    df_tf = df.resample(timeframe).agg({
        "open": "first",
        "high": "max",
        "low": "min",
        "close": "last",
        "volume": "sum"
    }).dropna()

    analyzer = TechnicalAnalyzer()
    return analyzer.analyze(df_tf)
