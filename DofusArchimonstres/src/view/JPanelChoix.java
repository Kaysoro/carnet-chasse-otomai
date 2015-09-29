package view;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class JPanelChoix extends JPanel{

	private static final long serialVersionUID = 1L;
	private JButton tout, etape, zone;
	private boolean toutIsSelected = false, zoneIsSelected = false, etapeIsSelected = false;
	
	public JPanelChoix(Dimension dimension){
		super();
		setLayout(new GridLayout(1, 3));
		setPreferredSize(dimension);
		tout = new JButton(new ImageIcon(getClass().getResource("/img/Elements/TousOFF.png")));
		tout.setFocusPainted(false);
		etape = new JButton(new ImageIcon(getClass().getResource("/img/Elements/EtapesOFF.png")));
		etape.setFocusPainted(false);
		zone = new JButton(new ImageIcon(getClass().getResource("/img/Elements/ZonesOFF.png")));
		zone.setFocusPainted(false);
		
		add(tout);
		add(etape);
		add(zone);
	}
	
	/**
	 * Permet de mettre les boutons à l'état d'origine
	 */
	public void imageDefault(){
		tout.setIcon(new ImageIcon(getClass().getResource("/img/Elements/TousOFF.png")));
		etape.setIcon(new ImageIcon(getClass().getResource("/img/Elements/EtapesOFF.png")));
		zone.setIcon(new ImageIcon(getClass().getResource("/img/Elements/ZonesOFF.png")));
	}
	
	/**
	 * Permet de mettre les 3 boutons à l'état non sélectionnés ( = FALSE )
	 */
	public void actualiserBooleen(boolean booleen){
		toutIsSelected = booleen;
		zoneIsSelected = booleen;
		etapeIsSelected = booleen;
	}

	public JButton getTout() {
		return tout;
	}

	public JButton getEtape() {
		return etape;
	}

	public JButton getZone() {
		return zone;
	}

	public boolean isToutIsSelected() {
		return toutIsSelected;
	}

	public boolean isZoneIsSelected() {
		return zoneIsSelected;
	}

	public boolean isEtapeIsSelected() {
		return etapeIsSelected;
	}
	
	public void setToutIsSelected(boolean valeur) {
		toutIsSelected = valeur;
	}

	public void setZoneIsSelected(boolean valeur) {
		zoneIsSelected = valeur;
	}

	public void setEtapeIsSelected(boolean valeur) {
		etapeIsSelected = valeur;
	}
}
