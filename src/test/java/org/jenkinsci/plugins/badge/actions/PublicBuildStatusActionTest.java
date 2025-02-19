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
import org.jenkinsci.plugins.badge.extensionpoints.JobSelectorExtensionPoint;
import org.junit.After;
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
    private StaplerRequest2 req;
    private StaplerResponse2 rsp;
    private JobSelectorExtensionPoint jobSelector;
    private ExtensionList<JobSelectorExtensionPoint> extensionList;
    private PublicBuildStatusAction action;
    private MockedStatic<ExtensionList> mockedExtList;

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

    @SuppressWarnings("unchecked")
    private void setupRequiredObjects() throws IOException {
        req = mock(StaplerRequest2.class);
        rsp = mock(StaplerResponse2.class);
        jobSelector = mock(JobSelectorExtensionPoint.class);
        extensionList = mock(ExtensionList.class);
        action = new PublicBuildStatusAction();
    }

    private void setupExtensionList(JobSelectorExtensionPoint... selectors) {
        when(extensionList.iterator()).thenReturn(Arrays.asList(selectors).iterator());
        mockedExtList = mockStatic(ExtensionList.class);
        mockedExtList
                .when(() -> ExtensionList.lookup(JobSelectorExtensionPoint.class))
                .thenReturn(extensionList);
    }

    private Job setupJobSelector(
            JobSelectorExtensionPoint selector, String jobName, BallColor color, boolean hasPermission) {
        @SuppressWarnings("rawtypes")
        Job job = mock(Job.class);
        @SuppressWarnings("rawtypes")
        Run mockRun = mock(Run.class);

        when(job.getIconColor()).thenReturn(color);
        when(job.getLastCompletedBuild()).thenReturn(mockRun);
        when(job.hasPermission(PublicBuildStatusAction.VIEW_STATUS)).thenReturn(hasPermission);
        when(mockRun.getIconColor()).thenReturn(color);
        when(selector.select(jobName)).thenReturn(job);

        return job;
    }

    @After
    public void tearDown() {
        if (mockedExtList != null) {
            mockedExtList.close();
        }
    }

    @Test
    public void testDoText_WithMultipleJobSelectors() throws Exception {
        setupRequiredObjects();
        JobSelectorExtensionPoint secondSelector = mock(JobSelectorExtensionPoint.class);
        setupExtensionList(jobSelector, secondSelector);

        setupJobSelector(secondSelector, "testJob", BallColor.BLUE, true);
        when(jobSelector.select("testJob")).thenReturn(null);

        String result = action.doText(req, rsp, "testJob", null);

        assertNotNull(result);
        assertEquals(BallColor.BLUE.getDescription(), result);
        verify(jobSelector).select("testJob");
        verify(secondSelector).select("testJob");
    }

    @Test
    public void testDoText_WhenProjectIsNullOrNoPermission() throws Exception {
        setupRequiredObjects();
        setupExtensionList(jobSelector);

        when(jobSelector.select("unknownJob")).thenReturn(null);
        assertThrows(HttpResponses.HttpResponseException.class, () -> action.doText(req, rsp, "unknownJob", null));

        @SuppressWarnings({"rawtypes"})
        Job mockJob = setupJobSelector(jobSelector, "testJob", BallColor.BLUE, false);

        assertThrows(HttpResponses.HttpResponseException.class, () -> action.doText(req, rsp, "testJob", null));
    }

    @Test
    public void testDoIcon_WhenProjectIsNotFoundOrNoPermission() throws Exception {
        setupRequiredObjects();
        setupExtensionList(jobSelector);

        @SuppressWarnings({"rawtypes"})
        Job mockJob = setupJobSelector(jobSelector, "testJob", BallColor.BLUE, false);

        HttpResponse response = action.doIcon(
                req, rsp, "testJob", null, "style", "subject", "status", "color", "animatedColor", "config", "link");

        assertNotNull(response);
    }
}
