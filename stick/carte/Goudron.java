package carte;

import unites.Unite;

public class Goudron extends Terrain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int reduction_dep=1;
	{
		nom=toString();
		description="La flaque de goudron baisse l'armure et ralentit le joueur (lui enlève "+reduction_dep+" de déplacement)";
		sub_text="Immédiat";
	}
	public Goudron() {
		super();
	}	
	public Goudron(int t,int camp) {
		super(t,camp);
	}

	//la flaque de goudron baisse le defense et ralenti le joueur (argument à rajouté dans unités potentiellement)
	@Override
	public void effect(Unite n) {
		n.sub_defensearmore(25);
		n.sub_deplacement(reduction_dep);
	}

	@Override
	public void stopeffect(Unite n) {
		n.add_defensearmore(25);
		n.add_deplacement(reduction_dep);
	}

	@Override
	public String toString() {
		return "Goudron";
	}

	

}
