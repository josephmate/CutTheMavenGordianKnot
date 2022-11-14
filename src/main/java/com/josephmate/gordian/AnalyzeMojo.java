package com.josephmate.gordian;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/***
 * <pre>
 * mvn com.josephmate:cut-the-maven-gordian-knot:1.0.0-SNAPSHOT:analyze
 * </pre>
 */
@Mojo(name = "analyze", requiresDependencyResolution = ResolutionScope.TEST)
@Execute(phase = LifecyclePhase.TEST_COMPILE)
public class AnalyzeMojo extends AbstractMojo {

    /**
     * The Maven project to analyze.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;

    public void execute() throws MojoExecutionException {

        getLog().info("Analyzing: " + project.getGroupId() + ":" + project.getArtifactId() + ":" + project.getVersion());

        getLog().info("project.getArtifacts():");
        for(Artifact artifact : project.getArtifacts()) {
            getLog().info("Depending on: " + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion());
        }
        getLog().info("project.getDependencies():");
        for(Dependency dependency : project.getDependencies()) {
            getLog().info("Depending on: " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion());
        }
    }
}
