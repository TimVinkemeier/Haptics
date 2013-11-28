package de.timvinkemeier.haptics.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryNotEmptyException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import javax.activity.InvalidActivityException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import de.timvinkemeier.haptics.configuration.JobConfiguration;
import de.timvinkemeier.haptics.configuration.ScheduleItem;
import de.timvinkemeier.haptics.configuration.TestConfiguration;
import de.timvinkemeier.haptics.exceptions.AmbiguousDefinitionException;
import de.timvinkemeier.haptics.exceptions.InvalidDefinitionException;
import de.timvinkemeier.haptics.exceptions.MissingDefinitionException;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;
import de.timvinkemeier.haptics.metrics.Metrics;
import de.timvinkemeier.haptics.metrics.Report;

// TODO: Auto-generated Javadoc
/**
 * The Class TestWorker.
 */
public class TestWorker extends Thread {
	
	/** The configuration. */
	private TestConfiguration configuration;
	
	/** The metrics. */
	private Metrics metrics;
	
	/** The jobs. */
	private HashMap<String, HJobBase> jobs = new HashMap<>();
	// private HashMap<String, List<String>> inputFilesCombined = new
	// HashMap<>();
	/** The hdfs. */
	private FileSystem hdfs = null;
	
	/** The schedule exception. */
	private volatile Exception scheduleException = null;
	
	/** The finished successful. */
	private boolean finishedSuccessful = false;
	
	/** The timestamp. */
	private String timestamp;

	/**
	 * Instantiates a new test worker.
	 *
	 * @param config the config
	 * @throws MissingDefinitionException the missing definition exception
	 */
	public TestWorker(TestConfiguration config) throws MissingDefinitionException {
		configuration = config;
		timestamp = "" + Calendar.getInstance().getTimeInMillis();

		if (configuration == null) {
			throw new MissingDefinitionException("No TestConfiguration provided. Unable to run test.");
		} else if (configuration.getClusterConfiguration() == null) {
			throw new MissingDefinitionException("No clusterConfiguration provided. Unable to run test.");
		} else if (configuration.getJobConfigurations() == null) {
			throw new MissingDefinitionException("No jobConfigurations provided. Unable to run test.");
		} else if (configuration.getSchedule() == null) {
			throw new MissingDefinitionException("No schedule provided. Unable to run test.");
		} else if (configuration.getSettings() == null) {
			throw new MissingDefinitionException("No settings provided. Unable to run test.");
		}
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			metrics = new Metrics(configuration.getClusterConfiguration().getConfiguration());
		} catch (Exception ex) {
			Log.print("Exception during initialization of metrics. Aborting testrun (check clusterConfiguration)...", LogLevel.Critical);
			Log.print(ex);
			return;
		}
		StopWatch sw = new StopWatch();
		metrics.start();
		sw.start();
		Log.print("=======================", LogLevel.Important);
		Log.print("Starting test execution", LogLevel.Important);
		Log.print("=======================", LogLevel.Important);
		StopWatch watch = new StopWatch();
		boolean onlyResumeWithCleanup = false;
		int errorphase = 100;

		// Phase 1: look for required input files (jars and others)
		Log.print("# Phase 1: Validation of input files and folders", LogLevel.Important);
		watch.start();
		try {
			ValidateFilesFolders();
			watch.stop();
			Log.print("## Phase 1 completed successfully (" + watch.getTime() + "ms)", LogLevel.Important);
		} catch (Exception ex) {
			Log.print("## Phase 1 resulted in the following error:", LogLevel.Critical);
			Log.print(ex);
			Log.print("## Further execution will be aborted...", LogLevel.Critical);
			onlyResumeWithCleanup = true;
			errorphase = 1;
		}

