/*
 * The MIT License
 *
 * Copyright 2023 Mark Waite.
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
package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.FreeStyleProject;
import hudson.model.Result;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JobBadgeActionTest {

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    private static final String NOT_BUILT_JOB_NAME = "not-built-job";
    private static JobBadgeAction notBuiltAction;

    private static final String SUCCESSFUL_JOB_NAME = "successful-job";
    private static final String BUILD_AND_RUN_MARKER = "fill=\"#44cc11\"";
    private static final String BUILD_NOT_RUN_MARKER = "fill=\"#9f9f9f\"";
    private static JobBadgeAction successfulAction;
    private static String jenkinsUrl;
    private static String badgeUrl;
    private static JobBadgeAction jobBadgeActionBuildAndRun;
    private JenkinsRule.WebClient webClient;

    public JobBadgeActionTest() {}

    @BeforeClass
    public static void createAction() throws Exception {
        /* Build a job for assertions on successful jobs */
        FreeStyleProject successfulJob = j.createFreeStyleProject(SUCCESSFUL_JOB_NAME);
        j.buildAndAssertStatus(Result.SUCCESS, successfulJob);
        successfulAction = new JobBadgeAction(successfulJob);

        /* Define a job and do not build it for assertions on jobs that have not been built */
        FreeStyleProject notBuiltJob = j.createFreeStyleProject(NOT_BUILT_JOB_NAME);
        notBuiltAction = new JobBadgeAction(notBuiltJob);
    }

    @Before
    public void createWebClient() {
        webClient = j.createWebClient();
    }

    @Before
    public void setUpActionForBadgeToBuildAndRun() throws IOException {
        // Create an instance of JobBadgeAction
        jobBadgeActionBuildAndRun = new JobBadgeAction(successfulAction.project);

        // Get the Jenkins URL
        jenkinsUrl = j.getURL().toString() + "job/" + jobBadgeActionBuildAndRun.getUrlEncodedFullName() + "/" + "badge/icon";
        badgeUrl = jenkinsUrl + "?build=1&style=style&subject=subject&status=status&color=green&config=config&animatedOverlayColor=animatedOverlayColor&link=link";

        // Open a connection to the badge URL
        URL url = new URL(badgeUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

    }

    @Test
    public void testIconBuildAndRun() throws Exception {
        // Check job status icon is "run" before job runs
        // Read the response from the connection
        JenkinsRule.JSONWebResponse json = webClient.getJSON(badgeUrl);
        String result = json.getContentAsString();
        assertThat(result, CoreMatchers.containsString("<svg "));
        System.out.println(result);
        assertThat(result, not(CoreMatchers.containsString(BUILD_NOT_RUN_MARKER)));
        assertThat(result, CoreMatchers.containsString(BUILD_AND_RUN_MARKER));
    }

    @Test
    public void testIconBuildNotRun() throws Exception {
        // Check job status icon is "not run" before job runs
        JenkinsRule.JSONWebResponse json = webClient.getJSON(badgeUrl);
        String result = json.getContentAsString();
        assertThat(result, CoreMatchers.containsString("<svg "));
        assertThat(result, not(CoreMatchers.containsString(BUILD_AND_RUN_MARKER)));
        assertThat(result, CoreMatchers.containsString(BUILD_NOT_RUN_MARKER));
    }

        @Test
        public void testGetIconFileName () {
            assertThat(notBuiltAction.getIconFileName(), is(nullValue()));
            assertThat(successfulAction.getIconFileName(), is(nullValue()));
        }

        @Test
        public void testGetIconClassName () {
            assertThat(notBuiltAction.getIconClassName(), is("symbol-shield-outline plugin-ionicons-api"));
            assertThat(successfulAction.getIconClassName(), is("symbol-shield-outline plugin-ionicons-api"));
        }

        @Test
        public void testGetDisplayName () {
            assertThat(notBuiltAction.getDisplayName(), is("Embeddable Build Status"));
            assertThat(successfulAction.getDisplayName(), is("Embeddable Build Status"));
        }

        @Test
        public void testGetUrlName () {
            assertThat(notBuiltAction.getUrlName(), is("badge"));
            assertThat(successfulAction.getUrlName(), is("badge"));
        }

        @Test
        public void testGetUrl () {
            assertThat(notBuiltAction.getUrl(), is(""));
            assertThat(successfulAction.getUrl(), is(""));
        }

        @Test
        public void testGetUrlEncodedFullName () {
            assertThat(notBuiltAction.getUrlEncodedFullName(), is(NOT_BUILT_JOB_NAME));
            assertThat(successfulAction.getUrlEncodedFullName(), is(SUCCESSFUL_JOB_NAME));
        }

        @Test
        public void testDoText () {
            assertThat(notBuiltAction.doText(), is("Not built"));
            assertThat(successfulAction.doText(), is("Success"));
    }
}
