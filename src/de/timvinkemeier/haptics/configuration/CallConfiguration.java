package de.timvinkemeier.haptics.configuration;

import java.security.InvalidParameterException;
import java.util.Iterator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

import de.timvinkemeier.haptics.extensions.ExtensionMethods;
import de.timvinkemeier.haptics.extensions.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class CallConfiguration.
 */
public class CallConfiguration {
	
	/** The call mode. */
	private CallMode callMode;
	
	/** The log level. */
	private LogLevel logLevel = LogLevel.Default;
	
	/** The logfile level. */
	private LogLevel logfileLevel = LogLevel.Default;
	
	/** The jsap. */
	private static JSAP jsap;
	
	/** The res. */
	private static JSAPResult res;

	/**
	 * Instantiates a new call configuration.
	 */
	private CallConfiguration() {
		super();
	}

	/**
	 * Parses the.
	 *
	 * @param commandlineArgs the commandline args
	 * @return the call configuration
	 * @throws JSAPException the jSAP exception
	 */
	public static CallConfiguration parse(String[] commandlineArgs) throws JSAPException {
		jsap = new JSAP();
		FlaggedOption fileOption = (FlaggedOption) new FlaggedOption("file").setUsageName("hapticsfile").setStringParser(JSAP.STRING_PARSER).setShortFlag('f').setLongFlag("file").setHelp("(#) Runs the test that is described in the given haptics file");
		jsap.registerParameter(fileOption);
		FlaggedOption testOption = (FlaggedOption) new FlaggedOption("setuptest").setUsageName("configfolder").setStringParser(JSAP.STRING_PARSER).setShortFlag('s').setLongFlag("setuptest").setHelp("(#) Runs a connection test with the given configuration (HDFS, JobTracker, SSH). The folder has to contain the necessary clusterConfiguration.");
		jsap.registerParameter(testOption);
		FlaggedOption cleanupOption = (FlaggedOption) new FlaggedOption("cleanup").setUsageName("configfolder").setStringParser(JSAP.STRING_PARSER).setShortFlag('c').setLongFlag("cleanup").setHelp("(#) Performs a cleanup of HDFS input and output paths and kills all running jobs from the cluster. The folder has to contain the necessary clusterConfiguration.");
		jsap.registerParameter(cleanupOption);
		FlaggedOption templateOption = (FlaggedOption) new FlaggedOption("template").setUsageName("outputfile").setStringParser(JSAP.STRING_PARSER).setShortFlag('t').setLongFlag("template").setHelp("(#) Creates a template haptics file at the given file location to ease test creation.");
		jsap.registerParameter(templateOption);
		FlaggedOption loglevelOption = (FlaggedOption) new FlaggedOption("loglevel").setUsageName("Verbose|Info|Important|Critical|None").setStringParser(JSAP.STRING_PARSER).setShortFlag('v').setLongFlag("loglevel").setHelp("(?) Sets the loglevel for the log file. Possible values are 'None', 'Critical', 'Important', 'Info' and 'Verbose'. Only used if -log <file> is specified. Default value is 'Critical'.");
		jsap.registerParameter(loglevelOption);
		FlaggedOption logfilelevelOption = (FlaggedOption) new FlaggedOption("logfilelevel").setUsageName("Verbose|Info|Important|Critical|None").setStringParser(JSAP.STRING_PARSER).setShortFlag('w').setLongFlag("logfilelevel").setHelp("(?) Sets the loglevel for the log file. Possible values are 'None', 'Critical', 'Severe', 'Info' and 'Verbose'. Only used if -log <file> is specified. Default value is the value of loglevel.");
		jsap.registerParameter(logfilelevelOption);
		FlaggedOption logfileOption = (FlaggedOption) new FlaggedOption("logfile").setUsageName("logfile").setStringParser(JSAP.STRING_PARSER).setShortFlag('l').setLongFlag("logfile").setHelp("(?) Sets the logfilepath. A log will be created using this path and logfilelevel (if given, else loglevel).");
		jsap.registerParameter(logfileOption);
		Switch logtimeSwitch = (Switch) new Switch("logtimes").setShortFlag('i').setLongFlag("includelogtimes").setHelp("(?) Includes times in log output.");
		jsap.registerParameter(logtimeSwitch);
		Switch resumeOnErrorSwitch = (Switch) new Switch("resumeonerror").setShortFlag('r').setLongFlag("resumeonerror").setHelp("(?) If the test specifies multiple runs, running tests will be continued if a test throws an error and has to be aborted.");
		jsap.registerParameter(resumeOnErrorSwitch);
		FlaggedOption clusterOption = (FlaggedOption) new FlaggedOption("clusterconfig").setUsageName("configfolder").setStringParser(JSAP.STRING_PARSER).setShortFlag('g').setLongFlag("clusterconfig").setHelp("(?) Overrides the clusterConfiguration setting from the hapticsfile with the given configuration.");
		jsap.registerParameter(clusterOption);
		FlaggedOption outputOption = (FlaggedOption) new FlaggedOption("out").setUsageName("outputbasefolder").setStringParser(JSAP.STRING_PARSER).setShortFlag('o').setLongFlag("out").setHelp("(?) Overrides the outputpath setting from the given hapticsfile with the given folder. Do not use with multi-test runs.");
		jsap.registerParameter(outputOption);
		Switch dbgSwitch = new Switch("dbg").setShortFlag(JSAP.NO_SHORTFLAG).setLongFlag("dbg");
		jsap.registerParameter(dbgSwitch);

		res = jsap.parse(commandlineArgs);
		String err = "";
		if (!res.success()) {
			for (@SuppressWarnings("rawtypes")
			Iterator errs = res.getErrorMessageIterator(); errs.hasNext();) {
				err += "Error: " + errs.next() + "\n";
			}
			displayHelp(err);
			throw new InvalidParameterException();
		}

		CallConfiguration cc = new CallConfiguration();

		// set loglevel
		cc.logLevel = LogLevel.fromString(res.getString("loglevel", "Default"));

		// set logfilelevel
		if (res.contains("logfilelevel")) {
			cc.logfileLevel = LogLevel.fromString(res.getString("logfilelevel"));
		} else {
			cc.logfileLevel = cc.logLevel;
		}

		if (res.contains("file")) {
			cc.callMode = CallMode.RunTest;
		} else if (res.contains("setuptest")) {
			cc.callMode = CallMode.TestSetup;
		} else if (res.contains("cleanup")) {
			cc.callMode = CallMode.Cleanup;
		} else if (res.contains("template")) {
			cc.callMode = CallMode.Template;
		} else {
			displayHelp("You have to give one option marked with (#)!");
			throw new InvalidParameterException();
		}

		return cc;
	}

