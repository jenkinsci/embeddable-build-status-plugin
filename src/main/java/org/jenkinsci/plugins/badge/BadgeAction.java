package org.jenkinsci.plugins.badge;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BallColor;
import hudson.plugins.git.Branch;
import hudson.plugins.git.util.BuildData;
import hudson.util.HttpResponses;
import hudson.util.IOUtils;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static javax.servlet.http.HttpServletResponse.SC_NOT_MODIFIED;

/**
* @author Kohsuke Kawaguchi
*/
public class BadgeAction extends AbstractBadgeAction implements Action {
    private final BadgeActionFactory factory;
    public final AbstractProject project;

    public BadgeAction(BadgeActionFactory factory, AbstractProject project) {
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
    public HttpResponse doIcon(@QueryParameter("branch") String branchName) {
        return respondWithStatusImageOr404(project, branchName);
    }

    @Override
    StatusImage createStatusImage(BallColor status) {
        return factory.getImage(status);
    }
}
