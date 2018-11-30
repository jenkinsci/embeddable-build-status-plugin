package org.jenkinsci.plugins.badge;

import hudson.Extension;
import hudson.model.*;
import jenkins.model.TransientActionFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings("rawtypes")
@Extension
public class RunBadgeActionFactory extends TransientActionFactory<Run> {

    private final IconRequestHandler iconRequestHandler;

    public RunBadgeActionFactory() throws IOException {
        this.iconRequestHandler = new IconRequestHandler();
    }

    @Override
    public Class<Run> type() {
        return Run.class;
    }

    @Override
    public Collection<? extends Action> createFor(Run target) {
        return Collections.singleton(new RunBadgeAction(this, target));
    }

    public IconRequestHandler iconRequestHandler() {
        return iconRequestHandler;
    }
}
