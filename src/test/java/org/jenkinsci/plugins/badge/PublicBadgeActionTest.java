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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hudson.model.AbstractProject;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.RunMap;
import hudson.plugins.git.Branch;
import hudson.plugins.git.Revision;
import hudson.plugins.git.util.Build;
import hudson.plugins.git.util.BuildData;
import hudson.security.GlobalMatrixAuthorizationStrategy;
import hudson.security.SecurityRealm;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.util.Arrays;

import org.eclipse.jgit.lib.ObjectId;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.recipes.PresetData;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import org.springframework.util.ReflectionUtils;

/**
 * @author Dominik Bartholdi (imod)
 */
public class PublicBadgeActionTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @PresetData(PresetData.DataSet.NO_ANONYMOUS_READACCESS)
    @Test
    public void authenticatedAccess() throws Exception {
        j.createFreeStyleProject("free");
        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login("alice", "alice");
        try {
            // try with wrong job name
            wc.goTo("buildStatus/icon?job=dummy");
            fail("should fail, because there is no job with this name");
        } catch (FailingHttpStatusCodeException x) {
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }
        wc.goTo("buildStatus/icon?job=free", "image/svg+xml");
    }

    @PresetData(PresetData.DataSet.NO_ANONYMOUS_READACCESS)
    @Test
    public void authenticatedBranchSpecified() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("free");
        addBuildToProject(buildForBranch(project, "develop"));

        JenkinsRule.WebClient wc = j.createWebClient();
        wc.login("alice", "alice");

        wc.goTo("buildStatus/icon?job=free&branch=develop", "image/svg+xml");
    }

    @PresetData(PresetData.DataSet.NO_ANONYMOUS_READACCESS)
    @Test
    public void invalidAnonymousAccess() throws Exception {
        j.createFreeStyleProject("free");
        JenkinsRule.WebClient wc = j.createWebClient();
        try {
            // try with wrong job name
            wc.goTo("buildStatus/icon?job=dummy");
            fail("should fail, because there is no job with this name");
        } catch (FailingHttpStatusCodeException x) {
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }

        try {
            // try with correct job name
            wc.goTo("buildStatus/icon?job=free", "image/svg+xml");
            fail("should fail, because there is no job with this name");
        } catch (FailingHttpStatusCodeException x) {
            // make sure return code does not leak security relevant information (must 404)
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }
    }

    @Test
    public void validAnonymousViewStatusAccess() throws Exception {

        final SecurityRealm realm = j.createDummySecurityRealm();
        j.jenkins.setSecurityRealm(realm);
        GlobalMatrixAuthorizationStrategy auth = new GlobalMatrixAuthorizationStrategy();
        auth.add(PublicBadgeAction.VIEW_STATUS, "anonymous");
        j.getInstance().setSecurityRealm(realm);
        j.getInstance().setAuthorizationStrategy(auth);

        j.createFreeStyleProject("free");

        JenkinsRule.WebClient wc = j.createWebClient();
        try {
            // try with wrong job name
            wc.goTo("buildStatus/icon?job=dummy");
            fail("should fail, because there is no job with this name");
        } catch (FailingHttpStatusCodeException x) {
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }

        wc.goTo("buildStatus/icon?job=free", "image/svg+xml");
    }

    @PresetData(PresetData.DataSet.ANONYMOUS_READONLY)
    @Test
    public void validAnonymousAccess() throws Exception {
        j.createFreeStyleProject("free");
        JenkinsRule.WebClient wc = j.createWebClient();
        try {
            // try with wrong job name
            wc.goTo("buildStatus/icon?job=dummy");
            fail("should fail, because there is no job with this name");
        } catch (FailingHttpStatusCodeException x) {
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }

        // try with correct job name
        wc.goTo("buildStatus/icon?job=free", "image/svg+xml");
    }

    @PresetData(PresetData.DataSet.ANONYMOUS_READONLY)
    @Test
    public void specifyExistingBranch() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("free");
        addBuildToProject(buildForBranch(project, "develop"));

        JenkinsRule.WebClient wc = j.createWebClient();
        wc.goTo("buildStatus/icon?job=free&branch=develop", "image/svg+xml");
    }

    @PresetData(PresetData.DataSet.NO_ANONYMOUS_READACCESS)
    @Test
    public void specifyNonExistingBranch() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject("free");
        addBuildToProject(buildForBranch(project, "develop"));

        JenkinsRule.WebClient wc = j.createWebClient();

        try {
            wc.goTo("buildStatus/icon?job=free&branch=foobar", "image/svg+xml");
        } catch (FailingHttpStatusCodeException x) {
            assertEquals(HttpURLConnection.HTTP_NOT_FOUND, x.getStatusCode());
        }
    }

    @SuppressWarnings("unchecked")
    private void addBuildToProject(FreeStyleBuild build) throws Exception {
        Field field = AbstractProject.class.getDeclaredField("builds");
        ReflectionUtils.makeAccessible(field);

        RunMap<FreeStyleBuild> runMap = (RunMap<FreeStyleBuild>)ReflectionUtils.getField(field, build.getParent());
        runMap.put(build);
    }

    private FreeStyleBuild buildForBranch(FreeStyleProject project, String branchName) throws IOException {
        FreeStyleBuild build = new FreeStyleBuild(project);

        BuildData buildData = new BuildData();
        build.addAction(buildData);

        Branch branch = new Branch("refs/remotes/origin/" + branchName, SOME_SHA1);
        Revision revision = new Revision(SOME_SHA1, Arrays.asList(branch));
        buildData.lastBuild = new Build(revision, 1, null);

        return build;
    }

    private static final ObjectId SOME_SHA1 = ObjectId.fromString("508911b993b97ee92c383a0df9cc13abe606d037");
}
