package org.jenkinsci.plugins.badge.extensions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import hudson.model.Job;
import hudson.model.Run;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SpecialValueParameterResolverExtensionTest{

    private SpecialValueParameterResolverExtension extension;
    private Job mockProject;
    private Run mockRun;

    @BeforeEach
    void setUp() {
        extension = new SpecialValueParameterResolverExtension();
        mockProject = Mockito.mock(Job.class);

        mockRun = Mockito.mock(Run.class);

        when(mockRun.getId()).thenReturn("1234");
        when(mockRun.getNumber()).thenReturn(1234);
        when(mockRun.getDurationString()).thenReturn("23:35");
        when(mockRun.getTimestampString()).thenReturn("23:35");
        when(mockRun.getDisplayName()).thenReturn("display name");
        when(mockRun.getDescription()).thenReturn("run description");
    }

    @Test
    void shouldResolveBuildId() {
        String actualParameter = extension.resolve(mockRun, "buildId");
        assertThat(actualParameter, is("1234"));
    }

    @Test
    void shouldResolveBuildNumber() {
        String actualParameter = extension.resolve(mockRun, "buildNumber");
        assertThat(actualParameter, is("1234"));
    }

    @Test
    void shouldResolveDuration() {
        String actualParameter = extension.resolve(mockRun, "duration");
        assertThat(actualParameter, is("23:35"));
    }

    @Test
    void shouldResolveStartTime() {
        String actualParameter = extension.resolve(mockRun, "startTime");
        assertThat(actualParameter, is("23:35"));
    }

    @Test
    void shouldResolveDisplayName() {
        String actualParameter = extension.resolve(mockRun, "displayName");
        assertThat(actualParameter, is("display name"));
    }

    @Test
    void shouldResolveDescription() {
        String actualParameter = extension.resolve(mockRun, "description");
        assertThat(actualParameter, is("run description"));
    }

    @Test
    void testMultipleParameters() {
        String actualParameter =
                extension.resolve(mockRun, "buildId buildNumber duration startTime displayName description");
        assertThat(actualParameter, is("1234 1234 23:35 23:35 display name run description"));
    }

    @Test
    void resolveEmptyStringParameter() {
        String actualParameter = extension.resolve(mockRun, "");
        assertThat(actualParameter, is(""));
    }

    @Test
    void resolvewithNullParameter() {
        String actualParameter = extension.resolve(mockRun, null);
        assertThat(actualParameter, is((String) null));
    }

    @Test
    void unknownParameterShouldNotBeResolved() {
        String actualParameter = extension.resolve(mockRun, "unknown");
        assertThat(actualParameter, is("unknown"));
    }

    @Test
    void testResolveWithNoMatchingParameters() {
        String actualParameter = extension.resolve(mockRun, "noMatchingParameters");
        assertThat(actualParameter, is("noMatchingParameters"));
    }
}
