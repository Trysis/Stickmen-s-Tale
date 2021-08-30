package Statut;
import unites.Unite;


public class Root extends Statut {
//root (enracinement en anglais) empeche l'unité de bouger
	
	public Root(int tours, Unite n) {
		super(tours, n);
		// TODO Auto-generated constructor stub
	}

	void effect() {
		n.set_movable(false);
	}

	@Override
	public void stopeffect() {
		n.set_movable(true);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Root";
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "empêche la cible de bouger en se déplacant";
	}

	@Override
	public
	void apply() {
		n.set_movable(false);
		
	}

}
