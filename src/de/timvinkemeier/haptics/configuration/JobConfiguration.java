package de.timvinkemeier.haptics.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.util.Tool;

import de.timvinkemeier.haptics.exceptions.AmbiguousDefinitionException;
import de.timvinkemeier.haptics.exceptions.InvalidDefinitionException;
import de.timvinkemeier.haptics.exceptions.MissingDefinitionException;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;
import de.timvinkemeier.haptics.extensions.JarLoader;

// TODO: Auto-generated Javadoc
/**
 * The Class JobConfiguration.
 */
@SuppressWarnings("all")
public class JobConfiguration {
	
	/** The job id. */
	private String jobID;
	
	/** The job jar path. */
	private String jobJarPath;
	
	/** The mapper class name. */
	private String mapperClassName;
	
	/** The reducer class name. */
	private String reducerClassName;
	
	/** The toolrunner class name. */
	private String toolrunnerClassName;
	
	/** The toolrunner arguments. */
	private String toolrunnerArguments;

	/** The input paths. */
	private List<String> inputPaths;
	
	/** The additional settings. */
	private HashMap<String, Object> additionalSettings;

	/**
	 * Instantiates a new job configuration.
	 */
	public JobConfiguration() {
		super();
	}

	/**
	 * Gets the mapper class.
	 *
	 * @return the mapper class
	 * @throws FileNotFoundException the file not found exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws AmbiguousDefinitionException the ambiguous definition exception
	 * @throws MissingDefinitionException the missing definition exception
	 * @throws InvalidDefinitionException the invalid definition exception
	 * @throws URISyntaxException the uRI syntax exception
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Mapper> getMapperClass() throws FileNotFoundException, ClassNotFoundException, IOException, AmbiguousDefinitionException, MissingDefinitionException, InvalidDefinitionException, URISyntaxException {
		if (ExtensionMethods.IsNullOrWhitespace(jobJarPath)) {
			throw new InvalidActivityException("jobJarPath has not been set for job '" + jobID + "'.");
		}
		if (ExtensionMethods.IsNullOrWhitespace(mapperClassName)) {
			// look for jar and get classes by name
			return JarLoader.getMapperFromJar(jobJarPath);
		} else {
			// look for jar and get specific class by name
			return (Class<Mapper>) JarLoader.getClassByName(jobJarPath, mapperClassName);
		}
	}

	/**
	 * Gets the reducer class.
	 *
	 * @return the reducer class
	 * @throws FileNotFoundException the file not found exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws AmbiguousDefinitionException the ambiguous definition exception
	 * @throws MissingDefinitionException the missing definition exception
	 * @throws InvalidDefinitionException the invalid definition exception
	 * @throws URISyntaxException the uRI syntax exception
	 */
	@SuppressWarnings("unchecked")
	public Class<? extends Reducer> getReducerClass() throws FileNotFoundException, ClassNotFoundException, IOException, AmbiguousDefinitionException, MissingDefinitionException, InvalidDefinitionException, URISyntaxException {
		if (ExtensionMethods.IsNullOrWhitespace(jobJarPath)) {
			throw new InvalidActivityException("jobJarPath has not been set for job '" + jobID + "'.");
		}
		if (ExtensionMethods.IsNullOrWhitespace(reducerClassName)) {
			// look for jar and get classes by name
			return JarLoader.getReducerFromJar(jobJarPath);
		} else {
			// look for jar and get specific class by name
			return (Class<Reducer>) JarLoader.getClassByName(jobJarPath, reducerClassName);
		}
	}

