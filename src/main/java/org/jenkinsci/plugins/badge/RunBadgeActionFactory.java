/**
 * @author Kohsuke Kawaguchi
 * @author Thomas Doering (thomas-dee)
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package org.jenkinsci.plugins.badge;

import hudson.Extension;
import hudson.model.*;
import jenkins.model.TransientActionFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import org.jenkinsci.plugins.badge.actions.RunBadgeAction;

@SuppressWarnings("rawtypes")
@Extension
public class RunBadgeActionFactory extends TransientActionFactory<Run> {

    public RunBadgeActionFactory() throws IOException {
    }

    @Override
    public Class<Run> type() {
        return Run.class;
    }

    @Override
    public Collection<? extends Action> createFor(Run target) {
        return Collections.singleton(new RunBadgeAction(target));
    }
}
