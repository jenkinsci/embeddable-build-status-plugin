package org.jenkinsci.plugins.badge;

import hudson.util.IOUtils;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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
 *
 * @author Kohsuke Kawaguchi
 */
class StatusImage implements HttpResponse {
    private final byte[] payload;

    /**
     * To improve the caching, compute unique ETag.
     *
     * This needs to differentiate different image types, and possible future image changes
     * in newer versions of this plugin.
     */
    private final String etag;

    private final String length;

    StatusImage(String fileName) throws IOException {
        etag = Jenkins.RESOURCE_PATH+'/'+fileName;

        URL image = new URL(
            Jenkins.getInstance().pluginManager.getPlugin("embeddable-build-status").baseResourceURL,
            "status/"+fileName);
        InputStream s = image.openStream();
        try {
            payload = IOUtils.toByteArray(s);
        } finally {
            IOUtils.closeQuietly(s);
        }
        length = Integer.toString(payload.length);
    }

    public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node) throws IOException, ServletException {
        String v = req.getHeader("If-None-Match");
        if (etag.equals(v)) {
            rsp.setStatus(SC_NOT_MODIFIED);
            return;
        }

        rsp.setHeader("ETag",etag);
        rsp.setHeader("Expires","Fri, 01 Jan 1984 00:00:00 GMT");
        rsp.setHeader("Cache-Control", "no-cache, private");
        rsp.setHeader("Content-Type", "image/png");
        rsp.setHeader("Content-Length", length);
        rsp.getOutputStream().write(payload);
    }
}
