package fr.pandacube.java.util.db;

import java.sql.Date;
import java.util.UUID;

public class PlayerElement extends SQLElement {
	
	private String playerId;
	private String token = null;
	private String mailCheck = null;
	private String password = null;
	private String mail = null;
	private String playerDisplayName;
	private long firstTimeInGame;
	private long timeWebRegister = 0;
	private long lastTimeInGame = 0;
	private long lastWebActivity = 0;
	private String onlineInServer = null;
	private String skinURL = null;
	private boolean isVanish = false;
	private Date birthday = null;
	private int lastYearCelebratedBirthday = 0;
	private Long banTimeout = null;
	private Long muteTimeout = null;
	private boolean isWhitelisted = false;

	public PlayerElement(UUID pId, String dispName, long firstTimeIG, long lastWebAct, String onlineInServer) {
		super("pandacube_player");
		setPlayerId(pId);
		setOnlineInServer(onlineInServer);
		setLastWebActivity(lastWebAct);
		setPlayerDisplayName(dispName);
		setFirstTimeInGame(firstTimeIG);
	}
	
	protected PlayerElement(int id, String pId, String dispName, long firstTimeIG, long lastWebAct, String onlineInServer) {
		super("pandacube_player", id);
		setPlayerId(UUID.fromString(pId));
		setOnlineInServer(onlineInServer);
		setLastWebActivity(lastWebAct);
		setPlayerDisplayName(dispName);
		setFirstTimeInGame(firstTimeIG);
	}

	@Override
	protected String[] getValues() {
		return new String[] {
				playerId,
				token,
				mailCheck,
				password,
				mail,
				playerDisplayName,
				Long.toString(firstTimeInGame),
				Long.toString(timeWebRegister),
				Long.toString(lastTimeInGame),
				Long.toString(lastWebActivity),
				onlineInServer,
				skinURL,
				isVanish?"1":"0",
				(birthday!=null)?birthday.toString():null,
				Integer.toString(lastYearCelebratedBirthday),
				(banTimeout!=null)?banTimeout.toString():null,
				(muteTimeout!=null)?muteTimeout.toString():null,
				isWhitelisted?"1":"0"
		};
	}

	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"playerId",
				"token",
				"mailCheck",
				"password",
				"mail",
				"playerDisplayName",
				"firstTimeInGame",
				"timeWebRegister",
				"lastTimeInGame",
				"lastWebActivity",
				"onlineInServer",
				"skinURL",
				"isVanish",
				"birthday",
				"lastYearCelebratedBirthday",
				"banTimeout",
				"muteTimeout",
				"isWhitelisted"
		};
	}

	public UUID getPlayerId() { return UUID.fromString(playerId); }
	public UUID getToken() { return (token == null) ? null : UUID.fromString(token); }
	public String getMailCheck() { return mailCheck; }
	public String getPasswordHash() { return password; }
	public String getMail() { return mail; }
	public long getFirstTimeInGame() { return firstTimeInGame; }
	public long getTimeWebRegister() { return timeWebRegister; }
	public long getLastTimeInGame() { return lastTimeInGame; }
	public long getLastWebActivity() { return lastWebActivity; }
	public String getOnlineInServer() { return onlineInServer; }
	public String getPlayerDisplayName() { return playerDisplayName; }
	public String getSkinURL() { return skinURL; }
	public boolean isVanish() { return isVanish; }
	public Date getBirthday() { return birthday; }
	public int getLastYearCelebratedBirthday() { return lastYearCelebratedBirthday; }
	public Long getBanTimeout() { return banTimeout; }
	public Long getMuteTimeout() { return muteTimeout; }
	public boolean isWhitelisted() { return isWhitelisted; }
	
	

	public void setPlayerId(UUID pName) {
		if (pName == null)
			throw new NullPointerException();
		playerId = pName.toString();
	}
	
	public void setToken(UUID t) {
		if (t == null)
			token = null;
		else
			token = t.toString();
	}

	public void setMailCheck(String mCheck) { mailCheck = mCheck; }

	public void setPasswordHash(String pass) { password = pass; }

	public void setMail(String m) { mail = m; }

	public void setFirstTimeInGame(long time) { firstTimeInGame = time; }

	public void setTimeWebRegister(long time) { timeWebRegister = time; }

	public void setLastTimeInGame(long time) { lastTimeInGame = time; }

	public void setLastWebActivity(long time) { lastWebActivity = time; }
	
	public void setOnlineInServer(String onlineInServer) { this.onlineInServer = onlineInServer; }
	
	public void setSkinURL(String skinURL) { this.skinURL = skinURL; }
	
	public void setPlayerDisplayName(String dispName) {
		if (dispName == null)
			throw new NullPointerException();
		playerDisplayName = dispName;
	}

	public void setVanish(boolean v) { isVanish = v; }
	
	public void setBirthday(Date b) { birthday = b; }
	
	public void setLastYearCelebratedBirthday(int y) { lastYearCelebratedBirthday = y; }
	
	public void setBanTimeout(Long banT) { banTimeout = banT; }
	
	public void setMuteTimeout(Long muteT) { muteTimeout = muteT; }
	
	public void setWhitelisted(boolean w) { isWhitelisted = w; }

}
