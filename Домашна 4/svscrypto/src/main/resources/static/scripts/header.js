document.querySelector("#menu-button").addEventListener("click", (e) => {
    document.querySelector("#dropdown-menu").classList.toggle("dropdown-menu")
})

let allSearchItems = [];

document.addEventListener('DOMContentLoaded', function () {
    const searchList = document.getElementById('searchListContainer');
    if (searchList) {
        allSearchItems = Array.from(searchList.querySelectorAll('.coin-dropdown-item'));
    }
});

function toggleSearch() {
    const searchContainer = document.getElementById('searchDropdown');
    const searchToggleBtn = document.querySelector('.search-toggle-btn');

    if (searchContainer) {
        searchContainer.classList.toggle('search-active');

        if (searchContainer.classList.contains('search-active')) {
            searchToggleBtn.style.transform = 'rotate(90deg)';
            setTimeout(() => {
                toggleSearchDropdown();
            }, 300);
        } else {
            searchToggleBtn.style.transform = 'rotate(0deg)';
            const dropdown = document.getElementById('searchDropdown');
            const menu = document.getElementById('searchDropdownMenu');
            if (dropdown && menu) {
                dropdown.classList.remove('active');
                menu.classList.remove('active');
            }
        }
    }
}

function toggleSearchDropdown() {
    const dropdown = document.getElementById('searchDropdown');
    const menu = document.getElementById('searchDropdownMenu');

    if (!dropdown || !menu) return;

    // Only toggle if search is active
    if (!dropdown.classList.contains('search-active')) return;

    dropdown.classList.toggle('active');
    menu.classList.toggle('active');

    if (menu.classList.contains('active')) {
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            setTimeout(() => searchInput.focus(), 100);
        }
    }
}

function selectSearchCoin(element) {
    const symbol = element.getAttribute('data-symbol');
    window.location.href = `/details/${symbol}`;
}

function filterSearchOptions(searchTerm) {
    const searchLower = searchTerm.toLowerCase();

    allSearchItems.forEach(item => {
        const symbol = item.getAttribute('data-symbol').toLowerCase();
        const name = item.querySelector('.coin-name-dropdown').textContent.toLowerCase();
        const matches = symbol.includes(searchLower) || name.includes(searchLower);
        item.style.display = matches ? 'flex' : 'none';
    });
}

// Close search dropdown when clicking outside
document.addEventListener('click', function (e) {
    const searchDropdown = document.getElementById('searchDropdown');
    const searchToggleBtn = document.querySelector('.search-toggle-btn');

    if (searchDropdown && !searchDropdown.contains(e.target) && !searchToggleBtn.contains(e.target)) {
        if (searchDropdown.classList.contains('search-active')) {
            toggleSearch();
        }
    }
});

document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keydown', function (event) {
            if (event.key === 'Enter') {
                const visibleItems = allSearchItems.filter(item => item.style.display !== 'none');
                if (visibleItems.length > 0) {
                    selectSearchCoin(visibleItems[0]);
                }
            }
        });
    }
});