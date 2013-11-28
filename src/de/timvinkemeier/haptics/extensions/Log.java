package de.timvinkemeier.haptics.extensions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO: Auto-generated Javadoc
/**
 * The Class Log.
 */
public class Log {
	
	/** The log level. */
	private static LogLevel logLevel = LogLevel.Default;
	
	/** The log file level. */
	private static LogLevel logFileLevel = LogLevel.Default;
	
	/** The include time. */
	private static boolean includeTime = false;
	
	/** The create logfile. */
	private static boolean createLogfile = false;
	
	/** The debug. */
	public static boolean debug = false;
	
	/** The logfilepath. */
	private static String logfilepath = "";
	
	/** The date format. */
	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Initializes the log (settings and logfile).
	 *
	 * @param loglevel the loglevel
	 * @param includeTime the include time
	 * @param debug the debug
	 * @param logfilepath the logfilepath
	 * @param logfilelevel the logfilelevel
	 */
	public static void initialize(LogLevel loglevel, boolean includeTime, boolean debug, String logfilepath, LogLevel logfilelevel) {
		Log.logLevel = loglevel;
		Log.includeTime = includeTime;
		Log.logfilepath = logfilepath;
		Log.logFileLevel = logfilelevel;
		Log.debug = debug;
		createLogfile = !(logfilepath == null || logfilepath.isEmpty());
		if (logLevel != LogLevel.None) {
			System.out.println("LOG: Loglevel set to " + loglevel.toString() + ".");
			System.out.println("LOG: Timestamps will " + (includeTime ? "" : "not ") + "be included.");
			System.out.println((createLogfile ? "LOG: Log output will be written to \"" + logfilepath + "\" (LogLevel " + logFileLevel.toString() + ")." : "LOG: No logfile will be created."));
		}
		if (createLogfile) {
			writeToLogFile(dateFormat.format(new Date()) + " ###");
			writeToLogFile(dateFormat.format(new Date()) + " ### Logfile initialized (LogLevel " + logFileLevel.toString() + ").");
			writeToLogFile(dateFormat.format(new Date()) + " ###");
		}
		if (debug) {
			print("> DEBUG MODE ENABLED <", LogLevel.Critical);
		}
	}

	/**
	 * Prints the given exception. If in debug mode, it will also print the
	 * stacktrace to standarderr.
	 * 
	 * @param ex
	 *            The Exception.
	 */
	public static void print(Exception ex) {
		String exclassname = ex.getClass().getName();
		exclassname = exclassname.substring(exclassname.lastIndexOf('.') + 1);
		print(exclassname + ": " + ex.getLocalizedMessage(), LogLevel.Critical);
		if (debug) {
			ex.printStackTrace();
		}
	}

	/**
	 * Prints the string if logLevel is Info or Verbose.
	 * 
	 * @param s
	 *            the s
	 */
	public static void print(String s) {
		print(s, LogLevel.Info);
	}

	/**
	 * Prints the given string if the current logLevel is higher or equal the
	 * given classification.
	 * 
	 * @param s
	 *            the s
	 * @param classification
	 *            the classification for this message
	 */
	public static void print(String s, LogLevel classification) {
		String prefix = (includeTime ? dateFormat.format(new Date()) + " " : "") + ExtensionMethods.ExtendToLength(classification.toString() + ":", 11);
		String out = prefix;
		boolean first = true;
		for (String sp : s.split("\n")) {
			out += (first ? sp : ExtensionMethods.MultiplyString(" ", prefix.length()) + sp) + "\n";
			first = false;
		}
		out = out.substring(0, out.length() - 1);
		if (classification.toInt() >= logLevel.toInt()) {
			System.out.println(out);
		}
		if (createLogfile && classification.toInt() >= logFileLevel.toInt()) {
			writeToLogFile(out);
		}
	}

	/**
	 * Writes to log file.
	 * 
	 * @param text
	 *            the text
	 */
	public static void writeToLogFile(String text) {
		try {
			FileWriter fw = new FileWriter(logfilepath, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.newLine();
			bw.close();
			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Checks if is creates the logfile.
	 *
	 * @return Returns the createLogfile.
	 */
	public static boolean isCreateLogfile() {
		return createLogfile;
	}

	/**
	 * Gets the logfilepath.
	 *
	 * @return Returns the logfilepath.
	 */
	public static String getLogfilepath() {
		return logfilepath;
	}
}
