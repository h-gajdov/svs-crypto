from abc import ABC, abstractmethod
import pandas as pd
import ta


class IndicatorStrategy(ABC):
    @abstractmethod
    def calculate(self, df: pd.DataFrame) -> pd.DataFrame:
        pass

    @abstractmethod
    def interpret_signal(self, row: pd.Series) -> str:
        pass


class RsiStrategy(IndicatorStrategy):
    def __init__(self, window=14, oversold=30, overbought=70):
        self.window = window
        self.oversold = oversold
        self.overbought = overbought

    def calculate(self, df: pd.DataFrame) -> pd.DataFrame:
        df['RSI'] = ta.momentum.RSIIndicator(df['close'], window=self.window).rsi()
        return df

    def interpret_signal(self, row: pd.Series) -> str:
        rsi_val = row.get("RSI")
        if pd.isna(rsi_val):
            return "HOLD (Insufficient Data)"

        if rsi_val < self.oversold:
            return "BUY (RSI oversold)"
        elif rsi_val > self.overbought:
            return "SELL (RSI overbought)"
        return "HOLD (RSI neutral)"


class MacdStrategy(IndicatorStrategy):
    def __init__(self, window_slow=26, window_fast=12, window_sign=9):
        self.window_slow = window_slow
        self.window_fast = window_fast
        self.window_sign = window_sign

    def calculate(self, df: pd.DataFrame) -> pd.DataFrame:
        macd = ta.trend.MACD(close=df["close"])
        df["MACD"] = macd.macd()
        df["MACD_signal"] = macd.macd_signal()
        return df

    def interpret_signal(self, row: pd.Series) -> str:
        macd_val = row.get("MACD")
        signal_val = row.get("MACD_signal")

        if pd.isna(macd_val) or pd.isna(signal_val):
            return "HOLD (Insufficient Data)"

        if macd_val > signal_val:
            return "BUY (MACD bullish)"
        elif macd_val < signal_val:
            return "SELL (MACD bearish)"
        else:
            return "HOLD (MACD neutral)"


class SmaStrategy(IndicatorStrategy):
    def __init__(self, window=20):
        self.window = window

    def calculate(self, df: pd.DataFrame) -> pd.DataFrame:
        col_name = f"SMA_{self.window}"
        df[col_name] = df["close"].rolling(window=self.window).mean()
        return df

    def interpret_signal(self, row: pd.Series) -> str:
        col_name = f"SMA_{self.window}"
        price = row["close"]
        sma_val = row.get(col_name)

        if pd.isna(sma_val):
            return "HOLD (Insufficient Data)"

        if price > sma_val:
            return f"BUY (Price > {col_name})"
        elif price < sma_val:
            return f"SELL (Price < {col_name})"
        return "HOLD"


class StochIndicator(IndicatorStrategy):

    def calculate(self, df: pd.DataFrame) -> pd.DataFrame:
        stoch = ta.momentum.StochasticOscillator(df["high"], df["low"], df["close"])
        df["STOCH_K"] = stoch.stoch()
        df["STOCH_D"] = stoch.stoch_signal()
        return df

    def interpret_signal(self, row: pd.Series) -> str:
        val = row.get("STOCH_K")

        if pd.isna(val):
            return "HOLD (Insufficient Data)"

        if val < 20:
            return "BUY (Stoch oversold)"
        elif val > 80:
            return "SELL (Stoch overbought)"
        return "HOLD (Stoch neutral)"


class EmaStrategy(IndicatorStrategy):
    def __init__(self, window=20):
        self.window = window
        self.col_name = f"EMA_{self.window}"

    def calculate(self, df: pd.DataFrame) -> pd.DataFrame:
        df[self.col_name] = df["close"].ewm(span=self.window, adjust=False).mean()
        return df

    def interpret_signal(self, row: pd.Series) -> str:
        price = row["close"]
        ema_val = row.get(self.col_name)

        if pd.isna(ema_val):
            return "HOLD (Insufficient Data)"

        if price > ema_val:
            return f"BUY (Price > {self.col_name})"
        elif price < ema_val:
            return f"SELL (Price < {self.col_name})"
        else:
            return f"HOLD (Price = {self.col_name})"


