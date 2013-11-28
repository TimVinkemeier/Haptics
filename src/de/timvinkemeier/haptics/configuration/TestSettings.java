package de.timvinkemeier.haptics.configuration;

import org.joda.time.LocalDateTime;

// TODO: Auto-generated Javadoc
/**
 * The Class TestSettings.
 */
public class TestSettings {
	// miscellanous info
	/** The author. */
	private String author;
	
	/** The name. */
	private String name;
	
	/** The description. */
	private String description;
	
	/** The version. */
	private String version;
	
	/** The creation date. */
	private LocalDateTime creationDate;
	
	/** The last modification date. */
	private LocalDateTime lastModificationDate;

	// settings
	/** The keep input in hdfs. */
	private boolean keepInputInHDFS;
	
	/** The keep output in hdfs. */
	private boolean keepOutputInHDFS;
	
	/** The output base path. */
	private String outputBasePath;
	
	/** The cleanup configuration. */
	private CleanupConfiguration cleanupConfiguration;
	
	/** The setup configuration. */
	private SetupConfiguration setupConfiguration;

	/**
	 * Instantiates a new test settings.
	 */
	public TestSettings() {

	}

	/**
	 * Instantiates a new test settings.
	 *
	 * @param author the author
	 * @param name the name
	 * @param description the description
	 * @param version the version
	 */
	public TestSettings(String author, String name, String description, String version) {
		this.author = author;
		this.name = name;
		this.description = description;
		this.version = version;
		this.creationDate = new LocalDateTime();
		this.lastModificationDate = creationDate;
	}

	/**
	 * Gets the author.
	 *
	 * @return Returns the author.
	 */
	public String getAuthor() {
		return this.author;
	}

	/**
	 * Sets the author.
	 *
	 * @param author The author to set.
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * Gets the name.
	 *
	 * @return Returns the name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description.
	 *
	 * @return Returns the description.
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the version.
	 *
	 * @return Returns the version.
	 */
	public String getVersion() {
		return this.version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the creation date.
	 *
	 * @return Returns the creationDate.
	 */
	public LocalDateTime getCreationDate() {
		return this.creationDate;
	}

	/**
	 * Sets the creation date.
	 *
	 * @param creationDate The creationDate to set.
	 */
	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Gets the last modification date.
	 *
	 * @return Returns the lastModificationDate.
	 */
	public LocalDateTime getLastModificationDate() {
		return this.lastModificationDate;
	}

	/**
	 * Sets the last modification date.
	 *
	 * @param lastModificationDate The lastModificationDate to set.
	 */
	public void setLastModificationDate(LocalDateTime lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	/**
	 * Gets the output base path.
	 *
	 * @return Returns the outputBasePath.
	 */
	public String getOutputBasePath() {
		return this.outputBasePath;
	}

	/**
	 * Sets the output base path.
	 *
	 * @param outputBasePath The outputBasePath to set.
	 */
	public void setOutputBasePath(String outputBasePath) {
		this.outputBasePath = outputBasePath;
	}

	/**
	 * Checks if is keep input in hdfs.
	 *
	 * @return Returns the keepInputInHDFS.
	 */
	public boolean isKeepInputInHDFS() {
		return this.keepInputInHDFS;
	}

	/**
	 * Sets the keep input in hdfs.
	 *
	 * @param keepInputInHDFS The keepInputInHDFS to set.
	 */
	public void setKeepInputInHDFS(boolean keepInputInHDFS) {
		this.keepInputInHDFS = keepInputInHDFS;
	}

	/**
	 * Checks if is keep output in hdfs.
	 *
	 * @return Returns the keepOutputInHDFS.
	 */
	public boolean isKeepOutputInHDFS() {
		return this.keepOutputInHDFS;
	}

	/**
	 * Sets the keep output in hdfs.
	 *
	 * @param keepOutputInHDFS The keepOutputInHDFS to set.
	 */
	public void setKeepOutputInHDFS(boolean keepOutputInHDFS) {
		this.keepOutputInHDFS = keepOutputInHDFS;
	}

	/**
	 * Gets the cleanup configuration.
	 *
	 * @return Returns the cleanupConfiguration.
	 */
	public CleanupConfiguration getCleanupConfiguration() {
		return this.cleanupConfiguration;
	}

	/**
	 * Sets the cleanup configuration.
	 *
	 * @param cleanupConfiguration The cleanupConfiguration to set.
	 */
	public void setCleanupConfiguration(CleanupConfiguration cleanupConfiguration) {
		this.cleanupConfiguration = cleanupConfiguration;
	}

	/**
	 * Gets the setup configuration.
	 *
	 * @return Returns the setupConfiguration.
	 */
	public SetupConfiguration getSetupConfiguration() {
		return this.setupConfiguration;
	}

	/**
	 * Sets the setup configuration.
	 *
	 * @param setupConfiguration The setupConfiguration to set.
	 */
	public void setSetupConfiguration(SetupConfiguration setupConfiguration) {
		this.setupConfiguration = setupConfiguration;
	}

	/**
	 * Gets a template TestSettings object.
	 * 
	 * @return the template
	 */
	public static TestSettings getTemplate() {
		TestSettings ts = new TestSettings("<Author>", "<Name>", "<Description>\n<multiline>", "0.0.0.0");
		ts.outputBasePath = "<outputBasePath>";
		ts.creationDate = new LocalDateTime();
		ts.lastModificationDate = ts.creationDate;
		ts.setupConfiguration = new SetupConfiguration();
		ts.setupConfiguration.setResumeOnError(false);
		ts.setupConfiguration.getScripts().put("localhost", "echo \"Any script here...\"\nkey 'localhost' means it will be executed on localhost.");
		ts.setupConfiguration.getScripts().put("remote1", "<user>:<password>@<host>:<port>\nAny script here...\"\nif a key contains 'remote', it will be executed on the remote host specified in the first line.\nall other keys will be ignored.");
		ts.cleanupConfiguration = new CleanupConfiguration();
		ts.cleanupConfiguration.setResumeOnError(false);
		ts.cleanupConfiguration.getScripts().put("localhost", "echo \"Any script here...\"\nkey 'localhost' means it will be executed on localhost.");
		ts.cleanupConfiguration.getScripts().put("remote1", "<user>:<password>@<host>:<port>\nAny script here...\"\nif a key contains 'remote', it will be executed on the remote host specified in the first line.\nall other keys will be ignored.");
		return ts;
	}
}
