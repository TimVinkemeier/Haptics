package de.timvinkemeier.haptics.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.apache.hadoop.mapreduce.Job;

import de.timvinkemeier.haptics.core.HJobBase;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;
import de.timvinkemeier.haptics.metrics.Metrics;

// TODO: Auto-generated Javadoc
/**
 * The Class Schedule.
 */
public class Schedule {
	
	/** The items. */
	private List<ScheduleItem> items;
	// private List<ScheduleItem> postponedItemsToStart = new
	// ArrayList<ScheduleItem>();
	// private List<ScheduleItem> postponedItemsToKill = new
	// ArrayList<ScheduleItem>();
	/** The submit threads. */
	private List<Thread> submitThreads = new ArrayList<>();
	
	/** The async exception. */
	public static volatile Exception asyncException = null;
	// private int killRetry = 0;
	// private String killRetryID = "";
	/** The last step. */
	public static int lastStep = -1;

	/**
	 * Instantiates a new schedule.
	 */
	public Schedule() {
		super();
	}

	/**
	 * Gets the items.
	 *
	 * @return Returns the items.
	 */
	public List<ScheduleItem> getItems() {
		return this.items;
	}

	/**
	 * Sets the items.
	 *
	 * @param items The items to set.
	 */
	public void setItems(List<ScheduleItem> items) {
		this.items = items;
	}

	/**
	 * Gets the items starting between now and lasttime.
	 * 
	 * @param now
	 *            The current testtime.
	 * @param lasttime
	 *            The last testtime.
	 * @return A List of items starting between lasttime and now.
	 */
	public List<ScheduleItem> getItemsStartingBetween(long now, long lasttime) {
		List<ScheduleItem> l = new ArrayList<>();
		for (ScheduleItem si : items) {
			if ((si.getStarttime() > lasttime || (lasttime == 0 && lasttime != now)) && si.getStarttime() <= now) {
				l.add(si);
			}
		}
		return l;
	}

	/**
	 * Gets the items stopping between now and lasttime.
	 * 
	 * @param now
	 *            the now
	 * @param lasttime
	 *            the lasttime
	 * @return the items stopping between
	 */
	public List<ScheduleItem> getItemsStoppingBetween(long now, long lasttime) {
		List<ScheduleItem> l = new ArrayList<>();
		for (ScheduleItem si : items) {
			if (si.getEndtime() > lasttime && si.getEndtime() <= now && si.getEndtime() > -1 && !si.getJobID().equals("builtin:action")) {
				l.add(si);
			}
		}
		return l;
	}

