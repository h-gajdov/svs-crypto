from cachetools import TTLCache, cached
from cache.caches import TECHNICAL_ANALYSIS_CACHE
from technicalAnalysis.analysis import analyze_symbol

class TechnicalAnalysisService:
    """Handles technical and ML-based analysis."""
    @staticmethod
    @cached(TECHNICAL_ANALYSIS_CACHE)
    def analyze_symbol(symbol: str):
        """
        Performs technical analysis for a symbol.

        Args:
            symbol: Cryptocurrency symbol.

        Returns:
            dict: Technical indicators.
        """
        return analyze_symbol(symbol)