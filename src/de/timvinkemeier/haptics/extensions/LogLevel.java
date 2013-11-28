package de.timvinkemeier.haptics.extensions;

// TODO: Auto-generated Javadoc
/**
 * The LogLevels for use with Log.
 */
public enum LogLevel {

	/** No messages will be printed. */
	None,
	/** Only critical messages will be printed. */
	Critical,
	/** Same as critical. */
	Default,
	/** Only important and critical messages will be printed. */
	Important,
	/** Info, important and critical messages will be printed. */
	Info,
	/** All messages will be printed. */
	Verbose;

	/**
	 * To string.
	 *
	 * @return the string
	 * @see java.lang.Enum#toString()
	 */
	public String toString() {
		switch (this) {
			case None:
				return "None";
			case Critical:
				return "Critical";
			case Default:
				return "Default (Critical)";
			case Important:
				return "Important";
			case Info:
				return "Info";
			case Verbose:
				return "Verbose";
			default:
				return "Default";
		}
	}

	/**
	 * From string.
	 * 
	 * @param s
	 *            the s
	 * @return the log level
	 */
	public static LogLevel fromString(String s) {
		if (s == null)
			return Critical;
		switch (s) {
			case "None":
				return None;
			case "Critical":
				return Critical;
			case "Default":
			case "Default (Critical)":
				return Default;
			case "Important":
				return Important;
			case "Info":
				return Info;
			case "Verbose":
				return Verbose;
			default:
				return Default;
		}
	}

	/**
	 * To int.
	 * 
	 * @return the int
	 */
	public int toInt() {
		switch (this) {
			case None:
				return 4;
			case Critical:
			case Default:
				return 3;
			case Important:
				return 2;
			case Info:
				return 1;
			case Verbose:
				return 0;
			default:
				return 3;
		}
	}
}
