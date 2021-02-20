/**
 * @author Thomas Doering (thomas-dee)
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package org.jenkinsci.plugins.badge.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Actionable;


/**
 * Extension point that allows custom selection of Runs
 */
public interface ParameterResolverExtensionPoint extends ExtensionPoint {
    public abstract String resolve(Actionable actionable, String selector);
}