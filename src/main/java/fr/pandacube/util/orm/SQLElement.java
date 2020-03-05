package fr.pandacube.util.orm;

import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import fr.pandacube.util.Log;

public abstract class SQLElement<E extends SQLElement<E>> {
	/** cache for fields for each subclass of SQLElement */
	/* package */ static final Map<Class<? extends SQLElement<?>>, SQLFieldMap<? extends SQLElement<?>>> fieldsCache = new HashMap<>();

	DBConnection db = ORM.getConnection();

	private boolean stored = false;
	private int id;

	private final SQLFieldMap<E> fields;

	private final Map<SQLField<E, ?>, Object> values;
	/* package */ final Set<String> modifiedSinceLastSave;

	@SuppressWarnings("unchecked")
	public SQLElement() {
		
		try {
			ORM.initTable((Class<E>)getClass());
		} catch (ORMInitTableException e) {
			throw new RuntimeException(e);
		}

		if (fieldsCache.get(getClass()) == null) {
			fields = new SQLFieldMap<>((Class<E>)getClass());

			// le champ id commun à toutes les tables
			SQLField<E, Integer> idF = new SQLField<>(SQLType.INT, false, true, 0);
			idF.setName("id");
			fields.addField(idF);

			generateFields(fields);
			fieldsCache.put((Class<E>)getClass(), fields);
		}
		else
			fields = (SQLFieldMap<E>) fieldsCache.get(getClass());

		values = new LinkedHashMap<>(fields.size());
		modifiedSinceLastSave = new HashSet<>(fields.size());

		initDefaultValues();

	}

	protected SQLElement(int id) {
		this();
		@SuppressWarnings("unchecked")
		SQLField<E, Integer> idField = (SQLField<E, Integer>) fields.get("id");
		set(idField, id, false);
		this.id = id;
		stored = true;
	}

	/**
	 * @return The name of the table in the database.
	 */
	protected abstract String tableName();

	@SuppressWarnings("unchecked")
	private void initDefaultValues() {
		// remplissage des données par défaut (si peut être null ou si valeur
		// par défaut existe)
		for (@SuppressWarnings("rawtypes")
		SQLField f : fields.values())
			if (f.defaultValue != null) set(f, f.defaultValue);
			else if (f.canBeNull || (f.autoIncrement && !stored)) set(f, null);
	}

	@SuppressWarnings("unchecked")
	protected void generateFields(SQLFieldMap<E> listToFill) {

		java.lang.reflect.Field[] declaredFields = getClass().getDeclaredFields();
		for (java.lang.reflect.Field field : declaredFields) {
			if (!SQLField.class.isAssignableFrom(field.getType())) {
				Log.debug("[ORM] The field " + field.getDeclaringClass().getName() + "." + field.getName() + " is of type " + field.getType().getName() + " so it will be ignored.");
				continue;
			}
			if (!Modifier.isStatic(field.getModifiers())) {
				Log.severe("[ORM] The field " + field.getDeclaringClass().getName() + "." + field.getName() + " can't be initialized because it is not static.");
				continue;
			}
			field.setAccessible(true);
			try {
				Object val = field.get(null);
				if (val == null || !(val instanceof SQLField)) {
					Log.severe("[ORM] The field " + field.getDeclaringClass().getName() + "." + field.getName() + " can't be initialized because its value is null.");
					continue;
				}
				SQLField<E, ?> checkedF = (SQLField<E, ?>) val;
				checkedF.setName(field.getName());
				if (!Modifier.isPublic(field.getModifiers()))
					Log.warning("[ORM] The field " + field.getDeclaringClass().getName() + "." + field.getName() + " should be public !");
				if (listToFill.containsKey(checkedF.getName())) throw new IllegalArgumentException(
						"SQLField " + checkedF.getName() + " already exist in " + getClass().getName());
				checkedF.setSQLElementType((Class<E>) getClass());
				listToFill.addField((SQLField<?, ?>) val);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				Log.severe("Can't get value of static field " + field.toString(), e);
			}
		}

	}

	/* package */ Map<String, SQLField<E, ?>> getFields() {
		return Collections.unmodifiableMap(fields);
	}

	public Map<SQLField<E, ?>, Object> getValues() {
		return Collections.unmodifiableMap(values);
	}

	public <T> void set(SQLField<E, T> field, T value) {
		set(field, value, true);
	}

