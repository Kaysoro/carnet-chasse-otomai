package control;

/**
 * @author Steven PESCHETEAU
 * 
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import model.Connexion;
import model.Etape;
import model.Monstre;
import model.Preferences;
import model.SousZone;
import model.Zone;
import view.ImageTextuelle;
import view.Interface;
import view.JButtonZone;
import view.JPanelMonstre;
import view.JPanelTree;
import view.SplashScreen;

public class Gestionnaire implements ActionListener, KeyListener, WindowListener {

	private final static String NOM_BDD = "mobdb.sqlite";
	private SplashScreen splash;
	private Interface graphic;
	private Connexion connexion;
	private List<JButton> list;
	private List<List<JButton>> sousList;
	private JPanelTree panelTree;
	private int ongletOuvert = -1;
	private List<Monstre> monstres, monstresActuels;
	private ActionListener monstreAssocieAction;
	private JButton oldSource;
	private int oldScrollBarValue;

	public Gestionnaire(){
		super();

		if(! isFileExist(NOM_BDD)){
			if (JOptionPane.showConfirmDialog(null, "La Base de Donn�es \"" + NOM_BDD + "\" n'a pas �t� trouv�e dans le r�pertoire "
					+ "de l'application.\nSouhaitez-vous cr�er une nouvelle base de donn�es (n�cessite une connexion Internet) ?" 
					+ "\n(Pas fonctionnel)", 
					"Base de Donn�es manquante", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				//TODO Cr�ation BDD
			} else
				System.exit(0);
		}
		// Lancement du splashscreen
		splash = new SplashScreen(1000);
		splash.setChargement(0, "Connexion � la Base de donn�es...");

		// Connexion � la base de donn�e
		Connexion.setDBPath(NOM_BDD);
		connexion = Connexion.getInstance();
		connexion.connect();
		Preferences.getEtapeActuelle();

		//Initialisation statistiques 
		splash.setChargement(13, "Chargement des Sous-Zones...");
		SousZone.initialisation();

		splash.setChargement(50, "Chargement des Monstes...");
		monstres = Monstre.getMonstres();
		monstresActuels = new ArrayList<Monstre>();

		splash.setChargement(85, "Liaisons des Monstres aux Zones...");
		Zone.initialisation();
		splash.setChargement(100, "Lancement de l'interface...");
		splash.dispose();
		graphic = new Interface();

		// On initialise les pr�f�rences
		graphic.getDescription().setSelected(Preferences.isDetailed());
		graphic.getTransition().setSelected(Preferences.isChanged());
		graphic.getEtape().setText("�tape Actuelle : " + Preferences.getEtapeActuelle().getNom().substring(6));

		// Listeners du menu
		graphic.getQuitter().addActionListener(this);
		graphic.getAPropos().addActionListener(this);
		graphic.getChangement().addActionListener(this);
		graphic.getTransition().addActionListener(this);
		graphic.getReset().addActionListener(this);
		graphic.getPetitReset().addActionListener(this);
		graphic.getReportBug().addActionListener(this);
		graphic.getDescription().addActionListener(this);
		graphic.getPanelChoix().getZone().addActionListener(this);
		graphic.getPanelChoix().getTout().addActionListener(this);
		graphic.getPanelChoix().getEtape().addActionListener(this);
		graphic.getStopRecherche().addActionListener(this);
		graphic.getRecherche().addKeyListener(this);

		//Instanciation de la liste
		list = new ArrayList<JButton>();

		// Ecoute de la fen�tre
		graphic.addWindowListener(this);

		// Action du click sur le monstre associ�
		monstreAssocieAction = new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				graphic.getRecherche().focusGained(null);
				graphic.getRecherche().setText(button.getToolTipText());
				rechercher();
			}
		};

		majStats();

		// On clique sur l'�tape en cours et on met � jour le label
		graphic.getPanelChoix().getEtape().doClick();
		int numeroEtapeActuelle = Integer.parseInt(Preferences.getEtapeActuelle().getNom().substring(6)) - 1;
		list.get(numeroEtapeActuelle).doClick();
		graphic.getScrollClasse().getVerticalScrollBar().setValue(panelTree.getHeight() / list.size() * numeroEtapeActuelle);
	}

	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();

		// Bouton Tous
		if (o == graphic.getPanelChoix().getTout()){
			ongletOuvert = -1;
			graphic.getPanelChoix().actualiserBooleen(false);
			graphic.getPanelChoix().imageDefault();
			graphic.getPanelChoix().setToutIsSelected(true);
			graphic.getPanelChoix().getTout().setIcon(new ImageIcon(getClass().getResource("/img/Elements/TousON.png")));

			graphic.getPanelOuest().removeAll();
			graphic.getPanelOuest().add(graphic.getPanelChoix(), BorderLayout.NORTH);
			JPanel tmp = new JPanel();
			graphic.setScrollClasse(new JScrollPane(tmp));
			graphic.getPanelOuest().add(graphic.getScrollClasse(), BorderLayout.CENTER);
			graphic.getPanelOuest().revalidate();

			graphic.getPanelMonstres().removeAll();
			monstresActuels.clear();
			monstresActuels.addAll(monstres);

			

			for(Monstre monstre : monstresActuels){
				JPanelMonstre panelMonstre = new JPanelMonstre(this, monstre, graphic.getDescription().isSelected());
				if (panelMonstre.getMonstreAssocie() != null)
					panelMonstre.getMonstreAssocie().addActionListener(monstreAssocieAction);
				graphic.getPanelMonstres().add(panelMonstre);
			}

			graphic.getPanelMonstres().revalidate();
			oldSource = (JButton) e.getSource();
			graphic.repaint();
			return;
		}

		// Bouton zone
		if (o == graphic.getPanelChoix().getZone()){
			ongletOuvert = -1;
			graphic.getPanelChoix().actualiserBooleen(false);
			graphic.getPanelChoix().imageDefault();
			graphic.getPanelChoix().setZoneIsSelected(true);
			graphic.getPanelChoix().getZone().setIcon(new ImageIcon(getClass().getResource("/img/Elements/ZonesON.png")));

			graphic.getPanelOuest().removeAll();
			graphic.getPanelOuest().add(graphic.getPanelChoix(), BorderLayout.NORTH);
			list = getButtonsFromZone();
			panelTree = new JPanelTree(list, getButtonsFromSousZone(), graphic.getDimension());
			graphic.setScrollClasse(new JScrollPane(panelTree));
			graphic.getPanelOuest().add(graphic.getScrollClasse(), BorderLayout.CENTER);
			graphic.getPanelOuest().revalidate();

			graphic.getPanelMonstres().removeAll();

			oldSource = (JButton) e.getSource();
			graphic.repaint();
			return;
		}

		// Bouton �tape
		if (o == graphic.getPanelChoix().getEtape()){
			graphic.getPanelChoix().actualiserBooleen(false);
			graphic.getPanelChoix().imageDefault();
			graphic.getPanelChoix().setEtapeIsSelected(true);
			graphic.getPanelChoix().getEtape().setIcon(new ImageIcon(getClass().getResource("/img/Elements/EtapesON.png")));

			graphic.getPanelOuest().removeAll();
			graphic.getPanelOuest().add(graphic.getPanelChoix(), BorderLayout.NORTH);
			list = getButtonsFromEtape();
			panelTree = new JPanelTree(list, graphic.getDimension());
			graphic.setScrollClasse(new JScrollPane(panelTree));
			graphic.getPanelOuest().add(graphic.getScrollClasse(), BorderLayout.CENTER);
			graphic.getPanelOuest().revalidate();

			graphic.getPanelMonstres().revalidate();
			oldSource = (JButton) e.getSource();
			graphic.repaint();
			return;
		}

		//changement
		if (o == graphic.getChangement()) {

			Object[] etapes = Etape.getEtapes().toArray();

			Etape etape = (Etape) JOptionPane.showInputDialog(graphic, 
					"S�lectionnez votre �tape actuelle.", 
					"S�lection de l'�tape actuelle",
					JOptionPane.QUESTION_MESSAGE, 
					null, etapes, etapes[0]);

			if (etape == null)
				return;

			Preferences.setEtapeActuelle(etape);
			graphic.getEtape().setText("�tape Actuelle : " + Preferences.getEtapeActuelle().getNom().substring(6));
			majStats();
		}

		// reset
		if (o == graphic.getReset()) {
			if (JOptionPane.showConfirmDialog(null, "�tes-vous s�r de vouloir r�initialiser vos captures ?", 
					"Confirmation de r�initialisation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				Monstre.reinitialiserMonstres();
			oldSource.doClick();

			majStats();
			oldSource.doClick();
			return;
		}

		// quitter
		if (o == graphic.getQuitter()) {
			connexion.close();
			graphic.dispose();
		}

		//Transition
		if (o == graphic.getTransition()){
			Preferences.setIsChanged(graphic.getTransition().isSelected());

			if (graphic.getTransition().isSelected())
				changementAuto();
		}

		//Description
		if (o == graphic.getDescription()){
			Preferences.setIsDetailed(graphic.getDescription().isSelected());

			graphic.getPanelMonstres().removeAll();

			for(Monstre monstre : monstresActuels){
				JPanelMonstre panelMonstre = new JPanelMonstre(this, monstre, graphic.getDescription().isSelected());
				if (panelMonstre.getMonstreAssocie() != null)
					panelMonstre.getMonstreAssocie().addActionListener(monstreAssocieAction);
				graphic.getPanelMonstres().add(panelMonstre);
			}

			//On change la position de la scrollbar
			int value = graphic.getScrollMonstre().getVerticalScrollBar().getValue();
			int newValue;

			if (graphic.getDescription().isSelected())
				newValue = value * JPanelMonstre.TAILLE_MAXIMALE / JPanelMonstre.TAILLE_MINIMALE;
			else
				newValue = value * JPanelMonstre.TAILLE_MINIMALE / JPanelMonstre.TAILLE_MAXIMALE;
			graphic.getScrollMonstre().getVerticalScrollBar().setValue(newValue);

			graphic.getPanelMonstres().revalidate();
			graphic.repaint();
			return;
		}

		// � propos
		if (o == graphic.getAPropos()) {

			JLabel textaPropos1 = new JLabel("Le carnet de chasse d'Otoma� a �t� r�alis� par ");

			/**
			 * Ajouter un lien hypertexte � un JLabel
			 * @author Axel 2013
			 * @see http://www.fobec.com/java/1131/ajouter-lien-hypertexte-jlabel.html
			 * @reponse Une coupe de champagne � ton nom !
			 */

			JLabel linkedIn = new JLabel("Steven PESCHETEAU");
			linkedIn.setForeground(Color.BLUE);
			linkedIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			linkedIn.addMouseListener(new MouseAdapter() {
				//Click sur le lien
				public void mouseClicked(MouseEvent e) {
					JLabel label = (JLabel)e.getSource();
					label.getText().replaceAll("\\<.*?\\>", "");
					try {
						Desktop.getDesktop().browse(new URI("https://www.linkedin.com/pub/steven-pescheteau/81/925/274"));
					} catch (URISyntaxException ex) {
						ex.printStackTrace();
					}catch (IOException ex) {
						ex.printStackTrace();
					}
				}

				//Survol sur le lien
				public void mouseEntered(MouseEvent e) {
					JLabel label=(JLabel)e.getSource();
					String plainText = label.getText().replaceAll("\\<.*?\\>", "");
					//Souligner le texte 
					String urlText="<html><u>"+plainText+"</u></html>";
					label.setText(urlText);
				}

				//Quitte la zone du lien    
				public void mouseExited(MouseEvent e) {
					JLabel label=(JLabel)e.getSource();
					String plainText = label.getText().replaceAll("\\<.*?\\>", "");
					//Texte sans souslignage
					String urlText="<html>"+plainText+"</html>";
					label.setText(urlText);
				}
			});

			JLabel textaPropos2 = new JLabel("alias Guerrier-zombie (serveur Goultard).");
			textaPropos2.setBorder(BorderFactory.createEmptyBorder(-3, 0, 0, 0));
			JLabel textaPropos3 = new JLabel("<html><br /><p align=center>Je tiens � remercier Grenouille et MonsieurChance pour leur travail,<br />"
					+ " sur lequel je me suis largement inspir�.</p>"
					+ "<br /><br /><p align=center>Version de l'application : 0.8<br>Optimis� pour Dofus 2.30.5</p>"
					+ "<br /><p align=center>Certaines illustrations sont la propri�t� d'Ankama Studio"
					+ "<br /> et du jeu Dofus - Tous droits r�serv�s</p></html>");

			JPanel panelMessage = new JPanel();
			panelMessage.setPreferredSize(new Dimension(340, 180));
			panelMessage.add(textaPropos1);
			panelMessage.add(linkedIn);
			panelMessage.add(textaPropos2);
			panelMessage.add(textaPropos3);
			JOptionPane.showMessageDialog(null, panelMessage, "� propos", -1);
			return;
		}

		// Report de Bugs
		if (o == graphic.getReportBug()) {
			if (Desktop.isDesktopSupported()) {
				if (Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					URI uri;
					try {
						uri = new URI("http://lafeuilleverte.forumgratuit.org/t2112-carnet-de-chasse-d-otomai-version-alpha");
						Desktop.getDesktop().browse(uri);
					} catch (URISyntaxException arg0) {
						arg0.printStackTrace();
					} catch (IOException arg0) {
						arg0.printStackTrace();
					}
				}
			}
			return;
		}

		// Bouton Stop Recherche
		if (graphic.getStopRecherche() == o){
			if ((graphic.getRecherche().getText() != null) || (! graphic.getRecherche().getText().equals(""))){
				graphic.getRecherche().focusGained(null);
				graphic.getRecherche().setText("");
				rechercher();
				graphic.getRecherche().focusLost(null);
			}
			return;
		}

		// Un Bouton parmi les Etapes
		if (graphic.getPanelChoix().isEtapeIsSelected()){
			for (int i = 0; i < list.size(); i++)
				if (list.get(i) == o){
					panelTree.refermerListePerso(-1, false);
					List<Etape> etapes = Etape.getEtapes();
					for(int j = 0; j < list.size(); j++){
						Etape etape = etapes.get(j);
						list.get(j).setIcon(ImageTextuelle.getSuperImage(etape.getNom(), (int) (100 * etape.getNombre() / etape.getMax()), false));
						list.get(j).setRolloverIcon(ImageTextuelle.getSuperImagePass(etape.getNom(), (int) (100 * etape.getNombre() / etape.getMax()), false));
					}

					Etape etape = etapes.get(i);
					list.get(i).setIcon(ImageTextuelle.getSuperImage(etape.getNom(), (int) (100 * etape.getNombre() / etape.getMax()), true));
					list.get(i).setRolloverIcon(ImageTextuelle.getSuperImagePass(etape.getNom(), (int) (100 * etape.getNombre()  / etape.getMax()), true));

					// On s'occupe de remettre les bons monstres dans la liste de monstres actuels
					monstresActuels.clear();
					monstresActuels.addAll(etape.getMonstres());

					// On refait les bons panels en fonction de la nouvelle liste
					
					graphic.getPanelMonstres().removeAll();

					for(Monstre monstre : monstresActuels){
						JPanelMonstre panelMonstre = new JPanelMonstre(this, monstre, graphic.getDescription().isSelected());
						if (panelMonstre.getMonstreAssocie() != null)
							panelMonstre.getMonstreAssocie().addActionListener(monstreAssocieAction);
						graphic.getPanelMonstres().add(panelMonstre);
					}
					
					// On rafra�chit
					graphic.getPanelMonstres().revalidate();
					oldSource = (JButton) e.getSource();
					graphic.repaint();
					return;
				}
		}

		// Un Bouton parmi les Zones
		if (graphic.getPanelChoix().isZoneIsSelected()){
			for (int i = 0; i < list.size(); i++)
				if (list.get(i) == o){

					int value = graphic.getScrollClasse().getVerticalScrollBar().getValue();

					Zone zone = ((JButtonZone) list.get(i)).getZone();

					// On s'occupe de remettre les bons monstres dans la liste de monstres actuels
					if (zone.getSousZones().size() == 1){
						monstresActuels.clear();
						monstresActuels.addAll(zone.getMonstres());

						// On refait les bons panels en fonction de la nouvelle liste
						
						graphic.getPanelMonstres().removeAll();

						for(Monstre monstre : monstresActuels){
							JPanelMonstre panelMonstre = new JPanelMonstre(this, monstre, graphic.getDescription().isSelected());
							if (panelMonstre.getMonstreAssocie() != null)
								panelMonstre.getMonstreAssocie().addActionListener(monstreAssocieAction);
							graphic.getPanelMonstres().add(panelMonstre);
						}

						for (JButton button : list){
							Zone zonee = ((JButtonZone) button).getZone();
							button.setIcon(ImageTextuelle.getSuperImage(zonee.getNom(), (int) (100 * zonee.getNombre() / zonee.getMax()), false));
							button.setRolloverIcon(ImageTextuelle.getSuperImagePass(zonee.getNom(), (int) (100 * zonee.getNombre() / zonee.getMax()), false));
						}
					}

					list.get(i).setIcon(ImageTextuelle.getSuperImage(zone.getNom(), (int) (100 * zone.getNombre() / zone.getMax()), true));
					list.get(i).setRolloverIcon(ImageTextuelle.getSuperImagePass(zone.getNom(), (int) (100 * zone.getNombre() / zone.getMax()), true));

					if (ongletOuvert != i){
						ongletOuvert = i;
						graphic.getPanelOuest().removeAll();
						graphic.getPanelOuest().add(graphic.getPanelChoix(), BorderLayout.NORTH);
						list = getButtonsFromZone();
						panelTree = new JPanelTree(list, getButtonsFromSousZone(), graphic.getDimension());
						graphic.setScrollClasse(new JScrollPane(panelTree));
						graphic.getPanelOuest().add(graphic.getScrollClasse(), BorderLayout.CENTER);
						graphic.getPanelOuest().revalidate();
						panelTree.refermerListePerso(i, true);
					}
					else{
						ongletOuvert = -1;
						panelTree.refermerListePerso(i, false);
					}

					graphic.getScrollClasse().getVerticalScrollBar().setValue(value);

					for(int j = 0; j < list.size(); j++){
						Zone zonee = Zone.getZones().get(j);
						list.get(j).setIcon(ImageTextuelle.getSuperImage(zonee.getNom(), (int) (100 * zonee.getNombre() / zonee.getMax()), false));
						list.get(j).setRolloverIcon(ImageTextuelle.getSuperImagePass(zonee.getNom(), (int) (100 * zonee.getNombre() / zonee.getMax()), false));
					}

					list.get(i).setIcon(ImageTextuelle.getSuperImage(zone.getNom(), (int) (100 * zone.getNombre() / zone.getMax()), true));
					list.get(i).setRolloverIcon(ImageTextuelle.getSuperImagePass(zone.getNom(), (int) (100 * zone.getNombre() / zone.getMax()), true));

					// On rafra�chit
					graphic.getPanelMonstres().revalidate();
					graphic.repaint();
					oldSource = (JButton) e.getSource();
					return;
				}


			// Un button parmi les SOUS zones
			if(ongletOuvert != - 1){
				List<JButton> listOfButton = sousList.get(ongletOuvert);

				for (int i = 0; i < listOfButton.size(); i++)
					if (o == listOfButton.get(i)){

						Zone zone = ((JButtonZone) listOfButton.get(i)).getZone();

						// On s'occupe de remettre les bons monstres dans la liste de monstres actuels
						monstresActuels.clear();
						monstresActuels.addAll(zone.getMonstres());

						// On refait les bons panels en fonction de la nouvelle liste
						
						graphic.getPanelMonstres().removeAll();

						for(Monstre monstre : monstresActuels){
							JPanelMonstre panelMonstre = new JPanelMonstre(this, monstre, graphic.getDescription().isSelected());
							if (panelMonstre.getMonstreAssocie() != null)
								panelMonstre.getMonstreAssocie().addActionListener(monstreAssocieAction);
							graphic.getPanelMonstres().add(panelMonstre);
						}

						for (JButton button : listOfButton){
							Zone zonee = ((JButtonZone) button).getZone();
							button.setIcon(ImageTextuelle.getSousImage(zonee.getNom(), (int) (100 * zonee.getNombre() / zonee.getMax()), false));
							button.setRolloverIcon(ImageTextuelle.getSousImagePass(zonee.getNom(), (int) (100 * zonee.getNombre() / zonee.getMax()), false));
						}

						listOfButton.get(i).setIcon(ImageTextuelle.getSousImage(zone.getNom(), (int) (100 * zone.getNombre() / zone.getMax()), true));
						listOfButton.get(i).setRolloverIcon(ImageTextuelle.getSousImagePass(zone.getNom(), (int) (100 * zone.getNombre() / zone.getMax()), true));

						graphic.getPanelMonstres().revalidate();
						graphic.repaint();
						oldSource = (JButton) e.getSource();
						return;
					}
			}
		}

		graphic.repaint();
	}

	private List<JButton> getButtonsFromZone() {
		List<Zone> zones = Zone.getZones();
		List<JButton> list = new ArrayList<JButton>();
		for (Zone zone : zones){
			JButtonZone button = new JButtonZone(ImageTextuelle.getSuperImage(zone.getNom(), (int) (100 * zone.getNombre() / zone.getMax()), false), zone);
			button.setRolloverIcon(ImageTextuelle.getSuperImagePass(zone.getNom(), (int) (100 * zone.getNombre() / zone.getMax()), false));
			button.addActionListener(this);
			zone.setButton(button);
			list.add(button);
		}

		return list;
	}

	private List<List<JPanel>> getButtonsFromSousZone() {
		List<List<SousZone>> sousZones = SousZone.getAllSousZones();
		List<List<JPanel>> list = new ArrayList<List<JPanel>>();
		sousList = new ArrayList<List<JButton>>();

		for (List<SousZone> listeSousZone : sousZones){

			List<JPanel> listPanel = new ArrayList<JPanel>();
			List<JButton> listButton = new ArrayList<JButton>();
			JPanel panel; 
			JButton button;

			if (listeSousZone.size() > 1)
				for (SousZone sousZone : listeSousZone){
					panel = new JPanel(new GridBagLayout());
					panel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
					button = new JButtonZone(ImageTextuelle.getSousImage(sousZone.getNom(), (int) (100 * sousZone.getNombre() / sousZone.getMax()), false), sousZone);
					button.setRolloverIcon(ImageTextuelle.getSousImagePass(sousZone.getNom(), (int) (100 * sousZone.getNombre() / sousZone.getMax()), false));
					sousZone.setButton(button);
					button.setPreferredSize(new Dimension(350, 70));
					button.setFocusPainted(false);
					button.addActionListener(this);
					panel.add(button);

					listButton.add(button);
					listPanel.add(panel);
				}

			list.add(listPanel);
			sousList.add(listButton);
		}

		return list;
	}

	private List<JButton> getButtonsFromEtape() {
		List<Etape> etapes = Etape.getEtapes();
		List<JButton> list = new ArrayList<JButton>();
		for (int i = 0; i < etapes.size(); ++i){
			Etape etape = etapes.get(i);
			JButton button = new JButton(ImageTextuelle.getSuperImage(etapes.get(i).getNom(), (int) (100 * etape.getNombre() / etape.getMax()), false));
			button.setRolloverIcon(ImageTextuelle.getSuperImagePass(etapes.get(i).getNom(), (int) (100 * etape.getNombre()  / etape.getMax()), false));
			button.addActionListener(this);
			etape.setButton(button);
			list.add(button);
		}

		return list;
	}

	public void rechercher(){
		String nomEcrit = graphic.getRecherche().getText();

		if (nomEcrit == null || nomEcrit.equals("") || nomEcrit.length() < 3){
			graphic.getStopRecherche().setIcon(new ImageIcon(getClass().getResource("/img/Elements/stopSearchHide.png")));
			oldSource.doClick();
			graphic.getScrollMonstre().getVerticalScrollBar().setValue(oldScrollBarValue);
		}
		else {
			graphic.getStopRecherche().setIcon(new ImageIcon(getClass().getResource("/img/Elements/stopSearch.png")));
			if (graphic.getScrollMonstre().getVerticalScrollBar().getValue() != 0)
				oldScrollBarValue = graphic.getScrollMonstre().getVerticalScrollBar().getValue();
			graphic.getScrollMonstre().getVerticalScrollBar().setValue(0);

			monstresActuels.clear();
			graphic.getPanelMonstres().removeAll();

			for(Monstre monstre : monstres){
				String nom = monstre.getNom();

				if(Normalizer.normalize(nom, Normalizer.Form.NFD)
						.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase()
						.contains(Normalizer.normalize(nomEcrit, Normalizer.Form.NFD)
								.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase())){
					monstresActuels.add(monstre);
					JPanelMonstre panelMonstre = new JPanelMonstre(this, monstre, graphic.getDescription().isSelected());
					if (panelMonstre.getMonstreAssocie() != null)
						panelMonstre.getMonstreAssocie().addActionListener(monstreAssocieAction);
					graphic.getPanelMonstres().add(panelMonstre);
				}
			}
		}
		graphic.getPanelMonstres().revalidate();
		graphic.repaint();
	}

	public void incrementerStats(Etape etape, List<SousZone> zones){
		etape.incrementerNombre();

		for(SousZone zone : zones)
			zone.incrementerNombre();

		updateMonsterButtons(etape, zones);
	}

	public void decrementerStats(Etape etape, List<SousZone> zones){
		etape.decrementerNombre();

		for(SousZone zone : zones)
			zone.decrementerNombre();

		updateMonsterButtons(etape, zones);
	}

	public void updateMonsterButtons(Etape etape, List<SousZone> zones){
		if (graphic.getPanelChoix().isEtapeIsSelected()){
			int valeur = (int) (etape.getNombre() * 100 / etape.getMax());
			JButton buttonEtape = etape.getButton();
			buttonEtape.setIcon(ImageTextuelle.makeStats((ImageIcon) buttonEtape.getIcon(), valeur));
			buttonEtape.setRolloverIcon(ImageTextuelle.makeStats((ImageIcon) buttonEtape.getRolloverIcon(), valeur));
		}
		else if (graphic.getPanelChoix().isZoneIsSelected()){
			for(SousZone zone : zones){
				int valeur = (int) (zone.getNombre() * 100 / zone.getMax());
				JButtonZone buttonZone = (JButtonZone) zone.getButton();
				//TODO icon fausse
				if (buttonZone != null)
					System.out.println(buttonZone.getZone());
				buttonZone.setIcon(ImageTextuelle.makeStats((ImageIcon) buttonZone.getIcon(), valeur));
				buttonZone.setRolloverIcon(ImageTextuelle.makeStats((ImageIcon) buttonZone.getRolloverIcon(), valeur));
				Zone zonee = zone.getZoneAssocie();

				valeur = (int) (zonee.getNombre() * 100 / zonee.getMax());
				buttonZone = (JButtonZone) zonee.getButton();
				buttonZone.setIcon(ImageTextuelle.makeStats((ImageIcon) buttonZone.getIcon(), valeur));
				buttonZone.setRolloverIcon(ImageTextuelle.makeStats((ImageIcon) buttonZone.getRolloverIcon(), valeur));
			}
		}
	}

	public void majStats(){

		// Stats dans le coin � droite
		StringBuilder st = new StringBuilder("<html>");
		int nbMonstre = 0, nbMax = 0;
		int etapeActuelle = Integer.parseInt(Preferences.getEtapeActuelle().getNom().substring(6));

		if (etapeActuelle < 17){
			for(int i = etapeActuelle; i < 17; ++i){
				nbMonstre += Etape.getEtapes().get(i - 1).getNombre();
				nbMax += Etape.getEtapes().get(i - 1).getMax();
			}

			st.append(nbMonstre + " / " + nbMax + " monstres<br/>");
			nbMonstre = 0; nbMax = 0;

			for(int i = 17; i < 20; ++i){
				nbMonstre += Etape.getEtapes().get(i - 1).getNombre();
				nbMax += Etape.getEtapes().get(i - 1).getMax();
			}

			st.append(nbMonstre + " / " + nbMax + " boss<br/>");
			nbMonstre = 0; nbMax = 0;

			for(int i = 20; i < 35; ++i){
				nbMonstre += Etape.getEtapes().get(i - 1).getNombre();
				nbMax += Etape.getEtapes().get(i - 1).getMax();
			}

			st.append(nbMonstre + " / " + nbMax + " archi-monstres<br/>");
		}
		else {

			if (etapeActuelle < 20){
				for(int i = etapeActuelle; i < 20; ++i){
					nbMonstre += Etape.getEtapes().get(i - 1).getNombre();
					nbMax += Etape.getEtapes().get(i - 1).getMax();
				}

				st.append(nbMonstre + " / " + nbMax + " boss<br/>");
				nbMonstre = 0; nbMax = 0;

				for(int i = 20; i < 35; ++i){
					nbMonstre += Etape.getEtapes().get(i - 1).getNombre();
					nbMax += Etape.getEtapes().get(i - 1).getMax();
				}

				st.append(nbMonstre + " / " + nbMax + " archi-monstres<br/>");
			}
			else {
				for(int i = etapeActuelle; i < 35; ++i){
					nbMonstre += Etape.getEtapes().get(i - 1).getNombre();
					nbMax += Etape.getEtapes().get(i - 1).getMax();
				}

				st.append(nbMonstre + " / " + nbMax + " archi-monstres<br/>");
			}
		}

		st.append("</html>");
		graphic.getStats().setText(st.toString());
	}

	public void changementAuto() {
		int etape = Integer.parseInt(Preferences.getEtapeActuelle().getNom().substring(6)) - 1;

		//TODO -1 aux captures de cette �tape
		etape = (etape + 1) % Etape.getEtapes().size();

		Preferences.setEtapeActuelle(Etape.getEtapes().get(etape - 1));
		graphic.getEtape().setText("�tape Actuelle : " + Preferences.getEtapeActuelle().getNom().substring(6));
		majStats();

		if (etape == 0){
			//TODO JDialog fin qu�te
		}
	}
	
	public boolean isFileExist(String nomFile){

		boolean result = false;
		String[] dir = new java.io.File(".").list();

		for (int i = 0; i < dir.length; ++i)
			if(dir[i].equals(nomFile)){
				result = true;
				break;
			}

		return result;
	}
	
	public void windowClosing(WindowEvent e) {

		connexion.close();
		graphic.dispose();
	}

	public void keyReleased(KeyEvent e) {
		rechercher();
	}

	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void keyPressed(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}


	public static void main(String[] args){
		new Gestionnaire();
	}
}