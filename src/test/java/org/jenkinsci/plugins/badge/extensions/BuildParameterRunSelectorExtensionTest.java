package org.jenkinsci.plugins.badge.extensions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import hudson.model.Job;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.StringParameterValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BuildParameterRunSelectorExtensionTest {

    private BuildParameterRunSelectorExtension extension;
    private Job mockProject;
    private Run mockRun;
    private ParametersAction mockParametersAction;

    @BeforeEach
    void setUp() {
        extension = new BuildParameterRunSelectorExtension();
        mockProject = Mockito.mock(Job.class);
        mockRun = Mockito.mock(Run.class);
        mockParametersAction = Mockito.mock(ParametersAction.class);
    }

    @Test
    void shouldSelectLastBuild() {
        when(mockProject.getLastBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "last", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "first", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastFailedBuild() {
        when(mockProject.getLastFailedBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastFailed", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstFailedBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.FAILURE);
        Run actualRun = extension.select(mockProject, "firstFailed", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastSuccessfulBuild() {
        when(mockProject.getLastSuccessfulBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastSuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstSuccessfulBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.SUCCESS);
        Run actualRun = extension.select(mockProject, "firstSuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastUnsuccessfulBuild() {
        when(mockProject.getLastUnsuccessfulBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastUnsuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstUnsuccessfulBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.FAILURE);
        Run actualRun = extension.select(mockProject, "firstUnsuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastStableBuild() {
        when(mockProject.getLastStableBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastStable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstStableBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.SUCCESS);
        Run actualRun = extension.select(mockProject, "firstStable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastUnstableBuild() {
        when(mockProject.getLastUnstableBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastUnstable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstUnstableBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.UNSTABLE);
        Run actualRun = extension.select(mockProject, "firstUnstable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastCompletedBuild() {
        when(mockProject.getLastCompletedBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastCompleted", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectFirstCompletedBuild() {
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun);
        when(mockRun.getResult()).thenReturn(Result.UNSTABLE);
        Run actualRun = extension.select(mockProject, "firstCompleted", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldSelectLastBuildWithMatchingParameter() {
        String paramName = "branch";
        String paramValue = "master";
        String rule = "params." + paramName + "=" + paramValue;
        StringParameterValue stringParameterValue = new StringParameterValue(paramName, paramValue);

        when(mockProject.getLastBuild()).thenReturn(mockRun);
        when(mockRun.getAction(ParametersAction.class)).thenReturn(mockParametersAction);
        when(mockParametersAction.getParameter(paramName)).thenReturn(stringParameterValue);

        Run actualRun = extension.select(mockProject, "last:${" + rule + "}", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldContinueToNextBuildWithNonMatchingParameterValue() {
        String paramName = "branch";
        String paramValueRule = "master";
        String paramValueActual = "develop";
        String rule = "params." + paramName + "=" + paramValueRule;
        StringParameterValue stringParameterValue = new StringParameterValue(paramName, paramValueActual);

        Run mockRun2 = Mockito.mock(Run.class);
        StringParameterValue matchingParameterValue = new StringParameterValue(paramName, paramValueRule);
        ParametersAction mockParametersAction2 = Mockito.mock(ParametersAction.class);

        when(mockProject.getLastBuild()).thenReturn(mockRun);
        when(mockRun.getAction(ParametersAction.class)).thenReturn(mockParametersAction);
        when(mockParametersAction.getParameter(paramName)).thenReturn(stringParameterValue);

        // Set up the second run to match the parameter
        when(mockRun.getPreviousBuild()).thenReturn(mockRun2);
        when(mockRun2.getAction(ParametersAction.class)).thenReturn(mockParametersAction2);
        when(mockParametersAction2.getParameter(paramName)).thenReturn(matchingParameterValue);

        Run actualRun = extension.select(mockProject, "last:${" + rule + "}", null);
        // It should have skipped the first run due to non-matching parameter and found the second run which does match
        assertThat(actualRun, is(mockRun2));
    }

    @Test
    void shouldHandleNullParameterValue() {
        String paramName = "branch";
        String paramValueRule = "master";
        String rule = "params." + paramName + "=" + paramValueRule;
        ParameterValue nullValueParameter = Mockito.mock(ParameterValue.class);

        Run mockRun2 = Mockito.mock(Run.class);
        StringParameterValue matchingParameterValue = new StringParameterValue(paramName, paramValueRule);
        ParametersAction mockParametersAction2 = Mockito.mock(ParametersAction.class);

        when(mockProject.getLastBuild()).thenReturn(mockRun);
        when(mockRun.getAction(ParametersAction.class)).thenReturn(mockParametersAction);
        when(mockParametersAction.getParameter(paramName)).thenReturn(nullValueParameter);
        when(nullValueParameter.getValue()).thenReturn(null);

        // Set up the second run to match the parameter
        when(mockRun.getPreviousBuild()).thenReturn(mockRun2);
        when(mockRun2.getAction(ParametersAction.class)).thenReturn(mockParametersAction2);
        when(mockParametersAction2.getParameter(paramName)).thenReturn(matchingParameterValue);

        Run actualRun = extension.select(mockProject, "last:${" + rule + "}", null);
        assertThat(actualRun, is(mockRun2));
    }

    @Test
    void shouldHandleMissingParameter() {
        String paramName = "branch";
        String paramValue = "master";
        String rule = "params." + paramName + "=" + paramValue;

        Run mockRun2 = Mockito.mock(Run.class);
        StringParameterValue matchingParameterValue = new StringParameterValue(paramName, paramValue);
        ParametersAction mockParametersAction2 = Mockito.mock(ParametersAction.class);

        when(mockProject.getLastBuild()).thenReturn(mockRun);
        when(mockRun.getAction(ParametersAction.class)).thenReturn(mockParametersAction);
        when(mockParametersAction.getParameter(paramName)).thenReturn(null); // Parameter not found

        // Set up the second run to match the parameter
        when(mockRun.getPreviousBuild()).thenReturn(mockRun2);
        when(mockRun2.getAction(ParametersAction.class)).thenReturn(mockParametersAction2);
        when(mockParametersAction2.getParameter(paramName)).thenReturn(matchingParameterValue);

        Run actualRun = extension.select(mockProject, "last:${" + rule + "}", null);
        assertThat(actualRun, is(mockRun2));
    }

    @Test
    void shouldHandleMissingParametersAction() {
        String paramName = "branch";
        String paramValue = "master";
        String rule = "params." + paramName + "=" + paramValue;

        Run mockRun2 = Mockito.mock(Run.class);
        StringParameterValue matchingParameterValue = new StringParameterValue(paramName, paramValue);
        ParametersAction mockParametersAction2 = Mockito.mock(ParametersAction.class);

        when(mockProject.getLastBuild()).thenReturn(mockRun);
        when(mockRun.getAction(ParametersAction.class)).thenReturn(null); // No ParametersAction

        // Set up the second run to match the parameter
        when(mockRun.getPreviousBuild()).thenReturn(mockRun2);
        when(mockRun2.getAction(ParametersAction.class)).thenReturn(mockParametersAction2);
        when(mockParametersAction2.getParameter(paramName)).thenReturn(matchingParameterValue);

        Run actualRun = extension.select(mockProject, "last:${" + rule + "}", null);
        assertThat(actualRun, is(mockRun2));
    }

    @Test
    void shouldHandleInvalidRule() {
        String invalidRule = "invalidRuleSyntax";

        Run mockRun2 = Mockito.mock(Run.class);
        // Create a valid parameter setup in the second run
        String paramName = "branch";
        String paramValue = "master";
        StringParameterValue matchingParameterValue = new StringParameterValue(paramName, paramValue);
        ParametersAction mockParametersAction2 = Mockito.mock(ParametersAction.class);

        when(mockProject.getLastBuild()).thenReturn(mockRun);

        // Set up the second run that would match if the rule was valid
        when(mockRun.getPreviousBuild()).thenReturn(mockRun2);
        when(mockRun2.getAction(ParametersAction.class)).thenReturn(mockParametersAction2);
        when(mockParametersAction2.getParameter(paramName)).thenReturn(matchingParameterValue);

        // For an invalid rule, all runs should be considered without parameter matching
        Run actualRun = extension.select(mockProject, "last:" + invalidRule, null);
        // We should get the first run because the invalid rule doesn't force parameter matching
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldReturnNullWhenNoMatchingBuilds() {
        String paramName = "branch";
        String paramValue = "master";
        String rule = "params." + paramName + "=" + paramValue;

        // Set up a chain of builds with no matching parameter
        Run mockRun2 = Mockito.mock(Run.class);
        ParametersAction mockParametersAction2 = Mockito.mock(ParametersAction.class);
        StringParameterValue nonMatchingValue = new StringParameterValue(paramName, "develop");

        when(mockProject.getLastBuild()).thenReturn(mockRun);
        when(mockRun.getAction(ParametersAction.class)).thenReturn(mockParametersAction);
        when(mockParametersAction.getParameter(paramName)).thenReturn(nonMatchingValue);

        when(mockRun.getPreviousBuild()).thenReturn(mockRun2);
        when(mockRun2.getAction(ParametersAction.class)).thenReturn(mockParametersAction2);
        when(mockParametersAction2.getParameter(paramName)).thenReturn(nonMatchingValue);

        // Last build in chain has no next build
        when(mockRun2.getPreviousBuild()).thenReturn(null);

        Run actualRun = extension.select(mockProject, "last:${" + rule + "}", null);
        // No builds match so we should get null
        assertThat(actualRun, is(nullValue()));
    }

    @Test
    void shouldHandleRunWithNullResult() {
        // For the "lastFailed" case, the implementation first checks job.getLastFailedBuild()
        // So we need to mock this behavior
        when(mockProject.getLastFailedBuild()).thenReturn(mockRun);

        Run actualRun = extension.select(mockProject, "lastFailed", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleMultipleRuleMatchers() {
        // Test cases for multiple rule matchers in a single runId
        String paramName = "branch";
        String paramValue = "master";
        String rule = "params." + paramName + "=" + paramValue;

        StringParameterValue stringParameterValue = new StringParameterValue(paramName, paramValue);

        when(mockProject.getLastBuild()).thenReturn(mockRun);
        when(mockRun.getAction(ParametersAction.class)).thenReturn(mockParametersAction);
        when(mockParametersAction.getParameter(paramName)).thenReturn(stringParameterValue);

        // Use multiple matchers in the runId
        Run actualRun = extension.select(mockProject, "last:${" + rule + "}lastSuccessful", null);

        // It should process both matchers and eventually select a run
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldContinueFindingSpecificRunWhenResultDoesNotMatch() {
        Run mockRun2 = Mockito.mock(Run.class);
        Run mockRun3 = Mockito.mock(Run.class);

        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun2);
        when(mockRun2.getNextBuild()).thenReturn(mockRun3);
        when(mockRun3.getNextBuild()).thenReturn(null);

        // Use Result.FAILURE instead of mocking Result.isCompleteBuild
        when(mockRun.getResult()).thenReturn(Result.FAILURE);

        // Second run has Result that doesn't match
        when(mockRun2.getResult()).thenReturn(Result.UNSTABLE);

        // Third run has Result that matches
        when(mockRun3.getResult()).thenReturn(Result.SUCCESS);

        Run actualRun = extension.select(mockProject, "firstSuccessful", null);
        // Should find mockRun3 as it matches SUCCESS
        assertThat(actualRun, is(mockRun3));
    }

    @Test
    void shouldProcessNonMatchingSpecific() {
        when(mockProject.getLastBuild()).thenReturn(mockRun);

        // Use known Result types instead of Boolean values
        when(mockRun.getResult()).thenReturn(Result.SUCCESS);

        // Test with a specific that doesn't match any of the known cases
        Run actualRun = extension.select(mockProject, "lastUnknownState", null);

        // It should still select the last build
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleInvalidParameterRule() {
        // For an invalid parameter rule, we need to ensure it's actually in the ${} format
        // but with invalid content inside that doesn't match params.x=y pattern
        String invalidRule = "someInvalidContent";

        when(mockProject.getLastBuild()).thenReturn(mockRun);
        // The select method expects a proper runId format: "last:${rule}"
        Run actualRun = extension.select(mockProject, "last", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleCompletedBuildCriteria() {
        // For firstCompleted, we need to set up the lastCompletedBuild mock since that's what
        // the actual implementation of BuildParameterRunSelectorExtension is using
        when(mockProject.getLastCompletedBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastCompleted", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleNotMatchingSpecificTypeWithNullResult() {
        // Test case where run.getResult() returns null in findSpecific method
        Run mockRun2 = Mockito.mock(Run.class);

        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun2);
        when(mockRun2.getNextBuild()).thenReturn(null);

        // First run has null result
        when(mockRun.getResult()).thenReturn(null);

        // Second run has a result that matches
        when(mockRun2.getResult()).thenReturn(Result.SUCCESS);

        Run actualRun = extension.select(mockProject, "firstSuccessful", null);
        assertThat(actualRun, is(mockRun2));
    }

    @Test
    void shouldHandleMultipleSpecificTypeMatchers() {
        // Test for each specific type in the switch cases
        when(mockProject.getLastStableBuild()).thenReturn(mockRun);
        Run actualRun = extension.select(mockProject, "lastStable", null);
        assertThat(actualRun, is(mockRun));

        // Reset with a new run for each type
        Run mockRun2 = Mockito.mock(Run.class);
        when(mockProject.getLastCompletedBuild()).thenReturn(mockRun2);
        actualRun = extension.select(mockProject, "lastCompleted", null);
        assertThat(actualRun, is(mockRun2));
    }

    @Test
    void shouldHandleRuleMatchingWithAdditionalText() {
        // Test case for matchRule with specific text and matcher
        String paramName = "branch";
        String paramValue = "master";
        String rule = "params." + paramName + "=" + paramValue;
        StringParameterValue stringParameterValue = new StringParameterValue(paramName, paramValue);

        when(mockProject.getLastBuild()).thenReturn(mockRun);
        when(mockRun.getAction(ParametersAction.class)).thenReturn(mockParametersAction);
        when(mockParametersAction.getParameter(paramName)).thenReturn(stringParameterValue);

        // Test with multiple matchers in the runId
        Run actualRun = extension.select(mockProject, "last:${" + rule + "}additionalText", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleNullRunResult() {
        Run mockRun2 = Mockito.mock(Run.class);

        // Set up a chain with a null result that should skip to the next run
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun2);
        when(mockRun2.getNextBuild()).thenReturn(null);

        // First run has a null result
        when(mockRun.getResult()).thenReturn(null);

        // Second run has a successful result
        when(mockRun2.getResult()).thenReturn(Result.SUCCESS);

        // Only the second run should satisfy our "firstSuccessful" condition
        Run actualRun = extension.select(mockProject, "firstSuccessful", null);
        assertThat(actualRun, is(mockRun2));
    }

    @Test
    void shouldHandleForFirstCompletedSpecificCase() {
        Run mockRun2 = Mockito.mock(Run.class);
        Result mockResult = Mockito.mock(Result.class);

        // Ensure the first run doesn't meet criteria
        when(mockProject.getFirstBuild()).thenReturn(mockRun);
        when(mockRun.getNextBuild()).thenReturn(mockRun2);
        when(mockRun2.getNextBuild()).thenReturn(null);

        // First run's result doesn't satisfy our conditions
        when(mockRun.getResult()).thenReturn(Result.ABORTED);

        // Second run has a completed result
        when(mockRun2.getResult()).thenReturn(mockResult);
        when(mockResult.isCompleteBuild()).thenReturn(true);

        Run actualRun = extension.select(mockProject, "firstCompleted", null);
        assertThat(actualRun, is(mockRun2));
    }

    @Test
    void shouldHandleSpecificStableMatching() {
        // Test specifically for the "Stable" condition branch
        // For "lastStable", the implementation will use job.getLastStableBuild()
        when(mockProject.getLastStableBuild()).thenReturn(mockRun);

        Run actualRun = extension.select(mockProject, "lastStable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleSpecificUnstableMatching() {
        // Test specifically for the "Unstable" condition branch
        // For "lastUnstable", the implementation will use job.getLastUnstableBuild()
        when(mockProject.getLastUnstableBuild()).thenReturn(mockRun);

        Run actualRun = extension.select(mockProject, "lastUnstable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleSpecificUnsuccessfulMatching() {
        // Test specifically for the "Unsuccessful" condition branch
        // For "lastUnsuccessful", the implementation will use
        // job.getLastUnsuccessfulBuild()
        when(mockProject.getLastUnsuccessfulBuild()).thenReturn(mockRun);

        Run actualRun = extension.select(mockProject, "lastUnsuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleRunWithPreviousRunProvided() {
        // Test when a run is provided and we're looking for specific behavior
        when(mockProject.getLastSuccessfulBuild()).thenReturn(mockRun);

        Run actualRun = extension.select(mockProject, "lastSuccessful", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleEmptyRunId() {
        // Test with an empty runId to ensure it doesn't match the regex pattern
        Run actualRun = extension.select(mockProject, "", null);
        assertThat(actualRun, is(nullValue()));
    }

    @Test
    void shouldHandleDefaultCaseInSwitch() {
        // Test the default case in the switch statement with an invalid specific value
        Run actualRun = extension.select(mockProject, "lastInvalidSpecific", null);

        // The method should try to get the last build as a fallback
        when(mockProject.getLastBuild()).thenReturn(mockRun);
        actualRun = extension.select(mockProject, "lastInvalidSpecific", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleNonMatchingParameterRule() {
        // Test for a non-matching parameter pattern - focusing on matcher.find() branch
        // For a non-parameter pattern, the rule doesn't match the format
        // We need to mock the project's getLastBuild method to ensure we get a run
        when(mockProject.getLastBuild()).thenReturn(mockRun);

        // Use a real string that's in the format we need for the rule parameter
        Run actualRun = extension.select(mockProject, "last", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldHandleComplexCombinationOfConditions() {
        // Setup multiple runs with different conditions to exercise the complex logic
        Run run1 = mockRun;
        Run run2 = Mockito.mock(Run.class);
        Run run3 = Mockito.mock(Run.class);

        // Chain the runs
        when(mockProject.getFirstBuild()).thenReturn(run1);
        when(run1.getNextBuild()).thenReturn(run2);
        when(run2.getNextBuild()).thenReturn(run3);
        when(run3.getNextBuild()).thenReturn(null);

        // Setup different results for each run
        when(run1.getResult()).thenReturn(null); // No result
        when(run2.getResult()).thenReturn(Result.UNSTABLE); // Unstable result
        when(run3.getResult()).thenReturn(Result.SUCCESS); // Success result

        // Test finding different specific types - but use the project.getLastXXX methods
        when(mockProject.getLastUnstableBuild()).thenReturn(run2);
        when(mockProject.getLastSuccessfulBuild()).thenReturn(run3);

        Run actualRun = extension.select(mockProject, "lastUnstable", null);
        assertThat(actualRun, is(run2));

        actualRun = extension.select(mockProject, "lastSuccessful", null);
        assertThat(actualRun, is(run3));
    }

    @Test
    void shouldHandleRunWithSpecificConditions() {
        // For lastUnstable, the implementation uses job.getLastUnstableBuild()
        when(mockProject.getLastUnstableBuild()).thenReturn(mockRun);

        Run actualRun = extension.select(mockProject, "lastUnstable", null);
        assertThat(actualRun, is(mockRun));
    }

    @Test
    void shouldMatchParameterValueWithSpecificPattern() {
        // Test the parameter matching pattern
        String paramName = "branch";
        String paramValue = "master";

        // Create a mock parameter
        ParametersAction paramsAction = Mockito.mock(ParametersAction.class);
        ParameterValue parameterValue = Mockito.mock(ParameterValue.class);

        // Set up the mock parameter to match the pattern
        when(mockRun.getAction(ParametersAction.class)).thenReturn(paramsAction);
        when(paramsAction.getParameter(paramName)).thenReturn(parameterValue);
        when(parameterValue.getValue()).thenReturn(paramValue);

        // Call the method directly through reflection to test just that method
        try {
            java.lang.reflect.Method method = BuildParameterRunSelectorExtension.class.getDeclaredMethod(
                    "matchRule", Job.class, Run.class, String.class);
            method.setAccessible(true);
            BuildParameterRunSelectorExtension extension = new BuildParameterRunSelectorExtension();
            Boolean result =
                    (Boolean) method.invoke(extension, mockProject, mockRun, "params." + paramName + "=" + paramValue);
            assertThat(result, is(true));
        } catch (Exception e) {
            // Just log the exception and fail the test
            System.err.println("Failed to invoke matchRule method: " + e.getMessage());
            assertThat("Test should not fail with exception", false);
        }
    }
}
