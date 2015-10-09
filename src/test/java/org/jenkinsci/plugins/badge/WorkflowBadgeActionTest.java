/*
 * The MIT License
 *
 * Copyright 2013 Dominik Bartholdi.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.badge;


import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.SecurityRealm;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.PresetData;


/**
 * Just the fun bits: check that actions register correctly
 * @author Dominik Bartholdi (imod)
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
        Thread.sleep(500);
        wc.goTo("buildStatus/icon?job=wf&build=1", "image/svg+xml");
    }

    @Test
    public void anonymousViewStatusAccess() throws Exception {
        // Allows anonymous access at security realm level
        final SecurityRealm realm = j.createDummySecurityRealm();
        j.jenkins.setSecurityRealm(realm);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        auth.add(PublicBadgeAction.VIEW_STATUS, "anonymous");
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
