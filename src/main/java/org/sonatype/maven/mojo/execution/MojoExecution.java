package org.sonatype.maven.mojo.execution;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginContainer;
import org.apache.maven.project.MavenProject;

public class MojoExecution
{
    /**
     * Returns true if the current project is located at the Execution Root Directory (from where mvn was launched).
     * 
     * @param mavenSession the MavenSession
     * @param basedir the basedir
     * @return true if execution root equals to the passed in basedir.
     */
    public static boolean isThisTheExecutionRoot( final MavenSession mavenSession, final File basedir )
    {
        return isThisTheExecutionRoot( mavenSession, basedir.getAbsolutePath() );
    }

    /**
     * Returns true if the current project is located at the Execution Root Directory (from where mvn was launched).
     * 
     * @param mavenSession the MavenSession
     * @param basedir the basedir
     * @return true if execution root equals to the passed in basedir.
     */
    public static boolean isThisTheExecutionRoot( final MavenSession mavenSession, final String basedir )
    {
        return mavenSession.getExecutionRootDirectory().equalsIgnoreCase( basedir );
    }

    /**
     * Returns true if the current project is the last one being executed in this build.
     * 
     * @param mavenSession the MavenSession
     * @return true if last project is being built.
     */
    public static boolean isCurrentTheLastProjectInExecution( final MavenSession mavenSession )
    {
        final MavenProject currentProject = mavenSession.getCurrentProject();
        final MavenProject lastProject =
            mavenSession.getSortedProjects().get( mavenSession.getSortedProjects().size() - 1 );

        return currentProject == lastProject;
    }

    /**
     * Returns true if the current project is the first one being executed in this build that has this Mojo defined.
     * 
     * @param mavenSession the MavenSession.
     * @param pluginGroupId the plugin's groupId.
     * @param pluginArtifactId the plugin's artifactId.
     * @return true if last project with given plugin is being built.
     */
    public static boolean isCurrentTheFirstProjectWithMojoInExecution( final MavenSession mavenSession,
                                                                       final String pluginGroupId,
                                                                       final String pluginArtifactId )
    {
        return mavenSession.getCurrentProject() == getFirstProjectWithMojoInExecution( mavenSession, pluginGroupId,
            pluginArtifactId );
    }

    /**
     * Returns true if the current project is the last one being executed in this build that has this Mojo defined.
     * 
     * @param mavenSession the MavenSession.
     * @param pluginGroupId the plugin's groupId.
     * @param pluginArtifactId the plugin's artifactId.
     * @return true if last project with given plugin is being built.
     */
    public static boolean isCurrentTheLastProjectWithMojoInExecution( final MavenSession mavenSession,
                                                                      final String pluginGroupId,
                                                                      final String pluginArtifactId )
    {
        return mavenSession.getCurrentProject() == getLastProjectWithMojoInExecution( mavenSession, pluginGroupId,
            pluginArtifactId );
    }

    /**
     * Returns first MavenProject from projects being built that has this Mojo defined.
     * 
     * @param mavenSession the MavenSession.
     * @param pluginGroupId the plugin's groupId.
     * @param pluginArtifactId the plugin's artifactId.
     * @return MavenProject of first project with given plugin is being built or null.
     */
    public static MavenProject getFirstProjectWithMojoInExecution( final MavenSession mavenSession,
                                                                   final String pluginGroupId,
                                                                   final String pluginArtifactId )
    {
        final ArrayList<MavenProject> projects = new ArrayList<MavenProject>( mavenSession.getSortedProjects() );
        MavenProject firstWithThisMojo = null;
        for ( MavenProject project : projects )
        {
            if ( null != findPlugin( project.getBuild(), pluginGroupId, pluginArtifactId ) )
            {
                firstWithThisMojo = project;
                break;
            }
        }
        return firstWithThisMojo;
    }

    /**
     * Returns last MavenProject from projects being built that has this Mojo defined.
     * 
     * @param mavenSession the MavenSession.
     * @param pluginGroupId the plugin's groupId.
     * @param pluginArtifactId the plugin's artifactId.
     * @return MavenProject of last project with given plugin is being built or null.
     */
    public static MavenProject getLastProjectWithMojoInExecution( final MavenSession mavenSession,
                                                                  final String pluginGroupId,
                                                                  final String pluginArtifactId )
    {
        final ArrayList<MavenProject> projects = new ArrayList<MavenProject>( mavenSession.getSortedProjects() );
        Collections.reverse( projects );
        MavenProject lastWithThisMojo = null;
        for ( MavenProject project : projects )
        {
            if ( null != findPlugin( project.getBuild(), pluginGroupId, pluginArtifactId ) )
            {
                lastWithThisMojo = project;
                break;
            }
        }
        return lastWithThisMojo;
    }

    /**
     * Searches for plugin in passed in PluginContainer.
     * 
     * @param container
     * @param pluginGroupId
     * @param pluginArtifactId
     * @return the plugin or null if not found.
     */
    public static Plugin findPlugin( final PluginContainer container, final String pluginGroupId,
                                     final String pluginArtifactId )
    {
        if ( container != null )
        {
            for ( Plugin plugin : container.getPlugins() )
            {
                if ( pluginGroupId.equals( plugin.getGroupId() ) && pluginArtifactId.equals( plugin.getArtifactId() ) )
                {
                    return plugin;
                }
            }
        }
        return null;
    }
}