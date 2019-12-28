/**
 * @author Thomas Doering (thomas-dee)
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package org.jenkinsci.plugins.badge.extensions;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.Job;
import hudson.model.ParametersAction;
import hudson.model.Result;
import hudson.model.ParameterValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jenkinsci.plugins.badge.extensionpoints.InternalRunSelectorExtensionPoint;

@SuppressWarnings("rawtypes")
@Extension
public class BuildParameterRunSelectorExtension implements InternalRunSelectorExtensionPoint {
    private static Pattern outerSelector = Pattern.compile("(last|first)(Failed|Successful|Unsuccessful|Stable|Unstable|Completed){0,1}(:\\$\\{([^\\{\\}\\s]+)\\}){0,1}");
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
                if (value != null && value.getValue() != null && value.getValue().toString().equals(paramValue)) {
                    return true;
                }
            }
        }

        return false;
    }

    private Run findSpecific(Job job, Run run, String what, String specific) {
        if (run == null) {
            if (what.equals("first")) {
                run = findSpecific(job, job.getFirstBuild(), what, specific);
            } else {
                if (specific != null) {
                    if (specific.equals("Failed")) {
                        run = job.getLastFailedBuild();
                    } else if (specific.equals("Successful")) {
                        run = job.getLastSuccessfulBuild();
                    } else if (specific.equals("Unsuccessful")) {
                        run = job.getLastUnsuccessfulBuild();                           
                    } else if (specific.equals("Stable")) {
                        run = job.getLastStableBuild();                           
                    } else if (specific.equals("Unstable")) {
                        run = job.getLastUnstableBuild();                           
                    } else if (specific.equals("Completed")) {
                        run = job.getLastCompletedBuild();  
                    }
                }      

                if (run == null) {
                    run = job.getLastBuild();
                }
            }
        } else {
            do {
                if (what.equals("first")) {
                    run = run.getNextBuild();
                } else {
                    run = run.getPreviousBuild();
                }

                if (run != null) {
                    Boolean doBreak = (specific == null);
                    if (!doBreak) {
                        Result result = run.getResult();
                        if (result != null) {
                            Boolean isCompleted     = result.isCompleteBuild();
                            Boolean isSuccessful    = result == Result.SUCCESS;
                            Boolean isFailed        = result == Result.FAILURE;
                            Boolean isUnstable      = result == Result.UNSTABLE;
                            Boolean isUnsuccessful  = !isSuccessful;
                            Boolean isStable        = isSuccessful;
            
                            doBreak =   (specific.equals("Completed")   && isCompleted) ||
                                        (specific.equals("Successful")  && isCompleted && isSuccessful) ||
                                        (specific.equals("Failed")      && isCompleted && isFailed) ||
                                        (specific.equals("Unstable")    && isCompleted && isUnstable) ||
                                        (specific.equals("Unsuccessful") && isCompleted && isUnsuccessful) ||
                                        (specific.equals("isStable")    && isCompleted && isStable);    
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
