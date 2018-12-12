package org.jenkinsci.plugins.badge.extensions;

import hudson.Extension;
import hudson.model.Actionable;
import hudson.model.Run;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jenkinsci.plugins.badge.extensionpoints.ParameterResolverExtensionPoint;

/**
 * @author Thomas D.
 */
@Extension
public class SpecialValueParameterResolverExtension implements ParameterResolverExtensionPoint {
    private static Pattern custom = Pattern.compile("\\$\\{(buildId|buildNumber|duration|runningTime|displayName)\\}");

    public String resolve(Actionable actionable, String parameter) {
        if (parameter != null && (actionable instanceof Run<?, ?>)) {
            Run<?, ?> run = (Run<?, ?>)actionable;
            /* try to match any custom value:
                 ${buildId}
                 ${buildNumber}
                 ${duration}
                 ${displayName}
                 ${runningTime}
            */
            Matcher matcher = custom.matcher(parameter);
            while (matcher.find()) {
                String customKey = matcher.group(1);
                if (customKey.equals("buildId")) {
                    parameter = matcher.replaceFirst(run.getId());
                } else if (customKey.equals("buildNumber")) {
                    parameter = matcher.replaceFirst(Integer.toString(run.getNumber()));
                } else if (customKey.equals("duration")) {
                    parameter = matcher.replaceFirst(run.getDurationString());
                } else if (customKey.equals("displayName")) {
                    parameter = matcher.replaceFirst(run.getDisplayName());
                } else if (customKey.equals("runningTime")) {
                    parameter = matcher.replaceFirst(run.getTimestampString());
                } else {
                    // this actually should NOT happen
                    parameter = matcher.replaceFirst(customKey);
                }
                matcher = custom.matcher(parameter);
            }
        }
        return parameter;
    }
}