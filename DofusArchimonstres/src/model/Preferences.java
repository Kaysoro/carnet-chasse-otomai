package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Preferences {

	private static Boolean isDetailed = null;
	private static Boolean isChanged = null;
	private static Boolean isRemoved = null;
	private static Etape etapeActuelle = null;

	public static boolean isDetailed(){

		if (isDetailed == null){
			Connexion connexion = Connexion.getInstance();
			Connection connection = connexion.getConnection();

			try {
				PreparedStatement attributionClasse = connection.prepareStatement("SELECT isDetailed FROM preferences");
				ResultSet resultSet = attributionClasse.executeQuery();

				resultSet.next();
				isDetailed = resultSet.getBoolean("isDetailed");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return isDetailed;
	}
	
	public static boolean isRemoved(){

		if (isRemoved == null){
			Connexion connexion = Connexion.getInstance();
			Connection connection = connexion.getConnection();

			try {
				PreparedStatement attributionClasse = connection.prepareStatement("SELECT isRemoved FROM preferences");
				ResultSet resultSet = attributionClasse.executeQuery();

				resultSet.next();
				isRemoved = resultSet.getBoolean("isRemoved");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return isRemoved;
	}
	
	public static boolean isChanged(){

		if (isChanged == null){
			Connexion connexion = Connexion.getInstance();
			Connection connection = connexion.getConnection();

			try {
				PreparedStatement attributionClasse = connection.prepareStatement("SELECT isChanged FROM preferences");
				ResultSet resultSet = attributionClasse.executeQuery();

				resultSet.next();
				isChanged = resultSet.getBoolean("isChanged");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return isChanged;
	}

	public static Etape getEtapeActuelle(){

		if (etapeActuelle == null){
			Connexion connexion = Connexion.getInstance();
			Connection connection = connexion.getConnection();

			try {
				PreparedStatement attributionClasse = connection.prepareStatement("SELECT etape_actuelle FROM preferences");
				ResultSet resultSet = attributionClasse.executeQuery();

				resultSet.next();
				String nom = resultSet.getString("etape_actuelle");

				for(Etape etape : Etape.getEtapes())
					if(etape.getNom().equals(nom)){
						etapeActuelle = etape;
						break;
					}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return etapeActuelle;
	}

	public static void setEtapeActuelle(Etape etape){
		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try{
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE preferences SET etape_actuelle = ?");

			preparedStatement.setString(1, etape.getNom());
			preparedStatement.executeUpdate();

			etapeActuelle = etape;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void setIsRemoved(boolean isRemoved){
		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try{
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE preferences SET isRemoved = ?");

			preparedStatement.setBoolean(1, isRemoved);
			preparedStatement.executeUpdate();

			Preferences.isRemoved = isRemoved;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void setIsDetailed(boolean isDetailed){
		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try{
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE preferences SET isDetailed = ?");

			preparedStatement.setBoolean(1, isDetailed);
			preparedStatement.executeUpdate();

			Preferences.isDetailed = isDetailed;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void setIsChanged(boolean isChanged){
		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try{
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE preferences SET isChanged = ?");

			preparedStatement.setBoolean(1, isChanged);
			preparedStatement.executeUpdate();

			Preferences.isChanged = isChanged;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
