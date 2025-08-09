const builder = document.querySelector(".ebs-builder");
const type = document.querySelector("select[name='type']");
const style = document.querySelector("select[name='style']");
const subject = document.querySelector("input[name='subject']");
const status = document.querySelector("input[name='status']");
const imagePreview = document.querySelector("#image-preview");
const copyButton = document.querySelector(".jenkins-copy-button");
const imageUrlInput = document.querySelector(".jenkins-quote input");

[type, style, subject, status].forEach(element => {
    element.addEventListener('change', () => {
        generateUrl();
    })
})

function generateUrl() {
    const badgeUrl = builder.dataset.badgeUrl;
    const textUrl = builder.dataset.textUrl;
    const jobUrl = builder.dataset.jobUrl;

    const searchParams = new URLSearchParams();
    // Flat is the default value so don't bother appending it
    if (style.value && style.value !== 'flat') {
        searchParams.append("style", style.value);
    }
    if (subject.value) {
        searchParams.append("subject", subject.value);
    }
    if (status.value) {
        searchParams.append("status", status.value);
    }

    let url = badgeUrl;
    if (searchParams.toString()) {
        url += '?' + searchParams.toString();
    }

    const urls = {
        'markdown': `[![Build Status](${url})](${jobUrl})`,
        'image': url,
        'html': `<a href="${jobUrl}"><img alt="Badge" src="${url}" /></a>`,
        'asciidoc': `image:${url}[link='${jobUrl}']`,
        'confluence': `[!${url}!|${jobUrl}]`,
        'xwiki': `[[image:${url}>>${jobUrl}||target='__new']]`,
        'rdoc': `{<img alt="Badge" src='${url}' />}[${jobUrl}]`,
        'textile': `"!${url}!":${jobUrl}`,
        'bitbucket': `[Build Status](${url} "${jobUrl}")`,
        'text': textUrl
    }

    if (document.startViewTransition) {
        document.startViewTransition(() => {
            imagePreview.src = url;
            imageUrlInput.value = urls[type.value];
        });
    } else {
        imagePreview.src = url;
        imageUrlInput.value = urls[type.value];
    }

    copyButton.setAttribute('text', urls[type.value]);
}

generateUrl();
