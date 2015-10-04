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
	private Zone zone;

	public SousZone(String nom, Zone zone_associe){
		super(nom);
		this.zone = zone_associe;
	}

	@Override
	public void incrementerNombre(){
		nombre++;
		zone.incrementerNombre();
		System.out.println(zone.getNom() + " : " + zone.getNombre() + " / " + zone.getMax());
	}

	@Override
	public void decrementerNombre(){
		nombre--;
		zone.decrementerNombre();
	}

	public static List<List<SousZone>> getAllSousZones(){

		if (sousZones == null){
			sousZones = new ArrayList<List<SousZone>>();
			List<Zone> zones = Zone.getZones();
			for (Zone zone : zones)
				sousZones.add(zone.getSousZones());
		}

		return sousZones;
	}

	public static void initialisation(){

		String nom, zone_associe;
		List<Zone> zones = Zone.getZones();

		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try {
			PreparedStatement attributionClasse = connection.prepareStatement("SELECT souszone.nom, zone_associe " 
					+ "FROM souszone, zone WHERE (zone_associe = zone.nom)");
			ResultSet resultSet = attributionClasse.executeQuery();
			
			while (resultSet.next()) {
				nom = resultSet.getString("nom");
				zone_associe = resultSet.getString("zone_associe");
				
				for(Zone zone : zones)
					if (zone.getNom().equals(zone_associe)){
						zone.ajouterSousZone(new SousZone(nom, zone));
						break;
					}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static List<SousZone> getSousZones(Monstre monstre) {
		List<SousZone> zones = new ArrayList<SousZone>();
		String zone_associe;

		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try {
			PreparedStatement attributionClasse = connection.prepareStatement("SELECT souszone_associe " +
					"FROM souszone, monstre_souszone " +
					"WHERE ((monstre_associe = ?) " +
					"AND (souszone.nom = souszone_associe));");
			attributionClasse.setString(1, monstre.getNom()); // Nom de la zone associé
			ResultSet resultSet = attributionClasse.executeQuery();

			while (resultSet.next()) {
				zone_associe = resultSet.getString("souszone_associe");
				
				boolean trouve = false;
				for(List<SousZone> zonees : getAllSousZones()){
					for(SousZone zone : zonees)
						if (zone.getNom().equals(zone_associe)){
							zones.add(zone);
							trouve = true;
							break;
						}
					if (trouve)
						break;
				}

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Collections.sort(zones);
		return zones;
	}

	public Zone getZoneAssocie(){
		return zone;
	}
}
