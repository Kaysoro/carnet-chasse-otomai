package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Type implements Comparable<Type>{

	private static List<Type> types = null;
	private String nom;

	public Type(String nom){
		super();
		this.nom = nom;
	}

	/**
	 *  Permet de récupérer les données de la table personnage
	 * @return Liste de Personnage avec les données associés
	 */
	public static List<Type> getTypes(){

		if (types == null){
			types = new ArrayList<Type>();
			String nom;

			Connexion connexion = Connexion.getInstance();
			Connection connection = connexion.getConnection();

			try {
				PreparedStatement attributionClasse = connection.prepareStatement("SELECT nom FROM type");
				ResultSet resultSet = attributionClasse.executeQuery();

				while (resultSet.next()) {
					nom = resultSet.getString("nom");

					types.add(new Type(nom));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			Collections.sort(types);

		}

		return types;
	}

	public int compareTo(Type o) {
		return nom.compareTo(o.getNom());
	}

	public String getNom(){
		return nom;
	}

	public static List<Type> getTypes(Monstre monstre) {
		List<Type> types = new ArrayList<Type>();
		String nom;

		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try {
			PreparedStatement attributionClasse = connection.prepareStatement("SELECT nom " +
					"FROM type, monstre_type " +
					"WHERE ((monstre_associe = ?) " +
					"AND (nom = type_associe));");
			attributionClasse.setString(1, monstre.getNom()); // Nom du monstre associé
			ResultSet resultSet = attributionClasse.executeQuery();

			while (resultSet.next()) {
				nom = resultSet.getString("nom");
				types.add(new Type(nom));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Collections.sort(types);
		return types;
	}
	
	@Override
	public String toString(){
		return nom;
	}
	
	@Override
	public boolean equals(Object type){
		return nom.equals(((Type) type).getNom());
	}
}
