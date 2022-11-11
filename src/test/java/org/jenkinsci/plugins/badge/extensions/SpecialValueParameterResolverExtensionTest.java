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
}