	/* package */ <T> void set(SQLField<E, T> sqlField, T value, boolean setModified) {
		if (sqlField == null) throw new IllegalArgumentException("sqlField can't be null");
		if (!fields.containsValue(sqlField)) // should not append at runtime because of generic type check at compilation
			throw new IllegalStateException("In the table "+getClass().getName()+ ": the field asked for modification is not initialized properly.");

		boolean modify = false;
		if (value == null) {
			if (sqlField.canBeNull || (sqlField.autoIncrement && !stored)) modify = true;
			else
				throw new IllegalArgumentException(
						"SQLField '" + sqlField.getName() + "' of " + getClass().getName() + " is a NOT NULL field");
		}
		else if (sqlField.type.isAssignableFrom(value)) modify = true;
		else
			throw new IllegalArgumentException("SQLField '" + sqlField.getName() + "' of " + getClass().getName()
					+ " type is '" + sqlField.type.toString() + "' and can't accept values of type "
					+ value.getClass().getName());

		if (modify) if (!values.containsKey(sqlField)) {
			values.put(sqlField, value);
			if (setModified) modifiedSinceLastSave.add(sqlField.getName());
		}
		else {
			Object oldVal = values.get(sqlField);
			if (!Objects.equals(oldVal, value)) {
				values.put(sqlField, value);
				if (setModified) modifiedSinceLastSave.add(sqlField.getName());
			}
			// sinon, rien n'est modifié
		}

	}

	public <T> T get(SQLField<E, T> field) {
		if (field == null) throw new IllegalArgumentException("field can't be null");
		if (values.containsKey(field)) {
			@SuppressWarnings("unchecked")
			T val = (T) values.get(field);
			return val;
		}
		throw new IllegalArgumentException("The field '" + field.getName() + "' in this instance of " + getClass().getName()
				+ " does not exist or is not set");
	}

	/**
	 * @param <T> the type of the specified field
	 * @param <P> the table class of the primary key targeted by the specified foreign key field
	 * @return the element in the table P that his primary key correspond to the foreign key value of this element.
	 */
	public <T, P extends SQLElement<P>> P getReferencedEntry(SQLFKField<E, T, P> field) throws ORMException {
		T fkValue = get(field);
		if (fkValue == null) return null;
		return ORM.getFirst(field.getForeignElementClass(), field.getPrimaryField().eq(fkValue), null);
	}

	/**
	 * @param <T> the type of the specified field
	 * @param <F> the table class of the foreign key that reference a primary key of this element.
	 * @return all elements in the table F for which the specified foreign key value correspond to the primary key of this element.
	 */
	public <T, F extends SQLElement<F>> SQLElementList<F> getReferencingForeignEntries(SQLFKField<F, T, E> field, SQLOrderBy<F> orderBy, Integer limit, Integer offset) throws ORMException {
		T value = get(field.getPrimaryField());
		if (value == null) return new SQLElementList<>();
		return ORM.getAll(field.getSQLElementType(), field.eq(value), orderBy, limit, offset);
	}

	public boolean isValidForSave() {
		return values.keySet().containsAll(fields.values());
	}

	private Map<SQLField<E, ?>, Object> getOnlyModifiedValues() {
		Map<SQLField<E, ?>, Object> modifiedValues = new LinkedHashMap<>();
		values.forEach((k, v) -> {
			if (modifiedSinceLastSave.contains(k.getName())) modifiedValues.put(k, v);
		});
		return modifiedValues;
	}

	public boolean isModified(SQLField<E, ?> field) {
		return modifiedSinceLastSave.contains(field.getName());
	}

