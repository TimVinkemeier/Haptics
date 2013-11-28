package de.timvinkemeier.haptics.configuration.serialization;

import org.joda.time.LocalDateTime;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

// TODO: Auto-generated Javadoc
/**
 * The Class JodaTimeRepresenter.
 */
public class JodaTimeRepresenter extends Representer {
	
	/**
	 * Instantiates a new joda time representer.
	 */
	public JodaTimeRepresenter() {
		multiRepresenters.put(LocalDateTime.class, new RepresentJodaDateTime());
	}

	/**
	 * The Class RepresentJodaDateTime.
	 */
	private class RepresentJodaDateTime extends RepresentDate {
		
		/**
		 * @see org.yaml.snakeyaml.representer.SafeRepresenter.RepresentDate#representData(java.lang.Object)
		 */
		public Node representData(Object data) {
			LocalDateTime date = (LocalDateTime) data;
			return super.representData(date.toDate());
		}
	}
}
