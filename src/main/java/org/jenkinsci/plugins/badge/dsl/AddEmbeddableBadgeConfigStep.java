/**
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge.dsl;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import java.util.HashSet;
import java.util.Set;
import org.jenkinsci.plugins.badge.EmbeddableBadgeConfig;
import org.jenkinsci.plugins.badge.actions.EmbeddableBadgeConfigsAction;
import org.jenkinsci.plugins.badge.annotations.OptionalParam;
import org.jenkinsci.plugins.badge.annotations.Param;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.jenkinsci.plugins.workflow.steps.SynchronousStepExecution;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

public class AddEmbeddableBadgeConfigStep extends Step {

    /* Package protected for automated tests */
    final EmbeddableBadgeConfig badgeConfig;

    @DataBoundConstructor
    public AddEmbeddableBadgeConfigStep(
            @Param(name = "id", description = "The id for the badge configuration") String id) {
        this.badgeConfig = new EmbeddableBadgeConfig(id);
    }

    public String getID() {
        return badgeConfig.getID();
    }

    public String getSubject() {
        return badgeConfig.getSubject();
    }

    @DataBoundSetter
    @OptionalParam(description = "The subject used for the badge configuration")
    public void setSubject(String subject) {
        this.badgeConfig.setSubject(subject);
    }

    public String getStatus() {
        return badgeConfig.getStatus();
    }

    @DataBoundSetter
    @OptionalParam(description = "The status used for the badge configuration")
    public void setStatus(String status) {
        this.badgeConfig.setStatus(status);
    }

    public String getColor() {
        return badgeConfig.getColor();
    }

    @DataBoundSetter
    @OptionalParam(description = "The color used for the badge configuration")
    public void setColor(String color) {
        this.badgeConfig.setColor(color);
    }

    public String getAnimatedOverlayColor() {
        return badgeConfig.getAnimatedOverlayColor();
    }

    @DataBoundSetter
    @OptionalParam(description = "The animated overlay color used for the badge configuration")
    public void setAnimatedOverlayColor(String animatedOverlayColor) {
        this.badgeConfig.setAnimatedOverlayColor(animatedOverlayColor);
    }

    public String getLink() {
        return badgeConfig.getLink();
    }

    @DataBoundSetter
    @OptionalParam(description = "The link the will be followed when clicking on the svg")
    public void setLink(String link) {
        this.badgeConfig.setLink(link);
    }

    @Override
    public StepExecution start(StepContext context) {
        return new Execution(badgeConfig, context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "addEmbeddableBadgeConfiguration";
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Add an Embeddable Badge Configuration";
        }

        @Override
        public Set<Class<?>> getRequiredContext() {
            Set<Class<?>> set = new HashSet<>();
            set.add(TaskListener.class);
            return set;
        }
    }

    public static class Execution extends SynchronousStepExecution<EmbeddableBadgeConfig> {

        @SuppressFBWarnings(value = "SE_TRANSIENT_FIELD_NOT_RESTORED", justification = "Only used when starting.")
        private final transient EmbeddableBadgeConfig badgeConfig;

        Execution(EmbeddableBadgeConfig badgeConfig, StepContext context) {
            super(context);
            this.badgeConfig = badgeConfig;
        }

        @Override
        protected EmbeddableBadgeConfig run() throws Exception {
            EmbeddableBadgeConfigsAction cfgsAction =
                    getContext().get(Run.class).getAction(EmbeddableBadgeConfigsAction.class);
            if (cfgsAction == null) {
                cfgsAction = new EmbeddableBadgeConfigsAction();
                getContext().get(Run.class).addAction(cfgsAction);
            }

            cfgsAction.addConfig(badgeConfig);
            return badgeConfig;
        }

        private static final long serialVersionUID = 1L;
    }
}
