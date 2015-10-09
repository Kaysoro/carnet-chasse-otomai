package model;

import java.util.List;

public class Joueur {

	private String nom;
	private List<Monstre> monstres;
	private Etape etapeActuelle;

	public Joueur(String nom, Etape etapeActuelle, List<Monstre> monstres) {
		super();
		this.nom = nom;
		this.monstres = monstres;
		this.etapeActuelle = etapeActuelle;
	}

	public String getNom() {
		return nom;
	}

	public List<Monstre> getMonstres() {
		return monstres;
	}

	public Etape getEtapeActuelle() {
		return etapeActuelle;
	}
	
	
	
}
