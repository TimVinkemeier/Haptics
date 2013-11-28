package de.timvinkemeier.haptics.metrics;

// TODO: Auto-generated Javadoc
/**
 * The Class FileTransferMetric.
 */
public class FileTransferMetric {
	
	/** The filename. */
	private String filename = "";
	
	/** The starttime. */
	private long starttime = -1;
	
	/** The endtime. */
	private long endtime = -1;
	
	/** The bytes. */
	private long bytes = -1;

	/**
	 * Instantiates a new file transfer metric.
	 *
	 * @param filename the filename
	 * @param start the start
	 * @param end the end
	 * @param bytes the bytes
	 */
	public FileTransferMetric(String filename, long start, long end, long bytes) {
		this.filename = filename;
		this.starttime = start;
		this.endtime = end;
		this.bytes = bytes;
	}

	/**
	 * Gets the average upload rate.
	 * 
	 * @return the average rate
	 */
	public long getAverageRate() {
		return (getDuration() > 0 ? bytes / getDuration() : -1);
	}

	/**
	 * Gets the duration.
	 * 
	 * @return the duration
	 */
	public long getDuration() {
		if (starttime == -1 || endtime == -1) {
			return -1;
		}
		return endtime - starttime;
	}

	/**
	 * Gets the filename.
	 *
	 * @return Returns the filename.
	 */
	public String getFilename() {
		return this.filename;
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
		return this.endtime;
	}

	/**
	 * Gets the bytes.
	 *
	 * @return Returns the bytes.
	 */
	public long getBytes() {
		return this.bytes;
	}

	/**
	 * Returns the values of this Filetransfermetric like
	 * '{filename}',{starttime (ms)},{endtime (ms)},{duration
	 * (ms)},{bytes},{averagerate (bytes/s}.
	 *
	 * @return the string
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "'" + filename + "'," + starttime + "," + endtime + "," + getDuration() + "," + bytes + "," + getAverageRate();
	}
}
