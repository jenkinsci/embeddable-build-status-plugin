/**
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.jenkinsci.plugins.scriptsecurity.sandbox.whitelists.Whitelisted;

public class EmbeddableBadgeConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, String> colors = new HashMap<>() {
        @Serial
        private static final long serialVersionUID = 1L;

        {
            put("failing", "red");
            put("passing", "brightgreen");
            put("unstable", "yellow");
            put("aborted", "aborted");
            put("running", "blue");
        }
    };

    private final String id;
    private String subject = null;
    private String status = null;
    private String color = null;
    private String animatedOverlayColor = null;
    private String link = null;

    public EmbeddableBadgeConfig(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    @Whitelisted
    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStatus() {
        return status;
    }

    @Whitelisted
    public void setStatus(String status) {
        this.status = status;
    }

    public String getColor() {
        if (color == null) {
            return colors.get(status);
        }
        return color;
    }

    @Whitelisted
    public void setColor(String color) {
        this.color = color;
    }

    public String getAnimatedOverlayColor() {
        if (this.animatedOverlayColor == null && this.color == null) {
            if (this.status != null && this.status.equals("running")) {
                return "blue";
            }
        }
        return this.animatedOverlayColor;
    }

    @Whitelisted
    public void setAnimatedOverlayColor(String animatedOverlayColor) {
        this.animatedOverlayColor = animatedOverlayColor;
    }

    public String getLink() {
        return link;
    }

    @Whitelisted
    public void setLink(String link) {
        this.link = link;
    }
}
