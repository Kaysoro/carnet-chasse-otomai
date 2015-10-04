package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Monstre;
import control.Gestionnaire;

public class JPanelMonstre extends JPanel {

	public static final int TAILLE_MINIMALE = 117;
	public static final int TAILLE_MAXIMALE = 231;
	private static final long serialVersionUID = 1L;
	private Monstre monstre;
	private JButton plus, moins, monstreAssocie;
	private JLabel nom, nombrePossede, etape, zone, niveau, 
	hp, pa, pm, resNeutre, resTerre, resFeu, resEau, resAir, image, type;
	private JPanel panelBasic, eastBasic, eastNorthBasic, eastSouthBasic, middleNorthBasic, middleSouthBasic, panelPlusBasic, panelMoinsBasic;
	private JPanel panelDetailed, westDetailed, westCarac, westWCarac, westECarac, eastCarac, eastWCarac, eastECarac;
	private Color beige = new Color(243, 227, 194), vert = new Color(34, 177, 76);

	public JPanelMonstre(final Gestionnaire gestionnaire, final Monstre monstre, boolean isDetailed){
		super();
		this.monstre = monstre;
		nom = new JLabel(monstre.getNom());
		nom.setFont(new Font("Sherif", Font.PLAIN, 16));
		nom.setHorizontalAlignment(JLabel.CENTER);
		image = new JLabel(monstre.getImage());
		image.setPreferredSize(new Dimension(150, 80));
		nombrePossede = new JLabel(monstre.getNombrePossede() + "");
		nombrePossede.setHorizontalAlignment(JLabel.CENTER);
		nombrePossede.setFont(new Font("Sherif", Font.PLAIN, 72));
		niveau = new JLabel(monstre.getNiveau());
		niveau.setHorizontalAlignment(JLabel.CENTER);
		hp = new JLabel(monstre.getHp());
		pa = new JLabel(monstre.getPa());
		pm = new JLabel(monstre.getPm());
		resNeutre = new JLabel(monstre.getResNeutre());
		resTerre = new JLabel(monstre.getResTerre());
		resFeu = new JLabel(monstre.getResFeu());
		resEau = new JLabel(monstre.getResEau());
		resAir = new JLabel(monstre.getResAir());

		try {
			etape = new JLabel(monstre.getEtapeAssocie().get(0).getNom());
			etape.setHorizontalAlignment(JLabel.CENTER);
		} catch(IndexOutOfBoundsException e){
			etape = new JLabel("Étape Inconnu");
			etape.setHorizontalAlignment(JLabel.CENTER);
		}

		try {
			type = new JLabel(monstre.getTypeAssocie().get(0).getNom());
			type.setHorizontalAlignment(JLabel.CENTER);
		} catch(IndexOutOfBoundsException e){
			type = new JLabel("Type Inconnu");
			type.setHorizontalAlignment(JLabel.CENTER);
		}

		try {
			if (type.getText().equals("Monstre"))
				monstreAssocie = new JButton(new ImageIcon(getClass().getResource("/img/Elements/Archi-Monstre.png")));
			if (type.getText().equals("Archi-Monstre"))
				monstreAssocie = new JButton(new ImageIcon(getClass().getResource("/img/Elements/Monstre.png")));

			monstreAssocie.setToolTipText(monstre.getMonstreAssocie().getNom());
			monstreAssocie.setPreferredSize(new Dimension(40, 40));
			monstreAssocie.setFocusPainted(false);

		}catch(NullPointerException e){
			monstreAssocie = null;
		}

		if (monstre.getZoneAssocie().size() != 0){
			String textZone;
			if (monstre.getZoneAssocie().size() == 1)
				textZone = "<html><p><u>Zone de prédilection :</u> ";
			else
				textZone = "<html><p><u>Zones de prédilection :</u> ";

			for(int i = 0; i < monstre.getZoneAssocie().size(); i++)
				if (i != (monstre.getZoneAssocie().size() - 1))
					textZone+= "<i>" + monstre.getZoneAssocie().get(i).getNom() +"</i>, ";
				else
					textZone+= "<i>" + monstre.getZoneAssocie().get(i).getNom() +"</i>.</p></html>";
			zone = new JLabel(textZone);
		}
		else
			zone = new JLabel("<html><p><u>Aucune zone de prédilection.</u></p></html>");

		zone.setPreferredSize(new Dimension(400, 80));
		zone.setVerticalAlignment(JLabel.TOP);

		plus = new JButton(new ImageIcon(getClass().getResource("/img/Elements/plus.png")));
		plus.setPreferredSize(new Dimension(38, 37));
		plus.setFocusPainted(false);
		plus.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				monstre.incrementerNombre();
				nombrePossede.setText(monstre.getNombrePossede() + "");
				changerCouleur();
				if (monstre.getNombrePossede() == 1)
					gestionnaire.incrementerStats(monstre.getEtapeAssocie().get(0), monstre.getZoneAssocie());
				gestionnaire.majStats();
			}
		});

		moins = new JButton(new ImageIcon(getClass().getResource("/img/Elements/moins.png")));
		moins.setPreferredSize(new Dimension(38, 37));
		moins.setFocusPainted(false);
		moins.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				int OldNombrePossede = monstre.getNombrePossede();
				monstre.decrementerNombre();
				nombrePossede.setText(monstre.getNombrePossede() + "");
				changerCouleur();
				if (monstre.getNombrePossede() == 0 && monstre.getNombrePossede() != OldNombrePossede)
					gestionnaire.decrementerStats(monstre.getEtapeAssocie().get(0), monstre.getZoneAssocie());
				gestionnaire.majStats();
			}
		});

		// J'savais pas où mettre ces deux lignes du coup je les ai mis là
		setBorder(BorderFactory.createRaisedBevelBorder());
		setLayout(new BorderLayout());

		//Panels principaux
		panelBasic = new JPanel(new BorderLayout());
		panelDetailed = new JPanel(new BorderLayout());
		panelDetailed.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		// On s'occupe de fourrer comme une dinde de noël le panel classique
		// West
		panelBasic.add(image, BorderLayout.WEST);

		// Middle
		panelMoinsBasic = new JPanel(new GridBagLayout());
		panelMoinsBasic.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 0));
		panelMoinsBasic.add(moins);
		panelPlusBasic = new JPanel(new GridBagLayout());
		panelPlusBasic.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 80));
		panelPlusBasic.add(plus);

		middleNorthBasic = new JPanel(new BorderLayout());
		middleNorthBasic.add(nom, BorderLayout.NORTH);
		middleSouthBasic = new JPanel(new BorderLayout());
		middleSouthBasic.add(panelMoinsBasic, BorderLayout.WEST);
		middleSouthBasic.add(nombrePossede, BorderLayout.CENTER);
		middleSouthBasic.add(panelPlusBasic, BorderLayout.EAST);
		middleNorthBasic.add(middleSouthBasic, BorderLayout.SOUTH);
		panelBasic.add(middleNorthBasic, BorderLayout.CENTER);

		// East
		eastBasic = new JPanel(new BorderLayout());
		eastBasic.setPreferredSize(new Dimension(200, 80));
		eastNorthBasic = new JPanel(new BorderLayout());
		eastNorthBasic.setPreferredSize(new Dimension(200, 50));
		eastNorthBasic.add(niveau, BorderLayout.NORTH);
		eastNorthBasic.add(etape, BorderLayout.CENTER);
		eastNorthBasic.add(type, BorderLayout.SOUTH);
		eastBasic.add(eastNorthBasic, BorderLayout.NORTH);
		eastSouthBasic = new JPanel(new GridBagLayout());
		eastSouthBasic.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		if (monstreAssocie != null){
			eastSouthBasic.add(monstreAssocie);
			eastBasic.add(eastSouthBasic, BorderLayout.SOUTH);
		}

		panelBasic.add(eastBasic, BorderLayout.EAST);

		// Panel res% + pv + le reste
		westDetailed = new JPanel(new BorderLayout());
		westDetailed.setPreferredSize(new Dimension(270, 80));
		westCarac = new JPanel(new BorderLayout());
		westWCarac = new JPanel(new GridLayout(5, 1));
		westWCarac.setPreferredSize(new Dimension(40, 80));
		westECarac = new JPanel(new GridLayout(5, 1));
		westECarac.setPreferredSize(new Dimension(90, 80));

		westWCarac.add(new JLabel(new ImageIcon(getClass().getResource("/img/Elements/PV.png"))));
		westECarac.add(hp);
		westWCarac.add(new JLabel(new ImageIcon(getClass().getResource("/img/Elements/PA.png"))));
		westECarac.add(pa);
		westWCarac.add(new JLabel(new ImageIcon(getClass().getResource("/img/Elements/PM.png"))));
		westECarac.add(pm);
		westCarac.add(westWCarac, BorderLayout.WEST);
		westCarac.add(westECarac, BorderLayout.EAST);
		westDetailed.add(westCarac, BorderLayout.WEST);

		eastCarac = new JPanel(new BorderLayout());
		eastWCarac = new JPanel(new GridLayout(5, 1));
		eastWCarac.setPreferredSize(new Dimension(40, 80));
		eastECarac = new JPanel(new GridLayout(5, 1));
		eastECarac.setPreferredSize(new Dimension(90, 80));
		eastWCarac.add(new JLabel(new ImageIcon(getClass().getResource("/img/Elements/Neutre.png"))));
		eastECarac.add(resNeutre);
		eastWCarac.add(new JLabel(new ImageIcon(getClass().getResource("/img/Elements/Terre.png"))));
		eastECarac.add(resTerre);
		eastWCarac.add(new JLabel(new ImageIcon(getClass().getResource("/img/Elements/Feu.png"))));
		eastECarac.add(resFeu);
		eastWCarac.add(new JLabel(new ImageIcon(getClass().getResource("/img/Elements/Eau.png"))));
		eastECarac.add(resEau);
		eastWCarac.add(new JLabel(new ImageIcon(getClass().getResource("/img/Elements/Air.png"))));
		eastECarac.add(resAir);
		eastCarac.add(eastWCarac, BorderLayout.WEST);
		eastCarac.add(eastECarac, BorderLayout.EAST);
		westDetailed.add(eastCarac, BorderLayout.EAST);
		panelDetailed.add(westDetailed, BorderLayout.WEST);
		panelDetailed.add(zone, BorderLayout.EAST);

		// Derniers préparatifs avant le spectacle
		add(panelBasic, BorderLayout.NORTH);

		if (isDetailed){
			JLabel separation = new JLabel("___________________________________________________" +
					"__________________________________________");
			separation.setBorder(BorderFactory.createEmptyBorder(-5, 0, 15, 0));
			separation.setHorizontalAlignment(JLabel.CENTER);
			separation.setForeground(Color.GRAY);
			add(separation, BorderLayout.CENTER);
			add(panelDetailed, BorderLayout.SOUTH);
		}

		if (monstre.getNombrePossede() > 0){
			this.setBackground(vert);
			panelBasic.setBackground(vert);
			eastBasic.setBackground(vert);
			eastNorthBasic.setBackground(vert);
			middleNorthBasic.setBackground(vert);
			middleSouthBasic.setBackground(vert);
			panelMoinsBasic.setBackground(vert);
			moins.setBackground(vert);
			plus.setBackground(vert);
			if (monstreAssocie != null)
				monstreAssocie.setBackground(vert);
			panelPlusBasic.setBackground(vert);
			eastSouthBasic.setBackground(vert);

			panelDetailed.setBackground(vert);
			westDetailed.setBackground(vert);
			westCarac.setBackground(vert);
			westWCarac.setBackground(vert);
			westECarac.setBackground(vert);
			eastCarac.setBackground(vert);
			eastWCarac.setBackground(vert);
			eastECarac.setBackground(vert);
		}
	}

	/**
	 * Fonction à appeler systématiquement lorsque le nombre possédé évolue
	 */
	public void changerCouleur(){
		if (monstre.getNombrePossede() == 1){
			this.setBackground(vert);
			panelBasic.setBackground(vert);
			eastBasic.setBackground(vert);
			eastNorthBasic.setBackground(vert);
			middleNorthBasic.setBackground(vert);
			middleSouthBasic.setBackground(vert);
			panelMoinsBasic.setBackground(vert);
			panelPlusBasic.setBackground(vert);
			moins.setBackground(vert);
			plus.setBackground(vert);
			if (monstreAssocie != null)
				monstreAssocie.setBackground(vert);
			eastSouthBasic.setBackground(vert);

			panelDetailed.setBackground(vert);
			westDetailed.setBackground(vert);
			westCarac.setBackground(vert);
			westWCarac.setBackground(vert);
			westECarac.setBackground(vert);
			eastCarac.setBackground(vert);
			eastWCarac.setBackground(vert);
			eastECarac.setBackground(vert);
		}
		if (monstre.getNombrePossede() == 0){
			this.setBackground(beige);
			panelBasic.setBackground(beige);
			eastBasic.setBackground(beige);
			eastNorthBasic.setBackground(beige);
			middleNorthBasic.setBackground(beige);
			middleSouthBasic.setBackground(beige);
			panelMoinsBasic.setBackground(beige);
			panelPlusBasic.setBackground(beige);
			moins.setBackground(beige);
			plus.setBackground(beige);
			if (monstreAssocie != null)
				monstreAssocie.setBackground(beige);
			eastSouthBasic.setBackground(beige);

			panelDetailed.setBackground(beige);
			westDetailed.setBackground(beige);
			westCarac.setBackground(beige);
			westWCarac.setBackground(beige);
			westECarac.setBackground(beige);
			eastCarac.setBackground(beige);
			eastWCarac.setBackground(beige);
			eastECarac.setBackground(beige);
		}
	}

	public JButton getMonstreAssocie(){
		return monstreAssocie;
	}

	public JButton getPlus(){
		return plus;
	}

	public JButton getMoins(){
		return moins;
	}
}
