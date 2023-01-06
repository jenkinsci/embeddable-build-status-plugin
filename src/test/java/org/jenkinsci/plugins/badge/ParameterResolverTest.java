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
        String resolvedParameter =
                new ParameterResolver().resolve(Mockito.mock(Actionable.class), queryParameter);
        assertThat(resolvedParameter, is(expectedParameter));
    }
}
