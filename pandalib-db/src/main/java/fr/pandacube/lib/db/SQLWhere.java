package fr.pandacube.lib.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.pandacube.lib.util.Log;

public abstract class SQLWhere<E extends SQLElement<E>> {

	public abstract ParameterizedSQLString toSQL() throws DBException;

	@Override
	public String toString() {
		try {
			return toSQL().sqlString();
		} catch (DBException e) {
			Log.warning(e);
			return "[SQLWhere.toString() error (see logs)]";
		}
	}
	
	public SQLWhereAnd<E> and(SQLWhere<E> other) {
		return new SQLWhereAnd<E>().and(this).and(other);
	}
	
	public SQLWhereOr<E> or(SQLWhere<E> other) {
		return new SQLWhereOr<E>().or(this).or(other);
	}
	
	public static <E extends SQLElement<E>> SQLWhereAnd<E> and() {
		return new SQLWhereAnd<>();
	}
	
	public static <E extends SQLElement<E>> SQLWhereOr<E> or() {
		return new SQLWhereOr<>();
	}
	
	public static String escapeLike(String str) {
		return str.replace("\\", "\\\\").replace("_", "\\_").replace("%", "\\%");
	}
	
	

	
	
	
	
	
	public static abstract class SQLWhereChain<E extends SQLElement<E>> extends SQLWhere<E> {

		private final SQLBoolOp operator;
		private final List<SQLWhere<E>> conditions = new ArrayList<>();

		private SQLWhereChain(SQLBoolOp op) {
			if (op == null) throw new IllegalArgumentException("op can't be null");
			operator = op;
		}

		protected void add(SQLWhere<E> sqlWhere) {
			if (sqlWhere == null) throw new IllegalArgumentException("sqlWhere can't be null");
			conditions.add(sqlWhere);
		}
		
		public boolean isEmpty() {
			return conditions.isEmpty();
		}
		
		@Override
		public ParameterizedSQLString toSQL() throws DBException {
			if (conditions.isEmpty()) {
				throw new DBException("SQLWhereChain needs at least one element inside !");
			}
			
			StringBuilder sql = new StringBuilder();
			List<Object> params = new ArrayList<>();
			boolean first = true;

			for (SQLWhere<E> w : conditions) {
				if (!first)
					sql.append(" ").append(operator.sql).append(" ");
				first = false;

				ParameterizedSQLString ret = w.toSQL();
				sql.append("(").append(ret.sqlString()).append(")");
				params.addAll(ret.parameters());
			}

			return new ParameterizedSQLString(sql.toString(), params);
		}

		protected enum SQLBoolOp {
			/** Equivalent to SQL "<code>AND</code>" */
			AND("AND"),
			/** Equivalent to SQL "<code>OR</code>" */
			OR("OR");
			/* package */ final String sql;

			SQLBoolOp(String s) {
				sql = s;
			}

		}

	}
	
	
	
	
	
	
	
	
	public static class SQLWhereAnd<E extends SQLElement<E>> extends SQLWhereChain<E> {

		private SQLWhereAnd() {
			super(SQLBoolOp.AND);
		}
		
		@Override
		public SQLWhereAnd<E> and(SQLWhere<E> other) {
			add(other);
			return this;
		}

	}
	
	
	
	
	
	
	public static class SQLWhereOr<E extends SQLElement<E>> extends SQLWhereChain<E> {

		private SQLWhereOr() {
			super(SQLBoolOp.OR);
		}
		
