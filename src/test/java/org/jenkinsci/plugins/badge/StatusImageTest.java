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
import static org.hamcrest.Matchers.nullValue;

import java.lang.reflect.Field;
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
        final int threadCount = 10;
        final String testText = "Concurrent Test " + RANDOM.nextInt();
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

                        StatusImage statusImage = new StatusImage();
                        int width = statusImage.measureText(testText);

                        if (width > 0) {
                            successCount.incrementAndGet();
                        }
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

    @Test
    void testFontLoadingCaching() throws Exception {
        // Test that font metrics are properly cached and reused
        StatusImage statusImage = new StatusImage();

        String testText = "Caching Test " + RANDOM.nextInt();
        int referenceMeasurement = statusImage.measureText(testText);

        // Make multiple calls and verify they're consistent (indicating caching is working)
        // All measurements should be identical if caching is working
        for (int i = 5 + RANDOM.nextInt(10); i > 0; i--) {
            assertThat(
                    "Measurement " + i + " does not match reference",
                    statusImage.measureText(testText),
                    is(referenceMeasurement));
        }
    }
}
