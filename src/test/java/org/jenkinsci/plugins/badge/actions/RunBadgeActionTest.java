package org.jenkinsci.plugins.badge.actions;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.BallColor;
import hudson.model.Job;
import hudson.model.Run;
import org.jenkinsci.plugins.badge.Messages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

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
    void getIconFileName() {
        assertNull(runBadgeAction.getIconFileName());
    }

    @Test
    void getIconClassName() {
        assertEquals("symbol-shield-outline plugin-ionicons-api", runBadgeAction.getIconClassName());
    }

    @Test
    void getDisplayName() {
        try (MockedStatic<Messages> mockedStatic = Mockito.mockStatic(Messages.class)) {
            mockedStatic.when(() -> Messages.RunBadgeAction_DisplayName()).thenReturn("display");
            assertEquals("display", runBadgeAction.getDisplayName());
        }
    }

    @Test
    void getUrlName() {
        assertEquals("badge", runBadgeAction.getUrlName());
    }

    @Test
    void getUrl() {
        try (MockedStatic<Stapler> mockedStatic = Mockito.mockStatic(Stapler.class)) {
            StaplerRequest staplerRequest = Mockito.mock(StaplerRequest.class);
            Mockito.when(staplerRequest.getReferer()).thenReturn("referer");
            mockedStatic.when(() -> Stapler.getCurrentRequest()).thenReturn(staplerRequest);

            assertEquals("referer", runBadgeAction.getUrl());
        }
    }

    @Test
    void getUrlEncodedFullName() {
        Run mockRun = Mockito.mock(Run.class);
        Mockito.when(mockRun.getParent()).thenReturn(null);
        runBadgeAction = new RunBadgeAction(mockRun);
        assertEquals("null-project-no-url-encoded-fullName", runBadgeAction.getUrlEncodedFullName());
    }

    @Test
    void getUrlEncodedFullNameWithJobNull() {
        Run mockRun = Mockito.mock(Run.class);
        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockJob.getFullName()).thenReturn(null);
        Mockito.when(mockRun.getParent()).thenReturn(mockJob);
        runBadgeAction = new RunBadgeAction(mockRun);

        assertEquals("null-project-fullName-no-url-encoded-fullName", runBadgeAction.getUrlEncodedFullName());
    }

    @Test
    void getUrlEncodedFullNameWithFullName() {
        Run mockRun = Mockito.mock(Run.class);
        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockJob.getFullName()).thenReturn("full-name");
        Mockito.when(mockRun.getParent()).thenReturn(mockJob);
        runBadgeAction = new RunBadgeAction(mockRun);

        assertEquals("full-name", runBadgeAction.getUrlEncodedFullName());
    }

    @Test
    void doText() {
        Run mockRun = Mockito.mock(Run.class);
        Mockito.when(mockRun.getIconColor()).thenReturn(BallColor.BLUE);
        runBadgeAction = new RunBadgeAction(mockRun);

        assertEquals("Success", runBadgeAction.doText());
    }
}
