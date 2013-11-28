package de.timvinkemeier.haptics.configuration.constraints;

import java.util.Collection;

import de.timvinkemeier.haptics.core.HJobBase;
import de.timvinkemeier.haptics.exceptions.InvalidDefinitionException;

// TODO: Auto-generated Javadoc
/**
 * The Class ExpressionBase.
 */
public abstract class ExpressionBase {

	/**
	 * Evaluate this expression. Has to be overridden in child classes.
	 *
	 * @param items the items
	 * @return true, if successful
	 * @throws InvalidDefinitionException the invalid definition exception
	 */
	public abstract boolean evaluate(Collection<HJobBase> items) throws InvalidDefinitionException;

}
