from cachetools import TTLCache, cached
from cache.caches import METRICS_CACHE, EXCHANGE_FLOW_CACHE, ONCHAIN_INDICATOR_CACHE
from onchain.onchain_metrics import get_all_metrics, get_exchange_flow
from services.SentimentService import SentimentService
from utils.normalization import (
    log_normalize,
    inverse_log_normalize,
    normalize_exchange_flow
)

class OnChainService:
    """Handles all on-chain metrics and indicators."""

    @staticmethod
    @cached(METRICS_CACHE)
    def get_metrics(symbol: str):
        """
        Retrieves all on-chain metrics for a symbol.

        Args:
            symbol: Cryptocurrency symbol.

        Returns:
            dict: On-chain metrics.
        """
        return get_all_metrics(symbol)

    @staticmethod
    @cached(EXCHANGE_FLOW_CACHE)
    def get_exchange_flow(symbol: str):
        """
        Retrieves exchange flow data.

        Args:
            symbol: Cryptocurrency symbol.

        Returns:
            dict: Exchange flow data.
        """
        return get_exchange_flow(symbol)

    @staticmethod
    @cached(ONCHAIN_INDICATOR_CACHE)
    def get_combined_indicator(symbol: str):
        """
        Combines on-chain metrics with sentiment into a single signal.

        Args:
            symbol: Cryptocurrency symbol.

        Returns:
            dict: Combined score and trading signal.
        """
        probability, label = SentimentService.get_sentiment_score(symbol)
        sentiment_score = probability if label == "positive" else -probability

        metrics = OnChainService.get_metrics(symbol)

        addr = log_normalize(metrics.get("AdrActCnt"), 1_000_000)
        tx = log_normalize(metrics.get("TxCnt"), 1_000_000)
        hash_rate = log_normalize(metrics.get("HashRate"), 2_000_000_000)
        tvl = log_normalize(metrics.get("tvl"), 50_000_000_000)
        nvt = inverse_log_normalize(metrics.get("nvt"), 100)
        mvrv = inverse_log_normalize(metrics.get("CapMVRVCur"), 5)
        exch = normalize_exchange_flow(metrics.get("exchange_flow"), 20_000_000_000)

        weights = {
            "addr": 0.10,
            "tx": 0.05,
            "hash": 0.20,
            "tvl": 0.05,
            "nvt": 0.25,
            "mvrv": 0.20,
            "exch": 0.15
        }

        onchain_score = (
                addr * weights["addr"] +
                tx * weights["tx"] +
                hash_rate * weights["hash"] +
                tvl * weights["tvl"] +
                nvt * weights["nvt"] +
                mvrv * weights["mvrv"] +
                exch * weights["exch"]
        )

        final_score = 0.75 * onchain_score + 0.25 * sentiment_score
        signal = "BUY" if final_score > 0.75 else "NEUTRAL" if final_score > 0.45 else "SELL"

        return {
            "symbol": symbol,
            "onchain_score": float(onchain_score),
            "combined_score": float(final_score),
            "signal": signal
        }
