package de.timvinkemeier.haptics.extensions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.apache.hadoop.examples.WordCount.IntSumReducer;
import org.apache.hadoop.examples.WordCount.TokenizerMapper;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;

import de.timvinkemeier.haptics.builtins.LineBegin.LineBeginMapper;
import de.timvinkemeier.haptics.builtins.LineBegin.LineBeginReducer;
import de.timvinkemeier.haptics.exceptions.AmbiguousDefinitionException;
import de.timvinkemeier.haptics.exceptions.InvalidDefinitionException;
import de.timvinkemeier.haptics.exceptions.MissingDefinitionException;

// TODO: Auto-generated Javadoc
/**
 * The Class JarLoader.
 */
@SuppressWarnings("all")
public class JarLoader {

	/**
	 * Gets a mapper class from the given jar by looking for a class name that
	 * contains 'Mapper'. If none is found, a MissingDefinitionException is
	 * thrown. If more than one is found, an AmbiguousDefinitionException is
	 * thrown. Otherwise the class gets loaded and returned.
	 *
	 * @param jarpath The path to the jarfile that contains the class.
	 * @return The loaded mapper.
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws AmbiguousDefinitionException the ambiguous definition exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws MissingDefinitionException the missing definition exception
	 * @throws InvalidDefinitionException the invalid definition exception
	 * @throws URISyntaxException the uRI syntax exception
	 */
	public static Class<? extends Mapper> getMapperFromJar(String jarpath) throws FileNotFoundException, IOException, AmbiguousDefinitionException, ClassNotFoundException, MissingDefinitionException, InvalidDefinitionException, URISyntaxException {
		JarEntry mapper = null;
		Class<? extends Mapper> c;
		if (jarpath.contains("builtin:")) {
			// load builtin mapper
			if (jarpath.toLowerCase().equals("builtin:linebegin")) {
				c = (Class<? extends Mapper>) LineBeginMapper.class; // getClassByName(FilenameUtils.concat(ExtensionMethods.GetApplicationDirectory().getAbsolutePath(),
																		// "HapticsBuiltIns.jar"),
																		// "de.timvinkemeier.haptics.builtins.LineBegin$LineBeginMapper");
			} else if (jarpath.toLowerCase().equals("builtin:wordcount")) {
				c = (Class<? extends Mapper>) TokenizerMapper.class; // getClassByName(FilenameUtils.concat(ExtensionMethods.GetApplicationDirectory().getAbsolutePath(),
																		// "HapticsBuiltIns.jar"),
																		// "org.apache.hadoop.examples.WordCount$TokenizerMapper");
			} else {
				throw new InvalidDefinitionException("Unsupported builtin definition '" + jarpath + "'.");
			}
		} else {
			ClassLoader cl = new URLClassLoader(new URL[] { new File(jarpath).toURL() });
			for (JarEntry entry : getClassesFromJar(jarpath)) {
				// find class that contains but is not equal to "Mapper"
				boolean contains = entry.getName().replaceAll("/", "\\.").contains("Mapper.class");
				boolean mapreduce = entry.getName().replaceAll("/", "\\.").equals("org.apache.hadoop.mapreduce.Mapper.class");
				boolean mapred = entry.getName().replaceAll("/", "\\.").equals("org.apache.hadoop.mapred.Mapper.class");
				if (contains && !mapred && !mapreduce) {
					if (Log.debug) {
						Log.print("Found Mapper class '" + entry.getName().replaceAll("/", "\\.") + "'", LogLevel.Verbose);
					}
					if (mapper == null) {
						mapper = entry;
					} else {
						// second one with "Mapper" found -> error
						throw new AmbiguousDefinitionException("More than one class with name containing 'Mapper' found in jar '" + jarpath + "' (" + mapper.getName().replaceAll("/", "\\.") + " and " + entry.getName().replaceAll("/", "\\.") + ").");
					}
				}
			}
			if (mapper == null) {
				throw new MissingDefinitionException("No mapper class found in '" + jarpath + "'.");
			}
			// ClassLoader cl = new URLClassLoader(new URL[] { new
			// File(jarpath).toURL() });
			c = (Class<Mapper>) cl.loadClass(mapper.getName().replaceAll("/", "\\.").replace(".class", ""));
			Log.print("Class '" + c.getName() + "' loaded from jar '" + jarpath + "' via implicit definition.", LogLevel.Verbose);
		}
		return c;
	}

