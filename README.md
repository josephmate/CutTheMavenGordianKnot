# CutTheMavenGordianKnot
Sometimes it is easier to cut dependencies rather than unravel them.

# Motivation

You've grown a successful app, but neglected the weeds.
They are starting to take over and starve the nutrients from your garden:
CVEs, mis matched dependencies, and undetected lurking vulnerabilities.
Unfortunately, the weeds aren't going anywhere because they've dug their roots deep into your app.
It's time to cut some of them out. 

# Solution

Analyze and find 'weak' dependencies that would be low cost for you to replace with your own implementation.

For now a 'weaker' dependency is defined as the one that was imported from less.

# Alternatives

If you want to remove dead dependencies try [maven-dependency-analyzer](https://github.com/apache/maven-dependency-analyzer).
It's also a good tool to prevent your team brings in dead dependencies since it can be configured to fail your build.
However, the weakness I noticed with this tool is that it only says which dependencies can be removed but does not estimate the strength of the dependency or provide evidence for the dependency.

# TODO
- [x] figure out how to add an entry point for maven https://maven.apache.org/guides/plugin/guide-java-plugin-development.html

```
mvn com.josephmate:cut-the-maven-gordian-knot:1.0.0-SNAPSHOT:sayhi
[INFO] Scanning for projects...
[INFO]
[INFO] -------------< com.josephmate:cut-the-maven-gordian-knot >--------------
[INFO] Building Apache Maven Dependency Analyzer 1.0.0-SNAPSHOT
[INFO] ----------------------------[ maven-plugin ]----------------------------
[INFO]
[INFO] --- cut-the-maven-gordian-knot:1.0.0-SNAPSHOT:sayhi (default-cli) @ cut-the-maven-gordian-knot ---
[INFO] Hello, world.
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.241 s
[INFO] Finished at: 2022-11-09T20:52:52-05:00
[INFO] ------------------------------------------------------------------------
```

- [ ] get all class files from jar from each dependency and build a dictionary of full class path to dependency
- [ ] iterate over files and extract imports
- [ ] calculate the number of imports from each dependency and order by count ascending
- [ ] come up with a format that is pleasant to read in build logs or terminal 