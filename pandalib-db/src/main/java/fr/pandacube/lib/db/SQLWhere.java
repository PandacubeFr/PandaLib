package fr.pandacube.lib.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import fr.pandacube.lib.util.log.Log;

/**
 * SQL {@code WHERE} expression.
 * @param <E> the table type.
 */
public abstract class SQLWhere<E extends SQLElement<E>> {

    /* package */ abstract ParameterizedSQLString toSQL() throws DBException;

    @Override
    public String toString() {
        try {
            return toSQL().sqlString();
        } catch (DBException e) {
            Log.warning(e);
            return "[SQLWhere.toString() error (see logs)]";
        }
    }


    /**
     * Create a SQL {@code WHERE} expression that is true when this expression {@code AND} the provided expression is
     * true.
     * @param other the other expression.
     * @return a new SQL {@code WHERE} expression.
     */
    public SQLWhere<E> and(SQLWhere<E> other) {
        return SQLWhere.<E>and().and(this).and(other);
    }

    /**
     * Create a SQL {@code WHERE} expression that is true when this expression {@code OR} the provided expression is
     * true.
     * @param other the other expression.
     * @return a new SQL {@code WHERE} expression.
     */
    public SQLWhere<E> or(SQLWhere<E> other) {
        return SQLWhere.<E>or().or(this).or(other);
    }


    /**
     * Create a SQL {@code WHERE} expression builder joining multiple expressions with the {@code AND} operator.
     * @return a new SQL {@code WHERE} expression.
     * @param <E> the table type.
     */
    public static <E extends SQLElement<E>> SQLWhereAndBuilder<E> and() {
        return new SQLWhereAndBuilder<>();
    }

    /**
     * Create a SQL {@code WHERE} expression builder joining multiple expressions with the {@code OR} operator.
     * @return a new SQL {@code WHERE} expression.
     * @param <E> the table type.
     */
    public static <E extends SQLElement<E>> SQLWhereOrBuilder<E> or() {
        return new SQLWhereOrBuilder<>();
    }

    /**
     * Create a custom SQL {@code WHERE} expression.
     * @param whereExpr the raw SQL {@code WHERE} expression.
     * @return a new SQL {@code WHERE} expression.
     */
    public static <E extends SQLElement<E>> SQLWhere<E> expression(String whereExpr) {
        return expression(whereExpr, List.of());
    }

    /**
     * Create a custom SQL {@code WHERE} expression.
     * @param whereExpr the raw SQL {@code WHERE} expression.
     * @param params the parameters of the provided expression.
     * @return a new SQL {@code WHERE} expression.
     */
    public static <E extends SQLElement<E>> SQLWhere<E> expression(String whereExpr, List<Object> params) {
        return new SQLWhereCustomExpression<>(whereExpr, params);
    }

    /**
     * Create a SQL {@code WHERE ... IN ...} expression with a custom left operand.
     * @param leftExpr the raw SQL left operand.
     * @param valuesIn the values on the right of the {@code IN} operator.
     * @return a new SQL {@code WHERE} expression.
     */
    public static <E extends SQLElement<E>> SQLWhere<E> expressionIn(String leftExpr, Collection<?> valuesIn) {
        return expressionIn(leftExpr, List.of(), valuesIn);
    }

    /**
     * Create a SQL {@code WHERE ... IN ...} expression with a custom left operand.
     * @param leftExpr the raw SQL left operand.
     * @param leftParams the parameters of the left operand.
     * @param valuesIn the values on the right of the {@code IN} operator.
     * @return a new SQL {@code WHERE} expression.
     */
    public static <E extends SQLElement<E>> SQLWhere<E> expressionIn(String leftExpr, List<Object> leftParams, Collection<?> valuesIn) {
        return new SQLWhereInCustom<>(leftExpr, leftParams, valuesIn);
    }








    /**
     * A SQL {@code WHERE} expression builder joining multiple expressions with the {@code AND} or {@code OR} operator.
     * @param <E> the table type.
     */
    public static abstract class SQLWhereChainBuilder<E extends SQLElement<E>> extends SQLWhere<E> {

