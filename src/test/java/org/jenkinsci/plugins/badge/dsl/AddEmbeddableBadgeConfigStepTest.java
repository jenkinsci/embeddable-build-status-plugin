package org.jenkinsci.plugins.badge.dsl;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.TaskListener;
import org.junit.jupiter.api.Test;

class AddEmbeddableBadgeConfigStepTest {

    @Test
    void testConstructorAndGetID() {
        AddEmbeddableBadgeConfigStep step = new AddEmbeddableBadgeConfigStep("testId");
        assertEquals("testId", step.getID());
    }

    @Test
    void testGetSubjectAndSetSubject() {
        AddEmbeddableBadgeConfigStep step = new AddEmbeddableBadgeConfigStep("testId");
        assertNull(step.getSubject());
        step.setSubject("testSubject");
        assertEquals("testSubject", step.getSubject());
    }

    @Test
    void testGetStatusAndSetStatus() {
        AddEmbeddableBadgeConfigStep step = new AddEmbeddableBadgeConfigStep("testId");
        assertNull(step.getStatus());
        step.setStatus("testStatus");
        assertEquals("testStatus", step.getStatus());
    }

    @Test
    void testGetColorAndSetColor() {
        AddEmbeddableBadgeConfigStep step = new AddEmbeddableBadgeConfigStep("testId");
        assertNull(step.getColor());
        step.setColor("testColor");
        assertEquals("testColor", step.getColor());
    }

    @Test
    void testGetAnimatedOverlayColorAndSetAnimatedOverlayColor() {
        AddEmbeddableBadgeConfigStep step = new AddEmbeddableBadgeConfigStep("testId");
        assertNull(step.getAnimatedOverlayColor());
        step.setAnimatedOverlayColor("testOverlayColor");
        assertEquals("testOverlayColor", step.getAnimatedOverlayColor());
    }

    @Test
    void testGetLinkAndSetLink() {
        AddEmbeddableBadgeConfigStep step = new AddEmbeddableBadgeConfigStep("testId");
        assertNull(step.getLink());
        step.setLink("testLink");
        assertEquals("testLink", step.getLink());
    }

    @Test
    void testDescriptor() {
        AddEmbeddableBadgeConfigStep.DescriptorImpl descriptor = new AddEmbeddableBadgeConfigStep.DescriptorImpl();
        assertEquals("addEmbeddableBadgeConfiguration", descriptor.getFunctionName());
        assertEquals("Add an Embeddable Badge Configuration", descriptor.getDisplayName());
        assertEquals(1, descriptor.getRequiredContext().size());
        assertTrue(descriptor.getRequiredContext().contains(TaskListener.class));
    }
}
