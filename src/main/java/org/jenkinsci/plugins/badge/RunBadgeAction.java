package org.jenkinsci.plugins.badge;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;

public class RunBadgeAction implements Action {
    private final RunBadgeActionFactory factory;
    public final Run run;
    public final Job project;

    public RunBadgeAction(RunBadgeActionFactory factory, Run run) {
        this.factory = factory;
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
    public HttpResponse doIcon(@QueryParameter String style) {
        return factory.getImage(run.getIconColor(), style);
    }
}
