package fr.pandacube.lib.db;

import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import fr.pandacube.lib.util.EnumUtil;
import fr.pandacube.lib.util.Log;

/**
 * Represents an entry in a SQL table. Each subclass is for a specific table.
 * @param <E> the type of the subclass.
 */// TODO exemple subclass
public abstract class SQLElement<E extends SQLElement<E>> {

    // cache for fields for each subclass of SQLElement
    /* package */ static final Map<Class<? extends SQLElement<?>>, SQLFieldMap<? extends SQLElement<?>>> fieldsCache = new HashMap<>();

    /* package */ static class SQLFieldMap<E extends SQLElement<E>> extends LinkedHashMap<String, SQLField<E, ?>> {
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

    private final DBConnection db = DB.getConnection();

    private boolean stored = false;
    private int id;

    private final SQLFieldMap<E> fields;

    private final Map<SQLField<E, ?>, Object> values;
    /* package */ final Set<String> modifiedSinceLastSave;

    /**
     * Create a new instance of a table entry, not yet saved in the database.
     * All the required values has to be set before saving the entry in the database.
     */
    @SuppressWarnings("unchecked")
    protected SQLElement() {

        try {
            DB.initTable((Class<E>)getClass());
        } catch (DBInitTableException e) {
            throw new RuntimeException(e);
        }

        if (fieldsCache.get(getClass()) == null) {
            fields = new SQLFieldMap<>(getCheckedClass());

            // le champ id commun à toutes les tables
            SQLField<E, Integer> idF = new SQLField<>(INT, false, true, 0);
            idF.setName("id");
            fields.addField(idF);

            generateFields(fields);
            fieldsCache.put(getCheckedClass(), fields);
        }
        else
            fields = (SQLFieldMap<E>) fieldsCache.get(getClass());

        values = new LinkedHashMap<>(fields.size());
        modifiedSinceLastSave = new HashSet<>(fields.size());

        initDefaultValues();

    }

    /**
     * Create a new instance of a table entry, representing an already present one in the database.
     * <p>
     * Subclasses must implement a constructor with the same signature, that calls this parent constructor, and may be
     * private to avoid accidental instanciation. This constructor will be called by the DB API when fetching entries
     * from the database.
     * @param id the id of the entry in the database.
     */
    protected SQLElement(int id) {
        this();
        @SuppressWarnings("unchecked")
        SQLField<E, Integer> idField = (SQLField<E, Integer>) fields.get("id");
        set(idField, id, false);
        this.id = id;
        stored = true;
    }

    /**
     * Gets the name of the table in the database, without the prefix defined by {@link DB#init(DBConnection, String)}.
     * @return The unprefixed name of the table in the database.
     */
    protected abstract String tableName();

    /**
     * Gets a checked version of the {@link Class} instance of this {@link SQLElement} object.
     * @return {@code (Class<E>) getClass()};
     */
    @SuppressWarnings("unchecked")
    public Class<E> getCheckedClass() {
        return (Class<E>) getClass();
    }

    /**
     * Fills the values of this entry that are known to be nullable or have a default value.
     */
    @SuppressWarnings("unchecked")
    private void initDefaultValues() {
        for (@SuppressWarnings("rawtypes")
        SQLField f : fields.values())
            if (f.defaultValue != null) set(f, f.defaultValue);
            else if (f.nullable || (f.autoIncrement && !stored)) set(f, null);
    }

    @SuppressWarnings("unchecked")
    private void generateFields(SQLFieldMap<E> listToFill) {

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
                if (!(val instanceof SQLField)) {
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
                Log.severe("Can't get value of static field " + field, e);
            }
        }

    }

    /* package */ Map<String, SQLField<E, ?>> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    /**
     * Gets the fiels of this entry’s table, mapped to the values of this entry.
     * @return the fiels of this entry’s table, mapped to the values of this entry.
     */
    public Map<SQLField<E, ?>, Object> getValues() {
        return Collections.unmodifiableMap(values);
    }

    /**
     * Sets a value in this entry.
     * <p>
     * This is not good practice to set the {@code id} field of any entry, because it’s an unique auto-incremented
     * value. Use {@link #save()} and {@link #delete()} to set or unset the {@code id} instead, in consistence with the
     * database.
     * @param field the field to set.
     * @param value the new value for this field.
     * @return this.
     * @param <T> the Java type of the field.
     */
    @SuppressWarnings("unchecked")
    public <T> E set(SQLField<E, T> field, T value) {
        set(field, value, true);
        return (E) this;
    }

