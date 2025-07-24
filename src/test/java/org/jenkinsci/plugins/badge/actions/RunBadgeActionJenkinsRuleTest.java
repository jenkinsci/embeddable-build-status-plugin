package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.Run;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class RunBadgeActionJenkinsRuleTest {

    private static JenkinsRule j;
    private static RunBadgeAction action;

    @BeforeAll
    static void createAction(JenkinsRule rule) throws Exception {
        j = rule;
        Run<?, ?> run = j.buildAndAssertSuccess(j.createFreeStyleProject());
        action = new RunBadgeAction(run);
    }

    @Test
    void getIconFileName() {
        assertThat(action.getIconFileName(), is(nullValue()));
    }

    @Test
    void getIconClassName() {
        assertThat(action.getIconClassName(), is("symbol-shield-outline plugin-ionicons-api"));
    }

    @Test
    void getDisplayName() {
        assertThat(action.getDisplayName(), is("Embeddable Build Status"));
    }

    @Test
    void getUrlName() {
        assertThat(action.getUrlName(), is("badge"));
    }

    @Test
    void getUrlEncodedFullName() {
        assertThat(action.getUrlEncodedFullName(), is("test0"));
    }

    @Test
    void doText() {
        assertThat(action.doText(), is("Success"));
    }
}
