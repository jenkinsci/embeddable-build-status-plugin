/**
 * @author Kohsuke Kawaguchi
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge;

import static jakarta.servlet.http.HttpServletResponse.*;

import hudson.PluginWrapper;
import jakarta.servlet.ServletException;
import java.awt.Canvas;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.Jenkins;
import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest2;
import org.kohsuke.stapler.StaplerResponse2;

/**
 * Status image as an {@link HttpResponse}, with proper cache handling.
 *
 * <p>Originally we used 302 redirects to map the status URL to a proper permanent image URL, but it
 * turns out that some browsers cache 302 redirects in violation of RFC (see
 * http://code.google.com/p/chromium/issues/detail?id=103458)
 *
 * <p>So this version directly serves the image at the status URL. Since the status can change any
 * time, we use ETag to skip the actual data transfer if possible.
 */
class StatusImage implements HttpResponse {
    public static final Logger LOGGER = Logger.getLogger(StatusImage.class.getName());
    private final byte[] payload;
    private static final String PLGIN_NAME = "embeddable-build-status";

    private static final Jenkins jInstance = Jenkins.get();
    private static final PluginWrapper plugin = jInstance.pluginManager.getPlugin(PLGIN_NAME);
    private static final URL baseUrl = plugin != null ? plugin.baseResourceURL : null;

    /**
     * To improve the caching, compute unique ETag.
     *
     * <p>This needs to differentiate different image types, and possible future image changes in
     * newer versions of this plugin.
     */
    private final String etag;

    private final String length;
    private String contentType;

    private static final Map<String, String> COLORS = Map.of(
            "red", "#e05d44",
            "brightgreen", "#44cc11",
            "green", "#97CA00",
            "yellowgreen", "#a4a61d",
            "yellow", "#dfb317",
            "orange", "#fe7d37",
            "lightgrey", "#9f9f9f",
            "blue", "#007ec6");

    StatusImage() {
        etag = '"' + Jenkins.RESOURCE_PATH + '/' + "empty" + '"';
        length = Integer.toString(0);
        payload = new byte[0];
    }

    StatusImage(String fileName) throws IOException {
        URL rootUrl = new URL(jInstance.getRootUrl());
        etag = '"' + fileName + '"';

        URL image = new URL(rootUrl, fileName);
        try (InputStream s = image.openStream()) {
            payload = IOUtils.toByteArray(s);
        }
        length = Integer.toString(payload.length);
    }

