package org.jenkinsci.plugins.badge.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.badge.EmbeddableBadgeConfig;
import org.jenkinsci.plugins.badge.actions.EmbeddableBadgeConfigsAction;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddEmbeddableBadgeConfigStepTest {

    private static Run run = mock(Run.class);
    private static StepContext context = mock(StepContext.class);
    private static AddEmbeddableBadgeConfigStep step = new AddEmbeddableBadgeConfigStep("testId");
    private static AddEmbeddableBadgeConfigStep.Execution execution =
            new AddEmbeddableBadgeConfigStep.Execution(step.badgeConfig, context);

    @BeforeEach
    void setUp() {
        run = mock(Run.class);
        context = mock(StepContext.class);
        step = new AddEmbeddableBadgeConfigStep("testId");
        execution = new AddEmbeddableBadgeConfigStep.Execution(step.badgeConfig, context);
    }

    @Test
    void testConstructorAndGetID() {
        assertEquals("testId", step.getID());
    }

    @Test
    void testGetSubjectAndSetSubject() {
        assertNull(step.getSubject());
        step.setSubject("testSubject");
        assertEquals("testSubject", step.getSubject());
    }

    @Test
    void testGetStatusAndSetStatus() {
        assertNull(step.getStatus());
        step.setStatus("testStatus");
        assertEquals("testStatus", step.getStatus());
    }

    @Test
    void testGetColorAndSetColor() {
        assertNull(step.getColor());
        step.setColor("testColor");
        assertEquals("testColor", step.getColor());
    }

    @Test
    void testGetAnimatedOverlayColorAndSetAnimatedOverlayColor() {
        assertNull(step.getAnimatedOverlayColor());
        step.setAnimatedOverlayColor("testOverlayColor");
        assertEquals("testOverlayColor", step.getAnimatedOverlayColor());
    }

    @Test
    void testGetLinkAndSetLink() {
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

    @Test
    void testSetSubject_shouldHandleNull() {
        step.setSubject(null);
        assertNull(step.getSubject());
    }

    @Test
    void testSetStatus_shouldHandleNull() {
        step.setStatus(null);
        assertNull(step.getStatus());
    }

    @Test
    void testSetColor_shouldHandleNull() {
        step.setColor(null);
        assertNull(step.getColor());
    }

    @Test
    void testSetAnimatedOverlayColor_shouldHandleNull() {
        step.setAnimatedOverlayColor(null);
        assertNull(step.getAnimatedOverlayColor());
    }

    @Test
    void testSetLink_shouldHandleNull() {
        step.setLink(null);
        assertNull(step.getLink());
    }

    @Test
    void testExecution_shouldAddConfigToRun() throws Exception {
        when(context.get(Run.class)).thenReturn(run);
        execution.run();
        verify(run, times(1)).addAction(any(EmbeddableBadgeConfigsAction.class));
    }

    @Test
    void testExecution_shouldReturnBadgeConfig() throws Exception {
        when(context.get(Run.class)).thenReturn(run);
        EmbeddableBadgeConfig result = execution.run();
        assertEquals(step.badgeConfig, result);
    }
}
