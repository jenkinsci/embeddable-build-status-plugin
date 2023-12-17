package org.jenkinsci.plugins.badge;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class EmbeddableBadgeConfigTest {
    @Test
    public void testConstructor() {
        String id = "testId-constructor";
        EmbeddableBadgeConfig embeddableBadgeConfig = new EmbeddableBadgeConfig(id);
        assertThat(embeddableBadgeConfig.getID(), is(id));
        assertThat(embeddableBadgeConfig.getSubject(), is(nullValue()));
        assertThat(embeddableBadgeConfig.getStatus(), is(nullValue()));
        assertThat(embeddableBadgeConfig.getAnimatedOverlayColor(), is(nullValue()));
        assertThat(embeddableBadgeConfig.getLink(), is(nullValue()));
        assertThat(embeddableBadgeConfig.getColor(), is(nullValue()));
    }

    @Test
    public void testGetAnimatedOverlayColorBlueForRunning() {
        EmbeddableBadgeConfig embeddableBadgeConfig = new EmbeddableBadgeConfig("testId-running-blue");
        embeddableBadgeConfig.setStatus("running");
        assertThat(embeddableBadgeConfig.getAnimatedOverlayColor(), is("blue"));
    }

    @ParameterizedTest
    @CsvSource({"failing,red", "passing,brightgreen", "unstable,yellow", "aborted,aborted", "running,blue"})
    public void testGetColor(String status, String expected) {
        EmbeddableBadgeConfig embeddableBadgeConfig = new EmbeddableBadgeConfig("testId-status-" + status);
        embeddableBadgeConfig.setStatus(status);
        assertThat(embeddableBadgeConfig.getColor(), is(expected));
    }

    @Test
    public void testSetSubject() {
        String subject = "Test Subject";
        EmbeddableBadgeConfig embeddableBadgeConfig = new EmbeddableBadgeConfig("testId-subject");
        embeddableBadgeConfig.setSubject(subject);
        assertThat(embeddableBadgeConfig.getSubject(), is(subject));
    }

    @Test
    public void testSetValidLink() {
        String link = "https://jenkins.io";
        EmbeddableBadgeConfig config = new EmbeddableBadgeConfig("test-valid-link");
        config.setLink(link);
        assertThat(config.getLink(), is(link));
    }

    @Test
    public void testSetEmptyLink() {
        String link = "";
        EmbeddableBadgeConfig config = new EmbeddableBadgeConfig("test-empty-link");
        config.setLink(link);
        assertThat(config.getLink(), is(link));
    }

    @Test
    public void testSetValidDefaultColor() {
        String color = "red";
        EmbeddableBadgeConfig config = new EmbeddableBadgeConfig("test-valid-default-color");
        config.setColor(color);
        assertThat(config.getColor(), is(color));
    }

    @Test
    public void testSetValidCustomColor() {
        String color = "#ff00ff"; // Magenta
        EmbeddableBadgeConfig config = new EmbeddableBadgeConfig("test-valid-custom-color");
        config.setColor(color);
        assertThat(config.getColor(), is(color));
    }

    @Test
    public void testSetEmptyColor() {
        String color = "";
        EmbeddableBadgeConfig config = new EmbeddableBadgeConfig("test-empty-color");
        config.setColor(color);
        assertThat(config.getColor(), is(""));
    }

    @Test
    public void testSetNullColor() {
        EmbeddableBadgeConfig config = new EmbeddableBadgeConfig("test-null-color");
        config.setColor(null);
        assertThat(config.getColor(), is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidColorName() {
        String color = "invalid_color";
        EmbeddableBadgeConfig config = new EmbeddableBadgeConfig("test-invalid-color-name");
        config.setColor(color);
    }
}
