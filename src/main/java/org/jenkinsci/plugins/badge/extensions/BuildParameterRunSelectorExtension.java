package org.jenkinsci.plugins.badge.extensions;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.Job;
import hudson.model.ParametersAction;
import hudson.model.ParameterValue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jenkinsci.plugins.badge.extensionpoints.RunSelectorExtensionPoint;

/**
 * @author Thomas D.
 */
@Extension
public class BuildParameterRunSelectorExtension implements RunSelectorExtensionPoint {

    private static Pattern outerSelector = Pattern.compile("(last|first):\\$\\{([^\\{\\}\\s]+)\\}");

    private static Pattern paramsPattern = Pattern.compile("params\\.([^=]+)=(.*)");
    private Boolean matchRule(Job job, Run run, String rule) {

        // 1. try to match ${params.xxx=yyy}
        Matcher matcher = paramsPattern.matcher(rule);
        if (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = matcher.group(2);
            
            ParametersAction params = run.getAction(ParametersAction.class);
            if (params != null) {
                ParameterValue value = params.getParameter(paramName);
                if (value != null && value.getValue().toString().equals(paramValue)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Run select(Job job, String runId) {

        Run run = null;
        Matcher matcher = outerSelector.matcher(runId);
        while (matcher.find()) {
            String what = matcher.group(1);
            String rule = matcher.group(2);

            if (run == null) {
                if (what.equals("first")) {
                    run = job.getFirstBuild();
                } else {
                    run = job.getLastBuild();
                }
            }

            // iterate over all Runs forward
            while (run != null) {
                if (matchRule(job, run, rule)) {
                    break;
                }

                if (what.equals("first")) {
                    run = run.getNextBuild();
                } else {
                    run = run.getPreviousBuild();
                }
            }
            runId = matcher.replaceAll("");
            matcher = outerSelector.matcher(runId);
        }

        return run;
    }
}
