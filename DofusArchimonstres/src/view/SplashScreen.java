package view;

import java.awt.*;

import javax.swing.*;

public class SplashScreen extends JWindow {

	private static final long serialVersionUID = 1L;
	private JProgressBar chargement;
	private long delay;
	public String messageChargement;

	/**
	 * Creates a Splash that will appear until another frame hides it, but at
	 * least during "delay" milliseconds.
	 * @param delay the delay in milliseconds
	 */
	public SplashScreen(long delay) {

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.getLookAndFeelDefaults().put("nimbusOrange", new Color(34, 177, 76));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		setSize(700, 400);
		setLocationRelativeTo(null);

		JPanel p = new JPanel(new BorderLayout());
		p.add(new SplashPicture("/img/Elements/splash.png"));
		p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
		getContentPane().add(p);

		chargement = new JProgressBar(0);
		chargement.setSize(new Dimension(600, 42));
		messageChargement = "";
		setVisible(true);
		this.delay = System.currentTimeMillis() + delay;
	}

	public void dispose(){
		long delta = delay - System.currentTimeMillis();

		if (delta > 0)
			try {
				Thread.sleep(delta);
			} catch (InterruptedException e) {}

		super.dispose();
	}


	public void setChargement(int value, String message){
		this.messageChargement = message;
		chargement.setValue(value);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
		repaint();
	}

	class SplashPicture extends JPanel {

		private static final long serialVersionUID = 1L;
		Image img;      


		public SplashPicture(String file) {
			img = new ImageIcon(getClass().getResource(file)).getImage();
			repaint(); 
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (img == null) return;
			int w = img.getWidth(this);  
			int h = img.getHeight(this);
			boolean zoom = (w > getWidth() || h > getHeight());
			if (zoom) g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
			else g.drawImage(img, (getWidth()-w)/2, (getHeight()-h)/2, this);

			//chargement.paint(g);

			g.drawImage((new ImageIcon(getClass().getResource("/img/Elements/titre.png")).getImage()), 0, 20, null);
			g.drawImage(ImageTextuelle.getImage(chargement), 50, 218, null);
			g.drawImage((new ImageIcon(getClass().getResource("/img/Elements/hand.png")).getImage()), 292, 197, null);
			g.drawImage((new ImageIcon(getClass().getResource("/img/Elements/feuillesL.png")).getImage()), 8, 195, null);
			g.drawImage((new ImageIcon(getClass().getResource("/img/Elements/feuillesR.png")).getImage()), 592, 195, null);
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.WHITE);
			g.setFont(new Font("", Font.BOLD, 15));
			FontMetrics fontMetrics = g.getFontMetrics();
			int longueurMessageChargement = fontMetrics.stringWidth(messageChargement);
			g.drawString(messageChargement, (getWidth() - longueurMessageChargement) / 2, 244);
			
		}
	}

	public JProgressBar getChargement() {
		return chargement;
	}
}