    /* package */ <T> void set(SQLField<E, T> sqlField, T value, boolean setModified) {
        if (sqlField == null) throw new IllegalArgumentException("sqlField can't be null");
        if (!fields.containsValue(sqlField)) // should not append at runtime because of generic type check at compilation
            throw new IllegalStateException("In the table "+getClass().getName()+ ": the field asked for modification is not initialized properly.");

        if (value == null) {
            if (!sqlField.nullable && (!sqlField.autoIncrement || stored))
                throw new IllegalArgumentException(
                        "SQLField '" + sqlField.getName() + "' of " + getClass().getName() + " is a NOT NULL field");
        }
        else if (!sqlField.type.isInstance(value)) {
            throw new IllegalArgumentException("SQLField '" + sqlField.getName() + "' of " + getClass().getName()
                    + " type is '" + sqlField.type + "' and can't accept values of type "
                    + value.getClass().getName());
        }

        if (!values.containsKey(sqlField)) {
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

    /**
     * Gets the value of the provided field in this entry.
     * @param field the field to get the value from.
     * @return the value of the provided field in this entry.
     * @param <T> the Java type of the field.
     */
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
     * Gets the foreign table entry targeted by the provided foreignkey of this table.
     * @param field a foreignkey of this table.
     * @param <T> the type of the foreignkey field.
     * @param <P> the targeted foreign table type.
     * @return the foreign table entry targeted by the provided foreignkey of this table.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public <T, P extends SQLElement<P>> P getReferencedEntry(SQLFKField<E, T, P> field) throws DBException {
        T fkValue = get(field);
        if (fkValue == null) return null;
        return DB.getFirst(field.getForeignElementClass(), field.getPrimaryField().eq(fkValue), null);
    }

    /**
     * Gets the original table entry which the provided foreign key is targeting this entry, and following the provided
     * {@code ORDER BY}, {@code LIMIT} and {@code OFFSET} clauses.
     * @param field a foreignkey in the original table.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @param limit the {@code LIMIT} clause of the query.
     * @param offset the {@code OFFSET} clause of the query.
     * @param <T> the type of the foreignkey field.
     * @param <F> the table class of the foreign key that reference a field of this entry.
     * @return the original table entry which the provided foreign key is targeting this entry.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public <T, F extends SQLElement<F>> SQLElementList<F> getReferencingForeignEntries(SQLFKField<F, T, E> field, SQLOrderBy<F> orderBy, Integer limit, Integer offset) throws DBException {
        T value = get(field.getPrimaryField());
        if (value == null) return new SQLElementList<>();
        return DB.getAll(field.getSQLElementType(), field.eq(value), orderBy, limit, offset);
    }

    /**
     * Determine if this entry is valid for save, that is when all the values are either set, or are nullable or have a
     * default value.
     * @return true if this entry is valid for save, false otherwise.
     */
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

    /**
     * Determine if the provided entry has been modified since this entry was created or saved.
     * @param field the field to check in this entry.
     * @return true if the field has been modified, false otherwise.
     */
    public boolean isModified(SQLField<E, ?> field) {
        return modifiedSinceLastSave.contains(field.getName());
    }

    /**
     * Saves this entry into the database, either by updating the already existing entry in it, or by creating a new
     * entry if it doesn’t exist yet.
     * @return this.
     * @throws DBException if an error occurs when interacting with the database.
     */
    @SuppressWarnings("unchecked")
    public E save() throws DBException {
        if (!isValidForSave())
            throw new IllegalStateException(this + " has at least one undefined value and can't be saved.");

        DB.initTable((Class<E>)getClass());
        try {

            if (stored) { // update in database
                // restore the id field to its real value in case it was modified using #set(...)
                values.put(fields.get("id"), id);
                modifiedSinceLastSave.remove("id");

                Map<SQLField<E, ?>, Object> modifiedValues = getOnlyModifiedValues();

                if (modifiedValues.isEmpty()) return (E) this;

                DB.update((Class<E>)getClass(), getIdField().eq(getId()), modifiedValues);
            }
            else { // add entry in the database
                // restore the id field to its real value in case it was modified using #set(...)
                values.put(fields.get("id"), null);

                StringBuilder concatValues = new StringBuilder();
                StringBuilder concatFields = new StringBuilder();
                List<Object> psValues = new ArrayList<>();

                boolean first = true;
                for (Map.Entry<SQLField<E, ?>, Object> entry : values.entrySet()) {
                    if (!first) {
                        concatValues.append(",");
                        concatFields.append(",");
                    }
                    first = false;
                    concatValues.append(" ? ");
                    concatFields.append("`").append(entry.getKey().getName()).append("`");
                    addValueToSQLObjectList(psValues, entry.getKey(), entry.getValue());
                }

                try (Connection c = db.getConnection();
                     PreparedStatement ps = c.prepareStatement("INSERT INTO " + DB.tablePrefix + tableName() + "  (" + concatFields + ") VALUES (" + concatValues + ")",
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
            throw new DBException("Error while saving data", e);
        }
        return (E) this;
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    /* package */ static <E extends SQLElement<E>> void addValueToSQLObjectList(List<Object> list, SQLField<E, ?> field, Object jValue) throws DBException {
        if (jValue != null && field.type instanceof SQLCustomType) {
            try {
                jValue = ((SQLCustomType)field.type).javaToDbConv.apply(jValue);
            } catch (Exception e) {
                throw new DBException("Error while converting value of field '"+field.getName()+"' with SQLCustomType from "+field.type.getJavaType()
                        +"(java source) to "+((SQLCustomType<?, ?>)field.type).intermediateJavaType+"(jdbc destination). The original value is '"+jValue+"'", e);
            }
        }
        list.add(jValue);
    }

    /**
     * Tells if this entry is currently stored in DB or not.
     * @return true if this entry is currently stored in DB, or false otherwise.
     */
    public boolean isStored() {
        return stored;
    }

    /**
     * Gets the id of the entry in the DB.
     * @return the id of the entry in the DB, or null if it’s not saved.
     */
    public Integer getId() {
        return (stored) ? id : null;
    }

    /**
     * Gets the {@link SQLField} instance corresponding to the {@code id} field of this table.
     * @return the {@link SQLField} instance corresponding to the {@code id} field of this table.
     */
    @SuppressWarnings("unchecked")
    public SQLField<E, Integer> getIdField() {
        return (SQLField<E, Integer>) fields.get("id");
    }

    /**
     * Deletes this entry from the database.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public void delete() throws DBException {
        if (stored) {
            DB.delete(getCheckedClass(), getIdField().eq(id));
            markAsNotStored();
        }
    }

    /* package */ void markAsNotStored() {
        stored = false;
        id = 0;
        modifiedSinceLastSave.clear();
        values.forEach((k, v) -> modifiedSinceLastSave.add(k.getName()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(this.getClass().getName());
        sb.append('{');
        sb.append(fields.values().stream()
                .map(f -> {
                    try {
                        return f.getName() + "=" + get(f);
                    } catch (IllegalArgumentException e) {
                        return f.getName() + "=(Undefined)";
                    }
                })
                .collect(Collectors.joining(", ")));
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(getClass().isInstance(o))) return false;
        SQLElement<?> oEl = (SQLElement<?>) o;
        if (oEl.getId() == null) return false;
        return oEl.getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode() ^ Objects.hashCode(getId());
    }


    /**
     * Creates a new SQL field.
     * @param type the type of the field.
     * @param nullable true if nullable, false if {@code NOT NULL}.
     * @param autoIncr if {@code AUTO_INCREMENT}.
     * @param deflt a default value for this field. A null value indicate that this has no default value.
     * @return the new SQL field.
     * @param <E> the table type.
     * @param <T> the Java type of this field.
     */
    protected static <E extends SQLElement<E>, T> SQLField<E, T> field(SQLType<T> type, boolean nullable, boolean autoIncr, T deflt) {
        return new SQLField<>(type, nullable, autoIncr, deflt);
    }

    /**
     * Creates a new SQL field.
     * @param type the type of the field.
     * @param nullable true if nullable, false if {@code NOT NULL}.
     * @return the new SQL field.
     * @param <E> the table type.
     * @param <T> the Java type of this field.
     */
    protected static <E extends SQLElement<E>, T> SQLField<E, T> field(SQLType<T> type, boolean nullable) {
        return new SQLField<>(type, nullable);
    }

    /**
     * Creates a new SQL field.
     * @param type the type of the field.
     * @param nullable true if nullable, false if {@code NOT NULL}.
     * @param autoIncr if {@code AUTO_INCREMENT}.
     * @return the new SQL field.
     * @param <E> the table type.
     * @param <T> the Java type of this field.
     */
    protected static <E extends SQLElement<E>, T> SQLField<E, T> field(SQLType<T> type, boolean nullable, boolean autoIncr) {
        return new SQLField<>(type, nullable, autoIncr);
    }

    /**
     * Creates a new SQL field.
     * @param type the type of the field.
     * @param nullable true if nullable, false if {@code NOT NULL}.
     * @param deflt a default value for this field. A null value indicate that this has no default value.
     * @return the new SQL field.
     * @param <E> the table type.
     * @param <T> the Java type of this field.
     */
    protected static <E extends SQLElement<E>, T> SQLField<E, T> field(SQLType<T> type, boolean nullable, T deflt) {
        return new SQLField<>(type, nullable, deflt);
    }






    /**
     * Creates a new SQL foreign key field pointing to the {@code id} field of the provided table.
     * @param nul true if this foreign key is nullable, false if {@code NOT NULL}.
     * @param deflt a default value for this field. A null value indicate that this has no default value.
     * @param fkEl the target table.
     * @return the new SQL foreign key field.
     * @param <E> the table type.
     * @param <F> the target table type.
     */
    protected static <E extends SQLElement<E>, F extends SQLElement<F>> SQLFKField<E, Integer, F> foreignKeyId(boolean nul, Integer deflt, Class<F> fkEl) {
        return SQLFKField.idFK(nul, deflt, fkEl);
    }

    /**
     * Creates a new SQL foreign key field pointing to the {@code id} field of the provided table.
     * @param nul true if this foreign key is nullable, false if {@code NOT NULL}.
     * @param fkEl the target table.
     * @return the new SQL foreign key field.
     * @param <E> the table type.
     * @param <F> the target table type.
     */
    protected static <E extends SQLElement<E>, F extends SQLElement<F>> SQLFKField<E, Integer, F> foreignKeyId(boolean nul, Class<F> fkEl) {
        return SQLFKField.idFK(nul, fkEl);
    }

    /**
     * Creates a new SQL foreign key field pointing to the provided field.
     * @param nul true if this foreign key is nullable, false if {@code NOT NULL}.
     * @param deflt a default value for this field. A null value indicate that this has no default value.
     * @param fkEl the target table.
     * @param fkF the field in the targeted table.
     * @return the new SQL foreign key field.
     * @param <E> the table type.
     * @param <T> the Java type of this field.
     * @param <F> the target table type.
     */
    protected static <E extends SQLElement<E>, T, F extends SQLElement<F>> SQLFKField<E, T, F> foreignKey(boolean nul, T deflt, Class<F> fkEl, SQLField<F, T> fkF) {
        return SQLFKField.customFK(nul, deflt, fkEl, fkF);
    }

    /**
     * Creates a new SQL foreign key field pointing to the provided field.
     * @param nul true if this foreign key is nullable, false if {@code NOT NULL}.
     * @param fkEl the target table.
     * @param fkF the field in the targeted table.
     * @return the new SQL foreign key field.
     * @param <E> the table type.
     * @param <T> the Java type of this field.
     * @param <F> the target table type.
     */
    protected static <E extends SQLElement<E>, T, F extends SQLElement<F>> SQLFKField<E, T, F> foreignKey(boolean nul, Class<F> fkEl, SQLField<F, T> fkF) {
        return SQLFKField.customFK(nul, fkEl, fkF);
    }











    // List of type from https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-type-conversions.html


    /** SQL type {@code BIT(1)} represented in Java as a {@link Boolean}. */
    public static final SQLType<Boolean> BIT_1 = new SQLType<>("BIT(1)", Boolean.class);

    /**
     * SQL type {@code BIT(bitCount)} represented in Java as a {@code byte[]}.
     * @param bitCount the number of bits. At least 2, or use {@link #BIT_1} if 1 bit is needed.
     * @return the SQL type {@code BIT} with the specified bitCount.
     */
    public static SQLType<byte[]> BIT(int bitCount) {
        if (bitCount <= 1)
            throw new IllegalArgumentException("charCount must be greater than 1. If 1 is desired, use BIT_1 instead.");
        return new SQLType<>("BIT(" + bitCount + ")", byte[].class);
    }

    /** SQL type {@code BOOLEAN} represented in Java as a {@link Boolean}. */
    public static final SQLType<Boolean> BOOLEAN = new SQLType<>("BOOLEAN", Boolean.class);

    /** SQL type {@code TINYINT} represented in Java as an {@link Integer}. */
    public static final SQLType<Integer> TINYINT = new SQLType<>("TINYINT", Integer.class); // can’t be Byte due to MYSQL JDBC Connector limitations

    /**
     * Alias for the SQL type {@code TINYINT} represented in Java as an {@link Integer}.
     * @deprecated use {@link #TINYINT} instead.
     */
    @Deprecated
    public static final SQLType<Integer> BYTE = TINYINT;

    /** SQL type {@code SMALLINT} represented in Java as an {@link Integer}. */
    public static final SQLType<Integer> SMALLINT = new SQLType<>("SMALLINT", Integer.class); // can’t be Short due to MYSQL JDBC Connector limitations

    /**
     * Alias for the SQL type {@code SMALLINT} represented in Java as an {@link Integer}.
     * @deprecated use {@link #SMALLINT} instead.
     */
    @Deprecated
    public static final SQLType<Integer> SHORT = SMALLINT;

    /** SQL type {@code MEDIUMINT} represented in Java as an {@link Integer}. */
    public static final SQLType<Integer> MEDIUMINT = new SQLType<>("MEDIUMINT", Integer.class);

    /** SQL type {@code INT} represented in Java as an {@link Integer}. */
    public static final SQLType<Integer> INT = new SQLType<>("INT", Integer.class);

    /**
     * Alias for the SQL type {@code INT} represented in Java as an {@link Integer}.
     * @deprecated use {@link #INT} instead.
     */
    @Deprecated
    public static final SQLType<Integer> INTEGER = INT;

    /** SQL type {@code BIGINT} represented in Java as a {@link Long}. */
    public static final SQLType<Long> BIGINT = new SQLType<>("BIGINT", Long.class);

    /**
     * Alias for the SQL type {@code BIGINT} represented in Java as a {@link Long}.
     * @deprecated use {@link #BIGINT} instead.
     */
    @Deprecated
    public static final SQLType<Long> LONG = BIGINT;

    /** SQL type {@code FLOAT} represented in Java as a {@link Float}. */
    public static final SQLType<Float> FLOAT = new SQLType<>("FLOAT", Float.class);

    /** SQL type {@code DOUBLE} represented in Java as a {@link Double}. */
    public static final SQLType<Double> DOUBLE = new SQLType<>("DOUBLE", Double.class);

    /** SQL type {@code DECIMAL} represented in Java as a {@link BigDecimal}. */
    public static final SQLType<BigDecimal> DECIMAL = new SQLType<>("DECIMAL", BigDecimal.class);

    /** SQL type {@code DATE} represented in Java as a {@link Date}. */
    public static final SQLType<Date> DATE = new SQLType<>("DATE", Date.class);

    /** SQL type {@code DATETIME} represented in Java as a {@link LocalDateTime}. */
    public static final SQLType<LocalDateTime> DATETIME = new SQLType<>("DATETIME", LocalDateTime.class);

    /** SQL type {@code TIMESTAMP} represented in Java as a {@link Timestamp}. */
    public static final SQLType<Timestamp> TIMESTAMP = new SQLType<>("TIMESTAMP", Timestamp.class);

    /** SQL type {@code TIME} represented in Java as a {@link Time}. */
    public static final SQLType<Time> TIME = new SQLType<>("TIME", Time.class);

    /**
     * SQL type {@code CHAR(charCount)} represented in Java as a {@code String}.
     * @param charCount the number of character.
     * @return the SQL type {@code CHAR} with the specified charCount.
     */
    public static SQLType<String> CHAR(int charCount) {
        if (charCount <= 0) throw new IllegalArgumentException("charCount must be positive.");
        return new SQLType<>("CHAR(" + charCount + ")", String.class);
    }

    /**
     * SQL type {@code VARCHAR(charCount)} represented in Java as a {@code String}.
     * @param charCount the number of character.
     * @return the SQL type {@code VARCHAR} with the specified charCount.
     */
    public static SQLType<String> VARCHAR(int charCount) {
        if (charCount <= 0) throw new IllegalArgumentException("charCount must be positive.");
        return new SQLType<>("VARCHAR(" + charCount + ")", String.class);
    }

    /**
     * SQL type {@code BINARY(byteCount)} represented in Java as a {@code byte[]}.
     * @param byteCount the number of bits.
     * @return the SQL type {@code BINARY} with the specified byteCount.
     */
    public static SQLType<byte[]> BINARY(int byteCount) {
        if (byteCount <= 0) throw new IllegalArgumentException("byteCount must be positive.");
        return new SQLType<>("BINARY(" + byteCount + ")", byte[].class);
    }

    /**
     * SQL type {@code VARBINARY(byteCount)} represented in Java as a {@code byte[]}.
     * @param byteCount the number of bits.
     * @return the SQL type {@code VARBINARY} with the specified byteCount.
     */
    public static SQLType<byte[]> VARBINARY(int byteCount) {
        if (byteCount <= 0) throw new IllegalArgumentException("byteCount must be positive.");
        return new SQLType<>("VARBINARY(" + byteCount + ")", byte[].class);
    }

    /** SQL type {@code BLOB} represented in Java as a {@link byte[]}. */
    public static final SQLType<byte[]> BLOB = new SQLType<>("BLOB", byte[].class);

    /** SQL type {@code TINYBLOB} represented in Java as a {@link byte[]}. */
    public static final SQLType<byte[]> TINYBLOB = new SQLType<>("TINYBLOB", byte[].class);

    /** SQL type {@code MEDIUMBLOB} represented in Java as a {@link byte[]}. */
    public static final SQLType<byte[]> MEDIUMBLOB = new SQLType<>("MEDIUMBLOB", byte[].class);

    /** SQL type {@code LONGBLOB} represented in Java as a {@link byte[]}. */
    public static final SQLType<byte[]> LONGBLOB = new SQLType<>("LONGBLOB", byte[].class);

    /** SQL type {@code TEXT} represented in Java as a {@link String}. */
    public static final SQLType<String> TEXT = new SQLType<>("TEXT", String.class);

    /**
     * Alias for the SQL type {@code TEXT} represented in Java as a {@link String}.
     * @deprecated use {@link #TEXT} instead.
     */
    @Deprecated
    public static final SQLType<String> STRING = TEXT;

    /** SQL type {@code TINYTEXT} represented in Java as a {@link String}. */
    public static final SQLType<String> TINYTEXT = new SQLType<>("TINYTEXT", String.class);

    /** SQL type {@code MEDIUMTEXT} represented in Java as a {@link String}. */
    public static final SQLType<String> MEDIUMTEXT = new SQLType<>("MEDIUMTEXT", String.class);

    /** SQL type {@code LONGTEXT} represented in Java as a {@link String}. */
    public static final SQLType<String> LONGTEXT = new SQLType<>("LONGTEXT", String.class);

    /**
     * SQL type {@code ENUM(...)} represented in Java as an enum value.
     * @param enumType the enum type.
     * @return the SQL type {@code ENUM} representing the provided enum.
     * @param <T> the type of the enum.
     */
    public static <T extends Enum<T>> SQLType<T> ENUM(Class<T> enumType) {
        if (enumType == null) throw new IllegalArgumentException("enumType can't be null.");
        StringBuilder enumStr = new StringBuilder("'");
        boolean first = true;
        for (T el : enumType.getEnumConstants()) {
            if (!first) enumStr.append("', '");
            first = false;
            enumStr.append(el.name());

        }
        enumStr.append("'");

        return new SQLCustomType<>("ENUM(" + enumStr + ")", String.class, enumType, s -> EnumUtil.searchEnum(enumType, s), Enum::name);
    }

    /**
     * SQL type {@code SET(...)} represented in Java as an {@link EnumSet} of the specified enum type.
     * @param enumType the enum type.
     * @return the SQL type {@code SET} representing the provided enum.
     * @param <T> the type of the enum.
     */
    public static <T extends Enum<T>> SQLType<EnumSet<T>> SET(Class<T> enumType) {
        if (enumType == null) throw new IllegalArgumentException("enumType can't be null.");
        StringBuilder enumStr = new StringBuilder("'");
        boolean first = true;
        for (T el : enumType.getEnumConstants()) {
            if (!first) enumStr.append("', '");
            first = false;
            enumStr.append(el.name());

        }
        enumStr.append("'");

        @SuppressWarnings("unchecked")
        Class<EnumSet<T>> enumSetType = (Class<EnumSet<T>>) EnumSet.noneOf(enumType).getClass();

        return new SQLCustomType<>("SET(" + enumStr + ")", String.class, enumSetType,
                s -> Arrays.stream(s.split(","))
                        .map(strV -> EnumUtil.searchEnum(enumType, strV))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(() -> EnumSet.noneOf(enumType))),
                set -> set.stream()
                        .map(Enum::name)
                        .collect(Collectors.joining(",")));
    }

    /** A custom type based on SQL type {@code CHAR(36)} represented in Java as a {@link UUID}. */
    public static final SQLType<UUID> CHAR36_UUID = new SQLCustomType<>(CHAR(36), UUID.class, UUID::fromString, UUID::toString);
}
