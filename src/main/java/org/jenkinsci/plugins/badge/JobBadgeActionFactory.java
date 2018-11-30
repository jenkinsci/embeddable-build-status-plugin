package org.jenkinsci.plugins.badge;

import hudson.Extension;
import hudson.model.*;
import jenkins.model.TransientActionFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.jenkinsci.plugins.badge.EmbeddableBadgeConfig;
import org.jenkinsci.plugins.badge.IconRequestHandler;

/**
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings("rawtypes")
@Extension
public class JobBadgeActionFactory extends TransientActionFactory<Job> {

    private final IconRequestHandler iconRequestHandler;

    public JobBadgeActionFactory() throws IOException {
        this.iconRequestHandler = new IconRequestHandler();
    }

    @Override
    public Class<Job> type() {
        return Job.class;
    }

    @Override
    public Collection<? extends Action> createFor(Job target) {
        return Collections.singleton(new JobBadgeAction(this,target));
    }

    public IconRequestHandler iconRequestHandler() {
        return iconRequestHandler;
    }
}