		// Phase 2: setup and pre-processing
		if (!onlyResumeWithCleanup) {
			Log.print("# Phase 2: Setup and pre-processing", LogLevel.Important);
			watch.reset();
			watch.start();
			try {
				PerformSetupPreprocessing();
				watch.stop();
				Log.print("## Phase 2 completed successfully (" + watch.getTime() + "ms)", LogLevel.Important);
			} catch (Exception ex) {
				Log.print("## Phase 2 resulted in the following error:", LogLevel.Critical);
				Log.print(ex);
				Log.print("## Further execution will be aborted...", LogLevel.Critical);
				onlyResumeWithCleanup = true;
				errorphase = 2;
			}
		} else {
			Log.print("## Phase 2 skipped because of previous errors");
		}

		// Phase 3: upload input files to hdfs
		if (!onlyResumeWithCleanup) {
			Log.print("# Phase 3: Upload of input data", LogLevel.Important);
			watch.reset();
			watch.start();
			try {
				UploadInputData();
				watch.stop();
				Log.print("## Phase 3 completed successfully (" + watch.getTime() + "ms)", LogLevel.Important);
			} catch (Exception ex) {
				Log.print("## Phase 3 resulted in the following error:", LogLevel.Critical);
				Log.print(ex);
				Log.print("## Further execution will be aborted...", LogLevel.Critical);
				onlyResumeWithCleanup = true;
				errorphase = 3;
			}
		} else {
			Log.print("## Phase 3 skipped because of previous errors");
		}

		// Phase 4: create Job objects for submitting
		if (!onlyResumeWithCleanup) {
			Log.print("# Phase 4: Creation of Hadoop job objects", LogLevel.Important);
			watch.reset();
			watch.start();
			try {
				CreateJobObjects();
				watch.stop();
				Log.print("## Phase 4 completed successfully (" + watch.getTime() + "ms)", LogLevel.Important);
			} catch (Exception ex) {
				Log.print("## Phase 4 resulted in the following error:", LogLevel.Critical);
				Log.print(ex);
				Log.print("## Further execution will be aborted...", LogLevel.Critical);
				onlyResumeWithCleanup = true;
				errorphase = 4;
			}
		} else {
			Log.print("## Phase 4 skipped because of previous errors");
		}

		// Phase 5: execute schedule
		if (!onlyResumeWithCleanup) {
			Log.print("# Phase 5: Schedule execution", LogLevel.Important);
			watch.reset();
			watch.start();
			try {
				ExecuteSchedule();
				watch.stop();
				Log.print("## Phase 5 completed successfully (" + watch.getTime() + "ms)", LogLevel.Important);
			} catch (Exception ex) {
				Log.print("## Phase 5 resulted in the following error:", LogLevel.Critical);
				Log.print(ex);
				Log.print("## Further execution will be aborted...", LogLevel.Critical);
				onlyResumeWithCleanup = true;
				errorphase = 5;
			}
		} else {
			Log.print("## Phase 5 skipped because of previous errors");
		}

		// Phase 6: get output from hdfs
		if (!onlyResumeWithCleanup) {
			Log.print("# Phase 6: Output download", LogLevel.Important);
			watch.reset();
			watch.start();
			try {
				DownloadOutput();
				watch.stop();
				Log.print("## Phase 6 completed successfully (" + watch.getTime() + "ms)", LogLevel.Important);
			} catch (Exception ex) {
				Log.print("## Phase 6 resulted in the following error:", LogLevel.Critical);
				Log.print(ex);
				Log.print("## Further execution will be aborted...", LogLevel.Critical);
				onlyResumeWithCleanup = true;
				errorphase = 6;
			}
		} else {
			Log.print("## Phase 6 skipped because of previous errors");
		}

