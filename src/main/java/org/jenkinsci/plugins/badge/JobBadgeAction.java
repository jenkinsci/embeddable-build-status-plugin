package org.jenkinsci.plugins.badge;

import hudson.model.Action;
import hudson.model.Job;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import java.util.List;

/**
* @author Kohsuke Kawaguchi
*/
public class JobBadgeAction implements Action {
    private final JobBadgeActionFactory factory;

    public final Job project;

    public JobBadgeAction(JobBadgeActionFactory factory, Job project) {
        this.factory = factory;
        this.project = project;
    }

    public String getIconFileName() {
        return Jenkins.RESOURCE_PATH+"/plugin/embeddable-build-status/images/24x24/shield.png";
    }

    public String getDisplayName() {
        return Messages.JobBadgeAction_DisplayName();
    }

    public String getUrlName() {
        return "badge";
    }

    /**
     * Serves the badge image.
     */
    public HttpResponse doIcon(@QueryParameter String style, @QueryParameter String subject, @QueryParameter String status, @QueryParameter String color) {
        /*
        List<? extends Action> lst = project.getLastBuild().getAllActions();
        for (Action action : lst) {
            status = action.getDisplayName();
            break;
        }
        */
        return factory.getImage(project.getIconColor(), style, subject, status, color);
    }

    /**
     * Serves text.
     */
    public String doText() {
        return project.getIconColor().getDescription();
    }
}
