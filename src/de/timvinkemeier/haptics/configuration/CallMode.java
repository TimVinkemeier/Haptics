package de.timvinkemeier.haptics.configuration;

// TODO: Auto-generated Javadoc
/**
 * The possible callmodes. CallConfiguration uses this to determine which
 * actions should be performed by Haptics.
 */
public enum CallMode {

	/**
	 * The Mode in which only help will be displayed.
	 * 
	 * This is used if no arguments, -? or -help is given.
	 */
	Help,

	/**
	 * The Mode in which the Haptics setup will be tested. (HDFS connection,
	 * JobTracker connection and SSH)
	 * 
	 * This is used if -testsetup or -ts is given.
	 */
	TestSetup,

	/**
	 * The Mode in which Haptics tests are run.
	 * 
	 * This is used if -run is given with the necessary arguments.
	 */
	RunTest,

	/**
	 * The mode in which a pure cleanup is performed (clearing input and output
	 * folders on HDFS)
	 * 
	 * This is used if -c is given with the necessary arguments.
	 * */
	Cleanup,

	/**
	 * The mode in which a template haptics file is created at the given
	 * location.
	 * 
	 * This is used if --template is given with the necessary arguments.
	 */
	Template
}
