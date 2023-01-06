package org.jenkinsci.plugins.badge;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import hudson.model.Action;
import hudson.model.Job;
import java.io.IOException;
import java.util.Collection;
import org.jenkinsci.plugins.badge.actions.JobBadgeAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class JobBadgeActionFactoryTest {

    private JobBadgeActionFactory factory;

    @BeforeEach
    void setUp() throws IOException {
        factory = new JobBadgeActionFactory();
    }

    @Test
    void shouldCreateJobBadgeAction() {
        Collection<? extends Action> action = factory.createFor(Mockito.mock(Job.class));
        assertThat(action.size(), is(1));
        assertThat(action.stream().findFirst().get(), instanceOf(JobBadgeAction.class));
    }

    @Test
    void shouldBeForJobType() {
        assertThat(factory.type(), is(Job.class));
    }
}
