package Statut;

import unites.Unite;

public class Desarmer extends Statut {

	public Desarmer(int tours, Unite n) {
		super(tours, n);
	}

	@Override
	void effect() {
		n.set_attaquable(false);
	}

	@Override
	public void stopeffect() {
		n.set_attaquable(true);
	}

	@Override
	public String toString() {
		return "Desarmer";
	}

	@Override
	public String description() {
		return "Désarme l'unité, l'empechant donc d'attaquer";
	}

	@Override
	public void apply() {
		n.set_attaquable(false);	
	}

}
