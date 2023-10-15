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
    @Override
    public Run select(Job project, String build, Run run) {
        int buildNr = 1;
        boolean buildIdIsInteger = true;
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
            switch (build) {
                case "last":
                    run = project.getLastBuild();
                    break;
                case "lastFailed":
                    run = project.getLastFailedBuild();
                    break;
                case "lastSuccessful":
                    run = project.getLastSuccessfulBuild();
                    break;
                case "lastUnsuccessful":
                    run = project.getLastUnsuccessfulBuild();
                    break;
                case "lastStable":
                    run = project.getLastStableBuild();
                    break;
                case "lastUnstable":
                    run = project.getLastUnstableBuild();
                    break;
                case "lastCompleted":
                    run = project.getLastCompletedBuild();
                    break;
                default:
                    // try to get build via ID
                    run = project.getBuild(build);
                    if (run == null && buildIdIsInteger) {
                        run = project.getBuildByNumber(buildNr);
                    }
                    break;
            }
        }
        return run;
    }
}
