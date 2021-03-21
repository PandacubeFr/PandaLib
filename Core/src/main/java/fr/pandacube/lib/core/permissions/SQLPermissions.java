package fr.pandacube.lib.core.permissions;

import fr.pandacube.lib.core.db.SQLElement;
import fr.pandacube.lib.core.db.SQLField;

public class SQLPermissions extends SQLElement<SQLPermissions> {

	public SQLPermissions() {
		super();
	}

	public SQLPermissions(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "permissions";
	}

	public static final SQLField<SQLPermissions, String> name = field(VARCHAR(64), false);
	public static final SQLField<SQLPermissions, Integer> type = field(TINYINT, false);
	public static final SQLField<SQLPermissions, String> key = field(VARCHAR(256), false);
	public static final SQLField<SQLPermissions, String> value = field(VARCHAR(256), false);
	public static final SQLField<SQLPermissions, String> server = field(VARCHAR(64), true);
	public static final SQLField<SQLPermissions, String> world = field(VARCHAR(64), true);

	
	public enum EntityType {
	    User,
	    Group;

	    public int getCode() {
	        return ordinal();
	    }

	    public static EntityType getByCode(int code) {
	    	if (code >= 0 && code < values().length)
	    		return values()[code];
	        return null;
	    }
	}
	
	
	
}
