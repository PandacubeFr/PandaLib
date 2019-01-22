package fr.pandacube.java.util.orm;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;

import fr.pandacube.java.util.Log;
import fr.pandacube.java.util.orm.SQLWhereChain.SQLBoolOp;
import fr.pandacube.java.util.orm.SQLWhereComp.SQLComparator;

/**
 *
 * @param <E>
 */
public class SQLElementList<E extends SQLElement<E>> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;

	private final Map<SQLField<E, ?>, Object> modifiedValues = new LinkedHashMap<>();

	@Override
	public synchronized boolean add(E e) {
		if (e == null || !e.isStored()) return false;
		return super.add(e);
	}

	/**
	 * Défini une valeur à un champ qui sera appliquée dans la base de données à
	 * tous les
	 * entrées présente dans cette liste lors de l'appel à {@link #saveCommon()}
	 * .
	 * Les valeurs stockés dans chaque élément de cette liste ne seront affectés
	 * que lors de
	 * l'appel à {@link #saveCommon()}
	 *
	 * @param <T>
	 * @param field le champs à modifier
	 * @param value la valeur à lui appliquer
	 */
	public synchronized <T> void setCommon(SQLField<E, T> field, T value) {
		if (field == null)
			throw new IllegalArgumentException("field can't be null");
		if (field.getName() == "id")
			throw new IllegalArgumentException("Can't modify id field in a SQLElementList");
		
		Class<E> elemClass = field.getSQLElementType();
		try {
			E emptyElement = elemClass.newInstance();
			emptyElement.set(field, value, false);
		} catch (Exception e) {
			throw new IllegalArgumentException("Illegal field or value or can't instanciante an empty instance of "
					+ elemClass.getName() + ". (the instance is only created to test validity of field and value)", e);
		}

		// ici, la valeur est bonne
		modifiedValues.put(field, value);

	}

	/**
	 * Applique toutes les valeurs défini avec
	 * {@link #setCommon(SQLField, Object)} à toutes
	 * les entrées dans la base de données correspondants aux entrées de cette
	 * liste. Les nouvelles
	 * valeurs sont aussi mises à jour dans les objets contenus dans cette
	 * liste, si la valeur n'a pas été modifiée individuellement avec
	 * {@link SQLElement#set(SQLField, Object)}.<br/>
	 * Les objets de cette liste qui n'ont pas leur données en base de données
	 * sont ignorées.
	 *
	 * @throws SQLException
	 */
	public synchronized void saveCommon() throws ORMException {
		List<E> storedEl = getStoredEl();
		if (storedEl.isEmpty()) return;

		String sqlSet = "";
		List<Object> psValues = new ArrayList<>();

		for (Map.Entry<SQLField<E, ?>, Object> entry : modifiedValues.entrySet()) {
			sqlSet += entry.getKey().getName() + " = ? ,";
			SQLElement.addValueToSQLObjectList(psValues, entry.getKey(), entry.getValue());
		}

		if (sqlSet.length() > 0) sqlSet = sqlSet.substring(0, sqlSet.length() - 1);

		String sqlWhere = "";
		boolean first = true;
		for (E el : storedEl) {
			if (!first) sqlWhere += " OR ";
			first = false;
			sqlWhere += "id = " + el.getId();
		}

		try(PreparedStatement ps = ORM.getConnection().getNativeConnection()
					.prepareStatement("UPDATE " + storedEl.get(0).tableName() + " SET " + sqlSet + " WHERE " + sqlWhere)) {
			
			int i = 1;
			for (Object val : psValues)
				ps.setObject(i++, val);

			Log.debug(ps.toString());
			ps.executeUpdate();

			applyNewValuesToElements(storedEl);
		} catch (SQLException e) {
			throw new ORMException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void applyNewValuesToElements(List<E> storedEl) {
		// applique les valeurs dans chaques objets de la liste
		for (E el : storedEl)
			for (@SuppressWarnings("rawtypes")
			SQLField entry : modifiedValues.keySet())
				if (!el.isModified(entry)) el.set(entry, modifiedValues.get(entry), false);
	}

	private List<E> getStoredEl() {
		return stream().filter(SQLElement::isStored).collect(Collectors.toCollection(() -> new ArrayList<>()));
	}

	public synchronized void removeFromDB() {
		List<E> storedEl = getStoredEl();
		if (storedEl.isEmpty()) return;

		try {

			String sqlWhere = "";
			boolean first = true;
			for (E el : storedEl) {
				if (!first) sqlWhere += " OR ";
				first = false;
				sqlWhere += "id = " + el.getId();
			}

			try (PreparedStatement st = ORM.getConnection().getNativeConnection()
					.prepareStatement("DELETE FROM " + storedEl.get(0).tableName() + " WHERE " + sqlWhere)) {
				Log.debug(st.toString());
				st.executeUpdate();

				for (E el : storedEl)
					el.markAsNotStored();

			}

		} catch (SQLException e) {
			Log.severe(e);
		}

	}
	
	
	
	public <T, P extends SQLElement<P>> SQLElementList<P> getReferencedEntries(SQLFKField<E, T, P> foreignKey, SQLOrderBy orderBy) throws ORMException {
		Set<T> values = new HashSet<>();
		forEach(v -> {
			T val = v.get(foreignKey);
			if (val != null)
				values.add(val);
		});
		
		if (values.isEmpty()) {
			return new SQLElementList<>();
		}
		
		SQLWhereChain where = new SQLWhereChain(SQLBoolOp.OR);
		values.forEach(v -> where.add(new SQLWhereComp(foreignKey.getPrimaryField(), SQLComparator.EQ, v)));
		
		
		return ORM.getAll(foreignKey.getForeignElementClass(), where, orderBy, null, null);
		
	}

	
	public <T, P extends SQLElement<P>> Map<T, P> getReferencedEntriesInGroups(SQLFKField<E, T, P> foreignKey) throws ORMException {
		SQLElementList<P> foreignElemts = getReferencedEntries(foreignKey, null);
		
		Map<T, P> ret = new HashMap<>();
		foreignElemts.forEach(foreignVal -> ret.put(foreignVal.get(foreignKey.getPrimaryField()), foreignVal));
		return ret;
	}
	

	
	public <T, F extends SQLElement<F>> SQLElementList<F> getReferencingForeignEntries(SQLFKField<F, T, E> foreignKey, SQLOrderBy orderBy, Integer limit, Integer offset) throws ORMException {
		Set<T> values = new HashSet<>();
		forEach(v -> {
			T val = v.get(foreignKey.getPrimaryField());
			if (val != null)
				values.add(val);
		});
		
		if (values.isEmpty()) {
			return new SQLElementList<>();
		}

		SQLWhereChain where = new SQLWhereChain(SQLBoolOp.OR);
		values.forEach(v -> where.add(new SQLWhereComp(foreignKey, SQLComparator.EQ, v)));
		
		return ORM.getAll(foreignKey.getSQLElementType(), where, orderBy, limit, offset);
		
	}

	
	public <T, F extends SQLElement<F>> Map<T, SQLElementList<F>> getReferencingForeignEntriesInGroups(SQLFKField<F, T, E> foreignKey, SQLOrderBy orderBy, Integer limit, Integer offset) throws ORMException {
		SQLElementList<F> foreignElements = getReferencingForeignEntries(foreignKey, orderBy, limit, offset);
		
		Map<T, SQLElementList<F>> map = new HashMap<>();
		foreignElements.forEach(foreignVal -> {
			SQLElementList<F> subList = map.getOrDefault(foreignVal.get(foreignKey), new SQLElementList<>());
			subList.add(foreignVal);
			map.put(foreignVal.get(foreignKey), subList);
		});
		
		return map;
	}
	
	
	
	
	
	public JsonArray asJsonArray() {
		JsonArray json = new JsonArray();
		forEach(el -> json.add(el.asJsonObject()));
		return json;
	}

}
