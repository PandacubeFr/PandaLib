package fr.pandacube.lib.db;

/**
 * Represents a SQL type.
 * <p>
 * The most common types are already declared as static values in {@link SQLElement} class.
 * @param <T> the Java type.
 */
public class SQLType<T> {

    /* package */ final String sqlDeclaration;
    private final Class<T> javaTypes;

    /**
     * Create a new type.
     * @param sqlD the name of the data type in SQL (like {@code "BIGINT"} or {@code "CHAR(16)"}).
     * @param javaT the corresponding java type.
     */
    public SQLType(String sqlD, Class<T> javaT) {
        sqlDeclaration = sqlD;
        javaTypes = javaT;
    }

    @Override
    public String toString() {
        return sqlDeclaration;
    }

    /**
     * Check if the provided object can be used as a possible value for this type.
     * @param val the objet to check.
     * @return true if the provided object can be used as a possible value for this type, false otherwise.
     */
    public boolean isInstance(Object val) {
        return javaTypes.isInstance(val);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SQLType<?> o
                && toString().equals(o.toString());
    }

    /**
     * Gets the corresponding Java type of this type.
     * @return the corresponding Java type of this type.
     */
    public Class<T> getJavaType() {
        return javaTypes;
    }


}
