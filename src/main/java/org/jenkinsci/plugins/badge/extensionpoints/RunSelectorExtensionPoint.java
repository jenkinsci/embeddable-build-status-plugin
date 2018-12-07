package org.jenkinsci.plugins.badge.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Run;
import hudson.model.Job;

/**
 * Extension point that allows custom selection of Runs
 */
public interface RunSelectorExtensionPoint extends ExtensionPoint {
    public abstract Run select(Job job, String selector);
}