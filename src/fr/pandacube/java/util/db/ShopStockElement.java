package fr.pandacube.java.util.db;

public class ShopStockElement extends SQLElement {
	
	private String material;
	private short damage = 0;
	private double quantity;
	private String server;
	

	public ShopStockElement(String m, short d, double q, String s) {
		super("pandacube_shop_stock");
		setMaterial(m);
		setDamage(d);
		setQuantity(q);
		setServer(s);
	}
	
	protected ShopStockElement(int id, String m, short d, double q, String s) {
		super("pandacube_shop_stock", id);
		setMaterial(m);
		setDamage(d);
		setQuantity(q);
		setServer(s);
	}

	@Override
	protected String[] getValues() {
		return new String[] {
				material,
				Short.toString(damage),
				Double.toString(quantity),
				server
		};
	}

	@Override
	protected String[] getFieldsName() {
		return new String[] {
				"material",
				"damage",
				"quantity",
				"server"
		};
	}

	public String getMaterial() { return material; }

	public void setMaterial(String m) {
		if (m == null) throw new IllegalArgumentException("Material can't be null");
		material = m;
	}

	public short getDamage() { return damage; }

	public void setDamage(short d) {
		damage = d;
	}

	public double getQuantity() { return quantity; }

	public void setQuantity(double q) {
		if (q < 0) q = 0;
		quantity = q;
	}

	public String getServer() { return server; }

	public void setServer(String s) {
		if (s == null) throw new IllegalArgumentException("Server can't be null");
		server = s;
	}

}
