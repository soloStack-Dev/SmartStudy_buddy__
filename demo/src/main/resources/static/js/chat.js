// Keep the messages panel scrolled to the bottom whenever new content is added.
document.addEventListener("htmx:afterSwap", function (event) {
    const target = event.detail.target;
    if (!target) {
        return;
    }
    if (target.id === "messages") {
        target.scrollTop = target.scrollHeight;
    }
});

