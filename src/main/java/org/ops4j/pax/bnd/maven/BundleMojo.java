package org.ops4j.pax.bnd.maven;

import static org.ops4j.pax.bnd.maven.Utils.getWorkspace;
import static org.ops4j.pax.bnd.maven.Utils.printInfo;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;

import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;

@Mojo(name = "bundle")
public class BundleMojo extends AbstractMojo {

    @Component
    protected MavenSession session;

    @Parameter(property = "supportedProjectTypes")
    protected List<String> supportedProjectTypes = Arrays.asList(new String[] { "jar", "bundle" });

    @Component
    protected MavenProject project;

    @Component
    private MavenProjectHelper m_projectHelper;

    @Component
    private ArtifactHandlerManager m_artifactHandlerManager;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File[] files = null;

        try {
            Workspace workspace = printInfo("Building using BND Build..", getLog(), getWorkspace(new File(session.getExecutionRootDirectory())));
            Project workspaceProject = workspace.getProject(project.getArtifactId());
            if (project == null)
                throw new MojoExecutionException("Something is broken with your workspace. Cannot find " + project.getArtifactId() + "(from pom.xml) in BND Workspace.");

            files = workspaceProject.build();
            Utils.printInfo("Build done: " + workspaceProject.getName(), getLog(), workspace);
            workspace.close();
        } catch (Exception e) {
            throw new MojoExecutionException("Problem building the project.", e);
        }

        // attach bundle to maven project
        File jarFile = files[0];

        Artifact mainArtifact = project.getArtifact();

        if ("bundle".equals(mainArtifact.getType())) {
            // workaround for MNG-1682: force maven to install artifact using
            // the "jar" handler
            mainArtifact.setArtifactHandler(m_artifactHandlerManager.getArtifactHandler("jar"));
        }

        mainArtifact.setFile(jarFile);

    }

}