	/**
	 * Gets a reducer class from the given jar by looking for a class name that
	 * contains 'Reducer'. If none is found, a MissingDefinitionException is
	 * thrown. If more than one is found, an AmbiguousDefinitionException is
	 * thrown. Otherwise the class gets loaded and returned.
	 *
	 * @param jarpath The path to the jarfile that contains the class.
	 * @return The loaded reducer.
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws AmbiguousDefinitionException the ambiguous definition exception
	 * @throws ClassNotFoundException the class not found exception
	 * @throws MissingDefinitionException the missing definition exception
	 * @throws InvalidDefinitionException the invalid definition exception
	 * @throws URISyntaxException the uRI syntax exception
	 */
	public static Class<? extends Reducer> getReducerFromJar(String jarpath) throws FileNotFoundException, IOException, AmbiguousDefinitionException, ClassNotFoundException, MissingDefinitionException, InvalidDefinitionException, URISyntaxException {
		JarEntry reducer = null;
		Class<? extends Reducer> c;
		if (jarpath.contains("builtin:")) {
			// load builtin reducer
			if (jarpath.toLowerCase().equals("builtin:linebegin")) {
				c = (Class<? extends Reducer>) LineBeginReducer.class; // getClassByName(FilenameUtils.concat(ExtensionMethods.GetApplicationDirectory().getAbsolutePath(),
																		// "HapticsBuiltIns.jar"),
																		// "de.timvinkemeier.haptics.builtins.LineBegin$LineBeginReducer");
				// c = (Class<Reducer>)
				// Class.forName("de.timvinkemeier.haptics.builtins.LineBegin$LineBeginReducer");
			} else if (jarpath.toLowerCase().equals("builtin:wordcount")) {
				c = (Class<? extends Reducer>) IntSumReducer.class;// getClassByName(FilenameUtils.concat(ExtensionMethods.GetApplicationDirectory().getAbsolutePath(),
																	// "HapticsBuiltIns.jar"),
																	// "org.apache.hadoop.examples.WordCount$IntSumReducer");
				// c = (Class<Reducer>)
				// Class.forName("org.apache.hadoop.examples.WordCount$IntSumReducer");
			} else {
				throw new InvalidDefinitionException("Unsupported builtin definition '" + jarpath + "'.");
			}
		} else {
			for (JarEntry entry : getClassesFromJar(jarpath)) {
				// find class that contains but is not equal to "Mapper"
				boolean contains = entry.getName().replaceAll("/", "\\.").contains("Reducer.class");
				boolean mapreduce = entry.getName().replaceAll("/", "\\.").equals("org.apache.hadoop.mapreduce.Reducer.class");
				boolean mapred = entry.getName().replaceAll("/", "\\.").equals("org.apache.hadoop.mapred.Reducer.class");
				if (contains && !mapred && !mapreduce) {
					if (Log.debug) {
						Log.print("Found Reducer class '" + entry.getName().replaceAll("/", "\\.") + "'", LogLevel.Verbose);
					}
					if (reducer == null) {
						reducer = entry;
					} else {
						// second one with "Reducer" found -> error
						throw new AmbiguousDefinitionException("More than one class with name containing 'Reducer' found in jar '" + jarpath + "'.");
					}
				}
			}
			if (reducer == null) {
				throw new MissingDefinitionException("No reducer class found in '" + jarpath + "'.");
			}
			ClassLoader cl = new URLClassLoader(new URL[] { new File(jarpath).toURL() });
			c = (Class<Reducer>) cl.loadClass(reducer.getName().replaceAll("/", "\\.").replace(".class", ""));
			Log.print("Class '" + c.getName() + "' loaded from jar '" + jarpath + "' via implicit definition.", LogLevel.Verbose);
		}
		return c;
	}

