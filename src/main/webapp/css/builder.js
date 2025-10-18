const builder = document.querySelector(".ebs-builder");
const type = document.querySelector("select[name='type']");
const style = document.querySelector("select[name='style']");
const subject = document.querySelector("input[name='subject']");
const status = document.querySelector("input[name='status']");
const protectedCheckbox = document.querySelector("input[name='protected']");
const imagePreview = document.querySelector("#image-preview");
const copyButton = document.querySelector(".jenkins-copy-button");
const imageUrlInput = document.querySelector(".jenkins-quote input");

[type, style, subject, status, protectedCheckbox].forEach(element => {
    element.addEventListener('change', () => {
        generateUrl();
    })
})

function generateUrl() {
    const badgeUrl = protectedCheckbox.checked ? builder.dataset.badgeUrl : builder.dataset.publicBadgeUrl;
    const textUrl = protectedCheckbox.checked ? builder.dataset.textUrl : builder.dataset.publicTextUrl;
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
        url += (url.includes('?') ? '&' : '?') + searchParams.toString();
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
