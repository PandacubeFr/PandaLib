package fr.pandacube.util.orm;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

/* package */ class SQLWhereLike<E extends SQLElement<E>> extends SQLWhere<E> {

	private SQLField<E, ?> field;
	private String likeExpr;

	/**
	 * Compare a field with a value
	 *
	 * @param f the field at left of the LIKE keyword. Can't be null
	 * @param like the like expression.
	 */
	/* package */ SQLWhereLike(SQLField<E, ?> f, String like) {
		if (f == null || like == null)
			throw new IllegalArgumentException("All arguments for SQLWhereLike constructor can't be null");
		field = f;
		likeExpr = like;
	}

	@Override
	public Pair<String, List<Object>> toSQL() {
		ArrayList<Object> params = new ArrayList<>();
		params.add(likeExpr);
		return new Pair<>("`" + field.getName() + "` LIKE ? ", params);
	}

}
