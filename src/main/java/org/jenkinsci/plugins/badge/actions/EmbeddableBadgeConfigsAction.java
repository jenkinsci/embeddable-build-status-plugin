/**
 * @author Thomas Doering
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package org.jenkinsci.plugins.badge.actions;

import hudson.model.Run;
import hudson.model.Job;
import hudson.model.Action;
import hudson.model.BuildBadgeAction;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import org.jenkinsci.plugins.badge.EmbeddableBadgeConfig;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Thomas D.
 */
@ExportedBean(defaultVisibility = 2)
public class EmbeddableBadgeConfigsAction implements Action, Serializable, BuildBadgeAction {
  private static final long serialVersionUID = 1L;
  private Map<String, EmbeddableBadgeConfig> badgeConfigs = new HashMap<String, EmbeddableBadgeConfig>();

  public EmbeddableBadgeConfigsAction() {
  }

  /* Action methods */
  public String getUrlName() {
    return "";
  }

  public String getDisplayName() {
    return "";
  }

  public String getIconFileName() {
    return null;
  }

  public static EmbeddableBadgeConfig resolve(Run<?, ?> run, String id) {
    if (id != null) {
        EmbeddableBadgeConfigsAction badgeConfigs = run.getAction(EmbeddableBadgeConfigsAction.class);
        if (badgeConfigs != null) {
            return badgeConfigs.getConfig(id);
        }
    }
    return null;
  }

  public static EmbeddableBadgeConfig resolve(Job<?, ?> job, String id) {
    return resolve(job.getLastBuild(), id);
  }

  @Exported
  public EmbeddableBadgeConfig getConfig(String id) {
    return badgeConfigs.get(id);
  }

  @Exported
  public void addConfig(EmbeddableBadgeConfig config) {
    String id = config.getID();
    if (badgeConfigs.get(id) == null) {
      badgeConfigs.put(id, config);
    }
  }
}
