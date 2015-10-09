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
		super("Carnet de Chasse d'Otoma� - Version ALPHA");

		// Param�tres basiques de la fen�tre
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(800, 500));
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(new ImageIcon(this.getClass().getResource("/img/Elements/Icone.png")).getImage());

		// fen�tre visible
		setVisible(true);
	}
}