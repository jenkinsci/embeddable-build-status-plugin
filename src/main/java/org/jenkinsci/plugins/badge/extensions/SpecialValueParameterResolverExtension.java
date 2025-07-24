/**
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge.extensions;

import hudson.Extension;
import hudson.model.Actionable;
import hudson.model.Job;
import hudson.model.Run;
import java.util.Objects;
import org.jenkinsci.plugins.badge.extensionpoints.ParameterResolverExtensionPoint;

@Extension
public class SpecialValueParameterResolverExtension implements ParameterResolverExtensionPoint {

    @Override
    public String resolve(Actionable actionable, String parameter) {
        if (parameter != null) {
            if (actionable instanceof Run<?, ?> run) {
                /* try to match any custom value:
                    ${buildId}
                    ${buildNumber}
                    ${duration}
                    ${description}
                    ${displayName}
                    ${startTime}
                */

                parameter = parameter
                        .replace("buildId", run.getId())
                        .replace("buildNumber", Integer.toString(run.getNumber()))
                        .replace("duration", run.getDurationString())
                        .replace("description", Objects.toString(run.getDescription(), ""))
                        .replace("displayName", run.getDisplayName())
                        .replace("startTime", run.getTimestampString());

            } else if (actionable instanceof Job<?, ?> job) {
                parameter = resolve(job.getLastBuild(), parameter);
            }
        }
        return parameter;
    }
}
