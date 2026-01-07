let allCoinItems = [];

document.addEventListener('DOMContentLoaded', function () {
    const coinList = document.getElementById('coinListContainer');
    if (coinList) {
        allCoinItems = Array.from(coinList.querySelectorAll('.coin-dropdown-item'));
    }
});

function toggleCoinDropdown() {
    const dropdown = document.getElementById('coinDropdown');
    const menu = document.getElementById('coinDropdownMenu');

    if (!dropdown || !menu) return;

    dropdown.classList.toggle('active');
    menu.classList.toggle('active');

    if (menu.classList.contains('active')) {
        const searchInput = document.getElementById('coinSearchInput');
        if (searchInput) {
            setTimeout(() => searchInput.focus(), 100);
        }
    }
}

function toggleTimeDropdown() {
    const dropdown = document.getElementById('timeDropdown');
    const menu = document.getElementById('timeDropdownMenu');

    if (!dropdown || !menu) return;

    dropdown.classList.toggle('active');
    menu.classList.toggle('active');
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
    if (hiddenInput) {
        hiddenInput.value = symbol;
    }

    const addBtn = document.getElementById('addCoinBtn');
    if (addBtn) {
        addBtn.disabled = false;
    }
    toggleCoinDropdown();

    const searchInput = document.getElementById('coinSearchInput');
    if (searchInput) {
        searchInput.value = '';
        filterCoinOptions('');
    }
}

function selectTime(element) {
    const days = element.getAttribute('data-days');
    const timeText = element.querySelector('.time-option-text').textContent;

    const selectedText = document.getElementById('selectedTimeText');
    if (selectedText) {
        selectedText.textContent = timeText;
    }

    toggleTimeDropdown();

    const url = new URL(window.location.href);
    url.searchParams.set("days", days);
    window.location.href = url.toString();
}

function filterCoinOptions(searchTerm) {
    const searchLower = searchTerm.toLowerCase();

    allCoinItems.forEach(item => {
        const symbol = item.getAttribute('data-symbol').toLowerCase();
        const name = item.querySelector('.coin-name-dropdown').textContent.toLowerCase();
        const matches = symbol.includes(searchLower) || name.includes(searchLower);
        item.style.display = matches ? 'flex' : 'none';
    });
}

document.addEventListener('click', function (e) {
    const coinDropdown = document.getElementById('coinDropdown');
    const timeDropdown = document.getElementById('timeDropdown');

    if (coinDropdown && !coinDropdown.contains(e.target)) {
        coinDropdown.classList.remove('active');
        const menu = document.getElementById('coinDropdownMenu');
        if (menu) {
            menu.classList.remove('active');
        }
    }

    if (timeDropdown && !timeDropdown.contains(e.target)) {
        timeDropdown.classList.remove('active');
        const menu = document.getElementById('timeDropdownMenu');
        if (menu) {
            menu.classList.remove('active');
        }
    }
});

const addCoinForm = document.getElementById('addCoinForm');
if (addCoinForm) {
    addCoinForm.addEventListener('submit', function () {
        setTimeout(() => {
            const selectedText = document.getElementById('selectedCoinText');
            const hiddenInput = document.getElementById('selectedSymbolInput');
            const addBtn = document.getElementById('addCoinBtn');

            if (selectedText) selectedText.textContent = 'Select a coin...';
            if (hiddenInput) hiddenInput.value = '';
            if (addBtn) addBtn.disabled = true;
        }, 100);
    });
}