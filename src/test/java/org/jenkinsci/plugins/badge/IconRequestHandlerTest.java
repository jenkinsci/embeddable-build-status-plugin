package org.jenkinsci.plugins.badge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.BallColor;
import hudson.model.Job;
import hudson.model.Run;
import java.lang.reflect.Field;
import org.jenkinsci.plugins.badge.actions.EmbeddableBadgeConfigsAction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

@WithJenkins
class IconRequestHandlerTest {

    @SuppressWarnings("unused")
    private static JenkinsRule jenkinsRule;

    private final IconRequestHandler iconRequestHandler = new IconRequestHandler();

    @BeforeAll
    static void beforeAll(JenkinsRule rule) {
        jenkinsRule = rule;
    }

    @Test
    void handleIconRequest() throws Exception {
        ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
        StatusImage mockedStatusImage = new StatusImage();
        when(mockIconResolver.getImage(
                        BallColor.BLUE, "style", "subject", "status", "color", "animatedOverlayColor", "link"))
                .thenReturn(mockedStatusImage);

        Field privateVariableField = IconRequestHandler.class.getDeclaredField("iconResolver");
        privateVariableField.setAccessible(true);
        privateVariableField.set(iconRequestHandler, mockIconResolver);

        StatusImage statusImage = iconRequestHandler.handleIconRequest(
                "style", "subject", "status", "color", "animatedOverlayColor", "link");
        assertEquals(mockedStatusImage, statusImage);
    }

    @Test
    void handleIconRequestForJob() throws NoSuchFieldException, IllegalAccessException {
        ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
        StatusImage mockedStatusImage = new StatusImage();
        ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
        Job job = mock(Job.class);

        when(job.getIconColor()).thenReturn(BallColor.BLUE);

        when(mockIconResolver.getImage(
                        BallColor.BLUE, "style", "subject", "status", "color", "animatedOverlayColor", "link"))
                .thenReturn(mockedStatusImage);

        when(mockParameterResolver.resolve(job, "subject")).thenReturn("subject");
        when(mockParameterResolver.resolve(job, "status")).thenReturn("status");
        when(mockParameterResolver.resolve(job, "animatedOverlayColor")).thenReturn("animatedOverlayColor");
        when(mockParameterResolver.resolve(job, "link")).thenReturn("link");
        when(mockParameterResolver.resolve(job, "color")).thenReturn("color");

        Field privateVariableFieldParameterResolver = IconRequestHandler.class.getDeclaredField("parameterResolver");
        privateVariableFieldParameterResolver.setAccessible(true);
        privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

        Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
        privateVariableFieldIconResolver.setAccessible(true);
        privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);

