package de.timvinkemeier.haptics.core;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import de.timvinkemeier.haptics.configuration.ExecutionState;
import de.timvinkemeier.haptics.configuration.Schedule;
import de.timvinkemeier.haptics.configuration.ScheduleItem;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class HToolJob.
 */
public class HToolJob extends HJobBase {

	/** The conf. */
	private Configuration conf;
	
	/** The tool. */
	private Tool tool;
	
	/** The args. */
	private String[] args;

	/**
	 * Instantiates a new h tool job.
	 *
	 * @param conf the conf
	 * @param si the si
	 * @param tool the tool
	 * @param args the args
	 */
	public HToolJob(Configuration conf, ScheduleItem si, Tool tool, String[] args) {
		this.conf = conf;
		this.tool = tool;
		this.args = args;
		this.si = si;
	}

	/**
	 * @see de.timvinkemeier.haptics.core.HJobBase#run()
	 */
	public void run() {
		try {
			executionState = ExecutionState.Started;
			Log.print("'" + getName() + "' started.", LogLevel.Verbose);
			preScript.run();
			executionState = ExecutionState.Running;
			ToolRunner.run(conf, tool, args);
			executionState = ExecutionState.Finished;
			Log.print("'" + getName() + "' finished.", LogLevel.Verbose);
			postScript.run();
			Log.print("'" + getName() + "' complete.", LogLevel.Verbose);
		} catch (InterruptedException ex) {
			executionState = ExecutionState.Killed;
			Log.print("Item '" + si.getID() + "' killed.", LogLevel.Info);
		} catch (Exception ex) {
			executionState = ExecutionState.Exception;
			Log.print("The following error occurred during execution of job '" + getName() + "'. Further execution will be aborted...", LogLevel.Critical);
			Log.print(ex);
			Schedule.asyncException = ex;
		}
	}

}