        private final SQLBoolOp operator;
        private final List<SQLWhere<E>> conditions = new ArrayList<>();

        private SQLWhereChainBuilder(SQLBoolOp op) {
            if (op == null) throw new IllegalArgumentException("op can't be null");
            operator = op;
        }

        /* package */ void add(SQLWhere<E> sqlWhere) {
            if (sqlWhere == null) throw new IllegalArgumentException("sqlWhere can't be null");
            conditions.add(sqlWhere);
        }

        /**
         * Tells if this expression builder is empty.
         * The builder must not be empty
         * @return true if this expression builder is empty, false otherwise.
         */
        public boolean isEmpty() {
            return conditions.isEmpty();
        }

        @Override
        /* package */ ParameterizedSQLString toSQL() throws DBException {
            if (conditions.isEmpty()) {
                throw new DBException("SQLWhereChainBuilder needs at least one element inside !");
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

        /* package */ enum SQLBoolOp {
            /** Equivalent to SQL {@code "AND"}. */
            AND("AND"),
            /** Equivalent to SQL {@code "OR"}. */
            OR("OR");
            /* package */ final String sql;

            SQLBoolOp(String s) {
                sql = s;
            }

        }

    }








    /**
     * A SQL {@code WHERE} expression builder joining multiple expressions with the {@code AND} operator.
     * @param <E> the table type.
     */
    public static class SQLWhereAndBuilder<E extends SQLElement<E>> extends SQLWhereChainBuilder<E> {

        private SQLWhereAndBuilder() {
            super(SQLBoolOp.AND);
        }

        @Override
        public SQLWhereAndBuilder<E> and(SQLWhere<E> other) {
            add(other);
            return this;
        }

    }






    /**
     * A SQL {@code WHERE} expression builder joining multiple expressions with the {@code OR} operator.
     * @param <E> the table type.
     */
    public static class SQLWhereOrBuilder<E extends SQLElement<E>> extends SQLWhereChainBuilder<E> {

        private SQLWhereOrBuilder() {
            super(SQLBoolOp.OR);
        }

        @Override
        public SQLWhereOrBuilder<E> or(SQLWhere<E> other) {
            add(other);
            return this;
        }

    }






    /* package */ static class SQLWhereComp<E extends SQLElement<E>> extends SQLWhere<E> {

        private final SQLField<E, ?> left;
        private final SQLComparator comp;
        private final Object right;

        /**
         * Compare a field with a value.
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
        /* package */ ParameterizedSQLString toSQL() throws DBException {
            return new ParameterizedSQLString("`" + left.getName() + "` " + comp.sql + " ? ",
                    List.of(left.fromJavaTypeToJDBCType(right)));
        }

        /* package */ enum SQLComparator {
            /** Equivalent to SQL {@code "="}. */
            EQ("="),
            /** Equivalent to SQL {@code ">"}. */
            GT(">"),
            /** Equivalent to SQL {@code ">="}. */
            GEQ(">="),
            /** Equivalent to SQL {@code "<"}. */
            LT("<"),
            /** Equivalent to SQL {@code "<="}. */
            LEQ("<="),
            /** Equivalent to SQL {@code "!="}. */
            NEQ("!=");

            /* package */ final String sql;

            SQLComparator(String s) {
                sql = s;
            }

        }

    }






    /* package */ static class SQLWhereInCustom<E extends SQLElement<E>> extends SQLWhere<E> {

        private final String leftExpression;
        private final List<Object> leftExpressionParameters;
        protected Collection<?> collectionIn;

        /* package */ <T> SQLWhereInCustom(String leftExpr, List<Object> leftExprParams, Collection<T> collectionIn) {
            if (leftExpr == null)
                throw new IllegalArgumentException("leftExpr can't be null");
            if (leftExprParams == null)
                leftExprParams = List.of();
            if (collectionIn == null)
                collectionIn = List.of();
            leftExpression = leftExpr;
            leftExpressionParameters = leftExprParams;
            this.collectionIn = collectionIn;
        }

        @Override
        /* package */ ParameterizedSQLString toSQL() throws DBException {
            List<Object> params = new ArrayList<>();

            if (collectionIn.isEmpty())
                return new ParameterizedSQLString(" 1=0 ", params);

            params.addAll(leftExpressionParameters);
            params.addAll(collectionIn);

            char[] questions = new char[collectionIn.size() * 2 - 1];
            for (int i = 0; i < questions.length; i++)
                questions[i] = i % 2 == 0 ? '?' : ',';

            return new ParameterizedSQLString("(" + leftExpression + ") IN (" + new String(questions) + ") ", params);
        }

    }






    /* package */ static class SQLWhereIn<E extends SQLElement<E>> extends SQLWhereInCustom<E> {

        private final SQLField<E, ?> field;
        private boolean collectionFiltered = false;

        /* package */ <T> SQLWhereIn(SQLField<E, T> f, Collection<T> v) {
            super("`" + Objects.requireNonNull(f).getName() + "`", List.of(), v);
            field = f;
        }


        @Override
        ParameterizedSQLString toSQL() throws DBException {
            if (!collectionFiltered) {
                collectionIn = field.fromListJavaTypeToJDBCType(collectionIn);
                collectionFiltered = true;
            }
            return super.toSQL();
        }
    }








    /* package */ static class SQLWhereLike<E extends SQLElement<E>> extends SQLWhere<E> {

        private final SQLField<E, ?> field;
        private final String likeExpr;

        /* package */ SQLWhereLike(SQLField<E, ?> f, String like) {
            if (f == null || like == null)
                throw new IllegalArgumentException("All arguments for SQLWhereLike constructor can't be null");
            field = f;
            likeExpr = like;
        }

        @Override
        /* package */ ParameterizedSQLString toSQL() {
            ArrayList<Object> params = new ArrayList<>();
            params.add(likeExpr);
            return new ParameterizedSQLString("`" + field.getName() + "` LIKE ? ", params);
        }

    }







    /* package */ static class SQLWhereNull<E extends SQLElement<E>> extends SQLWhere<E> {

        private final SQLField<E, ?> field;
        private final boolean isNull;

        /* package */ SQLWhereNull(SQLField<E, ?> field, boolean isNull) {
            if (field == null)
                throw new IllegalArgumentException("field can't be null");
            if (!field.nullable)
                Log.warning("Useless : Trying to check IS [NOT] NULL on the field " + field.getSQLElementType().getName()
                        + "#" + field.getName() + " which is declared in the ORM as 'can't be null'");
            this.field = field;
            this.isNull = isNull;
        }

        @Override
        /* package */ ParameterizedSQLString toSQL() {
            return new ParameterizedSQLString("`" + field.getName() + "` IS " + ((isNull) ? "NULL" : "NOT NULL"), new ArrayList<>());
        }

    }







    /* package */ static class SQLWhereCustomExpression<E extends SQLElement<E>> extends SQLWhere<E> {

        private final String sqlExpression;
        private final List<Object> parameters;

        /* package */ SQLWhereCustomExpression(String sqlExpression, List<Object> parameters) {
            if (sqlExpression == null)
                throw new IllegalArgumentException("sqlExpression can't be null");
            if (parameters == null)
                parameters = List.of();
            this.sqlExpression = sqlExpression;
            this.parameters = parameters;
        }

        @Override
            /* package */ ParameterizedSQLString toSQL() {
            return new ParameterizedSQLString(sqlExpression, parameters);
        }

    }









    /**
     * Escapes the {@code \}, {@code _} and  {@code %} in the string to be used in a {@code WHERE ... LIKE} expression.
     * @param str the string to escape.
     * @return the escaped string.
     */
    public static String escapeLike(String str) {
        return str.replace("\\", "\\\\")
                .replace("_", "\\_")
                .replace("%", "\\%");
    }

}
