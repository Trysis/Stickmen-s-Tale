package carte;

import Statut.Root;
import unites.*;

public class Trap extends Terrain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Unite lanc=null; //lanceur de l'unite
	private int nb_de_tour_sur_target=3;
	private int nb_de_tour_sur_non_target=2;
	{
		nom=toString();
		description="Immobilise la cible qui marche sur ce terrain pendant 2 tour";
		sub_text="Fin de tour";
	}
	public Trap() {
		super();
	}
	public Trap(Unite n,int t, int camp){
		super(t,camp);
		lanc=n;
		if(lanc!=null) {
			description="Immobilise la cible qui marche dessus pendant "+nb_de_tour_sur_target
					+"tours si c'est la cible du chasseur, sinon pendant "+nb_de_tour_sur_non_target+" tours";
		}
	}
	public void effect(Unite n) {
		if(lanc!=null) {
			if(lanc instanceof Chasseur) {
				if(((Chasseur)lanc).gettarget()==n) {					
					n.add_Statut(new  Root(3,n));
				}
				else {
					n.add_Statut(new  Root(2,n));
				}
			}
		}
		else {
		n.add_Statut(new  Root(1,n));
		}
		hexa.getFx().removeFxTerrain();
		hexa.setTerrain(null);
		hexa.getpla().getterrains().remove(this);
		hexa=null;
	}

	@Override
	public void stopeffect(Unite n) {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Trap";
	}

}
