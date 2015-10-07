package view;

/**
 * @author Steven PESCHETEAU
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Interface extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public static final int WIDTH_MONSTER = 755;
	private final Color backgroundColor = new Color(243, 227, 194);
	private final Color foregroundColor = new Color(34, 177, 76);
	private final Dimension dimensionImgScroll = new Dimension(400, 70);
	private JPanel panelOuest, panelEst, panelRecherche, panelNordEst, panelStat, panelInfo, panelMonstres;
	private JPanelChoix panelChoix;
	private JScrollPane scrollClasse, scrollMonstre;
	private JMenu menuFichier, menuOptions, menuHelp;
	private JMenuItem quitter, aPropos, reportBug, changement, reset, petitReset;
	private JCheckBoxMenuItem description, transition, retrait;
	private JMenuBar menuBar;
	private HintTextField recherche;
	private JLabel stats, etape;
	private JButton stopRecherche;
	private Container container = getContentPane();

	public Interface() {
		super("Carnet de Chasse d'Otomaï - Version ALPHA");

		// Paramètres basiques de la fenêtre
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(1200, 700));
		//TODO permettre le resizable
		setResizable(false);
		setLocationRelativeTo(null);
		setIconImage(new ImageIcon(this.getClass().getResource("/img/Elements/Icone.png")).getImage());

		// Permet de donner une visualisation plus ancré dans le système d'exploitation
		UIManager.put("OptionPane.background", backgroundColor);
		UIManager.put("Panel.background", backgroundColor);
		UIManager.put("Button.background", backgroundColor);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		//création du Menu
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuFichier = new JMenu("   Fichier   ");
		changement = new JMenuItem("Changer d'étape actuelle");
		changement.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		changement.setToolTipText("<html>L'étape actuelle influe sur les statistiques :<br> les étapes précédentes sont ignorées et non comptabilisées.</html>");
		reset = new JMenuItem("Réinitialiser les captures");
		reset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		reset.setToolTipText("Remet à zéro l'ensemble des captures des monstres, quelque soit leur type.");
		petitReset = new JMenuItem("Retirer une capture d'une fourchette d'étapes");
		petitReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		petitReset.setToolTipText("Retire, si possible, une capture entre deux étapes données.");
		quitter =  new JMenuItem("Quitter");
		quitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		quitter.setToolTipText("Termine l'application.");

		menuFichier.add(changement);
		menuFichier.add(petitReset);
		menuFichier.add(reset);
		menuFichier.add(quitter);
		menuBar.add(menuFichier);

		menuOptions = new JMenu("   Options   ");
		description = new JCheckBoxMenuItem("Afficher les descriptions des monstres");
		description.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		description.setToolTipText("Affiche la vitalité, les PA, les PM, les résistances et les zones de chaque monstre.");
		transition = new JCheckBoxMenuItem("Activer le changement d'étape");
		transition.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		transition.setToolTipText("<html>Lorsque l'étape actuelle est complétée, on passe<br>automatiquement à la suivante.</html>");
		retrait = new JCheckBoxMenuItem("Activer le retrait de capture automatique");
		retrait.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		retrait.setToolTipText("<html>Lorsque l'étape actuelle est complétée, on retire<br>automatiquement les monstres de celle-ci.</html>");
		menuOptions.add(description);
		menuOptions.add(transition);
		menuOptions.add(retrait);
		menuBar.add(menuOptions);

		menuHelp = new JMenu("        ?        ");
		aPropos = new JMenuItem("À propos de...");
		aPropos.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		aPropos.setToolTipText("Détails sur l'application et sur le créateur.");

		reportBug = new JMenuItem("Reporter un bug");
		reportBug.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		reportBug.setToolTipText("<html>Redirige vers une page du forum La Feuille Verte.<br> N'hésitez pas à relever d'éventuels bugs afin d'améliorer l'application !</html>");

		menuHelp.add(aPropos);
		menuHelp.add(reportBug);
		menuBar.add(menuHelp);

		//Gestion du panelChoix
		panelChoix = new JPanelChoix(new Dimension(dimensionImgScroll.width, 50));

		// Gestion du ScrollPane
		setScrollClasse(new JScrollPane());

		// Gestion du panel Ouest
		panelOuest = new JPanel(new BorderLayout());
		panelOuest.add(panelChoix, BorderLayout.NORTH);
		panelOuest.add(scrollClasse);
		
		// Gestion du Panel Recherche
		panelRecherche = new JPanel();
		recherche = new HintTextField("Nom d'un Monstre");
		recherche.setPreferredSize(new Dimension(250, 30));
		stopRecherche = new JButton(new ImageIcon(getClass().getResource("/img/Elements/stopSearchHide.png")));
		stopRecherche.setPreferredSize(new Dimension(32, 32));
		stopRecherche.setFocusPainted(false);
		panelRecherche.add(recherche);
		panelRecherche.add(stopRecherche);
		
		// Gestion du panel Stats
		panelStat = new JPanel(new GridLayout(1, 1));
		stats = new JLabel();
		stats.setForeground(foregroundColor);
		stats.setFont(stats.getFont().deriveFont(Font.BOLD));
		panelStat.add(stats);
		
		//Etape actuelle
		etape = new JLabel("Étape Actuelle : 1");
		etape.setForeground(foregroundColor);
		etape.setHorizontalAlignment(JLabel.CENTER);
		etape.setFont(stats.getFont().deriveFont(Font.BOLD));
		
		// Gestion du panel Info
		panelInfo = new JPanel(new GridLayout(1, 2));
		panelInfo.add(etape);
		panelInfo.add(panelStat);
		
		// Gestion du panel Nord Est
		panelNordEst = new JPanel(new BorderLayout());
		panelNordEst.setPreferredSize(new Dimension(dimensionImgScroll.width, 50));
		panelNordEst.add(panelRecherche, BorderLayout.WEST);
		panelNordEst.add(panelInfo, BorderLayout.CENTER);
		
		//Gestion du panel contenant les mobs
		panelMonstres = new JPanel(new WrapLayout(0, 0, 0));
		scrollMonstre = new JScrollPane(panelMonstres);
		scrollMonstre.getVerticalScrollBar().setUnitIncrement(20);
		scrollMonstre.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollMonstre.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		// gestion du panel Est
		panelEst = new JPanel(new BorderLayout());
		panelEst.add(panelNordEst, BorderLayout.NORTH);
		panelEst.add(scrollMonstre, BorderLayout.CENTER);

		// Gestion du conteneur
		container.setLayout(new BorderLayout());
		container.add(panelEst, BorderLayout.CENTER);
		container.add(panelOuest, BorderLayout.WEST);

		// fenêtre visible
		setVisible(true);
	}

	public void setScrollClasse(JScrollPane pane){
		scrollClasse = pane;
		scrollClasse.setPreferredSize(new Dimension(dimensionImgScroll.width + 20, 1));
		scrollClasse.getVerticalScrollBar().setUnitIncrement(15);
	}
	
	public JScrollPane getScrollClasse(){
		return scrollClasse;
	}
	
	public JScrollPane getScrollMonstre(){
		return scrollMonstre;
	}

	public JMenuItem getQuitter() {
		return quitter;
	}

	public JMenuItem getAPropos() {
		return aPropos;
	}

	public JMenuItem getDescription() {
		return description;
	}

	public JMenuItem getReportBug() {
		return reportBug;
	}

	public JPanelChoix getPanelChoix(){
		return panelChoix;
	}

	public JPanel getPanelOuest(){
		return panelOuest;
	}

	public Dimension getDimension(){
		return dimensionImgScroll;
	}

	public Color getBackgroundColor(){
		return backgroundColor;
	}

	public JMenuItem getaPropos() {
		return aPropos;
	}

	public HintTextField getRecherche() {
		return recherche;
	}
	
	public JMenuItem getReset(){
		return reset;
	}
	
	public JMenuItem getChangement(){
		return changement;
	}
	
	public JCheckBoxMenuItem getTransition(){
		return transition;
	}
	
	public JCheckBoxMenuItem getRetrait(){
		return retrait;
	}
	
	public JMenuItem getPetitReset(){
		return petitReset;
	}
	
	public JLabel getStats() {
		return stats;
	}

	public JLabel getEtape() {
		return etape;
	}

	public JButton getStopRecherche() {
		return stopRecherche;
	}
	
	public JPanel getPanelMonstres(){
		return panelMonstres;
	}

	public JPanel getPanelStats() {
		return panelStat;
	}
}