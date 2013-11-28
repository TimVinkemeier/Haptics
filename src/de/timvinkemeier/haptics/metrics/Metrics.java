package de.timvinkemeier.haptics.metrics;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.activity.InvalidActivityException;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.ClusterStatus;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobStatus;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.TaskReport;

import de.timvinkemeier.haptics.core.HJobBase;
import de.timvinkemeier.haptics.core.Phase;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;

// TODO: Auto-generated Javadoc
/**
 * The Class Metrics.
 */
public class Metrics {
	
	/** The start time. */
	private long startTime = -1;
	
	/** The end time. */
	private long endTime = -1;
	
	/** The first snaps. */
	private boolean firstSnaps = true;
	
	/** The job id blacklist. */
	private List<String> jobIDBlacklist = new ArrayList<String>();
	
	/** The upload metrics. */
	private UploadMetrics uploadMetrics = new UploadMetrics();
	
	/** The download metrics. */
	private DownloadMetrics downloadMetrics = new DownloadMetrics();
	
	/** The job metrics. */
	private HashMap<String, JobMetric> jobMetrics = new HashMap<>();
	
	/** The phase times. */
	private HashMap<Phase, long[]> phaseTimes = new HashMap<>();
	
	/** The client. */
	private JobClient client;

	/**
	 * Instantiates a new metrics.
	 *
	 * @param conf the conf
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Metrics(Configuration conf) throws IOException {
		super();
		client = new JobClient(new JobConf(conf));
	}

	/**
	 * Start phase.
	 *
	 * @param phase the phase
	 * @return the long
	 */
	public long startPhase(Phase phase) {
		if (!phaseTimes.containsKey(phase)) {
			phaseTimes.put(phase, new long[2]);
		}
		return phaseTimes.get(phase)[0] = new Date().getTime();
	}

	/**
	 * End phase.
	 * 
	 * @param phase
	 *            the phase
	 * @return the long
	 * @throws InvalidActivityException
	 *             the invalid activity exception
	 */
	public long endPhase(Phase phase) throws InvalidActivityException {
		if (!phaseTimes.containsKey(phase)) {
			throw new InvalidActivityException("Cannot end phase " + phase.toString() + " since it was not started!");
		}
		return phaseTimes.get(phase)[1] = new Date().getTime();
	}

	/**
	 * Gets the phase.
	 * 
	 * @param phase
	 *            the phase
	 * @return the phase
	 */
	public long[] getPhase(Phase phase) {
		return phaseTimes.containsKey(phase) ? phaseTimes.get(phase) : new long[] { -1, -1 };
	}

	/**
	 * Gets the full duration of the testrun in milliseconds.
	 * 
	 * @return the full duration
	 */
	public long getFullDuration() {
		if (startTime > -1 && endTime > -1) {
			return endTime - startTime;
		} else {
			return -1;
		}
	}

	/**
	 * Adds the job snaps.
	 * 
	 * @param thistime
	 *            the thistime
	 * @param jobs
	 *            the jobs
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("deprecation")
	public void addJobSnaps(long thistime, Collection<HJobBase> jobs) throws IOException {
		for (JobStatus js : client.getAllJobs()) {
			RunningJob job = client.getJob(js.getJobID());
			// in the first round of JobSnaps, check for those that are already
			// finished (they are likely old ones not belonging to the current
			// run of Haptics, so ignore them)
			if (firstSnaps && job.isComplete()) {
				jobIDBlacklist.add(job.getJobID());
			}
			// check if blacklisted
			boolean found = false;
			for (String s : jobIDBlacklist) {
				if (job.getJobID().equals(s))
					found = true;
			}
			// skip if ID is blacklisted
			if (!found) {
				if (!jobMetrics.containsKey((job.getJobName() + " (" + job.getJobID() + ")"))) {
					jobMetrics.put((job.getJobName() + " (" + job.getJobID() + ")"), new JobMetric(job.getJobID(), job.getJobName(), thistime));
				}
				jobMetrics.get((job.getJobName() + " (" + job.getJobID() + ")")).addSnap(new JobSnap(thistime, job));
			}
		}
		firstSnaps = false;
	}

	/**
	 * Start.
	 */
	public void start() {
		startTime = new Date().getTime();
	}

