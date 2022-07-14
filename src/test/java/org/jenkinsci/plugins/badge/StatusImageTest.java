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

public class StatusImageTest {

    @ClassRule
    public static JenkinsRule jenkinsRule = new JenkinsRule();

    public StatusImageTest() {
    }

    @Test
    public void testMeasureText() throws Exception {
        StatusImage statusImage = new StatusImage();
        assertThat(statusImage.measureText("W"), is(11));
        assertThat(statusImage.measureText("M"), is(9));
        assertThat(statusImage.measureText("X"), is(8));
        assertThat(statusImage.measureText("T"), is(7));
        assertThat(statusImage.measureText("|"), is(5));
        assertThat(statusImage.measureText(":"), is(5));
        assertThat(statusImage.measureText(";"), is(5));
        assertThat(statusImage.measureText("t"), is(4));
        assertThat(statusImage.measureText(","), is(4));
    }
}
