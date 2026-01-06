import { initChart, initChartControls, initDownloadButton } from "./chart.js";
import { initPrediction } from "./prediction.js";
import { displayOnChainMetrics, displayOnChainPrediction, displayExchangeFlows, displayWhaleAlerts } from "./onchain.js";
import { displayNews } from "./news.js";
import { renderTA, displayIndicators } from "./indicators.js";

document.addEventListener("DOMContentLoaded", () => {

    const data = window.DETAILS_PAGE;
    const symbol = data.symbol.replaceAll('"', '');

    initChart(data);
    initChartControls(symbol);
    initDownloadButton(symbol + "-chart");

    initPrediction();
    fetch(`/api/prediction?symbol=${symbol}`)
        .then(res => res.json())
        .then(d => window.pricePrediction = d);

    fetch(`/api/indicators?symbol=${symbol}`)
        .then(res => res.json())
        .then(d => displayIndicators(symbol, d));

    fetch(`/technical-analysis?symbol=${symbol}`)
        .then(res => res.json())
        .then(d => {
            renderTA("ta-1d", d["1D"]);
            renderTA("ta-1w", d["1W"]);
            renderTA("ta-1m", d["1M"]);
        });

    fetch(`/api/on-chain/metrics?symbol=${symbol}`)
        .then(res => res.json())
        .then(displayOnChainMetrics);

    fetch(`/api/on-chain/sentiment?symbol=${symbol}`)
        .then(res => res.json())
        .then(displayOnChainPrediction);

    fetch(`/api/on-chain/exchange-flows?symbol=${symbol}`)
        .then(res => res.json())
        .then(d => displayExchangeFlows(d.data, symbol));

    fetch(`/api/on-chain/whale-movements`)
        .then(res => res.json())
        .then(displayWhaleAlerts);

    fetch(`/api/on-chain/news?symbol=${symbol}`)
        .then(res => res.json())
        .then(displayNews);
});