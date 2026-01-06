import numpy as np

def safe_float(value) -> float:
    """
    Safely converts a value to float.

    Args:
        value: Any numeric-like value.

    Returns:
        float: Converted value or 0.0 on failure.
    """
    try:
        return float(value)
    except (TypeError, ValueError):
        return 0.0


def log_normalize(value, scale: float = 1.0) -> float:
    """
    Applies logarithmic normalization.

    Args:
        value: Raw metric value.
        scale: Expected maximum scale.

    Returns:
        float: Normalized value in range [0, 1].
    """
    value = max(safe_float(value), 0)
    return np.log1p(value) / np.log1p(scale)


def inverse_log_normalize(value, scale: float = 1.0) -> float:
    """
    Inverse logarithmic normalization (lower raw is better).

    Args:
        value: Raw metric value.
        scale: Expected maximum scale.

    Returns:
        float: Normalized value in range [0, 1].
    """
    value = max(safe_float(value), 0)
    return 1 - (np.log1p(value) / np.log1p(scale))


def normalize_exchange_flow(data, scale: float = 1e9) -> float:
    """
    Normalizes exchange netflow data.

    Args:
        data: Exchange flow response payload.
        scale: Expected maximum scale.

    Returns:
        float: Normalized exchange flow score.
    """
    try:
        netflow = safe_float(data["data"][0]["netflow"])
        return log_normalize(-netflow, scale)
    except (KeyError, IndexError, TypeError):
        return 0.5