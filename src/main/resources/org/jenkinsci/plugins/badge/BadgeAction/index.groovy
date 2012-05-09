package org.jenkinsci.plugins.badge.BadgeAction

def l = namespace(lib.LayoutTagLib)
def st = namespace("jelly:stapler")

l.layout {
    l.main_panel {
        h2(_("Embeddable Build Status Icon"))
        p(_("blurb"))
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

        def base =  "${app.rootUrl}job/${my.project.parent.name}/job/${my.project.name}/";
        def badge = base + "badge/icon"
        h3 {
            text(_("Image"))
            img(id:"badge",src:badge)
        }
        input(type:"text",value:badge,class:"select-all")

        h3(_("Markdown"))
        input(type:"text",value:"[![Build Status](${badge})](${base})",class:"select-all")
    }
}