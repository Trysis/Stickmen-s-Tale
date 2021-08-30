package Statut;

import unites.Unite;

public class Brulure extends Statut {
	private int pdvmax;
	public Brulure(int tours, Unite n) {
		super(tours, n);
		if(n!=null)pdvmax=n.get_pvMax();
	}

	@Override
	void effect() {
		n.sub_Pv((int)Math.ceil(pdvmax*0.05));
		n.sub_PvMax((int)Math.ceil(pdvmax*0.05));
	}

	@Override
	public void stopeffect() {
		n.set_attaquable(true);
	}

	@Override
	public String toString() {
		return "Brulure";
	}

	@Override
	public String description() {
		return "Brûle la cible, lui réduit de 5% ses pdv et pdvMax par tour (relatif aux pdv max de la cible lors de la première application du statut)";
	}

	@Override
	public void apply() {
	}

}
