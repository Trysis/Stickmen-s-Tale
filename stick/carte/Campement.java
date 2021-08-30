package carte;

import unites.Unite;

public class Campement extends Terrain{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int soin=25;
	{
		nom=toString();
		description="Le campement permet à l'unite qui passe dessus de se faire soigner de "+soin+" pdv, le terrain se détruit ensuite";
		sub_text="Immédiat";
	}
	public Campement() {
		super();
	}
	public Campement(int t,int camp) {
		super(t,camp);
	}

	//Le campement permet au joueur de se revigorer
	@Override
	public void effect(Unite n) {
		n.add_Pv(soin);

		hexa.getFx().removeFxTerrain();
		hexa.setTerrain(null);
		hexa=null;
	}

	@Override
	public void stopeffect(Unite n) {
		//les points de vie gagné sont permanant
	}

	@Override
	public String toString() {
		return "Campement";
	}


}
