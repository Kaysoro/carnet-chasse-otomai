package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Zone implements Comparable<Zone>{

	private String nom;
	private static List<Zone> zones = null;

	public Zone(String nom){
		super();
		this.nom = nom;
	}

	/**
	 *  Permet de récupérer les données de la table personnage
	 * @return Liste de Personnage avec les données associés
	 */
	public static List<Zone> getZones(){

		if (zones == null){
			zones = new ArrayList<Zone>();
			String nom;

			Connexion connexion = Connexion.getInstance();
			Connection connection = connexion.getConnection();

			try {
				PreparedStatement attributionClasse = connection.prepareStatement("SELECT nom FROM zone");
				ResultSet resultSet = attributionClasse.executeQuery();

				while (resultSet.next()) {
					nom = resultSet.getString("nom");

					zones.add(new Zone(nom));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			Collections.sort(zones);
		}

		return zones;
	}

	public int compareTo(Zone o) {
		return nom.compareTo(o.getNom());
	}

	public String getNom(){
		return nom;
	}

}
