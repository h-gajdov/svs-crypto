from analysis.generateSignals import *
from analysis.getSymbols import *
from analysis.indicators import *
from analysis.timeframeAnalysis import *

def analyze_all_cryptos():
    symbols = get_all_symbols()
    results = {}

    for symbol in symbols:
        print(f"\n==== Analyzing {symbol} ====")

        df = load_data(symbol)
        df = add_indicators(df)
        print(df)

        signals_1D = calculate_signals(df)
        signals_1W = timeframe_analysis(df, "1W")
        signals_1M = timeframe_analysis(df, "1M")

        results[symbol] = {
            "1D": signals_1D,
            "1W": signals_1W,
            "1M": signals_1M
        }

        print("1D:", signals_1D)
        print("1W:", signals_1W)
        print("1M:", signals_1M)

    return results


results = analyze_all_cryptos()
