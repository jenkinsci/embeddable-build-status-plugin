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
import hudson.model.UnprotectedRootAction;
import org.jenkinsci.plugins.badge.*;
import org.kohsuke.stapler.WebMethod;

import java.io.IOException;

import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

/**
 * Exposes any custom badge via unprotected URL.
  */
@Extension
public class PublicBadgeAction implements UnprotectedRootAction {

    public PublicBadgeAction() throws IOException {
    }

    public String getUrlName() {
        return "badge";
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    /**
     * Serves the badge image.
     */
    public HttpResponse doIndex(StaplerRequest req, StaplerResponse rsp,
                                @QueryParameter String style, 
                                @QueryParameter String subject, @QueryParameter String status, 
                                @QueryParameter String color, @QueryParameter String animatedOverlayColor, 
                                @QueryParameter String config) {
        return PluginImpl.iconRequestHandler.handleIconRequest(style, subject, status, color, animatedOverlayColor, config);
    }

    @WebMethod(name = "icon")
    public HttpResponse doIcon(StaplerRequest req, StaplerResponse rsp,
                                @QueryParameter String style, 
                                @QueryParameter String subject, @QueryParameter String status, 
                                @QueryParameter String color, @QueryParameter String animatedOverlayColor, 
                                @QueryParameter String config) {
        return doIndex(req, rsp, style, subject, status, color, animatedOverlayColor, config);
    }

    @WebMethod(name = "icon.svg")
    public HttpResponse doIconDotSvg(StaplerRequest req, StaplerResponse rsp,
                                @QueryParameter String style, 
                                @QueryParameter String subject, @QueryParameter String status, 
                                @QueryParameter String color, @QueryParameter String animatedOverlayColor, 
                                @QueryParameter String config) {
        return doIndex(req, rsp, style, subject, status, color, animatedOverlayColor, config);
    }
}
