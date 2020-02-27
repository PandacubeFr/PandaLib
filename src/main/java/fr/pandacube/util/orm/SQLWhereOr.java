package fr.pandacube.util.orm;

public class SQLWhereOr extends SQLWhereChain {

	/* package */ SQLWhereOr() {
		super(SQLBoolOp.OR);
	}
	
	@Override
	public SQLWhereOr or(SQLWhere other) {
		return (SQLWhereOr) add(other);
	}

}
