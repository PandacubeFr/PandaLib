package fr.pandacube.lib.db;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A SQL {@code ORDER BY} expression builder.
 * @param <E> the table type.
 */
public class SQLOrderBy<E extends SQLElement<E>> {

    /**
     * Creates a new SQL {@code ORDER BY} expression builder with the provided field to sort in the ascending order.
     * @param field le field to order.
     * @return a new SQL {@code ORDER BY} expression builder.
     * @param <E> the type of the table declaring the field.
     */
    public static <E extends SQLElement<E>> SQLOrderBy<E> asc(SQLField<E, ?> field) {
        return new SQLOrderBy<E>().thenAsc(field);
    }

    /**
     * Creates a new SQL {@code ORDER BY} expression builder with the provided field to sort in the descending order.
     * @param field le field to order.
     * @return a new SQL {@code ORDER BY} expression builder.
     * @param <E> the type of the table declaring the field.
     */
    public static <E extends SQLElement<E>> SQLOrderBy<E> desc(SQLField<E, ?> field) {
        return new SQLOrderBy<E>().thenDesc(field);
    }






    private final List<OBField<E>> orderByFields = new ArrayList<>();

    private SQLOrderBy() {}

    private SQLOrderBy<E> add(SQLField<E, ?> field, Direction d) {
        orderByFields.add(new OBField<>(field, d));
        return this;
    }

    /**
     * Adds the provided field to sort in the ascending order, in this {@code ORDER BY} expression builder.
     * @param field le field to order.
     * @return this.
     */
    public SQLOrderBy<E> thenAsc(SQLField<E, ?> field) {
        return add(field, Direction.ASC);
    }

    /**
     * Adds the provided field to sort in the descending order, in this {@code ORDER BY} expression builder.
     * @param field le field to order.
     * @return this.
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












    private record OBField<E extends SQLElement<E>>(SQLField<E, ?> field, Direction direction) { }

    private enum Direction {
        ASC, DESC
    }

}
