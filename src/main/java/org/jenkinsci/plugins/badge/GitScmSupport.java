package org.jenkinsci.plugins.badge;

import hudson.model.*;
import hudson.plugins.git.Branch;
import hudson.plugins.git.util.BuildData;
import jenkins.model.Jenkins;

public class GitScmSupport {
    public static BallColor getStatusForBranch(AbstractProject project, String branchName) {
        if (Jenkins.getInstance().getPlugin("git") == null) {
            return null;
        }

        RunMap<?> runs = project._getRuns();

        for(Run run : runs) {
            BuildData buildData = run.getAction(BuildData.class);

            if(buildData != null) {
                Branch branch = buildData.lastBuild.getRevision().getBranches().iterator().next();
                if(branch.getName().equals("refs/remotes/origin/" + branchName)) {
                    return run.getIconColor();
                }
            }
        }

        return null;
    }
}
