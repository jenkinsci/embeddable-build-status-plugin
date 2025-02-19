package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import hudson.ExtensionList;
import hudson.model.BallColor;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import hudson.model.Run;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.jenkinsci.plugins.badge.IconRequestHandler;
import org.jenkinsci.plugins.badge.extensionpoints.JobSelectorExtensionPoint;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;
import org.mockito.MockedStatic;

public class PublicBuildStatusActionTest {

    // JenkinsRule startup cost is high on Windows
    // Use a ClassRule to create one JenkinsRule used by all tests
    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    @Rule
    public TestName name = new TestName();

    private static final String SUCCESS_MARKER = "fill=\"#44cc11\"";
    private static final String NOT_RUN_MARKER = "fill=\"#9f9f9f\"";
    private static final String PASSING_MARKER = ">passing<";

    private FreeStyleProject job;
    private String jobStatusUrl;

    @Before
    public void createJob() throws IOException {
        // Give each job a name based on the name of the test method
        // Simplifies debugging and failure diagnosis
        // Also avoids any caching from reusing job name
        job = j.createFreeStyleProject("job-" + name.getMethodName());
        // Assure the job can pass on Windows and Unix
        job.getBuildersList()
                .add(
                        isWindows()
                                ? new BatchFile("echo hello from a batch file")
                                : new Shell("echo hello from a shell"));
        String statusUrl = j.getURL().toString() + "buildStatus/icon";
        jobStatusUrl = statusUrl + "?job=" + job.getName();
    }