	/**
	 * Executes a step of the schedule execution (i.e. performing all steps
	 * necessary between thistime and lasttime) in the following order: 1. kill
	 * jobs with endtime <= thistime (and != -1) 2. start jobs with starttime
	 * between lasttime and thistime
	 *
	 * @param jobs the jobs
	 * @return true, if successful
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	// public void executeStep_old(long thistime, long lasttime, HashMap<String,
	// Job> jobs, HashMap<String, ExpressionBase> startConstraints,
	// HashMap<String, ExpressionBase> killConstraints, Metrics metrics) throws
	// Exception {
	// if (asyncException != null) {
	// throw asyncException;
	// }
	//
	// int killedJobs = 0;
	// int startedJobs = 0;
	// List<ScheduleItem> itemsToDeleteFromPostpone = new ArrayList<>();
	//
	// // postponed kill items
	// for (ScheduleItem si : postponedItemsToKill) {
	// if (killConstraints.containsKey(si.getID())) {
	// if (!killConstraints.get(si.getID()).evaluate(items)) {
	// Log.print("Postponing kill of item '" + si.getID() +
	// "' due to kill constraints.", LogLevel.Verbose);
	// continue;
	// }
	// }
	// try {
	// jobs.get(si.getID()).killJob();
	// Log.print("'" + si.getID() + "' killed.");
	// itemsToDeleteFromPostpone.add(si);
	// killedJobs++;
	// } catch (IllegalStateException ex) {
	// // job should be killed before it was started properly -> try to
	// // kill it again
	// // reset retry counter
	// // if (!killRetryID.equals(si.getID())) {
	// // killRetry = 0;
	// // }
	// killRetryID = si.getID();
	// killRetry++;
	// if (killRetry > 100) {
	// Log.print("While trying to kill job '" + si.getID() +
	// "' the kill retry count exceeded 100. Assuming the job was never started,  but we cannot tell this for sure.",
	// LogLevel.Verbose);
	// } else {
	// Log.print("Try to kill job '" + si.getID() +
	// "' resulted in the following error: " + ex.getLocalizedMessage() +
	// "\nKilling will be tried again it the next step. (Retry " + killRetry +
	// "/100)", LogLevel.Info);
	// si.setEndtime((int) thistime + 1);
	// }
	// }
	// }
	// // to avoid concurrent modification
	// for (ScheduleItem si : itemsToDeleteFromPostpone) {
	// postponedItemsToKill.remove(si);
	// }
	//
	// // new killed items
	// for (ScheduleItem si : getItemsStoppingBetween(thistime, lasttime)) {
	// try {
	// // Actions can not be killed, just set the state
	// if (si.isAction()) {
	// si.state = ExecutionState.Killed;
	// continue;
	// }
	//
	// if (killConstraints.containsKey(si.getID())) {
	// if (!killConstraints.get(si.getID()).evaluate(items)) {
	// Log.print("Postponing kill of item '" + si.getID() +
	// "' due to kill constraints.", LogLevel.Verbose);
	// postponedItemsToKill.add(si);
	// continue;
	// }
	// }
	// jobs.get(si.getID()).killJob();
	// Log.print("'" + si.getID() + "' killed.");
	// killedJobs++;
	// } catch (IllegalStateException ex) {
	// // job should be killed before it was started properly -> try to
	// // kill it again
	// // reset retry counter
	// if (!killRetryID.equals(si.getID())) {
	// killRetry = 0;
	// }
	// killRetryID = si.getID();
	// killRetry++;
	// if (killRetry > 100) {
	// Log.print("While trying to kill job '" + si.getID() +
	// "' the kill retry count exceeded 100. Assuming the job was never started,  but we cannot tell this for sure.",
	// LogLevel.Verbose);
	// } else {
	// Log.print("Try to kill job '" + si.getID() +
	// "' resulted in the following error: " + ex.getLocalizedMessage() +
	// "\nKilling will be tried again it the next step. (Retry " + killRetry +
	// "/100)", LogLevel.Info);
	// si.setEndtime((int) thistime + 1);
	// }
	// }
	// }
	// itemsToDeleteFromPostpone.clear();
	// // postponed start items
	// for (ScheduleItem si : postponedItemsToStart) {
	// if (startConstraints.containsKey(si.getID())) {
	// if (!startConstraints.get(si.getID()).evaluate(items) && !(thistime >
	// si.getEndtime())) {
	// Log.print("Postponing start of item '" + si.getID() +
	// "' due to start constraints.", LogLevel.Verbose);
	// continue;
	// }
	// } else if (thistime > si.getEndtime()) {
	// itemsToDeleteFromPostpone.add(si);
	// continue;
	// }
	// submitThreads.add(submitAsync(thistime, si, jobs.get(si.getID())));
	// itemsToDeleteFromPostpone.add(si);
	// startedJobs++;
	// }
	// // to avoid concurrent modification
	// for (ScheduleItem si : itemsToDeleteFromPostpone) {
	// postponedItemsToStart.remove(si);
	// }
	// // new starting items
	// for (ScheduleItem si : getItemsStartingBetween(thistime, lasttime)) {
	// if (startConstraints.containsKey(si.getID())) {
	// if (!startConstraints.get(si.getID()).evaluate(items)) {
	// Log.print("Postponing start of item '" + si.getID() +
	// "' due to start constraints.", LogLevel.Verbose);
	// postponedItemsToStart.add(si);
	// continue;
	// }
	// }
	// // maybe job killed before trying to start -> log only
	// try {
	// submitThreads.add(submitAsync(thistime, si, jobs.get(si.getID())));
	// startedJobs++;
	// } catch (IllegalStateException ex) {
	// Log.print("Skipped start of job '" + si.getID() +
	// "' since it seems that it has already been killed.");
	// }
	// }
	// // add job metrics
	// for (ScheduleItem si : items) {
	// if (!si.getJobID().equals("builtin:action") && asyncException == null) {
	// metrics.addJobSnap(thistime, si.getID(), jobs.get(si.getID()));
	// }
	// }
	// Log.print("Successfully executed schedule step " + thistime + " (last: "
	// + lasttime + "). Killed: " + killedJobs + " Started: " + startedJobs,
	// (startedJobs + killedJobs > 0 ? LogLevel.Info : LogLevel.Verbose));
	// // check if schedule is finished
	// if (getItemsStoppingBetween(Long.MAX_VALUE, thistime).isEmpty() &&
	// getItemsStartingBetween(Long.MAX_VALUE, thistime).isEmpty() &&
	// !hasIncompleteJobs(jobs)) {
	// // wait for all submits to finish
	// Log.print("All schedule steps executed, waiting for scripts to finish (30 seconds maximum wait time)...");
	// for (Thread t : submitThreads) {
	// t.join(30000);
	// }
	// throw new InvalidActivityException("Schedule finished.");
	// }
	// }

	/**
	 * Submits a job asynchronously, also handling pre- and postscript
	 * operations.
	 * 
	 * @param job
	 *            the job
	 * @return
	 */
	// private Thread submitAsync(final long step, final ScheduleItem item,
	// final Job job) {
	// Thread t = new Thread(new Runnable() {
	//
	// @Override
	// public void run() {
	// try {
	// if (!item.isAction()) {
	// executePreScript(item, job, step);
	// job.submit();
	// item.state = ExecutionState.Running;
	// Log.print("Item '" + item.getID() + "' started.");
	// job.waitForCompletion(false);
	// if (job.isSuccessful()) {
	// item.state = ExecutionState.Finished;
	// } else {
	// item.state = ExecutionState.Exception;
	// }
	// executePostScript(item, job, step);
	// Log.print("Item '" + item.getID() + "' completed.");
	// } else {
	// Log.print("Executing action '" + item.getID() + "'.", LogLevel.Verbose);
	// item.state = ExecutionState.Running;
	// executePreScript(item, null, step);
	// executePostScript(item, null, step);
	// item.state = ExecutionState.Finished;
	// Log.print("Action '" + item.getID() + "' executed.");
	// }
	// } catch (Exception ex) {
	// Log.print("The following error occurred during submission of a job. Further execution will be aborted...",
	// LogLevel.Critical);
	// Log.print(ex);
	// item.state = ExecutionState.Exception;
	// asyncException = ex;
	// }
	// }
	// });
	// t.setName("Submit thread for item '" + item.getID() + "'");
	// t.start();
	// return t;
	// }

