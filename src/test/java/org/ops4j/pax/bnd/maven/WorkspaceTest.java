package org.ops4j.pax.bnd.maven;

import static org.ops4j.pax.bnd.maven.Utils.getWorkspace;
import static org.ops4j.pax.bnd.maven.Utils.printInfo;

import java.io.File;
import java.util.Collection;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.build.Workspace;

public class WorkspaceTest {
    // @Test
    public void testFoo() {
        try {
            File baseDir = new File("/Users/tonit/devel/rebaze/workspaceBNDBridge");
            System.out.println("Workspace is" + baseDir.getAbsolutePath());

            Workspace workspace = new Workspace(baseDir);
            for (Project project : workspace.getAllProjects()) {
                System.out.println("Project found: " + project.getName());

                for (Container container : project.getDeliverables()) {
                    System.out.println(" + Deliverale " + container.getFile().getAbsolutePath());
                }

                Collection<Container> cp = project.getBuildpath();
                for (Container entry : cp) {
                    System.out.println("+ " + entry.getFile().getAbsolutePath());
                }

                for (Container f : project.getClasspath()) {
                    System.out.println("------> build file: " + f.getFile().getAbsolutePath());
                }
                for (Project dep : project.getDependson()) {
                    System.out.println("Dep: " + dep.getName() + " --> " + dep.getOutput().getAbsolutePath());

                }
            }

            workspace.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //@Test
    public void testCreate() throws Exception {
        Log log = new SystemStreamLog();
        Workspace workspace = printInfo("Building using BND Build..", log, getWorkspace(new File("/Users/tonit/devel/rebaze/workspaceBNDBridge")));
        Project workspaceProject = workspace.getProject("test-impl");
        log.info("Project nobundle    : " + workspaceProject.isNoBundles());

        File[] files = workspaceProject.build();
        if (files != null) {
            for (File f : files) {
                log.info("OUT " + f.getAbsolutePath());
            }
        }else{
            for (String err : workspaceProject.getErrors()) {
                log.error(err);
            }
            throw new RuntimeException("No output from build!");
        }
        Utils.printInfo("Build done: " + workspaceProject.getName(), log, workspace);
       
        workspace.close();
    }
}
