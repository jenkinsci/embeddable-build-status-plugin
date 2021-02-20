/**
 * @author Thomas Doering (thomas-dee)
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package org.jenkinsci.plugins.badge.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Run;
import hudson.model.Job;

import org.kohsuke.accmod.restrictions.*;
import org.kohsuke.accmod.Restricted;

@SuppressWarnings("rawtypes")
@Restricted(NoExternalUse.class)
public interface InternalRunSelectorExtensionPoint extends ExtensionPoint {
    public Run select(Job job, String selector, Run reference);
}