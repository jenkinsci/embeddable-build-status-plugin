package org.jenkinsci.plugins.badge;


import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.SecurityRealm;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.PresetData;

import org.jenkinsci.plugins.badge.actions.PublicBuildStatusAction;

/**
 * Just the fun bits: check that actions register correctly
 */
public class WorkflowBadgeActionTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @PresetData(PresetData.DataSet.NO_ANONYMOUS_READACCESS)
    @Test
    public void authenticatedAccess() throws Exception {
        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));

        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login("alice", "alice");

        wc.goTo("buildStatus/icon?job=wf", "image/svg+xml");
        job.setQuietPeriod(0);
        job.scheduleBuild();
        j.waitUntilNoActivityUpTo(5000);
        wc.goTo("buildStatus/icon?job=wf&build=1", "image/svg+xml");
    }

    @Test
    public void anonymousViewStatusAccess() throws Exception {
        // Allows anonymous access at security realm level
        final SecurityRealm realm = j.createDummySecurityRealm();
        j.jenkins.setSecurityRealm(realm);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        auth.add(PublicBuildStatusAction.VIEW_STATUS, "anonymous");
        j.getInstance().setSecurityRealm(realm);
        j.getInstance().setAuthorizationStrategy(auth);

        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.goTo("buildStatus/icon?job=wf&", "image/svg+xml");
    }

    @PresetData(PresetData.DataSet.ANONYMOUS_READONLY)
    @Test
    public void anonymousRead() throws Exception {
        WorkflowJob job = j.getInstance().createProject(WorkflowJob.class, "wf");
        job.setDefinition(new CpsFlowDefinition("println('hello')"));
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.goTo("buildStatus/icon?job=wf&", "image/svg+xml");
    }
}
