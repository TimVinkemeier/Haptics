package de.timvinkemeier.haptics.configuration.constraints;

import java.util.Collection;

import com.sun.el.parser.ParseException;

import de.timvinkemeier.haptics.configuration.ExecutionState;
import de.timvinkemeier.haptics.core.HJobBase;
import de.timvinkemeier.haptics.exceptions.InvalidDefinitionException;
import de.timvinkemeier.haptics.extensions.ExtensionMethods;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleExpression.
 */
public class SimpleExpression extends ExpressionBase {

	/** The type. */
	private SimpleExpressionType type;
	
	/** The item id. */
	private String itemID;

	/**
	 * Instantiates a new simple expression.
	 *
	 * @param itemID the item id
	 * @param type the type
	 */
	public SimpleExpression(String itemID, SimpleExpressionType type) {
		this.itemID = itemID;
		this.type = type;
	}

	/**
	 * Evaluate.
	 *
	 * @param items the items
	 * @return true, if successful
	 * @throws InvalidDefinitionException the invalid definition exception
	 * @see de.timvinkemeier.haptics.configuration.constraints.ExpressionBase#evaluate(java.util.List)
	 */
	@Override
	public boolean evaluate(Collection<HJobBase> items) throws InvalidDefinitionException {
		switch (type) {
			case SUCCESSFULL:
				try {
					return ExtensionMethods.getItemByItemID(items, itemID).executionState == ExecutionState.Finished;
				} catch (Exception ex) {
					return false;
				}
			case FINISHED:
				try {
					return ExtensionMethods.getItemByItemID(items, itemID).executionState == ExecutionState.Finished || ExtensionMethods.getItemByItemID(items, itemID).executionState == ExecutionState.Killed || ExtensionMethods.getItemByItemID(items, itemID).executionState == ExecutionState.Exception;
				} catch (Exception ex) {
					return false;
				}
			case RUNNING:
				try {
					return ExtensionMethods.getItemByItemID(items, itemID).executionState == ExecutionState.Running;
				} catch (Exception ex) {
					return false;
				}
			default:
				throw new InvalidDefinitionException("Unexpected type for SimpleExpression.");
		}
	}
}
