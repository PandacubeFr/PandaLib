package fr.pandacube.java.util.orm;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class SQLWhereComp extends SQLWhere {

	private SQLField<?, ?> left;
	private SQLComparator comp;
	private Object right;

	/**
	 * Compare a field with a value
	 *
	 * @param l the field at left of the comparison operator. Can't be null
	 * @param c the comparison operator, can't be null
	 * @param r the value at right of the comparison operator. Can't be null
	 */
	public <T> SQLWhereComp(SQLField<?, T> l, SQLComparator c, T r) {
		if (l == null || r == null || c == null)
			throw new IllegalArgumentException("All arguments for SQLWhereComp constructor can't be null");
		left = l;
		comp = c;
		right = r;
	}

	@Override
	public Pair<String, List<Object>> toSQL() throws ORMException {
		List<Object> params = new ArrayList<>();
		SQLElement.addValueToSQLObjectList(params, left, right);
		return new Pair<>(left.getName() + " " + comp.sql + " ? ", params);
	}

	public enum SQLComparator {
		/** Equivalent to SQL "<code>=</code>" */
		EQ("="),
		/** Equivalent to SQL "<code>></code>" */
		GT(">"),
		/** Equivalent to SQL "<code>>=</code>" */
		GEQ(">="),
		/** Equivalent to SQL "<code>&lt;</code>" */
		LT("<"),
		/** Equivalent to SQL "<code>&lt;=</code>" */
		LEQ("<="),
		/** Equivalent to SQL "<code>!=</code>" */
		NEQ("!=");

		public final String sql;

		private SQLComparator(String s) {
			sql = s;
		}

	}

}