    @Test
    public void testDoIconJobBefore() throws Exception {
        // Check job status icon is "not run" before job runs
        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            JenkinsRule.JSONWebResponse json = webClient.getJSON(jobStatusUrl);
            String result = json.getContentAsString();
            assertThat(result, containsString("<svg "));
            assertThat(result, not(containsString(SUCCESS_MARKER)));
            assertThat(result, containsString(NOT_RUN_MARKER));
        }
    }

    @Test
    public void testDoIconBuildBefore() throws Exception {
        String buildStatusUrl = jobStatusUrl + "&build=123";

        // Check build status icon is "not run" before job runs
        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            JenkinsRule.JSONWebResponse json = webClient.getJSON(buildStatusUrl);
            String result = json.getContentAsString();
            assertThat(result, containsString("<svg "));
            assertThat(result, not(containsString(SUCCESS_MARKER)));
            assertThat(result, containsString(NOT_RUN_MARKER));
        }
    }

    @Test
    public void testDoIconJobAfter() throws Exception {
        // Run the job, assert that it was successful
        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);

        // Check job status icon is correct after job runs successfully
        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            JenkinsRule.JSONWebResponse json = webClient.getJSON(jobStatusUrl);
            String result = json.getContentAsString();
            assertThat(result, containsString("<svg "));
            assertThat(result, containsString(SUCCESS_MARKER));
            assertThat(result, not(containsString(NOT_RUN_MARKER)));
        }
    }

    @Test
    public void testDoIconBuildAfter() throws Exception {
        // Run the job, assert that it was successful
        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);

        // Check build status icon is correct after job runs successfully
        String buildStatusUrl = jobStatusUrl + "&build=" + build.getNumber();
        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            JenkinsRule.JSONWebResponse json = webClient.getJSON(buildStatusUrl);
            String result = json.getContentAsString();
            assertThat(result, containsString("<svg "));
            assertThat(result, containsString(SUCCESS_MARKER));
            assertThat(result, not(containsString(NOT_RUN_MARKER)));
        }
    }

    @Test
    public void testGetUrlName() throws IOException {
        PublicBuildStatusAction action = new PublicBuildStatusAction();
        assertThat(action.getUrlName(), is("buildStatus"));
    }

    @Test
    public void testGetIconFileName() throws IOException {
        PublicBuildStatusAction action = new PublicBuildStatusAction();
        assertThat(action.getIconFileName(), is(nullValue()));
    }

    @Test
    public void testGetDisplayName() throws IOException {
        PublicBuildStatusAction action = new PublicBuildStatusAction();
        assertThat(action.getDisplayName(), is(nullValue()));
    }

    private boolean isWindows() {
        return File.pathSeparatorChar == ';';
    }

    @Test
    public void doText_shouldReturnMissingQueryParameterWhenJobIsNull() throws IOException {
        PublicBuildStatusAction action = new PublicBuildStatusAction();
        String result = action.doText(null, null, null, "123");
        assertThat(result, is("Missing query parameter: job"));
    }

    @Test
    public void doText_shouldReturnProjectIconWhenJobHasNotRun() throws IOException {
        PublicBuildStatusAction action = new PublicBuildStatusAction();
        String result = action.doText(null, null, job.getName(), null);
        assertThat(result, is(job.getIconColor().getDescription()));
        assertThat(result, is("Not built"));
    }

    @Test
    public void doText_shouldReturnProjectIconColorDescription() throws Exception {
        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);
        String result =
                new PublicBuildStatusAction().doText(null, null, job.getName(), String.valueOf(build.getNumber()));
        assertThat(result, is(job.getIconColor().getDescription()));
        assertThat(result, is("Success"));
    }

    @Test
    public void doText_shouldReturnRunIconColorDescription() throws Exception {
        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);
        String result =
                new PublicBuildStatusAction().doText(null, null, job.getName(), String.valueOf(build.getNumber()));
        assertThat(result, is(build.getIconColor().getDescription()));
        assertThat(result, is("Success"));
    }

    @Test
    public void doIconShouldReturnCorrectResponseForNullJob() throws Exception {
        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            String url = j.getURL().toString() + "buildStatus/icon";
            JenkinsRule.JSONWebResponse json = webClient.getJSON(url);
            String result = json.getContentAsString();
            // Surprising that build passing is reported without a job argument, but
            // that is the result with the current release
            assertThat(result, containsString(PASSING_MARKER));
        }
    }

    @Test
    public void doIconDotSvgShouldReturnCorrectResponseForNullJob() throws Exception {
        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            String url = j.getURL().toString() + "buildStatus/icon.svg";
            JenkinsRule.JSONWebResponse json = webClient.getJSON(url);
            String result = json.getContentAsString();
            // Surprising that build passing is reported without a job argument, but
            // that is the result with the current release
            assertThat(result, containsString(PASSING_MARKER));
        }
    }

    @Test
    public void doIconShouldReturnCorrectResponseForValidJob() throws Exception {
        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);
        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            String url = j.getURL().toString() + "buildStatus/icon?job=" + job.getName();
            JenkinsRule.JSONWebResponse json = webClient.getJSON(url);
            String result = json.getContentAsString();
            assertThat(result, containsString(PASSING_MARKER));
        }
    }

    @Test
    public void doIconDotSvgShouldReturnCorrectResponseForValidJob() throws Exception {
        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);
        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            String url = j.getURL().toString() + "buildStatus/icon.svg?job=" + job.getName();
            JenkinsRule.JSONWebResponse json = webClient.getJSON(url);
            String result = json.getContentAsString();
            assertThat(result, containsString(PASSING_MARKER));
        }
    }

    @Test
    public void testDoText_WithMultipleJobSelectors() throws Exception {
        // Setup
        StaplerRequest2 req = mock(StaplerRequest2.class);
        StaplerResponse2 rsp = mock(StaplerResponse2.class);
        JobSelectorExtensionPoint firstSelector = mock(JobSelectorExtensionPoint.class);
        JobSelectorExtensionPoint secondSelector = mock(JobSelectorExtensionPoint.class);

        // Using raw type with @SuppressWarnings to avoid generics issues
        @SuppressWarnings({"rawtypes", "unchecked"})
        Job secondJob = mock(Job.class);

        @SuppressWarnings({"rawtypes", "unchecked"})
        Run mockRun = mock(Run.class);

        ExtensionList<JobSelectorExtensionPoint> extensionList = mock(ExtensionList.class);
        when(extensionList.iterator())
                .thenReturn(Arrays.asList(firstSelector, secondSelector).iterator());

        // Mock job's properties
        when(secondJob.getIconColor()).thenReturn(BallColor.BLUE);
        when(secondJob.getLastCompletedBuild()).thenReturn(mockRun);
        when(mockRun.getIconColor()).thenReturn(BallColor.BLUE);

        try (MockedStatic<ExtensionList> mockedExtList = mockStatic(ExtensionList.class)) {
            mockedExtList
                    .when(() -> ExtensionList.lookup(JobSelectorExtensionPoint.class))
                    .thenReturn(extensionList);

            when(firstSelector.select("testJob")).thenReturn(null);
            when(secondSelector.select("testJob")).thenReturn(secondJob);
            when(secondJob.hasPermission(PublicBuildStatusAction.VIEW_STATUS)).thenReturn(true);

            PublicBuildStatusAction action = new PublicBuildStatusAction();

            // Execute
            String result = action.doText(req, rsp, "testJob", null);

            // Verify
            assertNotNull(result);
            assertEquals(BallColor.BLUE.getDescription(), result); // Use the description of BallColor.BLUE
            verify(firstSelector).select("testJob");
            verify(secondSelector).select("testJob");
        }
    }

    @Test
    public void testDoText_WhenProjectIsNullOrNoPermission() throws Exception {
        // Setup
        StaplerRequest2 req = mock(StaplerRequest2.class);
        StaplerResponse2 rsp = mock(StaplerResponse2.class);
        JobSelectorExtensionPoint jobSelector = mock(JobSelectorExtensionPoint.class);

        ExtensionList<JobSelectorExtensionPoint> extensionList = mock(ExtensionList.class);
        when(extensionList.iterator()).thenReturn(Arrays.asList(jobSelector).iterator());

        try (MockedStatic<ExtensionList> mockedExtList = mockStatic(ExtensionList.class)) {
            mockedExtList
                    .when(() -> ExtensionList.lookup(JobSelectorExtensionPoint.class))
                    .thenReturn(extensionList);

            PublicBuildStatusAction action = new PublicBuildStatusAction();

            // Case 1: p == null → should throw HttpResponses.notFound()
            when(jobSelector.select("unknownJob")).thenReturn(null);
            assertThrows(HttpResponses.HttpResponseException.class, () -> action.doText(req, rsp, "unknownJob", null));

            // Case 2: !p.hasPermission(VIEW_STATUS) → should throw HttpResponses.notFound()
            @SuppressWarnings({"rawtypes", "unchecked"})
            Job mockJob = mock(Job.class);
            when(jobSelector.select("testJob")).thenReturn(mockJob);
            when(mockJob.hasPermission(PublicBuildStatusAction.VIEW_STATUS)).thenReturn(false);

            assertThrows(HttpResponses.HttpResponseException.class, () -> action.doText(req, rsp, "testJob", null));
        }
    }

    @Test
    public void testDoIcon_WhenProjectIsNotFoundOrNoPermission() throws Exception {
        // Setup
        StaplerRequest2 req = mock(StaplerRequest2.class);
        StaplerResponse2 rsp = mock(StaplerResponse2.class);
        JobSelectorExtensionPoint jobSelector = mock(JobSelectorExtensionPoint.class);
        IconRequestHandler iconHandler = mock(IconRequestHandler.class);

        ExtensionList extensionList = mock(ExtensionList.class);
        when(extensionList.iterator()).thenReturn(Arrays.asList(jobSelector).iterator());

        try (MockedStatic<ExtensionList> mockedExtList = mockStatic(ExtensionList.class)) {
            mockedExtList
                    .when(() -> ExtensionList.lookup(JobSelectorExtensionPoint.class))
                    .thenReturn(extensionList);

            PublicBuildStatusAction action = new PublicBuildStatusAction();

            // Test case where job exists but user lacks VIEW_STATUS permission
            Job mockJob = mock(Job.class);
            when(jobSelector.select("testJob")).thenReturn(mockJob);
            when(mockJob.hasPermission(PublicBuildStatusAction.VIEW_STATUS)).thenReturn(false);

            // This should trigger the return null case in getProject() since throwErrorWhenNotFound is false
            HttpResponse response = action.doIcon(
                    req,
                    rsp,
                    "testJob", // job name
                    null, // build
                    "style",
                    "subject",
                    "status",
                    "color",
                    "animatedColor",
                    "config",
                    "link");

            // Verify we got a response (since null project should still return a response)
            assertThat(response, is(not(nullValue())));
        }
    }
}