		// Phase 7: cleanup and post-processing
		if (errorphase > 2) { // else no cleanup necessary
			if (onlyResumeWithCleanup) {
				Log.print("# Phase 7: Cleanup (error recovery)", LogLevel.Important);
			} else {
				Log.print("# Phase 7: Cleanup and post-processing", LogLevel.Important);
			}
			watch.reset();
			watch.start();
			try {
				PerformCleanupPostprocessing(onlyResumeWithCleanup);
				watch.stop();
				Log.print("## Phase 7 completed successfully (" + watch.getTime() + "ms)", LogLevel.Important);
			} catch (Exception ex) {
				Log.print("## Phase 7 resulted in the following error:", LogLevel.Critical);
				Log.print(ex);
				Log.print("## Further execution will be aborted...", LogLevel.Critical);
				onlyResumeWithCleanup = true;
				// do not overwrite previous errorphase
				errorphase = errorphase < 100 ? errorphase : 7;
			}
		} else {
			Log.print("## Phase 7 skipped because of previous errors");
		}

		metrics.stop();

		// Phase 8: report generation
		if (!onlyResumeWithCleanup) {
			Log.print("# Phase 8: Report generation", LogLevel.Important);
			watch.reset();
			watch.start();
			try {
				GenerateReport();
				watch.stop();
				Log.print("## Phase 8 completed successfully (" + watch.getTime() + "ms)", LogLevel.Important);
			} catch (Exception ex) {
				Log.print("## Phase 8 resulted in the following error:", LogLevel.Critical);
				Log.print(ex);
				Log.print("## Further execution will be aborted...", LogLevel.Critical);
				onlyResumeWithCleanup = true;
				errorphase = 8;
			}
		} else {
			Log.print("## Phase 8 skipped because of previous errors");
		}