class BollingerBandsStrategy(IndicatorStrategy):
    def calculate(self, df: pd.DataFrame) -> pd.DataFrame:
        bb = ta.volatility.BollingerBands(df["close"])
        df["BB_upper"] = bb.bollinger_hband()
        df["BB_middle"] = bb.bollinger_mavg()
        df["BB_lower"] = bb.bollinger_lband()
        return df

    def interpret_signal(self, row: pd.Series) -> str:
        price = row["close"]
        lower = row.get("BB_lower")
        upper = row.get("BB_upper")

        if pd.isna(lower) or pd.isna(upper): return "HOLD (Insufficient Data)"

        if price < lower:
            return "BUY (Below Bollinger Lower)"
        elif price > upper:
            return "SELL (Above Bollinger Upper)"
        return "HOLD (Inside BB)"


class CciStrategy(IndicatorStrategy):
    def calculate(self, df: pd.DataFrame) -> pd.DataFrame:
        df["CCI"] = ta.trend.CCIIndicator(df["high"], df["low"], df["close"]).cci()
        return df

    def interpret_signal(self, row: pd.Series) -> str:
        val = row.get("CCI")
        if pd.isna(val): return "HOLD (Insufficient Data)"
        if val < -100:
            return "BUY (CCI oversold)"
        elif val > 100:
            return "SELL (CCI overbought)"
        return "HOLD (CCI neutral)"


class AdxStrategy(IndicatorStrategy):
    def calculate(self, df: pd.DataFrame) -> pd.DataFrame:
        if len(df) < 28:
            df["ADX"] = None
            return df

        try:
            df["ADX"] = ta.trend.ADXIndicator(df["high"], df["low"], df["close"], window=14).adx()
        except Exception:
            df["ADX"] = None
        return df

    def interpret_signal(self, row: pd.Series) -> str:
        val = row.get("ADX")
        if pd.isna(val): return "HOLD (Insufficient Data)"
        if val > 25: return "STRONG TREND (ADX > 25)"
        return "WEAK TREND (ADX < 25)"


class WmaStrategy(IndicatorStrategy):
    def __init__(self, window=20):
        self.window = window
        self.col_name = f"WMA_{self.window}"

    def calculate(self, df: pd.DataFrame) -> pd.DataFrame:
        df[self.col_name] = ta.trend.WMAIndicator(df["close"], window=self.window).wma()
        return df

    def interpret_signal(self, row: pd.Series) -> str:
        price = row["close"]
        wma_val = row.get(self.col_name)
        if pd.isna(wma_val): return "HOLD (Insufficient Data)"

        if price > wma_val:
            return f"BUY (Price > {self.col_name})"
        elif price < wma_val:
            return f"SELL (Price < {self.col_name})"
        return f"HOLD (Price = {self.col_name})"


class IndicatorFactory:
    @staticmethod
    def get_strategy(name: str):
        name = name.upper().strip()

        strategies = {
            "RSI": RsiStrategy(),
            "MACD": MacdStrategy(),
            "SMA": SmaStrategy(window=20),
            "EMA": EmaStrategy(window=20),
            "WMA": WmaStrategy(window=20),
            "STOCH": StochIndicator(),
            "BB": BollingerBandsStrategy(),
            "CCI": CciStrategy(),
            "ADX": AdxStrategy()
        }

        return strategies.get(name)

    @staticmethod
    def get_all_strategies():
        return [
            RsiStrategy(),
            MacdStrategy(),
            SmaStrategy(window=20),
            EmaStrategy(window=20),
            WmaStrategy(window=20),
            StochIndicator(),
            BollingerBandsStrategy(),
            CciStrategy(),
            AdxStrategy()
        ]


class TechnicalAnalyzer:
    def __init__(self, strategies=None):
        self.strategies = strategies if strategies else IndicatorFactory.get_all_strategies()

    def analyze(self, df: pd.DataFrame):
        if df is None or df.empty:
            return []

        for strategy in self.strategies:
            if len(df) < 20:
                continue

            df = strategy.calculate(df)

        last_row = df.iloc[-1]
        results = []
        for strategy in self.strategies:
            signal = strategy.interpret_signal(last_row)
            name = strategy.__class__.__name__.replace('Strategy', '').replace('Indicator', '')
            results.append({"indicator": name, "signal": signal})

        return results
