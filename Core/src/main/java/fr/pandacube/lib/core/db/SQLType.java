package fr.pandacube.lib.core.db;

public class SQLType<T> {

	protected final String sqlDeclaration;
	private final Class<T> javaTypes;

	/* package */ SQLType(String sqlD, Class<T> javaT) {
		sqlDeclaration = sqlD;
		javaTypes = javaT;
	}

	@Override
	public String toString() {
		return sqlDeclaration;
	}

	public boolean isAssignableFrom(Object val) {
		if (javaTypes.isInstance(val)) return true;
		return false;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof SQLType)) return false;
		return toString().equals(((SQLType<?>) obj).toString());
	}

	public Class<T> getJavaType() {
		return javaTypes;
	}

	
}
