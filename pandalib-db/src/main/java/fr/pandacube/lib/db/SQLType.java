package fr.pandacube.lib.db;

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

	public boolean isInstance(Object val) {
		return javaTypes.isInstance(val);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SQLType o
				&& toString().equals(o.toString());
	}

	public Class<T> getJavaType() {
		return javaTypes;
	}

	
}
