package org.jenkinsci.plugins.badge;

import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.util.HttpResponses;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;

/**
* @author Kohsuke Kawaguchi
*/
public class BadgeAction implements Action {
    public final AbstractProject project;
    public BadgeAction(AbstractProject project) {
        this.project = project;
    }

    public String getIconFileName() {
        return Jenkins.RESOURCE_PATH+"/plugin/gripper/images/24x24/shield.png";
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
    public HttpResponse doIcon() {
        String file;
        switch (project.getIconColor().noAnime()) {
        case RED:
        case ABORTED:
            file = "failure.png";
            break;
        case YELLOW:
            file = "unstable.png";
            break;
        case BLUE:
            file = "success.png";
            break;
        default:
            file = "running.png";
            break;
        }

        return HttpResponses.redirectViaContextPath(Jenkins.RESOURCE_PATH + "/plugin/gripper/status/" + file);
    }
}
