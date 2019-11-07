/**
 * @author Kohsuke Kawaguchi
 * @author Thomas Doering (thomas-dee)
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package org.jenkinsci.plugins.badge;

import org.apache.commons.io.IOUtils;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import hudson.PluginWrapper;

import javax.servlet.ServletException;

import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import static javax.servlet.http.HttpServletResponse.*;
/**
 * Status image as an {@link HttpResponse}, with proper cache handling.
 *
 * <p>
 * Originally we used 302 redirects to map the status URL to a proper permanent image URL,
 * but it turns out that some browsers cache 302 redirects in violation of RFC
 * (see http://code.google.com/p/chromium/issues/detail?id=103458)
 *
 * <p>
 * So this version directly serves the image at the status URL. Since the status
 * can change any time, we use ETag to skip the actual data transfer if possible.
 */
class StatusImage implements HttpResponse {
    private final byte[] payload;
    private static final String PLGIN_NAME = "embeddable-build-status";

    private static final Jenkins jInstance = Jenkins.getInstance();
    private static final PluginWrapper plugin = (jInstance != null ? jInstance.pluginManager.getPlugin(PLGIN_NAME) : null);
    private static final URL baseUrl = (plugin != null ? plugin.baseResourceURL : null);

    /**
     * To improve the caching, compute unique ETag.
     *
     * This needs to differentiate different image types, and possible future image changes
     * in newer versions of this plugin.
     */
    private final String etag;
    private final String length;
    private String contentType = null;

    private final Map<String, String> colors = new HashMap<String, String>() {
        private static final long serialVersionUID = 1L;
        {
            put( "red", "#e05d44" );
            put( "brightgreen", "#44cc11" );
            put( "green", "#97CA00" );
            put( "yellowgreen", "#a4a61d" );
            put( "yellow", "#dfb317" );
            put( "orange", "#fe7d37" );
            put( "lightgrey", "#9f9f9f" );
            put( "blue", "#007ec6" );
        };
    };

    StatusImage() {
        etag = '"' + Jenkins.RESOURCE_PATH + '/' + "empty" + '"';
        length = Integer.toString(0);
        payload = new byte[0];
    }

    StatusImage(String fileName) throws IOException {
        URL rootUrl = new URL(jInstance != null ? jInstance.getRootUrl() : "");
        etag = '"' + fileName + '"';

        URL image = new URL(rootUrl, fileName);
        InputStream s = image.openStream();
        try {
            payload = IOUtils.toByteArray(s);
        } finally {
            IOUtils.closeQuietly(s);
        }
        length = Integer.toString(payload.length);
    }

