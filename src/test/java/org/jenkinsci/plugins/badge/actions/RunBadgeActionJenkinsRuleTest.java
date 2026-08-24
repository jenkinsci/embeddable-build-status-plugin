package org.jenkinsci.plugins.badge.actions;

import static org.hamcrest.CoreMatchers.containsString;
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

    @Test
    void getNumber() {
        assertThat(action.getNumber(), is(1));
    }

    @Test
    void badgePagePublicMarkdownIncludesBuildNumber() throws Exception {
        String jenkinsUrl = j.getURL().toString();
        String pageUrl = jenkinsUrl + action.run.getUrl() + "badge/";
        String expectedPublicBadgeUrl = jenkinsUrl + "buildStatus/icon?job=" + action.getUrlEncodedFullName()
                + "&amp;build=" + action.getNumber();
        String expectedPublicTextUrl = jenkinsUrl + "buildStatus/text?job=" + action.getUrlEncodedFullName()
                + "&amp;build=" + action.getNumber();

        try (JenkinsRule.WebClient webClient = j.createWebClient()) {
            webClient.setJavaScriptEnabled(false);
            String html = webClient.getPage(pageUrl).getWebResponse().getContentAsString();

            assertThat(html, containsString("data-public-badge-url=\"" + expectedPublicBadgeUrl + "\""));
            assertThat(html, containsString("data-public-text-url=\"" + expectedPublicTextUrl + "\""));
        }
    }
}
