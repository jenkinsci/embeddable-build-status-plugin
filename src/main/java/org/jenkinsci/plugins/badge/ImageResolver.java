/*
 * The MIT License
 *
 * Copyright 2013 Kohsuke Kawaguchi, Dominik Bartholdi
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

import hudson.model.BallColor;
import java.io.IOException;
import java.util.Map;

public class ImageResolver {
    private static final Map<String, String> STATUSES = Map.of(
            "red", "failing",
            "brightgreen", "passing",
            "yellow", "unstable",
            "aborted", "aborted",
            "blue", "running",
            "disabled", "disabled",
            "notbuilt", "not run");

    public StatusImage getImage(
            BallColor color,
            String style,
            String subject,
            String status,
            String colorName,
            String animatedOverlayColor,
            String link) {
        String statusColorName = color.noAnime().toString();
        String statusAnimatedOverlayColorName = null;

        // check if "ball" is requested
        if (style != null) {
            String[] styleParts = style.split("-");
            if (styleParts.length == 2 && styleParts[0].equals("ball")) {
                String url = color.getImageOf(styleParts[1]);
                if (url == null) {
                    url = color.getImageOf("32x32");
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

        if (color.isAnimated() && colorName == null) {
            // animated means "running"
            statusAnimatedOverlayColorName = "blue";
        }

        if (statusColorName.equals("blue")) {
            statusColorName = "brightgreen";
        }

        if (colorName == null) {
            if (statusColorName.equals("aborted")
                    || statusColorName.equals("disabled")
                    || statusColorName.equals("notbuilt")) {
                colorName = "lightgrey";
            } else {
                colorName = statusColorName;
            }

            if (animatedOverlayColor == null) {
                animatedOverlayColor = statusAnimatedOverlayColorName;
            }
        }

        if (subject == null) {
            subject = "build";
        }

        if (status == null) {
            status = STATUSES.get(
                    statusAnimatedOverlayColorName != null ? statusAnimatedOverlayColorName : statusColorName);
            if (status == null) {
                status = "unknown";
            }
        }

        try {
            return new StatusImage(subject, status, colorName, animatedOverlayColor, style, link);
        } catch (IOException ioe) {
            return new StatusImage();
        }
    }
}
