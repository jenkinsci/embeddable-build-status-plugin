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

    private final ImageResolver iconResolver;

    public RunBadgeActionFactory() throws IOException {
        iconResolver = new ImageResolver();
    }

    @Override
    public Class<Run> type() {
        return Run.class;
    }

    @Override
    public Collection<? extends Action> createFor(Run target) {
        return Collections.singleton(new RunBadgeAction(this, target));
    }

    public StatusImage getImage(BallColor color) {
        return iconResolver.getImage(color);
    }

    public StatusImage getImage(BallColor color, String style) {
        return iconResolver.getImage(color, style);
    }
    
    public StatusImage getImage(BallColor jobColor, String style, String subject, String status, String color) {
        return iconResolver.getImage(jobColor, style, subject, status, color);
    }
}
