package Statut;
import unites.Unite;


public class Empoisonnement extends Statut {
	
	public Empoisonnement(int tours, Unite n) {
		super(tours, n);
	}

	@Override
	void effect() {
		if(first) {
			n.sub_defensemagique(15);
			first=false;
		}
		n.sub_Pv(8);
	}

	@Override
	public void stopeffect() {
		n.add_defensemagique(15);
	}

	@Override
	public String toString() {
		return "Empoisonnement";
	}

	@Override
	public String description() {
		return "DOT infligeant 8 pv par tour et réduisant la def.magique de 15";
	}

	@Override
	public
	void apply() {
	}

}
