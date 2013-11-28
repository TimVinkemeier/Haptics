package de.timvinkemeier.haptics.builtins;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

// TODO: Auto-generated Javadoc
/**
 * The Class LineBegin.
 */
public class LineBegin {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.err.println("This jar file is not intended to be run as a standalone. Use only with Haptics!");
	}

	/**
	 * The Class LineBeginMapper.
	 */
	public static class LineBeginMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

		/**
		 * @see org.apache.hadoop.mapreduce.Mapper#map(KEYIN, VALUEIN, org.apache.hadoop.mapreduce.Mapper.Context)
		 */
		@Override
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			try {
				context.write(new Text(value.toString().substring(0, 1)), new IntWritable(1));
			} catch (Exception ex) {
				context.write(new Text("<empty line>"), new IntWritable(1));
			}
		}
	}

	/**
	 * The Class LineBeginReducer.
	 */
	public static class LineBeginReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

		/**
		 * @see org.apache.hadoop.mapreduce.Reducer#reduce(KEYIN, java.lang.Iterable, org.apache.hadoop.mapreduce.Reducer.Context)
		 */
		@Override
		public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
			int count = 0;
			for (IntWritable i : values) {
				count += i.get();
			}
			context.write(key, new IntWritable(count));
		}

	}

}
