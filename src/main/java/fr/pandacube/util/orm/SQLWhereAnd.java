package fr.pandacube.util.orm;

public class SQLWhereAnd extends SQLWhereChain {

	/* package */ SQLWhereAnd() {
		super(SQLBoolOp.AND);
	}
	
	@Override
	public SQLWhereAnd and(SQLWhere other) {
		return (SQLWhereAnd) add(other);
	}

}
