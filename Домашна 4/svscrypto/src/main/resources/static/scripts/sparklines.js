document.addEventListener('DOMContentLoaded', function () {
    const sparklineDivs = document.querySelectorAll('.sparkline');

    sparklineDivs.forEach(div => {
        const pricesStr = div.dataset.prices || '';
        if (!pricesStr) return;

        // Convert "1.0,2.0,3.0" to [1,2,3]
        const prices = pricesStr.split(',').map(Number);

        if (prices.length === 0) return;

        const color = prices[prices.length - 1] >= prices[0] ? '#49d06e' : '#ff0000';

        Plotly.newPlot(div, [{
            x: Array.from({length: prices.length}, (_, i) => i),
            y: prices,
            mode: 'lines',
            line: {width: 2, color: color},
            hoverinfo: 'none'
        }], {
            margin: {t: 0, b: 0, l: 0, r: 0},
            xaxis: {visible: false, fixedrange: true},
            yaxis: {visible: false, fixedrange: true, range: [Math.min(...prices), Math.max(...prices)]},
            showlegend: false,
            width: 185,
            height: 75,
            paper_bgcolor: 'rgba(0,0,0,0)',
            plot_bgcolor: 'rgba(0,0,0,0)'
        }, {staticPlot: true});
    });
});