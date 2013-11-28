package de.timvinkemeier.haptics.configuration;

import java.io.File;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import de.timvinkemeier.haptics.exceptions.MissingDefinitionException;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class ClusterConfiguration.
 */
public class ClusterConfiguration {

	/** The configuration folder. */
	private String configurationFolder = "<configfolder>";
	
	/** The job tracker location. */
	private String jobTrackerLocation = "";
	
	/** The name node location. */
	private String nameNodeLocation = "";
	
	/** The configuration. */
	private Configuration configuration;

	/**
	 * Instantiates a new cluster configuration.
	 */
	public ClusterConfiguration() {
		super();
	}

	/**
	 * Gets the configuration folder.
	 *
	 * @return Returns the configurationFolder.
	 */
	public String getConfigurationFolder() {
		return this.configurationFolder;
	}

	/**
	 * Sets the configuration folder.
	 *
	 * @param configurationFolder The configurationFolder to set.
	 */
	public void setConfigurationFolder(String configurationFolder) {
		this.configurationFolder = configurationFolder;
	}

	/**
	 * Gets the configuration.
	 *
	 * @return the configuration
	 * @throws MissingDefinitionException the missing definition exception
	 */
	public Configuration getConfiguration() throws MissingDefinitionException {
		if (configuration == null) {
			configuration = new Configuration();
			if (ExtensionMethods.IsNullOrWhitespace(configurationFolder) || (configurationFolder.startsWith("<") && configurationFolder.endsWith(">"))) {
				// load from single settings
				if (ExtensionMethods.IsNullOrWhitespace(jobTrackerLocation) || ExtensionMethods.IsNullOrWhitespace(nameNodeLocation)) {
					throw new MissingDefinitionException("ClusterConfiguration is not set properly.");
				} else {
					configuration.set("fs.default.name", nameNodeLocation);
					configuration.set("mapred.job.tracker", jobTrackerLocation);
					Log.print("Configuration set. (NN: " + nameNodeLocation + ", JT: " + jobTrackerLocation + ")", LogLevel.Verbose);
				}
			} else {
				File configfolder = new File(configurationFolder);
				File[] configfiles = configfolder.listFiles();
				for (File f : configfiles) {
					if (f.isFile()) {
						configuration.addResource(new Path(f.getAbsolutePath()));
						Log.print("Configuration file loaded: " + f.getAbsolutePath(), LogLevel.Verbose);
					}
				}
			}
		}
		return configuration;
	}

	/**
	 * Reloads the configuration.
	 *
	 * @return the configuration
	 * @throws MissingDefinitionException the missing definition exception
	 */
	public Configuration reloadConfiguration() throws MissingDefinitionException {
		configuration = null;
		return getConfiguration();
	}

	/**
	 * Gets the job tracker location.
	 *
	 * @return Returns the jobTrackerLocation.
	 */
	public String getJobTrackerLocation() {
		return this.jobTrackerLocation;
	}

	/**
	 * Sets the job tracker location.
	 *
	 * @param jobTrackerLocation The jobTrackerLocation to set.
	 */
	public void setJobTrackerLocation(String jobTrackerLocation) {
		this.jobTrackerLocation = jobTrackerLocation;
	}

	/**
	 * Gets the name node location.
	 *
	 * @return Returns the nameNodeLocation.
	 */
	public String getNameNodeLocation() {
		return this.nameNodeLocation;
	}

	/**
	 * Sets the name node location.
	 *
	 * @param nameNodeLocation The nameNodeLocation to set.
	 */
	public void setNameNodeLocation(String nameNodeLocation) {
		this.nameNodeLocation = nameNodeLocation;
	}

	/**
	 * Gets the template.
	 * 
	 * @return the template
	 */
	public static ClusterConfiguration getTemplate() {
		ClusterConfiguration cc = new ClusterConfiguration();
		cc.configurationFolder = "<configurationfolder>";
		cc.jobTrackerLocation = "";
		cc.nameNodeLocation = "";
		return cc;
	}
}
