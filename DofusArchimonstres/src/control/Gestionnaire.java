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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.Joueur;
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
			if (JOptionPane.showConfirmDialog(null, "La Base de Données \"" + NOM_BDD + "\" n'a pas été trouvée dans le répertoire "
					+ "de l'application.\nSouhaitez-vous créer une nouvelle base de données (nécessite une connexion Internet) ?" 
					+ "\n(Pas fonctionnel)", 
					"Base de Données manquante", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				//TODO Création BDD
			} else
				System.exit(0);
		}
		// Lancement du splashscreen
		splash = new SplashScreen(1000);
		splash.setChargement(0, "Connexion à la Base de données...");

		// Connexion à la base de donnée
		Connexion.setDBPath(NOM_BDD);
		connexion = Connexion.getInstance();
		connexion.connect();
		Preferences.getEtapeActuelle();

		//Initialisation statistiques 
		SousZone.initialisation();
		splash.setChargement(26, "Chargement des Monstes...");
		monstres = Monstre.getMonstres();
		monstresActuels = new ArrayList<Monstre>();
		splash.setChargement(62, "Chargement des Sous-Zones...");
		SousZone.sortMonstres();
		splash.setChargement(85, "Liaisons des Monstres aux Zones...");
		Zone.initialisation();
		splash.setChargement(100, "Lancement de l'interface...");
		splash.dispose();
		graphic = new Interface();

		// On initialise les préférences
		graphic.getDescription().setSelected(Preferences.isDetailed());
		graphic.getTransition().setSelected(Preferences.isChanged());
		graphic.getRetrait().setSelected(Preferences.isRemoved());

		graphic.getEtape().setText("Étape Actuelle : " + Preferences.getEtapeActuelle().getNom().substring(6));

		// Listeners du menu
		graphic.getImporter().addActionListener(this);
		graphic.getExporter().addActionListener(this);
		graphic.getQuitter().addActionListener(this);
		graphic.getAPropos().addActionListener(this);
		graphic.getChangement().addActionListener(this);
		graphic.getTransition().addActionListener(this);
		graphic.getRetrait().addActionListener(this);
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

		// Ecoute de la fenêtre
		graphic.addWindowListener(this);

		// Action du click sur le monstre associé
		monstreAssocieAction = new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				JButton button = (JButton) e.getSource();
				graphic.getRecherche().focusGained(null);
				graphic.getRecherche().setText(button.getToolTipText());
				rechercher();
			}
		};

		majStats();

		// On clique sur l'étape en cours et on met à jour le label
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

		// Bouton étape
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
					"Sélectionnez votre étape actuelle.", 
					"Sélection de l'étape actuelle",
					JOptionPane.QUESTION_MESSAGE, 
					null, etapes, etapes[0]);

			if (etape == null)
				return;

			Preferences.setEtapeActuelle(etape);
			graphic.getEtape().setText("Étape Actuelle : " + Preferences.getEtapeActuelle().getNom().substring(6));
			majStats();

			if (graphic.getPanelChoix().isEtapeIsSelected())
				etape.getButton().doClick();
		}

		// reset
		if (o == graphic.getReset()) {
			if (JOptionPane.showConfirmDialog(null, "Êtes-vous sûr de vouloir réinitialiser vos captures ?", 
					"Confirmation de réinitialisation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				Monstre.reinitialiserMonstres();
			oldSource.doClick();

			majStats();
			oldSource.doClick();
			return;
		}

		// Importer
		if (o == graphic.getImporter()){
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier de partage du carnet de chasse d'Otomaï", "fpcco"));
			fileChooser.setAcceptAllFileFilterUsed(false);

			if (fileChooser.showOpenDialog(graphic) == JFileChooser.APPROVE_OPTION) {
				if (!fileChooser.getSelectedFile().exists()){
					JOptionPane.showMessageDialog(graphic, "Ce fichier n'existe pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
					return;
				}

				try {
					BufferedReader br = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
					String line =  br.readLine();
					String cvsSplitBy = ";";

					String[] data = line.split(cvsSplitBy);
					String nom = data[0];
					Etape etape = Etape.getEtapes().get(Integer.parseInt(data[1]) - 1);
					List<Monstre> monstresEnFace = new ArrayList<Monstre>();
					Map<String, Monstre> monstresdIci = new HashMap<String, Monstre>();
					
					for(Monstre monstre : Monstre.getMonstres())
						monstresdIci.put(monstre.getNom(), monstre);

					while ((line = br.readLine()) != null) {
						data = line.split(cvsSplitBy);
						Monstre monstre = Monstre.getMonstreJoueur(data[0], Integer.parseInt(data[1]));
						monstresEnFace.add(monstre);


						if (monstresdIci.get(monstre.getNom()) == null){
							JOptionPane.showMessageDialog(graphic, "Erreur : fichier corrompu.", "Erreur", JOptionPane.ERROR_MESSAGE);
							br.close();
							return;
						}
					}

					br.close();

					if (monstresEnFace.size() != monstresdIci.keySet().size()){
						JOptionPane.showMessageDialog(graphic, "Erreur : fichier corrompu.", "Erreur", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					// On a trouvé la correspondance du monstre
					Joueur joueur = new Joueur(Preferences.getNom(), Preferences.getEtapeActuelle(), Monstre.getMonstres());
					Joueur other = new Joueur(nom, etape, monstresEnFace);
					new GestionnaireCommerce(this, joueur, other);

				} catch (Exception e1) {
					JOptionPane.showMessageDialog(graphic, "Erreur : fichier corrompu.", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		// Exporter
		if (o == graphic.getExporter()){
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Fichier de partage du carnet de chasse d'Otomaï", "fpcco"));
			fileChooser.setAcceptAllFileFilterUsed(false);

			if (fileChooser.showSaveDialog(graphic) == JFileChooser.APPROVE_OPTION) {

				try {
					FileWriter fw;
					if (!fileChooser.getSelectedFile().exists())
						fw = new FileWriter(fileChooser.getSelectedFile() + ".fpcco");
					else
						fw = new FileWriter(fileChooser.getSelectedFile());

					fw.write(Monstre.exporterMonstres());
					fw.close();
					JOptionPane.showMessageDialog(graphic, "Exportation effectuée.", "Exportation", JOptionPane.DEFAULT_OPTION);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(graphic, "Une erreur est survenue lors de l'exportation.", "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
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

		//Retrait
		if (o == graphic.getRetrait()){
			Preferences.setIsRemoved(graphic.getRetrait().isSelected());
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

		// à propos
		if (o == graphic.getAPropos()) {

			JLabel textaPropos1 = new JLabel("Le carnet de chasse d'Otomaï a été réalisé par ");

			/**
			 * Ajouter un lien hypertexte à un JLabel
			 * @author Axel 2013
			 * @see http://www.fobec.com/java/1131/ajouter-lien-hypertexte-jlabel.html
			 * @reponse Une coupe de champagne à ton nom !
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
			JLabel textaPropos3 = new JLabel("<html><br /><p align=center>Je tiens à remercier Grenouille et MonsieurChance pour leur travail,<br />"
					+ " sur lequel je me suis largement inspiré.</p>"
					+ "<br /><br /><p align=center>Version de l'application : 0.9<br>Optimisé pour Dofus 2.30.5</p>"
					+ "<br /><p align=center>Certaines illustrations sont la propriété d'Ankama Studio"
					+ "<br /> et du jeu Dofus - Tous droits réservés</p></html>");

			JPanel panelMessage = new JPanel();
			panelMessage.setPreferredSize(new Dimension(340, 180));
			panelMessage.add(textaPropos1);
			panelMessage.add(linkedIn);
			panelMessage.add(textaPropos2);
			panelMessage.add(textaPropos3);
			JOptionPane.showMessageDialog(null, panelMessage, "À propos", -1);
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

					// On rafraîchit
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

					// On rafraîchit
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

		changementAuto();

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

		// Stats dans le coin à droite
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
		if (graphic.getTransition().isSelected()){
			int etape = Integer.parseInt(Preferences.getEtapeActuelle().getNom().substring(6)) - 1;
			int stats = Etape.getEtapes().get(etape).getNombre() / Etape.getEtapes().get(etape).getMax();
			if (stats == 1){
				if (graphic.getRetrait().isSelected()){
					List<Monstre> monstres = Etape.getEtapes().get(etape).getMonstres();

					for(Monstre monstre : monstres){
						monstre.decrementerNombre();
						decrementerStats(monstre.getEtapeAssocie().get(0), monstre.getZoneAssocie());
					}
				}

				etape = (etape + 1) % Etape.getEtapes().size();
				Preferences.setEtapeActuelle(Etape.getEtapes().get(etape));
				graphic.getEtape().setText("Étape Actuelle : " + Preferences.getEtapeActuelle().getNom().substring(6));
				majStats();

				if (etape == 0){
					//TODO JDialog fin quête
					etape++;
				}
				if (graphic.getPanelChoix().isEtapeIsSelected())
					Etape.getEtapes().get(etape).getButton().doClick();
				else
					oldSource.doClick();
				changementAuto();
			}
		}
	}

	public SousZone calculProposition(){
		//TODO
		return null;
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