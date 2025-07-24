package org.jenkinsci.plugins.badge;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.Actionable;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;
import org.mockito.Mockito;

class ParameterResolverTest {

    private ParameterResolver resolver;

    @BeforeEach
    void defineResolver() {
        resolver = new ParameterResolver();
    }

    @ParameterizedTest
    @CsvSource({
        "Build ${params.BUILD_BRANCH},Build params.BUILD_BRANCH",
        "Build ${params.BUILD_BRANCH|master},Build params.BUILD_BRANCH|master",
        "Build ${params.BUILD_BRANCH|master} (${displayName}),Build params.BUILD_BRANCH|master (displayName)"
    })
    void shouldResolveSubjectWithVariables(String queryParameter, String expectedParameter) {
        String resolvedParameter = resolver.resolve(Mockito.mock(Actionable.class), queryParameter);
        assertThat(resolvedParameter, is(expectedParameter));
    }

    @Test
    void shouldResolveNullParameter() {
        String resolvedParameter = resolver.resolve(Mockito.mock(Actionable.class), null);
        assertThat(resolvedParameter, is(nullValue()));
    }

    @Test
    void shouldResolveEmptyParameter() {
        String resolvedParameter = resolver.resolve(Mockito.mock(Actionable.class), "");
        assertThat(resolvedParameter, is(""));
    }

    @Test
    @WithJenkins
    void shouldResolveSpecialParameters(JenkinsRule r) throws Exception {
        FreeStyleProject p = r.createFreeStyleProject();
        p.setDescription("Project description is ignored");

        FreeStyleBuild build1 = r.buildAndAssertSuccess(p);
        String myDescription1 = "Build 1 description is used";
        build1.setDescription(myDescription1);
        String resolved1 = resolver.resolve(
                build1, "build-${buildNumber} ${displayName} id-${buildId} ${description} ${duration}");
        assertThat(resolved1, is("build-1 #1 id-1 " + myDescription1 + " " + build1.getDurationString()));

        FreeStyleBuild build2 = r.buildAndAssertSuccess(p);
        String myDescription2 = "Build 2 description is used";
        build2.setDescription(myDescription2);
        String resolved2 = resolver.resolve(
                build2, "${displayName} build-${buildNumber} id-${buildId} ${duration} ${description}");
        assertThat(resolved2, is("#2 build-2 id-2 " + build2.getDurationString() + " " + myDescription2));
    }
}
