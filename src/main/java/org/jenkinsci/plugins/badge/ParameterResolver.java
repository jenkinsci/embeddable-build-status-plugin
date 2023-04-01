/**
 * @author Thomas Doering (thomas-dee) Licensed under the MIT License. See License.txt in the
 *     project root for license information.
 */
package org.jenkinsci.plugins.badge;

import hudson.ExtensionList;
import hudson.model.Actionable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jenkinsci.plugins.badge.extensionpoints.ParameterResolverExtensionPoint;

public class ParameterResolver {
    private static Pattern parameterPattern = Pattern.compile("\\$\\{([^\\{\\}\\s]+)\\}");

    public String resolve(Actionable actionable, String parameter) {
        if (parameter != null) {
            Matcher matcher = parameterPattern.matcher(parameter);
            while (matcher.find()) {
                String resolvedMatch = null;
                for (ParameterResolverExtensionPoint resolver :
                        ExtensionList.lookup(ParameterResolverExtensionPoint.class)) {
                    String tmpResolved = resolver.resolve(actionable, matcher.group(1));
                    if (!tmpResolved.equals(matcher.group(1))) {
                        resolvedMatch = tmpResolved;
                        break;
                    }
                }
                if (resolvedMatch != null) {
                    parameter = matcher.replaceFirst(resolvedMatch
                            .replace("\\", "\\\\")
                            .replace("$", "\\$")
                            .replace("{", "\\{")
                            .replace("}", "\\}"));
                } else {
                    parameter = matcher.replaceFirst("$1");
                }
                matcher = parameterPattern.matcher(parameter);
            }
        }
        return parameter;
    }
}
