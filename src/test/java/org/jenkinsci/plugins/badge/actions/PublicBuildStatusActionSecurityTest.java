package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import hudson.ExtensionList;
import hudson.Functions;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import java.io.IOException;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.badge.extensionpoints.JobSelectorExtensionPoint;
import org.jenkinsci.plugins.badge.extensionpoints.RunSelectorExtensionPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;

@WithJenkins
class PublicBuildStatusActionSecurityTest {

    private JenkinsRule j;
    private FreeStyleProject job;

    @BeforeEach
    void createJob(JenkinsRule j, TestInfo info) throws IOException {
        this.j = j;
        // Give each job a name based on the name of the test method
        // Simplifies debugging and failure diagnosis
        // Also avoids any caching from reusing job name
        job = j.createFreeStyleProject(
                "job-" + info.getTestMethod().orElseThrow().getName());
        // Assure the job can pass on Windows and Unix
        job.getBuildersList()
                .add(
                        Functions.isWindows()
                                ? new BatchFile("echo hello from a batch file")
                                : new Shell("echo hello from a shell"));
    }

    @BeforeEach
    void setupSecurity() {
        JenkinsRule.DummySecurityRealm securityRealm = j.createDummySecurityRealm();
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

    @Test
    void testDoText_whenJobHasNoPermissions() {
        JobSelectorExtensionPoint jobSelector = (String jobName) -> job;
        ExtensionList.lookup(JobSelectorExtensionPoint.class).add(0, jobSelector);

        try (ACLContext c = ACL.as(User.getById("alice", true))) {
            HttpResponses.HttpResponseException ex =
                    assertThrows(HttpResponses.HttpResponseException.class, () -> new PublicBuildStatusAction()
                            .doText(null, null, job.getName(), null));
            assertThat(ex, instanceOf(HttpResponses.notFound().getClass()));
        }
    }

    @Test
    void testGetRun_whenNotRunAndJobHasNoPermissions() {
        RunSelectorExtensionPoint runSelector = (Job myJob, String selector, Run reference) -> null;
        ExtensionList.lookup(RunSelectorExtensionPoint.class).add(0, runSelector);

        try (ACLContext c = ACL.as(User.getById("alice", true))) {
            HttpResponses.HttpResponseException ex = assertThrows(
                    HttpResponses.HttpResponseException.class, () -> PublicBuildStatusAction.getRun(job, "", true));
            assertThat(ex, instanceOf(HttpResponses.notFound().getClass()));
        }
    }

    @Test
    void testGetRun_whenJobHasNoPermissions() throws Exception {
        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);

        RunSelectorExtensionPoint runSelector = (Job myJob, String selector, Run reference) -> build;
        ExtensionList.lookup(RunSelectorExtensionPoint.class).add(0, runSelector);

        try (ACLContext c = ACL.as(User.getById("alice", true))) {
            HttpResponses.HttpResponseException ex = assertThrows(
                    HttpResponses.HttpResponseException.class,
                    () -> PublicBuildStatusAction.getRun(job, build.getId(), true));
            assertThat(ex, instanceOf(HttpResponses.notFound().getClass()));
        }
    }

    @Test
    void testGetRun_whenJobHasNoPermissionsReturnsNull() throws Exception {
        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);

        RunSelectorExtensionPoint runSelector = (Job myJob, String selector, Run reference) -> build;
        ExtensionList.lookup(RunSelectorExtensionPoint.class).add(0, runSelector);

        try (ACLContext c = ACL.as(User.getById("alice", true))) {
            assertThat(PublicBuildStatusAction.getRun(job, build.getId(), false), is(nullValue()));
        }
    }

    @Test
    void testDoIcon_WhenJobHasNoPermissions() throws Exception {
        JobSelectorExtensionPoint jobSelector = (String jobName) -> job;
        ExtensionList.lookup(JobSelectorExtensionPoint.class).add(0, jobSelector);

        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);

        try (ACLContext c = ACL.as(User.getById("alice", true))) {
            HttpResponse response = new PublicBuildStatusAction()
                    .doIcon(null, null, job.getName(), null, null, null, null, null, null, null, null);
            assertThat(response.toString(), containsString("StatusImage"));
        }
    }
}
