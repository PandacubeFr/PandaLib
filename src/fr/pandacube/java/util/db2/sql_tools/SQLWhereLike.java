package fr.pandacube.java.util.db2.sql_tools;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

public class SQLWhereLike extends SQLWhere {
	

	private SQLField<String> field;
	private String likeExpr;
	
	/**
	 * Compare a field with a value
	 * @param l the field at left of the comparison operator. Can't be null
	 * @param c the comparison operator, can't be null
	 * @param r the value at right of the comparison operator. Can't be null
	 */
	public SQLWhereLike(SQLField<String> f, String like) {
		if (f == null || like == null)
			throw new IllegalArgumentException("All arguments for SQLWhereLike constructor can't be null");
		field = f;
		likeExpr = like;
	}
	
	
	@Override
	public Pair<String, List<Object>> toSQL() {
		ArrayList<Object> params = new ArrayList<>();
		params.add(likeExpr);
		return new Pair<>(field.name + " LIKE ? ", params);
	}

}
