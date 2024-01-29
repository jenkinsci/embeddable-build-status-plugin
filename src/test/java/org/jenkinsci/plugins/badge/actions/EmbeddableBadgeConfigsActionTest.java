package org.jenkinsci.plugins.badge.actions;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hudson.model.Run;
import org.jenkinsci.plugins.badge.EmbeddableBadgeConfig;
import org.junit.jupiter.api.Test;

class EmbeddableBadgeConfigsActionTest {

    @Test
    void testGetUrlName() {
        // Given
        EmbeddableBadgeConfigsAction action = new EmbeddableBadgeConfigsAction();

        // When
        String urlName = action.getUrlName();

        // Then
        assertEquals("", urlName, "UrlName should be an empty string");
    }

    @Test
    void testGetDisplayName() {
        // Given
        EmbeddableBadgeConfigsAction action = new EmbeddableBadgeConfigsAction();

        // When
        String displayName = action.getDisplayName();

        // Then
        assertEquals("", displayName, "DisplayName should be an empty string");
    }

    @Test
    void testGetIconFileName() {
        // Given
        EmbeddableBadgeConfigsAction action = new EmbeddableBadgeConfigsAction();

        // When
        String iconFileName = action.getIconFileName();

        // Then
        assertNull(iconFileName, "IconFileName should be null");
    }

    @Test
    void testResolveWithValidId() {
        // Given
        Run<?, ?> run = mock(Run.class);
        EmbeddableBadgeConfigsAction action = mock(EmbeddableBadgeConfigsAction.class);
        when(run.getAction(EmbeddableBadgeConfigsAction.class)).thenReturn(action);
        String id = "someId";
        EmbeddableBadgeConfig expectedConfig = mock(EmbeddableBadgeConfig.class);
        when(action.getConfig(id)).thenReturn(expectedConfig);

        // When
        EmbeddableBadgeConfig actualConfig = EmbeddableBadgeConfigsAction.resolve(run, id);

        // Then
        assertEquals(expectedConfig, actualConfig);
    }

    @Test
    void testResolveWithNullId() {
        // Given
        Run<?, ?> run = mock(Run.class);

        // When
        EmbeddableBadgeConfig actualConfig = EmbeddableBadgeConfigsAction.resolve(run, null);

        // Then
        assertNull(actualConfig);
    }

    @Test
    void testResolveWithNullAction() {
        // Given
        Run<?, ?> run = mock(Run.class);
        when(run.getAction(EmbeddableBadgeConfigsAction.class)).thenReturn(null);
        String id = "someId";

        // When
        EmbeddableBadgeConfig actualConfig = EmbeddableBadgeConfigsAction.resolve(run, id);

        // Then
        assertNull(actualConfig);
    }

    @Test
    void testGetConfig() {
        // Given
        EmbeddableBadgeConfigsAction action = new EmbeddableBadgeConfigsAction();
        EmbeddableBadgeConfig config = mock(EmbeddableBadgeConfig.class);
        when(config.getID()).thenReturn("someId");
        action.addConfig(config);

        // When
        EmbeddableBadgeConfig actualConfig = action.getConfig("someId");

        // Then
        assertNotNull(actualConfig, "Config should not be null");
        assertEquals(config, actualConfig, "Config objects should be equal");
        assertEquals("someId", actualConfig.getID(), "Config ID should match");
    }

    @Test
    void testAddConfig() {
        // Given
        EmbeddableBadgeConfigsAction action = new EmbeddableBadgeConfigsAction();
        EmbeddableBadgeConfig config = mock(EmbeddableBadgeConfig.class);
        when(config.getID()).thenReturn("someId");

        // When
        action.addConfig(config);

        // Then
        EmbeddableBadgeConfig actualConfig = action.getConfig("someId");
        assertNotNull(actualConfig, "Config should not be null");
        assertEquals(config, actualConfig, "Config objects should be equal");
        assertEquals("someId", actualConfig.getID(), "Config ID should match");
    }
}
