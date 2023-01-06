/**
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge.extensions;

import hudson.Extension;
import hudson.model.Job;
import hudson.model.Run;
import org.jenkinsci.plugins.badge.extensionpoints.InternalRunSelectorExtensionPoint;

@SuppressWarnings("rawtypes")
@Extension
public class BuildIdRunSelectorExtension implements InternalRunSelectorExtensionPoint {
    public Run select(Job project, String build, Run run) {
        Integer buildNr = 1;
        Boolean buildIdIsInteger = true;
        try {
            buildNr = Integer.parseInt(build);
        } catch (NumberFormatException e) {
            buildIdIsInteger = false;
        }

        if (buildIdIsInteger && buildNr <= 0) {
            if (run == null) {
                // find last build using relative build numbers
                run = project.getLastBuild();
            }

            for (; buildNr < 0 && run != null; buildNr++) {
                run = run.getPreviousBuild();
            }
        } else {
            if (build.equals("last")) {
                run = project.getLastBuild();
            } else if (build.equals("lastFailed")) {
                run = project.getLastFailedBuild();
            } else if (build.equals("lastSuccessful")) {
                run = project.getLastSuccessfulBuild();
            } else if (build.equals("lastUnsuccessful")) {
                run = project.getLastUnsuccessfulBuild();
            } else if (build.equals("lastStable")) {
                run = project.getLastStableBuild();
            } else if (build.equals("lastUnstable")) {
                run = project.getLastUnstableBuild();
            } else if (build.equals("lastCompleted")) {
                run = project.getLastCompletedBuild();
            } else {
                // try to get build via ID
                run = project.getBuild(build);
                if (run == null && buildIdIsInteger) {
                    run = project.getBuildByNumber(buildNr);
                }
            }
        }
        return run;
    }
}
