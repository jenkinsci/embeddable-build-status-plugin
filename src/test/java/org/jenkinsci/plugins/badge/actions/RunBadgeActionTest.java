package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.Job;
import hudson.model.Run;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/* Use mocks to test paths that are hard to reach with JenkinsRule */
class RunBadgeActionTest {

    RunBadgeAction runBadgeAction;

    @BeforeEach
    void setUp() {
        Run mockRun = Mockito.mock(Run.class);
        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockRun.getParent()).thenReturn(mockJob);
        runBadgeAction = new RunBadgeAction(mockRun);
    }

    @Test
    void getUrlEncodedFullNameWithProjectNull() {
        Run mockRun = Mockito.mock(Run.class);
        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockJob.getFullName()).thenReturn("full-name");
        Mockito.when(mockRun.getParent()).thenReturn(null);
        runBadgeAction = new RunBadgeAction(mockRun);

        assertThat(runBadgeAction.getUrlEncodedFullName(), is("null-project-no-url-encoded-fullName"));
    }

    @Test
    void getUrl() {
        try (MockedStatic<Stapler> mockedStatic = Mockito.mockStatic(Stapler.class)) {
            StaplerRequest staplerRequest = Mockito.mock(StaplerRequest.class);
            Mockito.when(staplerRequest.getReferer()).thenReturn("referer");
            mockedStatic.when(() -> Stapler.getCurrentRequest()).thenReturn(staplerRequest);

            assertThat(runBadgeAction.getUrl(), is("referer"));
        }
    }

    @Test
    void getUrlEncodedFullNameWithJobNull() {
        Run mockRun = Mockito.mock(Run.class);
        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockJob.getFullName()).thenReturn(null);
        Mockito.when(mockRun.getParent()).thenReturn(mockJob);
        runBadgeAction = new RunBadgeAction(mockRun);

        assertThat(runBadgeAction.getUrlEncodedFullName(), is("null-project-fullName-no-url-encoded-fullName"));
    }
}
