package org.jenkinsci.plugins.badge;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.Actionable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

class ParameterResolverTest {
    @ParameterizedTest
    @CsvSource({
        "Build ${params.BUILD_BRANCH},Build params.BUILD_BRANCH",
        "Build ${params.BUILD_BRANCH|master},Build params.BUILD_BRANCH|master",
        "Build ${params.BUILD_BRANCH|master} (${displayName}),Build params.BUILD_BRANCH|master (displayName)"
    })
    void shouldResolveSubjectWithVariables(String queryParameter, String expectedParameter) {
        String resolvedParameter = new ParameterResolver().resolve(Mockito.mock(Actionable.class), queryParameter);
        assertThat(resolvedParameter, is(expectedParameter));
    }

    void shouldResolveNullParameter() {
        String resolvedParameter = new ParameterResolver().resolve(Mockito.mock(Actionable.class), null);
        assertThat(resolvedParameter, is(null));
    }

    void shouldResolveEmptyParameter() {
        String resolvedParameter = new ParameterResolver().resolve(Mockito.mock(Actionable.class), "");
        assertThat(resolvedParameter, is(""));
    }

    void shouldResolveParameterWithoutVariables() {
        String resolvedParameter = new ParameterResolver().resolve(Mockito.mock(Actionable.class), "Build");
        assertThat(resolvedParameter, is("Build"));
    }

    void shouldResolveParameterWithVariables() {
        String resolvedParameter =
                new ParameterResolver().resolve(Mockito.mock(Actionable.class), "Build ${params.BUILD_BRANCH}");
        assertThat(resolvedParameter, is("Build params.BUILD_BRANCH"));
    }

    void shouldResolveParameterWithVariablesAndText() {
        String resolvedParameter = new ParameterResolver()
                .resolve(Mockito.mock(Actionable.class), "Build ${params.BUILD_BRANCH} (${displayName})");
        assertThat(resolvedParameter, is("Build params.BUILD_BRANCH (displayName)"));
    }

    void shouldResolveParameterWithVariablesAndTextAndDefault() {
        String resolvedParameter = new ParameterResolver()
                .resolve(Mockito.mock(Actionable.class), "Build ${params.BUILD_BRANCH|master} (${displayName})");
        assertThat(resolvedParameter, is("Build params.BUILD_BRANCH|master (displayName)"));
    }

    void shouldNotResolveParameterOfVaryingLengthInRegex() {
        ParameterResolver resolver = new ParameterResolver();
        String resolvedParameter = resolver.resolve(Mockito.mock(Actionable.class), "(?<=\\w*)");
        assertThat(resolvedParameter, is("(?<=\\w*)"));
    }

    void  shouldNotResolveParameterWithFullRangeUnicode(){
        String resolvedParameter = new ParameterResolver()
                .resolve(Mockito.mock(Actionable.class), "\\p{InGreek}");
        assertThat(resolvedParameter, is("\\p{InGreek}"));
    }

    void shouldNotResolveParameterWithRegexInlineFlags(){
        String resolvedParameter = new ParameterResolver()
                .resolve(Mockito.mock(Actionable.class), "(${\\Build(?i) params\\b})");
        assertThat(resolvedParameter, is("(${\\Build(?i) params\\b})"));
    }
}
