package fr.pandacube.lib.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.pandacube.lib.db.SQLWhere.SQLWhereComp;
import fr.pandacube.lib.db.SQLWhere.SQLWhereComp.SQLComparator;
import fr.pandacube.lib.db.SQLWhere.SQLWhereIn;
import fr.pandacube.lib.db.SQLWhere.SQLWhereLike;
import fr.pandacube.lib.db.SQLWhere.SQLWhereNull;

/**
 * A field in a SQL table.
 * @param <E> the table type.
 * @param <T> the Java type of this field.
 */
public class SQLField<E extends SQLElement<E>, T> {

    private Class<E> sqlElemClass;
    private String name = null;
    /* package */ final SQLType<T> type;
    /* package */ final boolean nullable;
    /* package */ final boolean autoIncrement;
    /* package */ final T defaultValue;

    /* package */ SQLField(SQLType<T> type, boolean nullable, boolean autoIncrement, T deflt) {
        this.type = type;
        this.nullable = nullable;
        this.autoIncrement = autoIncrement;
        defaultValue = deflt;
    }

    /* package */ SQLField(SQLType<T> type, boolean nullable) {
        this(type, nullable, false, null);
    }

    /* package */ SQLField(SQLType<T> type, boolean nullable, boolean autoIncrement) {
        this(type, nullable, autoIncrement, null);
    }

    /* package */ SQLField(SQLType<T> type, boolean nullable, T deflt) {
        this(type, nullable, false, deflt);
    }

    /* package */ ParameterizedSQLString forSQLPreparedStatement() {
        List<Object> params = new ArrayList<>(1);
        if (defaultValue != null && !autoIncrement)
            params.add(defaultValue);
        return new ParameterizedSQLString("`" + getName() + "` " + type.toString() + (nullable ? " NULL" : " NOT NULL")
                + (autoIncrement ? " AUTO_INCREMENT" : "")
                + ((defaultValue == null || autoIncrement) ? "" : " DEFAULT ?"), params);
    }

    /* package */ void setSQLElementType(Class<E> elemClass) {
        sqlElemClass = elemClass;
    }

    /**
     * Gets the type representing the table containing this field.
     * @return the type representing the table containing this field.
     */
    public Class<E> getSQLElementType() {
        return sqlElemClass;
    }

    // only for internal usage, the name determined after the name of the static field holding this instance.
    /* package */ void setName(String n) {
        name = n;
    }

    /**
     * Gets the name of this field.
     * It is automatically determined by the name of the static field holding this instance.
     * @return the name of this field.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type of this field.
     * @return the type of this field.
     */
    public SQLType<T> getType() {
        return type;
    }

    /**
     * Tells if this field accepts null values.
     * @return true if this field is {@code NULL}, false if it’s {@code NOT NULL}.
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * Tells if this field is auto incremented by the database on insertion.
     * @return true if this field is {@code AUTO_INCREMENT}, false otherwise.
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * Gets the default value of this field.
     * @return the default value of this field, or null if there is none.
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /* Don't use this {@code toString()} method in a SQL query, because the default value is not escaped correctly. */
    @Override
    public String toString() {
        return forSQLPreparedStatement().sqlString().replaceFirst("\\?",
                (defaultValue != null && !autoIncrement) ? defaultValue.toString() : "");
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SQLField<?, ?> f
                && f.getName().equals(getName())
                && f.sqlElemClass.equals(sqlElemClass);
    }

    @Override
    public int hashCode() {
        return getName().hashCode() ^ sqlElemClass.hashCode();
    }


    /**
     * Create a SQL {@code WHERE} expression comparing this field with the provided value using the {@code =} operator.
     * @param r the value to compare with.
     * @return a new SQL {@code WHERE} expression.
     */
    public fr.pandacube.lib.db.SQLWhere<E> eq(T r) {
        return comp(SQLComparator.EQ, r);
    }

    /**
     * Create a SQL {@code WHERE} expression comparing this field with the provided value using the {@code >=} operator.
     * @param r the value to compare with.
     * @return a new SQL {@code WHERE} expression.
     */
    public fr.pandacube.lib.db.SQLWhere<E> geq(T r) {
        return comp(SQLComparator.GEQ, r);
    }

