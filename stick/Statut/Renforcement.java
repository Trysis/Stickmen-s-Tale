package Statut;

import unites.Unite;

public class Renforcement extends Statut {

	public Renforcement(int tours, Unite n) {
		super(tours, n);
		// TODO Auto-generated constructor stub
	}

	@Override
	void effect() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopeffect() {
		// TODO Auto-generated method stub
		n.sub_defensearmore(25);
		n.sub_defensemagique(25);
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		n.add_defensearmore(25);
		n.add_defensemagique(25);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Renforcement";
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "Procure 25 d'armure magique et physique à l'unité";
	}

}
