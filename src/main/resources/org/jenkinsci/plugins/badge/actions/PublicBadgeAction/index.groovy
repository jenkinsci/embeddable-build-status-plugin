package org.jenkinsci.plugins.badge.JobBadgeAction

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

        def rootUrl =  "${app.rootUrl}";
        def publicBadgeIcon = "${app.rootUrl}badge/icon";
        def publicBadgeIconSvg = "${publicBadgeIcon}.svg";

        h3(_("flat"))
        img(src:publicBadgeIcon)
        raw ("<br/>")
        img(src:publicBadgeIcon + "?subject=Custom%20Subject&status=Any%20State&color=darkturquoise",
            title:publicBadgeIcon + "?subject=Custom%20Subject&status=Any%20State&color=darkturquoise")
        raw ("<br/>")
        img(src:publicBadgeIcon + "?subject=Custom%20Subject&status=Any%20State&color=darkturquoise&animatedOverlayColor=blue",
            title:publicBadgeIcon + "?subject=Custom%20Subject&status=Any%20State&color=darkturquoise&animatedOverlayColor=blue")
        h3(_("flat-square: "))
        img(src:publicBadgeIcon + "?style=flat-square")
        raw ("<br/>")
        img(src:publicBadgeIcon + "?style=flat-square&subject=Custom%20Subject&status=Any%20State&color=darkturquoise",
            title:publicBadgeIcon + "?style=flat-square&subject=Custom%20Subject&status=Any%20State&color=darkturquoise")
        raw ("<br/>")
        img(src:publicBadgeIcon + "?style=flat-square&subject=Custom%20Subject&status=Any%20State&color=darkturquoise&animatedOverlayColor=blue",
            title:publicBadgeIcon + "?style=flat-square&subject=Custom%20Subject&status=Any%20State&color=darkturquoise&animatedOverlayColor=blue")
        h3(_("plastic: "))
        img(src:publicBadgeIcon + "?style=plastic")
        raw ("<br/>")
        img(src:publicBadgeIcon + "?style=plastic&subject=Custom%20Subject&status=Any%20State&color=darkturquoise",
            title:publicBadgeIcon + "?style=plastic&subject=Custom%20Subject&status=Any%20State&color=darkturquoise")
        raw ("<br/>")
        img(src:publicBadgeIcon + "?style=plastic&subject=Custom%20Subject&status=Any%20State&color=darkturquoise&animatedOverlayColor=blue",
            title:publicBadgeIcon + "?style=plastic&subject=Custom%20Subject&status=Any%20State&color=darkturquoise&animatedOverlayColor=blue")

    }
}
