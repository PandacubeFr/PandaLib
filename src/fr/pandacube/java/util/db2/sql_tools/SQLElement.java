package fr.pandacube.java.util.db2.sql_tools;

import java.sql.Connection;
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
import java.util.logging.Level;

import org.apache.commons.lang.builder.ToStringBuilder;

import fr.pandacube.java.util.Log;
import fr.pandacube.java.util.db2.sql_tools.SQLWhereComp.SQLComparator;

public abstract class SQLElement {
	/** cache for fields for each subclass of SQLElement */
	/* package */ static final Map<Class<? extends SQLElement>, SQLFieldMap> fieldsCache = new HashMap<>();
	
	
	
	
	DBConnection db = ORM.getConnection();
	
	private boolean stored = false;
	private int id;
	
	private final String tableName;
	private final SQLFieldMap fields;

	private final Map<SQLField<?>, Object> values;
	/* package */ final Set<String> modifiedSinceLastSave;
	

	public SQLElement() {
		tableName = tableName();
		
		
		if (fieldsCache.get(getClass()) == null) {
			fields = new SQLFieldMap(getClass());
			
			// le champ id commun à toutes les tables
			fields.addField(new SQLField<>("id", SQLType.INT, false, true, 0));
			
			generateFields(fields);
			fieldsCache.put(getClass(), fields);
		}
		else {
			fields = fieldsCache.get(getClass());
		}
		
		values = new LinkedHashMap<>(fields.size());
		modifiedSinceLastSave = new HashSet<>(fields.size());
		
		initDefaultValues();
		
		
	}
	
	
	protected SQLElement(int id) { 
		this();
		@SuppressWarnings("unchecked")
		SQLField<Integer> idField = (SQLField<Integer>)fields.get("id");
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
		// remplissage des données par défaut (si peut être null ou si valeur par défaut existe)
		for (@SuppressWarnings("rawtypes") SQLField f : fields.values()) {
			if (f.defaultValue != null) {
				set(f, f.defaultValue);
			} else if (f.canBeNull || (f.autoIncrement && !stored)) {
				set(f, null);
			}
		}
	}
	
	protected void generateFields(SQLFieldMap listToFill) {
		
		java.lang.reflect.Field[] declaredFields = getClass().getDeclaredFields();
		for (java.lang.reflect.Field field : declaredFields) {
			if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()))
				continue;
			
