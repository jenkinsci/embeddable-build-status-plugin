package org.jenkinsci.plugins.badge;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import hudson.model.BallColor;
import java.io.IOException;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class ImageResolverTest {

    private static JenkinsRule jenkinsRule;
    private String testName;

    @BeforeAll
    static void beforeAll(JenkinsRule rule) {
        jenkinsRule = rule;
    }

    @BeforeEach
    void setTestName(TestInfo info) {
        testName = info.getTestMethod().orElseThrow().getName();
    }

    @Test
    void TestGetDefault32x32Ball() throws Exception {
        try (JenkinsRule.WebClient wc = jenkinsRule.createWebClient()) {
            String style = "ball"; // should give default due to invalid size in the style
            String subject = getSubject();
            String status = getStatus();
            String colorName = null;
            BallColor ball = BallColor.BLUE;
            StatusImage image = wc.executeOnServer(() -> {
                ImageResolver imageResolver = new ImageResolver();
                return imageResolver.getImage(ball, style, subject, status, colorName, null, null);
            });
            assertThat(image.getEtag(), containsString(subject));
            assertThat(image.getEtag(), containsString(status));
            assertThat(image.getEtag(), containsString("null"));
        }
    }

    @Test
    void TestGetNonDefaultBall() throws Exception {
        try (JenkinsRule.WebClient wc = jenkinsRule.createWebClient()) {
            String sizeHint = "16x16";
            String style = "ball-" + sizeHint; // should give url
            String subject = getSubject();
            String status = getStatus();
            BallColor ball = BallColor.RED;
            String colorName = ball.toString();
            StatusImage image = wc.executeOnServer(() -> {
                ImageResolver imageResolver = new ImageResolver();
                return imageResolver.getImage(ball, style, subject, status, colorName, null, null);
            });
            assertThat(image.getEtag(), not(containsString(subject)));
            assertThat(image.getEtag(), not(containsString(status)));
            assertThat(image.getEtag(), containsString("images/" + sizeHint + "/" + colorName + ".png"));
        }
    }

    @Test
    void testShouldReturnEmpty() throws Exception {
        try (JenkinsRule.WebClient wc = jenkinsRule.createWebClient()) {
            String style = "ball-42x45"; // invalid size hint will return default empty image
            String subject = getSubject();
            String status = getStatus();
            String colorName = null;
            BallColor ball = BallColor.BLUE;
            StatusImage image = wc.executeOnServer(() -> {
                ImageResolver imageResolver = new ImageResolver();
                return imageResolver.getImage(ball, style, subject, status, colorName, null, null);
            });
            assertThat(image.getEtag(), not(containsString(subject)));
            assertThat(image.getEtag(), not(containsString(status)));
            assertThat(image.getEtag(), containsString("empty"));
        }
    }

    private final Random random = new Random();

    private String getInvalidStyle() {
        return random.nextBoolean() ? "not-a-valid-style" : null;
    }

    private String getSubject() {
        return "subject-is-" + testName;
    }

    private String getStatus() {
        return "status-is-" + testName;
    }

    private final BallColor[] jobStatusColors = {
        BallColor.ABORTED_ANIME, BallColor.DISABLED_ANIME, BallColor.NOTBUILT_ANIME,
    };

    private BallColor getJobStatusColor() {
        return jobStatusColors[random.nextInt(jobStatusColors.length)]; // A job status animated color
    }

    private final BallColor[] animatedColors = {
        BallColor.ABORTED_ANIME, BallColor.DISABLED_ANIME, BallColor.NOTBUILT_ANIME,
    };

    private BallColor getAnimatedColor() {
        return animatedColors[random.nextInt(animatedColors.length)]; // An animated color with specific meaning
    }

    /* Any one of these colors will result in a lightgrey colored image */
    private final BallColor[] lightGreyEquivalents = {
        BallColor.ABORTED,
        BallColor.ABORTED_ANIME,
        BallColor.DISABLED,
        BallColor.DISABLED_ANIME,
        BallColor.NOTBUILT,
        BallColor.NOTBUILT_ANIME,
    };

    private BallColor getLightGreyBallColor() {
        return lightGreyEquivalents[random.nextInt(lightGreyEquivalents.length)]; // A light grey equivalent ball color
    }

    @Test
    void testGetImage() throws IOException { // Without a style
        String style = getInvalidStyle();
        BallColor color = BallColor.BLUE; // Not an animated color
        String subject = getSubject();
        String status = getStatus();
        String colorName = BallColor.YELLOW.toString();
        String animatedOverlayColor = BallColor.YELLOW_ANIME.toString();
        String link = null; // "https://www.example.com/my-link";
        ImageResolver imageResolver = new ImageResolver();
        StatusImage image =
                imageResolver.getImage(color, style, subject, status, colorName, animatedOverlayColor, link);
        assertThat(image.getEtag(), containsString(subject));
        assertThat(image.getEtag(), containsString(status));
        assertThat(image.getEtag(), containsString(colorName));
        assertThat(image.getContentType(), is("image/svg+xml;charset=utf-8"));
        assertThat(image.measureText("W"), is(9));
    }

    @Test
    void testGetImageAnimatedJobStatusColor() throws IOException {
        String style = getInvalidStyle();
        BallColor color = getAnimatedColor();
        String subject = getSubject();
        String status = getStatus();
        String colorName = null;
        String animatedOverlayColor = getJobStatusColor().toString();
        String link = null;
        ImageResolver imageResolver = new ImageResolver();
        StatusImage image =
                imageResolver.getImage(color, style, subject, status, colorName, animatedOverlayColor, link);
        assertThat(image.getEtag(), containsString(subject));
        assertThat(image.getEtag(), containsString(status));
        assertThat(image.getEtag(), containsString("lightgrey")); // Not built color
        assertThat(image.getEtag(), containsString(animatedOverlayColor));
        assertThat(image.getContentType(), is("image/svg+xml;charset=utf-8"));
        assertThat(image.measureText("W"), is(9));
    }

    @Test
    void testGetImageLightGrey() throws IOException {
        String style = getInvalidStyle();
        BallColor color = getLightGreyBallColor();
        String subject = getSubject();
        String status = getStatus();
        String colorName = null; // BallColor.YELLOW.toString();
        String animatedOverlayColor = BallColor.YELLOW_ANIME.toString();
        String link = null; // "https://www.example.com/my-link";
        ImageResolver imageResolver = new ImageResolver();
        StatusImage image =
                imageResolver.getImage(color, style, subject, status, colorName, animatedOverlayColor, link);
        assertThat(image.getEtag(), containsString(subject));
        assertThat(image.getEtag(), containsString(status));
        assertThat(image.getEtag(), containsString("lightgrey")); // Not built color
        assertThat(image.getEtag(), containsString(animatedOverlayColor));
        assertThat(image.getContentType(), is("image/svg+xml;charset=utf-8"));
        assertThat(image.measureText("W"), is(9));
    }
}
