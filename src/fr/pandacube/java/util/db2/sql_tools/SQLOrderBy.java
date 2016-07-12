package fr.pandacube.java.util.db2.sql_tools;

import java.util.ArrayList;
import java.util.List;

public class SQLOrderBy {
	
	private List<OBField> orderByFields = new ArrayList<>();
	
	/**
	 * Construit une nouvelle clause ORDER BY
	 */
	public SQLOrderBy() {}
	
	/**
	 * Ajoute un champ dans la clause ORDER BY en construction
	 * @param field le champ SQL à ordonner
	 * @param d le sens de tri (croissant ASC ou décroissant DESC)
	 * @return l'objet courant (permet de chainer les ajouts de champs)
	 */
	public SQLOrderBy addField(SQLField<?> field, Direction d) {
		orderByFields.add(new OBField(field, d));
		return this;
	}
	
	/**
	 * Ajoute un champ dans la clause ORDER BY en construction,
	 * avec comme ordre de tri croissant ASC par défaut
	 * @param field le champ SQL à ordonner dans l'ordre croissant ASC
	 * @return l'objet courant (permet de chainer les ajouts de champs)
	 */
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
	
	public enum Direction {
		ASC, DESC;
	}
}
