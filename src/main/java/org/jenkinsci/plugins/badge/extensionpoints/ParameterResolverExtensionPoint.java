package org.jenkinsci.plugins.badge.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Actionable;


/**
 * Extension point that allows custom selection of Runs
 * @author Thomas D.
 */
public interface ParameterResolverExtensionPoint extends ExtensionPoint {
    public abstract String resolve(Actionable actionable, String selector);
}