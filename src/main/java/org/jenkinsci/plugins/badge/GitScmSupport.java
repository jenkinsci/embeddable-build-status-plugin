package org.jenkinsci.plugins.badge;

import hudson.model.*;
import hudson.plugins.git.Branch;
import hudson.plugins.git.util.BuildData;

import java.util.Collection;

public class GitScmSupport {
    public static BallColor getStatusForBranch(AbstractProject project, String branchName) {
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
