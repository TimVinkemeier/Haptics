package de.timvinkemeier.haptics.core;

// TODO: Auto-generated Javadoc
/**
 * The Enum Phase.
 */
public enum Phase {
	
	/** The Validation. */
	Validation, 
 /** The Setup preprocessing. */
 SetupPreprocessing, 
 /** The Upload. */
 Upload, 
 /** The Job creation. */
 JobCreation, 
 /** The Schedule execution. */
 ScheduleExecution, 
 /** The Download. */
 Download, 
 /** The Cleanup postprocessing. */
 CleanupPostprocessing, 
 /** The Report generation. */
 ReportGeneration;

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
			case Validation:
				return "Validation";
			case SetupPreprocessing:
				return "SetupPreprocessing";
			case Upload:
				return "Upload";
			case JobCreation:
				return "JobCreation";
			case ScheduleExecution:
				return "ScheduleExecution";
			case Download:
				return "Download";
			case CleanupPostprocessing:
				return "CleanupPostprocessing";
			case ReportGeneration:
				return "ReportGeneration";
			default:
				return "<unknown phase>";
		}
	}
}
