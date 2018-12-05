package org.jenkinsci.plugins.badge;

import hudson.model.Action;
import hudson.model.Job;
import hudson.model.Run;
import hudson.model.ParametersAction;
import hudson.model.ParameterValue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;

import org.jenkinsci.plugins.badge.EmbeddableBadgeConfig;

public class RunParameterResolver {
    private static Pattern pattern = Pattern.compile("\\$\\{params\\.([^\\{\\}\\s]+)\\}");
    private static Pattern custom = Pattern.compile("\\$\\{(buildId|buildNumber|duration|runningTime|displayName)\\}");
    private static Pattern defaultPattern = Pattern.compile("\\$\\{params\\.([^\\{\\}\\s\\|]+)\\|([^\\}\\|]+)\\}");

    public EmbeddableBadgeConfig resolveConfig(Run run, String id) {
        if (id != null) {
            EmbeddableBadgeConfigsAction badgeConfigs = run.getAction(EmbeddableBadgeConfigsAction.class);
            if (badgeConfigs != null) {
                return badgeConfigs.getConfig(id);
            }
        }
        return null;
    }

    public EmbeddableBadgeConfig resolveConfig(Job job, String id) {
        return resolveConfig(job.getLastBuild(), id);
    }

    public String resolveParameter(Run run, String parameter) {
        if (parameter != null) {
            ParametersAction params = run.getAction(ParametersAction.class);
            if (params != null) {
                // try to match any ${params.<ParamName>|<DefaultValue>}
                Matcher matcher = defaultPattern.matcher(parameter);
                while (matcher.find()) {
                    // get value for <ParamName>
                    ParameterValue value = params.getParameter(matcher.group(1));
                    if (value != null) {
                        // replace ${params.<ParamName>|<DefaultValue>} with the value
                        String valueStr = value.getValue().toString();
                        parameter = matcher.replaceAll(valueStr);
                        matcher = defaultPattern.matcher(parameter);
                    } else {
                        // replace ${params.<ParamName>|<DefaultValue>} with the <DefaultValue>
                        parameter = matcher.replaceAll("$2");
                        matcher = defaultPattern.matcher(parameter);
                    }
                }

                // try to match any ${params.<ParamName>}
                matcher = pattern.matcher(parameter);
                while (matcher.find()) {
                    // get value for <ParamName>
                    ParameterValue value = params.getParameter(matcher.group(1));
                    if (value != null) {
                        // replace ${params.<ParamName>} with the value
                        String valueStr = value.getValue().toString();
                        parameter = matcher.replaceFirst(valueStr);
                        matcher = pattern.matcher(parameter);
                    } else {
                        // replace ${params.<ParamName>} with empty string
                        parameter = matcher.replaceFirst("");
                        matcher = pattern.matcher(parameter);
                    }
                }
            }

            
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

    public String resolveParameter(Job project, String parameter) {
        return resolveParameter(project.getLastBuild(), parameter);
    }
}
