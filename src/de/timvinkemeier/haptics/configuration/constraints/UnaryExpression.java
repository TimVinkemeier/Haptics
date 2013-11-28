package de.timvinkemeier.haptics.configuration.constraints;

import java.util.Collection;

import de.timvinkemeier.haptics.core.HJobBase;
import de.timvinkemeier.haptics.exceptions.InvalidDefinitionException;

// TODO: Auto-generated Javadoc
/**
 * The Class UnaryExpression.
 */
public class UnaryExpression extends ExpressionBase {
	
	/** The base. */
	private ExpressionBase base;
	
	/** The op. */
	private UnaryConstraintOperator op;

	/**
	 * Instantiates a new unary expression.
	 *
	 * @param base the base
	 * @param op the op
	 */
	public UnaryExpression(ExpressionBase base, UnaryConstraintOperator op) {
		this.base = base;
		this.op = op;
	}

	/**
	 * @see de.timvinkemeier.haptics.configuration.constraints.ExpressionBase#evaluate(java.util.Collection)
	 */
	@Override
	public boolean evaluate(Collection<HJobBase> collection) throws InvalidDefinitionException {
		switch (op) {
			case NOT:
				return !(base.evaluate(collection));
			default:
				throw new InvalidDefinitionException("Unexpected operator in UnaryExpression.evaluate().");
		}
	}

}
