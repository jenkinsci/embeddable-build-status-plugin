package org.jenkinsci.plugins.badge.actions;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.jenkinsci.plugins.badge.*;
import org.kohsuke.stapler.WebMethod;

public class RunBadgeAction implements Action {
    public final Run<?, ?> run;
    public final Job<?, ?> project;

    public RunBadgeAction(Run run) {
        this.run = run;
        this.project = run.getParent();
    }

    public String getIconFileName() {
        return Jenkins.RESOURCE_PATH+"/plugin/embeddable-build-status/images/24x24/shield.png";
    }

    public String getDisplayName() {
        return Messages.RunBadgeAction_DisplayName();
    }

    public String getUrlName() {
        return "badge";
    }

    /**
     * Serves the badge image.
     */
    @WebMethod(name = "icon")
    public HttpResponse doIcon(@QueryParameter String style, @QueryParameter String subject, @QueryParameter String status, @QueryParameter String color, @QueryParameter String animatedOverlayColor, @QueryParameter String config) {
        return PluginImpl.iconRequestHandler.handleIconRequestForRun(run, style, subject, status, color, animatedOverlayColor, config);
    }

    @WebMethod(name = "icon.svg")
    public HttpResponse doIconDotSvg(@QueryParameter String style, @QueryParameter String subject, @QueryParameter String status, @QueryParameter String color, @QueryParameter String animatedOverlayColor, @QueryParameter String config) {
        return doIcon(style, subject, status, color, animatedOverlayColor, config);
    }

    /**
     * Serves text.
     */
    public String doText() {
        return run.getIconColor().getDescription();
    }
}
