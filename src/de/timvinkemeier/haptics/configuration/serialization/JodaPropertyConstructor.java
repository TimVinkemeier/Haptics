package de.timvinkemeier.haptics.configuration.serialization;

import java.util.Date;

import org.joda.time.LocalDateTime;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.Tag;

// TODO: Auto-generated Javadoc
/**
 * The Class JodaPropertyConstructor.
 */
public class JodaPropertyConstructor extends Constructor {

	/**
	 * Instantiates a new joda property constructor.
	 */
	public JodaPropertyConstructor() {
		yamlClassConstructors.put(NodeId.scalar, new TimeStampConstruct());
	}

	/**
	 * The Class TimeStampConstruct.
	 */
	class TimeStampConstruct extends Constructor.ConstructScalar {
		
		/**
		 * @see org.yaml.snakeyaml.constructor.Constructor.ConstructScalar#construct(org.yaml.snakeyaml.nodes.Node)
		 */
		@Override
		public Object construct(Node nnode) {
			if (nnode.getTag().equals(Tag.TIMESTAMP)) {
				Construct dateConstructor = yamlConstructors.get(Tag.TIMESTAMP);
				Date date = (Date) dateConstructor.construct(nnode);
				return LocalDateTime.fromDateFields(date);
			} else {
				return super.construct(nnode);
			}
		}
	}
}