	/**
	 * Displays the help messages.
	 *
	 * @param err the err
	 */
	public static void displayHelp(String err) {
		System.out.println("Haptics 0.1.0.0");
		System.out.println("(c) 2013, Tim N. Vinkemeier");
		System.out.println("---------------------------");
		if (!ExtensionMethods.IsNullOrWhitespace(err)) {
			System.out.println(err);
			System.out.println("");
		}
		System.out.println("Syntax: haptics [any # command] [any combination of ? commands (usually used with -f)]");
		System.out.println("((#) marks a main command, (?) marks an optional command)");
		System.out.println("");
		System.out.println(jsap.getHelp());
	}

	/**
	 * Gets the call mode.
	 *
	 * @return Returns the CallMode.
	 */
	public CallMode getCallMode() {
		return this.callMode;
	}

	/**
	 * Gets the log level.
	 *
	 * @return Returns the logLevel.
	 */
	public LogLevel getLogLevel() {
		return this.logLevel;
	}

	/**
	 * Gets the log file level.
	 * 
	 * @return the log file level
	 */
	public LogLevel getLogFileLevel() {
		return logfileLevel;
	}

	/**
	 * Gets the argument.
	 * 
	 * @param id
	 *            the id
	 * @return the argument
	 */
	public String getArgument(String id) {
		return res.getString(id);
	}

	/**
	 * Gets the result.
	 * 
	 * @return the result
	 */
	public JSAPResult getResult() {
		return res;
	}
}