			try {
				Object val = field.get(null);
				if (val == null || !(val instanceof SQLField))
					continue;
				
				listToFill.addField((SQLField<?>)val);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				Log.getLogger().log(Level.SEVERE, "Can't get value of static field "+field.toString(), e);
			}
		}
		
		
		
		
	}
	
	/* package */ Map<String, SQLField<?>> getFields() {
		return Collections.unmodifiableMap(fields);
	}
	
	
	
	
	public Map<SQLField<?>, Object> getValues() {
		return Collections.unmodifiableMap(values);
	}
	
	
	
	
	
	
	
	
	public <T> void set(SQLField<T> field, T value) {
		set(field, value, true);
	}
	
	
	/* package */ <T> void set(SQLField<T> sqlField, T value, boolean setModified) {
		if (sqlField == null)
			throw new IllegalArgumentException("sqlField can't be null");
		if (!fields.containsValue(sqlField))
			throw new IllegalArgumentException(sqlField.name + " is not a SQLField of " + getClass().getName());
		
		boolean modify = false;
		if (value == null) {
			if (sqlField.canBeNull || (sqlField.autoIncrement && !stored))
				modify = true;
			else
				throw new IllegalArgumentException("SQLField '" + sqlField.name + "' of " + getClass().getName() + " is a NOT NULL field");
		} else {
			if (sqlField.type.isAssignableFrom(value))
				modify = true;
			else
				throw new IllegalArgumentException("SQLField '" + sqlField.name + "' of " + getClass().getName() + " type is '" + sqlField.type.toString() + "' and can't accept values of type " + value.getClass().getName());
		}
		
		if (modify) {
			if (!values.containsKey(sqlField)) {
				values.put(sqlField, value);
				if (setModified)
					modifiedSinceLastSave.add(sqlField.name);
			}
			else {
				Object oldVal = values.get(sqlField);
				if (!Objects.equals(oldVal, value)) {
					values.put(sqlField, value);
					if (setModified)
						modifiedSinceLastSave.add(sqlField.name);
				}
				// sinon, rien n'est modifié
			}
			
		}
		
	}
	
	
	public <T> T get(SQLField<T> field) {
		if (field == null)
			throw new IllegalArgumentException("field can't be null");
		if (values.containsKey(field)) {
			@SuppressWarnings("unchecked")
			T val = (T) values.get(field);
			return val;
		}
		throw new IllegalArgumentException("The field '" + field.name + "' in this instance of " + getClass().getName() + " does not exist or is not set");
	}
	
	
	public <T, E extends SQLElement> E getForeign(SQLFKField<T, E> field) throws ORMException {
		T fkValue = get(field);
		if (fkValue == null) return null;
		return ORM.getFirst(field.getForeignElementClass(),
				new SQLWhereComp(field.getForeignField(), SQLComparator.EQ, fkValue), null);
	}
	
	
	
	public boolean isValidForSave() {
		return values.keySet().containsAll(fields.values());
	}
	
	
	
	private Map<SQLField<?>, Object> getOnlyModifiedValues() {
		Map<SQLField<?>, Object> modifiedValues = new LinkedHashMap<>();
		values.forEach((k, v) -> {
			if (modifiedSinceLastSave.contains(k.name))
				modifiedValues.put(k, v);
		});
		return modifiedValues;
	}
	
	
	
	public boolean isModified(SQLField<?> field) {
		return modifiedSinceLastSave.contains(field.name);
	}
	
	
	
	public void save() throws ORMException {
		if (!isValidForSave())
			throw new IllegalStateException(toString() + " has at least one undefined value and can't be saved.");
		
		ORM.initTable(getClass());
		String toStringStatement = "";
		try {
			
			Connection conn = db.getNativeConnection();
	
			
			if (stored)
			{	// mettre à jour les valeurs dans la base
				
				// restaurer l'ID au cas il aurait été changé à la main dans values
				@SuppressWarnings("unchecked")
				SQLField<Integer> idField = (SQLField<Integer>) fields.get("id");
				values.put(idField, id);
				modifiedSinceLastSave.remove("id");
				Map<SQLField<?>, Object> modifiedValues = getOnlyModifiedValues();
				
				if (modifiedValues.isEmpty())
					return;
				
				String sql = "";
				List<Object> psValues = new ArrayList<>();
				
				for(Map.Entry<SQLField<?>, Object> entry : modifiedValues.entrySet()) {
					sql += entry.getKey().name + " = ? ,";
					if (entry.getKey().type.getJavaType().isEnum()) {
						// prise en charge enum (non prise en charge par JDBC)
						psValues.add(((Enum<?>)entry.getValue()).name());
					}
					else
						psValues.add(entry.getValue());
				}
				
				if (sql.length() > 0)
					sql = sql.substring(0, sql.length()-1);
				
				PreparedStatement ps = conn.prepareStatement("UPDATE "+tableName+" SET "+sql+" WHERE id="+id);
				
				try {
	
					int i = 1;
					for (Object val : psValues) {
						ps.setObject(i++, val);
					}

					toStringStatement = ps.toString();
					ps.executeUpdate();
				} finally {
					ps.close();
				}
			}
			else
			{	// ajouter dans la base
	
				// restaurer l'ID au cas il aurait été changé à la main dans values
				values.put(fields.get("id"), null);
				
				
				String concat_vals = "";
				String concat_fields = "";
				List<Object> psValues = new ArrayList<>();
				
				boolean first = true;
				for(Map.Entry<SQLField<?>, Object> entry : values.entrySet()) {
					if (!first) {
						concat_vals += ",";
						concat_fields += ",";
					}
					first = false;
					concat_vals += " ? ";
					concat_fields += entry.getKey().name;
					if (entry.getKey().type.getJavaType().isEnum()) {
						// prise en charge enum (non prise en charge par JDBC)
						psValues.add(((Enum<?>)entry.getValue()).name());
					}
					else
						psValues.add(entry.getValue());
				}
				
				
				PreparedStatement ps = conn.prepareStatement("INSERT INTO "+tableName+"  ("+concat_fields+") VALUES ("+concat_vals+")", Statement.RETURN_GENERATED_KEYS);
				try {
	
					int i = 1;
					for (Object val : psValues) {
						ps.setObject(i++, val);
					}

					toStringStatement = ps.toString();
					ps.executeUpdate();
					
					ResultSet rs = ps.getGeneratedKeys();
					try {
		                if(rs.next())
		                {
		                    id = rs.getInt(1);
		                }
		                
						stored = true;
					} finally {
						rs.close();
					}
				} finally {
					ps.close();
				}
				
			}
			
			modifiedSinceLastSave.clear();
		} catch(SQLException e) {
			throw new ORMException("Error while executing SQL statement "+toStringStatement, e);
		}
		Log.debug(toStringStatement);
	}
	
	
	public boolean isStored() { return stored; }
	
	public Integer getId() {
		return (stored) ? id : null;
	}
	
	@SuppressWarnings("unchecked")
	public SQLField<Integer> getFieldId() {
		return (SQLField<Integer>) getFields().get("id");
	}
	
	
	
	
	
	public void delete() throws ORMException {
		
		try {
			if (stored)
			{	//  supprimer la ligne de la base
				PreparedStatement st = db.getNativeConnection().prepareStatement("DELETE FROM "+tableName+" WHERE id="+id);
				try {
					Log.debug(st.toString());
					st.executeUpdate();
					markAsNotStored();
				} finally {
					st.close();
				}
			}
		} catch (SQLException e) {
			throw new ORMException(e);
		}
		
	}
	
	/**
	 * Méthode appelée quand l'élément courant est retirée de la base de données via une requête externe
	 */
	/* package */ void markAsNotStored() {
		stored = false;
		id = 0;
		modifiedSinceLastSave.clear();
		values.forEach((k, v) -> modifiedSinceLastSave.add(k.name));
	}
	
	
	
	
	protected static class SQLFieldMap extends LinkedHashMap<String, SQLField<?>> {
		private static final long serialVersionUID = 1L;
		
		private final Class<? extends SQLElement> sqlElemClass;
		
		private SQLFieldMap(Class<? extends SQLElement> elemClass) {
			sqlElemClass = elemClass;
		}
		
		private void addField(SQLField<?> f) {
			if (f == null) return;
			if (containsKey(f.name))
				throw new IllegalArgumentException("SQLField "+f.name+" already exist in "+sqlElemClass.getName());
			f.setSQLElementType(sqlElemClass);
			put(f.name, f);
		}
		
	}
	
	
	
	
	@Override
	public String toString() {
		ToStringBuilder b = new ToStringBuilder(this);
		
		for (SQLField<?> f : fields.values()) {
			try {
				b.append(f.name, get(f));
			} catch(IllegalArgumentException e) {
				b.append(f.name, "(Undefined)");
			}
			
		}
		
		return b.toString();
	}
	
	
	
	
	
}
