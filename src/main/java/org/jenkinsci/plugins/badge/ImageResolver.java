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
import java.util.HashMap;
import java.util.Map;

public class ImageResolver {

    private final Map<String, String> statuses = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put( "red", "failing" );
            put( "brightgreen", "passing" );
            put( "yellow", "unstable" );
            put( "aborted", "aborted" );
        };
    };

    public StatusImage getImage(BallColor color)  {
        return getImage(color, "default", null, null, null);
    }

    public StatusImage getImage(BallColor color, String style)  {
        return getImage(color, style, null, null, null);
    }

    public StatusImage getImage(BallColor color, String style, String subject, String status, String colorName) {
        String statusColorName = color.noAnime().toString();
        if (color.isAnimated()) {
            statusColorName = "blue"; // "running"
        } else if (color == BallColor.BLUE) {
            statusColorName = "brightgreen";
        }

        if (colorName == null) {
            if (statusColorName.equals("aborted")) {
                colorName = "lightgrey";
            } else {
                colorName = statusColorName;
            }
        }

        if (subject == null) {
            subject = "build";
        }

        if (status == null) {
            status = statuses.get(statusColorName);
            if (status == null) {
                status = "unknown";
            }
        }
        
        try {
            return new StatusImage(subject, status, colorName, style);
        } catch (IOException ioe) {
            return new StatusImage();
        }
    }
}
