const passwordInput = document.getElementById("password");
const rulesBox = document.getElementById("password-rules");

const rules = {
    length: document.getElementById("rule-length"),
    lower: document.getElementById("rule-lower"),
    number: document.getElementById("rule-number"),
    special: document.getElementById("rule-special")
};

passwordInput.addEventListener("focus", () => {
    rulesBox.style.display = "block";
});

passwordInput.addEventListener("blur", () => {
    rulesBox.style.display = "none";
});

passwordInput.addEventListener("input", () => {
    const value = passwordInput.value;

    toggleRule(rules.length, value.length >= 8);
    toggleRule(rules.lower, /[a-z]/.test(value));
    toggleRule(rules.number, /\d/.test(value));
    toggleRule(rules.special, /[^A-Za-z0-9]/.test(value));
});

function toggleRule(element, condition) {
    if (condition) {
        element.classList.add("valid");
        element.textContent = element.textContent.replace("❌", "✔️");
    } else {
        element.classList.remove("valid");
        element.textContent = element.textContent.replace("✔️", "❌");
    }
}