	/**
	 * Executes the prescript for this item, if existent.
	 * 
	 * @param item
	 *            the item
	 * @throws Exception
	 */
	// protected void executePreScript(ScheduleItem item, Job job, long step)
	// throws Exception {
	// String[] loc = item.getPreScriptLocation();
	// String[] script = item.getPreScript();
	// if (script == null || loc == null) {
	// Log.print("No prescript for item '" + item.getID() + "'.",
	// LogLevel.Verbose);
	// return;
	// }
	// String[] output = ScriptManager.executeScript(loc, script,
	// (loc[2].equals("localhost") || loc[2].equals("127.0.0.1") ?
	// ExtensionMethods.getEnvironmentVariablesForScripts(item, job, step) :
	// ExtensionMethods.getEnvironmentVariablesForRemoteScripts(item, job,
	// step)), "prescript for item '" + item.getID() + "'", false);
	// if (!ExtensionMethods.IsNullOrWhitespace(output[1])) {
	// // there was something on the error stream
	// throw new
	// ScriptExecutionException("Error during execution of prescript for item '"
	// + item.getID() + "':\n" + output[1]);
	// }
	// }

	/**
	 * Executes the postscript for this item, if existent.
	 * 
	 * @param item
	 *            the item
	 * @param job
	 *            the job
	 * @param step
	 *            the step
	 */
	// protected void executePostScript(ScheduleItem item, Job job, long step)
	// throws Exception {
	// String[] loc = item.getPostScriptLocation();
	// String[] script = item.getPostScript();
	// if (script == null || loc == null) {
	// Log.print("No postscript for item '" + item.getID() + "'.",
	// LogLevel.Verbose);
	// return;
	// }
	// String[] output = ScriptManager.executeScript(loc, script,
	// (loc[2].equals("localhost") || loc[2].equals("127.0.0.1") ?
	// ExtensionMethods.getEnvironmentVariablesForScripts(item, job, step) :
	// ExtensionMethods.getEnvironmentVariablesForRemoteScripts(item, job,
	// step)), "postscript for item '" + item.getID() + "'", false);
	// if (!ExtensionMethods.IsNullOrWhitespace(output[1])) {
	// // there was something on the error stream
	// throw new
	// ScriptExecutionException("Error during execution of postscript for item '"
	// + item.getID() + "':\n" + output[1]);
	// }
	// }

