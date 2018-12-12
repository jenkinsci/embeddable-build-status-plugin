package org.jenkinsci.plugins.badge;

import hudson.model.Actionable;
import hudson.ExtensionList;

import org.jenkinsci.plugins.badge.extensionpoints.ParameterResolverExtensionPoint;

/**
 * @author Thomas D.
 */
public class ParameterResolver {
    public String resolve(Actionable actionable, String parameter) {
        if (parameter != null) {
            // first try to get Job via ParameterResolverExtensionPoint
            for (ParameterResolverExtensionPoint resolver : ExtensionList.lookup(ParameterResolverExtensionPoint.class)) {
                parameter = resolver.resolve(actionable, parameter);
            }
        }
        return parameter;
    }
}
