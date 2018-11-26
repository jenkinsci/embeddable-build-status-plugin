package org.jenkinsci.plugins.badge;

import hudson.Extension;
import hudson.model.*;
import jenkins.model.TransientActionFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.jenkinsci.plugins.badge.EmbeddableBadgeConfig;

/**
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings("rawtypes")
@Extension
public class JobBadgeActionFactory extends TransientActionFactory<Job> {

    private final ImageResolver iconResolver;
    private final RunParameterResolver runParameterResolver;

    public JobBadgeActionFactory() throws IOException {
        iconResolver = new ImageResolver();
        runParameterResolver = new RunParameterResolver();
    }

    @Override
    public Class<Job> type() {
        return Job.class;
    }

    @Override
    public Collection<? extends Action> createFor(Job target) {
        return Collections.singleton(new JobBadgeAction(this,target));
    }

    public String resolveParameter(Job project, String parameter) {
        return runParameterResolver.resolveParameter(project, parameter);
    }

    public EmbeddableBadgeConfig resolveConfig(Job project, String id) {
        return runParameterResolver.resolveConfig(project, id);
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
