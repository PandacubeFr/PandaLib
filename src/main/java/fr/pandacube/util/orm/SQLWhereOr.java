package fr.pandacube.util.orm;

public class SQLWhereOr<E extends SQLElement<E>> extends SQLWhereChain<E> {

	/* package */ SQLWhereOr() {
		super(SQLBoolOp.OR);
	}
	
	@Override
	public SQLWhereOr<E> or(SQLWhere<E> other) {
		add(other);
		return this;
	}

}
