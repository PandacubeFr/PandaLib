package fr.pandacube.java.util.orm;

import java.util.function.Function;

/**
 * 
 * @author Marc
 *
 * @param <IT> intermediate type, the type of the value transmitted to the JDBC
 * @param <JT> Java type
 */
public class SQLCustomType<IT, JT> extends SQLType<JT> {
	
	public final Class<IT> intermediateJavaType;
	public final Function<IT, JT> dbToJavaConv;
	public final Function<JT, IT> javaToDbConv;

	protected SQLCustomType(SQLType<IT> type, Class<JT> javaT, Function<IT, JT> dbToJava, Function<JT, IT> javaToDb) {
		this(type.sqlDeclaration, type.getJavaType(), javaT, dbToJava, javaToDb);
	}
	
	protected SQLCustomType(String sqlD, Class<IT> intermediateJavaT, Class<JT> javaT, Function<IT, JT> dbToJava, Function<JT, IT> javaToDb) {
		super(sqlD, javaT);
		intermediateJavaType = intermediateJavaT;
		dbToJavaConv = dbToJava;
		javaToDbConv = javaToDb;
	}
	
	
	// tester en local
	
}
