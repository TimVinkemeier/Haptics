package de.timvinkemeier.haptics.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import de.timvinkemeier.haptics.configuration.serialization.JodaPropertyConstructor;
import de.timvinkemeier.haptics.configuration.serialization.JodaTimeRepresenter;
import de.timvinkemeier.haptics.exceptions.InvalidDefinitionException;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;
import de.timvinkemeier.haptics.extensions.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class TestConfiguration.
 */
public class TestConfiguration {
	
	/** The settings. */
	private TestSettings settings;
	
	/** The job configurations. */
	private List<JobConfiguration> jobConfigurations;
	
	/** The cluster configuration. */
	private ClusterConfiguration clusterConfiguration;
	
	/** The schedule. */
	private Schedule schedule;
	
	/** The test variables. */
	private HashMap<String, Object> testVariables;
	
	/** The config variables. */
	private HashMap<String, Object> configVariables;

	/**
	 * Instantiates a new test configuration.
	 */
	public TestConfiguration() {
		super();
	}

	/**
	 * Loads the given YAML-represented TestConfiguration.
	 *
	 * @param filepath The path to the YAML-file to be loaded.
	 * @return The deserialized TestConfiguration.
	 * @throws InvalidDefinitionException the invalid definition exception
	 * @throws FileNotFoundException the file not found exception
	 */
	public static TestConfiguration Load(String filepath) throws InvalidDefinitionException, FileNotFoundException {
		Representer rep = new Representer();
		rep.addClassTag(TestConfiguration.class, new Tag("Test"));
		rep.addClassTag(JobConfiguration.class, new Tag("JobConfiguration"));
		rep.addClassTag(ScheduleItem.class, new Tag("Item"));
		Yaml yaml = new Yaml(new JodaPropertyConstructor(), rep);
		TestConfiguration tc = yaml.loadAs(new FileInputStream(filepath), TestConfiguration.class);
		tc.validateNames();
		return tc;
	}

	/**
	 * Load from string.
	 * 
	 * @param data
	 *            the data
	 * @return the test configuration
	 * @throws InvalidDefinitionException
	 *             the invalid definition exception
	 */
	public static TestConfiguration LoadFromString(String data) throws InvalidDefinitionException {
		Representer rep = new Representer();
		rep.addClassTag(TestConfiguration.class, new Tag("Test"));
		rep.addClassTag(JobConfiguration.class, new Tag("JobConfiguration"));
		rep.addClassTag(ScheduleItem.class, new Tag("Item"));
		Yaml yaml = new Yaml(new JodaPropertyConstructor(), rep);
		TestConfiguration tc = yaml.loadAs(data, TestConfiguration.class);
		tc.validateNames();
		return tc;
	}

