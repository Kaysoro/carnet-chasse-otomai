package view;

/**
 * @author Steven PESCHETEAU
 */

import java.awt.Dimension;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class InterfaceCommerce extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public InterfaceCommerce() {
		super("Carnet de Chasse d'Otomaï - Version ALPHA");

		// Paramètres basiques de la fenêtre
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(800, 500));
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(new ImageIcon(this.getClass().getResource("/img/Elements/Icone.png")).getImage());

		// fenêtre visible
		setVisible(true);
	}
}