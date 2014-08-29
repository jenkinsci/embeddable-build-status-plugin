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

        def base =  "${app.rootUrl}${my.project.url}";
        def badge = base + "badge/icon"
        
        def fullJobName = h.escape(my.project.fullName);
        def publicbadge = "${app.rootUrl}buildStatus/icon?job=${fullJobName}";
        def publicbadgeWithBranch = "${app.rootUrl}buildStatus/icon?job=${fullJobName}&branch=master";

        h3 {
            text(_("Image"))
            img(id:"badge",src:badge)
        }
        b {text(_("protected"))}
        input(type:"text",value:badge,class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:publicbadge,class:"select-all")
        b {text(_("with branch specified"))}
        input(type:"text",value:publicbadgeWithBranch,class:"select-all")

        h3(_("Markdown"))
        b {text(_("protected"))}
        input(type:"text",value:"[![Build Status](${badge})](${base})",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[![Build Status](${publicbadge})](${base})",class:"select-all")

        h3(_("HTML"))
        b {text(_("protected"))}
        input(type:"text",value:"<a href='${base}'><img src='${badge}'></a>",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"<a href='${base}'><img src='${publicbadge}'></a>",class:"select-all")

        h3(_("Confluence"))
        b {text(_("protected"))}
        input(type:"text",value:"[!${badge}!|${base}]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[!${publicbadge}!|${base}]",class:"select-all")

        h3(_("XWiki"))
        b {text(_("protected"))}
        input(type:"text",value:"[[image:${badge}>>${base}||target='__new']]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[[image:${publicbadge}>>${base}||target='__new']]",class:"select-all")
    }
}
