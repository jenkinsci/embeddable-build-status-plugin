package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import hudson.ExtensionList;
import hudson.Functions;
import hudson.model.FreeStyleProject;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.User;
import hudson.security.ACL;
import hudson.security.ACLContext;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import java.io.IOException;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.badge.extensionpoints.JobSelectorExtensionPoint;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.MockAuthorizationStrategy;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.HttpResponses;

public class PublicBuildStatusActionSecurityTest {

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Rule
    public TestName name = new TestName();

    private FreeStyleProject job;

    @Before
    public void createJob() throws IOException {
        // Give each job a name based on the name of the test method
        // Simplifies debugging and failure diagnosis
        // Also avoids any caching from reusing job name
        job = j.createFreeStyleProject("job-" + name.getMethodName());
        // Assure the job can pass on Windows and Unix
        job.getBuildersList()
                .add(
                        Functions.isWindows()
                                ? new BatchFile("echo hello from a batch file")
                                : new Shell("echo hello from a shell"));
    }

    @Before
    public void setupSecurity() {
        String username = "alice";
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
    public void testDoText_whenJobHasNoPermissions() throws Exception {
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
    public void testDoIcon_WhenJobHasNoPermissions() throws Exception {
        JobSelectorExtensionPoint jobSelector = (String jobName) -> job;
        ExtensionList.lookup(JobSelectorExtensionPoint.class).add(0, jobSelector);

        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);

        try (ACLContext c = ACL.as(User.getById("alice", true))) {
            HttpResponse response = new PublicBuildStatusAction()
                    .doIcon(null, null, job.getName(), null, null, null, null, null, null, null, null);
            assertNotNull(response);
        }
    }
}
