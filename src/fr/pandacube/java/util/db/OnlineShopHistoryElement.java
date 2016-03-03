package fr.pandacube.java.util.db;

import java.util.UUID;

public class OnlineShopHistoryElement extends SQLElement {
	
	
	private long time;// timestamp en millisecondes
	private SourceType sourceType;// enum(REAL_MONEY, BAMBOU)
	private String sourcePlayerId;// l'id du joueur duquel vient l'élément source
	private double sourceQuantity;// la quantité d'entrée (en euro, ou bambou)
	private String sourceName;// le nom désignant la source ("euro", "bambou", ...)
	private DestType destType;// enum(BAMBOU, GRADE)
	private String destPlayerId;// l'id du joueur qui reçoit l'élément obtenu après cette transaction
	private double destQuantity;// la quantité de sortie (bambou, ou 1 pour l'achat d'un grade)
	private String destName;// le nom désignant la destination ("bambou", le nom du grade)

	public OnlineShopHistoryElement(long t, SourceType st, UUID sPID, double sQtt, String sN, DestType dt, UUID dPID, double dQtt, String dN) {
		super("pandacube_onlineshop_history");
		setTime(t);
		setSourceType(st);
		setSourcePlayerId(sPID);
		setSourceQuantity(sQtt);
		setSourceName(sN);
		setDestType(dt);
		setDestPlayerId(dPID);
		setDestQuantity(dQtt);
		setDestName(dN);
	}
	
	OnlineShopHistoryElement(int id, long t, String st, String sPID, double sQtt, String sN, String dt, String dPID, double dQtt, String dN) {
		super("pandacube_onlineshop_history", id);
		setTime(t);
		setSourceType(SourceType.valueOf(st));
		sourcePlayerId = sPID;
		setSourceQuantity(sQtt);
		setSourceName(sN);
		setDestType(DestType.valueOf(dt));
		destPlayerId = dPID;
		setDestQuantity(dQtt);
		setDestName(dN);
	}

	@Override
	protected String[] getValues() {
		return new String[] {
				Long.toString(time),
				sourceType.name(),
				sourcePlayerId,
				Double.toString(sourceQuantity),
				sourceName,
				destType.name(),
				destPlayerId,
				Double.toString(destQuantity),
				destName
				
		};
	}

	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"time",
				"sourceType",
				"sourcePlayerId",
				"sourceQuantity",
				"sourceName",
				"destType",
				"destPlayerId",
				"destQuantity",
				"destName"
		};
	}
	
	
	public long getTime() { return time; }
	public SourceType getSourceType() { return sourceType; }
	public UUID getSourcePlayerId() { return UUID.fromString(sourcePlayerId); }
	public double getSourceQuantity() { return sourceQuantity; }
	public String getSourceName() { return sourceName; }
	public DestType getDestType() { return destType; }
	public UUID getDestPlayerId() { return UUID.fromString(destPlayerId); }
	public double getDestQuantity() { return destQuantity; }
	public String getDestName() { return destName; }
	
	
	
	
	
	

	public void setTime(long t) { time = t; }
	public void setSourceType(SourceType st) {
		if (st == null) throw new IllegalArgumentException("sourceType can't be null");
		sourceType = st;
	}
	public void setSourcePlayerId(UUID pId) {
		if (pId == null) throw new IllegalArgumentException("sourcePlayerId can't be null");
		sourcePlayerId = pId.toString();
	}
	public void setSourceQuantity(double qtt) { sourceQuantity = qtt; }
	public void setSourceName(String name) {
		if (name == null) throw new IllegalArgumentException("sourceName can't be null");
		sourceName = name;
	}
	public void setDestType(DestType st) {
		if (st == null) throw new IllegalArgumentException("destType can't be null");
		destType = st;
	}
	public void setDestPlayerId(UUID pId) {
		if (pId == null) throw new IllegalArgumentException("destPlayerId can't be null");
		destPlayerId = pId.toString();
	}
	public void setDestQuantity(double qtt) { destQuantity = qtt; }
	public void setDestName(String name) {
		if (name == null) throw new IllegalArgumentException("destName can't be null");
		destName = name;
	}


	public static enum SourceType {
		REAL_MONEY, BAMBOU
	}
	
	public static enum DestType {
		BAMBOU, GRADE
	}

}
