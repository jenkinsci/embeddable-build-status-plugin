package org.jenkinsci.plugins.badge;

import hudson.model.Action;
import hudson.model.Job;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;


/**
* @author Kohsuke Kawaguchi
*/
public class BadgeAction implements Action {
    private final BadgeActionFactory factory;

    public final Job project;

    public BadgeAction(BadgeActionFactory factory, Job project) {
        this.factory = factory;
        this.project = project;
    }

    public String getIconFileName() {
        return Jenkins.RESOURCE_PATH+"/plugin/embeddable-build-status/images/24x24/shield.png";
    }

    public String getDisplayName() {
        return Messages.BadgeAction_DisplayName();
    }

    public String getUrlName() {
        return "badge";
    }

    /**
     * Serves the badge image.
     */
    public HttpResponse doIcon(@QueryParameter String style) {
        return factory.getImage(project.getIconColor(), style);
    }

    /**
     * Serves text.
     */
    public String doText() {
        return project.getIconColor().getDescription();
    }
}
