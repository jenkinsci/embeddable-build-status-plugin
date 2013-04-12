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
    private final StatusImage[] images;

    public BadgeActionFactory() throws IOException {
        images = new StatusImage[] {
                new StatusImage("failure@2x.png"),
                new StatusImage("unstable@2x.png"),
                new StatusImage("success@2x.png"),
                new StatusImage("running@2x.png"),
                new StatusImage("unknown@2x.png")
        };
    }

    @Override
    public Collection<? extends Action> createFor(AbstractProject target) {
        return Collections.singleton(new BadgeAction(this,target));
    }

    public StatusImage getImage(BallColor color) {
        if (color.isAnimated())
            return images[3];

        switch (color) {
        case RED:
        case ABORTED:
            return images[0];
        case YELLOW:
            return images[1];
        case BLUE:
            return images[2];
        default:
            return images[4];
        }
    }

}
