package fr.pandacube.java.util.db;

import java.sql.SQLException;
import java.util.UUID;

/**
 * Représente un message dans la base de donnée<br/>
 * <br/>
 * Les propriétés suivantes doivent être complétés hors constructeur (par défaut <code>null</code>) :
 * <ul>
 * 	<li><code>destNick</code></li>
 * 	<li>ou <code>destGroup</code></li>
 * </ul>
 * La propriété <code>deleted</code> est défini par défaut à Faux.
 * @author Marc Baloup
 *
 */
public class MPMessageElement extends SQLElement {

	private long time;
	private int securityKey; // permet de différencier deux message, dans le cas où 2 messages ont exactement la même valeur time
	private String viewerId;
	private String sourceId;
	private String destId = null;
	private Integer destGroup = null;
	private String message;
	private boolean wasRead;
	private boolean deleted = false;
	private boolean serverSync;
	
	
	
	public MPMessageElement(long t, int secKey, UUID viewId, UUID srcId, String msg, boolean r, boolean sync) {
		super("pandacube_mp_message");
		setTime(t);
		setSecurityKey(secKey);
		setViewerId(viewId);
		setSourceId(srcId);
		setMessage(msg);
		setRead(r);
		setServerSync(sync);
	}
	
	
	protected MPMessageElement(int id, long t, int secKey, String viewNick, String srcNick, String msg, boolean r, boolean sync) {
		super("pandacube_mp_message", id);
		setTime(t);
		setSecurityKey(secKey);
		setViewerId(UUID.fromString(viewNick));
		setSourceId((srcNick == null) ? null : UUID.fromString(srcNick));
		setMessage(msg);
		setRead(r);
		setServerSync(sync);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	protected String[] getValues() {
		return new String[] {
				Long.toString(time),
				Integer.toString(securityKey),
				viewerId,
				sourceId,
				destId,
				(destGroup==null)?null:destGroup.toString(),
				message,
				(wasRead)?"1":"0",
				(deleted)?"1":"0",
				(serverSync)?"1":"0"
		};
	}

	
	
	
	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"time",
				"securityKey",
				"viewerId",
				"sourceId",
				"destId",
				"destGroup",
				"message",
				"wasRead",
				"deleted",
				"serverSync"
		};
	}


	public long getTime() { return time; }
	public int getSecurityKey() { return securityKey; }
	public UUID getViewerId() { return UUID.fromString(viewerId); }
	public UUID getSourceId() {
		if (sourceId == null) return null;
		return UUID.fromString(sourceId);
	}
	public UUID getDestId() {
		if (destId == null) return null;
		return UUID.fromString(destId);
	}
	public Integer getDestGroup() { return destGroup; }
	public String getMessage() { return message; }
	public boolean isRead() { return wasRead; }
	public boolean isDeleted() { return deleted; }
	public boolean isServerSync() { return serverSync; }
	
	
	
	
	public void setTime(long t) { time = t; }
	public void setSecurityKey(int secKey) { securityKey = secKey; }
	
	public void setViewerId(UUID viewId) {
		if (viewId == null)
			throw new NullPointerException();
		viewerId = viewId.toString();
	}

	public void setSourceId(UUID srcId) {
		if (srcId == null) sourceId = null;
		else sourceId = srcId.toString();
	}

	public void setDestId(UUID destId) {
		if (destId == null) this.destId = null;
		else {
			this.destId = destId.toString();
			destGroup = null;
		}
	}

	public void setDestGroup(Integer destGroup) {
		this.destGroup = destGroup;
		if (destGroup != null)
			destId = null;
	}

	public void setMessage(String msg) {
		if (msg == null)
			throw new NullPointerException();
		message = msg;
	}

	public void setRead(boolean r) { wasRead = r; }
	public void setDeleted(boolean del) { deleted = del; }
	public void setServerSync(boolean sync) { serverSync = sync; }
	
	
	
	
	
	public MPGroupElement getDestGroupElement() throws SQLException {
		if (getDestGroup() == null) return null;
		
		return ORM.getTable(MPGroupTable.class).get(getDestGroup());
	}

}
