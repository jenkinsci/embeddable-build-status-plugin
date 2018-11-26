package org.jenkinsci.plugins.badge;

import hudson.model.Action;
import hudson.model.BuildBadgeAction;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;
import org.jenkinsci.plugins.badge.EmbeddableBadgeConfig;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
