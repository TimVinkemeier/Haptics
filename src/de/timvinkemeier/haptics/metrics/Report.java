package de.timvinkemeier.haptics.metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystem.Statistics;
import org.apache.hadoop.mapred.TaskStatus.Phase;

import com.xeiam.xchart.BitmapEncoder;
import com.xeiam.xchart.Chart;
import com.xeiam.xchart.ChartBuilder;
import com.xeiam.xchart.StyleManager.ChartType;
import com.xeiam.xchart.StyleManager.LegendPosition;

import de.timvinkemeier.haptics.core.Constants;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;
import de.timvinkemeier.haptics.extensions.Log;
import de.timvinkemeier.haptics.extensions.LogLevel;

// TODO: Auto-generated Javadoc
/**
 * The Class Report.
 */
public class Report {

	/**
	 * Generate.
	 *
	 * @param outputfolder the outputfolder
	 * @param metrics the metrics
	 * @param hdfs the hdfs
	 * @throws Exception the exception
	 */
	@SuppressWarnings("static-access")
	public static void generate(String outputfolder, Metrics metrics, FileSystem hdfs) throws Exception {
		Log.print("Generating report to '" + outputfolder + "'...", LogLevel.Verbose);
		FileUtils.forceMkdir(new File(outputfolder));
		metrics.save(FilenameUtils.concat(outputfolder, Constants.MetricsRawFileName));
		String hdfsfile = "### HDFS statistics\n";
		hdfsfile += "## General metrics\n";
		hdfsfile += "Name: " + hdfs.getName() + "\n";
		hdfsfile += "Total used size: " + ExtensionMethods.BytesToFormatted(hdfs.getUsed()) + " (" + hdfs.getUsed() + " Bytes)\n";
		hdfsfile += "Default block size: " + ExtensionMethods.BytesToFormatted(hdfs.getDefaultBlockSize()) + " (" + hdfs.getDefaultBlockSize() + " Bytes)\n";
		hdfsfile += "Default replication: " + hdfs.getDefaultReplication() + "\n";
		hdfsfile += "Home directory: " + hdfs.getHomeDirectory() + "\n";
		hdfsfile += "Working directory: " + hdfs.getWorkingDirectory() + "\n";
		hdfsfile += "\n";
		hdfsfile += "## Statistics\n";
		for (Statistics s : hdfs.getAllStatistics()) {
			hdfsfile += "Scheme: " + s.getScheme() + "\n";
			hdfsfile += "Bytes written: " + ExtensionMethods.BytesToFormatted(s.getBytesWritten()) + " (" + s.getBytesWritten() + " Bytes)\n";
			hdfsfile += "Bytes read: " + ExtensionMethods.BytesToFormatted(s.getBytesRead()) + " (" + s.getBytesRead() + " Bytes)\n";
			hdfsfile += "Write operations: " + s.getWriteOps() + "\n";
			hdfsfile += "Read operations: " + s.getReadOps() + "\n";
			hdfsfile += "LargeRead operations: " + s.getLargeReadOps() + "\n";
			hdfsfile += "\n";
		}
		FileUtils.writeStringToFile(new File(FilenameUtils.concat(outputfolder, Constants.HDFSStatisticsFileName)), hdfsfile);

		if (metrics.getJobMetrics().isEmpty()) {
			Log.print("No JobMetrics found, cannot create charts.", LogLevel.Important);
			return;
		}

		// Inner class sorts SIDs using their jobID
		// (format: <jobname> (<jobID>))
		class SIDComparator implements Comparator<String> {

			@Override
			public int compare(String o1, String o2) {
				o1 = o1.substring(o1.indexOf("(") + 1, o1.length() - 1);
				o2 = o2.substring(o2.indexOf("(") + 1, o2.length() - 1);
				return o1.compareTo(o2);
			}

		}

		// mapprogress chart
		Chart chart = new ChartBuilder().height(1080).width(1920).title("Map Progress over time").chartType(ChartType.Line).xAxisTitle("Step").yAxisTitle("Map Progress").build();
		chart.getStyleManager().setLegendPosition(LegendPosition.OutsideE);
		HashMap<String, double[]> map = new HashMap<>();
		String oneSID = "";

		List<String> sids = new ArrayList<>();
		for (String s : metrics.getJobMetrics().keySet())
			sids.add(s);
		Collections.sort(sids);
		Collections.sort(sids, new SIDComparator());
		for (String sid : sids) {
			map.put(sid, metrics.getJobMetrics().get(sid).getAsArray(Phase.MAP));
			oneSID = sid;
		}
		int length = map.get(oneSID).length;
		double[] xData = new double[length];
		for (int i = 0; i < length; i++)
			xData[i] = (double) (i + 1);
		for (String sid : sids) {
			chart.addSeries(sid, xData, map.get(sid));
		}
		BitmapEncoder.savePNG(chart, FilenameUtils.concat(outputfolder, Constants.MapProgressChartFileName));

		// reduceprogress chart
		chart = new ChartBuilder().height(1080).width(1920).title("Reduce Progress over time").chartType(ChartType.Line).xAxisTitle("Step").yAxisTitle("Reduce Progress").build();
		chart.getStyleManager().setLegendPosition(LegendPosition.OutsideE);
		map = new HashMap<>();
		oneSID = "";
		for (String sid : sids) {
			map.put(sid, metrics.getJobMetrics().get(sid).getAsArray(Phase.REDUCE));
			oneSID = sid;
		}
		length = map.get(oneSID).length;
		xData = new double[length];
		for (int i = 0; i < length; i++)
			xData[i] = (double) (i + 1);
		for (String sid : sids) {
			chart.addSeries(sid, xData, map.get(sid));
		}
		BitmapEncoder.savePNG(chart, FilenameUtils.concat(outputfolder, Constants.ReduceProgressChartFileName));

		// combined progress chart
		chart = new ChartBuilder().height(1080).width(1920).title("Combined Progress over time").chartType(ChartType.Line).xAxisTitle("Step").yAxisTitle("Progress").build();
		chart.getStyleManager().setLegendPosition(LegendPosition.OutsideE);
		map = new HashMap<>();
		oneSID = "";
		for (String sid : sids) {
			double[] arrayMap = metrics.getJobMetrics().get(sid).getAsArray(Phase.MAP);
			double[] arrayReduce = metrics.getJobMetrics().get(sid).getAsArray(Phase.REDUCE);
			double[] array = new double[Math.max(arrayMap.length, arrayReduce.length)];
			for (int i = 0; i < array.length; i++) {
				array[i] = (i < arrayMap.length ? arrayMap[i] : 0) + (i < arrayReduce.length ? arrayReduce[i] : 0);
			}
			map.put(sid, array);
			oneSID = sid;
		}
		length = map.get(oneSID).length;
		xData = new double[length];
		for (int i = 0; i < length; i++)
			xData[i] = (double) (i + 1);
		for (String sid : sids) {
			chart.addSeries(sid, xData, map.get(sid));
		}
		chart.getStyleManager().setPlotGridLinesVisible(true);
		BitmapEncoder.savePNG(chart, FilenameUtils.concat(outputfolder, Constants.CombinedProgressChartFileName));

		// job execution overview
		chart = new ChartBuilder().height(1080).width(1920).title("Schedule item execution intervals").chartType(ChartType.Scatter).xAxisTitle("Item").yAxisTitle("Step").build();
		chart.getStyleManager().setLegendPosition(LegendPosition.OutsideE);
		List<String> l = new ArrayList<>();
		for (String sid : metrics.getJobMetrics().keySet()) {
			l.add(sid);
		}
		Collections.sort(l);
		Collections.sort(l, new SIDComparator());
		long id = 0;
		for (String sid : l) {
			long start = 0;
			long end = 0;
			for (JobSnap snap : metrics.getJobMetrics().get(sid).getSnaps()) {
				if (snap.getMapProgress() > 0 && start == 0)
					start = snap.getSnapTime();
				if (snap.isCompleted() && end == 0) {
					end = snap.getSnapTime();
					break;
				}
			}
			chart.addSeries(sid, Arrays.asList((Number) id), Arrays.asList((Number) ((end + start) / 2)), Arrays.asList((Number) ((end - start) / 2)));
			id++;
		}
		chart.getStyleManager().setPlotGridLinesVisible(true);
		BitmapEncoder.savePNG(chart, FilenameUtils.concat(outputfolder, Constants.ItemExecutionIntervalsChartFileName));
	}
}
