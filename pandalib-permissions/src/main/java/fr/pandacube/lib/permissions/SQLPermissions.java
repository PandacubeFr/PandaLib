package fr.pandacube.lib.permissions;

import fr.pandacube.lib.db.SQLElement;
import fr.pandacube.lib.db.SQLField;

/**
 * SQL Table to store the permissions data.
 */
public class SQLPermissions extends SQLElement<SQLPermissions> {

	/**
	 * Instantiate a new entry in the table.
	 */
	public SQLPermissions() {
		super();
	}

	private SQLPermissions(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "permissions";
	}

	/** The name of the entity (player id or group name). */
	public static final SQLField<SQLPermissions, String> name = field(VARCHAR(64), false);
	/** The entity type, based on {@link EntityType}. */
	public static final SQLField<SQLPermissions, Integer> type = field(TINYINT, false);
	/** The key of the data ("permission", "inheritance", …). */
	public static final SQLField<SQLPermissions, String> key = field(VARCHAR(256), false);
	/** The data value (permission node, name of inherited group, prefix/suffix, …). */
	public static final SQLField<SQLPermissions, String> value = field(VARCHAR(256), false);
	/** The server in which the permission apply. */
	public static final SQLField<SQLPermissions, String> server = field(VARCHAR(64), true);
	/** The world in which the permission apply. */
	public static final SQLField<SQLPermissions, String> world = field(VARCHAR(64), true);

	/**
	 * All possible type of entity type.
	 */
	public enum EntityType {
		/**
		 * User entity type.
		 */
	    User,
		/**
		 * Group entity type.
		 */
	    Group;

		/**
		 * Returns the database value of this entity type.
		 * @return the database value of this entity type.
		 */
	    public int getCode() {
	        return ordinal();
	    }

		/**
		 * Gets the {@link EntityType} corresponding to the database value.
		 * @param code the database value.
		 * @return the {@link EntityType} corresponding to the database value.
		 */
	    public static EntityType getByCode(int code) {
	    	if (code >= 0 && code < values().length)
	    		return values()[code];
	        return null;
	    }
	}
	
	
	
}
