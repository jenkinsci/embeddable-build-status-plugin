/**
 * @author Dominik Bartholdi (imod)
 * @author Thomas Doering (thomas-dee)
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package org.jenkinsci.plugins.badge.actions;

import hudson.Extension;
import hudson.ExtensionList;
import hudson.model.Item;
import hudson.model.Job;
import hudson.model.Run;
import hudson.security.ACL;
import hudson.security.Permission;
import hudson.security.PermissionScope;
import hudson.util.HttpResponses;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.acegisecurity.context.SecurityContext;
import org.acegisecurity.context.SecurityContextHolder;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.WebMethod;
import hudson.model.UnprotectedRootAction;

import org.jenkinsci.plugins.badge.*;
import org.jenkinsci.plugins.badge.extensionpoints.JobSelectorExtensionPoint;
import org.jenkinsci.plugins.badge.extensionpoints.InternalRunSelectorExtensionPoint;
import org.jenkinsci.plugins.badge.extensionpoints.RunSelectorExtensionPoint;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Exposes the build status badge via unprotected URL.
 *
 * Even though the URL is unprotected, the user does still need the 'ViewStatus' permission on the given Job. If you want the status icons to be public readable/accessible, just grant the 'ViewStatus'
 * permission globally to 'anonymous'.
 */
@SuppressWarnings("rawtypes")
@Extension
public class PublicBuildStatusAction implements UnprotectedRootAction {
    public final static Permission VIEW_STATUS = new Permission(Item.PERMISSIONS, "ViewStatus", Messages._ViewStatus_Permission(), Item.READ, PermissionScope.ITEM);
    private static final Jenkins jInstance = Jenkins.getInstance();
    private IconRequestHandler iconRequestHandler;
    public PublicBuildStatusAction() throws IOException {
        iconRequestHandler = new IconRequestHandler();
    }

    @Override
    public String getUrlName() {
        return "buildStatus";
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @WebMethod(name = "icon")
    public HttpResponse doIcon(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job, 
                                @QueryParameter String build, @QueryParameter String style, 
                                @QueryParameter String subject, @QueryParameter String status, 
                                @QueryParameter String color, @QueryParameter String animatedOverlayColor, 
                                @QueryParameter String config, @QueryParameter String link) {
        if (job == null) {
            return PluginImpl.iconRequestHandler.handleIconRequest(style, subject, status, color, animatedOverlayColor, link);
        } else {
            Job<?, ?> project = getProject(job, false);
            if(build != null && project != null) {
                Run<?, ?> run = getRun(project, build, false);
                return iconRequestHandler.handleIconRequestForRun(run, style, subject, status, color, animatedOverlayColor, config, link);
            } else {
                return iconRequestHandler.handleIconRequestForJob(project, style, subject, status, color, animatedOverlayColor, config, link);
            }
        }
    }

    @WebMethod(name = "icon.svg")
    public HttpResponse doIconDotSvg(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job, 
                                @QueryParameter String build, @QueryParameter String style, 
                                @QueryParameter String subject, @QueryParameter String status, 
                                @QueryParameter String color, @QueryParameter String animatedOverlayColor, 
                                @QueryParameter String config, @QueryParameter String link) {
        return doIcon(req, rsp, job, build, style, subject, status, color, animatedOverlayColor, config, link);
    }

    public String doText(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job, @QueryParameter String build) {
        if (job == null) {
            return "Missing query parameter: job";
        }

        Job<?, ?> project = getProject(job, true);
        if(build != null) {
            Run<?, ?> run = getRun(project, build, true);
            return run.getIconColor().getDescription();
        } else {
            return project.getIconColor().getDescription();
        }
    }

    private static Job<?, ?> getProject(String job, Boolean throwErrorWhenNotFound) {
        Job<?, ?> p = null;
        if (job != null) {
            // as the user might have ViewStatus permission only (e.g. as anonymous) we get get the project impersonate and check for permission after getting the project
            SecurityContext orig = ACL.impersonate(ACL.SYSTEM);

            try {
                // first try to get Job via JobSelectorExtensionPoints
                for (JobSelectorExtensionPoint jobSelector : ExtensionList.lookup(JobSelectorExtensionPoint.class)) {
                    p = jobSelector.select(job);
                    if (p != null) {
                        break;
                    }
                }

                if (p == null && jInstance != null) {
                    p = jInstance.getItemByFullName(job, Job.class);
                }
           } finally {
                SecurityContextHolder.setContext(orig);
            }
        }

        // check if user has permission to view the status
        if(throwErrorWhenNotFound && (p == null || !p.hasPermission(VIEW_STATUS))){
            throw HttpResponses.notFound();            
        }
        
        return p;
    }

    @SuppressFBWarnings(value = "NP_LOAD_OF_KNOWN_NULL_VALUE", justification = "'run' is only null for the first enclosing for(token) run")
    public static Run<?, ?> getRun(Job<?, ?> project, String build, Boolean throwErrorWhenNotFound) {
        Run<?, ?> run = null;

        if (project != null && build != null) {
            // as the user might have ViewStatus permission only (e.g. as anonymous) we get get the project impersonate and check for permission after getting the project
            SecurityContext orig = ACL.impersonate(ACL.SYSTEM);
            try {
                for (String token : build.split(",")) {
                    Run newRun = null;
                    // first: try to get Run via our InternalRunSelectorExtensionPoints
                    for (InternalRunSelectorExtensionPoint runSelector : ExtensionList.lookup(InternalRunSelectorExtensionPoint.class)) {
                        newRun = runSelector.select(project, token, run);
                        if (newRun != null) {
                            break;
                        }
                    }

                    if (newRun == null) {
                        // second: try to get Run via RunSelectorExtensionPoints
                        for (RunSelectorExtensionPoint runSelector : ExtensionList.lookup(RunSelectorExtensionPoint.class)) {
                            newRun = runSelector.select(project, token, run);
                            if (newRun != null) {
                                break;
                            }
                        }
                    }

                    if (newRun != null) {
                        run = newRun;
                    } else {
                        break;
                    }
                }
            } finally {
                SecurityContextHolder.setContext(orig);
            }    
        }
        // check if user has permission to view the status
        if(throwErrorWhenNotFound && (run == null || !run.hasPermission(VIEW_STATUS))){
            throw HttpResponses.notFound();
        }

        return run;
    }
}
