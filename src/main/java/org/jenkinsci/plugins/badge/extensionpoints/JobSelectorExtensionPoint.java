/**
 * @author Thomas Doering (thomas-dee)
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package org.jenkinsci.plugins.badge.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Job;

/**
 * Extension point that allows custom selection of Jobs
 */
@SuppressWarnings("rawtypes")
public interface JobSelectorExtensionPoint extends ExtensionPoint {
    public abstract Job select(String selector);
}