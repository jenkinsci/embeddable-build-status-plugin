package org.jenkinsci.plugins.badge;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BallColor;
import hudson.model.TransientProjectActionFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Kohsuke Kawaguchi
 */
@Extension
public class BadgeActionFactory extends TransientProjectActionFactory {
    private final StatusImage[] images = new StatusImage[4];

    public BadgeActionFactory() throws IOException {
        images[0] = new StatusImage("failure.png");
        images[1] = new StatusImage("unstable.png");
        images[2] = new StatusImage("success.png");
        images[3] = new StatusImage("running.png");
    }

    @Override
    public Collection<? extends Action> createFor(AbstractProject target) {
        return Collections.singleton(new BadgeAction(this,target));
    }

    public StatusImage getImage(BallColor color) {
        switch (color.noAnime()) {
        case RED:
        case ABORTED:
            return images[0];
        case YELLOW:
            return images[1];
        case BLUE:
            return images[2];
        default:
            return images[3];
        }
    }

}
