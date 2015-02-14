package org.jenkinsci.plugins.badge.BadgeAction

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
        def jobUrlWithView =  "${app.rootUrl}${my.project.url}";
        def jobUrlWithoutView =  "${app.rootUrl}job/${fullJobName}";
        def badgeUrlWithView = jobUrlWithView + "badge/icon"
        def badgeUrlWithoutView = jobUrlWithoutView + "/badge/icon"
        def publicBadge = "${app.rootUrl}buildStatus/icon?job=${fullJobName}";


        h3 {
            text(_("Image"))
            img(id:"badgeUrlWithView",src:badgeUrlWithView)
            text(_(" or "))
            img(src:badgeUrlWithView + "?style=plastic")
        }

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
    }
}
