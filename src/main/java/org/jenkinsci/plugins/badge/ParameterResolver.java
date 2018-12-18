package org.jenkinsci.plugins.badge;

import hudson.model.Actionable;
import hudson.ExtensionList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.util.logging.Logger;

import org.jenkinsci.plugins.badge.extensionpoints.ParameterResolverExtensionPoint;

/**
 * @author Thomas D.
 */
public class ParameterResolver {
    private static Pattern parameterPattern = Pattern.compile("\\$\\{([^\\{\\}\\s]+)\\}");
    //private static Logger LOG = Logger.getLogger("org.jenkinsci.plugins.badge.ParameterResolver");
    public String resolve(Actionable actionable, String parameter) {
        if (parameter != null) {
            Matcher matcher = parameterPattern.matcher(parameter);
            //LOG.info(parameter);
            while (matcher.find()) {
                String resolvedMatch = null;
                for (ParameterResolverExtensionPoint resolver : ExtensionList.lookup(ParameterResolverExtensionPoint.class)) {
                    String tmpResolved = resolver.resolve(actionable, matcher.group(1));
                    if (!tmpResolved.equals(matcher.group(1))) {
                        resolvedMatch = tmpResolved;
                        break;
                    }
                }
                if (resolvedMatch != null) {
                    parameter = matcher.replaceAll(resolvedMatch);
                } else {
                    parameter = matcher.replaceAll("$1");
                }
                //LOG.info(parameter + "(" + resolvedMatch + ")");
                matcher = parameterPattern.matcher(parameter);
            }
        }
        return parameter;
    }
}
