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

import hudson.Extension;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Run;

public class IconRequestHandler {

    private final ImageResolver iconResolver;
    private final RunParameterResolver runParameterResolver;

    public IconRequestHandler() {
        this.iconResolver = new ImageResolver();
        this.runParameterResolver = new RunParameterResolver();
    }

    public StatusImage handleIconRequestForJob(Job job, String style, 
                    String subject, String status, 
                    String color, String animatedOverlayColor, 
                    String config) {

        EmbeddableBadgeConfig badgeConfig = runParameterResolver.resolveConfig(job, config);
        subject = runParameterResolver.resolveParameter(job, subject);
        status = runParameterResolver.resolveParameter(job, status);
        color = runParameterResolver.resolveParameter(job, color);
        animatedOverlayColor = runParameterResolver.resolveParameter(job, animatedOverlayColor);
        if (badgeConfig != null) {
            if (subject == null) subject = badgeConfig.getSubject();
            if (status == null) status = badgeConfig.getStatus();
            if (color == null) color = badgeConfig.getColor();
            if (animatedOverlayColor == null) animatedOverlayColor = badgeConfig.getAnimatedOverlayColor();
        }
        return iconResolver.getImage(job.getIconColor(), style, subject, status, color, animatedOverlayColor);
    }
    
    public StatusImage handleIconRequestForRun(Run run, String style, 
                    String subject, String status, 
                    String color, String animatedOverlayColor,
                    String config) {

        EmbeddableBadgeConfig badgeConfig = runParameterResolver.resolveConfig(run, config);
        subject = runParameterResolver.resolveParameter(run, subject);
        status = runParameterResolver.resolveParameter(run, status);
        color = runParameterResolver.resolveParameter(run, color);
        animatedOverlayColor = runParameterResolver.resolveParameter(run, animatedOverlayColor);
        if (badgeConfig != null) {
            if (subject == null) subject = badgeConfig.getSubject();
            if (status == null) status = badgeConfig.getStatus();
            if (color == null) color = badgeConfig.getColor();
            if (animatedOverlayColor == null) animatedOverlayColor = badgeConfig.getAnimatedOverlayColor();
        }
        return iconResolver.getImage(run.getIconColor(), style, subject, status, color, animatedOverlayColor);
    }

}
