document.addEventListener('DOMContentLoaded', function () {
    const rows = document.querySelectorAll('.clickable-row');
    rows.forEach(row => {
        row.style.cursor = 'pointer'; // show pointer on hover
        row.addEventListener('click', () => {
            const symbol = row.dataset.symbol;
            window.location.href = `/details/${symbol}`;
        });
    });
});