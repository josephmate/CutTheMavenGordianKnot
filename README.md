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
- [x] get all direct and transitive dependencies
- [ ] get all class files from jar from each transitive dependency and build a dictionary of full class path to dependency
- [ ] iterate over files
- [ ] extract imports
- [ ] calculate the number of imports from each dependency and order by count ascending
- [ ] come up with a format that is pleasant to read in build logs or terminal 

# Journal

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
- [x] get all direct and transitive dependencies
```
[INFO] project.getArtifacts():
[INFO] Depending on: org.apache.maven:maven-plugin-api:3.2.5
[INFO] Depending on: org.apache.maven:maven-artifact:3.2.5
[INFO] Depending on: org.eclipse.sisu:org.eclipse.sisu.plexus:0.3.0.M1
[INFO] Depending on: javax.enterprise:cdi-api:1.0
[INFO] Depending on: javax.annotation:jsr250-api:1.0
[INFO] Depending on: org.eclipse.sisu:org.eclipse.sisu.inject:0.3.0.M1
[INFO] Depending on: org.apache.maven:maven-model:3.2.5
[INFO] Depending on: org.codehaus.plexus:plexus-utils:3.0.20
[INFO] Depending on: org.apache.maven:maven-core:3.2.5
[INFO] Depending on: org.apache.maven:maven-settings:3.2.5
[INFO] Depending on: org.apache.maven:maven-settings-builder:3.2.5
[INFO] Depending on: org.apache.maven:maven-repository-metadata:3.2.5
[INFO] Depending on: org.apache.maven:maven-model-builder:3.2.5
[INFO] Depending on: org.apache.maven:maven-aether-provider:3.2.5
[INFO] Depending on: org.eclipse.aether:aether-spi:1.0.0.v20140518
[INFO] Depending on: org.eclipse.aether:aether-impl:1.0.0.v20140518
[INFO] Depending on: org.eclipse.aether:aether-api:1.0.0.v20140518
[INFO] Depending on: org.eclipse.aether:aether-util:1.0.0.v20140518
[INFO] Depending on: org.sonatype.sisu:sisu-guice:3.2.3
[INFO] Depending on: javax.inject:javax.inject:1
[INFO] Depending on: aopalliance:aopalliance:1.0
[INFO] Depending on: org.codehaus.plexus:plexus-interpolation:1.21
[INFO] Depending on: org.codehaus.plexus:plexus-classworlds:2.5.2
[INFO] Depending on: org.codehaus.plexus:plexus-component-annotations:1.5.5
[INFO] Depending on: org.sonatype.plexus:plexus-sec-dispatcher:1.3
[INFO] Depending on: org.sonatype.plexus:plexus-cipher:1.4
[INFO] Depending on: org.apache.maven.plugin-tools:maven-plugin-annotations:3.6.4
[INFO] Depending on: com.google.guava:guava:31.1-jre
[INFO] Depending on: com.google.guava:failureaccess:1.0.1
[INFO] Depending on: com.google.guava:listenablefuture:9999.0-empty-to-avoid-conflict-with-guava
[INFO] Depending on: com.google.code.findbugs:jsr305:3.0.2
[INFO] Depending on: org.checkerframework:checker-qual:3.12.0
[INFO] Depending on: com.google.errorprone:error_prone_annotations:2.11.0
[INFO] Depending on: com.google.j2objc:j2objc-annotations:1.3
[INFO] project.getDependencies():
[INFO] Depending on: org.apache.maven:maven-plugin-api:3.2.5
[INFO] Depending on: org.apache.maven:maven-model:3.2.5
[INFO] Depending on: org.apache.maven:maven-core:3.2.5
[INFO] Depending on: org.apache.maven.plugin-tools:maven-plugin-annotations:3.6.4
[INFO] Depending on: com.google.guava:guava:31.1-jre
```

# Annoying bugs on the way

