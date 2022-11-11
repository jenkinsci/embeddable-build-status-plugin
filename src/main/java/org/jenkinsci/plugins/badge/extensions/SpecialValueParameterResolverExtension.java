/**
 * @author Thomas Doering (thomas-dee)
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

 package org.jenkinsci.plugins.badge.extensions;

import hudson.Extension;
import hudson.model.Actionable;
import hudson.model.Run;
import hudson.model.Job;

import java.util.Objects;

import org.jenkinsci.plugins.badge.extensionpoints.ParameterResolverExtensionPoint;

@Extension
public class SpecialValueParameterResolverExtension implements ParameterResolverExtensionPoint {    
    
    public String resolve(Actionable actionable, String parameter) {
        if (parameter != null) {
            if (actionable instanceof Run<?, ?>) {
                Run<?, ?> run = (Run<?, ?>)actionable;
                /* try to match any custom value:
                    ${buildId}
                    ${buildNumber}
                    ${duration}
                    ${description}
                    ${displayName}
                    ${startTime}
                */
                
                parameter = parameter.replace("buildId", run.getId())
                         .replace("buildNumber", Integer.toString(run.getNumber()))
                         .replace("duration", run.getDurationString())
                         .replace("description", Objects.toString(run.getDescription(), ""))
                         .replace("displayName", run.getDisplayName())
                         .replace("startTime", run.getTimestampString());
                         
            } else if (actionable instanceof Job<?, ?>) {
                parameter = resolve(((Job<?, ?>)actionable).getLastBuild(), parameter);
            }
        }
        return parameter;
    }
}
