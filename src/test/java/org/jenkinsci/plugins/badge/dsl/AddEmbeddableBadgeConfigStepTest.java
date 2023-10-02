package org.jenkinsci.plugins.badge.dsl;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

public class AddEmbeddableBadgeConfigStepTest {
    @Test
    public void testConstructor() {
        AddEmbeddableBadgeConfigStep addEmbeddableBadgeConfigStep = new AddEmbeddableBadgeConfigStep("test-Id-constructor");
        assertThat(addEmbeddableBadgeConfigStep.getID(), is("test-Id-constructor"));
        assertThat(addEmbeddableBadgeConfigStep.getSubject(), is(nullValue()));
        assertThat(addEmbeddableBadgeConfigStep.getStatus(), is(nullValue()));
        assertThat(addEmbeddableBadgeConfigStep.getColor(), is(nullValue()));
        assertThat(addEmbeddableBadgeConfigStep.getAnimatedOverlayColor(), is(nullValue()));
        assertThat(addEmbeddableBadgeConfigStep.getLink(), is(nullValue()));
    }

    @Test
    public void testGetAnimatedOverlayColor(){
        AddEmbeddableBadgeConfigStep addEmbeddableBadgeConfigStep = new AddEmbeddableBadgeConfigStep("test-animated-overlay-color");
        assertThat(addEmbeddableBadgeConfigStep.getColor(),is(nullValue()));
    }

}