package org.jenkinsci.plugins.badge;

import hudson.model.*;
import hudson.plugins.git.Branch;
import hudson.plugins.git.util.BuildData;
import jenkins.model.Jenkins;

import java.util.Collection;

public class GitScmSupport {
    public static BallColor getStatusForBranch(AbstractProject<?, ?> project, String branchName) {
        if (Jenkins.getInstance().getPlugin("git") == null) {
            return null;
        }

        Collection<?extends AbstractBuild> runs = project._getRuns().values();

        for(Run run : runs) {
            BuildData buildData = run.getAction(BuildData.class);

            if(buildData != null) {
                Branch branch = buildData.lastBuild.getRevision().getBranches().iterator().next();

                // clean up the branch name to only include the actual branch name
                // this value can contain references to the remote etc.
                String runBranchName = branch.getName().replaceFirst(".*/", "");

                if(runBranchName.equals(branchName)) {
                    return run.getIconColor();
                }
            }
        }

        return null;
    }
}