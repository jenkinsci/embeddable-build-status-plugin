/**
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge.extensions;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Result;
import hudson.model.Run;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jenkinsci.plugins.badge.extensionpoints.InternalRunSelectorExtensionPoint;

@SuppressWarnings("rawtypes")
@Extension
public class BuildParameterRunSelectorExtension implements InternalRunSelectorExtensionPoint {
    private static Pattern outerSelector = Pattern.compile(
            "(last|first)(Failed|Successful|Unsuccessful|Stable|Unstable|Completed){0,1}(:\\$\\{([^\\{\\}\\s]+)\\}){0,1}");
    private static Pattern paramsPattern = Pattern.compile("params\\.([^=]+)=(.*)");

    private Boolean matchRule(Job job, Run run, String rule) {
        if (rule == null) {
            return true;
        }

        // 1. try to match ${params.xxx=yyy}
        Matcher matcher = paramsPattern.matcher(rule);
        if (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = matcher.group(2);

            ParametersAction params = run.getAction(ParametersAction.class);
            if (params != null) {
                ParameterValue value = params.getParameter(paramName);
                if (value != null
                        && value.getValue() != null
                        && value.getValue().toString().equals(paramValue)) {
                    return true;
                }
            }
        }

        return false;
    }

    private Run findSpecific(Job job, Run run, String what, String specific) {
        if (run == null) {
            if ("first".equals(what)) {
                run = findSpecific(job, job.getFirstBuild(), what, specific);
            } else {
                if (specific != null) {
                    switch (specific) {
                        case "Failed":
                            run = job.getLastFailedBuild();
                            break;
                        case "Successful":
                            run = job.getLastSuccessfulBuild();
                            break;
                        case "Unsuccessful":
                            run = job.getLastUnsuccessfulBuild();
                            break;
                        case "Stable":
                            run = job.getLastStableBuild();
                            break;
                        case "Unstable":
                            run = job.getLastUnstableBuild();
                            break;
                        case "Completed":
                            run = job.getLastCompletedBuild();
                            break;
                        default:
                            break;
                    }
                }

                if (run == null) {
                    run = job.getLastBuild();
                }
            }
        } else {
            do {
                if ("first".equals(what)) {
                    run = run.getNextBuild();
                } else {
                    run = run.getPreviousBuild();
                }

                if (run != null) {
                    boolean doBreak = specific == null;
                    if (!doBreak) {
                        Result result = run.getResult();
                        if (result != null) {
                            boolean isCompleted = result.isCompleteBuild();
                            boolean isSuccessful = result == Result.SUCCESS;
                            boolean isFailed = result == Result.FAILURE;
                            boolean isUnstable = result == Result.UNSTABLE;
                            boolean isUnsuccessful = !isSuccessful;
                            boolean isStable = isSuccessful;

                            doBreak = ("Completed".equals(specific) && isCompleted)
                                    || ("Successful".equals(specific) && isCompleted && isSuccessful)
                                    || ("Failed".equals(specific) && isCompleted && isFailed)
                                    || ("Unstable".equals(specific) && isCompleted && isUnstable)
                                    || ("Unsuccessful".equals(specific) && isCompleted && isUnsuccessful)
                                    || ("Stable".equals(specific) && isCompleted && isStable);
                        }
                    }

                    if (doBreak) {
                        break;
                    }
                }
            } while (run != null);
        }
        return run;
    }

    @Override
    public Run select(Job job, String runId, Run run) {
        Matcher matcher = outerSelector.matcher(runId);
        while (matcher.find()) {
            String what = matcher.group(1);
            String specific = matcher.group(2);
            String rule = matcher.group(4);

            if (run == null) {
                run = findSpecific(job, null, what, specific);
            }

            // iterate over all Runs forward
            while (run != null) {
                if (matchRule(job, run, rule)) {
                    break;
                }

                run = findSpecific(job, run, what, specific);
            }
            runId = matcher.replaceAll("");
            matcher = outerSelector.matcher(runId);
        }

        return run;
    }
}
