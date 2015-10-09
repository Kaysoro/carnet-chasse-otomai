package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

public class Monstre implements Comparable<Monstre>{

	private static List<Monstre> monstres = null;

	private String nom;
	private String niveau;
	private ImageIcon image;
	private String hp;
	private String pa;
	private String pm;
	private int nombrePossede;
	private String resNeutre;
	private String resTerre;
	private String resFeu;
	private String resEau;
	private String resAir;
	private Monstre monstreAssocie;
	private List<Type> typeAssocie;
	private List<SousZone> zoneAssocie;
	private List<Etape> etapeAssocie;

	private Monstre(String nom, String niveau, ImageIcon image, String hp, String pa, String pm, int nombrePossede,
			String resNeutre, String resTerre, String resFeu, String resEau, String resAir){
		super();
		this.nom = nom;
		this.image = image;
		this.niveau = niveau;
		this.hp = hp;
		this.pa = pa;
		this.pm = pm;
		this.nombrePossede = nombrePossede;
		this.resNeutre = resNeutre;
		this.resTerre = resTerre;
		this.resFeu = resFeu;
		this.resEau = resEau;
		this.resAir = resAir;
	}

	private Monstre(Monstre monstre, Monstre monstreAssocie, List<Type> typeAssocie, 
			List<SousZone> zoneAssocie, List<Etape> etapeAssocie){
		super();
		this.nom = monstre.getNom();
		this.niveau = monstre.getNiveau();
		this.hp = monstre.getHp();
		this.pa = monstre.getPa();
		this.pm = monstre.getPm();
		this.nombrePossede = monstre.getNombrePossede();
		this.resNeutre = monstre.getResNeutre();
		this.resTerre = monstre.getResTerre();
		this.resFeu = monstre.getResFeu();
		this.resEau = monstre.getResEau();
		this.resAir = monstre.getResAir();
		this.monstreAssocie = monstreAssocie;
		this.image = monstre.getImage();
		this.typeAssocie = typeAssocie;
		this.zoneAssocie = zoneAssocie;
		this.etapeAssocie = etapeAssocie;
	}

	public static Monstre getMonstreJoueur(String nom, int nombrePossede){
		return new Monstre(nom, "", null, "", "", "", nombrePossede, "", "", "", "", "");
	}

