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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.FreeStyleProject;
import hudson.model.Result;
import java.util.Random;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class JobBadgeActionTest {

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    private static final String NOT_BUILT_JOB_NAME = "not-built-job";
    private static JobBadgeAction notBuiltAction;

    private static final String SUCCESSFUL_JOB_NAME = "successful-job";
    private static JobBadgeAction successfulAction;

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

    private final Random random = new Random();
    private final String[] styles = {"plastic", "flat", "flat-square"};

    @Test
    public void testBadgeStatus() throws Exception {
        // Create an instance of JobBadgeAction
        JobBadgeAction action = new JobBadgeAction(successfulAction.project);

        String link = "https://jenkins.io";
        String status = "my-status-" + random.nextInt();
        String subject = "my-subject-" + random.nextInt();
        String style = styles[random.nextInt(styles.length)];
        String expectedStyle;
        String unexpectedStyle;
        switch (style) {
            case "plastic":
                expectedStyle = "<stop offset=\".1\"";
                unexpectedStyle = " fill=\"white\" fill-opacity=\"0.1\"/>";
                break;
            case "flat":
                expectedStyle = "<linearGradient id=\"a\" x2=\"0\" y2=\"100%\">";
                unexpectedStyle = "<stop offset=\".1\"";
                break;
            case "flat-square":
                expectedStyle = " fill=\"white\" fill-opacity=\"0.1\"/>";
                unexpectedStyle = "fill-opacity=\".3\">" + subject + "</text>";
                break;
            default:
                expectedStyle = "not-a-valid-style";
                unexpectedStyle = "never-should-be-used";
                break;
        }

        // Get the Jenkins URL
        // icon.svg reaches one branch, icon another, and adding "/1/" reaches a third branch
        String jenkinsUrl = j.getURL().toString() + "job/" + action.getUrlEncodedFullName() + "/badge/icon.svg";
        String badgeUrl = jenkinsUrl + "?subject=" + subject;
        badgeUrl = badgeUrl + "&status=" + status;
        badgeUrl = badgeUrl + "&link=" + link;
        badgeUrl = badgeUrl + "&style=" + style;
        // Null build reaches one branch
        // badgeUrl = badgeUrl + "&build=1";
        badgeUrl = badgeUrl + "&color=orange";
        badgeUrl = badgeUrl + "&config=my-config";
        badgeUrl = badgeUrl + "&animatedOverlayColor=yellow";

        // Check the badge for a job that has been built and run
        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            JenkinsRule.JSONWebResponse json = webClient.getJSON(badgeUrl);
            String result = json.getContentAsString();

            assertThat(result, containsString("<svg "));
            assertThat(result, containsString(link));
            assertThat(result, containsString(status));
            assertThat(result, containsString(subject));
            assertThat("For style " + style, result, containsString(expectedStyle));
            assertThat("For style " + style, result, not(containsString(unexpectedStyle)));
        }
    }

    @Test
    public void testGetIconFileName() {
        assertThat(notBuiltAction.getIconFileName(), is(nullValue()));
        assertThat(successfulAction.getIconFileName(), is(nullValue()));
    }

    @Test
    public void testGetIconClassName() {
        assertThat(notBuiltAction.getIconClassName(), is("symbol-shield-outline plugin-ionicons-api"));
        assertThat(successfulAction.getIconClassName(), is("symbol-shield-outline plugin-ionicons-api"));
    }

    @Test
    public void testGetDisplayName() {
        assertThat(notBuiltAction.getDisplayName(), is("Embeddable Build Status"));
        assertThat(successfulAction.getDisplayName(), is("Embeddable Build Status"));
    }

    @Test
    public void testGetUrlName() {
        assertThat(notBuiltAction.getUrlName(), is("badge"));
        assertThat(successfulAction.getUrlName(), is("badge"));
    }

    @Test
    public void testGetUrl() {
        assertThat(notBuiltAction.getUrl(), is(""));
        assertThat(successfulAction.getUrl(), is(""));
    }

    @Test
    public void testGetUrlEncodedFullName() {
        assertThat(notBuiltAction.getUrlEncodedFullName(), is(NOT_BUILT_JOB_NAME));
        assertThat(successfulAction.getUrlEncodedFullName(), is(SUCCESSFUL_JOB_NAME));
        assertThat(new JobBadgeAction(null).getUrlEncodedFullName(), is("null-project-no-url-encoded-fullName"));
    }

    @Test
    public void testDoText() {
        assertThat(notBuiltAction.doText(), is("Not built"));
        assertThat(successfulAction.doText(), is("Success"));
    }
}
