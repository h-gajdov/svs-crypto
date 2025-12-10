import requests
from datetime import datetime
from bs4 import BeautifulSoup
from selenium.webdriver import Chrome
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By

headers = {
    'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) '
                    'AppleWebKit/537.36 (KHTML, like Gecko) '
                    'Chrome/140.0.0.0 Safari/537.36',
}

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

if __name__ == '__main__':
    print(get_address_count('USDC'))
    print(get_transactions_count('USDC'))
    print(get_whale_movements())