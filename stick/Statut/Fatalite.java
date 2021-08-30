package Statut;

import unites.Unite;

public class Fatalite extends Statut {

	public Fatalite(int tours, Unite n) {
		super(tours, n);
	}

	@Override
	void effect() {
	}

	@Override
	public void stopeffect() {
	}

	@Override
	public void apply() {
	}

	@Override
	public String toString() {
		return "Fatalité";
	}

	@Override
	public String description() {
		return "Inflige 70 dégâts magique à la fin du statut";
	}
	public void tours(){
		if(n!=null) {//regarde si le statut n'est pas mort
			if(tours>0) {//vérifie qu'il reste des tours dispo
				effect();//afflige l'effet à l'unité en argument
				tours--;//enleve un tours
			}
			if(tours<=0) {//si le statut est fini
				n.inflige(70,1);//rétablie les stats si necessaire
				n.supprstatue(this);//supprime le statut de la list de statut de l'unité n
				n=null;//set à null le n en argument pour ne plus lui affliger de debuff	
			}
		}
	}
}
