package de.timvinkemeier.haptics.core;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activity.InvalidActivityException;

import org.apache.commons.io.FileUtils;

import de.timvinkemeier.haptics.configuration.TestConfiguration;
import de.timvinkemeier.haptics.exceptions.InvalidDefinitionException;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class TestPreprocessor.
 */
public class TestPreprocessor {
	
	/** The testfilepath. */
	private String testfilepath;
	
	/** The tests. */
	private List<TestConfiguration> tests = new ArrayList<>();
	
	/** The current test. */
	private int currentTest = -1;

	/**
	 * Instantiates a new test preprocessor.
	 * 
	 * @param testfilepath
	 *            the testfilepath
	 * @throws InvalidDefinitionException
	 *             the invalid definition exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public TestPreprocessor(String testfilepath) throws InvalidDefinitionException, IOException {
		this.testfilepath = testfilepath;
		process();
	}

	/**
	 * Processes the testconfiguration and generates new ones by unfolding the
	 * variables.
	 * 
	 * @throws InvalidDefinitionException
	 *             the invalid definition exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	private void process() throws InvalidDefinitionException, IOException {
		TestConfiguration oConfig = TestConfiguration.Load(testfilepath);
		String oData = FileUtils.readFileToString(new File(testfilepath));
		HashMap<String, Object> testVariables = oConfig.getTestVariables();
		HashMap<String, Object> configvariables = oConfig.getConfigVariables();

		// remove variables from oData
		oData = oData.replaceAll("testVariables:", "");
		oData = oData.replaceAll("configVariables:", "");
		if (testVariables != null) {
			for (String key : testVariables.keySet()) {
				oData = oData.replaceAll("\\s+" + key + ":.*", "");
			}
		}
		if (configvariables != null) {
			for (String key : configvariables.keySet()) {
				oData = oData.replaceAll("\\s+" + key + ":.*", "");
			}
		}
		// replace configvars in original test data (oData)
		oData = replaceVariables(oData, configvariables);
		// cut off 3 strange characters at the beginning (whereever they come
		// from)
		if (!oData.substring(0, 1).matches("([a-zA-Z]|#)") && !oData.startsWith("n")) {
			oData = oData.substring(3);
		}

		if (testVariables == null) {
			testVariables = new HashMap<>();
			List<Object> list = new ArrayList<>();
			list.add("[1]");
			testVariables.put("<hapticsplaceholder>", list);
		}
		// create multiple tests using testVariables
		// first create an array that holds all possible values
		List<Object>[] vars = (List<Object>[]) new List<?>[testVariables.keySet().size()];
		String[] keys = new String[testVariables.keySet().size()];
		int i = 0;
		for (String key : testVariables.keySet()) {
			keys[i] = key;
			vars[i] = new ArrayList<Object>();
			if (testVariables.get(key) instanceof List<?>) {
				for (Object value : expandList((List<Object>) testVariables.get(key))) {
					vars[i].add(value);
				}
			} else {
				vars[i].add(testVariables.get(key));
			}
			i++;
		}

		// testcount is the overall count of tests that will be generated
		int testcount = 1;
		for (int j = 0; j < vars.length; j++) {
			testcount *= vars[j].size();
		}
		// counters hold the pointers for each variable list
		int[] counters = new int[vars.length];
		for (int j = 0; j < counters.length; j++) {
			counters[j] = 0;
		}
		// create tests
		for (int x = 0; x < testcount; x++) {
			// create new test from current counters
			HashMap<String, Object> testvar = new HashMap<>();
			for (int v = 0; v < vars.length; v++) {
				testvar.put(keys[v], vars[v].get(counters[v]));
			}
			String newData = replaceVariables(oData, testvar);
			if (Log.debug)
				Log.print("New Data generated:\n" + newData, LogLevel.Verbose);
			tests.add(TestConfiguration.LoadFromString(newData));
			// increment counters
			counters[counters.length - 1]++;
			for (int v = counters.length - 1; v > 0; v--) {
				if (counters[v] > vars[v].size() - 1) {
					counters[v - 1]++;
					counters[v] = 0;
				}
			}
		}
	}

	/**
	 * Gets the tests count.
	 * 
	 * @return the tests count
	 */
	public int getTestsCount() {
		return tests.size();
	}

