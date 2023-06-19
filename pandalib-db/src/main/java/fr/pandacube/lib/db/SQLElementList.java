package fr.pandacube.lib.db;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An {@link ArrayList} that provides special operations for the table entries it contains.
 * @param <E> the table type.
 */
public class SQLElementList<E extends SQLElement<E>> extends ArrayList<E> {

    /**
     * Stores all the values modified by {@link #setCommon(SQLField, Object)}.
     */
    private final Map<SQLField<E, ?>, Object> modifiedValues = new LinkedHashMap<>();

    @Override
    public synchronized boolean add(E e) {
        if (e == null || !e.isStored()) return false;
        return super.add(e);
    }

    /**
     * Sets the value of a field for all the entries.
     * The changed value is stored in this {@link SQLElementList} itself, and does not modify the content of the entries.
     * To apply the modification into the database and into the entries themselves, call {@link #saveCommon()}.
     * @param field the field to set.
     * @param value the new value for this field.
     * @param <T> the Java type of the field.
     */
    public synchronized <T> void setCommon(SQLField<E, T> field, T value) {
        if (field == null)
            throw new IllegalArgumentException("field can't be null");
        if (Objects.equals(field.getName(), "id"))
            throw new IllegalArgumentException("Can't modify id field in a SQLElementList");

        Class<E> elemClass = field.getSQLElementType();
        try {
            E emptyElement = elemClass.getConstructor().newInstance();
            emptyElement.set(field, value, false);
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal field or value or can't instantiate an empty instance of "
                    + elemClass.getName() + ". (the instance is only created to test validity of field and value)", e);
        }

        // ici, la valeur est bonne
        modifiedValues.put(field, value);

    }

    /**
     * Apply all the changes made with {@link #setCommon(SQLField, Object)} to the entries currently present in this
     * list.
     * The change is applied in the database and into the entries in this list (except the fields that has already been
     * modified in an entry (checked using {@link SQLElement#isModified(SQLField)})).
     * The entries of this list that are not stored in database (using {@link SQLElement#isStored()}) are ignored.
     * @return the value returned by {@link PreparedStatement#executeUpdate()}.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public synchronized int saveCommon() throws DBException {
        List<E> storedEl = getStoredEl();
        if (storedEl.isEmpty()) return 0;

        @SuppressWarnings("unchecked")
        Class<E> classEl = (Class<E>)storedEl.get(0).getClass();

        int ret = DB.update(classEl,
                storedEl.get(0).getIdField().in(storedEl.stream().map(SQLElement::getId).collect(Collectors.toList())
                ),
                modifiedValues);

        applyNewValuesToElements(storedEl);

        return ret;
    }

    @SuppressWarnings("unchecked")
    private void applyNewValuesToElements(List<E> storedEl) {
        // applique les valeurs dans chaque objet de la liste
        for (E el : storedEl) {
            for (@SuppressWarnings("rawtypes") SQLField entry : modifiedValues.keySet()) {
                if (!el.isModified(entry)) {
                    el.set(entry, modifiedValues.get(entry), false);
                }
            }
        }
    }

    private List<E> getStoredEl() {
        return stream().filter(SQLElement::isStored).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Removes all the entries of this list from the database.
     * This method has the same effect as calling the {@link SQLElement#delete()} method individually on each element,
     * but with only one SQL query to delete all the entries.
     * <p>
     * If you intend to remove the entries from the database just after fetching them, call directly the
     * {@link DB#delete(Class, SQLWhere)} method instead.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public synchronized void deleteFromDB() throws DBException {
        List<E> storedEl = getStoredEl();
        if (storedEl.isEmpty()) return;

        @SuppressWarnings("unchecked")
        Class<E> classEl = (Class<E>)storedEl.get(0).getClass();

        DB.delete(classEl,
                storedEl.get(0).getIdField().in(storedEl.stream().map(SQLElement::getId).collect(Collectors.toList()))
        );
        for (E el : storedEl)
            el.markAsNotStored();

    }


    /**
     * Get all the entries targeted by the foreign key of all the entries in this list.
     * @param foreignKey a foreign key of this table.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @return a list of foreign table entries targeted by the provided foreign key of this table.
     * @param <T> the field’s Java type.
     * @param <P> the target table type.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public <T, P extends SQLElement<P>> SQLElementList<P> getReferencedEntries(SQLFKField<E, T, P> foreignKey, SQLOrderBy<P> orderBy) throws DBException {
        Set<T> values = stream()
                .map(v -> v.get(foreignKey))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return values.isEmpty()
                ? new SQLElementList<>()
                : DB.getAll(foreignKey.getForeignElementClass(), foreignKey.getPrimaryField().in(values), orderBy, null, null);
    }


    /**
     * Get all the entries targeted by the foreign key of all the entries in this list, mapped from the foreign key value.
     * @param foreignKey a foreign key of this table.
     * @return a map of the foreign key values, mapped to the foreign table’s entries.
     * @param <T> the field’s Java type.
     * @param <P> the target table type.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public <T, P extends SQLElement<P>> Map<T, P> getReferencedEntriesInGroups(SQLFKField<E, T, P> foreignKey) throws DBException {
        return getReferencedEntries(foreignKey, null).stream()
                .collect(Collectors.toMap(
                        foreignVal -> foreignVal.get(foreignKey.getPrimaryField()),
                        Function.identity(),
                        (a, b) -> b)
                );
    }


    /**
     * Gets all the original table’s entries which the provided foreign key is targeting the entries of this list, and
     * following the provided {@code ORDER BY}, {@code LIMIT} and {@code OFFSET} clauses.
     * @param foreignKey a foreign key in the original table.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @param limit the {@code LIMIT} clause of the query.
     * @param offset the {@code OFFSET} clause of the query.
     * @param <T> the type of the foreign key field.
     * @param <F> the table class of the foreign key that reference a field of this entry.
     * @return the original table’s entries which the provided foreign key is targeting the entries of this list.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public <T, F extends SQLElement<F>> SQLElementList<F> getReferencingForeignEntries(SQLFKField<F, T, E> foreignKey, SQLOrderBy<F> orderBy, Integer limit, Integer offset) throws DBException {
        Set<T> values = stream()
                .map(v -> v.get(foreignKey.getPrimaryField()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        return values.isEmpty()
                ? new SQLElementList<>()
                : DB.getAll(foreignKey.getSQLElementType(), foreignKey.in(values), orderBy, limit, offset);
    }


    /**
     * Gets all the original table’s entries which the provided foreign key is targeting the entries of this list,
     * following the provided {@code ORDER BY}, {@code LIMIT} and {@code OFFSET} clauses, and mapped from the foreign
     * key value.
     * @param foreignKey a foreign key in the original table.
     * @param orderBy the {@code ORDER BY} clause of the query.
     * @param limit the {@code LIMIT} clause of the query.
     * @param offset the {@code OFFSET} clause of the query.
     * @param <T> the type of the foreign key field.
     * @param <F> the table class of the foreign key that reference a field of this entry.
     * @return a map of the foreign key values, mapped to the orignal table’s entries.
     * @throws DBException if an error occurs when interacting with the database.
     */
    public <T, F extends SQLElement<F>> Map<T, SQLElementList<F>> getReferencingForeignEntriesInGroups(SQLFKField<F, T, E> foreignKey, SQLOrderBy<F> orderBy, Integer limit, Integer offset) throws DBException {
        return getReferencingForeignEntries(foreignKey, orderBy, limit, offset).stream()
                .collect(Collectors.groupingBy(
                        e -> e.get(foreignKey),
                        Collectors.toCollection(SQLElementList::new)
                ));
    }



}
