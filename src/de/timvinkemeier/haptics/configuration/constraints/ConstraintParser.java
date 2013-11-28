package de.timvinkemeier.haptics.configuration.constraints;

import com.sun.el.parser.ParseException;

// TODO: Auto-generated Javadoc
/**
 * The Class ConstraintParser.
 */
public class ConstraintParser {
	
	/** The exp. */
	private String exp;
	
	/** The pos. */
	private int pos = 0;

	/**
	 * Instantiates a new constraint parser.
	 *
	 * @param s the s
	 */
	public ConstraintParser(String s) {
		exp = s.toLowerCase().replace("running", "r").replace("finished", "f").replace("successful", "s").replace(" ", "").replace("\t", "").replace("\r", "").replace("\n", "");
	}

	/**
	 * Parses the expression.
	 * 
	 * @return the expression base
	 * @throws ParseException
	 *             the parse exception
	 */
	public ExpressionBase parse() throws ParseException {
		ExpressionBase leftBase = null;
		ExpressionBase rightBase = null;
		while (pos < exp.length() - 1) {
			char token = exp.charAt(pos);
			pos++;
			switch (token) {
				case '(':
					if (leftBase == null) {
						leftBase = parse();
					} else if (rightBase == null) {
						rightBase = parse();
					} else {
						throw new ParseException("Unexpected token '(' at position " + (pos - 1) + " in '" + exp + "'.");
					}
					break;
				case ')':
					return leftBase;
				case '&':
					if (leftBase == null || rightBase != null) {
						throw new ParseException("Unexpected token '&' at position " + (pos - 1) + " in '" + exp + "'.");
					} else {
						return new BinaryExpression(leftBase, parse(), BinaryConstraintOperator.AND);
					}
				case '|':
					if (leftBase == null || rightBase != null) {
						throw new ParseException("Unexpected token '|' at position " + (pos - 1) + " in '" + exp + "'.");
					} else {
						return new BinaryExpression(leftBase, parse(), BinaryConstraintOperator.OR);
					}
				case '^':
					if (leftBase == null || rightBase != null) {
						throw new ParseException("Unexpected token '^' at position " + (pos - 1) + " in '" + exp + "'.");
					} else {
						return new BinaryExpression(leftBase, parse(), BinaryConstraintOperator.XOR);
					}
				case '!':
					if (leftBase != null || rightBase != null) {
						throw new ParseException("Unexpected token '!' at position " + (pos - 1) + " in '" + exp + "'.");
					} else {
						return new UnaryExpression(parse(), UnaryConstraintOperator.NOT);
					}
				case 's':
					if (leftBase != null) {
						throw new ParseException("Unexpected token 's' at position " + (pos - 1) + " in '" + exp + "'.");
					}
					String itemID = "";
					char s;
					// skip '['
					pos++;
					while ((s = exp.charAt(pos)) != ']') {
						itemID += s;
						pos++;
					}
					pos++;
					leftBase = new SimpleExpression(itemID, SimpleExpressionType.SUCCESSFULL);
					break;
				case 'f':
					if (leftBase != null) {
						throw new ParseException("Unexpected token 'f' at position " + (pos - 1) + " in '" + exp + "'.");
					}
					itemID = "";
					// skip '['
					pos++;
					while ((s = exp.charAt(pos)) != ']') {
						itemID += s;
						pos++;
					}
					pos++;
					leftBase = new SimpleExpression(itemID, SimpleExpressionType.FINISHED);
					break;
				case 'r':
					if (leftBase != null) {
						throw new ParseException("Unexpected token 'r' at position " + (pos - 1) + " in '" + exp + "'.");
					}
					itemID = "";
					// skip '['
					pos++;
					while ((s = exp.charAt(pos)) != ']') {
						itemID += s;
						pos++;
					}
					pos++;
					leftBase = new SimpleExpression(itemID, SimpleExpressionType.RUNNING);
					break;
				default:
					throw new ParseException("Unexpected token '" + token + "' at position " + (pos - 1) + " in '" + exp + "'.");
			}
		}
		if (leftBase == null) {
			throw new ParseException("Parse error at the end of '" + exp + "' (nothing parsed).");
		}
		return leftBase;
	}
}
