/**
 * @author Kohsuke Kawaguchi
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.*;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import jenkins.model.TransientActionFactory;
import org.jenkinsci.plugins.badge.actions.RunBadgeAction;

@SuppressWarnings("rawtypes")
@Extension
public class RunBadgeActionFactory extends TransientActionFactory<Run> {

    public RunBadgeActionFactory() throws IOException {}

    @Override
    public Class<Run> type() {
        return Run.class;
    }

    @NonNull
    @Override
    public Collection<? extends Action> createFor(@NonNull Run target) {
        return Collections.singleton(new RunBadgeAction(target));
    }
}
