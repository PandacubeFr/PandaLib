package fr.pandacube.java.util.orm;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class SQLWhereChain extends SQLWhere {

	private SQLBoolOp operator;
	private List<SQLWhere> conditions = new ArrayList<>();

	public SQLWhereChain(SQLBoolOp op) {
		if (op == null) throw new IllegalArgumentException("op can't be null");
		operator = op;
	}

	public SQLWhereChain add(SQLWhere sqlWhere) {
		if (sqlWhere == null) throw new IllegalArgumentException("sqlWhere can't be null");
		conditions.add(sqlWhere);
		return this;
	}

	@Override
	public Pair<String, List<Object>> toSQL() throws ORMException {
		String sql = "";
		List<Object> params = new ArrayList<>();
		boolean first = true;

		for (SQLWhere w : conditions) {
			if (!first) sql += " " + operator.sql + " ";
			first = false;

			Pair<String, List<Object>> ret = w.toSQL();
			sql += "(" + ret.getValue0() + ")";
			params.addAll(ret.getValue1());
		}

		return new Pair<>(sql, params);
	}

	public enum SQLBoolOp {
		/** Equivalent to SQL "<code>AND</code>" */
		AND("AND"), /** Equivalent to SQL "<code>OR</code>" */
		OR("OR");
		public final String sql;

		private SQLBoolOp(String s) {
			sql = s;
		}

	}

}
