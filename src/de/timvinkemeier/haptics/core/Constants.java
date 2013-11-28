package de.timvinkemeier.haptics.core;

// TODO: Auto-generated Javadoc
/**
 * The Class Constants.
 */
public final class Constants {

	/** The path in HDFS where input data for jobs is stored. */
	public static final String HDFSInputTemporaryPath = "/haptics/input/";

	/** The path in HDFS where output data for jobs is stored. */
	public static final String HDFSOutputTemporaryPath = "/haptics/output/";

	/** The prefix for HDFS input data folders. */
	public static final String HDFSJobInputPrefix = "input-";

	/** The prefix for HDFS output data folders. */
	public static final String HDFSJobOutputPrefix = "output-";

	/**
	 * The name for the report folder that will be created below OutputBasePath.
	 */
	public static final String ReportFolderName = "Report";

	/** The Constant LogFileName. */
	public static final String LogFileName = "haptics_log.txt";

	/** The Constant MetricsRawFileName. */
	public static final String MetricsRawFileName = "metrics_raw.txt";

	/** The Constant HDFSStatisticsFileName. */
	public static final String HDFSStatisticsFileName = "HDFS_statistics.txt";

	/** The Constant MapProgressChartFileName. */
	public static final String MapProgressChartFileName = "mapProgress_chart.png";

	/** The Constant ReduceProgressChartFileName. */
	public static final String ReduceProgressChartFileName = "reduceProgress_chart.png";

	/** The Constant CombinedProgressChartFileName. */
	public static final String CombinedProgressChartFileName = "combinedProgress_chart.png";

	/** The Constant ItemExecutionIntervalsChartFileName. */
	public static final String ItemExecutionIntervalsChartFileName = "itemExecutionIntervals_chart.png";

	/**
	 * The maximum amount of time (in seconds) to wait for the submit threads to
	 * finish by themselves after an exception occurred during schedule
	 * execution. After this timespan, all active submit Threads will be killed.
	 */
	protected static final int MaxWaitSecondsForScheduleKill = 60;

}
