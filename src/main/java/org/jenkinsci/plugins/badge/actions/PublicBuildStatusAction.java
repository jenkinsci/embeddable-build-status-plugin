/*
 * The MIT License
 *
 * Copyright 2013 Dominik Bartholdi.
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

import org.jenkinsci.plugins.badge.*;
import org.jenkinsci.plugins.badge.actions.PublicBadgeAction;
import org.jenkinsci.plugins.badge.extensionpoints.JobSelectorExtensionPoint;
import org.jenkinsci.plugins.badge.extensionpoints.InternalRunSelectorExtensionPoint;
import org.jenkinsci.plugins.badge.extensionpoints.RunSelectorExtensionPoint;

/**
 * Exposes the build status badge via unprotected URL.
 * 
 * The status of a job can be checked like this:
 * 
 * <li>http://localhost:8080/buildstatus/icon[.svg]?job=[JOBNAME] <li>e.g. http://localhost:8080/buildstatus/icon.svg?job=free1 <br/>
 * <br/>
 *
 * The status of a particular build can be checked like this:
 *
 * <li>http://localhost:8080/buildstatus/icon[.svg]?job=[JOBNAME]&build=[BUILDNUMBER] <li>e.g. http://localhost:8080/buildstatus/icon.svg?job=free1&build=5<br/>
 * <br/>
 *
 * Even though the URL is unprotected, the user does still need the 'ViewStatus' permission on the given Job. If you want the status icons to be public readable/accessible, just grant the 'ViewStatus'
 * permission globally to 'anonymous'.
 * 
 * @author Dominik Bartholdi (imod)
 */
@SuppressWarnings("rawtypes")
@Extension
public class PublicBuildStatusAction extends PublicBadgeAction {

    public final static Permission VIEW_STATUS = new Permission(Item.PERMISSIONS, "ViewStatus", Messages._ViewStatus_Permission(), Item.READ, PermissionScope.ITEM);
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

    /**
     * Serves the badge image.
     */
    @WebMethod(name = "icon")
    public HttpResponse doIcon(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job, 
                                @QueryParameter String build, @QueryParameter String style, 
                                @QueryParameter String subject, @QueryParameter String status, 
                                @QueryParameter String color, @QueryParameter String animatedOverlayColor, 
                                @QueryParameter String config) {
        if (job == null) {
            return HttpResponses.errorWithoutStack(400, "Missing query parameter: job");
        }

        Job<?, ?> project = getProject(job);
        if(build != null) {
            Run<?, ?> run = getRun(project, build);
            return iconRequestHandler.handleIconRequestForRun(run, style, subject, status, color, animatedOverlayColor, config);
        } else {
            return iconRequestHandler.handleIconRequestForJob(project, style, subject, status, color, animatedOverlayColor, config);
        }
    }

    @WebMethod(name = "icon.svg")
    public HttpResponse doIconDotSvg(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job, 
                                @QueryParameter String build, @QueryParameter String style, 
                                @QueryParameter String subject, @QueryParameter String status, 
                                @QueryParameter String color, @QueryParameter String animatedOverlayColor, 
                                @QueryParameter String config) {
        return doIcon(req, rsp, job, build, style, subject, status, color, animatedOverlayColor, config);
    }

    /**
     * Serves text.
     */
    public String doText(StaplerRequest req, StaplerResponse rsp, @QueryParameter String job, @QueryParameter String build) {
        if (job == null) {
            return "Missing query parameter: job";
        }

        Job<?, ?> project = getProject(job);
        if(build != null) {
            Run<?, ?> run = getRun(project, build);
            return run.getIconColor().getDescription();
        } else {
            return project.getIconColor().getDescription();
        }
    }

    public static Job<?, ?> getProject(String job) {
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

                if (p == null) {
                    p = Jenkins.getInstance().getItemByFullName(job, Job.class);
                }
           } finally {
                SecurityContextHolder.setContext(orig);
            }
        }

        // check if user has permission to view the status
        if(p == null || !(p.hasPermission(VIEW_STATUS))){
            throw HttpResponses.notFound();            
        }
        
        return p;
    }

    public static Run<?, ?> getRun(Job<?, ?> project, String build) {
        Run<?, ?> run = null;

        if (project != null && build != null) {
            // as the user might have ViewStatus permission only (e.g. as anonymous) we get get the project impersonate and check for permission after getting the project
            SecurityContext orig = ACL.impersonate(ACL.SYSTEM);
            try {
                for (String token : build.split(",")) {
                    // first: try to get Run via our InternalRunSelectorExtensionPoints
                    for (InternalRunSelectorExtensionPoint runSelector : ExtensionList.lookup(InternalRunSelectorExtensionPoint.class)) {
                        Run newRun = runSelector.select(project, token, run);
                        if (newRun != null) {
                            run = newRun;
                            break;
                        }
                    }

                    if (run == null) {
                        // second: try to get Run via RunSelectorExtensionPoints
                        for (RunSelectorExtensionPoint runSelector : ExtensionList.lookup(RunSelectorExtensionPoint.class)) {
                            Run newRun = runSelector.select(project, token, run);
                            if (newRun != null) {
                                run = newRun;
                                break;
                            }
                        }
                    }
                }
            } finally {
                SecurityContextHolder.setContext(orig);
            }    
        }
        // check if user has permission to view the status
        if(run == null || !(run.hasPermission(VIEW_STATUS))){
            throw HttpResponses.notFound();
        }

        return run;
    }
}
