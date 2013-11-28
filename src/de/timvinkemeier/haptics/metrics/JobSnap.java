package de.timvinkemeier.haptics.metrics;

import java.io.IOException;

import org.apache.hadoop.mapred.RunningJob;

// TODO: Auto-generated Javadoc
/**
 * The Class JobSnap.
 */
public class JobSnap {
	
	/** The snap time. */
	private long snapTime = -1;
	
	/** The setup progress. */
	private float setupProgress = 0.0f;
	
	/** The map progress. */
	private float mapProgress = 0.0f;
	
	/** The reduce progress. */
	private float reduceProgress = 0.0f;
	
	/** The cleanup progress. */
	private float cleanupProgress = 0.0f;
	
	/** The completed. */
	private boolean completed = false;
	
	/** The successful. */
	private boolean successful = false;

	/**
	 * Instantiates a new job snap.
	 *
	 * @param snaptime the snaptime
	 * @param job the job
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public JobSnap(long snaptime, RunningJob job) throws IOException {
		this.snapTime = snaptime;
		this.setupProgress = job.setupProgress();
		this.mapProgress = job.mapProgress();
		this.reduceProgress = job.reduceProgress();
		this.cleanupProgress = job.cleanupProgress();
		this.completed = job.isComplete();
		this.successful = job.isSuccessful();
	}

	/**
	 * Gets the snap time.
	 *
	 * @return Returns the snapTime.
	 */
	public long getSnapTime() {
		return this.snapTime;
	}

	/**
	 * Sets the snap time.
	 *
	 * @param snapTime The snapTime to set.
	 */
	public void setSnapTime(long snapTime) {
		this.snapTime = snapTime;
	}

	/**
	 * Gets the map progress.
	 *
	 * @return Returns the mapProgress.
	 */
	public float getMapProgress() {
		return this.mapProgress;
	}

	/**
	 * Gets the reduce progress.
	 *
	 * @return Returns the reduceProgress.
	 */
	public float getReduceProgress() {
		return this.reduceProgress;
	}

	/**
	 * Checks if is completed.
	 *
	 * @return Returns the completed.
	 */
	public boolean isCompleted() {
		return this.completed;
	}

	/**
	 * Checks if is successful.
	 *
	 * @return Returns the successful.
	 */
	public boolean isSuccessful() {
		return this.successful;
	}

	/**
	 * Gets the setup progress.
	 *
	 * @return Returns the setupprogress.
	 */
	public float getSetupProgress() {
		return this.setupProgress;
	}

	/**
	 * Gets the cleanup progress.
	 *
	 * @return Returns the cleanupprogress.
	 */
	public float getCleanupProgress() {
		return this.cleanupProgress;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return snapTime + "\t" + setupProgress + "\t" + mapProgress + "\t" + reduceProgress + "\t" + cleanupProgress + "\t" + +(completed ? 1 : 0) + "\t" + (successful ? 1 : 0);
	}
}
