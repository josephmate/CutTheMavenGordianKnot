# CutTheMavenGordianKnot
Sometimes it is easier to cut dependencies rather than unravel them.

# Motivation

You've grown a successful app, but neglected the weeds. They are starting to take over and strave the nutrients from your garden: CVEs, mis matched dependencies, and undetected lurking vulnerabilities. Unfortunately, the weeds aren't going anywhere because they've dug their roots deep into your app. It's time to cut some of them out. 

# Solution

Analyze and find 'weak' dependencies that would be low cost for you to replace with your own implementation.

For now 'weak' depenencies are defined as the number of times a class was imported.

# Alternatives

If you want to remove dead dependencies try [maven-dependency-analyzer](https://github.com/apache/maven-dependency-analyzer). However, the weakness I noticed with this tool is that it only says which dependencies can be removed but does not estimate the strength of the dependency or provide evidence for the dependency.
