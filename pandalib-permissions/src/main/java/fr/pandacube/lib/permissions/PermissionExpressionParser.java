package fr.pandacube.lib.permissions;

import java.util.Iterator;
import java.util.function.Function;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Constant;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Operator.Associativity;
import com.fathzer.soft.javaluator.Parameters;

/**
 * Class that evaluates a permission string as if it was a boolean expression with permission nodes as variables.
 * <p>
 * A permission expression contains permission nodes, boolean operators ({@code "||"}, {@code "&&"} and {@code "!"}) and
 * literal values {@code "true"} and {@code "false"}.
 * Here are some example of permission expressions:
 * <pre>{@code
 * "p1.cmd"
 * "!p1.toto"
 * "p1.cmd!"
 * "p1.cmd || p1.toto"
 * "p1.cmd && p1.toto"
 * "p1.cmd && !p1.toto  "
 * "p1.cmd && true"
 * "false || p2.cmd"
 * }</pre>
 * Notice that spaces around permission nodes and operators does not affect the results of the parsing.
 */
public class PermissionExpressionParser {
	
	private static final PermissionEvaluator PERMISSION_EVALUATOR = new PermissionEvaluator();

	/**
	 * Evaluate the provided permission expression, testing each permission with the provided permTester.
	 *
	 * @param permString the permission expression to evaluate.
	 * @param permTester a function that gives the value of the provided permission node. It is usually a method
	 *                   reference to the {@code hasPermission(String)} method the player we want to test the
	 *                   permissions.
	 * @throws IllegalArgumentException if the expression is not correct.
	 * @return the result of the evaluation of the permission expression.
	 */
	public static boolean evaluate(String permString, LiteralPermissionTester permTester) {
		try {
			return PERMISSION_EVALUATOR.evaluate(permString, permTester);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Can’t evaluate the provided permission expression: '" + permString + "'", e);
		}
	}

	/**
	 * Functional interface that converts a string into a boolean.
	 */
	public interface LiteralPermissionTester extends Function<String, Boolean> { }
	
	
	
	
	
	private static class PermissionEvaluator extends AbstractEvaluator<Boolean> {

		private static final Operator NOT = new Operator("!", 1, Associativity.LEFT, 3);
		private static final Operator AND = new Operator("&&", 2, Associativity.LEFT, 2);
		private static final Operator OR = new Operator("||", 2, Associativity.LEFT, 1);
		private static final Constant TRUE = new Constant("true");
		private static final Constant FALSE = new Constant("false");
		
		
		private static final Parameters PARAMETERS;
		
		static {
			PARAMETERS = new Parameters();
			PARAMETERS.add(NOT);
			PARAMETERS.add(AND);
			PARAMETERS.add(OR);
			PARAMETERS.add(TRUE);
			PARAMETERS.add(FALSE);
			PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
		}
		
		
		
		public PermissionEvaluator() {
			super(PARAMETERS);
		}
		
		@Override
		protected Boolean toValue(String literal, Object evaluationContext) {
			if (literal.contains(" ") || literal.contains("|") || literal.contains("&"))
				throw new IllegalArgumentException("Unable to parse the following part of permission expression as one permission node: '" + literal + "'");
			return evaluationContext instanceof LiteralPermissionTester pt ? pt.apply(literal) : false;
		}
		
		@Override
		protected Boolean evaluate(Operator operator, Iterator<Boolean> operands, Object evaluationContext) {
			if (operator == NOT) {
				return !operands.next();
			} else if (operator == OR) {
				Boolean o1 = operands.next();
				Boolean o2 = operands.next();
				return o1 || o2;
			} else if (operator == AND) {
				Boolean o1 = operands.next();
				Boolean o2 = operands.next();
				return o1 && o2;
			} else {
				return super.evaluate(operator, operands, evaluationContext);
			}
		}
		
		@Override
		protected Boolean evaluate(Constant constant, Object evaluationContext) {
			if (constant == TRUE)
				return true;
			if (constant == FALSE)
				return false;
			return super.evaluate(constant, evaluationContext);
		}
	}

	private PermissionExpressionParser() {}
	
}
