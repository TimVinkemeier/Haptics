package de.timvinkemeier.haptics.core;

import java.io.IOException;

import org.apache.hadoop.mapreduce.Job;

import de.timvinkemeier.haptics.configuration.ExecutionState;
import de.timvinkemeier.haptics.configuration.Schedule;
import de.timvinkemeier.haptics.configuration.ScheduleItem;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class HMRJob.
 */
public class HMRJob extends HJobBase {

	/** The job. */
	private Job job;

	/**
	 * Instantiates a new hMR job.
	 *
	 * @param j the j
	 * @param si the si
	 */
	public HMRJob(Job j, ScheduleItem si) {
		this.job = j;
		this.si = si;
	}

	/**
	 * Run.
	 *
	 * @see de.timvinkemeier.haptics.core.HJobBase#run()
	 */
	public void run() {
		try {
			executionState = ExecutionState.Started;
			Log.print("'" + getName() + "' started.", LogLevel.Verbose);
			preScript.run();
			job.submit();
			executionState = ExecutionState.Running;
			job.waitForCompletion(Log.debug);
			if (job.isSuccessful()) {
				executionState = ExecutionState.Finished;
			} else {
				executionState = ExecutionState.Exception;
			}
			Log.print("'" + getName() + "' finished.", LogLevel.Verbose);
			postScript.run();
			Log.print("'" + getName() + "' complete.", LogLevel.Verbose);
		} catch (InterruptedException ex) {
			try {
				executionState = ExecutionState.Killed;
				job.killJob();
				Log.print("Item '" + si.getID() + "' killed.", LogLevel.Info);
			} catch (IOException ex1) {
				Log.print("The following error occurred during killing of job '" + getName() + "'. Further execution will be aborted...", LogLevel.Critical);
				Log.print(ex);
				Schedule.asyncException = ex;
			}
		} catch (Exception ex) {
			executionState = ExecutionState.Exception;
			Log.print("The following error occurred during execution of job '" + getName() + "'. Further execution will be aborted...", LogLevel.Critical);
			Log.print(ex);
			Schedule.asyncException = ex;
		}
	}

}