    StatusImage(String subject, String status, String colorName, String animatedColorName, String style, String link) throws IOException {
        // escape URL parameters
        if (subject != null) subject = StringEscapeUtils.escapeHtml(subject);
        if (status != null) status = StringEscapeUtils.escapeHtml(status);
        if (animatedColorName != null) animatedColorName = StringEscapeUtils.escapeHtml(animatedColorName);
        if (colorName != null) colorName = StringEscapeUtils.escapeHtml(colorName);
        if (style != null) style = StringEscapeUtils.escapeHtml(style);
        if (link != null) link = StringEscapeUtils.escapeHtml(link);
        
        if (baseUrl != null) {
            etag = Jenkins.RESOURCE_PATH + '/' + subject + status + colorName + animatedColorName + style;
    
            if (style == null) {
                style = "flat";
            }

            URL image = new URL(baseUrl, "status/" + style + ".svg");
                    
            URL animatedSnippet = null;
            String animatedColor = null;
            
            if (animatedColorName != null) {
                animatedSnippet = new URL(baseUrl, "status/animatedOverlay.svg.snippet");
                animatedColor = colors.get(animatedColorName.toLowerCase());
                if (animatedColor == null) {
                    if (colorName.matches("-?[0-9a-fA-F]+")) {
                        animatedColor = "#" + animatedColorName;
                    } else {
                        animatedColor = animatedColorName;
                    }
                }
            }

            InputStream s = image.openStream();
        
            double[] widths = { measureText(subject) + 20, measureText(status) + 20 };

            if (animatedColor != null) {
                widths[1] += 4;
            }
            
            String color = colors.get(colorName.toLowerCase());
            if (color == null) {
                if (colorName.matches("-?[0-9a-fA-F]+")) {
                    color = "#" + colorName;
                } else {
                    color = colorName;
                }
            }
            
            String fullwidth = String.valueOf(widths[0] + widths[1]);
            String subjectWidth = String.valueOf(widths[0]);
            String statusWidth = String.valueOf(widths[1]);
            String subjectPos = String.valueOf((widths[0] / 2) + 1);
            String statusPos = String.valueOf(widths[0] + (widths[1] / 2) - 1);
            String animatedOverlay = "";
            String linkCode = "<svg xmlns";

            // first: add animated overlay
            if (animatedSnippet != null) {
                String reducedStatusWidth = String.valueOf(widths[1] - 4.0);
                InputStream animatedOverlayStream = animatedSnippet.openStream();
                try {
                    animatedOverlay = IOUtils.toString(animatedOverlayStream, "utf-8")
                        .replace("{{reducedStatusWidth}}", reducedStatusWidth)
                        .replace("{{animatedColor}}", animatedColor);
                } finally {
                    IOUtils.closeQuietly(animatedOverlayStream);
                }
            }

            if (link != null) {
                linkCode = "<svg onclick=\"window.open('" + link + "');\" style=\"cursor: pointer;\" xmlns";
            }

            try {
                payload = IOUtils.toString(s, "utf-8")
                        .replace("{{animatedOverlayColor}}", animatedOverlay)
                        .replace("{{fullwidth}}", fullwidth)
                        .replace("{{subjectWidth}}", subjectWidth)
                        .replace("{{statusWidth}}", statusWidth)
                        .replace("{{subjectPos}}", subjectPos)
                        .replace("{{statusPos}}", statusPos)
                        .replace("{{subject}}", subject)
                        .replace("{{status}}", status)
                        .replace("{{color}}", color)
                        .replace("<svg xmlns", linkCode).getBytes(Charset.forName("UTF-8"));
            } finally {
                IOUtils.closeQuietly(s);
            }

            length = Integer.toString(payload.length);
            contentType = "image/svg+xml;charset=utf-8";
        } else {
            etag = '"' + Jenkins.RESOURCE_PATH + '/' + "empty" + '"';
            length = Integer.toString(0);
            payload = new byte[0];    
        }
    }

    public int measureText(String text) throws IOException {
        if (baseUrl != null) {
            URL fontURL = new URL(baseUrl, "fonts/verdana.ttf");
            InputStream fontStream = fontURL.openStream();
            Font defaultFont = null;
            try {
                defaultFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            } catch (FontFormatException e) {
                throw new IOException(e.getMessage());
            } finally {
                IOUtils.closeQuietly(fontStream);
            }

            defaultFont = defaultFont.deriveFont(11f);
            Canvas canvas = new Canvas();
            FontMetrics fontMetrics = canvas.getFontMetrics(defaultFont);
            return fontMetrics.stringWidth(text);
        }
        return 0;
    }

    public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
        String v = req.getHeader("If-None-Match");
        if (etag.equals(v)) {
            rsp.setStatus(SC_NOT_MODIFIED);
            return;
        }

        rsp.setHeader("ETag",etag);
        rsp.setHeader("Expires","Fri, 01 Jan 1984 00:00:00 GMT");
        rsp.setHeader("Cache-Control", "no-cache, no-store, private");
        if (contentType != null) {
            rsp.setHeader("Content-Type", contentType);
        }
        rsp.setHeader("Content-Length", length);
        rsp.getOutputStream().write(payload);
    }
}
