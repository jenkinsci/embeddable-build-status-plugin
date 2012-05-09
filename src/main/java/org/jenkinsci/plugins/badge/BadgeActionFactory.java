package org.jenkinsci.plugins.badge;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class BadgeActionFactory extends TransientProjectActionFactory {
    @Override
    public Collection<? extends Action> createFor(AbstractProject target) {
        return Collections.singleton(new BadgeAction(target));
    }

}
