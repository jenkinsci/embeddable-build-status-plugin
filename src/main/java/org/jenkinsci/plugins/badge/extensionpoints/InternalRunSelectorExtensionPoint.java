/**
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge.extensionpoints;

import hudson.ExtensionPoint;
import hudson.model.Job;
import hudson.model.Run;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.*;

@SuppressWarnings("rawtypes")
@Restricted(NoExternalUse.class)
public interface InternalRunSelectorExtensionPoint extends ExtensionPoint {
    public Run select(Job job, String selector, Run reference);
}
