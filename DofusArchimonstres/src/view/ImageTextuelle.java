package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JProgressBar;

public class ImageTextuelle {

	private final static Color beige = new Color(243, 227, 194);
	private final static Color vert = new Color(34, 177, 76);

	public static ImageIcon getSuperImage(String texte, int valeur, boolean estSelectionne){
		Font f = new Font("Helvetica", Font.BOLD, 24);
		Image imageFond, imageBarre;

		JProgressBar barreProgression = new JProgressBar(0, 100);
		barreProgression.setSize(new Dimension(380, 10));
		barreProgression.setValue(valeur);
		imageBarre = getImage(barreProgression);

		if (! estSelectionne)
			imageFond = new ImageIcon(ImageTextuelle.class.getResource("/img/Elements/1OFF.png")).getImage();
		else
			imageFond = new ImageIcon(ImageTextuelle.class.getResource("/img/Elements/1ON.png")).getImage();

		int width = imageFond.getWidth(null);
		int height = imageFond.getHeight(null);

		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimg.createGraphics();
		g.drawImage(imageFond, 0, 0, null);
		g.drawImage(imageBarre, 10, 50, null);
		
		g.setFont(f);
		
		// Longueur et largeur de l'écriture
		FontMetrics fontMetrics = g.getFontMetrics();
		//int longueur = fontMetrics.stringWidth(texte);
		int hauteur = fontMetrics.getHeight();

		if (! estSelectionne)
			g.setColor(vert);
		else
			g.setColor(beige);

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.drawString(texte, 5, (hauteur * 3 / 4) + ((height - hauteur) / 2));
		// Pour centrer sur la longueur : (width - longueur) / 2

		return new ImageIcon(bimg);
	}

	public static ImageIcon getSuperImagePass(String texte, int valeur, boolean estSelectionne){
		Font f = new Font("Helvetica", Font.BOLD, 24);
		Image imageFond, imageBarre;

		JProgressBar barreProgression = new JProgressBar(0, 100);
		barreProgression.setSize(new Dimension(380, 10));
		barreProgression.setValue(valeur);
		imageBarre = getImage(barreProgression);

		if (! estSelectionne)
			imageFond = new ImageIcon(ImageTextuelle.class.getResource("/img/Elements/1passOFF.png")).getImage();
		else
			imageFond = new ImageIcon(ImageTextuelle.class.getResource("/img/Elements/1passON.png")).getImage();

		int width = imageFond.getWidth(null);
		int height = imageFond.getHeight(null);

		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimg.createGraphics();
		g.drawImage(imageFond, 0, 0, null);
		g.drawImage(imageBarre, 10, 50, null);
		g.setFont(f);

		// Longueur et largeur de l'écriture
		FontMetrics fontMetrics = g.getFontMetrics();
		//int longueur = fontMetrics.stringWidth(texte);
		int hauteur = fontMetrics.getHeight();

		if (! estSelectionne)
			g.setColor(vert);
		else
			g.setColor(beige);

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.drawString(texte, 5, (hauteur * 3 / 4) + ((height - hauteur) / 2));

		return new ImageIcon(bimg);
	}
	
	public static ImageIcon makeStats(ImageIcon image, int valeur){
		Image imageBarre, imageFond = image.getImage();

		int width = imageFond.getWidth(null);
		int height = imageFond.getHeight(null);
		
		JProgressBar barreProgression = new JProgressBar(0, 100);
		if (width == 400)
			barreProgression.setSize(new Dimension(380, 10));
		else
			barreProgression.setSize(new Dimension(330, 10));
		barreProgression.setValue(valeur);
		imageBarre = getImage(barreProgression);

		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimg.createGraphics();
		g.drawImage(imageFond, 0, 0, null);
		g.drawImage(imageBarre, 10, 50, null);
		
		return new ImageIcon(bimg);
	}

	public static ImageIcon getSousImage(String texte, int valeur, boolean estSelectionne){
		Font f = new Font("Helvetica", Font.BOLD, 17);
		Image imageFond, imageBarre;

		JProgressBar barreProgression = new JProgressBar(0, 100);
		barreProgression.setSize(new Dimension(330, 10));
		barreProgression.setValue(valeur);
		imageBarre = getImage(barreProgression);

		if (! estSelectionne)
			imageFond = new ImageIcon(ImageTextuelle.class.getResource("/img/Elements/2OFF.png")).getImage();
		else
			imageFond = new ImageIcon(ImageTextuelle.class.getResource("/img/Elements/2ON.png")).getImage();

		int width = imageFond.getWidth(null);
		int height = imageFond.getHeight(null);

		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimg.createGraphics();
		g.drawImage(imageFond, 0, 0, null);
		g.drawImage(imageBarre, 10, 50, null);
		g.setFont(f);

		// Longueur et largeur de l'écriture
		FontMetrics fontMetrics = g.getFontMetrics();
		//int longueur = fontMetrics.stringWidth(texte);
		int hauteur = fontMetrics.getHeight();

		if (! estSelectionne)
			g.setColor(vert);
		else
			g.setColor(beige);

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.drawString(texte, 5, (hauteur * 3 / 4) + ((height - hauteur) / 2));
		// g.drawString(texte, (width - longueur) / 2, (hauteur * 3 / 4) + ((height - hauteur) / 2));
		return new ImageIcon(bimg);
	}

	public static ImageIcon getSousImagePass(String texte, int valeur, boolean estSelectionne){
		Font f = new Font("Helvetica", Font.BOLD, 17);
		Image imageFond, imageBarre;

		JProgressBar barreProgression = new JProgressBar(0, 100);
		barreProgression.setSize(new Dimension(330, 10));
		barreProgression.setValue(valeur);
		imageBarre = getImage(barreProgression);

		if (! estSelectionne)
			imageFond = new ImageIcon(ImageTextuelle.class.getResource("/img/Elements/2passOFF.png")).getImage();
		else
			imageFond = new ImageIcon(ImageTextuelle.class.getResource("/img/Elements/2passON.png")).getImage();

		int width = imageFond.getWidth(null);
		int height = imageFond.getHeight(null);

		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimg.createGraphics();
		g.drawImage(imageFond, 0, 0, null);
		g.drawImage(imageBarre, 10, 50, null);
		g.setFont(f);

		// Longueur et largeur de l'écriture
		FontMetrics fontMetrics = g.getFontMetrics();
		//int longueur = fontMetrics.stringWidth(texte);
		int hauteur = fontMetrics.getHeight();

		if (! estSelectionne)
			g.setColor(vert);
		else
			g.setColor(beige);

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.drawString(texte, 5, (hauteur * 3 / 4) + ((height - hauteur) / 2));
		// g.drawString(texte, (width - longueur) / 2, (hauteur * 3 / 4) + ((height - hauteur) / 2));

		return new ImageIcon(bimg);
	}

	public static Image getImage(JComponent component){
		if(component==null)
			return null;

		int width = component.getWidth();
		int height = component.getHeight() - 1;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		component.paint(g);
		g.dispose();
		return image;
	}
}