	public static List<Monstre> getMonstres(){

		if (monstres == null){
			List<Monstre> monstresTemporaire = new ArrayList<Monstre>();
			monstres = new ArrayList<Monstre>();
			String nom, niveau, hp, pa, pm;
			ImageIcon image;
			int nombrePossede;
			String resNeutre, resTerre, resFeu, resEau, resAir;
			Map<String, String> monstresAssocies = new HashMap<String, String>();

			Connexion connexion = Connexion.getInstance();
			Connection connection = connexion.getConnection();

			try {
				PreparedStatement recupereMonstre = connection.prepareStatement("SELECT * FROM monstre;");
				ResultSet resultSet = recupereMonstre.executeQuery();

				while (resultSet.next()) {
					nom = resultSet.getString("nom");
					niveau = resultSet.getString("niveau");

					try {
						image = new ImageIcon(Monstre.class.getResource("/img/Monstres/" + resultSet.getString("image")));
					} catch(NullPointerException e){
						image = new ImageIcon(Monstre.class.getResource("/img/Monstres/Unknown.png"));
					}

					hp = resultSet.getString("hp");
					pa = resultSet.getString("pa");
					pm = resultSet.getString("pm");
					nombrePossede = resultSet.getInt("nombre_possede");
					resNeutre  = resultSet.getString("res_neutre");
					resTerre  = resultSet.getString("res_terre");
					resFeu  = resultSet.getString("res_feu");
					resEau  = resultSet.getString("res_eau");
					resAir  = resultSet.getString("res_air");
					monstresAssocies.put(nom, resultSet.getString("monstre_associe"));

					if (! nom.equals("Unknown"))
						monstresTemporaire.add(new Monstre(nom, niveau, image, hp, pa, pm, nombrePossede, 
								resNeutre, resTerre, resFeu, resEau, resAir));
				}

				for(int i = 0; i < monstresTemporaire.size(); i++){
					Monstre monstreAssocie = null;
					List<SousZone> zones = SousZone.getSousZones(monstresTemporaire.get(i));
					List<Etape> etapes = Etape.getEtapes(monstresTemporaire.get(i));
					List<Type> types = Type.getTypes(monstresTemporaire.get(i));

					for(int j = 0; j < monstresTemporaire.size(); j++)
						if (monstresTemporaire.get(j).getNom().equals(monstresAssocies.get(monstresTemporaire.get(i).getNom()))){
							monstreAssocie = monstresTemporaire.get(j);
							break;
						}
					Monstre monstre = new Monstre(monstresTemporaire.get(i), monstreAssocie, types, zones, etapes);
					monstres.add(monstre);

					// On l'associe à ces données
					Etape etape = monstre.getEtapeAssocie().get(0);
					etape.ajouterMonstre(monstre);

					if (monstre.getNombrePossede() > 0)
						etape.incrementerNombre();

					for(Zone zone : zones){

						zone.ajouterMonstre(monstre);
						if (monstre.getNombrePossede() > 0)
							zone.incrementerNombre();
					}
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

			Collections.sort(monstres);
		}

		return monstres;
	}

	public void incrementerNombre(){
		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		nombrePossede++;

		try{
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE monstre SET "
					+ "nombre_possede = ? WHERE nom = ?");

			preparedStatement.setInt(1, nombrePossede); // Nom
			preparedStatement.setString(2, getNom()); // Where NOM =
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void decrementerNombre(){
		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		if (nombrePossede > 0){
			nombrePossede--;

			try{
				PreparedStatement preparedStatement = connection.prepareStatement("UPDATE monstre SET "
						+ "nombre_possede = ? WHERE nom = ?");

				preparedStatement.setInt(1, nombrePossede); // Nom
				preparedStatement.setString(2, getNom()); // Where NOM =
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void reinitialiserMonstres(){
		Connexion connexion = Connexion.getInstance();
		Connection connection = connexion.getConnection();

		try{
			PreparedStatement preparedStatement = connection.prepareStatement("UPDATE monstre SET "
					+ "nombre_possede = 0");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		List<Monstre> monstres = getMonstres();
		for(Monstre monstre : monstres)
			monstre.reinitialiserNombre();
	}

	public int compareTo(Monstre o) {
		return nom.compareTo(o.getNom());
	}

	private void reinitialiserNombre(){
		if (nombrePossede > 0){
			getEtapeAssocie().get(0).decrementerNombre();

			for(SousZone zone : getZoneAssocie())
				zone.decrementerNombre();
		}
		nombrePossede = 0;
	}

	public static String exporterMonstres(){
		StringBuilder st = new StringBuilder(Preferences.getNom() + ";" + Preferences.getEtapeActuelle().getNom().substring(6) + ";\n");
		for(Monstre monstre : getMonstres()){
			st.append(monstre.getNom() + ";");
			st.append(monstre.getNombrePossede() + ";\n");
		}
		return st.toString();
	}

	public String getNom() {
		return nom;
	}

	public String getNiveau() {
		return niveau;
	}

	public ImageIcon getImage() {
		return image;
	}

	public String getHp() {
		return hp;
	}

	public String getPa() {
		return pa;
	}

	public String getPm() {
		return pm;
	}

	public int getNombrePossede() {
		return nombrePossede;
	}

	public String getResNeutre() {
		return resNeutre;
	}

	public String getResTerre() {
		return resTerre;
	}

	public String getResFeu() {
		return resFeu;
	}

	public String getResEau() {
		return resEau;
	}

	public String getResAir() {
		return resAir;
	}

	public Monstre getMonstreAssocie() {
		return monstreAssocie;
	}

	public List<Type> getTypeAssocie() {
		return typeAssocie;
	}

	public List<Etape> getEtapeAssocie() {
		return etapeAssocie;
	}

	public List<SousZone> getZoneAssocie() {
		return zoneAssocie;
	}

	@Override
	public String toString(){
		return nom + " // " + niveau 
				+ "\n" + hp + " // " + pa  + " // " + pm
				+ "\nRes%N : " + resNeutre + " // Res%T : " +
				resTerre + " // Res%F : " + resFeu + " // Res%E : "
				+ resEau + " // Res%A : " + resAir;
	}

	public boolean equals(Monstre monstre){
		return getNom().equals(monstre.getNom());
	}
}