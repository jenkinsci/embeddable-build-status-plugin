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
public class JobBadgeActionFactory extends TransientActionFactory<Job> {

    private final ImageResolver iconResolver;

    public JobBadgeActionFactory() throws IOException {
        iconResolver = new ImageResolver();
    }

    @Override
    public Class<Job> type() {
        return Job.class;
    }

    @Override
    public Collection<? extends Action> createFor(Job target) {
        return Collections.singleton(new JobBadgeAction(this,target));
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
