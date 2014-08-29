package org.jenkinsci.plugins.badge;

import hudson.model.AbstractProject;
import hudson.model.BallColor;
import hudson.util.HttpResponses;
import org.kohsuke.stapler.HttpResponse;

public abstract class AbstractBadgeAction {

    abstract StatusImage createStatusImage(BallColor status);

    protected HttpResponse respondWithStatusImageOr404(AbstractProject project, String branchName) {
        BallColor status;

        if(branchName != null) {
            status = GitScmSupport.getStatusForBranch(project, branchName);
        } else {
            status = project.getIconColor();
        }

        if(status == null) return HttpResponses.notFound();
        else return createStatusImage(status);
    }
}