	@SuppressWarnings("unchecked")
	public void save() throws ORMException {
		if (!isValidForSave())
			throw new IllegalStateException(toString() + " has at least one undefined value and can't be saved.");

		ORM.initTable((Class<E>)getClass());
		try {

			if (stored) { // mettre à jour les valeurs dans la base

				// restaurer l'ID au cas il aurait été changé à la main dans
				// values
				SQLField<E, Integer> idField = (SQLField<E, Integer>) fields.get("id");
				values.put(idField, id);
				modifiedSinceLastSave.remove("id");
				Map<SQLField<E, ?>, Object> modifiedValues = getOnlyModifiedValues();

				if (modifiedValues.isEmpty()) return;
				
				ORM.update((Class<E>)getClass(), getFieldId().eq(getId()), modifiedValues);
			}
			else { // ajouter dans la base

				// restaurer l'ID au cas il aurait été changé à la main dans
				// values
				values.put(fields.get("id"), null);

				String concat_vals = "";
				String concat_fields = "";
				List<Object> psValues = new ArrayList<>();

				boolean first = true;
				for (Map.Entry<SQLField<E, ?>, Object> entry : values.entrySet()) {
					if (!first) {
						concat_vals += ",";
						concat_fields += ",";
					}
					first = false;
					concat_vals += " ? ";
					concat_fields += "`" + entry.getKey().getName() + "`";
					addValueToSQLObjectList(psValues, entry.getKey(), entry.getValue());
				}
				
				try (PreparedStatement ps = db.getNativeConnection().prepareStatement(
						"INSERT INTO " + tableName() + "  (" + concat_fields + ") VALUES (" + concat_vals + ")",
						Statement.RETURN_GENERATED_KEYS)) {

					int i = 1;
					for (Object val : psValues)
						ps.setObject(i++, val);

					ps.executeUpdate();

					try (ResultSet rs = ps.getGeneratedKeys()) {
						if (rs.next()) id = rs.getInt(1);
						stored = true;
					}
				}

			}

			modifiedSinceLastSave.clear();
		} catch (SQLException e) {
			throw new ORMException("Error while saving data", e);
		}
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static <E extends SQLElement<E>> void addValueToSQLObjectList(List<Object> list, SQLField<E, ?> field, Object jValue) throws ORMException {
		if (jValue != null && field.type instanceof SQLCustomType) {
			try {
				jValue = ((SQLCustomType)field.type).javaToDbConv.apply(jValue);
			} catch (Exception e) {
				throw new ORMException("Error while converting value of field '"+field.getName()+"' with SQLCustomType from "+field.type.getJavaType()
						+"(java source) to "+((SQLCustomType<?, ?>)field.type).intermediateJavaType+"(jdbc destination). The original value is '"+jValue.toString()+"'", e);
			}
		}
		list.add(jValue);
	}

	public boolean isStored() {
		return stored;
	}

	public Integer getId() {
		return (stored) ? id : null;
	}

	@SuppressWarnings("unchecked")
	public SQLField<E, Integer> getFieldId() {
		return (SQLField<E, Integer>) fields.get("id");
	}

	public void delete() throws ORMException {

		if (stored) { // supprimer la ligne de la base
			try (PreparedStatement st = db.getNativeConnection()
					.prepareStatement("DELETE FROM " + tableName() + " WHERE id=" + id)) {
				Log.debug(st.toString());
				st.executeUpdate();
				markAsNotStored();
			} catch (SQLException e) {
				throw new ORMException(e);
			}
		}

	}

	/**
	 * Méthode appelée quand l'élément courant est retirée de la base de données
	 * via une requête externe
	 */
	/* package */ void markAsNotStored() {
		stored = false;
		id = 0;
		modifiedSinceLastSave.clear();
		values.forEach((k, v) -> modifiedSinceLastSave.add(k.getName()));
	}

	protected static class SQLFieldMap<E extends SQLElement<E>> extends LinkedHashMap<String, SQLField<E, ?>> {
		private static final long serialVersionUID = 1L;

		private final Class<E> sqlElemClass;

		private SQLFieldMap(Class<E> elemClass) {
			sqlElemClass = elemClass;
		}

		private void addField(SQLField<?, ?> f) {
			if (f == null) return;
			if (containsKey(f.getName())) throw new IllegalArgumentException(
					"SQLField " + f.getName() + " already exist in " + sqlElemClass.getName());
			@SuppressWarnings("unchecked")
			SQLField<E, ?> checkedF = (SQLField<E, ?>) f;
			checkedF.setSQLElementType(sqlElemClass);
			put(checkedF.getName(), checkedF);
		}

	}

	@Override
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this);

		for (SQLField<E, ?> f : fields.values())
			try {
				b.append(f.getName(), get(f));
			} catch (IllegalArgumentException e) {
				b.append(f.getName(), "(Undefined)");
			}

		return b.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(getClass().isInstance(o))) return false;
		SQLElement<?> oEl = (SQLElement<?>) o;
		if (oEl.getId() == null) return false;
		return oEl.getId().equals(getId());
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	
	
	public JsonObject asJsonObject() {
		JsonObject json = new JsonObject();
		for (SQLField<E, ?> f : getFields().values()) {
			json.add(f.getName(), new Gson().toJsonTree(get(f)));
		}
		return json;
	}

}
