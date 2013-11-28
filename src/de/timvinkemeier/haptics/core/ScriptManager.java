package de.timvinkemeier.haptics.core;

import de.timvinkemeier.haptics.exceptions.ScriptExecutionException;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;
import de.timvinkemeier.haptics.extensions.OS;
import de.timvinkemeier.haptics.ssh.SSHManager;

// TODO: Auto-generated Javadoc
/**
 * The Class ScriptManager.
 */
public class ScriptManager {
	
	/**
	 * Execute script.
	 *
	 * @param location the location
	 * @param commandLines the command lines
	 * @param environmentVariables the environment variables
	 * @param scriptDescription the script description
	 * @param throwErrorOnErrorOutput the throw error on error output
	 * @return the string[]
	 * @throws Exception the exception
	 */
	public static String[] executeScript(String[] location, String[] commandLines, String[] environmentVariables, String scriptDescription, boolean throwErrorOnErrorOutput) throws Exception {
		String error = "";
		String output = "";
		int exitCodeTotal = 0;
		int linesExecuted = 0;

		if (location[2].equals("localhost") || location[2].equals("127.0.0.1")) {
			// local script execution
			Log.print("Executing " + scriptDescription + " on localhost...");
			// combine commands to one (os specific separator)
			String sep = ExtensionMethods.getOS() == OS.Windows ? " & " : "; ";
			if (Log.debug) {
				Log.print("Separator is: " + sep, LogLevel.Verbose);
			}
			String command = ExtensionMethods.StringArrayToString(commandLines, sep);
			if (Log.debug) {
				Log.print("commandLines '" + ExtensionMethods.StringArrayToString(commandLines, "\\n") + "' was extended to '" + command + "'.", LogLevel.Verbose);
			}
			Process pr = null;
			if (ExtensionMethods.getOS() == OS.Windows) {
				// in windows, start cmd.exe and execute it there
				String[] cmd = new String[] { "cmd.exe", "/c", "\"" + command + "\"" };
				pr = Runtime.getRuntime().exec(cmd, environmentVariables, null);
			} else {
				ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
				for (String s : environmentVariables) {
					if (Log.debug) {
						Log.print("Setting environment: " + s, LogLevel.Verbose);
					}
					String[] split = s.split("=");
					pb.environment().put(split[0], split.length > 1 ? split[1] : "");
				}
				pr = pb.start();
			}
			output += ExtensionMethods.StreamToString(pr.getInputStream());
			error += ExtensionMethods.StreamToString(pr.getErrorStream());
			pr.waitFor();
			linesExecuted++;
			if (pr.exitValue() > 0) {
				exitCodeTotal = pr.exitValue();
			}
			if (!ExtensionMethods.IsNullOrWhitespace(output))
				Log.print("Output of " + scriptDescription + ":\n" + output, LogLevel.Verbose);
			if (!ExtensionMethods.IsNullOrWhitespace(error))
				Log.print("Erroroutput of " + scriptDescription + ":\n" + error, LogLevel.Verbose);
			Log.print(scriptDescription + " exited with code " + exitCodeTotal + " (" + linesExecuted + " lines executed).");
		} else {
			// remote script execution
			SSHManager sshm = new SSHManager(location[0], location[1], location[2], null, Integer.parseInt(ExtensionMethods.IsNullOrWhitespace(location[3]) ? "22" : location[3]));
			sshm.connect(true);
			Log.print("Executing " + scriptDescription + " on '" + location[2] + (ExtensionMethods.IsNullOrWhitespace(location[3]) ? "" : ":" + location[3]) + "'" + (ExtensionMethods.IsNullOrWhitespace(location[0]) ? "" : " (user " + location[0] + ")") + "...");
			output += sshm.sendCommands(commandLines, environmentVariables);
			sshm.close();
			if (!ExtensionMethods.IsNullOrWhitespace(output))
				Log.print("Output of " + scriptDescription + ":\n" + output, LogLevel.Verbose);
			Log.print(scriptDescription + " executed (" + commandLines.length + " lines).");
		}
		if (!ExtensionMethods.IsNullOrWhitespace(error) && throwErrorOnErrorOutput)
			throw new ScriptExecutionException("Error during execution of '" + scriptDescription + "':\n" + error);
		return new String[] { output, error };
	}
}
