package fr.pandacube.java.util.db2.sql_tools;

import java.sql.Date;

public class SQLType<T> {
	
	private final String sqlType;
	private final String sqlTypeParam;
	private final Class<T> javaTypes;
	
	public SQLType(String sqlT, String sqlP, Class<T> javaT) {
		sqlType = sqlT;
		sqlTypeParam = sqlP;
		javaTypes = javaT;
	}
	
	@Override
	public String toString() {
		return sqlType + sqlTypeParam;
	}
	
	public boolean isAssignableFrom(Object val) {
		if (javaTypes.isInstance(val))
			return true;
		return false;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SQLType))
			return false;
		return toString().equals(((SQLType<?>)obj).toString());
	}
	
	public Class<T> getJavaType() {
		return javaTypes;
	}
	
	
	
	
	
	
	public static final SQLType<Boolean> BOOLEAN = new SQLType<>("BOOLEAN", "", Boolean.class);
	
	public static final SQLType<Byte> TINYINT = new SQLType<>("TINYINT", "", Byte.class);
	public static final SQLType<Byte> BYTE = TINYINT;
	
	public static final SQLType<Short> SMALLINT = new SQLType<>("SMALLINT", "", Short.class);
	public static final SQLType<Short> SHORT = SMALLINT;
	
	public static final SQLType<Integer> INT = new SQLType<>("INT", "", Integer.class);
	public static final SQLType<Integer> INTEGER = INT;
	
	public static final SQLType<Long> BIGINT = new SQLType<>("BIGINT", "", Long.class);
	public static final SQLType<Long> LONG = BIGINT;
	
	public static final SQLType<Date> DATE = new SQLType<>("DATE", "", Date.class);
	
	public static final SQLType<Float> FLOAT = new SQLType<>("FLOAT", "", Float.class);
	
	public static final SQLType<Double> DOUBLE = new SQLType<>("DOUBLE", "", Double.class);
	
	public static final SQLType<String> CHAR(int charCount) {
		if (charCount <= 0) throw new IllegalArgumentException("charCount must be positive.");
		return new SQLType<>("CHAR", "("+charCount+")", String.class);
	}
	
	public static final SQLType<String> VARCHAR(int charCount) {
		if (charCount <= 0) throw new IllegalArgumentException("charCount must be positive.");
		return new SQLType<>("VARCHAR", "("+charCount+")", String.class);
	}
	
	public static final SQLType<String> TEXT = new SQLType<>("TEXT", "", String.class);
	public static final SQLType<String> STRING = TEXT;
	
	public static final <T extends Enum<T>> SQLType<T> ENUM(Class<T> enumType) {
		if (enumType == null) throw new IllegalArgumentException("enumType can't be null.");
		String enumStr = "'";
		boolean first = true;
		for (T el : enumType.getEnumConstants()) {
			if (!first)
				enumStr += "', '";
			first = false;
			enumStr += el.name();
			
		}
		enumStr += "'";
		
		return new SQLType<>("VARCHAR", "("+enumStr+")", enumType);
	}
	
	
}
