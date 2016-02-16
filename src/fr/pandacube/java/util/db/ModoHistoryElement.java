package fr.pandacube.java.util.db;

import java.util.UUID;

public class ModoHistoryElement extends SQLElement {
	
	private String modoId = null;
	private ActionType actionType;
	private long time;
	private String playerId;
	private Long value = null;
	private String message;
	

	public ModoHistoryElement(UUID modo, ActionType type, UUID player, String message) {
		super("pandacube_modo_history");
		setModoId(modo);
		setActionType(type);
		setPlayerId(player);
		setMessage(message);
		time = System.currentTimeMillis();
	}
	
	ModoHistoryElement(int id, String modo, ActionType type, String player, String message) {
		super("pandacube_modo_history", id);
		setModoId((modo == null)?null:UUID.fromString(modo));
		setActionType(type);
		setPlayerId(UUID.fromString(player));
		setMessage(message);
		time = System.currentTimeMillis();
	}

	@Override
	protected String[] getValues() {
		return new String[] {
				modoId,
				actionType.name(),
				String.valueOf(time),
				playerId,
				(value == null)?null:value.toString(),
				message
		};
	}

	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"modoId",
				"actionType",
				"time",
				"playerId",
				"value",
				"message"
		};
	}
	
	
	
	public UUID getModoId() {
		return modoId == null ? null : UUID.fromString(modoId);
	}

	public void setModoId(UUID modo) {
		this.modoId = modo == null ? null : modo.toString();
	}



	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		if (actionType == null) throw new IllegalArgumentException("le paramètre ne peut être null");
		this.actionType = actionType;
	}


	/**
	 * Retourne la durée de la sanction appliquée (en secondes), ou la somme d'argent retirée du compte
	 * @return
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Value correspond soit à la durée de la sanction appliquée (en secondes), soit à la valeur de l'amende appliquée
	 * @param value
	 */
	public void setValue(Long value) {
		if (value != null && value.longValue() < 0) value = null;
		this.value = value;
	}



	public UUID getPlayerId() {
		return UUID.fromString(playerId);
	}

	public void setPlayerId(UUID player) {
		if (player == null) throw new IllegalArgumentException("le paramètre ne peut être null");
		this.playerId = player.toString();
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		if (message == null) throw new IllegalArgumentException("le paramètre ne peut être null");
		this.message = message;
	}







	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}







	public enum ActionType{
		BAN, UNBAN, MUTE, UNMUTE, REPORT, KICK
	}

}
