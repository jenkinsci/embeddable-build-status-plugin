package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EmbeddableBadgeConfigsActionTest {

    EmbeddableBadgeConfigsAction embeddableBadgeConfigsAction;

    @BeforeEach
    void setUp() {
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
}
