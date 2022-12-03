package org.jenkinsci.plugins.badge;

import org.junit.ClassRule;
import hudson.model.BallColor;
import java.io.IOException;
import java.util.Random;

import org.junit.Test;

import org.jvnet.hudson.test.JenkinsRule;

import org.mockito.Mockito;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

public class ImageResolverTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    // @Test
    public void TestGetDefault32x32Ball() throws Exception {
        ImageResolver ImageTester = new ImageResolver();
        String style = "ball-null"; // should give default due to url being null
        String status = null;
        String subject = null;
        String colorName = null;
        String fileName = "images/32x32/blue.png";
        BallColor ball = mock(BallColor.class);
        //default
        Mockito.when(ball.getImageOf("null")).thenReturn(null);
        Mockito.when(ball.getImageOf("32x32")).thenReturn(fileName);
        Mockito.when(ball.noAnime()).thenCallRealMethod();
        StatusImage TestImage = ImageTester.getImage(ball, style, subject, status, colorName, null, null);
        assertThat(TestImage.getEtag(), containsString(fileName));

    }

    // @Test
    public void TestGetNonDefaultBall() throws Exception {
        ImageResolver ImageTester = new ImageResolver();
        String style = "ball-16x16"; // should give url
        String status = null;
        String subject = null;
        String colorName = null;
        String fileName = "images/16x16/red.png";
        BallColor ball = mock(BallColor.class);
        //default
        Mockito.when(ball.getImageOf("null")).thenReturn(null);
        Mockito.when(ball.getImageOf("16x16")).thenReturn(fileName);
        Mockito.when(ball.noAnime()).thenCallRealMethod();
        StatusImage TestImage = ImageTester.getImage(ball, style, subject, status, colorName, null, null);
        assertThat(TestImage.getEtag(), containsString(fileName));

    }

    // @Test
    public void testShouldReturnEmpty() throws Exception {
        ImageResolver ImageTester = new ImageResolver();
        String style = "ball-42x45"; // should throw exception
        String status = null;
        String subject = null;
        String colorName = null;
        String fileName = "DoesNotExist";;
        BallColor ball = mock(BallColor.class);
        //default
        Mockito.when(ball.getImageOf("null")).thenReturn(null);
        Mockito.when(ball.getImageOf("42x45")).thenReturn(fileName);
        Mockito.when(ball.noAnime()).thenCallRealMethod();
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
        return jobStatusColors[0]; // random.nextInt(jobStatusColors.length)]; // A job status animated color
    }

    private BallColor[] animatedColors = {
        BallColor.ABORTED_ANIME,
        BallColor.DISABLED_ANIME,
        BallColor.NOTBUILT_ANIME,
        BallColor.BLUE_ANIME,
        BallColor.GREY_ANIME,
        BallColor.RED_ANIME,
        BallColor.YELLOW_ANIME,
    };

    private BallColor getAnimatedColor() {
        return animatedColors[0]; // random.nextInt(animatedColors.length)]; // An animated color
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
        return lightGreyEquivalents[0]; // random.nextInt(lightGreyEquivalents.length)]; // A light grey equivalent ball color
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
