export function formatLargeNumber(value) {
    const absValue = Math.abs(value);
    let formatted;

    if (absValue >= 1e9) formatted = (absValue / 1e9).toFixed(2) + "B";
    else if (absValue >= 1e6) formatted = (absValue / 1e6).toFixed(2) + "M";
    else if (absValue >= 1e3) formatted = (absValue / 1e3).toFixed(2) + "K";
    else formatted = absValue.toFixed(2);

    return value < 0 ? "-" + formatted : formatted;
}

export function formatHashRate(hashRate) {
    if (hashRate >= 1e12) return (hashRate / 1e12).toFixed(2) + " TH/s";
    if (hashRate >= 1e9)  return (hashRate / 1e9).toFixed(2) + " GH/s";
    if (hashRate >= 1e6)  return (hashRate / 1e6).toFixed(2) + " MH/s";
    if (hashRate >= 1e3)  return (hashRate / 1e3).toFixed(2) + " kH/s";
    return hashRate + " H/s";
}