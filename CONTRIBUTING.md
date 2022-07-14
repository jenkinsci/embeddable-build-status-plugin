# Contributing to the Embeddable Build Status Plugin

Plugin source code is hosted on [GitHub](https://github.com/jenkinsci/embeddable-build-status-plugin).
New feature proposals and bug fix proposals should be submitted as
[GitHub pull requests](https://help.github.com/articles/creating-a-pull-request).
Your pull request will be evaluated by the [Jenkins job](https://ci.jenkins.io/job/Plugins/job/embeddable-build-status-plugin/).

Before submitting your change, please assure that you've added tests which verify your change.

## Code Coverage

[JaCoCo code coverage](https://www.jacoco.org/jacoco/) reporting is available as a maven target and can be displayed by the [Jenkins warnings next generation plugin](https://plugins.jenkins.io/warnings-ng/).
Please try to improve code coverage with tests when you submit.
* `mvn -P enable-jacoco clean install jacoco:report` to report code coverage with JaCoCo.

Please don't introduce new spotbugs output.
* `mvn spotbugs:check` to analyze project using [Spotbugs](https://spotbugs.github.io)
* `mvn spotbugs:gui` to review report using GUI

## Maintaining automated tests

Automated tests are run as part of the `verify` phase.
Automated tests in the `continuous-integration` profile are run with multiple Java virtual machines, depending on the number of available processor cores.
Run automated tests with multiple Java virtual machines in a development with the command:

```
$ mvn clean -DforkCount=1C verify
```

## Report an Issue

Use the ["Report an issue" page](https://www.jenkins.io/participate/report-issue/redirect/#17120) to submit bug reports.

## Security Issues

Follow the [Jenkins project vulnerability reporting instructions](https://jenkins.io/security/reporting/) to report vulnerabilities.
