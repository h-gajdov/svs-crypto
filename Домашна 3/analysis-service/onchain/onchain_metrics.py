import requests
from datetime import datetime

def get_address_count(symbol, daysBefore=1):
    url = f'https://community-api.coinmetrics.io/v4/timeseries/asset-metrics?assets={symbol.lower()}&metrics=AdrActCnt'
    response = requests.get(url)
    data = response.json()
    if "error" in data:
        raise RuntimeError(f"API returned an error: {data['error']}")
    
    result = data["data"][-daysBefore:]
    for entry in result:
        entry['asset'] = entry['asset'].upper()
        time_str = entry['time'] 
        time_str = time_str.split('.')[0] + "Z"
        dt = datetime.strptime(time_str, "%Y-%m-%dT%H:%M:%SZ")
        entry['timestamp'] = dt.timestamp()
    return result

if __name__ == '__main__':
    print(get_address_count('BTC'))