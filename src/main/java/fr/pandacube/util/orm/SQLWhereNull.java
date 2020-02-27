package fr.pandacube.util.orm;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.javatuples.Pair;

import fr.pandacube.util.Log;

/* package */ class SQLWhereNull extends SQLWhere {

	private SQLField<?, ?> fild;
	private boolean nulll;

	/**
	 * Init a IS NULL / IS NOT NULL expression for a SQL WHERE condition.
	 *
	 * @param field the field to check null / not null state
	 * @param isNull true if we want to ckeck if "IS NULL", or false to check if
	 *        "IS NOT NULL"
	 */
	/* package */ SQLWhereNull(SQLField<?, ?> field, boolean isNull) {
		if (field == null) throw new IllegalArgumentException("field can't be null");
		if (!field.canBeNull) Log.getLogger().log(Level.WARNING,
				"Useless : Trying to check IS [NOT] NULL on the field " + field.getSQLElementType().getName() + "#"
						+ field.getName() + " which is declared in the ORM as 'can't be null'");
		fild = field;
		nulll = isNull;
	}

	@Override
	public Pair<String, List<Object>> toSQL() {
		return new Pair<>("`" + fild.getName() + "` IS " + ((nulll) ? "NULL" : "NOT NULL"), new ArrayList<>());
	}

}
