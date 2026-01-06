let chartDiv;
let layout;
let currentField = "price";
let currentTime = "max";

export function initChart(data) {
    chartDiv = document.getElementById("data-graph");

    const dates = data.timestamps.map(ts => new Date(ts * 1000));
    const values = data.values;

    layout = {
        paper_bgcolor: 'rgba(0,0,0,0)',
        plot_bgcolor: 'rgba(0,0,0,0)',
        margin: { t: 20, r: 20, l: 60, b: 80 },
        yaxis: { tickformat: '$,', autorange: true },
        xaxis: { tickangle: -45, type: 'date', tickformat: "%d %b", nticks: 10 }
    };

    Plotly.newPlot(
        chartDiv,
        [getTraceForField(currentField, dates, values)],
        layout,
        { responsive: true }
    );
}

export function initChartControls(symbol) {

    document.querySelectorAll(".timeframe-btn").forEach(btn => {
        btn.addEventListener("click", e => {
            e.preventDefault();

            document.querySelectorAll(".timeframe-btn")
                .forEach(b => b.classList.remove("graph-btn-selected"));
            btn.classList.add("graph-btn-selected");

            currentTime = btn.dataset.time;

            fetch(`/details/${symbol}/plot/${currentTime}`)
                .then(res => res.json())
                .then(data => {
                    const dates = data.timestamps.map(ts => new Date(ts * 1000));
                    Plotly.react(
                        chartDiv,
                        [getTraceForField(currentField, dates, data.values)],
                        layout
                    );
                });
        });
    });

    document.querySelectorAll("[data-field]").forEach(btn => {
        btn.addEventListener("click", () => {

            document.querySelectorAll("[data-field]")
                .forEach(b => b.classList.remove("graph-btn-selected"));
            btn.classList.add("graph-btn-selected");

            currentField = btn.dataset.field.toLowerCase();

            fetch(`/details/${symbol}/plot/${currentTime}?field=${currentField}`)
                .then(res => res.json())
                .then(data => {
                    const dates = data.timestamps.map(ts => new Date(ts * 1000));
                    Plotly.react(
                        chartDiv,
                        [getTraceForField(currentField, dates, data.values)],
                        layout
                    );
                });
        });
    });
}

function getTraceForField(field, dates, values) {
    if (field === "price") {
        return {
            x: dates,
            y: values,
            type: "scatter",
            mode: "lines",
            fill: "tozeroy",
            line: { color: "#4A90E2" }
        };
    }

    const colors = values.map((v, i) =>
        i === 0 ? "#47cf6d" : v >= values[i - 1] ? "#47cf6d" : "#ff4d4d"
    );

    return {
        x: dates,
        y: values,
        type: "bar",
        marker: { color: colors }
    };
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