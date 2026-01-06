from cachetools import TTLCache, cached
from cache.caches import NEWS_CACHE, NEWS_SENTIMENT_CACHE
from sentiment.sentiment_analysis import get_sentiment, estimate_sentiment
from onchain.onchain_metrics import get_coin_id

class SentimentService:
    """Handles news retrieval and sentiment estimation."""

    @staticmethod
    @cached(NEWS_CACHE)
    def get_news(symbol: str):
        """
        Fetches news for a given crypto symbol.

        Args:
            symbol: Cryptocurrency symbol (e.g. BTC).

        Returns:
            list: News articles.
        """
        coin_id = get_coin_id(symbol)["coin_id"]
        return get_sentiment(f"{symbol},{coin_id}")

    @staticmethod
    @cached(NEWS_SENTIMENT_CACHE)
    def get_sentiment_score(symbol: str):
        """
        Estimates sentiment score from news.

        Args:
            symbol: Cryptocurrency symbol.

        Returns:
            tuple(float, str): Probability score and sentiment label.
        """
        news = SentimentService.get_news(symbol)
        return estimate_sentiment(news)