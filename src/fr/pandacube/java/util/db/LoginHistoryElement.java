package fr.pandacube.java.util.db;

import java.net.InetAddress;
import java.util.UUID;

public class LoginHistoryElement extends SQLElement {
	
	
	private long time;
	private String playerId;
	private String ip = null;
	private ActionType actionType;
	private int nbOnline;
	private String playerName;
	private int minecraftVersion = 0;
	
	
	public LoginHistoryElement(long t, UUID pId, ActionType action, int nbO) {
		super("pandacube_login_history");
		setTime(t);
		setPlayerId(pId);
		setActionType(action);
		setNbOnline(nbO);
	}
	
	LoginHistoryElement(int id, long t, String pId, String ip, ActionType action, int nbO) {
		super("pandacube_login_history", id);
		if (pId == null)
			throw new IllegalArgumentException("pId ne peuvent être null");
		setTime(t);
		playerId = pId;
		this.ip = ip;
		setActionType(action);
		setNbOnline(nbO);
	}

	@Override
	protected String[] getValues() {
		return new String[] {
				Long.toString(time),
				playerId,
				ip,
				actionType.toString(),
				Integer.toString(nbOnline),
				playerName,
				Integer.toString(minecraftVersion)
		};
	}

	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"time",
				"playerId",
				"ip",
				"actionType",
				"nbOnline",
				"playerName",
				"minecraftVersion"
		};
	}
	
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}


	public UUID getPlayerId() {
		return UUID.fromString(playerId);
	}

	public void setPlayerId(UUID pId) {
		if (pId == null)
			throw new IllegalArgumentException("pId ne peut être null");
		playerId = pId.toString();
	}


	public String getIp() {
		return ip;
	}

	public void setIp(InetAddress addr) {
		if (addr == null)
			ip = null;
		else
			ip = addr.getHostAddress();
	}


	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionT) {
		if (actionT == null)
			throw new IllegalArgumentException("actionT ne peut être null");
		actionType = actionT;
	}


	public int getNbOnline() {
		return nbOnline;
	}

	public void setNbOnline(int nbOnline) {
		this.nbOnline = nbOnline;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public void setPlayerName(String pn) {
		playerName = pn;
	}
	
	public int getMinecraftVersion() {
		return minecraftVersion;
	}
	
	public void setMinecraftVersion(int m) {
		minecraftVersion = m;
	}
	
	
	
	
	
	
	
	
	


	public enum ActionType {
		LOGIN, LOGOUT
	}

}
