import requests
import time

from bs4 import BeautifulSoup

def fetch_mvrv(symbol):
    return None

def fetch_hashrate(symbol):
    try:
        if symbol == "BTC":
            r = requests.get("https://api.blockchain.info/charts/hash-rate?timespan=7days&format=json")
            data = r.json()
            return data["values"][-1]["y"] if "values" in data else None
        elif symbol == "DOGE":
            r = requests.get("https://sochain.com/api/v2/get_info/DOGE")
            return float(r.json()["data"]["network_hashrate"])
        else:
            return None
    except Exception as e:
        print(f"Error fetching hashrate for {symbol}: {e}")
        return None

def get_all_metrics_free(symbols):
    symbols = [s.upper() for s in symbols]
    results = {s: {"symbol": s, "tvl": None, "nvt": None, "mvrv": None, "hashrate": None} for s in symbols}

    for s in symbols:
        results[s]["mvrv"] = fetch_mvrv(s)

        results[s]["hashrate"] = fetch_hashrate(s)

    return list(results.values())

if __name__ == "__main__":
    coins = ["BTC", "ETH", "SOL", "DOGE", "MATIC", "USDC", "BNB", "XRP"]
    
    all_metrics = get_all_metrics_free(coins)
    for m in all_metrics:
        print(m)
