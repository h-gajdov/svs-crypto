let counter = 0;
let predictionInterval;

export function initPrediction() {
    const btn = document.getElementById("get-prediction-btn");
    if (!btn) return;

    btn.addEventListener("click", e => {
        if (!window.pricePrediction) {
            e.target.insertAdjacentHTML("afterend", `<p id="thinking">Thinking</p>`);
            e.target.remove();
            predictionInterval = setInterval(thinkingHandler, 500);
            return;
        }

        e.target.remove();
        displayPrediction();
    });
}

function thinkingHandler() {
    if (!window.pricePrediction) {
        const thinking = document.getElementById("thinking");
        if (thinking) {
            thinking.innerText = "Thinking" + ".".repeat(++counter % 4);
        }
        return;
    }

    clearInterval(predictionInterval);
    counter = 0;

    const thinking = document.getElementById("thinking");
    if (thinking) thinking.remove();

    displayPrediction();
}

function displayPrediction() {
    const data = window.pricePrediction;
    if (!data) return;

    document.getElementById("predicted_next_close_price").innerText =
        data.predicted_next_close_price + "$ (USD)";

    Object.keys(data.metrics).forEach(key => {
        const el = document.getElementById(key);
        if (el) el.innerText = data.metrics[key];
    });

    document.getElementById("prediction-div").classList.remove("hidden");
}