    StatusImage(String subject, String status, String colorName, String animatedColorName, String style, String link)
            throws IOException {
        // escape URL parameters
        if (subject != null) {
            subject = StringEscapeUtils.escapeHtml4(subject);
        }
        if (status != null) {
            status = StringEscapeUtils.escapeHtml4(status);
        }
        if (animatedColorName != null) {
            animatedColorName = StringEscapeUtils.escapeHtml4(animatedColorName);
        }
        if (colorName != null) {
            colorName = StringEscapeUtils.escapeHtml4(colorName);
        }
        if (link != null) {
            // double-escape because concatenating into an attribute effectively removes one level of quoting
            link = StringEscapeUtils.escapeHtml4(StringEscapeUtils.escapeHtml4(link));
        }

        if (baseUrl != null) {
            etag = Jenkins.RESOURCE_PATH + '/' + subject + status + colorName + animatedColorName + style;

            if (style == null
                    || !Arrays.asList("flat-square", "plastic")
                            .contains(style)) { // explicitly list allowed values for SECURITY-2792
                style = "flat";
            }

            URL image = new URL(baseUrl, "status/" + style + ".svg");

            URL animatedSnippet = null;
            String animatedColor = null;

            if (animatedColorName != null) {
                animatedSnippet = new URL(baseUrl, "status/animatedOverlay.svg.snippet");
                animatedColor = COLORS.get(animatedColorName.toLowerCase());
                if (animatedColor == null) {
                    if (colorName.matches("-?[0-9a-fA-F]+")) {
                        animatedColor = "#" + animatedColorName;
                    } else {
                        animatedColor = animatedColorName;
                    }
                }
            }

            double[] widths = {measureText(subject) + 20, measureText(status) + 20};

            if (animatedColor != null) {
                widths[1] += 4;
            }

            String color = COLORS.get(colorName.toLowerCase());
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
                try (InputStream animatedOverlayStream = animatedSnippet.openStream()) {
                    animatedOverlay = IOUtils.toString(animatedOverlayStream, StandardCharsets.UTF_8)
                            .replace("{{reducedStatusWidth}}", reducedStatusWidth)
                            .replace("{{animatedColor}}", animatedColor);
                }
            }

            if (link != null) {
                try {
                    URL url = new URL(link);
                    final String protocol = url.getProtocol();
                    if ("http".equals(protocol) || "https".equals(protocol)) {
                        linkCode = "<svg onclick=\"window.open(&quot;"
                                + link
                                + "&quot;);\" style=\"cursor: pointer;\" xmlns";
                    } else {
                        LOGGER.log(Level.FINE, "Invalid link protocol: {0}", protocol);
                    }
                } catch (MalformedURLException ex) {
                    LOGGER.log(Level.FINE, "Invalid link URL: " + link, ex);
                }
            }

            try (InputStream s = image.openStream()) {
                payload = IOUtils.toString(s, StandardCharsets.UTF_8)
                        .replace("{{animatedOverlayColor}}", animatedOverlay)
                        .replace("{{fullwidth}}", fullwidth)
                        .replace("{{subjectWidth}}", subjectWidth)
                        .replace("{{statusWidth}}", statusWidth)
                        .replace("{{subjectPos}}", subjectPos)
                        .replace("{{statusPos}}", statusPos)
                        .replace("{{subject}}", subject)
                        .replace("{{status}}", status)
                        .replace("{{color}}", color)
                        .replace("<svg xmlns", linkCode)
                        .getBytes(StandardCharsets.UTF_8);
            }

            length = Integer.toString(payload.length);
            contentType = "image/svg+xml;charset=utf-8";
        } else {
            etag = '"' + Jenkins.RESOURCE_PATH + '/' + "empty" + '"';
            length = Integer.toString(0);
            payload = new byte[0];
        }
    }

    private static final FontMetrics DEFAULT_FONT_METRICS;

    static {
        Font defaultFont = null;
        final String FONT_NAME = "fonts/Bitstream-Vera-Sans-Roman.ttf";
        try {
            URL fontURL = new URL(baseUrl, FONT_NAME);
            try (InputStream fontStream = fontURL.openStream()) {
                defaultFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                defaultFont = defaultFont.deriveFont(11f);
            } catch (FontFormatException ex) {
                Logger.getLogger(StatusImage.class.getName())
                        .log(Level.SEVERE, "Font format exception " + FONT_NAME, ex);
            } catch (IOException ex) {
                Logger.getLogger(StatusImage.class.getName()).log(Level.SEVERE, "IOException reading " + FONT_NAME, ex);
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(StatusImage.class.getName())
                    .log(Level.SEVERE, "Malformed URL on static font " + FONT_NAME, ex);
        }
        Canvas canvas = new Canvas();
        DEFAULT_FONT_METRICS = canvas.getFontMetrics(defaultFont);
    }

    public int measureText(String text) throws IOException {
        return baseUrl != null ? DEFAULT_FONT_METRICS.stringWidth(text) : 0;
    }

    @Override
    public void generateResponse(StaplerRequest2 req, StaplerResponse2 rsp, Object node)
            throws IOException, ServletException {
        String v = req.getHeader("If-None-Match");
        if (etag.equals(v)) {
            rsp.setStatus(SC_NOT_MODIFIED);
            return;
        }

        rsp.setHeader("ETag", etag);
        rsp.setHeader("Expires", "Fri, 01 Jan 1984 00:00:00 GMT");
        rsp.setHeader("Cache-Control", "no-cache, no-store, private");
        if (contentType != null) {
            rsp.setHeader("Content-Type", contentType);
        }
        rsp.setHeader("Content-Length", length);
        rsp.getOutputStream().write(payload);
    }

    /* Package protected getters for tests */
    String getEtag() {
        return etag;
    }

    /* Package protected getters for tests */
    String getLength() {
        return length;
    }

    /* Package protected getters for tests */
    String getContentType() {
        return contentType;
    }
}
