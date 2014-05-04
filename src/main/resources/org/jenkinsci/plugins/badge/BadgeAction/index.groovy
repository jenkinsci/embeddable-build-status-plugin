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
        height: 18px;
    }
    h3 p.note {
        font-style: italic;
        font-size: 0.7em;
        font-weight: normal;
        display: inline !important;
        margin-left: 10px;
    }
</style>
""")

        def base =  "${app.rootUrl}${my.project.url}";
        def badge = base + "badge/icon"
        
        def fullJobName = h.escape(my.project.fullName);
        def publicbadge = "${app.rootUrl}buildStatus/icon?job=${fullJobName}";
        h3 {
            text(_("Image"))
            img(id:"badge",src:badge)
        }
        b {text(_("protected"))}
        input(type:"text",value:badge,class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:publicbadge,class:"select-all")

        h3 {
            text(_("Markdown"))
            p(_("markdown_note"),class:"note")
        }
        b {text(_("protected"))}
        input(type:"text",value:"[![Build Status](${badge})](${base})",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[![Build Status](${publicbadge})](${base})",class:"select-all")

        h3(_("HTML"))
        b {text(_("protected"))}
        input(type:"text",value:"<a href='${base}'><img src='${badge}' height='18' /></a>",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"<a href='${base}'><img src='${publicbadge}' height='18' /></a>",class:"select-all")

        h3(_("Confluence"))
        b {text(_("protected"))}
        input(type:"text",value:"[!${badge}!height=18px|${base}]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[!${publicbadge}!height=18px|${base}]",class:"select-all")

        h3(_("XWiki"))
        b {text(_("protected"))}
        input(type:"text",value:"[[image:${badge}>>${base}||height='18' target='__new']]",class:"select-all")
        b {text(_("unprotected"))}
        input(type:"text",value:"[[image:${publicbadge}>>${base}||height='18' target='__new']]",class:"select-all")
    }
}