	/**
	 * Checks for incomplete jobs.
	 * 
	 * @param jobs
	 *            the jobs
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private boolean hasIncompleteJobs(HashMap<String, Job> jobs) throws IOException {
		for (Job j : jobs.values()) {
			try {
				if (!j.isComplete()) {
					return true;
				}
			} catch (IllegalStateException ex) {
				// job in state define, not running
				return true;
			}
		}
		return false;
	}

	/**
	 * Kill submit threads.
	 * 
	 * @return the number of threads that where killed.
	 */
	@SuppressWarnings("deprecation")
	public int killSubmitThreads() {
		int i = 0;
		for (Thread t : submitThreads) {
			if (t.isAlive()) {
				t.stop();
				i++;
			}
		}
		return i;
	}

	/**
	 * Active submit thread count.
	 * 
	 * @return the int
	 */
	public int activeSubmitThreadCount() {
		int i = 0;
		for (Thread t : submitThreads) {
			if (t.isAlive())
				i++;
		}
		return i;
	}

	/**
	 * Execute step.
	 * 
	 * @param thistime
	 *            the thistime
	 * @param lasttime
	 *            the lasttime
	 * @param jobs
	 *            the jobs
	 * @param metrics
	 *            the metrics
	 * @throws Exception
	 *             the exception
	 */
	public void executeStep(long thistime, long lasttime, HashMap<String, HJobBase> jobs, Metrics metrics) throws Exception {
		if (asyncException != null) {
			throw asyncException;
		}

		List<Thread> threadsToRemove = new ArrayList<>();
		int killedJobs = 0, startedJobs = 0;
		for (HJobBase hj : jobs.values()) {
			if (hj.shouldBeKilled(thistime, jobs.values())) {
				for (Thread t : submitThreads) {
					if (t.getName().equals(hj.getName())) {
						// kill it
						t.interrupt();
						threadsToRemove.add(t);
						killedJobs++;
					}
				}
				hj.executionState = ExecutionState.Killed;
			}
		}
		for (Thread t : threadsToRemove) {
			submitThreads.remove(t);
		}
		submitThreads.clear();

		for (HJobBase hj : jobs.values()) {
			if (hj.shouldStart(thistime, jobs.values())) {
				Thread t = new Thread(hj, hj.getName());
				submitThreads.add(t);
				startedJobs++;
				t.start();
			}
		}

		metrics.addJobSnaps(thistime, jobs.values());

		Log.print("Successfully executed schedule step " + thistime + " (last: " + lasttime + "). Killed: " + killedJobs + " Started: " + startedJobs, (startedJobs + killedJobs > 0 ? LogLevel.Info : LogLevel.Verbose));
		// check for finished schedule and take metrics
		boolean finished = true;
		for (HJobBase hj : jobs.values()) {
			// metrics
			if (hj.executionState == ExecutionState.NotStarted || hj.executionState == ExecutionState.Started || hj.executionState == ExecutionState.Running) {
				finished = false;
			}
		}
		if (finished) {
			metrics.finalizeJobMetrics(thistime, jobs.values());
			throw new InvalidActivityException("Schedule finished.");
		}
	}
}
