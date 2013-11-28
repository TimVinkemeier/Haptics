package de.timvinkemeier.haptics.metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.timvinkemeier.haptics.extensions.ExtensionMethods;

// TODO: Auto-generated Javadoc
/**
 * The Class DownloadMetrics.
 */
public class DownloadMetrics {
	
	/** The downloads. */
	private List<FileTransferMetric> downloads = new ArrayList<FileTransferMetric>();
	
	/** The total bytes. */
	private long totalBytes = -1;
	
	/** The total millis. */
	private long totalMillis = -1;
	
	/** The total count. */
	private long totalCount = -1;
	
	/** The last start. */
	private long lastStart = -1;
	
	/** The last filename. */
	private String lastFilename = "";
	
	/** The last size. */
	private long lastSize = -1;

	/**
	 * Begin new download.
	 *
	 * @param f the f
	 */
	public void beginNewDownload(File f) {
		if (totalBytes == -1)
			totalBytes = 0;
		if (totalMillis == -1)
			totalMillis = 0;
		if (totalCount == -1)
			totalCount = 0;
		long length = ExtensionMethods.getSize(f);
		totalBytes += length;
		totalCount++;
		lastStart = new Date().getTime();
		lastFilename = f.getAbsolutePath();
		lastSize = length;
	}

	/**
	 * Begin new download.
	 *
	 * @param filename the filename
	 * @param length the length
	 */
	public void beginNewDownload(String filename, long length) {
		if (totalBytes == -1)
			totalBytes = 0;
		if (totalMillis == -1)
			totalMillis = 0;
		if (totalCount == -1)
			totalCount = 0;
		totalBytes += length;
		totalCount++;
		lastStart = new Date().getTime();
		lastFilename = filename;
		lastSize = length;
	}

	/**
	 * End new download.
	 */
	public void endNewDownload() {
		long end = new Date().getTime();
		totalMillis += end - lastStart;
		downloads.add(new FileTransferMetric(lastFilename, lastStart, end, lastSize));
	}

	/**
	 * Gets the average upload bytes.
	 *
	 * @return the average upload bytes
	 */
	public long getAverageUploadBytes() {
		return (totalCount > 0 ? totalBytes / totalCount : -1);
	}

	/**
	 * Gets the average upload time.
	 *
	 * @return the average upload time
	 */
	public long getAverageUploadTime() {
		return (totalCount > 0 ? totalMillis / totalCount : -1);
	}

	/**
	 * Gets the average upload rate.
	 *
	 * @return the average upload rate
	 */
	public long getAverageUploadRate() {
		return (long) (totalBytes / Math.max((totalMillis / 1000.0), 0.001));
	}

	/**
	 * Gets the total bytes.
	 *
	 * @return Returns the totalBytes.
	 */
	public long getTotalBytes() {
		return this.totalBytes;
	}

	/**
	 * Gets the total millis.
	 *
	 * @return Returns the totalMillis.
	 */
	public long getTotalMillis() {
		return this.totalMillis;
	}

	/**
	 * Gets the total count.
	 *
	 * @return Returns the totalCount.
	 */
	public long getTotalCount() {
		return this.totalCount;
	}

	/**
	 * Gets the downloads.
	 *
	 * @return Returns the uploads.
	 */
	public List<FileTransferMetric> getDownloads() {
		return this.downloads;
	}

}
