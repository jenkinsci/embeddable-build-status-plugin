package org.jenkinsci.plugins.badge.extensions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.Job;
import hudson.model.Run;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BuildIdRunSelectorExtensionTest {

    private BuildIdRunSelectorExtension extension;
    private Job mockProject;
    private Run mockRun;

    @BeforeEach
    void setUp() {
        extension = new BuildIdRunSelectorExtension();
        mockProject = Mockito.mock(Job.class);
        mockRun = Mockito.mock(Run.class);
    }

    @Test
    void shouldSelectLastBuild() {
        Mockito.when(mockProject.getLastBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "last", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastBuildIfBuildNumberIsZero() {
        Mockito.when(mockProject.getLastBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "0", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldIterativelySelectPreviousNthBuildIfBuildNumberIsNegative() {
        Mockito.when(mockRun.getPreviousBuild()).thenReturn(mockRun);
        Mockito.when(mockProject.getLastBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "-3", null);
        assertThat(actualRun, is(mockRun));
        Mockito.verify(mockRun, Mockito.times(3)).getPreviousBuild();
    }

    @Test
    void shouldSelectLastFailedBuild() {
        Mockito.when(mockProject.getLastFailedBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastFailed", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastSuccessfulBuild() {
        Mockito.when(mockProject.getLastSuccessfulBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastSuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastUnsuccessfulBuild() {
        Mockito.when(mockProject.getLastUnsuccessfulBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastUnsuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastStableBuild() {
        Mockito.when(mockProject.getLastStableBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastStable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastUnstableBuild() {
        Mockito.when(mockProject.getLastUnstableBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastUnstable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastCompletedBuild() {
        Mockito.when(mockProject.getLastCompletedBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastCompleted", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectBuildById() {
        String buildId = "buildId";
        Mockito.when(mockProject.getBuild(buildId)).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, buildId, null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectBuildByNumber() {
        int buildNumber = 123;
        Mockito.when(mockProject.getBuildByNumber(buildNumber)).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, buildNumber + "", null);
        assertThat(actualRun, is(mockRun));
    }
}
