package org.jenkinsci.plugins.badge;

import org.junit.ClassRule;
import hudson.model.BallColor;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.CoreMatchers.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

class ImageResolverBallDummy extends ImageResolver{
    @Override
    public StatusImage getImage(BallColor color, String style, String subject, String status, String colorName,
            String animatedOverlayColor, String link) {

        if (style != null) {
            String[] styleParts = style.split("-");
            if (styleParts.length == 2 && styleParts[0].equals("ball")) {
                String url =  "images/" + styleParts[1] + '/' + color.getImage();
                if (url.contains("null")) {
                    url =   "images/" + "32x32" + '/' + color.getImage();
                }

                if (url != null) {
                    try {
                        return new StatusImage(url);
                    } catch (IOException ioe) {
                        return new StatusImage();
                    }
                }
            }
        }
        style = null;
        return super.getImage(color, style, subject, status, colorName, animatedOverlayColor, link);
    }

}
public class ImageResolverTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public TestName testName = new TestName();

    @Test
    public void TestGetDefault32x32Ball() throws Exception {
        ImageResolverBallDummy ImageTester = new ImageResolverBallDummy();
        String style = "ball-null"; // should give default due to url being null
        String status = null;
        String subject = null;
        String colorName = null;
        String fileName = "images/32x32/blue.png";
        BallColor ball=BallColor.BLUE;
        StatusImage TestImage = ImageTester.getImage(ball, style, subject, status, colorName, null, null);
        assertThat(TestImage.getEtag(), containsString(fileName));

    }

    @Test
    public void TestGetNonDefaultBall() throws Exception {
        ImageResolverBallDummy ImageTester = new ImageResolverBallDummy();
        String style = "ball-16x16"; // should give url
        String status = null;
        String subject = null;
        String colorName = null;
        String fileName = "images/16x16/red.png";
        BallColor ball=BallColor.RED;
        StatusImage TestImage = ImageTester.getImage(ball, style, subject, status, colorName, null, null);
        assertThat(TestImage.getEtag(), containsString(fileName));

    }

    @Test
    public void testShouldReturnEmpty() throws Exception {
        ImageResolverBallDummy ImageTester = new ImageResolverBallDummy();
        String style = "ball-42x45"; // should throw exception
        String status = null;
        String subject = null;
        String colorName = null;
        String fileName = "DoesNotExist";
        BallColor ball=BallColor.BLUE;
        StatusImage TestImage = ImageTester.getImage(ball, style, subject, status, colorName, null, null);
        assertThat(TestImage.getEtag(), containsString("empty"));

    }

    private final Random random = new Random();

    private String getInvalidStyle() {
        return random.nextBoolean() ? "not-a-valid-style" : null;
    }

    private String getSubject() {
        return "subject-is-" + testName.getMethodName();
    }

    private String getStatus() {
        return "status-is-" + testName.getMethodName();
    }

    private BallColor[] jobStatusColors = {
        BallColor.ABORTED_ANIME,
        BallColor.DISABLED_ANIME,
        BallColor.NOTBUILT_ANIME,
    };

    private BallColor getJobStatusColor() {
        return jobStatusColors[random.nextInt(jobStatusColors.length)]; // A job status animated color
    }

    private BallColor[] animatedColors = {
        BallColor.ABORTED_ANIME,
        BallColor.DISABLED_ANIME,
        BallColor.NOTBUILT_ANIME,
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
    public void testGetImage() throws IOException { // Without a style
        String style = getInvalidStyle();
        BallColor color = BallColor.BLUE; // Not an animated color
        String subject = getSubject();
        String status = getStatus();
        String colorName = BallColor.YELLOW.toString();
        String animatedOverlayColor = BallColor.YELLOW_ANIME.toString();
        String link = null; // "https://www.example.com/my-link";
        ImageResolver imageResolver = new ImageResolver();
        StatusImage image = imageResolver.getImage(color, style, subject, status, colorName, animatedOverlayColor, link);
        assertThat(image.getEtag(), containsString(subject));
        assertThat(image.getEtag(), containsString(status));
        assertThat(image.getEtag(), containsString(colorName));
        assertThat(image.getContentType(), is("image/svg+xml;charset=utf-8"));
        assertThat(image.measureText("W"), is(9));
    }

    @Test
    public void testGetImageAnimatedJobStatusColor() throws IOException {
        String style = getInvalidStyle();
        BallColor color = getAnimatedColor();
        String subject = getSubject();
        String status = getStatus();
        String colorName = null;
        String animatedOverlayColor = getJobStatusColor().toString();
        String link = null;
        ImageResolver imageResolver = new ImageResolver();
        StatusImage image = imageResolver.getImage(color, style, subject, status, colorName, animatedOverlayColor, link);
        assertThat(image.getEtag(), containsString(subject));
        assertThat(image.getEtag(), containsString(status));
        assertThat(image.getEtag(), containsString("lightgrey")); // Not built color
        assertThat(image.getEtag(), containsString(animatedOverlayColor));
        assertThat(image.getContentType(), is("image/svg+xml;charset=utf-8"));
        assertThat(image.measureText("W"), is(9));
    }

    @Test
    public void testGetImageLightGrey() throws IOException {
        String style = getInvalidStyle();
        BallColor color = getLightGreyBallColor();
        String subject = getSubject();
        String status = getStatus();
        String colorName = null; // BallColor.YELLOW.toString();
        String animatedOverlayColor = BallColor.YELLOW_ANIME.toString();
        String link = null; // "https://www.example.com/my-link";
        ImageResolver imageResolver = new ImageResolver();
        StatusImage image = imageResolver.getImage(color, style, subject, status, colorName, animatedOverlayColor, link);
        assertThat(image.getEtag(), containsString(subject));
        assertThat(image.getEtag(), containsString(status));
        assertThat(image.getEtag(), containsString("lightgrey")); // Not built color
        assertThat(image.getEtag(), containsString(animatedOverlayColor));
        assertThat(image.getContentType(), is("image/svg+xml;charset=utf-8"));
        assertThat(image.measureText("W"), is(9));
    }
}
