package org.ops4j.pax.bnd.maven;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.handler.DefaultArtifactHandler;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;

@Mojo(name = "prepare")
public class BuildpathhMojo extends AbstractMojo {

    @Component
    protected MavenSession session;

    @Parameter(property = "supportedProjectTypes")
    protected List<String> supportedProjectTypes = Arrays.asList(new String[] { "jar", "bundle" });

    @Component
    protected MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Set<Artifact> artifacts = new HashSet<Artifact>();
        try {
            Workspace workspace = Utils.printInfo("Running the Pax BND Buildpath Mojo", getLog(), Utils.getWorkspace(new File(session.getExecutionRootDirectory())));
            Project workspaceProject = workspace.getProject(project.getArtifactId());
            if (project == null)
                throw new MojoExecutionException("Something is broken with your workspace. Cannot find " + project.getArtifactId() + "(from pom.xml) in BND Workspace.");

            for (Project dep : workspaceProject.getDependson()) {
                for (Container deliverable : dep.getDeliverables()) {
                    getLog().info("+ " + deliverable.getFile().getAbsolutePath());
                    artifacts.add(asArtifact(deliverable.getFile()));
                }
            }
            Collection<Container> cp = workspaceProject.getBuildpath();

            for (Container entry : cp) {
                getLog().info("+ " + entry.getFile().getAbsolutePath());
                artifacts.add(asArtifact(entry.getFile()));
            }
            workspace.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        session.getCurrentProject().setResolvedArtifacts(artifacts);
    }

    public void execute(MavenProject p) throws MojoExecutionException, MojoFailureException {
        Set<Artifact> artifacts = new HashSet<Artifact>();

        try {
            getLog().info("Running the Pax BND Buildpath Mojo");
            Workspace workspace = Utils.getWorkspace(new File(session.getExecutionRootDirectory()));
            Project workspaceProject = workspace.getProject(p.getArtifactId());
            if (project == null)
                throw new MojoExecutionException("Something is broken with your workspace. Cannot find " + p.getArtifactId() + "(from pom.xml) in BND Workspace.");

            for (Project dep : workspaceProject.getDependson()) {
                for (Container deliverable : dep.getDeliverables()) {
                    getLog().info("+ " + deliverable.getFile().getAbsolutePath());
                    artifacts.add(asArtifact(deliverable.getFile()));
                }
            }
            Collection<Container> cp = workspaceProject.getBuildpath();

            for (Container entry : cp) {
                getLog().info("+ " + entry.getFile().getAbsolutePath());
                artifacts.add(asArtifact(entry.getFile()));
            }
            workspace.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /**
         * artifacts.add(asArtifact(new File(
         * "/Users/tonit/devel/rebaze/m2e-bnd-example/test-api/target/test-api-1.0.0.jar"
         * ))); ; artifacts.add(asArtifact(new File(
         * "/Users/tonit/.m2/repository/biz/aQute/annotation/1.50.0/annotation-1.50.0.jar"
         * ))); ;
         **/
        // Set<Artifact> pomArtifacts =
        // session.getCurrentProject().getArtifacts();

        session.getCurrentProject().setResolvedArtifacts(artifacts);
    }

    private Artifact asArtifact(File element) {
        Artifact a = new DefaultArtifact("group", element.getName(), "0", "compile", "bundle", "", null);
        a.setFile(element);
        a.setArtifactHandler(newHandler(a));
        getLog().info("Added artifact: " + a.toString());
        return a;
    }

    public static ArtifactHandler newHandler(Artifact artifact) {
        String ext = "jar";
        String type = "bundle";
        DefaultArtifactHandler handler = new DefaultArtifactHandler(type);
        handler.setExtension(ext);
        handler.setLanguage("java");
        handler.setAddedToClasspath(true);
        handler.setIncludesDependencies(true);
        return handler;
    }

}
