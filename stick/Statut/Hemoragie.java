package Statut;
import unites.Unite;

public class Hemoragie extends Statut {
//Hemoragie fait saigner l'unité, cette blessure l'empeche d'attaquer à sa pleine puissance et lui inflige des dégats


	public Hemoragie(int tours, Unite n) {
		super(tours, n);
	}

	@Override
	void effect() {
		if(first) {
			n.sub_attaque(15);
			first=false;
		}
		n.sub_Pv(8);
	}

	@Override
	public void stopeffect() {
		n.add_attaque(15);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Hémorragie";
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "DOT infligeant 8 pv par tour et réduisant l'attaque de 15";
	}

	@Override
	public
	void apply() {
		// TODO Auto-generated method stub
		
	}

}
