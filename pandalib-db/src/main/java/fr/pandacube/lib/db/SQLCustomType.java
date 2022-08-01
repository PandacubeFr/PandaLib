package fr.pandacube.lib.db;

import java.util.function.Function;

/**
 * Represents a SQL type that needs conversion from and to the JDBC values.
 * <p>
 * For instance, if there is a UUID field in a table, it’s possible to create a new type as follows:
 * <pre>{@code
 * SQLType<UUID> UUID = new SQLCustomType<>(SQLElement.CHAR(36), UUID.class, UUID::fromString, UUID::toString);
 * }</pre>
 * @param <IT> intermediate Java type, the type of the value transmitted to the JDBC.
 * @param <JT> the final Java type.
 */
public class SQLCustomType<IT, JT> extends SQLType<JT> {

    /* package */ final Class<IT> intermediateJavaType;
    /* package */ final Function<IT, JT> dbToJavaConv;
    /* package */ final Function<JT, IT> javaToDbConv;

    /**
     * Creates a new custom type, using a type that is already managed by JDBC and already has a {@link SQLType}
     * instance, like {@link SQLElement#VARCHAR(int)}.
     * @param type the raw {@link SQLType} instance.
     * @param javaT the class of the Java type.
     * @param dbToJava a function that converts from the JDBC value to Java value.
     * @param javaToDb a function that converts from Java value to JDBC value.
     */
    public SQLCustomType(SQLType<IT> type, Class<JT> javaT, Function<IT, JT> dbToJava, Function<JT, IT> javaToDb) {
        this(type.sqlDeclaration, type.getJavaType(), javaT, dbToJava, javaToDb);
    }

    /**
     * Creates a new custom type.
     * @param sqlD the name of the type in SQL (like {@code BLOB} or {@code BIGINT}).
     * @param intermediateJavaT the class of the JDBC value type.
     * @param javaT the class of the Java value type.
     * @param dbToJava a function that converts from the JDBC value to Java value.
     * @param javaToDb a function that converts from Java value to JDBC value.
     */
    public SQLCustomType(String sqlD, Class<IT> intermediateJavaT, Class<JT> javaT, Function<IT, JT> dbToJava, Function<JT, IT> javaToDb) {
        super(sqlD, javaT);
        intermediateJavaType = intermediateJavaT;
        dbToJavaConv = dbToJava;
        javaToDbConv = javaToDb;
    }
}
