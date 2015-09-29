package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Etape implements Comparable<Etape>{

	private String nom;
	private static List<Etape> etapes = null;
	private static int[] nbMonstres = null;

	public Etape(String nom){
		super();
		this.nom = nom;
	}

	/**
	 *  Permet de récupérer les données de la table personnage
	 * @return Liste de Personnage avec les données associés
	 */
	public static List<Etape> getEtapes(){

		if (etapes == null){
			etapes = new ArrayList<Etape>();
			String nom;

			Connexion connexion = Connexion.getInstance();
			Connection connection = connexion.getConnection();

			try {
				PreparedStatement attributionClasse = connection.prepareStatement("SELECT nom FROM etape");
				ResultSet resultSet = attributionClasse.executeQuery();

				while (resultSet.next()) {
					nom = resultSet.getString("nom");

					etapes.add(new Etape(nom));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			Collections.sort(etapes);
			
			nbMonstres = new int[etapes.size()];
			
			List<Monstre> monstres = Monstre.getMonstres();
			for(Monstre monstre : monstres)
				nbMonstres[Integer.parseInt(monstre.getEtapeAssocie().get(0).nom.substring(6)) - 1]++;
		}
		return etapes;
	}
	
	public static int getNbMonstresEtape(int etape){
		getEtapes();
		return nbMonstres[etape - 1];
	}

	public int compareTo(Etape o) {
		String nomDecoupe = nom.substring(0, 6);
		String oNomDecoupe = o.getNom().substring(0, 6);

		if (nomDecoupe.equals(oNomDecoupe))
			return 0;
		else
			return nom.compareTo(o.getNom());
	}

	public String getNom(){
		return nom;
	}

	public static List<Etape> getEtapes(Monstre monstre) {
		List<Etape> etapes = new ArrayList<Etape>();
		String nom;

		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try {
			PreparedStatement attributionClasse = connection.prepareStatement("SELECT nom " +
					"FROM etape, monstre_etape " +
					"WHERE ((monstre_associe = ?) " +
					"AND (nom = etape_associe));");
			attributionClasse.setString(1, monstre.getNom()); // Nom du monstre associé
			ResultSet resultSet = attributionClasse.executeQuery();

			while (resultSet.next()) {
				nom = resultSet.getString("nom");
				etapes.add(new Etape(nom));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Collections.sort(etapes);
		return etapes;
	}
	
	@Override
	public String toString(){
		return nom;
	}
}
