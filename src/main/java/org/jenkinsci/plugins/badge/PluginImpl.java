/**
 * @author Dominik Bartholdi (imod)
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge;

import hudson.Plugin;

public class PluginImpl extends Plugin {
    public static final IconRequestHandler iconRequestHandler = new IconRequestHandler();

    @Override
    public void start() throws Exception {}
}
