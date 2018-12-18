package org.jenkinsci.plugins.badge.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Job;

/**
 * Extension point that allows custom selection of Jobs
 * @author Thomas D.
 */
@SuppressWarnings("rawtypes")
public interface JobSelectorExtensionPoint extends ExtensionPoint {
    public abstract Job select(String selector);
}