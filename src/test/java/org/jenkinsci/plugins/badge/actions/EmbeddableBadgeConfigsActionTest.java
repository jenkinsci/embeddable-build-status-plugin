package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import hudson.model.Job;
import hudson.model.Run;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class EmbeddableBadgeConfigsActionTest {

    EmbeddableBadgeConfigsAction embeddableBadgeConfigsAction;

    @BeforeEach
    void setUp() {
        Run mockRun = Mockito.mock(Run.class);
        Job mockJob = Mockito.mock(Job.class);
        Mockito.when(mockRun.getParent()).thenReturn(mockJob);
        embeddableBadgeConfigsAction = new EmbeddableBadgeConfigsAction();
    }

    @Test
    public void getUrlNameTest() {
        assertThat(embeddableBadgeConfigsAction.getUrlName(), is(""));
    }

    @Test
    public void getDisplayNameTest() {
        assertThat(embeddableBadgeConfigsAction.getDisplayName(), is(""));
    }

    @Test
    public void getIconFileNameTest() {
        assertThat(embeddableBadgeConfigsAction.getIconFileName(), is(nullValue()));
    }

    @Test
    public void resolveTest() {
        Job mockRun = Mockito.mock(Job.class);
        Mockito.when(mockRun.getParent()).thenReturn(null);
        assertThat(mockRun.getLastBuild(), is(nullValue()));
    }

    @Test
    public void getConfigTest() {
        Run mockRun = Mockito.mock(Run.class);
        Mockito.when(mockRun.getParent()).thenReturn(null);
        assertThat(mockRun.getId(), is(nullValue()));
    }
}
