package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import hudson.ExtensionList;
import hudson.Functions;
import hudson.model.FreeStyleProject;
import hudson.model.Run;
import hudson.tasks.BatchFile;
import hudson.tasks.Shell;
import java.io.IOException;
import org.jenkinsci.plugins.badge.extensionpoints.JobSelectorExtensionPoint;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.jvnet.hudson.test.JenkinsRule;

public class PublicBuildStatusActionJobSelectorsTest {

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

    @Test
    public void testDoText_WithMultipleJobSelectors() throws Exception {
        JobSelectorExtensionPoint nullSelector = (String jobName) -> null;
        JobSelectorExtensionPoint validSelector = (String jobName) -> job;

        ExtensionList<JobSelectorExtensionPoint> extensionsList = ExtensionList.lookup(JobSelectorExtensionPoint.class);
        extensionsList.add(0, nullSelector);
        extensionsList.add(1, validSelector);

        String result = new PublicBuildStatusAction().doText(null, null, job.getName(), null);
        assertThat(result, is("Not built"));
    }

    @Test
    public void testDoText_WithMultipleJobSelectorsAfterRun() throws Exception {
        Run<?, ?> build = job.scheduleBuild2(0).get();
        j.assertBuildStatusSuccess(build);

        JobSelectorExtensionPoint nullSelector = (String jobName) -> null;
        JobSelectorExtensionPoint validSelector = (String jobName) -> job;

        ExtensionList<JobSelectorExtensionPoint> extensionsList = ExtensionList.lookup(JobSelectorExtensionPoint.class);
        extensionsList.add(0, nullSelector);
        extensionsList.add(1, validSelector);

        assertThat(new PublicBuildStatusAction().doText(null, null, job.getName(), null), is("Success"));
        assertThat(new PublicBuildStatusAction().doText(null, null, job.getName(), "1"), is("Success"));
    }
}
