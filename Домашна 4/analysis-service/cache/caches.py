"""
Central cache configuration for the Crypto Analytics API.

This module defines all TTLCache instances used across services..
"""

from cachetools import TTLCache

NEWS_CACHE = TTLCache(
    maxsize=300,
    ttl=1800  # 30 minutes
)

NEWS_SENTIMENT_CACHE = TTLCache(
    maxsize=300,
    ttl=1800  # 30 minutes
)

METRICS_CACHE = TTLCache(
    maxsize=500,
    ttl=600  # 10 minutes
)

ONCHAIN_INDICATOR_CACHE = TTLCache(
    maxsize=200,
    ttl=300  # 5 minutes
)

EXCHANGE_FLOW_CACHE = TTLCache(
    maxsize=100,
    ttl=120  # 2 minutes
)

WHALE_CACHE = TTLCache(
    maxsize=100,
    ttl=120  # 2 minutes
)

TECHNICAL_ANALYSIS_CACHE = TTLCache(
    maxsize=300,
    ttl=300  # 5 minutes
)