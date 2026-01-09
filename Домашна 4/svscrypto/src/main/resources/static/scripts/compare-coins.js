let coinSearchTimeout = null;
const coinSearchCache = {}; // cache results

function toggleCoinDropdown() {
    const dropdown = document.getElementById('coinDropdown');
    const menu = document.getElementById('coinDropdownMenu');
    if (!dropdown || !menu) return;

    const isOpening = !menu.classList.contains('active');
    dropdown.classList.toggle('active');
    menu.classList.toggle('active');

    const searchInput = document.getElementById('coinSearchInput');
    if (searchInput && isOpening) {
        setTimeout(() => searchInput.focus(), 100);
        filterCoinOptions('');
    }
}

function filterCoinOptions(searchTerm) {
    const container = document.getElementById('coinListContainer');
    if (!container) return;

    container.innerHTML = '';

    if (coinSearchCache[searchTerm]) {
        renderCoinSearchResults(coinSearchCache[searchTerm]);
        return;
    }

    clearTimeout(coinSearchTimeout);
    coinSearchTimeout = setTimeout(() => {
        fetch(`/api/search/symbols?q=${encodeURIComponent(searchTerm)}&limit=10`)
            .then(res => res.json())
            .then(data => {
                coinSearchCache[searchTerm] = data;
                renderCoinSearchResults(data);
            })
            .catch(err => console.error("Search error:", err));
    }, 300);
}

function renderCoinSearchResults(data) {
    const container = document.getElementById('coinListContainer');
    container.innerHTML = '';

    data.forEach(coin => {
        const div = document.createElement('div');
        div.className = 'coin-dropdown-item';
        div.setAttribute('data-symbol', coin.symbol);
        div.onclick = () => selectCoin(div);

        div.innerHTML = `
            <img src="https://img.logo.dev/crypto/${coin.symbol}?token=pk_Eik_EQB_QCOrBDpPKu6RYQ"
                 alt="${coin.symbol}" class="coin-icon-dropdown"
                 onerror="this.src='data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%2236%22 height=%2236%22><circle cx=%2218%22 cy=%2218%22 r=%2218%22 fill=%22%23ccc%22/></svg>'">
            <div class="coin-info-dropdown-wrapper">
                <div class="coin-name-dropdown-row">
                    <span class="coin-name-dropdown">${coin.name}</span>
                    <span class="coin-symbol-dropdown">${coin.symbol}</span>
                </div>
                <div class="coin-price-dropdown-row">
                    <span class="coin-price-dropdown">${coin.formattedLastPrice}</span>
                    <span class="coin-change-dropdown ${coin.changePercent >= 0 ? 'positive' : 'negative'}">
                        ${coin.changePercent >= 0 ? '+' : ''}${coin.formattedChange}%
                    </span>
                </div>
            </div>
        `;
        container.appendChild(div);
    });
}

function selectCoin(element) {
    const symbol = element.getAttribute('data-symbol');
    const coinName = element.querySelector('.coin-name-dropdown').textContent;
    const coinSymbol = element.querySelector('.coin-symbol-dropdown').textContent;

    const selectedText = document.getElementById('selectedCoinText');
    if (selectedText) {
        selectedText.textContent = coinName + ' (' + coinSymbol + ')';
    }

    const hiddenInput = document.getElementById('selectedSymbolInput');
    if (hiddenInput) hiddenInput.value = symbol;

    const addBtn = document.getElementById('addCoinBtn');
    if (addBtn) addBtn.disabled = false;

    toggleCoinDropdown();

    const searchInput = document.getElementById('coinSearchInput');
    if (searchInput) {
        searchInput.value = '';
        filterCoinOptions('');
    }
}