```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-plugin-plugin:3.6.0:descriptor (default-descriptor) on project cut-the-maven-gordian-knot: Execution default-descriptor of goal org.apache.maven.plugins:maven-plugin-plugin:3.6.0:descriptor failed: begin 314, end 312, length 332 -> [Help 1]
org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal org.apache.maven.plugins:maven-plugin-plugin:3.6.0:descriptor (default-descriptor) on project cut-the-maven-gordian-knot: Execution default-descriptor of goal org.apache.maven.plugins:maven-plugin-plugin:3.6.0:descriptor failed: begin 314, end 312, length 332
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:215)
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:156)
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:148)
    at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject (LifecycleModuleBuilder.java:117)
    at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject (LifecycleModuleBuilder.java:81)
    at org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder.build (SingleThreadedBuilder.java:56)
    at org.apache.maven.lifecycle.internal.LifecycleStarter.execute (LifecycleStarter.java:128)
    at org.apache.maven.DefaultMaven.doExecute (DefaultMaven.java:305)
    at org.apache.maven.DefaultMaven.doExecute (DefaultMaven.java:192)
    at org.apache.maven.DefaultMaven.execute (DefaultMaven.java:105)
    at org.apache.maven.cli.MavenCli.execute (MavenCli.java:956)
    at org.apache.maven.cli.MavenCli.doMain (MavenCli.java:288)
    at org.apache.maven.cli.MavenCli.main (MavenCli.java:192)
    at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0 (Native Method)
    at jdk.internal.reflect.NativeMethodAccessorImpl.invoke (NativeMethodAccessorImpl.java:78)
    at jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke (DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke (Method.java:567)
    at org.codehaus.plexus.classworlds.launcher.Launcher.launchEnhanced (Launcher.java:282)
    at org.codehaus.plexus.classworlds.launcher.Launcher.launch (Launcher.java:225)
    at org.codehaus.plexus.classworlds.launcher.Launcher.mainWithExitCode (Launcher.java:406)
    at org.codehaus.plexus.classworlds.launcher.Launcher.main (Launcher.java:347)
Caused by: org.apache.maven.plugin.PluginExecutionException: Execution default-descriptor of goal org.apache.maven.plugins:maven-plugin-plugin:3.6.0:descriptor failed: begin 314, end 312, length 332
    at org.apache.maven.plugin.DefaultBuildPluginManager.executeMojo (DefaultBuildPluginManager.java:148)
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:210)
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:156)
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:148)
    at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject (LifecycleModuleBuilder.java:117)
    at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject (LifecycleModuleBuilder.java:81)
    at org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder.build (SingleThreadedBuilder.java:56)
    at org.apache.maven.lifecycle.internal.LifecycleStarter.execute (LifecycleStarter.java:128)
    at org.apache.maven.DefaultMaven.doExecute (DefaultMaven.java:305)
    at org.apache.maven.DefaultMaven.doExecute (DefaultMaven.java:192)
    at org.apache.maven.DefaultMaven.execute (DefaultMaven.java:105)
    at org.apache.maven.cli.MavenCli.execute (MavenCli.java:956)
    at org.apache.maven.cli.MavenCli.doMain (MavenCli.java:288)
    at org.apache.maven.cli.MavenCli.main (MavenCli.java:192)
    at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0 (Native Method)
    at jdk.internal.reflect.NativeMethodAccessorImpl.invoke (NativeMethodAccessorImpl.java:78)
    at jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke (DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke (Method.java:567)
    at org.codehaus.plexus.classworlds.launcher.Launcher.launchEnhanced (Launcher.java:282)
    at org.codehaus.plexus.classworlds.launcher.Launcher.launch (Launcher.java:225)
    at org.codehaus.plexus.classworlds.launcher.Launcher.mainWithExitCode (Launcher.java:406)
    at org.codehaus.plexus.classworlds.launcher.Launcher.main (Launcher.java:347)
Caused by: java.lang.StringIndexOutOfBoundsException: begin 314, end 312, length 332
    at java.lang.String.checkBoundsBeginEnd (String.java:3751)
    at java.lang.String.substring (String.java:1907)
    at org.apache.maven.tools.plugin.generator.GeneratorUtils.makeHtmlValid (GeneratorUtils.java:304)
    at org.apache.maven.tools.plugin.generator.GeneratorUtils.toText (GeneratorUtils.java:340)
    at org.apache.maven.tools.plugin.generator.PluginDescriptorGenerator.processMojoDescriptor (PluginDescriptorGenerator.java:218)
    at org.apache.maven.tools.plugin.generator.PluginDescriptorGenerator.writeDescriptor (PluginDescriptorGenerator.java:163)
    at org.apache.maven.tools.plugin.generator.PluginDescriptorGenerator.execute (PluginDescriptorGenerator.java:84)
    at org.apache.maven.plugin.plugin.AbstractGeneratorMojo.execute (AbstractGeneratorMojo.java:264)
    at org.apache.maven.plugin.plugin.DescriptorGeneratorMojo.execute (DescriptorGeneratorMojo.java:91)
    at org.apache.maven.plugin.DefaultBuildPluginManager.executeMojo (DefaultBuildPluginManager.java:137)
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:210)
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:156)
    at org.apache.maven.lifecycle.internal.MojoExecutor.execute (MojoExecutor.java:148)
    at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject (LifecycleModuleBuilder.java:117)
    at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject (LifecycleModuleBuilder.java:81)
    at org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder.build (SingleThreadedBuilder.java:56)
    at org.apache.maven.lifecycle.internal.LifecycleStarter.execute (LifecycleStarter.java:128)
    at org.apache.maven.DefaultMaven.doExecute (DefaultMaven.java:305)
    at org.apache.maven.DefaultMaven.doExecute (DefaultMaven.java:192)
    at org.apache.maven.DefaultMaven.execute (DefaultMaven.java:105)
    at org.apache.maven.cli.MavenCli.execute (MavenCli.java:956)
    at org.apache.maven.cli.MavenCli.doMain (MavenCli.java:288)
    at org.apache.maven.cli.MavenCli.main (MavenCli.java:192)
    at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0 (Native Method)
    at jdk.internal.reflect.NativeMethodAccessorImpl.invoke (NativeMethodAccessorImpl.java:78)
    at jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke (DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke (Method.java:567)
    at org.codehaus.plexus.classworlds.launcher.Launcher.launchEnhanced (Launcher.java:282)
    at org.codehaus.plexus.classworlds.launcher.Launcher.launch (Launcher.java:225)
    at org.codehaus.plexus.classworlds.launcher.Launcher.mainWithExitCode (Launcher.java:406)
    at org.codehaus.plexus.classworlds.launcher.Launcher.main (Launcher.java:347)
```
Fixed by upgrading maven-plugin-plugin to 3.7.0.