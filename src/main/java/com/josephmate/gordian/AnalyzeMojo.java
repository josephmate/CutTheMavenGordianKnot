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
import java.util.HashSet;
import java.util.Set;
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

        Set<String> projectImports = collectProjectImports();

        printProjectImports(projectImports);

        printImportCountsPerDependency(projectImports);
    }

    private void printProjectImports(Set<String> projectImports) {
        getLog().info("Project imports:");
        for (String imp : projectImports) {
            getLog().info("  import " + imp + ";");
        }
    }

    private void printImportCountsPerDependency(Set<String> projectImports) throws MojoExecutionException {
        getLog().info("Import counts per dependency:");
        for (Artifact artifact : project.getArtifacts()) {
            String coords = artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();

            File jar = artifact.getFile();
            if (jar == null || !jar.exists()) {
                getLog().warn("  No JAR file found for " + coords);
                continue;
            }

            int[] result = countImportsFromJar(jar, projectImports);
            int importCount = result[0];
            int totalClasses = result[1];
            double importWeight = totalClasses == 0 ? 0.0 : (double) importCount / totalClasses;
            getLog().info(String.format("  %s: imports=%d totalClasses=%d importWeight=%.4f",
                    coords, importCount, totalClasses, importWeight));
        }
    }

    // returns int[]{importCount, totalOuterClasses}
    private int[] countImportsFromJar(File jar, Set<String> projectImports) throws MojoExecutionException {
        int importCount = 0;
        int totalClasses = 0;
        try (JarFile jarFile = new JarFile(jar)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                if (name.endsWith(".class") && !name.contains("$")) {
                    totalClasses++;
                    String className = name
                            .substring(0, name.length() - ".class".length())
                            .replace('/', '.');
                    if (projectImports.contains(className)) {
                        importCount++;
                    }
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read JAR: " + jar, e);
        }
        return new int[]{importCount, totalClasses};
    }

    private Set<String> collectProjectImports() throws MojoExecutionException {
        Set<String> imports = new HashSet<>();
        for (String sourceRoot : project.getCompileSourceRoots()) {
            gatherImports(Paths.get(sourceRoot), imports);
        }
        for (String sourceRoot : project.getTestCompileSourceRoots()) {
            gatherImports(Paths.get(sourceRoot), imports);
        }
        return imports;
    }

    private void gatherImports(Path sourceRoot, Set<String> imports) throws MojoExecutionException {
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
                            // match: import some.fully.qualified.ClassName;
                            // exclude static imports (import static ...)
                            if (trimmed.startsWith("import ") && !trimmed.startsWith("import static ")
                                    && trimmed.endsWith(";")) {
                                String className = trimmed
                                        .substring("import ".length(), trimmed.length() - 1);
                                imports.add(className);
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
