package de.timvinkemeier.haptics.extensions;

import java.io.IOException;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

// TODO: Auto-generated Javadoc
/**
 * The Class HDFS.
 */
public class HDFS {
	
	/**
	 * Gets the file paths.
	 *
	 * @param hdfs the hdfs
	 * @param dirpath the dirpath
	 * @return the file paths
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static String[] getFilePaths(FileSystem hdfs, String dirpath) throws IOException {
		return getFilePaths(hdfs, dirpath, true);
	}

	/**
	 * Returns an array containing all paths of objects that lie beneath the
	 * given directory. (Paths include the namenode path if withNameNode is
	 * true, else the part is removed)
	 * 
	 * @param hdfs
	 *            The HDFS FileSystem object.
	 * @param dirpath
	 *            The path to list the files beneath.
	 * @param withNamenode
	 *            If true, the namenode url will be included in the paths. If
	 *            false, they will not be included.
	 * @return The filepaths
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static String[] getFilePaths(FileSystem hdfs, String dirpath, boolean withNamenode) throws IOException {
		FileStatus[] stati = hdfs.listStatus(new Path(dirpath));
		String[] paths = new String[stati.length];
		int i = 0;
		for (FileStatus fs : stati) {
			paths[i] = fs.getPath().toString();
			i++;
		}
		return paths;
	}
}
