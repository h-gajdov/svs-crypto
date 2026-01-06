import { formatLargeNumber } from './formatters.js';

export function renderTA(elementId, signals) {
    const ul = document.getElementById(elementId);
    const spinner = ul.parentNode.querySelector('.spinner-wrapper');
    if (spinner) spinner.remove();
    ul.innerHTML = "";

    signals.forEach(s => {
        let cls = "ta-hold";
        if (s.signal.startsWith("BUY")) cls = "ta-buy";
        if (s.signal.startsWith("SELL")) cls = "ta-sell";
        ul.innerHTML += `<li class="${cls}">${s.signal}</li>`;
    });
}

export function displayIndicators(symbol, data) {
    const format = val => val !== null ? val.toFixed(2) : "-";
    document.querySelector("#rsi").innerText = formatLargeNumber(data['RSI']);
    document.querySelector("#macd").innerText = formatLargeNumber(data['MACD']);
    document.querySelector("#macd_signal").innerText = formatLargeNumber(data['MACD_signal']);
    document.querySelector("#stoch_k").innerText = formatLargeNumber(data['STOCH_K']);
    document.querySelector("#stoch_d").innerText = formatLargeNumber(data['STOCH_D']);
    document.querySelector("#adx").innerText = formatLargeNumber(data['ADX']);
    document.querySelector("#cci").innerText = formatLargeNumber(data['CCI']);
    document.querySelector("#sma20").innerText = formatLargeNumber(data['SMA_20']);
    document.querySelector("#ema20").innerText = formatLargeNumber(data['EMA_20']);
    document.querySelector("#wma20").innerText = formatLargeNumber(data['WMA_20']);
    document.querySelector("#bb_middle").innerText = formatLargeNumber(data['BB_middle']);
    document.querySelector("#bb_upper").innerText = formatLargeNumber(data['BB_upper']);
    document.querySelector("#bb_lower").innerText = formatLargeNumber(data['BB_lower']);
    document.querySelector("#vma20").innerText = formatLargeNumber(data['VMA_20']);
}