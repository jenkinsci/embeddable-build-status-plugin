package org.jenkinsci.plugins.badge.extensions;

import hudson.model.Job;
import hudson.model.Run;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

class SpecialValueParameterResolverExtensionTest {

    private SpecialValueParameterResolverExtension extension;
    private Job mockProject;
    private Run mockRun;

    @BeforeEach
    void setUp() {

        extension = new SpecialValueParameterResolverExtension();
        mockProject = Mockito.mock(Job.class);
        mockRun = Mockito.mock(Run.class);
    }
//    (buildId|buildNumber|duration|description|displayName|startTime)

    @Test
    void shouldResolveBuildId() {
        when(mockRun.getId()).thenReturn("1234");
        String actualParameter = extension.resolve(mockRun, "buildId");
        assertThat(actualParameter, is("1234"));
    }
    @Test
    void shouldResolveBuildNumber() {
        when(mockRun.getNumber()).thenReturn(1234);
        String actualParameter = extension.resolve(mockRun, "buildNumber");
        assertThat(actualParameter, is("1234"));
    }
    @Test
    void shouldResolveDuration() {
        when(mockRun.getDurationString()).thenReturn("23:35");
        String actualParameter = extension.resolve(mockRun, "duration");
        assertThat(actualParameter, is("23:35"));
    }
    @Test
    void shouldResolveStartTime() {
        when(mockRun.getTimestampString()).thenReturn("23:35");
        String actualParameter = extension.resolve(mockRun, "startTime");
        assertThat(actualParameter, is("23:35"));
    }
    @Test
    void shouldResolveDisplayName() {
        when(mockRun.getDisplayName()).thenReturn("display name");
        String actualParameter = extension.resolve(mockRun, "displayName");
        assertThat(actualParameter, is("display name"));
    }
    @Ignore("Runs into an infinite loop. Needs debugging.")
    void shouldResolveDescription() {
        when(mockRun.getDescription()).thenReturn("run description");
        String actualParameter = extension.resolve(mockRun, "description");
        assertThat(actualParameter, is("run description"));
    }
}