	/**
	 * Stop.
	 */
	public void stop() {
		endTime = new Date().getTime();
	}

	/**
	 * Gets the start time.
	 *
	 * @return Returns the startTime.
	 */
	public long getStartTime() {
		return this.startTime;
	}

	/**
	 * Gets the end time.
	 *
	 * @return Returns the endTime.
	 */
	public long getEndTime() {
		return this.endTime;
	}

	/**
	 * Gets the job metrics.
	 *
	 * @return Returns the jobMetrics.
	 */
	public HashMap<String, JobMetric> getJobMetrics() {
		return this.jobMetrics;
	}

	/**
	 * Gets the upload metrics.
	 * 
	 * @return the upload metrics
	 */
	public UploadMetrics getUploadMetrics() {
		return uploadMetrics;
	}

	/**
	 * Gets the download metrics.
	 *
	 * @return Returns the downloadMetrics.
	 */
	public DownloadMetrics getDownloadMetrics() {
		return this.downloadMetrics;
	}

	/**
	 * Gets the phase times.
	 *
	 * @return Returns the phaseTimes.
	 */
	public HashMap<Phase, long[]> getPhaseTimes() {
		return this.phaseTimes;
	}

	/**
	 * Saves the data from this metrics object in raw format to the given file.
	 *
	 * @param filename the filename
	 * @throws Exception the exception
	 */
	public void save(String filename) throws Exception {
		String content = "";
		content += "#### Metric raw data\n";
		content += "### General test metrics\n";
		content += "StartTime: " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(new Date(startTime)) + "\n";
		content += "EndTime: " + DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG).format(new Date(endTime)) + "\n";
		content += "Duration (ms): " + (endTime - startTime) + "\n";
		content += "\n";
		content += "### Phase times (starttime, endtime, duration)\n";
		int phasenum = 1;
		for (Phase p : Phase.values()) {
			try {
				content += phasenum + "-" + p.name() + ": " + phaseTimes.get(p)[0] + "," + phaseTimes.get(p)[1] + "," + (phaseTimes.get(p)[1] - phaseTimes.get(p)[0]) + "\n";
			} catch (Exception ex) {
				content += phasenum + "-" + p.name() + ": <unknown value>\n";
			}
			phasenum++;
		}
		content += "\n";
		content += "### Upload metrics\n";
		content += "## Averages\n";
		content += "Total upload volume: " + ExtensionMethods.BytesToFormatted(uploadMetrics.getTotalBytes()) + " (" + uploadMetrics.getTotalBytes() + " Bytes)\n";
		content += "Total upload time (ms): " + uploadMetrics.getTotalMillis() + "\n";
		content += "Uploaded files count: " + uploadMetrics.getTotalCount() + "\n";
		content += "Average upload volume: " + ExtensionMethods.BytesToFormatted(uploadMetrics.getAverageUploadBytes()) + " (" + uploadMetrics.getAverageUploadBytes() + " Bytes)\n";
		content += "Average upload rate: " + ExtensionMethods.BytesToFormatted(uploadMetrics.getAverageUploadRate()) + "/s\n";
		content += "Average upload time (ms): " + uploadMetrics.getAverageUploadTime() + "\n";
		content += "\n";
		content += "## Detailed file transfers ('filename', starttime, endtime, duration, bytes, averagerate)\n";
		int ftmcount = 1;
		for (FileTransferMetric ftm : uploadMetrics.getUploads()) {
			content += ftmcount + " " + ftm.toString() + "\n";
			ftmcount++;
		}
		content += "\n";
		content += "### Download metrics\n";
		content += "## Averages\n";
		content += "Total download volume: " + ExtensionMethods.BytesToFormatted(downloadMetrics.getTotalBytes()) + " (" + downloadMetrics.getTotalBytes() + " Bytes)\n";
		content += "Total download time (ms): " + downloadMetrics.getTotalMillis() + "\n";
		content += "Downloaded files count: " + downloadMetrics.getTotalCount() + "\n";
		content += "Average download volume: " + ExtensionMethods.BytesToFormatted(downloadMetrics.getAverageUploadBytes()) + " (" + downloadMetrics.getAverageUploadBytes() + " Bytes)\n";
		content += "Average download rate: " + ExtensionMethods.BytesToFormatted(downloadMetrics.getAverageUploadRate()) + "/s\n";
		content += "Average download time (ms): " + downloadMetrics.getAverageUploadTime() + "\n";
		content += "\n";
		content += "## Detailed file transfers ('filename', starttime, endtime, duration, bytes, averagerate)\n";
		ftmcount = 1;
		for (FileTransferMetric ftm : downloadMetrics.getDownloads()) {
			content += ftmcount + " " + ftm.toString() + "\n";
			ftmcount++;
		}
		content += "\n";
		content += "### Cluster Status\n";
		ClusterStatus cs = client.getClusterStatus(true);
		content += "JobTrackerState: " + cs.getJobTrackerState().toString() + "\n";
		content += "Used memory: " + ExtensionMethods.BytesToFormatted(cs.getUsedMemory()) + "\n";
		content += "\n";
		Exception e = null;
		try {
			content += "### Job Metrics\n";
			for (String key : jobMetrics.keySet()) {
				content += "## " + jobMetrics.get(key).toString() + "\n";
				content += "# Task Reports\n";
				content += "- Setup -\n";
				if (jobMetrics.get(key).getSetupReports() != null) {
					for (TaskReport tr : jobMetrics.get(key).getSetupReports()) {
						content += ExtensionMethods.StringArrayToString(tr.getCounters().makeCompactString().split(","), "\n") + "\n";
					}
				}
				content += "- Map -\n";
				if (jobMetrics.get(key).getMapReports() != null) {
					for (TaskReport tr : jobMetrics.get(key).getMapReports()) {
						content += ExtensionMethods.StringArrayToString(tr.getCounters().makeCompactString().split(","), "\n") + "\n";
					}
				}
				content += "- Reduce -\n";
				if (jobMetrics.get(key).getReduceReports() != null) {
					for (TaskReport tr : jobMetrics.get(key).getReduceReports()) {
						content += ExtensionMethods.StringArrayToString(tr.getCounters().makeCompactString().split(","), "\n") + "\n";
					}
				}
				content += "- Cleanup -\n";
				if (jobMetrics.get(key).getCleanupReports() != null) {
					for (TaskReport tr : jobMetrics.get(key).getCleanupReports()) {
						content += ExtensionMethods.StringArrayToString(tr.getCounters().makeCompactString().split(","), "\n") + "\n";
					}
				}
				content += "# Job Snaps (step, setupprogress, mapprogress, reduceprogress, cleanupprogress, finished, successful)\n";
				for (JobSnap j : jobMetrics.get(key).getSnaps()) {
					content += j.toString() + "\n";
				}
				content += "\n";
			}
		} catch (Exception ex) {
			e = ex;
		}
		FileUtils.writeStringToFile(new File(filename), content);
		if (e != null)
			throw e;
	}

	/**
	 * Finalize job metrics.
	 * 
	 * @param thistime
	 *            the thistime
	 * @param values
	 *            the values
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("deprecation")
	public void finalizeJobMetrics(long thistime, Collection<HJobBase> values) throws IOException {
		for (JobStatus js : client.getAllJobs()) {
			RunningJob job = client.getJob(js.getJobID());
			if (jobMetrics.containsKey((job.getJobName() + " (" + job.getJobID() + ")"))) {
				jobMetrics.get((job.getJobName() + " (" + job.getJobID() + ")")).finalize(thistime, job, client.getSetupTaskReports(js.getJobID()), client.getMapTaskReports(js.getJobID()), client.getReduceTaskReports(js.getJobID()), client.getCleanupTaskReports(js.getJobID()));
			}
		}
	}

}
