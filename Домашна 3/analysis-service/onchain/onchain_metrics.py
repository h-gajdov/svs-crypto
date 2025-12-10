import requests
from datetime import datetime

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

if __name__ == '__main__':
    print(get_address_count('USDC'))
    print(get_transactions_count('USDC'))