/**
 * @author Thomas Döring (thomas-dee)
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package org.jenkinsci.plugins.badge;

import hudson.model.BallColor;
import hudson.model.Job;
import hudson.model.Run;

import org.jenkinsci.plugins.badge.actions.EmbeddableBadgeConfigsAction;

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
