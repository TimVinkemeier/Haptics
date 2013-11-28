package de.timvinkemeier.haptics.configuration.constraints;

import java.util.Collection;

import de.timvinkemeier.haptics.core.HJobBase;
import de.timvinkemeier.haptics.exceptions.InvalidDefinitionException;

// TODO: Auto-generated Javadoc
/**
 * The Class BinaryExpression.
 */
public class BinaryExpression extends ExpressionBase {
	
	/** The left. */
	private ExpressionBase left;
	
	/** The right. */
	private ExpressionBase right;
	
	/** The op. */
	private BinaryConstraintOperator op;

	/**
	 * Instantiates a new binary expression.
	 *
	 * @param leftBase the left base
	 * @param rightBase the right base
	 * @param operator the operator
	 */
	public BinaryExpression(ExpressionBase leftBase, ExpressionBase rightBase, BinaryConstraintOperator operator) {
		left = leftBase;
		right = rightBase;
		op = operator;
	}

	/**
	 * @see de.timvinkemeier.haptics.configuration.constraints.ExpressionBase#evaluate(java.util.Collection)
	 */
	@Override
	public boolean evaluate(Collection<HJobBase> items) throws InvalidDefinitionException {
		switch (op) {
			case AND:
				return left.evaluate(items) && right.evaluate(items);
			case OR:
				return left.evaluate(items) || right.evaluate(items);
			case XOR:
				return left.evaluate(items) ^ right.evaluate(items);
			default:
				throw new InvalidDefinitionException("Unexpected operator in BinaryExpression.evaluate().");
		}
	}
}