	/**
	 * Gets a list of JarEntries representing classes contained in the given
	 * jarfile.
	 * 
	 * @param jarpath
	 *            The path to the jarfile.
	 * @return The classes found in the jar.
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static List<JarEntry> getClassesFromJar(String jarpath) throws FileNotFoundException, IOException {
		ArrayList<JarEntry> list = new ArrayList<>();
		JarInputStream jf = new JarInputStream(new FileInputStream(jarpath));
		JarEntry entry = jf.getNextJarEntry();
		while (entry != null) {
			if (entry.getName().endsWith(".class")) {
				list.add(entry);
				// Log.print("Class found in '" + jarpath + "': " +
				// entry.getName().replaceAll("/", "\\.").replace(".class", ""),
				// LogLevel.Verbose);
			}
			entry = jf.getNextJarEntry();
		}
		return list;
	}

	/**
	 * Loads the class with the given name from the given jarfile.
	 * 
	 * @param jarPath
	 *            The path to the jarfile that contains the class.
	 * @param className
	 *            The name of the class.
	 * @return The loaded class.
	 * @throws MalformedURLException
	 *             the malformed url exception
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 */
	public static Class<?> getClassByName(String jarPath, String className) throws MalformedURLException, ClassNotFoundException {
		URL url = new File(jarPath).toURL(); // new
												// File(jarPath).getParentFile()
												// == null ? new
												// File(jarPath).toURL() : new
												// File(jarPath).getParentFile().toURL();
		if (Log.debug) {
			Log.print("Trying to load class '" + className + "' from path '" + url + "'", LogLevel.Verbose);
		}
		// ClassLoader cl = new URLClassLoader(new URL[] { url });
		ClassLoader cl = URLClassLoader.getSystemClassLoader();

		Class<?> c = cl.loadClass(className);
		Log.print("Class '" + c.getName() + "' loaded from jar '" + jarPath + "' via name.", LogLevel.Verbose);
		return c;
	}

	/**
	 * Gets the output key class for the given reducer.
	 *
	 * @param rc the rc
	 * @return the output key class
	 * @throws MissingDefinitionException the missing definition exception
	 */
	public static Class<?> getOutputKeyClass(Class<?> rc) throws MissingDefinitionException {
		Class<?> rcok = null;
		try {
			Type[] args = ((ParameterizedType) rc.getGenericSuperclass()).getActualTypeArguments();
			rcok = (Class) args[2];
		} catch (Exception ex) {}
		if (rcok != null) {
			return rcok;
		} else {
			throw new MissingDefinitionException("Output Key class could not be resolved.");
		}
	}

	/**
	 * Gets the output value class for the given reducer.
	 *
	 * @param rc the rc
	 * @return the output value class
	 * @throws MissingDefinitionException the missing definition exception
	 */
	public static Class<?> getOutputValueClass(Class<?> rc) throws MissingDefinitionException {
		Class<?> rcov = null;
		try {
			rcov = (Class) ((ParameterizedType) rc.getGenericSuperclass()).getActualTypeArguments()[3];
		} catch (Exception ex) {}
		if (rcov != null) {
			return rcov;
		} else {
			throw new MissingDefinitionException("Output Value class could not be resolved.");
		}
	}

	/**
	 * Gets the partitioner from jar.
	 * 
	 * @param jarPath
	 *            the jar path
	 * @return the partitioner from jar
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws MissingDefinitionException
	 *             the missing definition exception
	 * @throws AmbiguousDefinitionException
	 *             the ambiguous definition exception
	 * @throws FileNotFoundException
	 *             the file not found exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Class<Partitioner> getPartitionerFromJar(String jarPath) throws ClassNotFoundException, MissingDefinitionException, AmbiguousDefinitionException, FileNotFoundException, IOException {
		JarEntry partitioner = null;
		Class<Partitioner> c;
		// no partitioner in builtins
		if (jarPath.toLowerCase().contains("builtin:")) {
			throw new MissingDefinitionException("No partitioner for builtins. This exception is thrown by design.");
		}

		for (JarEntry entry : getClassesFromJar(jarPath)) {
			// find class that contains but is not equal to "Mapper"
			boolean contains = entry.getName().replaceAll("/", "\\.").contains("Partitioner.class");
			boolean mapreduce = entry.getName().replaceAll("/", "\\.").equals("org.apache.hadoop.mapreduce.Partitioner.class");
			boolean mapred = entry.getName().replaceAll("/", "\\.").equals("org.apache.hadoop.mapred.Partitioner.class");
			if (contains && !mapred && !mapreduce) {
				if (Log.debug) {
					Log.print("Found Partitioner class '" + entry.getName().replaceAll("/", "\\.") + "'", LogLevel.Verbose);
				}
				if (partitioner == null) {
					partitioner = entry;
				} else {
					// second one with "Partitioner" found -> error
					throw new AmbiguousDefinitionException("More than one class with name containing 'Partitioner' found in jar '" + jarPath + "'.");
				}
			}
		}
		if (partitioner == null) {
			throw new MissingDefinitionException("No partitioner class found in '" + jarPath + "'.");
		}
		ClassLoader cl = new URLClassLoader(new URL[] { new File(jarPath).toURL() });
		c = (Class<Partitioner>) cl.loadClass(partitioner.getName().replaceAll("/", "\\.").replace(".class", ""));
		Log.print("Class '" + c.getName() + "' loaded from jar '" + jarPath + "' via implicit definition.", LogLevel.Verbose);
		return c;
	}
}
