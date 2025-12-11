import requests
from datetime import datetime
from bs4 import BeautifulSoup

def fetch_extended_data():
    try:
        chains = requests.get("https://api.llama.fi/chains").json()
        tvl_map = {}
        for c in chains:
            symbol = c.get("tokenSymbol")
            tvl = {
                "tvl": c.get("tvl"),
                "coin_id": c.get("gecko_id")
            }
            if symbol and tvl is not None:
                tvl_map[symbol.upper()] = tvl
        return tvl_map
    except Exception as e:
        print(f"Error fetching chain TVLs: {e}")
        return {}

extended_coin_data = fetch_extended_data()

def get_coin_id(symbol):
    return {
        "asset": symbol,
        "coin_id": extended_coin_data.get(symbol)['coin_id']
    }

def get_tvl(symbol):
    if symbol not in extended_coin_data: return 0
    else: {
        "asset": symbol,
        "tvl": extended_coin_data.get(symbol).get("tvl")
    }

def get_nvt(symbol):
    try:
        if symbol not in extended_coin_data: return 0
        coin_id = extended_coin_data.get(symbol)['coin_id']
        if not coin_id:
            return 0
        url = f"https://api.coingecko.com/api/v3/coins/{coin_id}"
        r = requests.get(url)
        data = r.json()
        market_cap = data["market_data"]["market_cap"]["usd"]
        volume = data["market_data"]["total_volume"]["usd"]
        return {
            "asset": symbol,
            "nvt":market_cap / volume if volume else 0
            }
    except Exception as e:
        print(f"Error fetching NVT for {symbol}: {e}")
        return None

def get_coinmetrics_data(symbol, daysBefore, metrics):
    url = f'https://community-api.coinmetrics.io/v4/timeseries/asset-metrics?assets={symbol.lower()}&metrics={metrics}'
    response = requests.get(url)
    data = response.json()
    if "error" in data:
        raise RuntimeError(f"API returned an error: {data['error']}")

    return data['data'][-daysBefore:]

def parse_coinmetrics_data(data):
    for entry in data:
        entry['asset'] = entry['asset'].upper()
        time_str = entry['time'] 
        time_str = time_str.split('.')[0] + "Z"
        dt = datetime.strptime(time_str, "%Y-%m-%dT%H:%M:%SZ")
        entry['timestamp'] = dt.timestamp()
    return data

def get_address_count(symbol, daysBefore=1):   
    result = get_coinmetrics_data(symbol, daysBefore, 'AdrActCnt')
    return parse_coinmetrics_data(result)

def get_transactions_count(symbol, daysBefore=1):
    result = get_coinmetrics_data(symbol, daysBefore, 'TxCnt')
    return parse_coinmetrics_data(result)

def get_whale_movements(number_of_alerts=5):
    url = 'https://whale-alert.io/alerts.json?range=last_30_days'
    response = requests.get(url)
    data = response.json()

    data = data[:number_of_alerts]
    for entry in data: 
        #remove unnecessary keys
        entry.pop('id', None)
        entry.pop('emoticons', None)
        entry['time'] = datetime.fromtimestamp(entry['timestamp']).strftime('%Y-%m-%dT%H:%M:%S.') + '000000000Z'
    return data

def get_hash_rate(symbol, daysBefore=1):
    result = get_coinmetrics_data(symbol, daysBefore, "HashRate")
    result = parse_coinmetrics_data(result)
    for entry in result:
        entry['HashRate'] = float(entry['HashRate'])
    return result

def get_mvrv_ratio(symbol, daysBefore=1):
    result = get_coinmetrics_data(symbol, daysBefore, "CapMVRVCur")
    result = parse_coinmetrics_data(result)
    for entry in result:
        entry['CapMVRVCur'] = float(entry['CapMVRVCur'])
    return result
    
if __name__ == '__main__':
    print(get_address_count('BTC'))
    print(get_transactions_count('USDC'))
    print(get_whale_movements())
    print(get_nvt("BTC"))
    print(get_tvl("BTC"))
    print(get_coin_id("BTC"))
    print(get_hash_rate('BTC'))
    print(get_mvrv_ratio('USDC'))