		sw.stop();
		String longString = "";
		if (!onlyResumeWithCleanup) {
			Log.print("Test execution finished (" + metrics.getFullDuration() + "ms).", LogLevel.Important);
			longString = "You can find the reports generated here: " + FilenameUtils.concat(configuration.getSettings().getOutputBasePath(), Constants.ReportFolderName);
			finishedSuccessful = true;
		} else {
			longString = "Test execution aborted because of error in phase " + errorphase + " (" + sw.getTime() + "ms).";
			finishedSuccessful = false;
		}
		Log.print(ExtensionMethods.MultiplyString("=", longString.length()), LogLevel.Important);
		if (!onlyResumeWithCleanup) {
			Log.print("Test execution finished (" + metrics.getFullDuration() + "ms).", LogLevel.Important);
		}
		Log.print(longString, LogLevel.Important);
		Log.print(ExtensionMethods.MultiplyString("=", longString.length()), LogLevel.Important);
	}

	/**
	 * Validates that the files and folders that are needed for this test run
	 * exist (this includes input data paths of jobs as well as their
	 * .jar-files)
	 *
	 * @throws InvalidDefinitionException the invalid definition exception
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws MissingDefinitionException the missing definition exception
	 */
	private void ValidateFilesFolders() throws InvalidDefinitionException, URISyntaxException, IOException, MissingDefinitionException {
		metrics.startPhase(Phase.Validation);
		// JobConfiguration inputs and jars
		for (JobConfiguration jc : configuration.getJobConfigurations()) {
			// input paths
			if (jc.getInputPaths() != null) {
				for (String s : jc.getInputPaths()) {
					if (!fileExists(s)) {
						throw new FileNotFoundException("File '" + s + "' not found (needed for job '" + jc.getJobID() + "')");
					} else {
						Log.print("Successfully validated " + (new File(s).isFile() ? "file" : "folder") + " \"" + s + "\"", LogLevel.Verbose);
					}
				}
			}
			// overrideInputPaths
			for (ScheduleItem si : configuration.getSchedule().getItems()) {
				if (si.getOverrideInputPaths() != null && si.getOverrideInputPaths().size() > 0) {
					for (String s : si.getOverrideInputPaths()) {
						if (!fileExists(s)) {
							throw new FileNotFoundException("File '" + s + "' not found (needed for job '" + jc.getJobID() + "')");
						} else {
							Log.print("Successfully validated " + (new File(s).isFile() ? "file" : "folder") + " '" + s + "'", LogLevel.Verbose);
						}
					}
				}
			}
			// jobJarPath (only if not builtin)
			if (!jc.getJobJarPath().contains("builtin:")) {
				File jobjar = new File(jc.getJobJarPath());
				if (jobjar.isDirectory()) {
					throw new InvalidDefinitionException("Folder '" + jc.getJobJarPath() + "' was given as jobJarPath for job '" + jc.getJobID() + "'. Expected .jar file.");
				} else if (!jobjar.exists()) {
					throw new FileNotFoundException("File '" + jc.getJobJarPath() + "' not found (needed for job '" + jc.getJobID() + "')");
				} else if (jobjar.isFile() && !jobjar.getAbsolutePath().endsWith(".jar")) {
					throw new InvalidDefinitionException("File '" + jc.getJobJarPath() + "' was given as jobJarPath for job '" + jc.getJobID() + "'. Expected .jar file.");
				}
			} else if (jc.getJobJarPath().contains("builtin:")) {
				// File builtins = new
				// File(FilenameUtils.concat(ExtensionMethods.GetApplicationDirectory().getAbsolutePath(),
				// "HapticsBuiltIns.jar"));
				// if (!builtins.exists())
				// throw new FileNotFoundException("Could not find '" +
				// builtins.getAbsolutePath() +
				// "'. This jar file is necessary for builtin MapReduce jobs.");
			}
			if (!jc.getJobJarPath().contains("builtin:")) {
				Log.print("Successfully validated file '" + jc.getJobJarPath() + "'", LogLevel.Verbose);
			}
		}
		// outputpath (has to be empty)
		File out = new File(configuration.getSettings().getOutputBasePath());
		if (!out.exists())
			out.mkdirs();
		if (!out.isDirectory() || out.listFiles().length > 0) {
			throw new DirectoryNotEmptyException("Output Base directory '" + out.getAbsolutePath() + "' has to be empty.");
		}
		Log.print("Successfully validated output directory '" + out.getAbsolutePath() + "'.", LogLevel.Verbose);
		metrics.endPhase(Phase.Validation);
	}

	/**
	 * CHecks whether a given file exists. The path can either be a local path
	 * or an HDFS path which has to start with hdfs: (Examples:
	 * /home/test/test.txt (local), hdfs:/home/test.txt (HDFS))
	 * 
	 * @param path
	 *            the path
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws MissingDefinitionException
	 *             the missing definition exception
	 */
	private boolean fileExists(String path) throws IOException, MissingDefinitionException {
		if (path.startsWith("hdfs:")) {
			// HDFS path
			if (hdfs == null) {
				Log.print("Connecting to HDFS master...");
				hdfs = FileSystem.get(configuration.getClusterConfiguration().getConfiguration());
				Log.print("Done.");
			}
			return hdfs.exists(new Path(path.substring(5)));
		} else {
			// local path
			return new File(path).exists();
		}
	}

	/**
	 * Performs setup and preprocessing tasks if such are stated in the
	 * JobConfiguration.
	 *
	 * @throws Exception the exception
	 */
	private void PerformSetupPreprocessing() throws Exception {
		metrics.startPhase(Phase.SetupPreprocessing);
		HashMap<String, String> scripts = configuration.getSettings().getSetupConfiguration() == null ? null : configuration.getSettings().getSetupConfiguration().getScripts();
		if (scripts != null) {
			for (String key : scripts.keySet()) {
				if (key.equals("localhost") || key.startsWith("remote")) {
					String[] script = new String[0];
					String[] loc = new String[0];
					String rl = "<location>";
					try {
						if (key.equals("localhost")) {
							// execute script on localhost
							script = ExtensionMethods.ExtendWithBuiltinScripts(scripts.get(key)).split("\n");
							loc = new String[] { "", "", "localhost", "" };
							rl = "localhost";
						} else if (key.startsWith("remote")) {
							// execute remote
							String locString = scripts.get(key).split("\n")[0];
							loc = ExtensionMethods.splitHostString(locString);
							rl = loc[0] + "@" + loc[2] + ":" + loc[3];
							script = ExtensionMethods.ExtendWithBuiltinScripts(scripts.get(key).replace(locString + "\n", "")).split("\n");
						}
						// ScriptManager.executeScript(loc, script,
						// ExtensionMethods.getEnvironmentVariablesForSetupCleanupScripts(key.startsWith("remote"),
						// true), "setup script", true);
						new Script(loc, script, ScriptType.Setup, "", "", true).run();
					} catch (Exception ex) {
						if (configuration.getSettings().getSetupConfiguration().isResumeOnError()) {
							Log.print("Error during setup script execution on " + rl + ". Continuing with the next script (resumeOnError).", LogLevel.Critical);
							Log.print(ex);
						} else {
							throw ex;
						}
					}
				}
			}
		}
		metrics.endPhase(Phase.SetupPreprocessing);
	}

	/**
	 * Uploads the input data into temporary folders in HDFS.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws MissingDefinitionException the missing definition exception
	 */
	private void UploadInputData() throws IOException, MissingDefinitionException {
		metrics.startPhase(Phase.Upload);
		if (hdfs == null) {
			Log.print("Connecting to HDFS master...");
			hdfs = FileSystem.get(configuration.getClusterConfiguration().getConfiguration());
			Log.print("Done.");
		}

		// creating directory structure
		Log.print("Creating input directory structure (" + Constants.HDFSInputTemporaryPath + timestamp + ")...", LogLevel.Verbose);
		hdfs.mkdirs(new Path(Constants.HDFSInputTemporaryPath + timestamp));
		Log.print("Done.", LogLevel.Verbose);
		Log.print("Creating output directory structure (" + Constants.HDFSOutputTemporaryPath + timestamp + ")...", LogLevel.Verbose);
		hdfs.mkdirs(new Path(Constants.HDFSOutputTemporaryPath + timestamp));
		Log.print("Done.", LogLevel.Verbose);

		// JobConfiguration input paths
		for (JobConfiguration jc : configuration.getJobConfigurations()) {
			if (jc.getInputPaths() != null) {
				String hdfspath = Constants.HDFSInputTemporaryPath + timestamp + "/" + Constants.HDFSJobInputPrefix + jc.getJobID() + "/";
				hdfs.mkdirs(new Path(hdfspath));
				Log.print("Uploading input data for job '" + jc.getJobID() + "'...");

				for (String p : jc.getInputPaths()) {
					if (p.startsWith("hdfs:"))
						continue;
					File f = new File(p);
					uploadFile(hdfs, f, hdfspath);
				}
				Log.print("Done.");
			}
		}

		// overrideInput input paths
		for (ScheduleItem si : configuration.getSchedule().getItems()) {
			List<String> oip = si.getOverrideInputPaths();
			if (oip.size() > 0) {
				String hdfspath = Constants.HDFSInputTemporaryPath + timestamp + "/" + Constants.HDFSJobInputPrefix + si.getJobID() + "-" + si.getID() + "/";
				hdfs.mkdirs(new Path(hdfspath));
				Log.print("Uploading override input data for schedule item '" + si.getID() + "'...");
				for (String p : oip) {
					if (p.startsWith("hdfs:"))
						continue;
					File f = new File(p);
					uploadFile(hdfs, f, hdfspath);
				}
				Log.print("Done.");
			}
		}
		metrics.endPhase(Phase.Upload);
	}

	/**
	 * Uploads the given file (folder or single file) to the given FileSystem
	 * using the given path.
	 * 
	 * @param hdfs
	 *            The FileSystem (usually HDFS).
	 * @param f
	 *            The File to upload.
	 * @param path
	 *            The path below which the File should be placed. If a file with
	 *            that name already exists, a number is appended to distinguish
	 *            them.
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void uploadFile(FileSystem hdfs, File f, String path) throws IOException {
		if (!f.exists())
			throw new FileNotFoundException("File '" + f.getAbsolutePath() + "' could not be found.");

		if (f.isFile()) {
			// upload single file
			String filename = FilenameUtils.getBaseName(f.getName());
			String ext = FilenameUtils.getExtension(f.getName());
			String i = "";
			while (hdfs.exists(new Path(path + filename + i + "." + ext))) {
				// file with this name already exists - append a number to
				// distinguish them
				i = "" + ((i.isEmpty() ? 0 : Integer.parseInt(i)) + 1);
			}
			String filepath = path + filename + i + "." + ext;
			Log.print("Uploading file \"" + f.getAbsolutePath() + "\" (" + ExtensionMethods.BytesToFormatted(f.length()) + ") to \"" + filepath + "\"...", LogLevel.Verbose);
			metrics.getUploadMetrics().beginNewUpload(f);
			hdfs.copyFromLocalFile(new Path(f.getAbsolutePath()), new Path(filepath));
			metrics.getUploadMetrics().endNewUpload();
			Log.print("Done.", LogLevel.Verbose);
		} else {
			// upload contents of a folder
			for (File ff : f.listFiles()) {
				uploadFile(hdfs, ff, path);
			}
		}
	}

	/**
	 * Creates the job objects.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws IllegalStateException the illegal state exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws AmbiguousDefinitionException the ambiguous definition exception
	 * @throws MissingDefinitionException the missing definition exception
	 * @throws InvalidDefinitionException the invalid definition exception
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	@SuppressWarnings("rawtypes")
	private void CreateJobObjects() throws IOException, IllegalStateException, ClassNotFoundException, AmbiguousDefinitionException, MissingDefinitionException, InvalidDefinitionException, URISyntaxException, InstantiationException, IllegalAccessException {
		metrics.startPhase(Phase.JobCreation);
		for (ScheduleItem si : configuration.getSchedule().getItems()) {
			if (!si.getJobID().equals("builtin:action")) {
				JobConfiguration jc = ExtensionMethods.getJobConfigurationByJobID(configuration.getJobConfigurations(), si.getJobID());
				if (jc == null) {
					throw new MissingDefinitionException("ScheduleItem '" + si.getID() + "' references jobID '" + si.getJobID() + "' - no matching JobConfiguration found.");
				}
				jobs.put(si.getID(), HJobBase.fromJobConfiguration(jc, configuration.getClusterConfiguration().getConfiguration(), si, timestamp));
			} else {
				// ####################################################
				// create action with scripts
				// ####################################################
				jobs.put(si.getID(), HAction.fromScheduleItem(si));
			}
		}
		metrics.endPhase(Phase.JobCreation);
	}

	/**
	 * Execute schedule.
	 *
	 * @return An error String. If empty, everything worked fine, else it
	 * contains error details.
	 * @throws Exception the exception
	 */
	private void ExecuteSchedule() throws Exception {
		metrics.startPhase(Phase.ScheduleExecution);

		// create constraint HashMaps
		// final HashMap<String, ExpressionBase> startConstraints = new
		// HashMap<>();
		// final HashMap<String, ExpressionBase> killConstraints = new
		// HashMap<>();
		// for (ScheduleItem si : configuration.getSchedule().getItems()) {
		// if (si.getStartConstraints() != null) {
		// startConstraints.put(si.getID(), si.getStartConstraints());
		// }
		// if (si.getKillConstraints() != null) {
		// killConstraints.put(si.getID(), si.getKillConstraints());
		// }
		// }

		// execute schedule
		final Timer t = new Timer();
		final Semaphore s = new Semaphore(0);
		final long schedulestart = new Date().getTime();
		Log.print("Executing schedule...");
		t.scheduleAtFixedRate(new TimerTask() {
			private long lasttime = 0;
			private long thistime = 0;

			@Override
			public void run() {
				thistime = (long) (new Date().getTime() - schedulestart) / 1000;
				try {
					// configuration.getSchedule().executeStep(thistime,
					// lasttime, jobs, startConstraints, killConstraints,
					// metrics);
					configuration.getSchedule().executeStep(thistime, lasttime, jobs, metrics);
					lasttime = thistime;
				} catch (InvalidActivityException ex) {
					// schedule is finished
					t.cancel();
					s.release();
				} catch (Exception ex) {
					t.cancel();
					int retries = 0;
					Log.print("Waiting at most " + Constants.MaxWaitSecondsForScheduleKill + " seconds for Job threads to finish themselves. After that, they will be killed...");
					while (configuration.getSchedule().activeSubmitThreadCount() > 0 && retries < Constants.MaxWaitSecondsForScheduleKill) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException ex1) {}
						retries++;
						if (retries % 10 == 0)
							Log.print((Constants.MaxWaitSecondsForScheduleKill - retries) + " seconds left.", LogLevel.Verbose);
					}
					if (configuration.getSchedule().killSubmitThreads() > 0)
						Log.print("Running job threads on this machine have been killed. Since we cannot know the state of the jobs on the cluster,  consider running a cleanup before starting the next test!", LogLevel.Critical);
					scheduleException = ex;
					s.release();
				}
			}
		}, 0, 1000);
		s.acquire();
		if (scheduleException != null) {
			throw scheduleException;
		}
		Log.print("Done.");
		metrics.endPhase(Phase.ScheduleExecution);
	}

	/**
	 * Download output.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void DownloadOutput() throws IOException {
		metrics.startPhase(Phase.Download);
		for (ScheduleItem si : configuration.getSchedule().getItems()) {
			if (!si.getJobID().contains("builtin:")) {
				String hdfspath = Constants.HDFSOutputTemporaryPath + timestamp + "/" + Constants.HDFSJobOutputPrefix + si.getJobID() + "-" + si.getID() + "/";
				String localpath = FilenameUtils.concat(configuration.getSettings().getOutputBasePath(), Constants.HDFSJobOutputPrefix + si.getJobID() + "-" + si.getID() + "\\");
				try {
					Log.print("Downloading output data for item '" + si.getID() + "' (" + hdfspath + ") to '" + localpath + "'...");
					metrics.getDownloadMetrics().beginNewDownload(hdfspath, hdfs.getFileStatus(new Path(hdfspath)).getLen());
					hdfs.copyToLocalFile(new Path(hdfspath), new Path(localpath));
					metrics.getDownloadMetrics().endNewDownload();
					Log.print("Done.");
				} catch (FileNotFoundException ex) {
					Log.print("File not found. Does this item produce output to its designated output directory?", LogLevel.Important);
				}

			}
		}
		metrics.endPhase(Phase.Download);
	}

	/**
	 * Perform cleanup postprocessing.
	 *
	 * @param skipPP the skip pp
	 * @return An error String. If empty, everything worked fine, else it
	 * contains error details.
	 * @throws Exception the exception
	 */
	private void PerformCleanupPostprocessing(boolean skipPP) throws Exception {
		metrics.startPhase(Phase.CleanupPostprocessing);
		if (!skipPP) {
			// POST-PROCESSING
			HashMap<String, String> scripts = configuration.getSettings().getCleanupConfiguration() == null ? null : configuration.getSettings().getCleanupConfiguration().getScripts();
			if (scripts != null) {
				for (String key : scripts.keySet()) {
					if (key.equals("localhost") || key.startsWith("remote")) {
						String[] script = new String[0];
						String[] loc = new String[0];
						String rl = "<location>";
						try {
							if (key.equals("localhost")) {
								// execute script on localhost
								script = ExtensionMethods.ExtendWithBuiltinScripts(scripts.get(key)).split("\n");
								loc = new String[] { "", "", "localhost", "" };
								rl = "localhost";
							} else if (key.startsWith("remote")) {
								// execute remote
								String locString = scripts.get(key).split("\n")[0];
								loc = ExtensionMethods.splitHostString(locString);
								rl = loc[0] + "@" + loc[2] + ":" + loc[3];
								script = ExtensionMethods.ExtendWithBuiltinScripts(scripts.get(key).replace(locString + "\n", "")).split("\n");
							}
							// ScriptManager.executeScript(loc, script,
							// ExtensionMethods.getEnvironmentVariablesForSetupCleanupScripts(key.startsWith("remote"),
							// false), "cleanup script", true);
							new Script(loc, script, ScriptType.Cleanup, "", "", true);
						} catch (Exception ex) {
							if (configuration.getSettings().getSetupConfiguration().isResumeOnError()) {
								Log.print("Error during cleanup script execution on " + rl + ". Continuing with the next script (resumeOnError).", LogLevel.Critical);
								Log.print(ex);
							} else {
								Log.print("Error during cleanup script execution on " + rl + ". Aborting further postprocessing.", LogLevel.Critical);
								Log.print(ex);
							}
						}
					}
				}
			}
		}
		// CLEANUP
		// delete input data from HDFS
		if (!configuration.getSettings().isKeepInputInHDFS()) {
			Log.print("Deleting input data from HDFS (" + Constants.HDFSInputTemporaryPath + timestamp + ")...", LogLevel.Verbose);
			try {
				hdfs.delete(new Path(Constants.HDFSInputTemporaryPath + timestamp), true);
			} catch (NullPointerException ex) {
				// hdfs object not created -> error in previous phase
				throw new NullPointerException("HDFS connection not available because of previous errors. This is not important, since there is no need for cleanup.");
			}
			Log.print("Done.", LogLevel.Verbose);
		}
		// delete output data from HDFS
		if (!configuration.getSettings().isKeepOutputInHDFS()) {
			Log.print("Deleting output data from HDFS (" + Constants.HDFSOutputTemporaryPath + timestamp + ")...", LogLevel.Verbose);
			hdfs.delete(new Path(Constants.HDFSOutputTemporaryPath + timestamp), true);
			Log.print("Done.", LogLevel.Verbose);
		}
		metrics.endPhase(Phase.CleanupPostprocessing);
	}

	/**
	 * Generates the report.
	 *
	 * @throws Exception the exception
	 */
	private void GenerateReport() throws Exception {
		metrics.startPhase(Phase.ReportGeneration);
		// average upload time and rate
		Log.print("Average upload time for " + metrics.getUploadMetrics().getTotalCount() + " upload" + (metrics.getUploadMetrics().getTotalCount() != 1 ? "s" : "") + " was " + metrics.getUploadMetrics().getAverageUploadTime() + "ms.");
		Log.print("Average upload rate was " + ExtensionMethods.BytesToFormatted(metrics.getUploadMetrics().getAverageUploadRate()) + "/s (" + ExtensionMethods.BytesToFormatted(metrics.getUploadMetrics().getTotalBytes()) + " in " + metrics.getUploadMetrics().getTotalMillis() + "ms).");
		metrics.endPhase(Phase.ReportGeneration);
		Report.generate(FilenameUtils.concat(configuration.getSettings().getOutputBasePath(), Constants.ReportFolderName), metrics, hdfs);
		if (Log.isCreateLogfile()) {
			FileUtils.copyFile(new File(Log.getLogfilepath()), new File(new File(FilenameUtils.concat(configuration.getSettings().getOutputBasePath(), Constants.ReportFolderName)), Constants.LogFileName));
			Log.print("Copied logfile to reports folder.", LogLevel.Verbose);
		}
	}

	/**
	 * Checks for finished successful.
	 *
	 * @return Returns the finishedSuccessful.
	 */
	public boolean hasFinishedSuccessful() {
		return this.finishedSuccessful;
	}
}
