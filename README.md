embeddable-build-status-plugin <sup><small>v2.0</small></sup>
==============================

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

## `subject`, `status`, `color` and `animatedOverlayColor`
The customized examples above uses the following query parameters:

`?subject=Custom Text&status=My passing text&color=pink`

All four query parameters can also access used pipeline build parameters:

`?subject=Build ${params.BRANCH_NAME}`

## `build`
The following values are supported

- Build-ID (`integer`)
- relative negative Build-Index (`0` = last, `-1` = previous, `-2` ...)
- Identifier (`last`, `lastFailed`, `lastSuccessful`, `lastUnsuccessful`, `lastStable`, `lastUnstable` or `lastCompleted`)
- Selector via BuildParameter: 
  - `last:${params.<BuildParamName>=<BuildParamValue>}` (e.g. `last:${params.BRANCH=master}`)
  - `first:${params.<BuildParamName>=<BuildParamValue>}` (e.g. `first:${params.BRANCH=master}`)
  
  Those selectors can be concatendated:
  - `last:${params.MY_PARAM=123},first:${params.BRANCH=master}`


Cou can also use any selector implemented via `RunSelectorExtensionPoint`.

## `job`
The path for the selected job **or**
any selector implemented via `JobSelectorExtensionPoint`

**Note:** This parameters is only supported for the unprotected URL `http://<jenkinsip>/buildStatus?job=<job>...`.

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

