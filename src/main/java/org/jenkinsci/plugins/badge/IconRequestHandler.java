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
import hudson.model.Job;
import hudson.model.Run;

import org.jenkinsci.plugins.badge.actions.EmbeddableBadgeConfigsAction;

/**
 * @author Thomas D.
 */
@SuppressWarnings("rawtypes")
public class IconRequestHandler {

    private final ImageResolver iconResolver;
    private final ParameterResolver parameterResolver;

    public IconRequestHandler() {
        this.iconResolver = new ImageResolver();
        this.parameterResolver = new ParameterResolver();
    }

    public StatusImage handleIconRequest(String style, 
                    String subject, String status, 
                    String color, String animatedOverlayColor) {
        return iconResolver.getImage(BallColor.BLUE, style, subject, status, color, animatedOverlayColor);
    }
    
    public StatusImage handleIconRequestForJob(Job job, String style, 
                    String subject, String status, 
                    String color, String animatedOverlayColor, 
                    String config) {

        if (job != null) {
            subject = parameterResolver.resolve(job, subject);
            status = parameterResolver.resolve(job, status);
            color = parameterResolver.resolve(job, color);
            animatedOverlayColor = parameterResolver.resolve(job, animatedOverlayColor);

            if (config != null && (subject == null || status == null || color == null || animatedOverlayColor == null)) {
                EmbeddableBadgeConfig badgeConfig = EmbeddableBadgeConfigsAction.resolve(job, config);
                if (badgeConfig != null) {
                    if (subject == null) subject = badgeConfig.getSubject();
                    if (status == null) status = badgeConfig.getStatus();
                    if (color == null) color = badgeConfig.getColor();
                    if (animatedOverlayColor == null) animatedOverlayColor = badgeConfig.getAnimatedOverlayColor();
                } else {
                    // fallback to unknown badge
                    if (status == null) status = "not run";
                    if (color == null) color = "lightgrey";
                }
            }
            return iconResolver.getImage(job.getIconColor(), style, subject, status, color, animatedOverlayColor);
        } else {
            return iconResolver.getImage(BallColor.NOTBUILT, style, subject, null, null, null);
        }
    }
    
    public StatusImage handleIconRequestForRun(Run run, String style, 
                    String subject, String status, 
                    String color, String animatedOverlayColor,
                    String config) {

        if (run != null) {
            subject = parameterResolver.resolve(run, subject);
            status = parameterResolver.resolve(run, status);
            color = parameterResolver.resolve(run, color);
            animatedOverlayColor = parameterResolver.resolve(run, animatedOverlayColor);
            if (config != null && (subject == null || status == null || color == null || animatedOverlayColor == null)) {
                EmbeddableBadgeConfig badgeConfig = EmbeddableBadgeConfigsAction.resolve(run, config);
                if (badgeConfig != null) {
                    if (subject == null) subject = badgeConfig.getSubject();
                    if (status == null) status = badgeConfig.getStatus();
                    if (color == null) color = badgeConfig.getColor();
                    if (animatedOverlayColor == null) animatedOverlayColor = badgeConfig.getAnimatedOverlayColor();
                } else {
                    // fallback to disabled badge
                    if (status == null) status = "not run";
                    if (color == null) color = "lightgrey";
                }
            }    
            return iconResolver.getImage(run.getIconColor(), style, subject, status, color, animatedOverlayColor);
        } else {
            return iconResolver.getImage(BallColor.NOTBUILT, style, subject, null, null, null);
        }
    }

}
