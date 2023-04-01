/**
 * @author Kohsuke Kawaguchi
 * @author Thomas Doering Licensed under the MIT License. See License.txt in the project root for
 *     license information.
 */
package org.jenkinsci.plugins.badge.actions;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import org.jenkins.ui.icon.IconSpec;
import org.jenkinsci.plugins.badge.*;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.WebMethod;

/**
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings("rawtypes")
public class JobBadgeAction implements Action, IconSpec {
    public final Job project;

    public JobBadgeAction(Job project) {
        this.project = project;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getIconClassName() {
        return "symbol-shield-outline plugin-ionicons-api";
    }

    @Override
    public String getDisplayName() {
        return Messages.JobBadgeAction_DisplayName();
    }

    @Override
    public String getUrlName() {
        return "badge";
    }

    @WebMethod(name = "icon")
    public HttpResponse doIcon(
            @QueryParameter String build,
            @QueryParameter String style,
            @QueryParameter String subject,
            @QueryParameter String status,
            @QueryParameter String color,
            @QueryParameter String config,
            @QueryParameter String animatedOverlayColor,
            @QueryParameter String link) {
        if (build != null) {
            Run<?, ?> run = PublicBuildStatusAction.getRun(this.project, build, false);
            return PluginImpl.iconRequestHandler.handleIconRequestForRun(
                    run, style, subject, status, color, animatedOverlayColor, config, link);
        }

        return PluginImpl.iconRequestHandler.handleIconRequestForJob(
                project, style, subject, status, color, animatedOverlayColor, config, link);
    }

    @WebMethod(name = "icon.svg")
    public HttpResponse doIconDotSvg(
            @QueryParameter String build,
            @QueryParameter String style,
            @QueryParameter String subject,
            @QueryParameter String status,
            @QueryParameter String color,
            @QueryParameter String animatedOverlayColor,
            @QueryParameter String config,
            @QueryParameter String link) {
        return doIcon(build, style, subject, status, color, animatedOverlayColor, config, link);
    }

    public String doText() {
        return project.getIconColor().getDescription();
    }
}