        StatusImage statusImage = iconRequestHandler.handleIconRequestForJob(
                job, "style", "subject", "status", "color", "animatedOverlayColor", "config", "link");
        assertEquals(statusImage, mockedStatusImage);
    }

    @Test
    void handleIconRequestForJob1() throws NoSuchFieldException, IllegalAccessException {
        ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
        StatusImage mockedStatusImage = new StatusImage();
        ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
        when(mockIconResolver.getImage(BallColor.NOTBUILT, "style", "subject", null, null, null, null))
                .thenReturn(mockedStatusImage);

        Field privateVariableFieldParameterResolver = IconRequestHandler.class.getDeclaredField("parameterResolver");
        privateVariableFieldParameterResolver.setAccessible(true);
        privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

        Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
        privateVariableFieldIconResolver.setAccessible(true);
        privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);

        StatusImage statusImage = iconRequestHandler.handleIconRequestForJob(
                null, "style", "subject", "status", "color", "animatedOverlayColor", null, "link");
        assertEquals(statusImage, mockedStatusImage);
    }

    @Test
    void handleIconRequestForJob2() throws NoSuchFieldException, IllegalAccessException {
        try (MockedStatic<EmbeddableBadgeConfigsAction> embeddableBadgeConfigsActionMockedStatic =
                Mockito.mockStatic(EmbeddableBadgeConfigsAction.class)) {

            ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
            StatusImage mockedStatusImage = new StatusImage();
            ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
            Job job = mock(Job.class);

            when(job.getIconColor()).thenReturn(BallColor.BLUE);

            when(mockIconResolver.getImage(
                            BallColor.BLUE, "style", "subject", "status", "lightgrey", "animatedOverlayColor", "link"))
                    .thenReturn(mockedStatusImage);

            when(mockParameterResolver.resolve(job, "subject")).thenReturn("subject");
            when(mockParameterResolver.resolve(job, "status")).thenReturn("status");
            when(mockParameterResolver.resolve(job, "animatedOverlayColor")).thenReturn("animatedOverlayColor");
            when(mockParameterResolver.resolve(job, "link")).thenReturn("link");
            when(mockParameterResolver.resolve(job, null)).thenReturn(null);

            Field privateVariableFieldParameterResolver =
                    IconRequestHandler.class.getDeclaredField("parameterResolver");
            privateVariableFieldParameterResolver.setAccessible(true);
            privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

            Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
            privateVariableFieldIconResolver.setAccessible(true);
            privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);
            embeddableBadgeConfigsActionMockedStatic
                    .when(() -> EmbeddableBadgeConfigsAction.resolve(job, "config"))
                    .thenReturn(null);

            StatusImage statusImage = iconRequestHandler.handleIconRequestForJob(
                    job, "style", "subject", "status", null, "animatedOverlayColor", "config", "link");
            System.out.println("statusImage  " + statusImage);
            assertEquals(statusImage, mockedStatusImage);
        }
    }

    @Test
    void handleIconRequestForJob3() throws NoSuchFieldException, IllegalAccessException {
        try (MockedStatic<EmbeddableBadgeConfigsAction> embeddableBadgeConfigsActionMockedStatic =
                Mockito.mockStatic(EmbeddableBadgeConfigsAction.class)) {

            ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
            StatusImage mockedStatusImage = new StatusImage();
            ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
            Job job = mock(Job.class);

            when(job.getIconColor()).thenReturn(BallColor.BLUE);

            when(mockIconResolver.getImage(
                            BallColor.BLUE, "style", "subject", "not run", "color", "animatedOverlayColor", "link"))
                    .thenReturn(mockedStatusImage);

            when(mockParameterResolver.resolve(job, "subject")).thenReturn("subject");
            when(mockParameterResolver.resolve(job, null)).thenReturn(null);
            when(mockParameterResolver.resolve(job, "animatedOverlayColor")).thenReturn("animatedOverlayColor");
            when(mockParameterResolver.resolve(job, "link")).thenReturn("link");
            when(mockParameterResolver.resolve(job, "color")).thenReturn("color");

            Field privateVariableFieldParameterResolver =
                    IconRequestHandler.class.getDeclaredField("parameterResolver");
            privateVariableFieldParameterResolver.setAccessible(true);
            privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

            Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
            privateVariableFieldIconResolver.setAccessible(true);
            privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);
            embeddableBadgeConfigsActionMockedStatic
                    .when(() -> EmbeddableBadgeConfigsAction.resolve(job, "config"))
                    .thenReturn(null);

            StatusImage statusImage = iconRequestHandler.handleIconRequestForJob(
                    job, "style", "subject", null, "color", "animatedOverlayColor", "config", "link");
            assertEquals(statusImage, mockedStatusImage);
        }
    }

    @Test
    void handleIconRequestForJob4() throws NoSuchFieldException, IllegalAccessException {
        try (MockedStatic<EmbeddableBadgeConfigsAction> embeddableBadgeConfigsActionMockedStatic =
                Mockito.mockStatic(EmbeddableBadgeConfigsAction.class)) {

            ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
            StatusImage mockedStatusImage = new StatusImage();
            ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
            Job job = mock(Job.class);

            when(job.getIconColor()).thenReturn(BallColor.BLUE);

            when(mockIconResolver.getImage(
                            BallColor.BLUE, "style", "subject", "status", "color", "animatedOverlayColor", "link"))
                    .thenReturn(mockedStatusImage);

            when(mockParameterResolver.resolve(job, "subject")).thenReturn("subject");
            when(mockParameterResolver.resolve(job, "status")).thenReturn("status");
            when(mockParameterResolver.resolve(job, "animatedOverlayColor")).thenReturn("animatedOverlayColor");
            when(mockParameterResolver.resolve(job, null)).thenReturn(null);
            when(mockParameterResolver.resolve(job, "color")).thenReturn("color");

            Field privateVariableFieldParameterResolver =
                    IconRequestHandler.class.getDeclaredField("parameterResolver");
            privateVariableFieldParameterResolver.setAccessible(true);
            privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

            Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
            privateVariableFieldIconResolver.setAccessible(true);
            privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);

            EmbeddableBadgeConfig mockEmbeddableBadgeConfig = mock(EmbeddableBadgeConfig.class);
            when(mockEmbeddableBadgeConfig.getLink()).thenReturn("link");

            embeddableBadgeConfigsActionMockedStatic
                    .when(() -> EmbeddableBadgeConfigsAction.resolve(job, "config"))
                    .thenReturn(mockEmbeddableBadgeConfig);

            StatusImage statusImage = iconRequestHandler.handleIconRequestForJob(
                    job, "style", "subject", "status", "color", "animatedOverlayColor", "config", null);
            assertEquals(statusImage, mockedStatusImage);
        }
    }

    @Test
    void handleIconRequestForJob5() throws NoSuchFieldException, IllegalAccessException {
        try (MockedStatic<EmbeddableBadgeConfigsAction> embeddableBadgeConfigsActionMockedStatic =
                Mockito.mockStatic(EmbeddableBadgeConfigsAction.class)) {

            ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
            StatusImage mockedStatusImage = new StatusImage();
            ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
            Job job = mock(Job.class);

            when(job.getIconColor()).thenReturn(BallColor.BLUE);

            when(mockIconResolver.getImage(
                            BallColor.BLUE, "style", "subject", "status", "color", "animatedOverlayColor", "link"))
                    .thenReturn(mockedStatusImage);

            when(mockParameterResolver.resolve(job, null)).thenReturn(null);

            Field privateVariableFieldParameterResolver =
                    IconRequestHandler.class.getDeclaredField("parameterResolver");
            privateVariableFieldParameterResolver.setAccessible(true);
            privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

            Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
            privateVariableFieldIconResolver.setAccessible(true);
            privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);

            EmbeddableBadgeConfig mockEmbeddableBadgeConfig = mock(EmbeddableBadgeConfig.class);

            when(mockEmbeddableBadgeConfig.getStatus()).thenReturn("status");
            when(mockEmbeddableBadgeConfig.getColor()).thenReturn("color");
            when(mockEmbeddableBadgeConfig.getLink()).thenReturn("link");
            when(mockEmbeddableBadgeConfig.getSubject()).thenReturn("subject");
            when(mockEmbeddableBadgeConfig.getAnimatedOverlayColor()).thenReturn("animatedOverlayColor");

            embeddableBadgeConfigsActionMockedStatic
                    .when(() -> EmbeddableBadgeConfigsAction.resolve(job, "config"))
                    .thenReturn(mockEmbeddableBadgeConfig);

            StatusImage statusImage = iconRequestHandler.handleIconRequestForJob(
                    job, "style", "subject", null, "color", "animatedOverlayColor", "config", "link");
            assertEquals(statusImage, mockedStatusImage);
        }
    }

    @Test
    void handleIconRequestForRun() throws IllegalAccessException, NoSuchFieldException {
        ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
        StatusImage mockedStatusImage = new StatusImage();
        ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
        Run run = mock(Run.class);

        when(run.getIconColor()).thenReturn(BallColor.BLUE);

        when(mockIconResolver.getImage(
                        BallColor.BLUE, "style", "subject", "status", "color", "animatedOverlayColor", "link"))
                .thenReturn(mockedStatusImage);

        when(mockParameterResolver.resolve(run, "subject")).thenReturn("subject");
        when(mockParameterResolver.resolve(run, "status")).thenReturn("status");
        when(mockParameterResolver.resolve(run, "animatedOverlayColor")).thenReturn("animatedOverlayColor");
        when(mockParameterResolver.resolve(run, "link")).thenReturn("link");
        when(mockParameterResolver.resolve(run, "color")).thenReturn("color");

        Field privateVariableFieldParameterResolver = IconRequestHandler.class.getDeclaredField("parameterResolver");
        privateVariableFieldParameterResolver.setAccessible(true);
        privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

        Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
        privateVariableFieldIconResolver.setAccessible(true);
        privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);

        StatusImage statusImage = iconRequestHandler.handleIconRequestForRun(
                run, "style", "subject", "status", "color", "animatedOverlayColor", "config", "link");
        assertEquals(statusImage, mockedStatusImage);
    }

    @Test
    void handleIconRequestForRun1() throws NoSuchFieldException, IllegalAccessException {
        ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
        StatusImage mockedStatusImage = new StatusImage();
        ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
        when(mockIconResolver.getImage(BallColor.NOTBUILT, "style", "subject", null, null, null, null))
                .thenReturn(mockedStatusImage);

        Field privateVariableFieldParameterResolver = IconRequestHandler.class.getDeclaredField("parameterResolver");
        privateVariableFieldParameterResolver.setAccessible(true);
        privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

        Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
        privateVariableFieldIconResolver.setAccessible(true);
        privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);

        StatusImage statusImage = iconRequestHandler.handleIconRequestForRun(
                null, "style", "subject", "status", "color", "animatedOverlayColor", null, "link");
        assertEquals(statusImage, mockedStatusImage);
    }

    @Test
    void handleIconRequestForRun2() throws NoSuchFieldException, IllegalAccessException {
        try (MockedStatic<EmbeddableBadgeConfigsAction> embeddableBadgeConfigsActionMockedStatic =
                Mockito.mockStatic(EmbeddableBadgeConfigsAction.class)) {

            ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
            StatusImage mockedStatusImage = new StatusImage();
            ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
            Run run = mock(Run.class);

            when(run.getIconColor()).thenReturn(BallColor.BLUE);

            when(mockIconResolver.getImage(
                            BallColor.BLUE, "style", "subject", "status", "lightgrey", "animatedOverlayColor", "link"))
                    .thenReturn(mockedStatusImage);

            when(mockParameterResolver.resolve(run, "subject")).thenReturn("subject");
            when(mockParameterResolver.resolve(run, "status")).thenReturn("status");
            when(mockParameterResolver.resolve(run, "animatedOverlayColor")).thenReturn("animatedOverlayColor");
            when(mockParameterResolver.resolve(run, "link")).thenReturn("link");
            when(mockParameterResolver.resolve(run, null)).thenReturn(null);

            Field privateVariableFieldParameterResolver =
                    IconRequestHandler.class.getDeclaredField("parameterResolver");
            privateVariableFieldParameterResolver.setAccessible(true);
            privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

            Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
            privateVariableFieldIconResolver.setAccessible(true);
            privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);
            embeddableBadgeConfigsActionMockedStatic
                    .when(() -> EmbeddableBadgeConfigsAction.resolve(run, "config"))
                    .thenReturn(null);

            StatusImage statusImage = iconRequestHandler.handleIconRequestForRun(
                    run, "style", "subject", "status", null, "animatedOverlayColor", "config", "link");
            assertEquals(statusImage, mockedStatusImage);
        }
    }

    @Test
    void handleIconRequestForRun3() throws NoSuchFieldException, IllegalAccessException {
        try (MockedStatic<EmbeddableBadgeConfigsAction> embeddableBadgeConfigsActionMockedStatic =
                Mockito.mockStatic(EmbeddableBadgeConfigsAction.class)) {

            ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
            StatusImage mockedStatusImage = new StatusImage();
            ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
            Run run = mock(Run.class);

            when(run.getIconColor()).thenReturn(BallColor.BLUE);

            when(mockIconResolver.getImage(
                            BallColor.BLUE, "style", "subject", "not run", "color", "animatedOverlayColor", "link"))
                    .thenReturn(mockedStatusImage);

            when(mockParameterResolver.resolve(run, "subject")).thenReturn("subject");
            when(mockParameterResolver.resolve(run, null)).thenReturn(null);
            when(mockParameterResolver.resolve(run, "animatedOverlayColor")).thenReturn("animatedOverlayColor");
            when(mockParameterResolver.resolve(run, "link")).thenReturn("link");
            when(mockParameterResolver.resolve(run, "color")).thenReturn("color");

            Field privateVariableFieldParameterResolver =
                    IconRequestHandler.class.getDeclaredField("parameterResolver");
            privateVariableFieldParameterResolver.setAccessible(true);
            privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

            Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
            privateVariableFieldIconResolver.setAccessible(true);
            privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);
            embeddableBadgeConfigsActionMockedStatic
                    .when(() -> EmbeddableBadgeConfigsAction.resolve(run, "config"))
                    .thenReturn(null);

            StatusImage statusImage = iconRequestHandler.handleIconRequestForRun(
                    run, "style", "subject", null, "color", "animatedOverlayColor", "config", "link");
            assertEquals(statusImage, mockedStatusImage);
        }
    }

    @Test
    void handleIconRequestForRun4() throws NoSuchFieldException, IllegalAccessException {
        try (MockedStatic<EmbeddableBadgeConfigsAction> embeddableBadgeConfigsActionMockedStatic =
                Mockito.mockStatic(EmbeddableBadgeConfigsAction.class)) {

            ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
            StatusImage mockedStatusImage = new StatusImage();
            ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
            Run run = mock(Run.class);

            when(run.getIconColor()).thenReturn(BallColor.BLUE);

            when(mockIconResolver.getImage(
                            BallColor.BLUE, "style", "subject", "status", "color", "animatedOverlayColor", "link"))
                    .thenReturn(mockedStatusImage);

            when(mockParameterResolver.resolve(run, "subject")).thenReturn("subject");
            when(mockParameterResolver.resolve(run, "status")).thenReturn("status");
            when(mockParameterResolver.resolve(run, "animatedOverlayColor")).thenReturn("animatedOverlayColor");
            when(mockParameterResolver.resolve(run, null)).thenReturn(null);
            when(mockParameterResolver.resolve(run, "color")).thenReturn("color");

            Field privateVariableFieldParameterResolver =
                    IconRequestHandler.class.getDeclaredField("parameterResolver");
            privateVariableFieldParameterResolver.setAccessible(true);
            privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

            Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
            privateVariableFieldIconResolver.setAccessible(true);
            privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);

            EmbeddableBadgeConfig mockEmbeddableBadgeConfig = mock(EmbeddableBadgeConfig.class);
            when(mockEmbeddableBadgeConfig.getLink()).thenReturn("link");

            embeddableBadgeConfigsActionMockedStatic
                    .when(() -> EmbeddableBadgeConfigsAction.resolve(run, "config"))
                    .thenReturn(mockEmbeddableBadgeConfig);

            StatusImage statusImage = iconRequestHandler.handleIconRequestForRun(
                    run, "style", "subject", "status", "color", "animatedOverlayColor", "config", null);
            assertEquals(statusImage, mockedStatusImage);
        }
    }

    @Test
    void handleIconRequestForRun5() throws NoSuchFieldException, IllegalAccessException {
        try (MockedStatic<EmbeddableBadgeConfigsAction> embeddableBadgeConfigsActionMockedStatic =
                Mockito.mockStatic(EmbeddableBadgeConfigsAction.class)) {

            ImageResolver mockIconResolver = Mockito.mock(ImageResolver.class);
            StatusImage mockedStatusImage = new StatusImage();
            ParameterResolver mockParameterResolver = mock(ParameterResolver.class);
            Run run = mock(Run.class);

            when(run.getIconColor()).thenReturn(BallColor.BLUE);

            when(mockIconResolver.getImage(
                            BallColor.BLUE, "style", "subject", "status", "color", "animatedOverlayColor", "link"))
                    .thenReturn(mockedStatusImage);

            when(mockParameterResolver.resolve(run, null)).thenReturn(null);

            Field privateVariableFieldParameterResolver =
                    IconRequestHandler.class.getDeclaredField("parameterResolver");
            privateVariableFieldParameterResolver.setAccessible(true);
            privateVariableFieldParameterResolver.set(iconRequestHandler, mockParameterResolver);

            Field privateVariableFieldIconResolver = IconRequestHandler.class.getDeclaredField("iconResolver");
            privateVariableFieldIconResolver.setAccessible(true);
            privateVariableFieldIconResolver.set(iconRequestHandler, mockIconResolver);

            EmbeddableBadgeConfig mockEmbeddableBadgeConfig = mock(EmbeddableBadgeConfig.class);

            when(mockEmbeddableBadgeConfig.getStatus()).thenReturn("status");
            when(mockEmbeddableBadgeConfig.getColor()).thenReturn("color");
            when(mockEmbeddableBadgeConfig.getLink()).thenReturn("link");
            when(mockEmbeddableBadgeConfig.getSubject()).thenReturn("subject");
            when(mockEmbeddableBadgeConfig.getAnimatedOverlayColor()).thenReturn("animatedOverlayColor");

            embeddableBadgeConfigsActionMockedStatic
                    .when(() -> EmbeddableBadgeConfigsAction.resolve(run, "config"))
                    .thenReturn(mockEmbeddableBadgeConfig);

            StatusImage statusImage = iconRequestHandler.handleIconRequestForRun(
                    run, "style", "subject", null, "color", "animatedOverlayColor", "config", "link");
            assertEquals(statusImage, mockedStatusImage);
        }
    }
}
