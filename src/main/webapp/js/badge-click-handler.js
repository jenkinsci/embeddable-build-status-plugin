document.addEventListener('click', function(event) {
    var svg = event.target.closest('svg.jenkins-badge-clickable');
    if (svg) {
        var url = svg.getAttribute('data-jenkins-link-url');
        if (url) {
            window.open(url);
            event.preventDefault();
        }
    }
});