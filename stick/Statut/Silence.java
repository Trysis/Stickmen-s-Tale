package Statut;
import unites.Unite;

public class Silence extends Statut {
public Silence(int tours, Unite n) {
		super(tours, n);
		// TODO Auto-generated constructor stub
	}

	//silence empeche de lancer des sorts
	@Override
	void effect() {
		n.set_castable(false);
	}

	@Override
	public void stopeffect() {
		n.set_castable(true);
	}

	@Override
	public String toString() {
		return "Silence";
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "empêche la cible de lancer des sorts";
	}

	@Override
	public
	void apply() {
		n.set_castable(false);	
	}

}
