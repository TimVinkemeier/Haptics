package de.timvinkemeier.haptics.extensions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.mapreduce.Job;

import de.timvinkemeier.haptics.configuration.JobConfiguration;
import de.timvinkemeier.haptics.core.HJobBase;
import de.timvinkemeier.haptics.core.ScriptType;

// TODO: Auto-generated Javadoc
/**
 * This class contains public static utility methods.
 */
public class ExtensionMethods {

	/**
	 * Gets the application directory.
	 *
	 * @return A File-object representing the application directory.
	 * @throws URISyntaxException the uRI syntax exception
	 */
	public static File GetApplicationDirectory() throws URISyntaxException {
		if (Log.debug) {
			Log.print("Application directory found is: '" + new File("").getAbsolutePath() + "'", LogLevel.Verbose);
		}
		return new File("");
	}

	/**
	 * Converts a String[] to a String by concatenating the elements and adding
	 * a space in between 2 elements.
	 * 
	 * @param args
	 *            The String[] to convert.
	 * @return The String with spaces.
	 */
	public static String StringArrayToString(String[] args) {
		return StringArrayToString(args, " ");
	}

	/**
	 * Converts a String[] to a String by concatenating the elements and adding
	 * the given delimiter in between 2 elements.
	 * 
	 * @param args
	 *            The String[] to convert.
	 * @param delimiter
	 *            The delimiter to add between each 2 elements.
	 * @return The String with delimiters.
	 */
	public static String StringArrayToString(String[] args, String delimiter) {
		if (args == null) {
			return "<nullarray>";
		}
		String s = "";
		for (String a : args) {
			s += a + delimiter;
		}
		return s.substring(0, Math.max(s.length() - delimiter.length(), 0));
	}

	/**
	 * Extends a given String to the given length by adding spaces in front. If
	 * length is smaller than the length of the given String, it will return
	 * unchanged.
	 * 
	 * @param s
	 *            The String to extend.
	 * @param length
	 *            The length to which it should be extended.
	 * @return The extended String.
	 */
	public static String ExtendToLength(String s, int length) {
		while (s.length() < length) {
			s += " ";
		}
		return s;
	}

	/**
	 * Multiplies a given String (this means concatenating it 'factor' times).
	 * 
	 * @param string
	 *            The String to multiply.
	 * @param factor
	 *            The amount of times the String should be concatenated.
	 * @return The multiplied String.
	 */
	public static String MultiplyString(String string, int factor) {
		String s = "";
		for (int i = 0; i < factor; i++) {
			s += string;
		}
		return s;
	}

	/**
	 * Converts the given bytecount to a String representation with only one
	 * decimal place and unit (like 12,4MB).
	 * 
	 * @param size
	 *            The size to convert (in bytes).
	 * @return The String representation.
	 */
	public static String BytesToFormatted(long size) {
		if (size < 1024) {
			return "" + size + " bytes";
		} else if (size < Math.pow(1024, 2)) {
			BigDecimal bd = new BigDecimal(((float) size / Math.pow(1024, 1)));
			bd = bd.setScale(1, RoundingMode.HALF_UP);
			return "" + bd.doubleValue() + "KB";
		} else if (size < Math.pow(1024, 3)) {
			BigDecimal bd = new BigDecimal(((float) size / Math.pow(1024, 2)));
			bd = bd.setScale(1, RoundingMode.HALF_UP);
			return "" + bd.doubleValue() + "MB";
		} else if (size < Math.pow(1024, 4)) {
			BigDecimal bd = new BigDecimal(((float) size / Math.pow(1024, 3)));
			bd = bd.setScale(1, RoundingMode.HALF_UP);
			return "" + bd.doubleValue() + "GB";
		} else if (size < Math.pow(1024, 5)) {
			BigDecimal bd = new BigDecimal(((float) size / Math.pow(1024, 4)));
			bd = bd.setScale(1, RoundingMode.HALF_UP);
			return "" + bd.doubleValue() + "TB";
		} else if (size < Math.pow(1024, 6)) {
			BigDecimal bd = new BigDecimal(((float) size / Math.pow(1024, 5)));
			bd = bd.setScale(1, RoundingMode.HALF_UP);
			return "" + bd.doubleValue() + "PB";
		} else {
			BigDecimal bd = new BigDecimal(((float) size / Math.pow(1024, 6)));
			bd = bd.setScale(1, RoundingMode.HALF_UP);
			return "" + bd.doubleValue() + "EB";
		}
	}

