package fr.pandacube.java.util.db;

import java.net.InetAddress;
import java.util.UUID;

public class LoginHistoryElement extends SQLElement {
	
	
	private long time;
	private String playerId;
	private String ip;
	private ActionType actionType;
	private int nbOnline;
	
	
	public LoginHistoryElement(long t, UUID pId, InetAddress IP, ActionType action, int nbO) {
		super("pandacube_login_history");
		setTime(t);
		setPlayerId(pId);
		setIp(IP);
		setActionType(action);
		setNbOnline(nbO);
	}
	
	LoginHistoryElement(int id, long t, String pId, String IP, ActionType action, int nbO) {
		super("pandacube_login_history", id);
		if (IP == null || pId == null)
			throw new IllegalArgumentException("pId et IP ne peuvent être null");
		setTime(t);
		playerId = pId;
		ip = IP;
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
				Integer.toString(nbOnline)
		};
	}

	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"time",
				"playerId",
				"ip",
				"actionType",
				"nbOnline"
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
			throw new IllegalArgumentException("addr ne peut être null");
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


	public enum ActionType {
		LOGIN, LOGOUT
	}

}
