package fr.pandacube.util.orm;

import java.util.ArrayList;
import java.util.List;

public class SQLOrderBy {

	private List<OBField> orderByFields = new ArrayList<>();

	/**
	 * Construit une nouvelle clause ORDER BY
	 */
	private SQLOrderBy() {}

	/**
	 * Ajoute un champ dans la clause ORDER BY en construction
	 *
	 * @param field le champ SQL à ordonner
	 * @param d le sens de tri (croissant ASC ou décroissant DESC)
	 * @return l'objet courant (permet de chainer les ajouts de champs)
	 */
	private SQLOrderBy add(SQLField<?, ?> field, Direction d) {
		orderByFields.add(new OBField(field, d));
		return this;
	}

	/**
	 * Ajoute un champ dans la clause ORDER BY en construction avec pour direction ASC
	 *
	 * @param field le champ SQL à ordonner
	 * @return l'objet courant (permet de chainer les ajouts de champs)
	 */
	public SQLOrderBy thenAsc(SQLField<?, ?> field) {
		return add(field, Direction.ASC);
	}

	/**
	 * Ajoute un champ dans la clause ORDER BY en construction avec pour direction DESC
	 *
	 * @param field le champ SQL à ordonner
	 * @return l'objet courant (permet de chainer les ajouts de champs)
	 */
	public SQLOrderBy thenDesc(SQLField<?, ?> field) {
		return add(field, Direction.DESC);
	}

	/* package */ String toSQL() {
		String ret = "";
		boolean first = true;
		for (OBField f : orderByFields) {
			if (!first) ret += ", ";
			first = false;
			ret += "`" + f.field.getName() + "` " + f.direction.name();
		}
		return ret;
	}

	@Override
	public String toString() {
		return toSQL();
	}

	private class OBField {
		public final SQLField<?, ?> field;
		public final Direction direction;

		public OBField(SQLField<?, ?> f, Direction d) {
			field = f;
			direction = d;
		}

	}

	private enum Direction {
		ASC, DESC;
	}
	
	
	
	
	
	
	

	
	public static SQLOrderBy asc(SQLField<?, ?> field) {
		return new SQLOrderBy().thenAsc(field);
	}
	
	public static SQLOrderBy desc(SQLField<?, ?> field) {
		return new SQLOrderBy().thenDesc(field);
	}
	
	
	
	
}
