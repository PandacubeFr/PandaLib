package fr.pandacube.util.orm;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public abstract class SQLWhereChain<E extends SQLElement<E>> extends SQLWhere<E> {

	private SQLBoolOp operator;
	protected List<SQLWhere<E>> conditions = new ArrayList<>();

	/* package */ SQLWhereChain(SQLBoolOp op) {
		if (op == null) throw new IllegalArgumentException("op can't be null");
		operator = op;
	}

	protected void add(SQLWhere<E> sqlWhere) {
		if (sqlWhere == null) throw new IllegalArgumentException("sqlWhere can't be null");
		conditions.add(sqlWhere);
	}
	
	@Override
	public Pair<String, List<Object>> toSQL() throws ORMException {
		if (conditions.isEmpty()) {
			throw new ORMException("SQLWhereChain needs at least one element inside !");
		}
		
		String sql = "";
		List<Object> params = new ArrayList<>();
		boolean first = true;

		for (SQLWhere<E> w : conditions) {
			if (!first) sql += " " + operator.sql + " ";
			first = false;

			Pair<String, List<Object>> ret = w.toSQL();
			sql += "(" + ret.getValue0() + ")";
			params.addAll(ret.getValue1());
		}

		return new Pair<>(sql, params);
	}

	/* package */ enum SQLBoolOp {
		/** Equivalent to SQL "<code>AND</code>" */
		AND("AND"),
		/** Equivalent to SQL "<code>OR</code>" */
		OR("OR");
		/* package */ final String sql;

		private SQLBoolOp(String s) {
			sql = s;
		}

	}

}
