package de.timvinkemeier.haptics.metrics;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TaskReport;
import org.apache.hadoop.mapred.TaskStatus.Phase;

// TODO: Auto-generated Javadoc
/**
 * The Class JobMetric.
 */
public class JobMetric {
	
	/** The snaps. */
	private List<JobSnap> snaps = new ArrayList<>();
	
	/** The starttime. */
	private long starttime = -1;
	
	/** The endtime. */
	private long endtime = -1;
	
	/** The maxtime. */
	private long maxtime = -1;
	
	/** The job name. */
	private String jobName;
	
	/** The job id. */
	private String jobID;
	
	/** The final job. */
	private RunningJob finalJob = null;
	
	/** The setup reports. */
	private TaskReport[] setupReports = null;
	
	/** The map reports. */
	private TaskReport[] mapReports = null;
	
	/** The reduce reports. */
	private TaskReport[] reduceReports = null;
	
	/** The cleanup reports. */
	private TaskReport[] cleanupReports = null;

	/**
	 * Instantiates a new job metric.
	 *
	 * @param jobID the job id
	 * @param jobName the job name
	 * @param starttime the starttime
	 */
	public JobMetric(String jobID, String jobName, long starttime) {
		this.jobName = jobName;
		this.jobID = jobID;
		this.starttime = starttime;
	}

	/**
	 * Adds the snap.
	 * 
	 * @param jobSnap
	 *            the job snap
	 */
	public void addSnap(JobSnap jobSnap) {
		snaps.add(jobSnap);
		if ((endtime == -1 || jobSnap.getSnapTime() < endtime) && jobSnap.isCompleted())
			endtime = jobSnap.getSnapTime();
		if (jobSnap.getSnapTime() > maxtime)
			maxtime = jobSnap.getSnapTime();
	}

	/**
	 * Gets the snaps.
	 *
	 * @return Returns the snaps.
	 */
	public List<JobSnap> getSnaps() {
		return this.snaps;
	}

	/**
	 * Gets the array.
	 *
	 * @param p the p
	 * @return the map array
	 */
	public double[] getAsArray(Phase p) {
		double[] d = new double[(int) maxtime + 1];
		for (JobSnap js : snaps) {
			switch (p) {
				case STARTING:
					d[(int) js.getSnapTime()] = js.getSetupProgress();
					break;
				case MAP:
					d[(int) js.getSnapTime()] = js.getMapProgress();
					break;
				case REDUCE:
					d[(int) js.getSnapTime()] = js.getReduceProgress();
					break;
				case CLEANUP:
					d[(int) js.getSnapTime()] = js.getCleanupProgress();
					break;
				default:
					throw new InvalidParameterException("Supported values are STARTING, MAP, REDUCE and CLEANUP.");
			}
		}
		return d;
	}

	/**
	 * Gets the starttime.
	 *
	 * @return Returns the starttime.
	 */
	public long getStarttime() {
		return this.starttime;
	}

	/**
	 * Gets the endtime.
	 *
	 * @return Returns the endtime.
	 */
	public long getEndtime() {
		if (!snaps.isEmpty()) {
			long stepmax = 0;
			for (JobSnap s : snaps) {
				if (s.getSnapTime() > stepmax)
					stepmax = s.getSnapTime();
			}
			endtime = starttime + stepmax * 1000;
		} else {
			endtime = -1;
		}
		return this.endtime;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = jobName + "\n";
		s += "jobID: " + jobID + "\n";
		s += "Times: " + starttime + "," + endtime + "," + (endtime - starttime) + "\n";
		try {
			s += "FailureInfo: " + finalJob.getFailureInfo() + "\n";
			s += "Final Runstate: " + JobStatus.getJobRunState(finalJob.getJobState()) + "\n";
		} catch (Exception ex) {}
		return s;
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
	 * Gets the setup reports.
	 *
	 * @return Returns the setupReports.
	 */
	public TaskReport[] getSetupReports() {
		return this.setupReports;
	}

	/**
	 * Gets the map reports.
	 *
	 * @return Returns the mapReports.
	 */
	public TaskReport[] getMapReports() {
		return this.mapReports;
	}

	/**
	 * Gets the reduce reports.
	 *
	 * @return Returns the reduceReports.
	 */
	public TaskReport[] getReduceReports() {
		return this.reduceReports;
	}

	/**
	 * Gets the cleanup reports.
	 *
	 * @return Returns the cleanupReports.
	 */
	public TaskReport[] getCleanupReports() {
		return this.cleanupReports;
	}

	/**
	 * Finalize.
	 *
	 * @param thistime the thistime
	 * @param job the job
	 * @param setupReports the setup reports
	 * @param mapReports the map reports
	 * @param reduceReports the reduce reports
	 * @param cleanupReports the cleanup reports
	 */
	public void finalize(long thistime, RunningJob job, TaskReport[] setupReports, TaskReport[] mapReports, TaskReport[] reduceReports, TaskReport[] cleanupReports) {
		if (endtime == -1)
			endtime = thistime;
		finalJob = job;
		this.setupReports = setupReports;
		this.mapReports = mapReports;
		this.reduceReports = reduceReports;
		this.cleanupReports = cleanupReports;
	}
}