	/**
	 * Saves this TestConfiguration as YAML to the given file.
	 * 
	 * @param filepath
	 *            The filepath.
	 * @param includeComments
	 *            If true, the YAML-text will include comments describing the
	 *            written elements.
	 */
	public void Save(String filepath, boolean includeComments) {
		try {
			OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(filepath), "UTF-8");
			osw.write(SaveToString(includeComments));
			osw.close();
		} catch (Exception ex) {
			Log.print(ex);
		}
	}

	/**
	 * Save to string.
	 *
	 * @param includeComments the include comments
	 * @return the string
	 */
	public String SaveToString(boolean includeComments) {
		Representer rep = new JodaTimeRepresenter();
		rep.addClassTag(TestConfiguration.class, new Tag("Test"));
		rep.addClassTag(JobConfiguration.class, new Tag("JobConfiguration"));
		rep.addClassTag(ScheduleItem.class, new Tag("Item"));
		DumperOptions dop = new DumperOptions();
		dop.setDefaultFlowStyle(FlowStyle.BLOCK);
		Yaml yaml = new Yaml(new Constructor(TestConfiguration.class), rep, dop);
		String text = yaml.dump(this);
		// cut JodaTimes to simple dates
		text = text.replaceAll("T\\d\\d:\\d\\d:\\d\\d.\\d\\d\\dZ", "");
		// remove !<Test> tag (unnecessary)
		text = text.replaceFirst("!<Test>", "");
		if (includeComments) {
			text = CommentYaml(text);
		}
		return text;
	}

	/**
	 * Comments a YAML-serialized TestConfiguration.
	 * 
	 * @param text
	 *            The Output to comment.
	 * @return The commented text.
	 */
	private String CommentYaml(String text) {
		String ntext = "# Haptics TestConfiguration file (automatically generated)";
		ntext += "# For further information refer to the documentation.\n";

		ntext += text;
		String ccc = "\n# The clusterConfiguration section contains the following info:\n";
		ccc += "# ---------------------------------------------------------------------------------------------------------------------\n";
		ccc += "# configurationFolder - The folder in which the core-site.xml, mapred-site.xml and hdfs-site.xml files are located\n";
		ccc += "# jobTrackerLocation - [usage NOT recommended] If you don't have the configuration files for the cluster, you can use this to specify the location of the JobTracker (like 1.2.3.4:9000)\n";
		ccc += "# nameNodeLocation - [usage NOT recommended] If you don't have the configuration files for the cluster, you can use this to specify the location of the NameNode (like hdfs://1.2.3.4:9000)\n";
		ccc += "# ---------------------------------------------------------------------------------------------------------------------\n";
		ccc += "clusterConfiguration:";
		ntext = ntext.replaceFirst("clusterConfiguration:", ccc);

		String jcc = "\n# The jobConfigurations section contains configuration info as a list. Each item contains the following info:\n";
		jcc += "# ---------------------------------------------------------------------------------------------------------------------\n";
		jcc += "# additionalSettings - A HashMap with additional settings. Please refer to the documentation for further info.\n";
		jcc += "# jobJarPath - The full path to the jobs .jar-file\n";
		jcc += "# jobID - The custom unique jobid to link this configuration to the schedule.\n";
		jcc += "# mapperClassName - The name of the job's mapper class. Can be omitted if there is only one class in the jar that contains 'Mapper' in its name - Haptics will find this automatically.\n";
		jcc += "# reducerClassName - See mapperClassName\n";
		jcc += "# toolrunnerClassName - Please refer to the documentation for further information on this advanced feature.\n";
		jcc += "# toolrunnerArguments - Please refer to the documentation for further information on this advanced feature.\n";
		jcc += "# inputPaths - A list of file and/or folder paths to serve as the input data for the job.\n";
		jcc += "# ---------------------------------------------------------------------------------------------------------------------\n";
		jcc += "jobConfigurations:";
		ntext = ntext.replaceFirst("jobConfigurations:", jcc);

		String sc = "\n# The schedule section contains scheduling items as a list. Each item contains the following info:\n";
		sc += "# ---------------------------------------------------------------------------------------------------------------------\n";
		sc += "# additionalSettings - A HashMap with additional settings. Please refer to the documentation for further info. (Note that these are different from JobConfiguration.additionalSettings)\n";
		sc += "# starttime - The time in seconds after the beginning of the test (t=0) after which the job should be submitted. (positive Integer)\n";
		sc += "# endtime - The time in seconds after the beginning of the test (t=0) after which the job should be killed. (positive Integer greater than starttime or -1 for 'never kill')\n";
		sc += "# jobID - The ID of a job configured under jobConfigurations.\n";
		sc += "# ID - The custom unique ID to be able to refer to this schedule item.\n";
		sc += "# ---------------------------------------------------------------------------------------------------------------------\n";
		sc += "schedule:";
		ntext = ntext.replaceFirst("schedule:", sc);

		String setc = "\n# The settings section contains the following info:\n";
		setc += "# ---------------------------------------------------------------------------------------------------------------------\n";
		setc += "# author - The name of the author of this test.\n";
		setc += "# creationDate - The Date this test was initially created (Format: yyyy-mm-dd).\n";
		setc += "# description - A description of this test.\n";
		setc += "# lastModificationDate - The Date this test was modified for the last time (Format: yyyy-mm-dd).\n";
		setc += "# name - The name of this test.\n";
		setc += "# version - A version number for distinguishing different versions of this test. (or a custom string).\n";
		setc += "# keepInputInHDFS - If true, the input data will not be removed from HDFS after the schedule has finished.\n";
		setc += "# keepOutputInHDFS- Equivalent to keepInputInHDFS.\n";
		setc += "# outputBasePath - The local path below which all output data will be saved.\n";
		setc += "# setupConfiguration - Contains the scripts for the setup and preprocessing phase. Refer to the documentation for further information.\n";
		setc += "# cleanupConfiguration - Equivalent to setupConfiguration for the cleanup and postprocessing phase.\n";
		setc += "# ---------------------------------------------------------------------------------------------------------------------\n";
		setc += "settings:";
		ntext = ntext.replaceFirst("settings:", setc);

		String vc = "\n# The variables section contains the following info:\n";
		vc += "# ---------------------------------------------------------------------------------------------------------------------\n";
		vc += "# testVariables - Variable definitions that define multiple test runs. Refer to the documentation for further information.\n";
		vc += "# configVariables - Variable definitions that reduce writing effort by expanding marked definitions. Refer to the documentation for further information\n";
		vc += "# ---------------------------------------------------------------------------------------------------------------------\n";
		vc += ntext.indexOf("configVariables:") > ntext.indexOf("testVariables:") ? "testVariables:" : "configVariables:";
		ntext = ntext.replaceFirst(ntext.indexOf("configVariables:") > ntext.indexOf("testVariables:") ? "configVariables:" : "configVariables:", vc);
		return ntext;
	}

	/**
	 * Validate names.
	 * 
	 * @throws InvalidDefinitionException
	 *             the invalid definition exception
	 */
	public void validateNames() throws InvalidDefinitionException {
		// no whitespace allowed in IDs
		for (JobConfiguration jc : jobConfigurations) {
			if (ExtensionMethods.containsWhitespace(jc.getJobID())) {
				throw new InvalidDefinitionException("Job IDs may not contain whitespace (@ item '" + jc.getJobID() + "').");
			}
		}
		for (ScheduleItem si : schedule.getItems()) {
			if (ExtensionMethods.containsWhitespace(si.getID())) {
				throw new InvalidDefinitionException("Schedule item IDs may not contain whitespace (@ item '" + si.getID() + "').");
			}
		}
	}

	/**
	 * Gets the job configurations.
	 *
	 * @return Returns the jobConfigurations.
	 */
	public List<JobConfiguration> getJobConfigurations() {
		return this.jobConfigurations;
	}

	/**
	 * Sets the job configurations.
	 *
	 * @param jobConfigurations The jobConfigurations to set.
	 */
	public void setJobConfigurations(List<JobConfiguration> jobConfigurations) {
		this.jobConfigurations = jobConfigurations;
	}

	/**
	 * Gets the cluster configuration.
	 *
	 * @return Returns the clusterConfiguration.
	 */
	public ClusterConfiguration getClusterConfiguration() {
		return this.clusterConfiguration;
	}

	/**
	 * Sets the cluster configuration.
	 *
	 * @param clusterConfiguration The clusterConfiguration to set.
	 */
	public void setClusterConfiguration(ClusterConfiguration clusterConfiguration) {
		this.clusterConfiguration = clusterConfiguration;
	}

	/**
	 * Gets the schedule.
	 *
	 * @return Returns the schedule.
	 */
	public Schedule getSchedule() {
		return this.schedule;
	}

	/**
	 * Sets the schedule.
	 *
	 * @param schedule The schedule to set.
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * Gets the settings.
	 *
	 * @return Returns the settings.
	 */
	public TestSettings getSettings() {
		return this.settings;
	}

	/**
	 * Sets the settings.
	 *
	 * @param settings The settings to set.
	 */
	public void setSettings(TestSettings settings) {
		this.settings = settings;
	}

	/**
	 * Gets the test variables.
	 *
	 * @return Returns the testVariables.
	 */
	public HashMap<String, Object> getTestVariables() {
		return this.testVariables;
	}

	/**
	 * Sets the test variables.
	 *
	 * @param testVariables The testVariables to set.
	 */
	public void setTestVariables(HashMap<String, Object> testVariables) {
		this.testVariables = testVariables;
	}

	/**
	 * Gets the config variables.
	 *
	 * @return Returns the configvariables.
	 */
	public HashMap<String, Object> getConfigVariables() {
		return this.configVariables;
	}

	/**
	 * Sets the config variables.
	 *
	 * @param configvariables The configvariables to set.
	 */
	public void setConfigVariables(HashMap<String, Object> configvariables) {
		this.configVariables = configvariables;
	}

	/**
	 * Gets a template.
	 * 
	 * @return the template
	 */
	public static TestConfiguration getTemplate() {
		TestConfiguration tc = new TestConfiguration();
		tc.clusterConfiguration = ClusterConfiguration.getTemplate();
		tc.jobConfigurations = new ArrayList<JobConfiguration>();
		JobConfiguration jc = new JobConfiguration();
		jc.setAdditionalSettings(null);
		jc.setInputPaths(new ArrayList<String>());
		jc.getInputPaths().add("<inputpath1>");
		jc.setJobID("<jobID>");
		jc.setJobJarPath("<jobJarPath>");
		jc.setMapperClassName("");
		jc.setReducerClassName("");
		tc.jobConfigurations.add(jc);
		tc.schedule = new Schedule();
		ScheduleItem si = new ScheduleItem();
		si.setAdditionalSettings(null);
		si.setEndtime(-1);
		si.setStarttime(0);
		si.setID("<sID>");
		si.setJobID("<jobID>");
		tc.schedule.setItems(new ArrayList<ScheduleItem>());
		tc.schedule.getItems().add(si);
		tc.settings = TestSettings.getTemplate();
		tc.testVariables = new HashMap<>();
		tc.configVariables = new HashMap<>();
		return tc;
	}
}
