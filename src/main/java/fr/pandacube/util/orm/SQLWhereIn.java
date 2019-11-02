package fr.pandacube.util.orm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.javatuples.Pair;

public class SQLWhereIn extends SQLWhere {

	private SQLField<?, ?> field;
	private Collection<?> values;

	public <T> SQLWhereIn(SQLField<?, T> f, Collection<T> v) {
		if (f == null || v == null)
			throw new IllegalArgumentException("All arguments for SQLWhereIn constructor can't be null");
		field = f;
		values = v;
	}

	@Override
	public Pair<String, List<Object>> toSQL() throws ORMException {
		List<Object> params = new ArrayList<>();
		for (Object v : values)
			SQLElement.addValueToSQLObjectList(params, field, v);
		
		char[] questions = new char[values.size() == 0 ? 0 : (values.size() * 2 - 1)];
		for (int i = 0; i < questions.length; i++)
			questions[i] = i % 2 == 0 ? '?' : ',';
		
		return new Pair<>("`" + field.getName() + "` IN (" + new String(questions) + ") ", params);
	}

}
