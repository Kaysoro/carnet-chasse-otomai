package control;

import model.Joueur;
import view.InterfaceCommerce;

public class GestionnaireCommerce {

	private Gestionnaire gestionnaire;
	private InterfaceCommerce graphic;
	private Joueur joueur;
	private Joueur other;
	
	public GestionnaireCommerce(Gestionnaire gestionnaire, Joueur joueur, Joueur other) {
		super();
		this.gestionnaire = gestionnaire;
		this.joueur = joueur;
		this.other = other;
	}

	public Gestionnaire getGestionnaire() {
		return gestionnaire;
	}

	public InterfaceCommerce getGraphic() {
		return graphic;
	}

	public Joueur getJoueur() {
		return joueur;
	}

	public Joueur getOther() {
		return other;
	}
	
}
