package fr.pandacube.java.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;

public abstract class SQLElement {
	
	DBConnection db = ORM.connection;
	
	
	private boolean saved = false;

	protected final String tableName;
	
	// champ relatif aux données
	private int id = 0;
	
	

	public SQLElement(String name) {
		tableName = name;
		saved = false;
	}
	protected SQLElement(String name, int id) {
		tableName = name;
		this.id = id;
		saved = true;
	}
	
	
	
	
	
	public void save() {
		
		try {
			Connection conn;
			conn = db.getConnection();

			String[] fields = getFieldsName(), values = getValues();
			
			
			
			if (saved)
			{	// mettre à jour les valeurs dans la base
				String sql = "";
				for (int i=0; i<fields.length && i<values.length; i++)
				{
					sql += fields[i]+" = ? ,";
				}
				
				if (sql.length() > 0)
					sql = sql.substring(0, sql.length()-1);
				
				PreparedStatement st = conn.prepareStatement("UPDATE "+tableName+" SET "+sql+" WHERE id="+id);
				try {
					for (int i=0; i<fields.length && i<values.length; i++)
					{
						st.setString(i+1, values[i]);
					}
					
					st.executeUpdate();
				} finally {
					st.close();
				}
			}
			else
			{	// ajouter dans la base
				String concat_vals = "";
				String concat_fields = StringUtils.join(fields, ',');
				for (int i=0; i<fields.length && i<values.length; i++)
				{
					if (i!=0) concat_vals += ",";
					concat_vals += " ? ";
				}
				
				
				PreparedStatement st = conn.prepareStatement("INSERT INTO "+tableName+"  ("+concat_fields+") VALUES ("+concat_vals+")", Statement.RETURN_GENERATED_KEYS);
				try {
					for (int i=0; i<fields.length && i<values.length; i++)
					{
						st.setString(i+1, values[i]);
					}
					
					st.executeUpdate();
					
					ResultSet rs = st.getGeneratedKeys();
					try {
		                if(rs.next())
		                {
		                    id = rs.getInt(1);
		                }
		                
						saved = true;
					} finally {
						rs.close();
					}
				} finally {
					st.close();
				}
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public void delete() {
		
		try {
			if (saved)
			{	//  supprimer la ligne de la base
				PreparedStatement st = db.getConnection().prepareStatement("DELETE FROM "+tableName+" WHERE id="+id);
				try {
					st.executeUpdate();
					saved = false;
				} finally {
					st.close();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	
	public int getId() {
		if (!saved)
			throw new IllegalStateException("Ne peut pas fournir l'ID d'un élément non sauvegardé");
		return id;
	}
	
	
	/**
	 * Récupère la liste des valeurs des champs de la table correspondante, excepté
	 * le champ <code>id</code>
	 * @return les valeurs des champs sous la forme de chaine de caractères
	 */
	protected abstract String[] getValues();
	
	

	/**
	 * Récupère la liste des noms des champs de la table correspondante, excepté
	 * le champ <code>id</code>
	 * @return les noms des champs sous la forme de chaine de caractères
	 */
	protected abstract String[] getFieldsName();
}
