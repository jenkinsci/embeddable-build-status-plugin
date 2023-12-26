package org.jenkinsci.plugins.badge.extensions;

import edu.hm.hafner.coverage.Coverage;
import edu.hm.hafner.coverage.Metric;
import edu.hm.hafner.coverage.Value;
import hudson.Extension;
import hudson.model.Actionable;
import hudson.model.Job;
import hudson.model.Run;
import io.jenkins.plugins.coverage.metrics.model.Baseline;
import io.jenkins.plugins.coverage.metrics.model.ElementFormatter;
import io.jenkins.plugins.coverage.metrics.steps.CoverageBuildAction;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.badge.extensionpoints.ParameterResolverExtensionPoint;

@Extension(optional = true)
public class CoverageParameterResolverExtension implements ParameterResolverExtensionPoint {

    private static final ElementFormatter FORMATTER = new ElementFormatter();

    public static final Logger LOGGER = Logger.getLogger(CoverageParameterResolverExtension.class.getName());

    @Override
    public String resolve(Actionable actionable, String parameter) {

        // Just return parameter if coverage plugin is not installed
        if (Jenkins.get().getPlugin("coverage") == null) {
            return parameter;
        }

        if (parameter != null) {
            if (actionable instanceof Run<?, ?>) {
                Run<?, ?> run = (Run<?, ?>) actionable;

                // Get the action
                CoverageBuildAction action = run.getAction(CoverageBuildAction.class);
                if (action == null) {
                    return parameter;
                }

                // Get the values
                Value intructionCoverage = action.getStatistics()
                        .getValue(Baseline.PROJECT, Metric.INSTRUCTION)
                        .orElse(null);
                Value branchCoverage = action.getStatistics()
                        .getValue(Baseline.PROJECT, Metric.BRANCH)
                        .orElse(null);
                Value lineOfCode = action.getStatistics()
                        .getValue(Baseline.PROJECT, Metric.LOC)
                        .orElse(null);

                // Replace the parameters
                parameter = parameter
                        .replace(
                                "intructionCoverage",
                                intructionCoverage != null ? FORMATTER.format(intructionCoverage) : parameter)
                        .replace(
                                "branchCoverage", branchCoverage != null ? FORMATTER.format(branchCoverage) : parameter)
                        .replace("lineOfCode", lineOfCode != null ? FORMATTER.format(lineOfCode) : parameter)
                        .replace("colorInstructionCoverage", getColor(intructionCoverage))
                        .replace("colorBranchCoverage", getColor(branchCoverage));

            } else if (actionable instanceof Job<?, ?>) {
                parameter = resolve(((Job<?, ?>) actionable).getLastBuild(), parameter);
            }
        }
        return parameter;
    }

    private String getColor(Value value) {
        if (value instanceof Coverage) {
            Coverage coverage = (Coverage) value;
            int percentage = coverage.getCoveredPercentage().toInt();
            if (percentage <= 20.0) {
                return "red";
            } else if (percentage <= 30.0) {
                return "orange";
            } else if (percentage <= 40.0) {
                return "yellow";
            } else if (percentage <= 50.0) {
                return "yellowgreen";
            } else if (percentage <= 70.0) {
                return "green";
            } else {
                return "brightgreen";
            }
        }
        return "green";
    }
}
