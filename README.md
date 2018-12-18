embeddable-build-status-plugin
==============================
Version: **v2.0**<sup><small> (unreleased)</small></sup>

---

This plugin allows to add customizable [shields.io](https://shields.io) like badges to any website.

Customization can be done via query parameters.

# Query Parameters
## `style`
There three basic types supported:
### *plastic*
![Badge](src/doc/plastic_unconfigured.svg "Badge") (default)

![Customized Badge](src/doc/plastic_configured.svg "Customized Badge") (customized)

### *flat* (default)
![Badge](src/doc/flat_unconfigured.svg "Badge") (default)

![Customized Badge](src/doc/flat_configured.svg "Customized Badge") (customized)

### *flat-square*
![Badge](src/doc/flat-square_unconfigured.svg "Badge") (default)

![Customized Badge](src/doc/flat-square_configured.svg "Customized Badge") (customized)

## `config`
You can add pre-customized badge configurations via pipeline script (see **"DSL"** below).

## `subject` and `status`
The customized examples above uses the following query parameters:

`?subject=Custom Text&status=My passing text`

All four query parameters can also use variables like `?subject=Build ${variable}`

Available builtin variables are:
 - `buildId`, `buildNumber`, `displayName`, `duration`, and `runningTime`
 - `params.<BuildParameterName>` where `<BuildParameterName>` matches any Parameter used for running the job.

   **Note:** If the build parameter is not set you can use the following syntax to use a fallback value:
   `params.<BuildParameterName>|<FallbackValue>`
 
Example: `?subject=Build ${params.BUILD_BRANCH|master} (${displayName})`

##### *ExtensionPoint*
This plugin provides a `ParameterResolverExtensionPoint` which allow for custom `${<Parameter>}` resolver implementations.

## `color` and `animatedOverlayColor`

You can override the color using the following valid color values:
- one of the values: `red`, `brightgreen`, `green`, `yellowgreen`, `yellow`, `orange`, `lightgrey`, `blue`
- a valid hexadecimal HTML RGB color <b>without</b> the hashtag (e.g. `FFAABB`).
- any valid [SVG color name](https://www.december.com/html/spec/colorsvg.html)

## `build`
Select the build. 

### *Selectors*
Allowed selectors are:

- Build-ID (`integer`)
- relative negative Build-Index (`0` = last, `-1` = previous, `-2` ...)
- Selector via the following Rule:

  `(last|first)[Failed|Successful|Unsuccessful|Stable|Unstable|Completed][:${params.<BuildParamerName>=<BuildParameterValue>}]`

  - `(...)` is required
  - `[...]` is optional

  Examples:
  - `last`
  - `first`
  - `lastStable`
  - `firstCompleted`
  - `lastSuccessful:${params.BRANCH=master}`
  
##### *ExtensionPoint*
This plugin provides a `RunSelectorExtensionPoint` which allow for custom run selector implementations.

### *Concatenation*

All those selectors can be concatendated as comma separated list:

`build=last,-10,firstSuccessful:${params.BRANCH=master}`

This searches in the last `10` runs for the first successful build of the `master` branch (provided the Build Parameter `BRANCH` exists).

## `job`
**Note: This parameters is only supported for the unprotected URL!** 

The path for the selected job **or**
any selector implemented via `JobSelectorExtensionPoint`

##### *ExtensionPoint* 
This plugin provides a `JobSelectorExtensionPoint` which allow for custom job selector implementations.
`http://<jenkinsip>/buildStatus?job=<job>...`.

# DSL 

```groovy
/**
 * Adds a badge configuration with the given id.
 * minimal params
 * 
 * id: A unique id for the configuration
 */
addEmbeddableBadgeConfiguration(id: <id>)

/**
 * all params
 * 
 * id: A unique id for the configuration
 * subject: A subject text
 * status: A status text
 * color: A valid color (RGB-HEX: RRGGBB or valid SVG color name)
 * animatedOverlayColor: A valid color (RGB-HEX: RRGGBB or valid SVG color name)
 */
addEmbeddableBadgeConfiguration(id: <string>, subject: <string>, status: <string>, color: <string>, animatedOverlayColor: <string>)
```

This function returns a configuration object.

#### Example
```groovy
def win32BuildBadge = addEmbeddableBadgeConfiguration(id: "win32build", subject: "Windows Build")

pipeline {
    agent any
    stages {
        steps {
            script {
                win32BuildBadge.setStatus('running')
                try {
                    RunBuild()
                    win32BuildBadge.setStatus('passing')
                } catch (Exception err) {
                    win32BuildBadge.setStatus('failing')

                    /* Note: If you do not set the color
                             the configuration uses the best status-matching color.
                             passing -> brightgreen
                             failing -> red 
                             ...
                    */
                    win32BuildBadge.setColor('pink')

                    error 'Build failed'
                }
            }
        }
    }
}
```

You can use the `config` query parameter to reference the `win32build` id:

`?config=win32build`

![Passing](src/doc/config_example_1.svg "Passing")
![Failing](src/doc/config_example_2.svg "Failing")


See https://wiki.jenkins-ci.org/display/JENKINS/Embeddable+Build+Status+Plugin

