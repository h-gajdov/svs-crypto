document.querySelector("#menu-button").addEventListener("click", () => {
    const menu = document.querySelector("#dropdown-menu");
    if (menu) menu.classList.toggle("dropdown-menu");
});

function toggleSearch() {
    const searchContainer = document.getElementById("searchDropdown");
    const searchToggleBtn = document.querySelector(".search-toggle-btn");
    if (!searchContainer) return;

    searchContainer.classList.toggle("search-active");

    if (searchContainer.classList.contains("search-active")) {
        searchToggleBtn.style.transform = "rotate(90deg)";
        setTimeout(() => toggleSearchDropdown(), 300);
    } else {
        searchToggleBtn.style.transform = "rotate(0deg)";
        const menu = document.getElementById("searchDropdownMenu");
        if (menu) menu.classList.remove("active");
    }
}

function toggleSearchDropdown() {
    const dropdown = document.getElementById("searchDropdown");
    const menu = document.getElementById("searchDropdownMenu");
    if (!dropdown || !menu) return;
    if (!dropdown.classList.contains("search-active")) return;

    dropdown.classList.toggle("active");
    menu.classList.toggle("active");

    if (menu.classList.contains("active")) {
        const searchInput = document.getElementById("searchInput");
        if (searchInput) setTimeout(() => searchInput.focus(), 100);
        filterSearchOptions("");
    }
}

function selectSearchCoin(element) {
    const symbol = element.getAttribute("data-symbol");
    if (symbol) window.location.href = `/details/${symbol}`;
}

let searchTimeout = null;
const searchCache = {}; // client-side cache

function filterSearchOptions(searchTerm) {
    const container = document.getElementById("searchListContainer");
    if (!container) return;

    container.innerHTML = "";

    if (searchCache[searchTerm]) {
        renderSearchResults(searchCache[searchTerm]);
        return;
    }

    clearTimeout(searchTimeout);
    searchTimeout = setTimeout(() => {
        fetch(`/api/search/symbols?q=${encodeURIComponent(searchTerm)}&limit=10`)
            .then(res => res.json())
            .then(data => {
                searchCache[searchTerm] = data;
                renderSearchResults(data);
            })
            .catch(err => console.error("Search error:", err));
    }, 300);
}

function renderSearchResults(data) {
    const container = document.getElementById("searchListContainer");
    container.innerHTML = "";

    data.forEach(coin => {
        const div = document.createElement("div");
        div.className = "coin-dropdown-item";
        div.setAttribute("data-symbol", coin.symbol);
        div.onclick = () => selectSearchCoin(div);

        div.innerHTML = `
            <img
                src="https://img.logo.dev/crypto/${coin.symbol}?token=pk_Eik_EQB_QCOrBDpPKu6RYQ"
                alt="${coin.symbol}"
                class="coin-icon-dropdown"
                onerror="this.src='data:image/svg+xml,<svg xmlns=%22http://www.w3.org/2000/svg%22 width=%2236%22 height=%2236%22><circle cx=%2218%22 cy=%2218%22 r=%2218%22 fill=%22%23ccc%22/></svg>'"
            >
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

document.addEventListener("click", (e) => {
    const searchDropdown = document.getElementById("searchDropdown");
    const searchToggleBtn = document.querySelector(".search-toggle-btn");

    if (!searchDropdown || !searchToggleBtn) return;

    if (!searchDropdown.contains(e.target) && !searchToggleBtn.contains(e.target)) {
        if (searchDropdown.classList.contains("search-active")) toggleSearch();
    }
});

document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("searchInput");
    if (searchInput) {
        searchInput.addEventListener("keydown", (event) => {
            if (event.key === "Enter") {
                const container = document.getElementById("searchListContainer");
                const firstItem = container?.querySelector(".coin-dropdown-item");
                if (firstItem) selectSearchCoin(firstItem);
            }
        });
    }
});