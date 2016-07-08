package fr.pandacube.java.util.db2.sql_tools;

import java.util.ArrayList;
import java.util.List;

public class SQLOrderBy {
	
	private List<OBField> orderByFields = new ArrayList<>();
	
	public SQLOrderBy() {}
	
	public SQLOrderBy addField(SQLField<?> field,Direction d) {
		orderByFields.add(new OBField(field, d));
		return this;
	}
	
	public SQLOrderBy addField(SQLField<?> field) {
		return addField(field, Direction.ASC);
	}
	
	
	/* package */ String toSQL() {
		String ret = "";
		boolean first = true;
		for (OBField f : orderByFields) {
			if (!first) ret += ", ";
			first = false;
			ret += f.field.name + " " + f.direction.name();
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return toSQL();
	}
	
	
	private class OBField {
		public final SQLField<?> field;
		public final Direction direction;
		
		public OBField(SQLField<?> f, Direction d) {
			field = f;
			direction = d;
		}
		
		
	}
	
	private enum Direction {
		ASC, DESC;
	}
}
