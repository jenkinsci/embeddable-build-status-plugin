/**
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge.extensions;

import hudson.Extension;
import hudson.model.Actionable;
import hudson.model.Job;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Run;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jenkinsci.plugins.badge.extensionpoints.ParameterResolverExtensionPoint;

@Extension
public class BuildParameterResolverExtension implements ParameterResolverExtensionPoint {
    private static Pattern pattern = Pattern.compile("params\\.([^\\{\\}\\s]+)");
    private static Pattern defaultPattern = Pattern.compile("params\\.([^\\{\\}\\s\\|]+)\\|([^\\}\\|]+)");

    @Override
    public String resolve(Actionable actionable, String parameter) {
        if (actionable instanceof Run<?, ?> run) {

            ParametersAction params = run.getAction(ParametersAction.class);
            if (params != null) {
                // try to match any ${params.<ParamName>|<DefaultValue>}
                Matcher matcher = defaultPattern.matcher(parameter);
                while (matcher.find()) {
                    // get value for <ParamName>
                    ParameterValue value = params.getParameter(matcher.group(1));
                    Object val = value != null ? value.getValue() : null;
                    if (val != null) {
                        // replace ${params.<ParamName>|<DefaultValue>} with the value
                        String valueStr = val.toString();
                        parameter = matcher.replaceAll(valueStr.replace("\\", "\\\\")
                                .replace("$", "\\$")
                                .replace("{", "\\{")
                                .replace("}", "\\}"));
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
                    Object val = value != null ? value.getValue() : null;
                    if (val != null) {
                        // replace ${params.<ParamName>} with the value
                        String valueStr = val.toString();
                        parameter = matcher.replaceFirst(valueStr.replace("\\", "\\\\")
                                .replace("$", "\\$")
                                .replace("{", "\\{")
                                .replace("}", "\\}"));
                        matcher = pattern.matcher(parameter);
                    } else {
                        // replace ${params.<ParamName>} with empty string
                        parameter = matcher.replaceFirst("");
                        matcher = pattern.matcher(parameter);
                    }
                }
            }
        } else if (actionable instanceof Job<?, ?> job) {
            parameter = resolve(job.getLastBuild(), parameter);
        }
        return parameter;
    }
}
