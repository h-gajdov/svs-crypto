if (document.body.classList.contains('theme-dark')) {
    document.body.setAttribute('data-bs-theme', 'dark');
} else {
    document.body.setAttribute('data-bs-theme', 'light');
}

const themeToggle = document.querySelector(".theme-toggle");
const themeIcon = document.querySelector("#theme-icon");
const body = document.body;

let darkMode = localStorage.getItem("darkMode") === "true";

if (darkMode) body.classList.add("theme-dark");
updateIcon();

themeToggle.addEventListener("click", () => {
    darkMode = !darkMode;
    body.classList.toggle("theme-dark", darkMode);
    localStorage.setItem("darkMode", darkMode);
    updateIcon();
});

function updateIcon() {
    if (darkMode) {
        themeIcon.classList.remove("fa-moon");
        themeIcon.classList.add("fa-sun");
    } else {
        themeIcon.classList.remove("fa-sun");
        themeIcon.classList.add("fa-moon");
    }
}