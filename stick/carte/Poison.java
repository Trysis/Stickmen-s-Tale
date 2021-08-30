package carte;

import Statut.Empoisonnement;
import unites.Unite;

public class Poison extends Terrain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int nb_de_tour=3;
	{
		nom=toString();
		description="Empoisonne l'unité qui rentre dedans pendant "+nb_de_tour+" tours";
		sub_text="Fin de tour";
	}
	public Poison() {
		super();
	}
	public Poison(int t,int camp) {
		super(t,camp);
	}

	//attention le poison ca tue
	@Override
	public void effect(Unite n) {
        n.add_Statut(new Empoisonnement(nb_de_tour,n));
	}

	@Override
	public void stopeffect(Unite n) {
	}

	@Override
	public String toString() {
		return "Poison";
	}

}
