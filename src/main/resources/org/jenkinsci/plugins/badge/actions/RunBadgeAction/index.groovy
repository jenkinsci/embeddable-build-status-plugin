package org.jenkinsci.plugins.badge.RunBadgeAction

def l = namespace(lib.LayoutTagLib)
def st = namespace("jelly:stapler")

l.layout {
    l.main_panel {
        h2(_("Embeddable Build Status Icon"))
        p(raw(_("blurb")))
        raw("""
<p>
</p>
<script>
    Behaviour.register({
        "INPUT.select-all" : function(e) {
            e.onclick = function () {
                e.focus();
                e.select();
            }
        }
    });
</script>
<style>
    INPUT.select-all {
        width:100%;
    }
    IMG#badge {
        margin-left:2em;
    }
</style>
""")

        def fullJobName = h.escape(my.project.fullName);
        def jobUrlWithView =  "${app.rootUrl}${my.project.url}${my.run.number}/";
        def jobUrlWithoutView =  "${app.rootUrl}job/${fullJobName}/${my.run.number}/";
        def badgeUrlWithView = jobUrlWithView + "badge/icon"
        def badgeUrlWithoutView = jobUrlWithoutView + "/badge/icon"
        def textUrlWithView = jobUrlWithView + "badge/text"
        def textUrlWithoutView = jobUrlWithoutView + "/badge/text"
        def publicBadge = "${app.rootUrl}buildStatus/icon?job=${fullJobName}&build=${my.run.number}";
        def publicText = "${app.rootUrl}buildStatus/text?job=${fullJobName}&build=${my.run.number}";

        h3(_("flat"))
        img(id:"badgeUrlWithView",src:badgeUrlWithView)
        raw ("<br/>")
        img(id:"badgeUrlWithView",
            src:badgeUrlWithView + "?subject=Custom%20Subject&status=Any%20State&color=darkturquoise",
            title:badgeUrlWithView + "?subject=Custom%20Subject&status=Any%20State&color=darkturquoise")
        h3(_("flat-square: "))
        img(src:badgeUrlWithView + "?style=flat-square")
        raw ("<br/>")
        img(src:badgeUrlWithView + "?style=flat-square&subject=Custom%20Subject&status=Any%20State&color=darkturquoise",
            title:badgeUrlWithView + "?style=flat-square&subject=Custom%20Subject&status=Any%20State&color=darkturquoise")
        h3(_("plastic: "))
        img(src:badgeUrlWithView + "?style=plastic")
        raw ("<br/>")
        img(src:badgeUrlWithView + "?style=plastic&subject=Custom%20Subject&status=Any%20State&color=darkturquoise",
            title:badgeUrlWithView + "?style=plastic&subject=Custom%20Subject&status=Any%20State&color=darkturquoise")
        

        h3(_("Plain Link (with view)"))
        b {text(_("protected"))}
        input(type:"text",value:badgeUrlWithView,class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:publicBadge,class:"select-all")

        h3(_("Plain Link (without view)"))
        b {text(_("protected"))}
        input(type:"text",value:badgeUrlWithoutView,class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:publicBadge,class:"select-all")


        h3(_("Markdown (with view)"))
        b {text(_("protected"))}
        input(type:"text",value:"[![Build Status](${badgeUrlWithView})](${jobUrlWithView})",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[![Build Status](${publicBadge})](${jobUrlWithView})",class:"select-all")

        h3(_("Markdown (without view)"))
        b {text(_("protected"))}
        input(type:"text",value:"[![Build Status](${badgeUrlWithoutView})](${jobUrlWithoutView})",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[![Build Status](${publicBadge})](${jobUrlWithoutView})",class:"select-all")

        h3(_("HTML (with view)"))
        b {text(_("protected"))}
        input(type:"text",value:"<a href='${jobUrlWithView}'><img src='${badgeUrlWithView}'></a>",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"<a href='${jobUrlWithView}'><img src='${publicBadge}'></a>",class:"select-all")

        h3(_("HTML (without view)"))
        b {text(_("protected"))}
        input(type:"text",value:"<a href='${jobUrlWithoutView}'><img src='${badgeUrlWithoutView}'></a>",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"<a href='${jobUrlWithoutView}'><img src='${publicBadge}'></a>",class:"select-all")

        h3(_("Confluence (with view)"))
        b {text(_("protected"))}
        input(type:"text",value:"[!${badgeUrlWithView}!|${jobUrlWithView}]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[!${publicBadge}!|${jobUrlWithView}]",class:"select-all")

        h3(_("Confluence (without view)"))
        b {text(_("protected"))}
        input(type:"text",value:"[!${badgeUrlWithoutView}!|${jobUrlWithoutView}]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[!${publicBadge}!|${jobUrlWithoutView}]",class:"select-all")

        h3(_("XWiki (with view)"))
        b {text(_("protected"))}
        input(type:"text",value:"[[image:${badgeUrlWithView}>>${jobUrlWithView}||target='__new']]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[[image:${publicBadge}>>${jobUrlWithView}||target='__new']]",class:"select-all")

        h3(_("XWiki (without view)"))
        b {text(_("protected"))}
        input(type:"text",value:"[[image:${badgeUrlWithoutView}>>${jobUrlWithoutView}||target='__new']]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[[image:${publicBadge}>>${jobUrlWithoutView}||target='__new']]",class:"select-all")

        h3(_("RDoc (with view)"))
        b {text(_("protected"))}
        input(type:"text",value:"{<img src='${badgeUrlWithView}'/>}[${jobUrlWithView}]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"{<img src='${publicBadge}'/>}[${jobUrlWithView}]",class:"select-all")

        h3(_("RDoc (without view)"))
        b {text(_("protected"))}
        input(type:"text",value:"{<img src='${badgeUrlWithoutView}'/>}[${jobUrlWithoutView}]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"{<img src='${publicBadge}'/>}[${jobUrlWithoutView}]",class:"select-all")

        h3(_("Textile (with view)"))
        b {text(_("protected"))}
        input(type:"text",value:"\"!${badgeUrlWithView}!\":${jobUrlWithView}",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"\"!${publicBadge}!\":${jobUrlWithView}",class:"select-all")

        h3(_("Textile (without view)"))
        b {text(_("protected"))}
        input(type:"text",value:"\"!${badgeUrlWithoutView}!\":${jobUrlWithoutView}",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"\"!${publicBadge}!\":${jobUrlWithoutView}",class:"select-all")

        h3(_("Bitbucket (with view"))
        b {text(_("protected"))}
        input(type:"text",value:"[Build Status](${badgeUrlWithView}) \"${jobUrlWithView}\")",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[Build Status](${publicBadge} \"${jobUrlWithView}\")",class:"select-all")

        h3(_("Bitbucket (without view"))
        b {text(_("protected"))}
        input(type:"text",value:"[Build Status](${badgeUrlWithoutView}) \"${jobUrlWithoutView}\")",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[Build Status](${publicBadge} \"${jobUrlWithoutView}\")",class:"select-all")

        h2(_("Embeddable Build Status Text"))
        p(raw(_("blurb_text")))

        h3(_("Text Only"))
        b {text(_("protected (with view)"))}
        input(type:"text",value:textUrlWithView,class:"select-all")
        b {text(_("protected (without view)"))}
        input(type:"text",value:textUrlWithoutView,class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:publicText,class:"select-all")
    }
}
