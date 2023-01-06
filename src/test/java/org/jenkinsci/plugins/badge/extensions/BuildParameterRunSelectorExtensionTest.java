package org.jenkinsci.plugins.badge.extensions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import hudson.model.Job;
import hudson.model.Result;
import hudson.model.Run;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BuildParameterRunSelectorExtensionTest {

    private BuildParameterRunSelectorExtension extension;
    private Job mockProject;
    private Run mockRun;

    @BeforeEach
    void setUp() {

        extension = new BuildParameterRunSelectorExtension();
        mockProject = Mockito.mock(Job.class);
        mockRun = Mockito.mock(Run.class);
    }

    @Test
    void shouldSelectLastBuild() {
        when(mockProject.getLastBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "last", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "first", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastFailedBuild() {
        when(mockProject.getLastFailedBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastFailed", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstFailedBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.FAILURE);
        Run actualRun = extension.select(mockProject, "firstFailed", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastSuccessfulBuild() {
        when(mockProject.getLastSuccessfulBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastSuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstSuccessfulBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.SUCCESS);
        Run actualRun = extension.select(mockProject, "firstSuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastUnsuccessfulBuild() {
        when(mockProject.getLastUnsuccessfulBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastUnsuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstUnsuccessfulBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.FAILURE);
        Run actualRun = extension.select(mockProject, "firstUnsuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastStableBuild() {
        when(mockProject.getLastStableBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastStable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstStableBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.SUCCESS);
        Run actualRun = extension.select(mockProject, "firstStable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastUnstableBuild() {
        when(mockProject.getLastUnstableBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastUnstable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstUnstableBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.UNSTABLE);
        Run actualRun = extension.select(mockProject, "firstUnstable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastCompletedBuild() {
        when(mockProject.getLastCompletedBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastCompleted", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstCompletedBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.UNSTABLE);
        Run actualRun = extension.select(mockProject, "firstCompleted", null);
        assertThat(actualRun, is(mockRun));
    }
}
