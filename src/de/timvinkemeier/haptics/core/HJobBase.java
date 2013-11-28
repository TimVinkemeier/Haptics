package de.timvinkemeier.haptics.core;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import com.sun.el.parser.ParseException;

import de.timvinkemeier.haptics.configuration.ExecutionState;
import de.timvinkemeier.haptics.configuration.JobConfiguration;
import de.timvinkemeier.haptics.configuration.ScheduleItem;
import de.timvinkemeier.haptics.exceptions.AmbiguousDefinitionException;
import de.timvinkemeier.haptics.exceptions.InvalidDefinitionException;
import de.timvinkemeier.haptics.exceptions.MissingDefinitionException;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;
import de.timvinkemeier.haptics.extensions.JarLoader;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class HJobBase.
 */
public abstract class HJobBase implements Runnable {
	
	/** The execution state. */
	public ExecutionState executionState = ExecutionState.NotStarted;
	
	/** The si. */
	protected ScheduleItem si;
	
	/** The pre script. */
	protected Script preScript = null;
	
	/** The post script. */
	protected Script postScript = null;

	/**
	 * Instantiates a new h job base.
	 */
	public HJobBase() {
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public abstract void run();

	/**
	 * From job configuration.
	 *
	 * @param jc the jc
	 * @param conf the conf
	 * @param si the si
	 * @param timestamp the timestamp
	 * @return the h job base
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws MissingDefinitionException the missing definition exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InvalidDefinitionException the invalid definition exception
	 * @throws URISyntaxException the uRI syntax exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 */
	@SuppressWarnings("rawtypes")
	public static HJobBase fromJobConfiguration(JobConfiguration jc, Configuration conf, ScheduleItem si, String timestamp) throws IOException, MissingDefinitionException, ClassNotFoundException, InvalidDefinitionException, URISyntaxException, InstantiationException, IllegalAccessException {
		boolean mapper, reducer, tool = false;
		HJobBase base = null;

		// job mapper, reducer, toolrunnerClass
		Class<? extends Mapper> mc = null;
		Class<? extends Reducer> rc = null;
		Class<Tool> tc = null;
		Exception lastex = null;

		// ####################################################
		// Try to load Mapper/Reducer/ToolrunnerClass
		// ####################################################
		try {
			mc = jc.getMapperClass();
			mapper = true;
		} catch (MissingDefinitionException ex) {
			mapper = false;
			lastex = ex;
		} catch (AmbiguousDefinitionException ex) {
			mapper = false;
			lastex = ex;
		}

		try {
			rc = jc.getReducerClass();
			reducer = true;
		} catch (MissingDefinitionException ex) {
			reducer = false;
			lastex = ex;
		} catch (AmbiguousDefinitionException ex) {
			reducer = false;
			lastex = ex;
		}

		try {
			tc = jc.getToolrunnerClass();
			tool = true;
		} catch (MissingDefinitionException ex) {
			tool = false;
		}

		if (tool) {
			// ####################################################
			// create HToolJob with toolrunnerclass and arguments
			// ####################################################
			List<String> parList = new ArrayList<>();
			if (!ExtensionMethods.IsNullOrWhitespace(jc.getToolrunnerArguments())) {
				String args = jc.getToolrunnerArguments().trim();
				if (jc.getInputPaths() != null && !jc.getInputPaths().isEmpty() && !ExtensionMethods.IsNullOrWhitespace(jc.getInputPaths().get(0))) {
					if (jc.getInputPaths().get(0).startsWith("hdfs:")) {
						args = args.replaceAll("<in>", jc.getInputPaths().get(0).replace("hdfs:", ""));
					} else {
						String rep = "";
						if (si.getOverrideInputPaths().size() > 0) {
							// input paths overridden
							rep = Constants.HDFSInputTemporaryPath + timestamp + "/" + Constants.HDFSJobInputPrefix + si.getJobID() + "-" + si.getID();
						} else {
							// input from JobConfiguration
							rep = Constants.HDFSInputTemporaryPath + timestamp + "/" + Constants.HDFSJobInputPrefix + jc.getJobID();
						}
						args = args.replaceAll("<in>", rep);
					}
				}
				args = args.replaceAll("<out>", Constants.HDFSOutputTemporaryPath + timestamp + "/" + Constants.HDFSJobOutputPrefix + jc.getJobID() + "-" + si.getID());

				String regExp = "\"(\\\"|[^\"])*?\"|[^ ]+";
				Pattern pattern = Pattern.compile(regExp, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(args);
				while (matcher.find()) {
					if (!ExtensionMethods.IsNullOrWhitespace(matcher.group()))
						parList.add(matcher.group());
				}
			}
			HToolJob htj = new HToolJob(new JobConf(conf), si, tc.newInstance(), parList.toArray(new String[parList.size()]));
			Log.print("ToolJob created for scheduleItem '" + si.getID() + "' (JobID: '" + jc.getJobID() + "', Class: '" + tc.getName() + "', Arguments: '" + ExtensionMethods.StringArrayToString(parList.toArray(new String[parList.size()])) + "').", LogLevel.Info);
			base = htj;
		} else if (mapper || reducer) {
			// ####################################################
			// create HMRJob with mapper and/or Reducer
			// ####################################################
			Job job = new Job(conf);
			job.setJobName(si.getID());
			Class<?> okc = null;
			Class<?> ovc = null;
			Class<?> mokc = null;
			Class<?> movc = null;
			job.setMapperClass(mc);
			job.setReducerClass(rc);
			if (reducer) {
				okc = JarLoader.getOutputKeyClass(rc);
				ovc = JarLoader.getOutputValueClass(rc);
				job.setOutputKeyClass(okc);
				job.setOutputValueClass(ovc);
			} else {
				okc = JarLoader.getOutputKeyClass(mc);
				ovc = JarLoader.getOutputValueClass(mc);
			}
			if (mapper) {
				job.setJarByClass(mc);
			} else {
				job.setJarByClass(rc);
			}
			mokc = JarLoader.getOutputKeyClass(mc);
			movc = JarLoader.getOutputValueClass(mc);
			job.setMapOutputKeyClass(mokc);
			job.setMapOutputValueClass(movc);

			// set additional parameters from additionalSettings
			if (jc.getAdditionalSettings() != null && !jc.getAdditionalSettings().isEmpty()) {
				for (String key : jc.getAdditionalSettings().keySet()) {
					String value = jc.getAdditionalSettings().get(key).toString();
					try {
						job.getConfiguration().set(key, value);
						Log.print("Parameter '" + key + "' set to '" + value + "' (item '" + si.getID() + "').", LogLevel.Verbose);
					} catch (Exception ex) {
						Log.print("Parameter '" + key + "' could not be set to '" + value + "' for item '" + si.getID() + "' (" + ex.getMessage() + ")", LogLevel.Important);
					}
				}
			}

			// override additional parameters from schedule item
			// additionalSettings
			if (si.getAdditionalSettings() != null && !si.getAdditionalSettings().isEmpty()) {
				for (String key : si.getAdditionalSettings().keySet()) {
					// parameters look like mapred.map.tasks
					if (key.contains(".")) {
						String value = si.getAdditionalSettings().get(key).toString();
						try {
							job.getConfiguration().set(key, value);
							Log.print("Override parameter '" + key + "' set to '" + value + "' (item '" + si.getID() + "').", LogLevel.Verbose);
						} catch (Exception ex) {
							Log.print("Override parameter '" + key + "' could not be set to '" + value + "' for item '" + si.getID() + "' (" + ex.getMessage() + ")", LogLevel.Important);
						}
					}
				}
			}
			if (si.getOverrideInputPaths().size() > 0) {
				// input paths overridden
				FileInputFormat.addInputPath(job, new Path(Constants.HDFSInputTemporaryPath + timestamp + "/" + Constants.HDFSJobInputPrefix + si.getJobID() + "-" + si.getID()));
			} else {
				// input from JobConfiguration
				FileInputFormat.addInputPath(job, new Path(Constants.HDFSInputTemporaryPath + timestamp + "/" + Constants.HDFSJobInputPrefix + jc.getJobID()));
			}

			// HDFS input paths
			// JobConfiguration input paths
			for (String p : jc.getInputPaths()) {
				if (p.startsWith("hdfs:")) {
					FileInputFormat.addInputPath(job, new Path(p.substring(5)));
				}
			}
			// overrideInput input paths
			List<String> oip = si.getOverrideInputPaths();
			for (String p : oip) {
				if (p.startsWith("hdfs:")) {
					FileInputFormat.addInputPath(job, new Path(p.substring(5)));
				}
			}

			// output path
			FileOutputFormat.setOutputPath(job, new Path(Constants.HDFSOutputTemporaryPath + timestamp + "/" + Constants.HDFSJobOutputPrefix + jc.getJobID() + "-" + si.getID()));
			Log.print("MRJob created for scheduleItem '" + si.getID() + "' (JobID: " + jc.getJobID() + ", Mapper: " + mc.getName() + ", Reducer: " + rc.getName() + " [MapOutputFormat: <" + mokc.getName().substring(mokc.getName().lastIndexOf(".") + 1) + ", " + movc.getName().substring(movc.getName().lastIndexOf(".") + 1) + ">, OutputFormat: <" + okc.getName().substring(okc.getName().lastIndexOf(".") + 1) + ", " + ovc.getName().substring(ovc.getName().lastIndexOf(".") + 1) + ">]).", LogLevel.Info);
			base = new HMRJob(job, si);
		} else {
			// ####################################################
			// definition incomplete
			// ####################################################
			throw new MissingDefinitionException("Missing definition for mapper, reducer and toolrunnerClass (item '" + si.getID() + "').", lastex);
		}

		// #########################################
		// Scripts
		// #########################################
		base.preScript = new Script(si.getPreScriptLocation(), si.getPreScript(), ScriptType.Pre, si.getID(), si.getJobID(), false);
		base.postScript = new Script(si.getPostScriptLocation(), si.getPostScript(), ScriptType.Post, si.getID(), si.getJobID(), false);
		return base;
	}

	/**
	 * Should start.
	 *
	 * @param currentStep the current step
	 * @param items the items
	 * @return true, if successful
	 * @throws ParseException the parse exception
	 * @throws InvalidDefinitionException the invalid definition exception
	 */
	public boolean shouldStart(long currentStep, Collection<HJobBase> items) throws ParseException, InvalidDefinitionException {
		return currentStep >= si.getStarttime() && executionState == ExecutionState.NotStarted && (si.getStartConstraints() != null ? si.getStartConstraints().evaluate(items) : true);
	}

	/**
	 * Should be killed.
	 *
	 * @param currentStep the current step
	 * @param items the items
	 * @return true, if successful
	 * @throws ParseException the parse exception
	 * @throws InvalidDefinitionException the invalid definition exception
	 */
	public boolean shouldBeKilled(long currentStep, Collection<HJobBase> items) throws ParseException, InvalidDefinitionException {
		return currentStep >= si.getEndtime() && si.getEndtime() >= 0 && !(executionState == ExecutionState.Finished || executionState == ExecutionState.Exception) && (si.getKillConstraints() != null ? si.getKillConstraints().evaluate(items) : true);
	}

	/**
	 * Gets the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return si.getID();
	}
}
