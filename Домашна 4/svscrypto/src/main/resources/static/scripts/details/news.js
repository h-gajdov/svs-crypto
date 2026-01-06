export function displayNews(news) {
    document.querySelector('#sentiment-label span').innerText = news['sentiment'];
    const newsArr = news['news'].slice(0, 5);
    const newsSection = document.querySelector("#news-container");
    newsSection.innerHTML = "";

    newsArr.forEach(n => {
        const date = new Date(n.created_at);
        const formatted = date.toLocaleDateString() + " " + date.toLocaleTimeString();
        const author = n.author[0];
        const entryDiv = `<div class="news-entry">
            <span class="news-heading">${n.headline}</span>
            <span class="news-summary">${n.summary}</span>
            <span class="news-source">Posted at: ${formatted}</span>
            <div style="display: flex; justify-content: space-between;">
                <div class="news-content-wrapper">
                    <section>
                        <span class="news-author">Author: ${author}</span>
                        <span class="news-source">Source: ${n.source}</span>
                    </section>
                    <img src="${n.image}">
                </div>
                <button class="btn btn-primary" onclick="window.open('${n.url}', '_blank')">
                    View more
                </button>
            </div>
        </div>`;
        newsSection.innerHTML += entryDiv;
    });
}