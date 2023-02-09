package org.jenkinsci.plugins.badge.actions;

import hudson.model.FreeStyleProject;
import hudson.model.Run;
import hudson.tasks.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class PublicBuildStatusActionTest {

    private PublicBuildStatusAction publicBuildStatusActionMock;

    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Before
    public void setUp() throws Exception {
        publicBuildStatusActionMock = new PublicBuildStatusAction();
    }

    @Test
    public void testDoIcon() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new Shell("echo hello"));
        Run<?, ?> build = project.scheduleBuild2(0).get();

        StaplerRequest request = mock(StaplerRequest.class);
        StaplerResponse response = mock(StaplerResponse.class);

        PublicBuildStatusAction action = new PublicBuildStatusAction();
        HttpResponse httpResponse =
                action.doIcon(
                        request,
                        response,
                        project.getName(),
                        Integer.toString(build.getNumber()),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

        assertNotNull(httpResponse);
    }

    @Test
    public void testDoIconForJob() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new Shell("echo hello"));

        StaplerRequest request = mock(StaplerRequest.class);
        StaplerResponse response = mock(StaplerResponse.class);

        PublicBuildStatusAction action = new PublicBuildStatusAction();
        HttpResponse httpResponse =
                action.doIcon(
                        request,
                        response,
                        project.getName(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

        assertNotNull(httpResponse);
    }

    @Test
    public void testDoText() throws Exception {
        FreeStyleProject project = j.createFreeStyleProject();
        project.getBuildersList().add(new Shell("echo hello"));
        Run<?, ?> build = project.scheduleBuild2(0).get();

        StaplerRequest request = mock(StaplerRequest.class);
        StaplerResponse response = mock(StaplerResponse.class);

        PublicBuildStatusAction action = new PublicBuildStatusAction();
        String textResponse =
                action.doText(
                        request,
                        response,
                        project.getName(),
                        Integer.toString(build.getNumber()));

        assertNotNull(textResponse);
    }

    @Test
    public void testGetUrlName() throws IOException {
        PublicBuildStatusAction action = new PublicBuildStatusAction();
        String expected = "buildStatus";
        String actual = action.getUrlName();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetIconFileName() throws IOException {
        PublicBuildStatusAction action = new PublicBuildStatusAction();
        String actual = action.getIconFileName();
        assertNull(actual);
    }

    @Test
    public void testGetDisplayName() throws IOException {
        PublicBuildStatusAction action = new PublicBuildStatusAction();
        String actual = action.getDisplayName();
        assertNull(actual);
    }

}
