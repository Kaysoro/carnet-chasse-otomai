package model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

public abstract class Statistiques {

	protected List<Monstre> monstres;
	protected int nombre;
	protected JButton button;
	
	public Statistiques(){
		monstres = new ArrayList<Monstre>();
	}
	
	public void incrementerNombre(){
		nombre++;
	}
	
	public void decrementerNombre(){
		nombre--;
	}
	
	public int getNombre(){
		return nombre;
	}
	
	public void setButton(JButton button){
		this.button = button;
	}
	
	public JButton getButton(){
		return button;
	}
	
	public int getMax(){
		return monstres.size();
	}
	
	public List<Monstre> getMonstres(){
		return monstres;
	}
	
	protected void ajouterMonstre(Monstre monstre){
		monstres.add(monstre);
	}
}
