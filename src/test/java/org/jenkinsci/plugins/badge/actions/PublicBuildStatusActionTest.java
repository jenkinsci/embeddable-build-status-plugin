package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import hudson.ExtensionList;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.security.AuthorizationStrategy;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import java.io.File;
import java.io.IOException;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.badge.extensionpoints.JobSelectorExtensionPoint;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.JenkinsRule.DummySecurityRealm;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;
import org.kohsuke.stapler.Stapler;

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

    // Helpers method
    private void setupSecurity(String username) {
        DummySecurityRealm securityRealm = j.createDummySecurityRealm();
        MockAuthorizationStrategy authStrategy = new MockAuthorizationStrategy()
                .grant(Jenkins.READ, Item.READ)
                .everywhere()
                .to("user")
                .grant(Jenkins.READ)
                .everywhere()
                .to("user")
                .grant(Item.READ)
                .everywhere()
                .to("user")
                .grant(PublicBuildStatusAction.VIEW_STATUS)
                .everywhere()
                .to("admin");

        j.jenkins.setSecurityRealm(securityRealm);
        j.jenkins.setAuthorizationStrategy(authStrategy);
    }

    private void cleanupSecurity(AuthorizationStrategy originalStrategy, User user) throws IOException {
        j.jenkins.setAuthorizationStrategy(originalStrategy);
        j.jenkins.setSecurityRealm(null);
        if (user != null) {
            user.delete();
        }
    }

    @Test
    public void testDoText_WithMultipleJobSelectors() throws Exception {
        JobSelectorExtensionPoint nullSelector = new JobSelectorExtensionPoint() {
            @Override
            public Job<?, ?> select(String jobName) {
                return null;
            }
        };

        JobSelectorExtensionPoint validSelector = new JobSelectorExtensionPoint() {
            @Override
            public Job<?, ?> select(String jobName) {
                return job;
            }
        };

        ExtensionList<JobSelectorExtensionPoint> extensionsList = ExtensionList.lookup(JobSelectorExtensionPoint.class);
        extensionsList.add(0, nullSelector);
        extensionsList.add(1, validSelector);

        try {
            String result = new PublicBuildStatusAction().doText(null, null, job.getName(), null);
            assertNotNull(result);
            assertEquals("Not built", result);
            assertNull(nullSelector.select(job.getName()));
            assertEquals(job, validSelector.select(job.getName()));
        } finally {
            extensionsList.remove(nullSelector);
            extensionsList.remove(validSelector);
        }
    }

    @Test
    public void testDoText_whenJobHasNoPermissions() throws Exception {
        JobSelectorExtensionPoint jobSelector = new JobSelectorExtensionPoint() {
            @Override
            public Job<?, ?> select(String jobName) {
                return job;
            }
        };
        ExtensionList<JobSelectorExtensionPoint> extensionList = ExtensionList.lookup(JobSelectorExtensionPoint.class);
        extensionList.add(0, jobSelector);

        AuthorizationStrategy originalStrategy = j.jenkins.getAuthorizationStrategy();
        setupSecurity("alice");
        User alice = j.jenkins.getUser("alice");

        try (ACLContext c = ACL.as(User.getById("alice", true))) {
            assertThrows(HttpResponses.HttpResponseException.class, () -> new PublicBuildStatusAction()
                    .doText(null, null, job.getName(), null));
        } finally {
            extensionList.remove(jobSelector);
            cleanupSecurity(originalStrategy, alice);
        }
    }

    @Test
    public void testDoIcon_WhenJobHasNoPermissions() throws Exception {
        JobSelectorExtensionPoint jobSelector = new JobSelectorExtensionPoint() {
            @Override
            public Job<?, ?> select(String jobName) {
                return job;
            }
        };
        ExtensionList<JobSelectorExtensionPoint> extensionList = ExtensionList.lookup(JobSelectorExtensionPoint.class);
        extensionList.add(0, jobSelector);

        AuthorizationStrategy originalStrategy = j.jenkins.getAuthorizationStrategy();
        setupSecurity("alice");
        User alice = j.jenkins.getUser("alice");

        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);

        try (ACLContext c = ACL.as(User.getById("alice", true))) {
            HttpResponse response = new PublicBuildStatusAction()
                    .doIcon(
                            Stapler.getCurrentRequest2(),
                            Stapler.getCurrentResponse2(),
                            job.getName(),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null);
            assertNotNull(response);
        } finally {
            extensionList.remove(jobSelector);
            cleanupSecurity(originalStrategy, alice);
        }
    }
}
