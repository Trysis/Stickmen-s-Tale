package Statut;

import unites.Unite;

public class Insensibiliter extends Statut {

	public Insensibiliter(int tours, Unite n) {
		
		super(tours, n);
		// TODO Auto-generated constructor stub
	}

	@Override
	void effect() {
		// TODO Auto-generated method stub
		n.set_castable(true);
		n.set_movable(true);
		n.set_attaquable(true);
		n.set_insensible(true);
	}

	@Override
	public void stopeffect() {
		// TODO Auto-generated method stub
		
		n.set_insensible(false);
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		n.set_castable(true);
		n.set_movable(true);
		n.set_attaquable(true);
		n.set_insensible(true);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Insensibilité";
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "L'unité ne peut pas être bougée et n'est pas sensible au statut incapabilisant";
	}

}
