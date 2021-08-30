package Statut;
import unites.Unite;

public class Stun extends Statut{
//Stun empeche le joueur de lancer des sorts, attaquer et bouger
	
	public Stun(int tours, Unite n) {
		super(tours, n);
	}

	void effect() {
		n.set_attaquable(false);
		n.set_movable(false);
		n.set_castable(false);
	}

	@Override
	public void stopeffect() {
		n.set_attaquable(true);
		n.set_movable(true);
		n.set_castable(true);
	}

	@Override
	public String toString() {
		return "Stun";
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "empêche la cible de bouger, de lancer de sorts, ainsi que d'attaquer";
	}

	@Override
	public
	void apply() {
		// TODO Auto-generated method stub
		n.set_attaquable(false);
		n.set_movable(false);
		n.set_castable(false);
	}

}