		@Override
		public SQLWhereOr<E> or(SQLWhere<E> other) {
			add(other);
			return this;
		}

	}
	
	
	
	
	
	
	/* package */ static class SQLWhereComp<E extends SQLElement<E>> extends SQLWhere<E> {

		private final SQLField<E, ?> left;
		private final SQLComparator comp;
		private final Object right;

		/**
		 * Compare a field with a value
		 *
		 * @param l the field at left of the comparison operator. Can't be null
		 * @param c the comparison operator, can't be null
		 * @param r the value at right of the comparison operator. Can't be null
		 */
		/* package */ <T> SQLWhereComp(SQLField<E, T> l, SQLComparator c, T r) {
			if (l == null || r == null || c == null)
				throw new IllegalArgumentException("All arguments for SQLWhereComp constructor can't be null");
			left = l;
			comp = c;
			right = r;
		}

		@Override
		public ParameterizedSQLString toSQL() throws DBException {
			List<Object> params = new ArrayList<>();
			SQLElement.addValueToSQLObjectList(params, left, right);
			return new ParameterizedSQLString("`" + left.getName() + "` " + comp.sql + " ? ", params);
		}

		/* package */ enum SQLComparator {
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

			/* package */ final String sql;

			SQLComparator(String s) {
				sql = s;
			}

		}

	}
	
	
	
	
	
	
	/* package */ static class SQLWhereIn<E extends SQLElement<E>> extends SQLWhere<E> {

		private final SQLField<E, ?> field;
		private final Collection<?> values;

		/* package */ <T> SQLWhereIn(SQLField<E, T> f, Collection<T> v) {
			if (f == null || v == null)
				throw new IllegalArgumentException("All arguments for SQLWhereIn constructor can't be null");
			field = f;
			values = v;
		}

		@Override
		public ParameterizedSQLString toSQL() throws DBException {
			List<Object> params = new ArrayList<>();
			
			if (values.isEmpty())
				return new ParameterizedSQLString(" 1=0 ", params);
			
			for (Object v : values)
				SQLElement.addValueToSQLObjectList(params, field, v);
			
			char[] questions = new char[values.size() == 0 ? 0 : (values.size() * 2 - 1)];
			for (int i = 0; i < questions.length; i++)
				questions[i] = i % 2 == 0 ? '?' : ',';
			
			return new ParameterizedSQLString("`" + field.getName() + "` IN (" + new String(questions) + ") ", params);
		}

	}
	
	
	
	
	
	
	
	
	/* package */ static class SQLWhereLike<E extends SQLElement<E>> extends SQLWhere<E> {

		private final SQLField<E, ?> field;
		private final String likeExpr;

		/**
		 * Compare a field with a value
		 *
		 * @param f the field at left of the LIKE keyword. Can't be null
		 * @param like the like expression.
		 */
		/* package */ SQLWhereLike(SQLField<E, ?> f, String like) {
			if (f == null || like == null)
				throw new IllegalArgumentException("All arguments for SQLWhereLike constructor can't be null");
			field = f;
			likeExpr = like;
		}

		@Override
		public ParameterizedSQLString toSQL() {
			ArrayList<Object> params = new ArrayList<>();
			params.add(likeExpr);
			return new ParameterizedSQLString("`" + field.getName() + "` LIKE ? ", params);
		}

	}
	
	
	
	
	
	
	
	/* package */ static class SQLWhereNull<E extends SQLElement<E>> extends SQLWhere<E> {

		private final SQLField<E, ?> field;
		private final boolean isNull;

		/**
		 * Init a IS NULL / IS NOT NULL expression for a SQL WHERE condition.
		 *
		 * @param field the field to check null / not null state
		 * @param isNull true if we want to ckeck if "IS NULL", or false to check if
		 *        "IS NOT NULL"
		 */
		/* package */ SQLWhereNull(SQLField<E, ?> field, boolean isNull) {
			if (field == null)
				throw new IllegalArgumentException("field can't be null");
			if (!field.canBeNull)
				Log.warning("Useless : Trying to check IS [NOT] NULL on the field " + field.getSQLElementType().getName()
						+ "#" + field.getName() + " which is declared in the ORM as 'can't be null'");
			this.field = field;
			this.isNull = isNull;
		}

		@Override
		public ParameterizedSQLString toSQL() {
			return new ParameterizedSQLString("`" + field.getName() + "` IS " + ((isNull) ? "NULL" : "NOT NULL"), new ArrayList<>());
		}

	}

}
