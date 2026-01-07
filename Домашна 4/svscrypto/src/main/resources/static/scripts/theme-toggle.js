const themeToggle = document.querySelector(".theme-toggle");
const themeIcon = document.querySelector("#theme-icon");
const root = document.documentElement;
let darkMode = localStorage.getItem("theme") === "dark";

updateIcon();

themeToggle.addEventListener("click", () => {
    darkMode = !darkMode;
    root.classList.toggle("theme-dark", darkMode);
    root.setAttribute("data-bs-theme", darkMode ? "dark" : "light");
    localStorage.setItem("theme", darkMode ? "dark" : "light");
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