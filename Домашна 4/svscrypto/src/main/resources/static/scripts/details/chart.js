let chartDiv;
let layout;
let currentField = "price";
let currentTime = "max";

export function initChart(data) {
    chartDiv = document.getElementById("data-graph");

    layout = {
        paper_bgcolor: 'rgba(0,0,0,0)',
        plot_bgcolor: 'rgba(0,0,0,0)',
        margin: { t: 20, r: 20, l: 60, b: 80 },
        yaxis: { tickformat: '$,', autorange: true },
        xaxis: {
            type: 'date',
            tickangle: -45,
            tickformat: "%d %b %Y",
            nticks: 10
        }
    };

    renderChart(data);
}

export function initChartControls(symbol) {
    document.querySelectorAll(".timeframe-btn").forEach(btn => {
        btn.addEventListener("click", e => {
            e.preventDefault();

            document.querySelectorAll(".timeframe-btn").forEach(b => b.classList.remove("graph-btn-selected"));
            btn.classList.add("graph-btn-selected");

            currentTime = btn.dataset.time;
            fetchChartData(symbol);
        });
    });

    // Field buttons
    document.querySelectorAll("[data-field]").forEach(btn => {
        btn.addEventListener("click", () => {

            document.querySelectorAll("[data-field]").forEach(b => b.classList.remove("graph-btn-selected"));
            btn.classList.add("graph-btn-selected");

            currentField = btn.dataset.field.toLowerCase();
            fetchChartData(symbol);
        });
    });
}

function fetchChartData(symbol) {
    const fieldParam = currentField === "candles" ? "candles" : currentField;
    fetch(`/details/${symbol}/plot/${currentTime}?field=${fieldParam}`)
        .then(res => res.json())
        .then(data => renderChart(data));
}

function renderChart(data) {
    const dates = data.timestamps.map(ts => new Date(ts * 1000));
    let trace;

    if (currentField === "price") {
        trace = {
            x: dates,
            y: data.values,
            type: "scatter",
            mode: "lines",
            fill: "tozeroy",
            line: { color: "#4A90E2" }
        };
    } else if (currentField === "candles") {
        trace = {
            x: dates,
            open: data.open,
            high: data.high,
            low: data.low,
            close: data.close,
            type: "candlestick",
            increasing: { line: { color: "#4caf50" } },
            decreasing: { line: { color: "#f44336" } }
        };
    } else {
        const values = data.values || data[currentField] || [];
        const colors = values.map((v, i) => i === 0 ? "#47cf6d" : v >= values[i - 1] ? "#47cf6d" : "#ff4d4d");

        trace = {
            x: dates,
            y: values,
            type: "bar",
            marker: { color: colors }
        };
    }

    Plotly.react(chartDiv, [trace], layout);
}

export function initDownloadButton(filename = "chart") {
    const btn = document.getElementById("download-graph-btn");
    if (!btn) return;

    btn.addEventListener("click", () => {
        Plotly.downloadImage(chartDiv, {
            format: "png",
            filename,
            height: 600,
            width: 1000,
            scale: 2
        });
    });
}