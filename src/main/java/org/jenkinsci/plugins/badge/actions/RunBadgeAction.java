/**
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge.actions;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.jenkins.ui.icon.IconSpec;
import org.jenkinsci.plugins.badge.*;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.WebMethod;

public class RunBadgeAction implements Action, IconSpec {
    public final Run<?, ?> run;
    public final Job<?, ?> project;

    public RunBadgeAction(Run<?, ?> run) {
        this.run = run;
        this.project = run.getParent();
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
        return Messages.RunBadgeAction_DisplayName();
    }

    @Override
    public String getUrlName() {
        return "badge";
    }

    public String getUrl() {
        /* TODO: Is a permission check needed here? */
        /* Needed for the jelly syntax hints page */
        String url = Stapler.getCurrentRequest().getReferer();
        return url == null ? "null-referer" : url;
    }

    public String getUrlEncodedFullName() {
        /* TODO: Is a permission check needed here? */
        /* Needed for the jelly syntax hints page */
        if (project == null) {
            return "null-project-no-url-encoded-fullName";
        }
        if (project.getFullName() == null) {
            return "null-project-fullName-no-url-encoded-fullName";
        }
        String fullName = URLEncoder.encode(project.getFullName(), StandardCharsets.UTF_8);
        return fullName == null ? "null-url-encoded-fullName" : fullName;
    }

    @WebMethod(name = "icon")
    public HttpResponse doIcon(
            @QueryParameter String style,
            @QueryParameter String subject,
            @QueryParameter String status,
            @QueryParameter String color,
            @QueryParameter String animatedOverlayColor,
            @QueryParameter String config,
            @QueryParameter String link) {
        return PluginImpl.iconRequestHandler.handleIconRequestForRun(
                run, style, subject, status, color, animatedOverlayColor, config, link);
    }

    @WebMethod(name = "icon.svg")
    public HttpResponse doIconDotSvg(
            @QueryParameter String style,
            @QueryParameter String subject,
            @QueryParameter String status,
            @QueryParameter String color,
            @QueryParameter String animatedOverlayColor,
            @QueryParameter String config,
            @QueryParameter String link) {
        return doIcon(style, subject, status, color, animatedOverlayColor, config, link);
    }

    public String doText() {
        return run.getIconColor().getDescription();
    }
}
