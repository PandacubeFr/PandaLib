package fr.pandacube.lib.permissions;

import java.util.Iterator;
import java.util.function.Function;

import com.fathzer.soft.javaluator.AbstractEvaluator;
import com.fathzer.soft.javaluator.BracketPair;
import com.fathzer.soft.javaluator.Constant;
import com.fathzer.soft.javaluator.Operator;
import com.fathzer.soft.javaluator.Operator.Associativity;
import com.fathzer.soft.javaluator.Parameters;

public class PermissionExpressionParser {
	
	private static final PermissionEvaluator PERMISSION_EVALUATOR = new PermissionEvaluator();

	public static boolean evaluate(String permString, LitteralPermissionTester permTester) {
		try {
			return PERMISSION_EVALUATOR.evaluate(permString, permTester);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Can’t evaluate the provided permission expression: '" + permString + "'", e);
		}
	}

	public interface LitteralPermissionTester extends Function<String, Boolean> { }
	
	
	
	
	
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
			return evaluationContext instanceof LitteralPermissionTester pt ? pt.apply(literal) : false;
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
	
	
	/* TODO move to test code
	public static void main(String[] args) {
		java.util.List<String> pList = java.util.Arrays.asList("p1.cmd", "p1.toto", "p2.lol");
		LitteralPermissionTester tester = p -> pList.contains(p);
		
		for (String permExpr : java.util.Arrays.asList(
				"p1.cmd", // true
				"p1.notexist", // false
				"p2lol.lol", // false
				"!p1.notexist", // true
				"!p1.cmd", // false
				"p1.cmd!", // false
				"p1.cmd! p2.lol", // exception
				"p1.cmd || p1.toto", // true || true == true
				"p1.cmd || p1.notexist", // true || false == true
				"p1.fefef || p2.lol", // false || true == true
				"p1.fefef || p2.lolilol", // false || false == false
				"p1.cmd && p1.toto", // true && true == true
				"p1.cmd && p1.notexist", // true && false == false
				"p1.fefef && p2.lol", // false && true == false
				"p1.fefef && p2.lolilol", // false && false == false
				"p1.cmd && !p1.toto  ", // true && !true == false
				"   !p1.cmd &&    p1.toto", // !true && true == false
				"!p1.cmd & p1.toto", // exception
				"!p1.cmd | p1.toto", // exception
				"p1.not exist" // exception
				)) {
			try {
				System.out.println(permExpr + " -> " + evaluate(permExpr, tester));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	*/
	
}
