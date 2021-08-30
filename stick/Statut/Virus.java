package Statut;

import carte.Hexagone;
import unites.Malade;
import unites.Obstacle;
import unites.Unite;

public class Virus extends Statut {
	Unite m;
	public Virus(int tours, Unite n, Unite malade) {
		super(tours, n);
		m=malade;
		if(malade instanceof Malade && !(n instanceof Obstacle)) {
		((Malade) m).getvictime().add(this);
		}
		else {
			n=null;
		}
	}
	public Virus(int tours,Unite n) {
		super(tours,n);
	}

	@Override
	void effect() {
		if(n!=null) {
		Hexagone p=n.getplace();
		if(p==null)return;
			if(p.getdroit()!=null && p.getdroit().getunite()!=null) {
				boolean alone=true;
				for(Statut st:p.getdroit().getunite().getStatuts()) {
					if(st instanceof Virus) {
						alone=false;
					}
				}
				if (alone)p.getdroit().getunite().add_Statut(new Virus(6,p.getdroit().getunite(),m));
			}
			
			if(p.gethautdroit()!=null && p.gethautdroit().getunite()!=null) {
				boolean alone=true;
				for(Statut st:p.gethautdroit().getunite().getStatuts()) {
					if(st instanceof Virus) {
						alone=false;
					}
				}
				if (alone)p.gethautdroit().getunite().add_Statut(new Virus(6,p.gethautdroit().getunite(),m));
			}
			
			if(p.getbasdroit()!=null && p.getbasdroit().getunite()!=null) {
				boolean alone=true;
				for(Statut st:p.getbasdroit().getunite().getStatuts()) {
					if(st instanceof Virus) {
						alone=false;
					}
				}
				if (alone)p.getbasdroit().getunite().add_Statut(new Virus(6,p.getbasdroit().getunite(),m));
			}
			
			if(p.getgauche()!=null && p.getgauche().getunite()!=null) {
				boolean alone=true;
				for(Statut st:p.getgauche().getunite().getStatuts()) {
					if(st instanceof Virus) {
						alone=false;
					}
				}
				if (alone)p.getgauche().getunite().add_Statut(new Virus(6,p.getgauche().getunite(),m));
			}
			
			if(p.gethautgauche()!=null && p.gethautgauche().getunite()!=null) {
				boolean alone=true;
				for(Statut st:p.gethautgauche().getunite().getStatuts()) {
					if(st instanceof Virus) {
						alone=false;
					}
				}
				if (alone)p.gethautgauche().getunite().add_Statut(new Virus(6,p.gethautgauche().getunite(),m));
			}
			
			if(p.getbasgauche()!=null && p.getbasgauche().getunite()!=null) {
				boolean alone=true;
				for(Statut st:p.getbasgauche().getunite().getStatuts()) {
					if(st instanceof Virus) {
						alone=false;
					}
				}
				if (alone)p.getbasgauche().getunite().add_Statut(new Virus(6,p.getbasgauche().getunite(),m));
			}
		}
			
	}

	@Override
	public void stopeffect() {
		// TODO Auto-generated method stub
		if(n!=null) {
		n.add_defensemagique(10);
		}
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		if(n!=null) {
		n.sub_defensemagique(10);
		}
	}

	@Override
	public String toString() {
		return "Virus";
	}

	@Override
	public String description() {
		// TODO Auto-generated method stub
		return "Réduit la défense magique de 10, se propage à tous les voisins à chaque tour";
	}

}
