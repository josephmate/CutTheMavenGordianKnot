package com.josephmate.gordian;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

        printProjectImports();

        for (Artifact artifact : project.getArtifacts()) {
            String coords = artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();
            getLog().info("Artifact: " + coords);

            File jar = artifact.getFile();
            if (jar == null || !jar.exists()) {
                getLog().warn("  No JAR file found for " + coords);
                continue;
            }

            try (JarFile jarFile = new JarFile(jar)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    // Only top-level classes: ends with .class, no $ (inner/anonymous)
                    if (name.endsWith(".class") && !name.contains("$")) {
                        String className = name
                                .substring(0, name.length() - ".class".length())
                                .replace('/', '.');
                        getLog().info("  Class: " + className);
                    }
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to read JAR: " + jar, e);
            }
        }
    }

    private void printProjectImports() throws MojoExecutionException {
        getLog().info("Project imports:");
        for (String sourceRoot : project.getCompileSourceRoots()) {
            collectImports(Paths.get(sourceRoot));
        }
        for (String sourceRoot : project.getTestCompileSourceRoots()) {
            collectImports(Paths.get(sourceRoot));
        }
    }

    private void collectImports(Path sourceRoot) throws MojoExecutionException {
        if (!sourceRoot.toFile().exists()) {
            return;
        }
        try {
            Files.walk(sourceRoot)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(javaFile -> {
                    try (BufferedReader reader = new BufferedReader(new FileReader(javaFile.toFile()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            String trimmed = line.trim();
                            if (trimmed.startsWith("import ")) {
                                getLog().info("  " + javaFile + ": " + trimmed);
                            }
                        }
                    } catch (IOException e) {
                        getLog().warn("Could not read " + javaFile + ": " + e.getMessage());
                    }
                });
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to walk source root: " + sourceRoot, e);
        }
    }
}