	/**
	 * Gets the toolrunner class.
	 *
	 * @return the toolrunner class
	 * @throws InvalidActivityException the invalid activity exception
	 * @throws MissingDefinitionException the missing definition exception
	 * @throws MalformedURLException the malformed url exception
	 * @throws ClassNotFoundException the class not found exception
	 */
	public Class<Tool> getToolrunnerClass() throws InvalidActivityException, MissingDefinitionException, MalformedURLException, ClassNotFoundException {
		if (ExtensionMethods.IsNullOrWhitespace(jobJarPath)) {
			throw new InvalidActivityException("jobJarPath has not been set for job '" + jobID + "'.");
		}
		if (!ExtensionMethods.IsNullOrWhitespace(toolrunnerClassName)) {
			// look for jar and get specific class by name
			return (Class<Tool>) JarLoader.getClassByName(jobJarPath, toolrunnerClassName);
		} else {
			throw new MissingDefinitionException("No toolrunnerClass defined.");
		}
	}

	/**
	 * Gets the job id.
	 *
	 * @return Returns the jobid.
	 */
	public String getJobID() {
		return this.jobID;
	}

	/**
	 * Sets the job id.
	 *
	 * @param jobid The jobid to set.
	 */
	public void setJobID(String jobid) {
		this.jobID = jobid;
	}

	/**
	 * Gets the additional settings.
	 *
	 * @return Returns the additionalSettings.
	 */
	public HashMap<String, Object> getAdditionalSettings() {
		return this.additionalSettings;
	}

	/**
	 * Sets the additional settings.
	 *
	 * @param additionalSettings The additionalSettings to set.
	 */
	public void setAdditionalSettings(HashMap<String, Object> additionalSettings) {
		this.additionalSettings = additionalSettings;
	}

	/**
	 * Gets the job jar path.
	 *
	 * @return Returns the jobJarPath.
	 */
	public String getJobJarPath() {
		return this.jobJarPath;
	}

	/**
	 * Sets the job jar path.
	 *
	 * @param jobJarPath The jobJarPath to set.
	 */
	public void setJobJarPath(String jobJarPath) {
		this.jobJarPath = jobJarPath;
	}

	/**
	 * Gets the mapper class name.
	 *
	 * @return Returns the mapperClassName.
	 */
	public String getMapperClassName() {
		return this.mapperClassName;
	}

	/**
	 * Sets the mapper class name.
	 *
	 * @param mapperClassName The mapperClassName to set.
	 */
	public void setMapperClassName(String mapperClassName) {
		this.mapperClassName = mapperClassName;
	}

	/**
	 * Gets the reducer class name.
	 *
	 * @return Returns the reducerClassName.
	 */
	public String getReducerClassName() {
		return this.reducerClassName;
	}

	/**
	 * Sets the reducer class name.
	 *
	 * @param reducerClassName The reducerClassName to set.
	 */
	public void setReducerClassName(String reducerClassName) {
		this.reducerClassName = reducerClassName;
	}

	/**
	 * Gets the input paths.
	 *
	 * @return Returns the inputPaths.
	 */
	public List<String> getInputPaths() {
		return this.inputPaths;
	}

	/**
	 * Sets the input paths.
	 *
	 * @param inputPaths The inputPaths to set.
	 */
	public void setInputPaths(List<String> inputPaths) {
		this.inputPaths = inputPaths;
	}

	/**
	 * Gets the toolrunner class name.
	 *
	 * @return Returns the toolrunnerClassName.
	 */
	public String getToolrunnerClassName() {
		return this.toolrunnerClassName;
	}

	/**
	 * Sets the toolrunner class name.
	 *
	 * @param toolrunnerClassName The toolrunnerClassName to set.
	 */
	public void setToolrunnerClassName(String toolrunnerClassName) {
		this.toolrunnerClassName = toolrunnerClassName;
	}

	/**
	 * Gets the toolrunner arguments.
	 *
	 * @return Returns the toolrunnerArguments.
	 */
	public String getToolrunnerArguments() {
		return this.toolrunnerArguments;
	}

	/**
	 * Sets the toolrunner arguments.
	 *
	 * @param toolrunnerArguments The toolrunnerArguments to set.
	 */
	public void setToolrunnerArguments(String toolrunnerArguments) {
		this.toolrunnerArguments = toolrunnerArguments;
	}

}
