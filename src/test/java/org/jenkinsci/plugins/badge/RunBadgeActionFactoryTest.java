package org.jenkinsci.plugins.badge;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import hudson.model.Action;
import hudson.model.Run;
import java.io.IOException;
import java.util.Collection;
import org.jenkinsci.plugins.badge.actions.RunBadgeAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class RunBadgeActionFactoryTest {

    private RunBadgeActionFactory factory;

    @BeforeEach
    void setUp() throws IOException {
        factory = new RunBadgeActionFactory();
    }

    @Test
    void shouldCreateJobBadgeAction() {
        Collection<? extends Action> action = factory.createFor(Mockito.mock(Run.class));
        assertThat(action.size(), is(1));
        assertThat(action.stream().findFirst().get(), instanceOf(RunBadgeAction.class));
    }

    @Test
    void shouldBeForJobType() {
        assertThat(factory.type(), is(Run.class));
    }
}
