package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Etape extends Statistiques implements Comparable<Etape>{

	private String nom;
	private static List<Etape> etapes = null;

	private Etape(String nom){
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
		}
		return etapes;
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
	
	@Override
	public String toString(){
		return nom;
	}

	@Override
	protected void ajouterMonstre(Monstre monstre) {
		monstres.add(monstre);
	}

	public static List<Etape> getEtapes(Monstre monstre) {
	    List<Etape> etapes = new ArrayList<Etape>();

	    Connexion connexion = Connexion.getInstance();
	    Connection connection = connexion.getConnection();
	    try
	    {
	      PreparedStatement attributionClasse = connection.prepareStatement("SELECT nom FROM etape, monstre_etape WHERE ((monstre_associe = ?) AND (nom = etape_associe));");

	      attributionClasse.setString(1, monstre.getNom());
	      ResultSet resultSet = attributionClasse.executeQuery();

	      while (resultSet.next()) {
	        String nom = resultSet.getString("nom");
	        for(Etape etape : getEtapes())
	        	if (etape.getNom().equals(nom)){
	        		etapes.add(etape);
	        		break;
	        	}
	      }
	    } catch (SQLException e) {
	      e.printStackTrace();
	    }

	    Collections.sort(etapes);
	    return etapes;
	  }
}
