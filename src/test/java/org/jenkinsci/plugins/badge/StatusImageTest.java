/*
 * The MIT License
 *
 * Copyright 2022 Mark Waite.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.badge;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class StatusImageTest {

    private static final String SVG_CONTENT_TYPE = "image/svg+xml;charset=utf-8";
    private static final Random RANDOM = new Random();

    @SuppressWarnings("unused")
    private static JenkinsRule jenkinsRule;

    @BeforeAll
    static void beforeAll(JenkinsRule rule) {
        jenkinsRule = rule;
    }

    @BeforeEach
    void setUp() throws Exception {
        // Reset the static font cache before each test to ensure clean state
        resetFontCache();
    }

    /**
     * Helper method to reset the cached font metrics for testing
     */
    private void resetFontCache() throws Exception {
        Field cachedFontMetricsField = StatusImage.class.getDeclaredField("cachedFontMetrics");
        cachedFontMetricsField.setAccessible(true);
        cachedFontMetricsField.set(null, null);
    }

    @Test
    void testMeasureText() throws Exception {
        StatusImage statusImage = new StatusImage();
        assertThat(statusImage.measureText("W"), is(9)); // 11 in Verdana
        assertThat(statusImage.measureText("M"), is(9));
        assertThat(statusImage.measureText("X"), is(7)); // 8 in Verdana
        assertThat(statusImage.measureText("T"), is(7));
        assertThat(statusImage.measureText("|"), is(4)); // 5 in Verdana
        assertThat(statusImage.measureText(":"), is(4)); // 5 in Verdana
        assertThat(statusImage.measureText(";"), is(4)); // 5 in Verdana
        assertThat(statusImage.measureText("t"), is(4));
        assertThat(statusImage.measureText(","), is(4));
        assertThat(statusImage.measureText("WWWWWWWWWW"), is(90)); // 110 in Verdana
        assertThat(
                statusImage.measureText("When in the course of human events it becomes necessary"),
                is(330)); // 338 in Verdana
    }

    @Test
    void testConstructorExamplePage() throws Exception {
        String subject = "Custom Subject";
        String status = "Any State";
        String colorName = "darkturquoise";
        String animatedColorName = null;
        String style = null;
        String link = null;
        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        assertThat(statusImage.getEtag(), containsString(subject));
        assertThat(statusImage.getEtag(), containsString(status));
        assertThat(statusImage.getEtag(), containsString(colorName));
        assertThat(statusImage.getEtag(), containsString("null"));
        assertThat(statusImage.getContentType(), is(SVG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), allOf(greaterThan(920), lessThan(960)));
    }

    @Test
    void testConstructorFailingBuildPlasticStyle() throws Exception {
        String subject = "build";
        String status = "failing";
        String colorName = "red";
        String animatedColorName = null;
        String style = "plastic";
        String link = null;
        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        assertThat(statusImage.getEtag(), containsString(subject));
        assertThat(statusImage.getEtag(), containsString(status));
        assertThat(statusImage.getEtag(), containsString(colorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getEtag(), containsString("null"));
        assertThat(statusImage.getContentType(), is(SVG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), allOf(greaterThan(973), lessThan(1033)));
    }

    @Test
    void testConstructorFailingBuildPlasticStyleAnimatedNumericColor() throws Exception {
        String subject = "build";
        String status = "failing";
        String colorName = "red";
        String animatedColorName = "fe7d37";
        String style = "plastic";
        String link = null;
        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        assertThat(statusImage.getEtag(), containsString(subject));
        assertThat(statusImage.getEtag(), containsString(status));
        assertThat(statusImage.getEtag(), containsString(colorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getEtag(), containsString(animatedColorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getContentType(), is(SVG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), allOf(greaterThan(1237), lessThan(1297)));
    }

    @Test
    void testConstructorFailingBuildPlasticStyleNumericColorAnimatedNumericColor() throws Exception {
        String subject = "build";
        String status = "failing";
        String colorName = "ff0000";
        String animatedColorName = "fe7d37";
        String style = "plastic";
        String link = null;
        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        assertThat(statusImage.getEtag(), containsString(subject));
        assertThat(statusImage.getEtag(), containsString(status));
        assertThat(statusImage.getEtag(), containsString(colorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getEtag(), containsString(animatedColorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getContentType(), is(SVG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), allOf(greaterThan(1237), lessThan(1297)));
    }

    @Test
    void testConstructorFailingBuildPlasticStyleAnimatedColor() throws Exception {
        String subject = "build";
        String status = "failing";
        String colorName = "red";
        String animatedColorName = "yellow";
        String style = "plastic";
        String link = null;
        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        assertThat(statusImage.getEtag(), containsString(subject));
        assertThat(statusImage.getEtag(), containsString(status));
        assertThat(statusImage.getEtag(), containsString(colorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getEtag(), containsString(animatedColorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getContentType(), is(SVG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), allOf(greaterThan(1237), lessThan(1297)));
    }

    @Test
    void testConstructorFailingBuildPlasticStyleNumericColor() throws Exception {
        String subject = "build";
        String status = "failing";
        String colorName = "ff0000";
        String animatedColorName = null;
        String style = "plastic";
        String link = null;
        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        assertThat(statusImage.getEtag(), containsString(subject));
        assertThat(statusImage.getEtag(), containsString(status));
        assertThat(statusImage.getEtag(), containsString(colorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getEtag(), containsString("null"));
        assertThat(statusImage.getContentType(), is(SVG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), allOf(greaterThan(973), lessThan(1033)));
    }

    @Test
    void testConstructorPassingBuildPlasticStyle() throws Exception {
        String subject = "build";
        String status = "passing";
        String colorName = "brightgreen";
        String animatedColorName = null;
        String style = "plastic";
        String link = null;
        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        assertThat(statusImage.getEtag(), containsString(subject));
        assertThat(statusImage.getEtag(), containsString(status));
        assertThat(statusImage.getEtag(), containsString(colorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getEtag(), containsString("null"));
        assertThat(statusImage.getContentType(), is(SVG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), allOf(greaterThan(973), lessThan(1033)));
    }

    @Test
    void testConstructorFailingBuildFlatSquareStyle() throws Exception {
        String subject = "build";
        String status = "failing";
        String colorName = "red";
        String animatedColorName = null;
        String style = "flat-square";
        String link = null;
        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        assertThat(statusImage.getEtag(), containsString(subject));
        assertThat(statusImage.getEtag(), containsString(status));
        assertThat(statusImage.getEtag(), containsString(colorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getEtag(), containsString("null"));
        assertThat(statusImage.getContentType(), is(SVG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), allOf(greaterThan(510), lessThan(540)));
    }

    @Test
    void testConstructorPassingBuildFlatSquareStyle() throws Exception {
        String subject = "build";
        String status = "passing";
        String colorName = "brightgreen";
        String animatedColorName = null;
        String style = "flat-square";
        String link = null;
        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        assertThat(statusImage.getEtag(), containsString(subject));
        assertThat(statusImage.getEtag(), containsString(status));
        assertThat(statusImage.getEtag(), containsString(colorName));
        assertThat(statusImage.getEtag(), containsString(style));
        assertThat(statusImage.getEtag(), containsString("null"));
        assertThat(statusImage.getContentType(), is(SVG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), allOf(greaterThan(510), lessThan(540)));
    }

    @Test
    void testConstructor() throws Exception {
        String subject = "build";
        String status = "not run";
        String colorName = "lightgrey";
        String animatedColorName = null;
        String style = null;
        String link = null;
        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        assertThat(statusImage.getEtag(), containsString(subject));
        assertThat(statusImage.getEtag(), containsString(status));
        assertThat(statusImage.getEtag(), containsString(colorName));
        assertThat(statusImage.getEtag(), containsString("null"));
        assertThat(statusImage.getContentType(), is(SVG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), allOf(greaterThan(875), lessThan(925)));
    }

    // private static final String PNG_CONTENT_TYPE = "image/png";

    @Test
    void testConstructorPassingBuild32x32BallStyle() throws Exception {
        String fileName = "images/32x32/blue.png";
        StatusImage statusImage = new StatusImage(fileName);
        assertThat(statusImage.getEtag(), containsString(fileName));
        // assertThat(statusImage.getContentType(), is(PNG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), is(1656));
    }

    @Test
    void testConstructorPassingBuild16x16BallStyle() throws Exception {
        String fileName = "images/16x16/blue.png";
        StatusImage statusImage = new StatusImage(fileName);
        assertThat(statusImage.getEtag(), containsString(fileName));
        // assertThat(statusImage.getContentType(), is(PNG_CONTENT_TYPE));
        assertThat(Integer.valueOf(statusImage.getLength()), is(656));
    }

    // ========================================================================
    // Font Loading Tests - Added for performance fix verification
    // ========================================================================
    @Test
    void testConcurrentFontLoading() throws Exception {
        // Test thread safety of font loading mechanism
        final int threadCount = 10 + RANDOM.nextInt(10);
        final String testText = "Concurrent Test " + RANDOM.nextInt();
        final int textWidth = new StatusImage().measureText(testText);
        assertThat(textWidth, is(greaterThan(5 * testText.length())));
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch doneLatch = new CountDownLatch(threadCount);
        final AtomicReference<Exception> exception = new AtomicReference<>();
        final AtomicInteger successCount = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        try {
            // Start multiple threads that will all try to measure text concurrently
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        startLatch.await(); // Wait for all threads to be ready

                        assertThat(new StatusImage().measureText(testText), is(textWidth));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        exception.set(e);
                    } finally {
                        doneLatch.countDown();
                    }
                });
            }

            startLatch.countDown(); // Start all threads

            boolean completed = doneLatch.await(10, TimeUnit.SECONDS);
            assertThat("All threads should complete within timeout", completed, is(true));
            assertThat("Exception thrown during thread test", exception.get(), is(nullValue()));
            assertThat("All threads should succeed in measuring text", successCount.get(), is(threadCount));

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    @Test
    void testFontLoadingRobustness() throws Exception {
        // Test that font loading is robust to various text inputs
        StatusImage statusImage = new StatusImage();

        assertThat("Unexpected empty string width", statusImage.measureText(""), is(0));
        assertThat("Unexpected single space string width", statusImage.measureText(" "), is(3));
        assertThat("Unexpected single 'i' string width", statusImage.measureText("i"), is(3));

        String[] testInputs = {
            "A", // Single character
            "M", // Single character
            "W", // Single character
            "The quick brown fox", // Normal text
            "Text with numbers " + RANDOM.nextInt(), // Alphanumeric
            "Special chars: !@#$%^&*()", // Special characters
            "Unicode: αβγδε", // Unicode characters
            "Very long text that goes on and on and should still be measured correctly " + RANDOM.nextDouble()
        };

        for (String input : testInputs) {
            int width = statusImage.measureText(input);
            assertThat("Insufficient width for input: " + input, width, greaterThan(input.length() * 5));
        }
    }

    // ========================================================================
    // CSP Compliance Tests - Added for inline event handler removal
    // ========================================================================

    @Test
    void testStatusImageWithValidLinkHasCSPCompliantAttributes() throws Exception {
        String subject = "build";
        String status = "passing";
        String colorName = "brightgreen";
        String animatedColorName = null;
        String style = "flat";
        String link = "https://example.com/build";

        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        String svgContent = getPayloadAsString(statusImage);

        // Verify CSP-compliant attributes are present
        assertThat(
                "SVG should have jenkins-badge-clickable class",
                svgContent,
                containsString("class=\"jenkins-badge-clickable\""));
        assertThat(
                "SVG should have data-jenkins-link-url attribute",
                svgContent,
                containsString("data-jenkins-link-url=\"" + link + "\""));
        assertThat("SVG should have cursor pointer style", svgContent, containsString("style=\"cursor: pointer;\""));

        // Verify no inline event handlers
        assertThat("SVG should not contain onclick attribute", svgContent, not(containsString("onclick")));
        assertThat("SVG should not contain window.open call", svgContent, not(containsString("window.open")));
    }

    @Test
    void testStatusImageWithHttpLinkHasCSPCompliantAttributes() throws Exception {
        String subject = "test";
        String status = "failing";
        String colorName = "red";
        String animatedColorName = null;
        String style = "plastic";
        String link = "http://insecure.example.com/build";

        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        String svgContent = getPayloadAsString(statusImage);

        // Verify CSP-compliant attributes are present for HTTP links too
        assertThat(
                "SVG should have jenkins-badge-clickable class for HTTP links",
                svgContent,
                containsString("class=\"jenkins-badge-clickable\""));
        assertThat(
                "SVG should have data-jenkins-link-url attribute for HTTP links",
                svgContent,
                containsString("data-jenkins-link-url=\"" + link + "\""));

        // Verify no inline event handlers
        assertThat(
                "SVG should not contain onclick attribute for HTTP links", svgContent, not(containsString("onclick")));
    }

    @Test
    void testStatusImageWithNullLinkHasNoCSPAttributes() throws Exception {
        String subject = "build";
        String status = "passing";
        String colorName = "brightgreen";
        String animatedColorName = null;
        String style = "flat";
        String link = null;

        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        String svgContent = getPayloadAsString(statusImage);

        // Verify no CSP attributes when no link provided
        assertThat(
                "SVG should not have jenkins-badge-clickable class when no link",
                svgContent,
                not(containsString("jenkins-badge-clickable")));
        assertThat(
                "SVG should not have data-jenkins-link-url attribute when no link",
                svgContent,
                not(containsString("data-jenkins-link-url")));
        assertThat(
                "SVG should not have cursor pointer style when no link",
                svgContent,
                not(containsString("cursor: pointer")));

        // Verify no inline event handlers
        assertThat("SVG should not contain onclick attribute when no link", svgContent, not(containsString("onclick")));
    }

    @Test
    void testStatusImageWithInvalidProtocolHasNoCSPAttributes() throws Exception {
        String subject = "build";
        String status = "passing";
        String colorName = "brightgreen";
        String animatedColorName = null;
        String style = "flat";
        String link = "javascript:alert('xss')";

        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        String svgContent = getPayloadAsString(statusImage);

        // Verify no CSP attributes when invalid protocol provided
        assertThat(
                "SVG should not have jenkins-badge-clickable class for invalid protocol",
                svgContent,
                not(containsString("jenkins-badge-clickable")));
        assertThat(
                "SVG should not have data-jenkins-link-url attribute for invalid protocol",
                svgContent,
                not(containsString("data-jenkins-link-url")));
        assertThat(
                "SVG should not have cursor pointer style for invalid protocol",
                svgContent,
                not(containsString("cursor: pointer")));

        // Verify no inline event handlers
        assertThat(
                "SVG should not contain onclick attribute for invalid protocol",
                svgContent,
                not(containsString("onclick")));
    }

    @Test
    void testStatusImageWithMalformedLinkHasNoCSPAttributes() throws Exception {
        String subject = "build";
        String status = "passing";
        String colorName = "brightgreen";
        String animatedColorName = null;
        String style = "flat";
        String link = "not-a-valid-url";

        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        String svgContent = getPayloadAsString(statusImage);

        // Verify no CSP attributes when malformed URL provided
        assertThat(
                "SVG should not have jenkins-badge-clickable class for malformed URL",
                svgContent,
                not(containsString("jenkins-badge-clickable")));
        assertThat(
                "SVG should not have data-jenkins-link-url attribute for malformed URL",
                svgContent,
                not(containsString("data-jenkins-link-url")));
        assertThat(
                "SVG should not have cursor pointer style for malformed URL",
                svgContent,
                not(containsString("cursor: pointer")));

        // Verify no inline event handlers
        assertThat(
                "SVG should not contain onclick attribute for malformed URL",
                svgContent,
                not(containsString("onclick")));
    }

    @Test
    void testStatusImageWithLinkEscapesHtmlProperly() throws Exception {
        String subject = "build";
        String status = "passing";
        String colorName = "brightgreen";
        String animatedColorName = null;
        String style = "flat";
        String link = "https://example.com/path?param=\"value\"&other=<test>";

        StatusImage statusImage = new StatusImage(subject, status, colorName, animatedColorName, style, link);
        String svgContent = getPayloadAsString(statusImage);

        // Verify HTML escaping is preserved in the link attribute (double-escaped as per StatusImage.java line 106)
        assertThat(
                "SVG should have properly escaped link URL",
                svgContent,
                containsString(
                        "data-jenkins-link-url=\"https://example.com/path?param=&amp;quot;value&amp;quot;&amp;amp;other=&amp;lt;test&amp;gt;\""));

        // Verify CSP-compliant attributes are still present
        assertThat(
                "SVG should still have jenkins-badge-clickable class with special characters",
                svgContent,
                containsString("class=\"jenkins-badge-clickable\""));

        // Verify no inline event handlers
        assertThat(
                "SVG should not contain onclick attribute with special characters",
                svgContent,
                not(containsString("onclick")));
    }

    /**
     * Helper method to access the payload for testing
     */
    private byte[] getPayload(StatusImage statusImage) throws Exception {
        Field payloadField = StatusImage.class.getDeclaredField("payload");
        payloadField.setAccessible(true);
        return (byte[]) payloadField.get(statusImage);
    }

    /**
     * Test helper method to get payload content
     */
    private String getPayloadAsString(StatusImage statusImage) throws Exception {
        return new String(getPayload(statusImage), StandardCharsets.UTF_8);
    }
}