	/**
	 * Stream to string.
	 * 
	 * @param s
	 *            the s
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String StreamToString(InputStream s) throws IOException {
		BufferedReader buff = new BufferedReader(new InputStreamReader(s));
		String str = "";
		String r = "";
		while ((str = buff.readLine()) != null) {
			r += str + "\n";
		}
		return r;
	}

	/**
	 * Checks if a given String is null, empty or only whitespace.
	 * 
	 * @param s
	 *            The String to check.
	 * @return True, if the given String is null, empty or only whitespace.
	 *         False otherwise.
	 */
	public static boolean IsNullOrWhitespace(String s) {
		return (s == null || s.trim().isEmpty());
	}

	/**
	 * Gets a job by job id.
	 * 
	 * @param jobs
	 *            the list
	 * @param id
	 *            the id
	 * @return the by job id
	 */
	public static Job getJobByJobID(Collection<Job> jobs, String id) {
		for (Job j : jobs) {
			if (j.getJobName().equals(id)) {
				return j;
			}
		}
		return null;
	}

	/**
	 * Gets the item by item id.
	 * 
	 * @param items
	 *            the items
	 * @param id
	 *            the id
	 * @return the item by item id
	 */
	public static HJobBase getItemByItemID(Collection<HJobBase> items, String id) {
		for (HJobBase j : items) {
			if (j.getName().toLowerCase().equals(id)) {
				return j;
			}
		}
		return null;
	}

	/**
	 * Gets a job by job id.
	 *
	 * @param configs the configs
	 * @param id the id
	 * @return the by job id
	 */
	public static JobConfiguration getJobConfigurationByJobID(List<JobConfiguration> configs, String id) {
		for (JobConfiguration jc : configs) {
			if (jc.getJobID().equals(id)) {
				return jc;
			}
		}
		return null;
	}

	/**
	 * Gets a date object representing today with time values set to 0.
	 *
	 * @return The date representing today.
	 */
	@SuppressWarnings("deprecation")
	public static Timestamp getToday() {
		Date d = new Date();
		return new Timestamp(d.getYear(), d.getMonth(), d.getDate(), 2, 0, 0, 0);
	}

	/**
	 * Gets the environment variables for scripts from the given.
	 *
	 * @param jobid the jobid
	 * @param sid the sid
	 * @param type the type
	 * @param step the step
	 * @param local the local
	 * @return the environment variables for scripts
	 * @throws UnknownHostException the unknown host exception
	 * @throws URISyntaxException the uRI syntax exception
	 */
	// public static String[] getEnvironmentVariablesForScripts(String jobid,
	// String sid, String jobjar, long step) throws URISyntaxException {
	// return new String[] { "PATH=" + System.getenv("PATH"), "HAPTICSJOBID=" +
	// jobid, "HAPTICSSID=" + sid, "HAPTICSSTEP=" + step, "HAPTICSJOBJAR=" +
	// jobjar, "HAPTICSPATH=" + GetApplicationDirectory().getAbsolutePath() };
	// }

	/**
	 * Gets the environment variables for remote scripts from the given.
	 * 
	 * @param item
	 *            the item
	 * @param job
	 *            the job
	 * @param step
	 *            the step
	 * @return the environment variables for scripts
	 * @throws UnknownHostException
	 * @throws URISyntaxException
	 */
	public static String[] getEnvironmentVariables(String jobid, String sid, ScriptType type, long step, boolean local) throws UnknownHostException, URISyntaxException {
		return new String[] { (local ? "PATH=" + System.getenv("PATH") : "HAPTICSISREMOTE=true"), "HAPTICSSCRIPTTYPE=" + type.toString() + "", "HAPTICSJOBID=" + jobid + "", "HAPTICSSID=" + sid + "", "HAPTICSSTEP=" + step + "", "HAPTICSPATH=" + InetAddress.getLocalHost().getHostName() + "(" + InetAddress.getLocalHost().getHostAddress() + ")@" + GetApplicationDirectory().getAbsolutePath() };
	}

	/**
	 * Gets the environment variables for setup cleanup scripts.
	 *
	 * @return the environment variables for setup cleanup scripts
	 */
	// public static String[]
	// getEnvironmentVariablesForSetupCleanupScripts(boolean remote, boolean
	// setup) throws URISyntaxException {
	// if (remote) {
	// return new String[] { "HAPTICSPATH=" +
	// GetApplicationDirectory().getAbsolutePath(), "HAPTICSSCRIPTTYPE=" +
	// (setup ? "setup" : "cleanup") };
	// } else {
	// return new String[] { "PATH=" + System.getenv("PATH"), "HAPTICSPATH=" +
	// GetApplicationDirectory().getAbsolutePath(), "HAPTICSSCRIPTTYPE=" +
	// (setup ? "setup" : "cleanup") };
	// }
	// }

