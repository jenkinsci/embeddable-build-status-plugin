package org.jenkinsci.plugins.badge;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.jenkinsci.plugins.badge.extensions.CoverageParameterResolverExtension;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class CoverageParameterResolverExtensionTest {

    @Test
    void shouldResolveIntructionCoverage(JenkinsRule jenkins) throws Exception {
        String pipeline = IOUtils.toString(
                CoverageParameterResolverExtensionTest.class.getResourceAsStream("/pipelines/coverage.groovy"),
                StandardCharsets.UTF_8);
        WorkflowJob workflowJob = jenkins.createProject(WorkflowJob.class);
        workflowJob.setDefinition(new CpsFlowDefinition(pipeline, false));
        WorkflowRun run1 = workflowJob.scheduleBuild2(0).waitForStart();
        jenkins.waitForCompletion(run1);
        assertThat(run1.getResult(), equalTo(hudson.model.Result.SUCCESS));

        // Coverages badges
        assertThat(new CoverageParameterResolverExtension().resolve(run1, "unknown"), is("unknown"));
        assertThat(new CoverageParameterResolverExtension().resolve(run1, "intructionCoverage"), is("50.00%"));
        assertThat(new CoverageParameterResolverExtension().resolve(run1, "branchCoverage"), is("100.00%"));
        assertThat(new CoverageParameterResolverExtension().resolve(run1, "lineOfCode"), is("10"));
        assertThat(
                new CoverageParameterResolverExtension().resolve(run1, "colorInstructionCoverage"), is("yellowgreen"));
        assertThat(new CoverageParameterResolverExtension().resolve(run1, "colorBranchCoverage"), is("brightgreen"));
    }
}
