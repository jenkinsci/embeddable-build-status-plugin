package org.jenkinsci.plugins.badge;

import hudson.Extension;
import hudson.model.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Extension
public class RunBadgeActionFactory extends TransientBuildActionFactory {

    private final ImageResolver iconResolver;

    public RunBadgeActionFactory() throws IOException {
        iconResolver = new ImageResolver();
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
}
