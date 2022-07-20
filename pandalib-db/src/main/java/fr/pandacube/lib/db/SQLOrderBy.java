package fr.pandacube.lib.db;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SQLOrderBy<E extends SQLElement<E>> {

	private final List<OBField> orderByFields = new ArrayList<>();

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
	private SQLOrderBy<E> add(SQLField<E, ?> field, Direction d) {
		orderByFields.add(new OBField(field, d));
		return this;
	}

	/**
	 * Ajoute un champ dans la clause ORDER BY en construction avec pour direction ASC
	 *
	 * @param field le champ SQL à ordonner
	 * @return l'objet courant (permet de chainer les ajouts de champs)
	 */
	public SQLOrderBy<E> thenAsc(SQLField<E, ?> field) {
		return add(field, Direction.ASC);
	}

	/**
	 * Ajoute un champ dans la clause ORDER BY en construction avec pour direction DESC
	 *
	 * @param field le champ SQL à ordonner
	 * @return l'objet courant (permet de chainer les ajouts de champs)
	 */
	public SQLOrderBy<E> thenDesc(SQLField<E, ?> field) {
		return add(field, Direction.DESC);
	}

	/* package */ String toSQL() {
		return orderByFields.stream()
				.map(f -> "`" + f.field.getName() + "` " + f.direction.name())
				.collect(Collectors.joining(", "));
	}

	@Override
	public String toString() {
		return toSQL();
	}

	private class OBField {
		public final SQLField<E, ?> field;
		public final Direction direction;

		public OBField(SQLField<E, ?> f, Direction d) {
			field = f;
			direction = d;
		}

	}

	private enum Direction {
		ASC, DESC
	}
	
	
	
	
	
	
	

	
	public static <E extends SQLElement<E>> SQLOrderBy<E> asc(SQLField<E, ?> field) {
		return new SQLOrderBy<E>().thenAsc(field);
	}
	
	public static <E extends SQLElement<E>> SQLOrderBy<E> desc(SQLField<E, ?> field) {
		return new SQLOrderBy<E>().thenDesc(field);
	}
	
	
	
	
}
