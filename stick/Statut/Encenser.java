package Statut;

import unites.*;
import unites.Unite;

public class Encenser extends Statut {
	Unite lanc;
	public Encenser(int tours, Unite n,Unite m) {
		super(tours, n);
		lanc=m;		
	}
	public Encenser(int tours,Unite n) {
		super(tours,n);
	}


	@Override
	void effect() {
		n.add_Pv(17);
		if(lanc instanceof Pretresse) {
			((Pretresse) lanc).add_louange(1);
			lanc.actualise();
			}
	}

	@Override
	public void stopeffect() {
	}

	@Override
	public void apply() {
	}

	@Override
	public String toString() {
		return "Encensé";
	}

	@Override
	public String description() {
		return "L'unité se heal de 10 pv par tours";
	}

}
