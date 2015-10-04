package view;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import model.Zone;

public class JButtonZone extends JButton{

	private static final long serialVersionUID = 1L;
	private Zone zone;
	
	public JButtonZone(ImageIcon img, Zone zone) {
		super(img);
		this.zone = zone;
	}

	public Zone getZone(){
		return zone;
	}
}
