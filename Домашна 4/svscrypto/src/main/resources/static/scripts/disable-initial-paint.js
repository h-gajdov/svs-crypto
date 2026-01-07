(function () {
    const theme = localStorage.getItem("theme");

    if (theme === "dark") {
        document.documentElement.classList.add("theme-dark");
    }

    // Disable transitions during initial paint
    document.documentElement.classList.add("no-theme-transition");
})();