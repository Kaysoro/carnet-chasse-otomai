package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SousZone extends Zone{

	private static List<List<SousZone>> sousZones = null;
	private String zone_associe;

	public SousZone(String nom, String zone_associe){
		super(nom);
		this.zone_associe = zone_associe;
	}

	/**
	 *  Permet de récupérer les données de la table personnage
	 * @return Liste de Personnage avec les données associés
	 */
	public static List<SousZone> getSousZones(Zone zone){

		List<SousZone> zones = new ArrayList<SousZone>();
		String nom, zone_associe;

		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try {
			PreparedStatement attributionClasse = connection.prepareStatement("SELECT nom, zone_associe FROM souszone WHERE (zone_associe = ?)");
			attributionClasse.setString(1, zone.getNom()); // Nom de la zone associé
			ResultSet resultSet = attributionClasse.executeQuery();

			while (resultSet.next()) {
				nom = resultSet.getString("nom");
				zone_associe = resultSet.getString("zone_associe");

				zones.add(new SousZone(nom, zone_associe));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Collections.sort(zones);
		return zones;
	}

	/**
	 *  Permet de récupérer les données de la table personnage
	 * @return Liste de Personnage avec les données associés
	 */
	public static List<List<SousZone>> getAllSousZones(){

		if (sousZones == null){
			sousZones = new ArrayList<List<SousZone>>();
			List<Zone> zones = Zone.getZones();

			for (Zone zone : zones)
				sousZones.add(SousZone.getSousZones(zone));
		}

		return sousZones;
	}

	public String getZoneAssocie(){
		return zone_associe;
	}

	public static List<SousZone> getSousZones(Monstre monstre) {
		List<SousZone> zones = new ArrayList<SousZone>();
		String nom, zone_associe;

		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try {
			PreparedStatement attributionClasse = connection.prepareStatement("SELECT nom, souszone_associe " +
					"FROM souszone, monstre_souszone " +
					"WHERE ((monstre_associe = ?) " +
					"AND (nom = souszone_associe));");
			attributionClasse.setString(1, monstre.getNom()); // Nom de la zone associé
			ResultSet resultSet = attributionClasse.executeQuery();

			while (resultSet.next()) {
				nom = resultSet.getString("nom");
				zone_associe = resultSet.getString("souszone_associe");

				zones.add(new SousZone(nom, zone_associe));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Collections.sort(zones);
		return zones;
	}
}
