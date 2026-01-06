import { formatLargeNumber, formatHashRate } from './formatters.js';

export function displayOnChainMetrics(metricsData) {
    document.querySelector("#active_addresses_count").innerText = formatLargeNumber(metricsData['AdrActCnt']);
    document.querySelector("#transactions_count").innerText = formatLargeNumber(metricsData['TxCnt']);
    document.querySelector("#hashrate_value").innerText = formatHashRate(metricsData['HashRate']);
    document.querySelector("#tvl_value").innerText = formatLargeNumber(metricsData['tvl']);
    document.querySelector("#nvt_value").innerText = formatLargeNumber(metricsData['nvt']);
    document.querySelector("#mvrv_value").innerText = formatLargeNumber(metricsData['CapMVRVCur']);
}

export function displayOnChainPrediction(data) {
    document.querySelector('#signal-label').innerText = data['signal'];
    document.querySelector('#signal-score').innerText = formatLargeNumber(data['combined_score']);
}

export function displayExchangeFlows(exchangeFlows, symbol) {
    const table = document.querySelector('#exchange-flow-table');
    const spinner = table.querySelector('.spinner-wrapper');
    if (spinner) spinner.remove();

    exchangeFlows.forEach(flow => {
        const arrow = flow.netflow < 0 ? '▼ ' : '▲ ';
        const colorClass = flow.netflow < 0 ? 'change-negative' : 'change-positive';
        const flowElement = `
        <div class="exchange-row">
            <span>${symbol.replaceAll('"', '')}</span>
            <span class="${colorClass}">${arrow}${formatLargeNumber(flow.netflow)} $</span>
            <span>${flow.timeframe}</span>
        </div>`;
        table.innerHTML += flowElement;
    });
}

export function displayWhaleAlerts(data) {
    const container = document.querySelector("#whale-container");
    container.innerHTML = "";
    data.forEach(item => {
        const date = new Date(item.time);
        const formatted = date.toLocaleDateString() + " " + date.toLocaleTimeString();
        const text = item.text.charAt(0).toUpperCase() + item.text.slice(1);
        const amountsDiv = getAmountsDiv(item.amounts);
        const alertDiv = `<div class="whale-entry">
            <span class="whale-time">${formatted}</span>
            <span class="whale-text">${text}</span>
            ${amountsDiv.outerHTML}
        </div>`;
        container.innerHTML += alertDiv;
    });
}

function getAmountsDiv(amounts) {
    const result = document.createElement('div');
    result.classList.add("whale-amounts");
    amounts.forEach(a => {
        const symbol = a.symbol;
        const amount = a.amount;
        const value = a.value_usd;
        const div = `
        <div class="wh-am">
            <div class="wh-symbol-wrapper">
                <span class="am-symbol">Symbol: ${symbol}</span>
                <img src="https://img.logo.dev/crypto/${symbol}?token=pk_Eik_EQB_QCOrBDpPKu6RYQ">
                <button class="btn btn-primary" onclick="window.open('/details/${symbol}', '_blank')">
                    View coin
                </button>
            </div>
            <span class="am-amount">Amount: ${formatLargeNumber(amount)}</span>
            <span class="am-value">Value: ${formatLargeNumber(value)}$</span>
        </div>`;
        result.innerHTML += div;
    });
    return result;
}