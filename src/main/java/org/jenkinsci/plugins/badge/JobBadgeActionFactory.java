/**
 * @author Kohsuke Kawaguchi
 * @author Thomas Döring (thomas-dee)
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

import org.jenkinsci.plugins.badge.actions.JobBadgeAction;

@SuppressWarnings("rawtypes")
@Extension
public class JobBadgeActionFactory extends TransientActionFactory<Job> {

    public JobBadgeActionFactory() throws IOException {
    }

    @Override
    public Class<Job> type() {
        return Job.class;
    }

    @Override
    public Collection<? extends Action> createFor(Job target) {
        return Collections.singleton(new JobBadgeAction(target));
    }
}
