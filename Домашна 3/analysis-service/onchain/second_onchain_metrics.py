import requests
import time

symbol_to_id = {
    "BTC": "bitcoin",
    "ETH": "ethereum",
    "SOL": "solana",
    "DOGE": "dogecoin",
    "MATIC": "matic-network",
    "USDC": "usd-coin",
    "BNB": "binancecoin"
}

def fetch_chain_tvls():
    try:
        chains = requests.get("https://api.llama.fi/chains").json()
        tvl_map = {}
        for c in chains:
            symbol = c.get("symbol")
            tvl = c.get("tvl")
            if symbol and tvl is not None:
                tvl_map[symbol.upper()] = tvl
        return tvl_map
    except Exception as e:
        print(f"Error fetching chain TVLs: {e}")
        return {}

def fetch_nvt(symbol):
    try:
        coin_id = symbol_to_id.get(symbol)
        if not coin_id:
            return None
        url = f"https://api.coingecko.com/api/v3/coins/{coin_id}"
        r = requests.get(url)
        data = r.json()
        market_cap = data["market_data"]["market_cap"]["usd"]
        volume = data["market_data"]["total_volume"]["usd"]
        return market_cap / volume if volume else None
    except Exception as e:
        print(f"Error fetching NVT for {symbol}: {e}")
        return None

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

    chain_tvls = fetch_chain_tvls()

    for s in symbols:
        results[s]["tvl"] = chain_tvls.get(s)

        results[s]["nvt"] = fetch_nvt(s)
        time.sleep(0.1) 

        results[s]["mvrv"] = fetch_mvrv(s)

        results[s]["hashrate"] = fetch_hashrate(s)

    return list(results.values())

if __name__ == "__main__":
    coins = ["BTC", "ETH", "SOL", "DOGE", "MATIC", "USDC", "BNB"]
    all_metrics = get_all_metrics_free(coins)
    for m in all_metrics:
        print(m)
