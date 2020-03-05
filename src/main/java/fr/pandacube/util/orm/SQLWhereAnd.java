package fr.pandacube.util.orm;

public class SQLWhereAnd<E extends SQLElement<E>> extends SQLWhereChain<E> {

	/* package */ SQLWhereAnd() {
		super(SQLBoolOp.AND);
	}
	
	@Override
	public SQLWhereAnd<E> and(SQLWhere<E> other) {
		add(other);
		return this;
	}

}
