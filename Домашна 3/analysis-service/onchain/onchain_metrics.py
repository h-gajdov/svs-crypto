import requests
from datetime import datetime
from bs4 import BeautifulSoup
from selenium.webdriver import Chrome
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.options import Options

chrome_options = Options()
chrome_options.add_argument("--headless=new")
chrome_options.add_argument("user-agent=Mozilla/5.0 ...")
driver = Chrome(options=chrome_options)
print("BROWSER STARTED...")

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
        "coin_id": extended_coin_data.get(symbol, {'coin_id': ''})['coin_id']
    }

def get_tvl(symbol):
    return {
        "asset": symbol,
        "tvl": extended_coin_data.get(symbol).get("tvl") if symbol in extended_coin_data else 0
    }

def get_nvt(symbol):
    try:
        if symbol not in extended_coin_data:
            raise Exception('Coin id not found')
        
        coin_id = extended_coin_data.get(symbol)['coin_id']
    
        if not coin_id:
            raise Exception('Coin id not found')
        
        url = f"https://api.coingecko.com/api/v3/coins/{coin_id}"
        r = requests.get(url)
        data = r.json()
        market_cap = data["market_data"]["market_cap"]["usd"]
        volume = data["market_data"]["total_volume"]["usd"]
        return {
            "asset": symbol,
            "nvt": market_cap / volume if volume else 0
            }
    except Exception as e:
        print(f"Error fetching NVT for {symbol}: {e}")
        return {
            'asset': symbol,
            'nvt': 0
        }

def get_coinmetrics_data(symbol, daysBefore, metrics):
    url = f'https://community-api.coinmetrics.io/v4/timeseries/asset-metrics?assets={symbol.lower()}&metrics={metrics}'
    response = requests.get(url)
    data = response.json()
    if "error" in data:
        now_str = datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%fZ")
        return [{"asset": symbol, **{m: 0 for m in metrics.split(",")}, "time": now_str} for _ in range(daysBefore)] #put 0 for every metric

    return data['data'][-daysBefore:]

def parse_coinmetrics_data(data, metrics):
    for entry in data:
        entry['asset'] = entry['asset'].upper()

        time_str = entry.get('time')
        if time_str:
            time_str = entry['time']
            time_str = time_str.split('.')[0] + "Z"
            dt = datetime.strptime(time_str, "%Y-%m-%dT%H:%M:%SZ")
            entry['timestamp'] = dt.timestamp()
        else:
            entry['timestamp'] = None

        for metric in metrics.split(','):
            value = entry.get(metric)

            try:
                entry[metric] = float(value) if value is not None else 0.0
            except (ValueError, TypeError):
                entry[metric] = 0.0

    return data

def get_address_count(symbol, daysBefore=1):   
    result = get_coinmetrics_data(symbol, daysBefore, 'AdrActCnt')
    return parse_coinmetrics_data(result, 'AdrActCnt')

def get_transactions_count(symbol, daysBefore=1):
    result = get_coinmetrics_data(symbol, daysBefore, 'TxCnt')
    return parse_coinmetrics_data(result, 'TxCnt')

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
    result = parse_coinmetrics_data(result, "HashRate")
    return result

def get_mvrv_ratio(symbol, daysBefore=1):
    result = get_coinmetrics_data(symbol, daysBefore, "CapMVRVCur")
    result = parse_coinmetrics_data(result, "CapMVRVCur")
    return result

def parse_number(s):
    s = s.replace("+", "").replace("$", "").strip()
    multipliers = {
        "K": 1_000,
        "M": 1_000_000,
        "B": 1_000_000_000,
        "T": 1_000_000_000_000,
        "Q": 1_000_000_000_000_000,
        "m": 1e-3,
        "μ": 1e-6
    }

    if s[-1] in multipliers:
        return float(s[:-1]) * multipliers[s[-1]]
    else:
        return float(s)

def get_exchange_flow(symbol):
    url = f'https://www.coinglass.com/currencies/{symbol}?type=spot'
    driver.get(url)
    element = WebDriverWait(driver, 30).until(
        EC.presence_of_element_located((By.XPATH, "//button[text()='Spot Flows']"))
    )
    driver.execute_script("arguments[0].click();", element)

    def rows_loaded(driver):
        rows = driver.find_elements(By.CSS_SELECTOR, '.cg-style-a2wtpa tbody tr')
        return rows if len(rows) >= 48 and rows[1].text else False

    WebDriverWait(driver, 30).until(rows_loaded)

    soup = BeautifulSoup(driver.page_source, 'html.parser')
    rows = soup.select('.cg-style-a2wtpa tbody tr')[1:23] #30 is maxmimum amount of seconds to wait else it throws TimeoutException
    result = []
    for row in rows:
        cells = row.find_all('td')
        netflow = parse_number(cells[3].text)
        timeframe = cells[0].text
        result.append({"timeframe": timeframe, 'netflow': netflow})

    return {
        'asset': symbol,
        'data': result
    }

def get_all_metrics(symbol):
    metrics = 'AdrActCnt,TxCnt,HashRate,CapMVRVCur'
    
    result = get_coinmetrics_data(symbol, 1, metrics)
    result = parse_coinmetrics_data(result, metrics)[0]
    result['nvt'] = get_nvt(symbol)['nvt']
    result['tvl'] = get_tvl(symbol)['tvl']
    result['coin_id'] = get_coin_id(symbol)['coin_id']
    result['exchange_flow'] = get_exchange_flow(symbol)

    return result

if __name__ == '__main__':
    print(get_address_count('BTC'))
    print(get_transactions_count('USDC'))
    print(get_whale_movements())
    print(get_nvt("SHIB"))
    print(get_tvl("BTC"))
    print(get_coin_id("BTC"))
    print(get_hash_rate('BTC'))
    print(get_mvrv_ratio('USDC'))
    print(get_exchange_flow('XRP'))
    print(get_all_metrics('BTC'))