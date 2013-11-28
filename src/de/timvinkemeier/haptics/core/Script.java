package de.timvinkemeier.haptics.core;

import java.net.URISyntaxException;
import java.net.UnknownHostException;

import de.timvinkemeier.haptics.configuration.Schedule;
import de.timvinkemeier.haptics.exceptions.ScriptExecutionException;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class Script.
 */
public class Script {
	
	/** The location. */
	private String[] location;
	
	/** The commands. */
	private String[] commands;
	
	/** The type. */
	private ScriptType type;
	
	/** The item id. */
	private String itemID;
	
	/** The job id. */
	private String jobID;
	
	/** The throw exception on incomplete definition. */
	private boolean throwExceptionOnIncompleteDefinition = false;

	/**
	 * Instantiates a new script.
	 *
	 * @param location the location
	 * @param commands the commands
	 * @param type the type
	 * @param itemID the item id
	 * @param jobID the job id
	 * @param throwExceptionOnIncompleteDefinition the throw exception on incomplete definition
	 */
	public Script(String[] location, String[] commands, ScriptType type, String itemID, String jobID, boolean throwExceptionOnIncompleteDefinition) {
		this.location = location;
		this.commands = commands;
		this.type = type;
		this.itemID = itemID;
		this.jobID = jobID;
		this.throwExceptionOnIncompleteDefinition = throwExceptionOnIncompleteDefinition;
	}

	/**
	 * Run.
	 * 
	 * @throws UnknownHostException
	 *             the unknown host exception
	 * @throws URISyntaxException
	 *             the uRI syntax exception
	 * @throws Exception
	 *             the exception
	 */
	public void run() throws UnknownHostException, URISyntaxException, Exception {
		if (commands == null || location == null) {
			if (throwExceptionOnIncompleteDefinition) {
				throw new ScriptExecutionException("Location or script missing in " + type.toString() + "script for item '" + itemID + "'.");
			} else {
				Log.print("Location or script missing in " + type.toString() + "script for item '" + itemID + "'.", LogLevel.Info);
				return;
			}
		}
		String[] output = ScriptManager.executeScript(location, commands, ExtensionMethods.getEnvironmentVariables(jobID, itemID, type, Schedule.lastStep, (location[2].equals("localhost") || location[2].equals("127.0.0.1"))), type.toString() + "script for item '" + itemID + "'", false);
		if (!ExtensionMethods.IsNullOrWhitespace(output[1])) {
			// there was something on the error stream
			throw new ScriptExecutionException("Error during execution of " + type.toString() + "script for item '" + itemID + "':\n" + output[1]);
		}
	}
}
