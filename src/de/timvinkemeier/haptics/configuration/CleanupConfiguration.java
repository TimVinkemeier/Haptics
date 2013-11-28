package de.timvinkemeier.haptics.configuration;

import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class CleanupConfiguration.
 */
public class CleanupConfiguration {
	
	/** The scripts. */
	private HashMap<String, String> scripts;
	
	/** The resume on error. */
	private boolean resumeOnError;

	/**
	 * Instantiates a new cleanup configuration.
	 */
	public CleanupConfiguration() {
		resumeOnError = false;
		scripts = new HashMap<>();
	}

	/**
	 * Gets the scripts.
	 *
	 * @return Returns the scripts.
	 */
	public HashMap<String, String> getScripts() {
		return this.scripts;
	}

	/**
	 * Sets the scripts.
	 *
	 * @param scripts The scripts to set.
	 */
	public void setScripts(HashMap<String, String> scripts) {
		this.scripts = scripts;
	}

	/**
	 * Checks if is resume on error.
	 *
	 * @return Returns the resumeOnError.
	 */
	public boolean isResumeOnError() {
		return this.resumeOnError;
	}

	/**
	 * Sets the resume on error.
	 *
	 * @param resumeOnError The resumeOnError to set.
	 */
	public void setResumeOnError(boolean resumeOnError) {
		this.resumeOnError = resumeOnError;
	}

}
