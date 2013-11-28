package de.timvinkemeier.haptics.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.InvalidPathException;
import java.security.InvalidParameterException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobStatus;

import com.martiansoftware.jsap.JSAPException;

import de.timvinkemeier.haptics.configuration.CallConfiguration;
import de.timvinkemeier.haptics.configuration.CallMode;
import de.timvinkemeier.haptics.configuration.TestConfiguration;
import de.timvinkemeier.haptics.exceptions.AbortExecutionException;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class Haptics.
 */
public class Haptics {
	
	/** The call config. */
	private static CallConfiguration callConfig;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JSAPException the jSAP exception
	 */
	public static void main(String[] args) throws IOException, JSAPException {
		try {
			callConfig = CallConfiguration.parse(args);
			Log.initialize(callConfig.getLogLevel(), callConfig.getResult().getBoolean("logtimes"), callConfig.getResult().getBoolean("dbg"), callConfig.getResult().getString("logfile", ""), callConfig.getLogFileLevel());
			Log.print("Starting with the following arguments: \"" + ExtensionMethods.StringArrayToString(args) + "\"", LogLevel.Verbose);
			if (callConfig.getCallMode() == CallMode.Help) {
				CallConfiguration.displayHelp("");
			} else if (callConfig.getCallMode() == CallMode.RunTest) {
				RunTest(callConfig);
			} else if (callConfig.getCallMode() == CallMode.Cleanup) {
				PerformCleanup(callConfig.getResult().getString("cleanup"));
			} else if (callConfig.getCallMode() == CallMode.TestSetup) {
				PerformSetupTest(callConfig.getResult().getString("setuptest"));
			} else if (callConfig.getCallMode() == CallMode.Template) {
				String template = TestConfiguration.getTemplate().SaveToString(true);
				// reorder config and testVariables and remove state: NotStarted
				template = template.replaceFirst("testVariables: \\{\\}", "");
				template = template.replaceFirst("configVariables: \\{\\}", "testVariables: \\{\\}\nconfigVariables: \\{\\}");
				template = template.replaceFirst("    state: NotStarted\\n", "");

				try {
					OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(callConfig.getResult().getString("template")), "UTF-8");
					osw.write(template);
					osw.close();
					Log.print("Template file saved to '" + callConfig.getResult().getString("template") + "'.", LogLevel.Critical);
				} catch (Exception ex) {
					Log.print(ex);
				}
			}
		} catch (InvalidParameterException ex) {
			// help with error messages already displayed
			return;
		} catch (Exception ex) {
			System.err.println("Haptics crashed. Sorry for that :(");
			ex.printStackTrace();
		} finally {
			System.out.println("\\-\\");
		}
	}

	/**
	 * Performs cleanup.
	 * 
	 * @param cc
	 *            the arguments
	 */
	private static void PerformCleanup(String cc) {
		try {
			if (ExtensionMethods.IsNullOrWhitespace((String) cc)) {
				throw new InvalidPathException(null, "No clusterconfiguration was given.");
			}

			// load clusterConfiguration
			Log.print("Loading configuration...", LogLevel.Info);
			String folderpath = cc;
			Configuration config = new Configuration();
			File configfolder = new File(folderpath);
			File[] configfiles = configfolder.listFiles();
			for (File f : configfiles) {
				if (f.isFile()) {
					config.addResource(new Path(f.getAbsolutePath()));
					Log.print("Configuration file loaded: " + f.getAbsolutePath(), LogLevel.Verbose);
				}
			}
			Log.print("Done.", LogLevel.Verbose);

			Log.print("Performing cleanup of Cluster '" + config.get("mapred.job.tracker") + "'...", LogLevel.Info);

			// connect to JobTracker
			Log.print("Connecting to JobTracker...", LogLevel.Verbose);
			JobClient jc = new JobClient(new JobConf(config));
			int i = 0;
			int c = jc.getAllJobs().length;
			Log.print("Done. There are " + c + " jobs. Unfinished jobs will be killed.", LogLevel.Verbose);
			for (JobStatus js : jc.getAllJobs()) {
				if (js.getRunState() == JobStatus.PREP || js.getRunState() == JobStatus.RUNNING) {
					i++;
					jc.getJob(js.getJobID()).killJob();
					Log.print("Killed job '" + js.getJobId() + "' (" + i + "/" + c + ")", LogLevel.Verbose);
				}
			}
			Log.print("Done. " + (c - i) + "/" + c + " jobs were skipped since they were not running.", LogLevel.Verbose);

			// connect to HDFS
			Log.print("Connecting to HDFS...", LogLevel.Verbose);
			FileSystem hdfs = FileSystem.get(config);
			Log.print("Done.", LogLevel.Verbose);

			// delete input data
			Log.print("Deleting input data from HDFS (" + Constants.HDFSInputTemporaryPath + ")...", LogLevel.Verbose);
			hdfs.delete(new Path(Constants.HDFSInputTemporaryPath), true);
			Log.print("Done.", LogLevel.Verbose);

			// delete output data from HDFS
			Log.print("Deleting output data from HDFS (" + Constants.HDFSOutputTemporaryPath + ")...", LogLevel.Verbose);
			hdfs.delete(new Path(Constants.HDFSOutputTemporaryPath), true);
			Log.print("Done.", LogLevel.Verbose);

			Log.print("> Cleanup successful!", LogLevel.Important);
		} catch (Exception ex) {
			Log.print("### Exception occurred during cleanup ###", LogLevel.Critical);
			Log.print(ex);
			Log.print("### Aborting execution ###", LogLevel.Critical);
		}
	}

	/**
	 * Runs a test.
	 *
	 * @param cc the cc
	 */
	private static void RunTest(CallConfiguration cc) {
		int successful = 0;
		int failed = 0;
		int testcount = 0;
		int current = 1;
		try {
			String file = cc.getResult().getString("file");
			Log.print("Preprocessing test file '" + file + "'...");
			TestPreprocessor tp = new TestPreprocessor(file);
			testcount = tp.getTestsCount();
			Log.print("Preprocessing finished.");
			Log.print("" + testcount + " test" + (testcount > 1 ? "s" : "") + " will be run.");
			while (tp.hasNext()) {
				TestConfiguration tc = tp.next();
				try {
					Log.print("Preparing to run test " + current + "/" + testcount + "...", LogLevel.Info);
					if (cc.getResult().contains("clusterconfig")) {
						// override cluster configuration
						Log.print("Overriding ClusterConfiguration from file with '" + cc.getResult().getString("clusterconfig") + "'.", LogLevel.Info);
						tc.getClusterConfiguration().setConfigurationFolder(cc.getResult().getString("clusterconfig"));
						tc.getClusterConfiguration().reloadConfiguration();
					}
					// override outputbasepath
					if (cc.getResult().contains("out")) {
						Log.print("Overriding OutputBasePath from file with '" + cc.getResult().getString("out") + "'.", LogLevel.Info);
						FileUtils.forceMkdir(new File(cc.getResult().getString("out")));
						tc.getSettings().setOutputBasePath(cc.getResult().getString("out"));
					}
					TestWorker tw = new TestWorker(tc);
					Log.print("Preparation finished. Running test " + current + "/" + testcount + ".");
					tw.start();
					tw.join();
					current++;
					if (!tw.hasFinishedSuccessful()) {
						failed++;
						throw new AbortExecutionException("Testrun not finished successfully.");
					}
					successful++;
				} catch (AbortExecutionException ex) {
					if (!cc.getResult().getBoolean("resumeonerror")) {
						Log.print("### Testrun " + (current - 1) + "/" + testcount + " did not finish successfully. Aborting further testruns. ###", LogLevel.Critical);
						throw ex;
					} else {
						Log.print("### Testrun " + (current - 1) + "/" + testcount + " did not finish successfully." + (current < testcount ? " Continuing with the next one." : "") + " ###", LogLevel.Critical);
					}
				} catch (Exception ex) {
					Log.print("### Exception occurred during execution of test " + (current - 1) + "/" + testcount + " ###", LogLevel.Critical);
					Log.print(ex);
					if (ex instanceof NullPointerException) {
						Log.print("!! Hint: NullPointerExceptions are often caused by incomplete test definitions. Check you definition file for completeness!", LogLevel.Critical);
					}
					if (!cc.getResult().getBoolean("resumeonerror")) {
						Log.print("### Aborting execution ###", LogLevel.Critical);
						throw new AbortExecutionException(ex);
					}
					if (current < testcount)
						Log.print("### Continuing with the next test. ###", LogLevel.Critical);
				}
			}
		} catch (AbortExecutionException ex) {
			// ignore
		} catch (Exception ex) {
			Log.print("### Exception occurred during test execution ###", LogLevel.Critical);
			Log.print(ex);
			Log.print("### Aborting execution ###", LogLevel.Critical);
		}
		Log.print("### All testruns finished. Successful: " + successful + "/" + (current - 1) + ", Failed: " + failed + "/" + (current - 1) + ", Tests executed: " + (current - 1) + "/" + testcount + " ###");
	}

	/**
	 * Perform setup test.
	 * 
	 * @param cc
	 *            the cc
	 */
	private static void PerformSetupTest(String cc) {
		try {
			if (ExtensionMethods.IsNullOrWhitespace((String) cc)) {
				throw new InvalidPathException(null, "No clusterconfiguration was given.");
			}

			StopWatch sw = new StopWatch();
			sw.start();
			// load clusterConfiguration
			Log.print("Loading configuration...", LogLevel.Info);
			String folderpath = cc;
			Configuration config = new Configuration();
			File configfolder = new File(folderpath);
			File[] configfiles = configfolder.listFiles();
			for (File f : configfiles) {
				if (f.isFile()) {
					config.addResource(new Path(f.getAbsolutePath()));
					Log.print("Configuration file loaded: " + f.getAbsolutePath(), LogLevel.Verbose);
				}
			}
			Log.print("Done.", LogLevel.Verbose);

			Log.print("Performing setup test of Cluster '" + config.get("mapred.job.tracker") + "'...", LogLevel.Info);

			// connect to JobTracker
			Log.print("Connecting to JobTracker...", LogLevel.Verbose);
			JobClient jc = new JobClient(new JobConf(config));
			int c = jc.getAllJobs().length;
			Log.print("JobTracker connection established successfully.", LogLevel.Verbose);

			// connect to HDFS
			Log.print("Connecting to HDFS...", LogLevel.Verbose);
			FileSystem hdfs = FileSystem.get(config);
			Log.print("Done.", LogLevel.Verbose);

			// create test data
			String onembstring = ExtensionMethods.generateRandomString(1024 * 1024);
			int mbsize = 30;
			File testfile = new File(ExtensionMethods.GetApplicationDirectory().getAbsolutePath(), "testdata.txt");
			Log.print("Creating test data for network performance test on local path (" + testfile.getAbsolutePath() + ")...", LogLevel.Verbose);
			FileWriter fw = new FileWriter(testfile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			for (int j = 0; j < mbsize; j++) {
				bw.write(onembstring);
				bw.newLine();
			}
			bw.close();
			fw.close();
			Log.print("File created successfully. (size: " + ExtensionMethods.BytesToFormatted(testfile.length()) + ")", LogLevel.Verbose);

			// move test data to HDFS
			Log.print("Moving test data to HDFS (/haptics/setuptest/sample.txt)...", LogLevel.Verbose);
			StopWatch up = new StopWatch();
			up.start();
			hdfs.copyFromLocalFile(new Path(testfile.getAbsolutePath()), new Path("/haptics/setuptest/sample.txt"));
			up.stop();
			String avgupload = "" + (ExtensionMethods.BytesToFormatted((long) (testfile.length() / (Math.max((up.getTime() / 1000.0), 0.001)))));
			testfile.delete();
			Log.print("File moved successfully.", LogLevel.Verbose);

			// delete test data from HDFS
			Log.print("Deleting test data from HDFS (/haptics/setuptest)...", LogLevel.Verbose);
			hdfs.delete(new Path("/haptics/setuptest"), true);
			Log.print("Folder deleted successfully.", LogLevel.Verbose);

			sw.stop();
			Log.print("> Setup test successful! (" + sw.getTime() + "ms) The average upload speed to HDFS was " + avgupload + "/s. You're good to go ;)", LogLevel.Critical);
			if (sw.getTime() > 20000)
				Log.print("> Note that the setup test took very long (expected time is 5-20 seconds). Maybe the network is just under heavy load, but consider that this will also affect your test runs. You can also try to run the test again later.", LogLevel.Important);
		} catch (Exception ex) {
			Log.print("### Exception occurred during setup test ###", LogLevel.Critical);
			Log.print(ex);
			Log.print("### Aborting execution ###", LogLevel.Critical);
		}
	}
}
