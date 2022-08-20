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

import org.junit.ClassRule;
import org.junit.Test;

import org.jvnet.hudson.test.JenkinsRule;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;

public class StatusImageTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    public StatusImageTest() {
    }

    @Test
    public void testMeasureText() throws Exception {
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
        assertThat(statusImage.measureText("When in the course of human events it becomes necessary"), is(330)); // 338 in Verdana
    }

    private static final String SVG_CONTENT_TYPE = "image/svg+xml;charset=utf-8";

    @Test
    public void testConstructorExamplePage() throws Exception {
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
        assertThat(Integer.parseInt(statusImage.getLength()), allOf(greaterThan(920), lessThan(960)));
    }

    @Test
    public void testConstructorFailingBuildPlasticStyle() throws Exception {
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
        assertThat(Integer.parseInt(statusImage.getLength()), allOf(greaterThan(973), lessThan(1033)));
    }

    @Test
    public void testConstructorFailingBuildPlasticStyleNumericColor() throws Exception {
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
        assertThat(Integer.parseInt(statusImage.getLength()), allOf(greaterThan(973), lessThan(1033)));
    }

    @Test
    public void testConstructorPassingBuildPlasticStyle() throws Exception {
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
        assertThat(Integer.parseInt(statusImage.getLength()), allOf(greaterThan(973), lessThan(1033)));
    }

    @Test
    public void testConstructorFailingBuildFlatSquareStyle() throws Exception {
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
        assertThat(Integer.parseInt(statusImage.getLength()), allOf(greaterThan(510), lessThan(540)));
    }

    @Test
    public void testConstructorPassingBuildFlatSquareStyle() throws Exception {
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
        assertThat(Integer.parseInt(statusImage.getLength()), allOf(greaterThan(510), lessThan(540)));
    }

    @Test
    public void testConstructor() throws Exception {
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
        assertThat(Integer.parseInt(statusImage.getLength()), allOf(greaterThan(875), lessThan(925)));
    }

    // private static final String PNG_CONTENT_TYPE = "image/png";

    @Test
    public void testConstructorPassingBuild32x32BallStyle() throws Exception {
        String fileName = "/jenkins/static/3fcf04bd/images/32x32/blue.png";
        StatusImage statusImage = new StatusImage(fileName);
        assertThat(statusImage.getEtag(), containsString(fileName));
        // assertThat(statusImage.getContentType(), is(PNG_CONTENT_TYPE));
        assertThat(statusImage.getLength(), is("1656"));
    }

    @Test
    public void testConstructorPassingBuild16x16BallStyle() throws Exception {
        String fileName = "/jenkins/static/3fcf04bd/images/16x16/blue.png";
        StatusImage statusImage = new StatusImage(fileName);
        assertThat(statusImage.getEtag(), containsString(fileName));
        // assertThat(statusImage.getContentType(), is(PNG_CONTENT_TYPE));
        assertThat(statusImage.getLength(), is("656"));
    }
}