    /**
     * Create a SQL {@code WHERE} expression comparing this field with the provided value using the {@code >} operator.
     * @param r the value to compare with.
     * @return a new SQL {@code WHERE} expression.
     */
    public fr.pandacube.lib.db.SQLWhere<E> gt(T r) {
        return comp(SQLComparator.GT, r);
    }

    /**
     * Create a SQL {@code WHERE} expression comparing this field with the provided value using the {@code <=} operator.
     * @param r the value to compare with.
     * @return a new SQL {@code WHERE} expression.
     */
    public fr.pandacube.lib.db.SQLWhere<E> leq(T r) {
        return comp(SQLComparator.LEQ, r);
    }

    /**
     * Create a SQL {@code WHERE} expression comparing this field with the provided value using the {@code <} operator.
     * @param r the value to compare with.
     * @return a new SQL {@code WHERE} expression.
     */
    public fr.pandacube.lib.db.SQLWhere<E> lt(T r) {
        return comp(SQLComparator.LT, r);
    }

    /**
     * Create a SQL {@code WHERE} expression comparing this field with the provided value using the {@code !=} operator.
     * @param r the value to compare with.
     * @return a new SQL {@code WHERE} expression.
     */
    public fr.pandacube.lib.db.SQLWhere<E> neq(T r) {
        return comp(SQLComparator.NEQ, r);
    }

    private fr.pandacube.lib.db.SQLWhere<E> comp(SQLComparator c, T r) {
        if (r == null)
            throw new IllegalArgumentException("The value cannot be null. Use SQLField#isNull(value) or SQLField#isNotNull(value) to check for null values");
        return new SQLWhereComp<>(this, c, r);
    }

    /**
     * Create a SQL {@code WHERE} expression comparing this field with the provided value using the {@code LIKE}
     * keyword.
     * @param like the value to compare with.
     * @return a new SQL {@code WHERE} expression.
     */
    public fr.pandacube.lib.db.SQLWhere<E> like(String like) {
        return new SQLWhereLike<>(this, like);
    }



    /**
     * Create a SQL {@code WHERE} expression testing the presence of this field in the provided collection of value
     * using the {@code IN} keyword.
     * @param v the value to compare with.
     * @return a new SQL {@code WHERE} expression.
     */
    public fr.pandacube.lib.db.SQLWhere<E> in(Collection<T> v) {
        return new SQLWhereIn<>(this, v);
    }



    /**
     * Create a SQL {@code WHERE} expression testing the nullity of this field using the {@code IS NULL} keyword.
     * @return a new SQL {@code WHERE} expression.
     */
    public fr.pandacube.lib.db.SQLWhere<E> isNull() {
        return new SQLWhereNull<>(this, true);
    }

    /**
     * Create a SQL {@code WHERE} expression testing the non-nullity of this field using the {@code IS NOT NULL}
     * keyword.
     * @return a new SQL {@code WHERE} expression.
     */
    public fr.pandacube.lib.db.SQLWhere<E> isNotNull() {
        return new SQLWhereNull<>(this, false);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    /* package */ Object fromJavaTypeToJDBCType(Object value) throws DBException {
        Object ret = value;
        if (value != null && type instanceof SQLCustomType customType) {
            try {
                ret = customType.javaToDbConv.apply(value);
            } catch (Exception e) {
                throw new DBException("Error while converting value of field '" + name + "' with SQLCustomType from " + type.getJavaType()
                        + "(java source) to " + customType.intermediateJavaType + "(jdbc destination). The original value is '" + value + "'", e);
            }
        }
        return ret;
    }

    /* package */ Collection<Object> fromListJavaTypeToJDBCType(Collection<?> values) throws DBException {
        if (values == null)
            return null;
        List<Object> ret = new ArrayList<>(values.size());
        for (Object value : values) {
            ret.add(fromJavaTypeToJDBCType(value));
        }
        return ret;
    }

}