	/**
	 * Checks for next.
	 * 
	 * @return true, if successful
	 */
	public boolean hasNext() {
		return (currentTest + 1) < tests.size();
	}

	/**
	 * Next.
	 * 
	 * @return the test configuration
	 * @throws InvalidActivityException
	 *             the invalid activity exception
	 */
	public TestConfiguration next() throws InvalidActivityException {
		currentTest++;
		if (currentTest < tests.size()) {
			return tests.get(currentTest);
		} else {
			throw new InvalidActivityException("No more tests!");
		}
	}

	/**
	 * Expand list.
	 *
	 * @param list the list
	 * @return the object[]
	 */
	private Object[] expandList(List<?> list) {
		List<Object> l = new ArrayList<>();
		for (Object elem : list) {
			try {
				Object[] expands = expandImplicitList((String) elem);
				for (Object o : expands) {
					l.add(o);
				}
			} catch (Exception ex) {
				// ignore, valid list element
				l.add(elem);
			}
		}
		return l.toArray();
	}

	/**
	 * Expand implicit list.
	 * 
	 * @param value
	 *            the value
	 * @return the object[]
	 */
	private Object[] expandImplicitList(String value) {
		try {
			if (value.contains("..")) {
				value = value.replaceAll("\\s", "");
				String[] parts = value.split("\\.\\.");
				char start = value.charAt(0);
				char end = value.charAt(3);
				int istart = 0;
				int iend = 0;
				boolean numbers = false;
				try {
					istart = Integer.parseInt(parts[0]);
					iend = Integer.parseInt(parts[1]);
					numbers = true;
				} catch (Exception ex) {
					numbers = false;
				}
				if (numbers) {
					if (istart > iend) {
						throw new InvalidParameterException("The given string is not an implicit list (start > end in '" + value + ").");
					}
					List<Integer> ints = new ArrayList<>();
					for (int i = istart; i <= iend; i++) {
						ints.add(i);
					}
					return ints.toArray();
				} else {
					List<Character> chars = new ArrayList<>();
					if ((int) start > (int) end) {
						throw new InvalidParameterException("The given string is not an implicit list (start > end in '" + value + ").");
					}
					for (int i = (int) start; i <= (int) end; i++) {
						if (Character.isLetter((char) i))
							chars.add((char) i);
					}
					return chars.toArray();
				}
			}
		} catch (Exception ex) {
			throw new InvalidParameterException("The given string '" + value + "' is not an implicit list.");
		}
		return null;
	}

	/**
	 * Replaces the given variables with their values, also unfolding list
	 * variables.
	 * 
	 * @param data
	 *            the data
	 * @param variables
	 *            the variables
	 * @return the string
	 */
	private String replaceVariables(String data, HashMap<String, Object> variables) {
		if (!(variables == null)) {
			for (String key : variables.keySet()) {
				if (variables.get(key) instanceof List<?>) {
					// multiply replace
					Object[] elements = expandList(((List<?>) variables.get(key)));
					String replacement = "";
					for (Object elem : elements) {
						String rep = elem.toString().replace("$", "\\$").replace("{", "\\{").replace("}", "\\}");
						replacement += "\n$1" + rep + "$3";
					}
					if (replacement.startsWith("\n"))
						replacement = replacement.substring(1);
					Pattern p = Pattern.compile("^(.*)(\\$\\{" + key + "\\})(.*)$", Pattern.UNIX_LINES | Pattern.MULTILINE);
					Matcher m = p.matcher(data);
					data = m.replaceAll(replacement);
				} else {
					// simple replace
					data = data.replaceAll("(\\$\\{" + key + "\\})", variables.get(key).toString());
				}
			}
		}
		return data;
	}
}
