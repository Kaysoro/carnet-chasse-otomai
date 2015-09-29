package view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;

public class JPanelTree extends JPanel {

	private static final long serialVersionUID = 1L;
	private List<JButton> buttons = null;
	private List<List<JPanel>> panels = null;
	private Dimension dimensionImgScroll;
	private GridBagConstraints contraintes;

	public JPanelTree(List<JButton> list, Dimension dimensionImgScroll){
		super();

		this.buttons = list;
		this.dimensionImgScroll = dimensionImgScroll;
		
		// Gestion du layout et de ses contraintes
		setLayout(new GridBagLayout());
		contraintes = new GridBagConstraints();
		contraintes.gridx = 0;
		contraintes.gridy = GridBagConstraints.RELATIVE;

		//Remplissage du panel
		refermerListePerso(-1, false);
		revalidate();
	}

	public JPanelTree(List<JButton> list, List<List<JPanel>> sousList, Dimension dimensionImgScroll){
		super();

		this.buttons = list;
		this.panels = sousList;
		this.dimensionImgScroll = dimensionImgScroll;

		// Gestion du layout et de ses contraintes
		setLayout(new GridBagLayout());
		contraintes = new GridBagConstraints();
		contraintes.gridx = 0;
		contraintes.gridy = GridBagConstraints.RELATIVE;

		//Remplissage du panel
		refermerListePerso(-1, false);
		revalidate();
	}

	public void refermerListePerso(int index, boolean ouvrir){
		removeAll();

		for (int i = 0; i < buttons.size(); i++){
			buttons.get(i).setPreferredSize(dimensionImgScroll);
			buttons.get(i).setFocusPainted(false);

			add(buttons.get(i), contraintes);

			if ((panels != null) && (ouvrir))
				if (i == index){
					List<JPanel> list = panels.get(index);

					for(JPanel panel : list){
						panel.setPreferredSize(dimensionImgScroll);
						add(panel, contraintes);
					}
					
					setSize(new Dimension(400, (buttons.size() + panels.get(i).size()) * 70));
				}
		}

		revalidate();
	}
}
