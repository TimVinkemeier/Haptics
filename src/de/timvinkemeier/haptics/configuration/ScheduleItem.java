package de.timvinkemeier.haptics.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.sun.el.parser.ParseException;

import de.timvinkemeier.haptics.configuration.constraints.ConstraintParser;
import de.timvinkemeier.haptics.configuration.constraints.ExpressionBase;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;

// TODO: Auto-generated Javadoc
/**
 * The Class ScheduleItem.
 */
public class ScheduleItem {
	
	/** The job id. */
	private String jobID;
	
	/** The id. */
	private String ID;
	
	/** The starttime. */
	private int starttime = 0;
	
	/** The endtime. */
	private int endtime = -1;
	
	/** The additional settings. */
	private HashMap<String, Object> additionalSettings;
	
	/** The state. */
	public ExecutionState state = ExecutionState.NotStarted;

	/**
	 * Instantiates a new schedule item.
	 */
	public ScheduleItem() {
		super();
	}

	/**
	 * Instantiates a new schedule item.
	 *
	 * @param jobID the job id
	 * @param starttime the starttime
	 * @param endtime the endtime
	 * @param additional the additional
	 */
	public ScheduleItem(String jobID, int starttime, int endtime, HashMap<String, Object> additional) {
		this.jobID = jobID;
		this.starttime = starttime;
		this.endtime = endtime;
		this.additionalSettings = additional;
	}

	/**
	 * Gets the override input paths or an empty list, if not set.
	 * 
	 * @return the override input paths
	 */
	@SuppressWarnings("unchecked")
	public List<String> getOverrideInputPaths() {
		List<String> list = new ArrayList<>();
		if (!(additionalSettings == null) && additionalSettings.containsKey("overrideInputPaths")) {
			return (List<String>) additionalSettings.get("overrideInputPaths");
		}
		return list;
	}

	/**
	 * Gets the preScriptLocation in the following form if existent, else null.
	 * Format: String[4] with [user, passwd, host, port].
	 * 
	 * @return the pre script location
	 */
	public String[] getPreScriptLocation() {
		if (additionalSettings != null && additionalSettings.containsKey("preScriptLocation")) {
			return ExtensionMethods.splitHostString((String) additionalSettings.get("preScriptLocation"));
		} else {
			return null;
		}
	}

	/**
	 * Gets the post script location. Defaults to the preScriptLocation.
	 * 
	 * @return the post script location
	 */
	public String[] getPostScriptLocation() {
		if (additionalSettings != null && additionalSettings.containsKey("postScriptLocation")) {
			return ExtensionMethods.splitHostString((String) additionalSettings.get("postScriptLocation"));
		} else {
			return getPreScriptLocation();
		}
	}

	/**
	 * Gets the preScript as a String[] with one entry for each line or null if
	 * not set.
	 * 
	 * @return the pre script
	 */
	public String[] getPreScript() {
		if (additionalSettings != null && additionalSettings.containsKey("preScript")) {
			String script = ((String) additionalSettings.get("preScript"));
			// replace builtins
			script = ExtensionMethods.ExtendWithBuiltinScripts(script);
			return script.split("\\n");
		} else {
			return null;
		}
	}

	/**
	 * Gets the postScript as a String[] with one entry for each line or null if
	 * not set.
	 * 
	 * @return the pre script
	 */
	public String[] getPostScript() {
		if (additionalSettings != null && additionalSettings.containsKey("postScript")) {
			String script = ((String) additionalSettings.get("postScript"));
			// replace builtins
			script = ExtensionMethods.ExtendWithBuiltinScripts(script);
			return script.split("\\n");
		} else {
			return null;
		}
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
	 * Gets the endtime.
	 *
	 * @return Returns the endtime.
	 */
	public int getEndtime() {
		return this.endtime;
	}

	/**
	 * Sets the endtime.
	 *
	 * @param endtime The endtime to set.
	 */
	public void setEndtime(int endtime) {
		this.endtime = endtime;
	}

	/**
	 * Gets the starttime.
	 *
	 * @return Returns the starttime.
	 */
	public int getStarttime() {
		return this.starttime;
	}

	/**
	 * Sets the starttime.
	 *
	 * @param starttime The starttime to set.
	 */
	public void setStarttime(int starttime) {
		this.starttime = starttime;
	}

	/**
	 * Gets the job id.
	 *
	 * @return Returns the jobID.
	 */
	public String getJobID() {
		return this.jobID;
	}

	/**
	 * Sets the job id.
	 *
	 * @param jobID The jobID to set.
	 */
	public void setJobID(String jobID) {
		this.jobID = jobID;
	}

	/**
	 * Gets the id.
	 *
	 * @return Returns the iD.
	 */
	public String getID() {
		return this.ID;
	}

	/**
	 * Sets the id.
	 *
	 * @param iD The iD to set.
	 */
	public void setID(String iD) {
		this.ID = iD;
	}

	/**
	 * Checks if is action.
	 * 
	 * @return true, if is action
	 */
	public boolean isAction() {
		return this.jobID.equals("builtin:action");
	}

	/**
	 * Gets the known hosts file (or null if not set).
	 * 
	 * @return the known hosts file
	 */
	public String getKnownHostsFile() {
		if (additionalSettings.containsKey("sshKnownHosts")) {
			return (String) additionalSettings.get("sshKnownHosts");
		}
		return null;
	}

	/**
	 * Gets the start constraints.
	 *
	 * @return the start constraints
	 * @throws ParseException the parse exception
	 */
	public ExpressionBase getStartConstraints() throws ParseException {
		if (additionalSettings != null && additionalSettings.containsKey("startConstraints")) {
			return new ConstraintParser((String) additionalSettings.get("startConstraints")).parse();
		} else {
			return null;
		}
	}

	/**
	 * Gets the kill constraints.
	 *
	 * @return the kill constraints
	 * @throws ParseException the parse exception
	 */
	public ExpressionBase getKillConstraints() throws ParseException {
		if (additionalSettings != null && additionalSettings.containsKey("killConstraints")) {
			return new ConstraintParser((String) additionalSettings.get("killConstraints")).parse();
		} else {
			return null;
		}
	}

}
