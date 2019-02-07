/**
 * @author Thomas Döring (thomas-dee)
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package org.jenkinsci.plugins.badge.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Run;
import hudson.model.Job;

/**
 * Extension point that allows custom selection of Runs
 */
@SuppressWarnings("rawtypes")
public interface RunSelectorExtensionPoint extends ExtensionPoint {
    public Run select(Job job, String selector, Run reference);
}