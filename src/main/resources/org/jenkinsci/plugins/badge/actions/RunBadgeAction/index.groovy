package org.jenkinsci.plugins.badge.RunBadgeAction
import java.net.URLEncoder;

def l = namespace(lib.LayoutTagLib)
def st = namespace("jelly:stapler")

l.layout(type: "one-column") {
    l.main_panel {
        h2(_("Embeddable Build Status Icon"))
        p(raw(_("blurb")))
        st.adjunct(includes: "org.jenkinsci.plugins.badge.actions.JobBadgeAction.ClickHandler")
        l.css(src: "/plugin/embeddable-build-status/css/design.css")

        def fullJobName = URLEncoder.encode(my.project.fullName, "UTF-8");
        def jobUrl =  "${app.rootUrl}${my.project.url}${my.run.number}/";
        def badgeUrl = jobUrl + "badge/icon"
        def textUrl = jobUrl + "badge/text"
        def publicBadge = "${app.rootUrl}buildStatus/icon?job=${fullJobName}&build=${my.run.number}";
        def publicText = "${app.rootUrl}buildStatus/text?job=${fullJobName}&build=${my.run.number}";

        h2(_("Examples"))
        small(_("examples_note"))
        
        h3(_("flat"))
        img(id:"badgeUrl",
            src: badgeUrl,
            title: badgeUrl)
        raw ("<br/>")
        img(id:"badgeUrl",
            src:badgeUrl + "?subject=Custom%20Subject&status=Any%20State&color=darkturquoise",
            title:badgeUrl + "?subject=Custom%20Subject&status=Any%20State&color=darkturquoise")

        h3(_("flat-square: "))
        img(src:badgeUrl + "?style=flat-square", 
            title: badgeUrl + "?style=flat-square")
        raw ("<br/>")
        img(src:badgeUrl + "?style=flat-square&subject=Custom%20Subject&status=Any%20State&color=darkturquoise",
            title:badgeUrl + "?style=flat-square&subject=Custom%20Subject&status=Any%20State&color=darkturquoise")

        h3(_("plastic: "))
        img(src:badgeUrl + "?style=plastic", 
        title: badgeUrl + "?style=plastic")
        raw ("<br/>")
        img(src:badgeUrl + "?style=plastic&subject=Custom%20Subject&status=Any%20State&color=darkturquoise",
            title:badgeUrl + "?style=plastic&subject=Custom%20Subject&status=Any%20State&color=darkturquoise")

        h3(_("ball-<size>: "))
        img(src:badgeUrl + "?style=ball-16x16", 
            title: badgeUrl + "?style=ball-16x16")
        raw ("<br/>")
        img(src:badgeUrl + "?style=ball-32x32", 
            title: badgeUrl + "?style=ball-32x32")
        
        h2(_("Links"))

        h3(_("Plain Link"))
        b {text(_("protected"))}
        input(type:"text",value:badgeUrl,class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:publicBadge,class:"select-all")

        h3(_("Markdown"))
        b {text(_("protected"))}
        input(type:"text",value:"[![Build Status](${badgeUrl})](${jobUrl})",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[![Build Status](${publicBadge})](${jobUrl})",class:"select-all")

        h3(_("HTML"))
        b {text(_("protected"))}
        input(type:"text",value:"<a href='${jobUrl}'><img src='${badgeUrl}'></a>",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"<a href='${jobUrl}'><img src='${publicBadge}'></a>",class:"select-all")

        h3(_("Confluence"))
        b {text(_("protected"))}
        input(type:"text",value:"[!${badgeUrl}!|${jobUrl}]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[!${publicBadge}!|${jobUrl}]",class:"select-all")

        h3(_("XWiki"))
        b {text(_("protected"))}
        input(type:"text",value:"[[image:${badgeUrl}>>${jobUrl}||target='__new']]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[[image:${publicBadge}>>${jobUrl}||target='__new']]",class:"select-all")

        h3(_("RDoc"))
        b {text(_("protected"))}
        input(type:"text",value:"{<img src='${badgeUrl}'/>}[${jobUrl}]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"{<img src='${publicBadge}'/>}[${jobUrl}]",class:"select-all")

        h3(_("Textile"))
        b {text(_("protected"))}
        input(type:"text",value:"\"!${badgeUrl}!\":${jobUrl}",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"\"!${publicBadge}!\":${jobUrl}",class:"select-all")

        h3(_("Bitbucket"))
        b {text(_("protected"))}
        input(type:"text",value:"[Build Status](${badgeUrl}) \"${jobUrl}\")",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[Build Status](${publicBadge} \"${jobUrl}\")",class:"select-all")

        h2(_("Embeddable Build Status Text"))
        p(raw(_("blurb_text")))

        h3(_("Text Only"))
        b {text(_("protected"))}
        input(type:"text",value:textUrl,class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:publicText,class:"select-all")
    }
}
