package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.Run;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class RunBadgeActionJenkinsRuleTest {

    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    private static RunBadgeAction action;

    @BeforeClass
    public static void createAction() throws Exception {
        Run<?, ?> run = j.buildAndAssertSuccess(j.createFreeStyleProject());
        action = new RunBadgeAction(run);
    }

    @Test
    public void getIconFileName() {
        assertThat(action.getIconFileName(), is(nullValue()));
    }

    @Test
    public void getIconClassName() {
        assertThat(action.getIconClassName(), is("symbol-shield-outline plugin-ionicons-api"));
    }

    @Test
    public void getDisplayName() {
        assertThat(action.getDisplayName(), is("Embeddable Build Status"));
    }

    @Test
    public void getUrlName() {
        assertThat(action.getUrlName(), is("badge"));
    }

    @Test
    public void getUrlEncodedFullName() {
        assertThat(action.getUrlEncodedFullName(), is("test0"));
    }

    @Test
    public void doText() {
        assertThat(action.doText(), is("Success"));
    }
}
