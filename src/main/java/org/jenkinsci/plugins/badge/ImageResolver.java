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

public class ImageResolver {
    
    private final StatusImage[] images;
    
    public ImageResolver() throws IOException{
        images = new StatusImage[] {
                new StatusImage("failure.svg"),
                new StatusImage("unstable.svg"),
                new StatusImage("success.svg"),
                new StatusImage("running.svg"),
                new StatusImage("unknown.svg")
        };
    }
    
    public StatusImage getImage(BallColor color) {
        if (color.isAnimated())
            return images[3];

        switch (color) {
        case RED:
        case ABORTED:
            return images[0];
        case YELLOW:
            return images[1];
        case BLUE:
            return images[2];
        default:
            return images[4];
        }
    }
}