package org.jenkinsci.plugins.badge.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Job;

/**
 * Extension point that allows custom selection of Jobs
 */
public interface JobSelectorExtensionPoint extends ExtensionPoint {
    public abstract Job select(String selector);
}