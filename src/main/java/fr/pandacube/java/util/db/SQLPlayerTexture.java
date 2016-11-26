package fr.pandacube.java.util.db;

import fr.pandacube.java.util.db.sql_tools.SQLElement;
import fr.pandacube.java.util.db.sql_tools.SQLFKField;
import fr.pandacube.java.util.db.sql_tools.SQLField;
import fr.pandacube.java.util.db.sql_tools.SQLType;

public class SQLPlayerTexture extends SQLElement<SQLPlayerTexture> {
	
	public SQLPlayerTexture() {
		super();
	}

	public SQLPlayerTexture(int id) {
		super(id);
	}

	@Override
	protected String tableName() {
		return "pandacube_player_texture";
	}
	
	public static final SQLFKField<SQLPlayerTexture, String, SQLPlayer> playerId = new SQLFKField<>("playerId", SQLType.CHAR(36), false,
			SQLPlayer.class, SQLPlayer.playerId);
	public static final SQLField<SQLPlayerTexture, String> alias = new SQLField<>("alias", SQLType.VARCHAR(64), false);
	public static final SQLField<SQLPlayerTexture, String> textureData = new SQLField<>("textureData", SQLType.TEXT, false);
	public static final SQLField<SQLPlayerTexture, String> textureSignature = new SQLField<>("textureSignature", SQLType.VARCHAR(8192), false);

}
