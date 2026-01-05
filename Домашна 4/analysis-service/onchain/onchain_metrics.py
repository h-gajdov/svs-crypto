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
    """Fetch TVL and CoinGecko IDs for all chains."""
    try:
        chains = requests.get("https://api.llama.fi/chains").json()
        tvl_map = {
            c['tokenSymbol'].upper(): {"tvl": c.get("tvl"), "coin_id": c.get("gecko_id")}
            for c in chains if c.get("tokenSymbol") and c.get("tvl") is not None
        }
        return tvl_map
    except Exception as e:
        print(f"Error fetching chain TVLs: {e}")
        return {}

extended_coin_data = fetch_extended_data()

def get_coin_id(symbol: str) -> dict:
    """Return CoinGecko ID for a symbol."""
    return {"asset": symbol, "coin_id": extended_coin_data.get(symbol, {'coin_id': ''})['coin_id']}

def get_tvl(symbol: str) -> dict:
    """Return TVL for a symbol."""
    return {"asset": symbol, "tvl": extended_coin_data.get(symbol, {}).get("tvl", 0)}

def get_coinmetrics_data(symbol: str, days_before: int, metrics: str) -> list:
    """Fetch CoinMetrics timeseries data or return zeros on error."""
    url = f'https://community-api.coinmetrics.io/v4/timeseries/asset-metrics?assets={symbol.lower()}&metrics={metrics}'
    try:
        response = requests.get(url).json()
        if "error" in response:
            now_str = datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%fZ")
            return [{"asset": symbol, **{m: 0 for m in metrics.split(",")}, "time": now_str} for _ in range(days_before)]
        return response['data'][-days_before:]
    except Exception as e:
        print(f"Error fetching CoinMetrics data for {symbol}: {e}")
        now_str = datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%fZ")
        return [{"asset": symbol, **{m: 0 for m in metrics.split(",")}, "time": now_str} for _ in range(days_before)]

def parse_coinmetrics_data(data: list, metrics: str) -> list:
    """Parse CoinMetrics data, normalize timestamps and convert metrics to float."""
    for entry in data:
        entry['asset'] = entry['asset'].upper()
        time_str = entry.get('time')
        if time_str:
            time_str = time_str.split('.')[0] + "Z"
            entry['timestamp'] = datetime.strptime(time_str, "%Y-%m-%dT%H:%M:%SZ").timestamp()
        else:
            entry['timestamp'] = None
        for metric in metrics.split(','):
            try:
                entry[metric] = float(entry.get(metric, 0))
            except (ValueError, TypeError):
                entry[metric] = 0.0
    return data

def get_metric(symbol: str, metric: str, days_before: int = 1) -> list:
    """Fetch and parse a single CoinMetrics metric."""
    result = get_coinmetrics_data(symbol, days_before, metric)
    return parse_coinmetrics_data(result, metric)

def get_nvt(symbol: str) -> dict:
    """Fetch NVT ratio from CoinGecko."""
    try:
        coin_id = extended_coin_data.get(symbol, {}).get('coin_id')
        if not coin_id:
            raise ValueError("Coin id not found")
        data = requests.get(f"https://api.coingecko.com/api/v3/coins/{coin_id}").json()
        market_cap = data["market_data"]["market_cap"]["usd"]
        volume = data["market_data"]["total_volume"]["usd"]
        return {"asset": symbol, "nvt": market_cap / volume if volume else 0}
    except Exception as e:
        print(f"Error fetching NVT for {symbol}: {e}")
        return {"asset": symbol, "nvt": 0}

def get_whale_movements(number_of_alerts: int = 5) -> list:
    """Fetch recent whale movements."""
    try:
        data = requests.get('https://whale-alert.io/alerts.json?range=last_30_days').json()[:number_of_alerts]
        for entry in data:
            entry.pop('id', None)
            entry.pop('emoticons', None)
            entry['time'] = datetime.fromtimestamp(entry['timestamp']).strftime('%Y-%m-%dT%H:%M:%S.') + '000000000Z'
        return data
    except Exception as e:
        print(f"Error fetching whale movements: {e}")
        return []

def parse_exchange_flow_number(s: str) -> float:
    """Convert strings like '+$1.2K' to numbers."""
    s = s.replace("+", "").replace("$", "").strip()
    multipliers = {"K": 1e3, "M": 1e6, "B": 1e9, "T": 1e12, "Q": 1e15, "m": 1e-3, "μ": 1e-6}
    return float(s[:-1]) * multipliers[s[-1]] if s[-1] in multipliers else float(s)

def get_exchange_flow(symbol: str) -> dict:
    """Scrape exchange flow data from Coinglass."""
    default_timeframes = [
        "5 minute", "15 minute", "30 minute", "1 hour", "4 hour", "8 hour", "12 hour", "24 hour",
        "3 day", "5 day", "7 day", "10 day", "15 day", "30 day", "40 day", "50 day", "60 day",
        "90 day", "120 day", "150 day", "180 day", "1 Year"
    ]
    try:
        driver.get(f'https://www.coinglass.com/currencies/{symbol}?type=spot')
        element = WebDriverWait(driver, 30).until(EC.presence_of_element_located((By.XPATH, "//button[text()='Spot Flows']")))
        driver.execute_script("arguments[0].click();", element)

        def rows_loaded(driver):
            rows = driver.find_elements(By.CSS_SELECTOR, '.cg-style-a2wtpa tbody tr')
            return rows if len(rows) >= 48 and rows[1].text else False

        WebDriverWait(driver, 30).until(rows_loaded)
        soup = BeautifulSoup(driver.page_source, 'html.parser')
        rows = soup.select('.cg-style-a2wtpa tbody tr')[1:23]

        result = [{"timeframe": r.find_all('td')[0].text, "netflow": parse_exchange_flow_number(r.find_all('td')[3].text)} for r in rows]

        # Fill missing timeframes with 0
        found_timeframes = [d['timeframe'] for d in result]
        for tf in default_timeframes:
            if tf not in found_timeframes:
                result.append({"timeframe": tf, "netflow": 0})
        return {"asset": symbol, "data": result}
    except Exception:
        return {"asset": symbol, "data": [{"timeframe": tf, "netflow": 0} for tf in default_timeframes]}

def get_all_metrics(symbol: str) -> dict:
    """Return all key metrics for a symbol."""
    metrics = 'AdrActCnt,TxCnt,HashRate,CapMVRVCur'
    result = parse_coinmetrics_data(get_coinmetrics_data(symbol, 1, metrics), metrics)[0]
    result.update({
        'nvt': get_nvt(symbol)['nvt'],
        'tvl': get_tvl(symbol)['tvl'],
        'coin_id': get_coin_id(symbol)['coin_id']
    })
    return result

get_address_count = lambda s, d=1: get_metric(s, 'AdrActCnt', d)
get_transactions_count = lambda s, d=1: get_metric(s, 'TxCnt', d)
get_hash_rate = lambda s, d=1: get_metric(s, 'HashRate', d)
get_mvrv_ratio = lambda s, d=1: get_metric(s, 'CapMVRVCur', d)

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