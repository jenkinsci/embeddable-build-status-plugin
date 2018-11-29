embeddable-build-status-plugin
==============================

This plugin allows to add customizable [shields.io](https://shields.io) like badges to any website.

Customization can be done via query parameters.

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

## `subject`, `status` and `color`
The customized examples above uses the following query parameters:

`?subject=Custom Text&status=My passing text&color=pink`

All three query parameters can also access used pipeline build parameters:

`?subject=Build ${params.BRANCH_NAME}`

## `config`
You can add pre-customized badge configurations via pipeline script.

### `addEmbeddableBadgeConfiguration`
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
 */
addEmbeddableBadgeConfiguration(id: <string>, subject: <string>, status: <string>, color: <string>)
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