	/**
	 * Gets the os.
	 * 
	 * @return the os
	 */
	public static OS getOS() {
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
			return OS.Windows;
		} else if (System.getProperty("os.name").toLowerCase().indexOf("nux") > -1 || System.getProperty("os.name").toLowerCase().indexOf("nix") > -1 || System.getProperty("os.name").toLowerCase().indexOf("aix") > -1) {
			return OS.Unix;
		} else if (System.getProperty("os.name").toLowerCase().indexOf("mac") > -1) {
			return OS.Mac;
		} else if (System.getProperty("os.name").toLowerCase().indexOf("sunos") > -1) {
			return OS.Solaris;
		}

		return OS.Unknown;
	}

	/**
	 * Generate random string with given length.
	 * 
	 * @param length
	 *            the length
	 * @return the string
	 */
	public static String generateRandomString(int length) {
		int cb = (length + 3) / 4 * 3; // base 64: 3 bytes = 4 chars
		byte[] ab = new byte[cb];
		Random random = new Random();
		random.nextBytes(ab);
		return Base64.encodeBase64String(ab);
	}

	/**
	 * Contains whitespace.
	 * 
	 * @param id
	 *            the id
	 * @return true, if successful
	 */
	public static boolean containsWhitespace(String id) {
		return id.contains(" ") || id.contains("\t") || id.contains("\r") || id.contains("\n");
	}

	/**
	 * Gets the size of a file, even if it is a directory (sum of its contents).
	 * Note that this may take very long for large directories.
	 * 
	 * @param f
	 *            the f
	 * @return the size
	 */
	public static long getSize(File f) {
		if (f == null)
			return 0;
		if (f.isFile()) {
			return f.length();
		} else {
			long l = 0;
			if (f.listFiles() == null)
				return 0;
			for (File fi : f.listFiles()) {
				l += getSize(fi);
			}
			return l;
		}
	}

	/**
	 * Splits the given host string into an array like the following example
	 * user:password@host:port => [user,password,host,port].
	 * 
	 * @param hostString
	 *            the host string
	 * @return the string[]
	 */
	public static String[] splitHostString(String hostString) {
		String loc = hostString;
		String[] r = new String[4];
		String left = loc.split("@")[0].trim();
		String right = loc.split("@").length < 2 ? null : loc.split("@")[1].trim();
		if (left != null && right == null) {
			right = left.trim();
			left = null;
		}
		if (left != null && left.contains(":")) {
			r[0] = left.split(":")[0].trim();
			r[1] = left.split(":").length < 2 ? null : left.split(":")[1].trim();
		} else {
			r[0] = "";
			r[1] = left == null ? "" : left.trim();
		}
		if (right != null && right.contains(":")) {
			r[2] = right.split(":")[0].trim();
			r[3] = right.split(":").length < 2 ? null : right.split(":")[1].trim();
		} else {
			r[2] = right == null ? "" : right.trim();
			r[3] = "";
		}
		return r;
	}

	/**
	 * Extend with builtin scripts.
	 * 
	 * @param script
	 *            the script
	 * @return the string
	 */
	public static String ExtendWithBuiltinScripts(String script) {
		// builtin kill methods
		script = script.replace("builtin:nodekill", "jps|grep -w NameNode|awk '{print $1}'|xargs kill -9\njps|grep -w TaskTracker|awk '{print $1}'|xargs kill -9\njps|grep -w JobTracker|awk '{print $1}'|xargs kill -9\njps|grep -w SecondaryNameNode|awk '{print $1}'|xargs kill -9\njps|grep -w DataNode|awk '{print $1}'|xargs kill -9\n");
		script = script.replace("builtin:namenodekill", "jps|grep -w NameNode|awk '{print $1}'|xargs kill -9\n");
		script = script.replace("builtin:datanodekill", "jps|grep -w DataNode|awk '{print $1}'|xargs kill -9\n");
		script = script.replace("builtin:secondarynamenodekill", "jps|grep -w SecondaryNameNode|awk '{print $1}'|xargs kill -9\n");
		script = script.replace("builtin:jobtrackerkill", "jps|grep -w JobTracker|awk '{print $1}'|xargs kill -9\n");
		script = script.replace("builtin:tasktrackerkill", "jps|grep -w TaskTracker|awk '{print $1}'|xargs kill -9\n");
		script = script.replace("\n\n", "\n");
		return script;
	}
}
