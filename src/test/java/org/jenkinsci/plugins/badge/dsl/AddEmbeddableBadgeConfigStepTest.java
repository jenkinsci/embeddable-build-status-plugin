package org.jenkinsci.plugins.badge.dsl;

import static org.junit.jupiter.api.Assertions.*;

import hudson.model.Run;
import java.lang.reflect.Field;
import java.util.Set;
import org.jenkinsci.plugins.badge.EmbeddableBadgeConfig;
import org.jenkinsci.plugins.badge.actions.EmbeddableBadgeConfigsAction;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AddEmbeddableBadgeConfigStepTest {

    private AddEmbeddableBadgeConfigStep addEmbeddableBadgeConfigStep = new AddEmbeddableBadgeConfigStep("id");
    private AddEmbeddableBadgeConfigStep.DescriptorImpl descriptor = new AddEmbeddableBadgeConfigStep.DescriptorImpl();
    EmbeddableBadgeConfig mockEmbeddableBadgeConfig;

    @BeforeEach
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        mockEmbeddableBadgeConfig = Mockito.mock(EmbeddableBadgeConfig.class);
        Field privateVariableField = AddEmbeddableBadgeConfigStep.class.getDeclaredField("badgeConfig");
        privateVariableField.setAccessible(true);

        Mockito.when(mockEmbeddableBadgeConfig.getID()).thenReturn("id");
        Mockito.when(mockEmbeddableBadgeConfig.getSubject()).thenReturn("subject");
        Mockito.when(mockEmbeddableBadgeConfig.getStatus()).thenReturn("status");
        Mockito.when(mockEmbeddableBadgeConfig.getLink()).thenReturn("link");
        Mockito.when(mockEmbeddableBadgeConfig.getColor()).thenReturn("color");
        Mockito.when(mockEmbeddableBadgeConfig.getAnimatedOverlayColor()).thenReturn("animatedColor");

        Mockito.doNothing().when(mockEmbeddableBadgeConfig).setSubject("subject");
        Mockito.doNothing().when(mockEmbeddableBadgeConfig).setStatus("status");
        Mockito.doNothing().when(mockEmbeddableBadgeConfig).setLink("link");
        Mockito.doNothing().when(mockEmbeddableBadgeConfig).setColor("color");
        Mockito.doNothing().when(mockEmbeddableBadgeConfig).setAnimatedOverlayColor("animatedColor");

        privateVariableField.set(addEmbeddableBadgeConfigStep, mockEmbeddableBadgeConfig);
    }

    @Test
    void getID() {
        String id = addEmbeddableBadgeConfigStep.getID();
        assertEquals("id", id);
    }

    @Test
    void getSubject() {
        String subject = addEmbeddableBadgeConfigStep.getSubject();
        assertEquals("subject", subject);
    }

    @Test
    void setSubject() {
        addEmbeddableBadgeConfigStep.setSubject("subject");
    }

    @Test
    void getStatus() {
        assertEquals("status", addEmbeddableBadgeConfigStep.getStatus());
    }

    @Test
    void setStatus() {
        addEmbeddableBadgeConfigStep.setStatus("status");
    }

    @Test
    void getColor() {
        assertEquals("color", addEmbeddableBadgeConfigStep.getColor());
    }

    @Test
    void setColor() {
        addEmbeddableBadgeConfigStep.setColor("color");
    }

    @Test
    void getAnimatedOverlayColor() {
        assertEquals("animatedColor", addEmbeddableBadgeConfigStep.getAnimatedOverlayColor());
    }

    @Test
    void setAnimatedOverlayColor() {
        addEmbeddableBadgeConfigStep.setAnimatedOverlayColor("animatedColor");
    }

    @Test
    void getLink() {
        assertEquals("link", addEmbeddableBadgeConfigStep.getLink());
    }

    @Test
    void setLink() {
        addEmbeddableBadgeConfigStep.setLink("link");
    }

    @Test
    void start() {
        StepContext stepContext = Mockito.mock(StepContext.class);
        StepExecution stepExecution = addEmbeddableBadgeConfigStep.start(stepContext);
        assertNotNull(stepExecution);
    }

    @Test
    void descriptorImplTest() {
        assertEquals("addEmbeddableBadgeConfiguration", descriptor.getFunctionName());
    }

    @Test
    void getDisplayNameTest() {
        assertEquals("Add an Embeddable Badge Configuration", descriptor.getDisplayName());
    }

    @Test
    void getRequiredContextTest() {
        Set<Class<?>> st = descriptor.getRequiredContext();
        assertNotNull(st);
    }

    @Test
    void runTest() throws Exception {
        StepContext stepContext = Mockito.mock(StepContext.class);
        StepExecution stepExecution = Mockito.mock(StepExecution.class);
        Run run = Mockito.mock(Run.class);
        EmbeddableBadgeConfigsAction embeddableBadgeConfigsAction = Mockito.mock(EmbeddableBadgeConfigsAction.class);

        /* Without null */
        Mockito.when(stepContext.get(Run.class)).thenReturn(run);
        Mockito.when(stepContext.get(Run.class).getAction(EmbeddableBadgeConfigsAction.class))
                .thenReturn(embeddableBadgeConfigsAction);
        Mockito.when(stepExecution.getContext()).thenReturn(stepContext);

        AddEmbeddableBadgeConfigStep.Execution execution =
                new AddEmbeddableBadgeConfigStep.Execution(mockEmbeddableBadgeConfig, stepContext);
        EmbeddableBadgeConfig embeddableBadgeConfig = execution.run();
        assertEquals(mockEmbeddableBadgeConfig, embeddableBadgeConfig);

        /* With null */

        Mockito.when(stepContext.get(Run.class).getAction(EmbeddableBadgeConfigsAction.class))
                .thenReturn(null);
        AddEmbeddableBadgeConfigStep.Execution execution1 =
                new AddEmbeddableBadgeConfigStep.Execution(mockEmbeddableBadgeConfig, stepContext);
        EmbeddableBadgeConfig embeddableBadgeConfig1 = execution1.run();

        assertEquals(mockEmbeddableBadgeConfig, embeddableBadgeConfig1);
    }
}
