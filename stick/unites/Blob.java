package unites;

import java.util.LinkedList;
import Statut.Desarmer;
import Statut.Renforcement;
import Statut.Stun;
import application.Fx_Unite;
import controleur.Game;
import carte.Hexagone;

public class Blob extends Unite implements Unite.Cac{
	private static final long serialVersionUID = 1L;
	private int nb_absorber=0;
	
	public Blob( Hexagone place,Fx_Unite Unite, int camp) {
		super("Blob", 3, 275, 10, 20, 25, 2, place, Unite, camp);
		set_Skill(new Coup(),new Armure(), new Assimilation());
		if(camp==1) {
			others.add(place.getdroit());
			place.getdroit().setunite(this);
		}
		else if(camp==2) {
			others.add(place.getgauche());
			place.getgauche().setunite(this);
		}
	}
	public Blob() {
		this(null,null,-1);
	}
	
	public String[] getStats() {
		stats[6]="Unité(s) absorbée(s):"+nb_absorber;
		return super.getStats();
	}
	
	public void placen(Hexagone n) {//Methode utiliser pour redefinir dans les unités double 
		   this.place=n;
		   others=new LinkedList<Hexagone>();
		   if(camp==1) {
				others.add(place.getdroit());
			
			}
			else if(camp==2) {
				others.add(place.getgauche());
			}
	}
	public boolean obstruction(Hexagone n) {
		if(camp==1) {
		
			
			return n.getunite()==null && n.getdroit().getunite()==null;
		}
		else if(camp==2) {

			return n.getunite()==null && n.getgauche().getunite()==null;
		}
		return false;
	}
	public boolean deplacementf(Hexagone x) { 
		if(mort!=true) {
			if(place!=null) {	
				if(this.guiunite!=null) {
					for(Fx_Unite n: guiunite.getOthers()) {
						n.getHexagone().removeFx_Unite();
					}
					getplace().getFx().removeFx_Unite();
					getFx_Unite().setHexagone(null);
				}
				if(this.place.getterrain()!=null) {
			       this.place.getterrain().stopeffect(this);
			    }
				this.place.setunite(null);
			    for(Hexagone m:others) {
			    	if(m.getterrain()!=null) {
				        m.getterrain().stopeffect(this);
				    }	
			    	m.setunite(null);
			    }	
			}
		    placen(x); 
		    this.place.setunite(this);
		    for(Hexagone m:others) {
		    	m.setunite(this);
		    }
		   if(this.guiunite!=null) {
		      guiunite.setX(place.getx());
		     guiunite.setY(place.gety());
		     getplace().getFx().setFx_Unite(getFx_Unite());
		     getFx_Unite().setHexagone(getplace().getFx());
	  }
	  return true;
		}
	return false;
	}
   public boolean deplacement(Hexagone x) {
		   if(deplacement.contains(x)) {
	        	if(this.place.getterrain()!=null) {
	        		this.place.getterrain().stopeffect(this);
	            }            	
	        	this.place.setunite(null);
	        	for(Hexagone m:others) {
			    	if(m.getterrain()!=null) {
				        m.getterrain().stopeffect(this);
				    }	
			    	m.setunite(null);
			    }
	            this.placen(x);
	            this.place.setunite(this);
	            for(Hexagone m:others) {
	    	    	m.setunite(this);
	    	    }
	            if(this.guiunite!=null) {
	            	guiunite.setX(place.getx());
	            	guiunite.setY(place.gety());
	            }
	            return true;
	        }
        return false;
    }

 void dcd() {
		if(!estmort()) {
			
		this.mort=true;
		this.place.setunite(null);
		place=null;
		for(Hexagone m:others) {
		
			m.setunite(null);
		}
		others=new LinkedList<Hexagone>();
		}
		actualise();
		Game.lose();
	}
 
 @Override
 protected void degats_adversaire(Hexagone n) {
 	// TODO Auto-generated method stub
 	if(n.getunite()!=null) {
 		n.getunite().inflige(attaque,0);//20 pV en moins en comptant l'armure
 	}
 }

 @Override
 public void actu_range() {
 	// TODO Auto-generated method stub
 	range.removeAll(range);
 	range.add(place);	
 	for(int i=0;i<portee;i++) {
 		int pop=range.size();
 		for(int o=0;o<pop;o++) {
 			range=range.get(o).add_autour_withunits(range);
 		}
 	}
 range.remove(place);
 }
	@Override
	void actu_dep(int dep) {
		// TODO Auto-generated method stub
		if(camp==2) {
			deplacement=new LinkedList<Hexagone>();
			deplacement.add(place);	
			
			if(place.getgauche().getgauche()!=null && place.getgauche().getgauche().getunite()==null) {
				deplacement.add(place.getgauche());
			}
			if(place.getdroit()!=null && place.getdroit().getunite()==null) {
				deplacement.add(place.getdroit());
			}
			for(int i=0;i<dep-1;i++) {
				int pop=deplacement.size();
				for(int o=0;o<pop;o++) {
					deplacement=deplacement.get(o).add_autour_withgauche(deplacement);
				}
			}
			deplacement.remove(place);
		}
		else {
			deplacement=new LinkedList<Hexagone>();
			deplacement.add(place);	
			if(place.getgauche()!=null && place.getgauche().getunite()==null) {
				deplacement.add(place.getgauche());
			}
			if(place.getdroit().getdroit()!=null && place.getdroit().getdroit().getunite()==null) {
				deplacement.add(place.getdroit());
			}
			for(int i=0;i<dep-1;i++) {
				int pop=deplacement.size();
				for(int o=0;o<pop;o++) {
					deplacement=deplacement.get(o).add_autour_withdroit(deplacement);
				}
			}
			deplacement.remove(place);
		}
	}
	public static int get_Prix() {
		 return 150;
	 }
	public class Coup  extends Skill implements Skill.cc{
		{	
			name="Coup assomant";
			description="Stun pour deux tours l'unité devant lui et lui infligant 10 de degats physiques. Désarme la cible "
					+ "et les deux unites à côté d'elle et du glob pendant 3 tours";
			sub_description="";
			cout=4;
			cdmax=3;
		}
		@Override
		public void use(Hexagone n) {
			// TODO Auto-generated method stub
			if(n.getunite()!=null) {
				cd=cdmax;
				n.getunite().add_Statut(new Stun(2,n.getunite()));
				n.getunite().inflige(10,0);
			}
			if(camp==1) {
				if(n.gethautgauche()!=null && n.gethautgauche().getunite()!=null) {
					n.gethautgauche().getunite().add_Statut(new Desarmer(3,n.gethautgauche().getunite()));
				}
				if(n.getbasgauche()!=null && n.getbasgauche().getunite()!=null) {
					n.getbasgauche().getunite().add_Statut(new Desarmer(3,n.getbasgauche().getunite()));
				}
			}
			else if(camp==2) {
				if(n.gethautdroit()!=null && n.gethautdroit().getunite()!=null) {
					n.gethautdroit().getunite().add_Statut(new Desarmer(3,n.gethautdroit().getunite()));
				}
				if(n.getbasdroit()!=null && n.getbasdroit().getunite()!=null) {
					n.getbasdroit().getunite().add_Statut(new Desarmer(3,n.getbasdroit().getunite()));
				}
			}
		}

		@Override
		public void setrange() {
			// TODO Auto-generated method stub
			rang=new LinkedList<Hexagone>();
			if(camp==1) {
				if(place.getdroit().getdroit()!=null) {
					rang.add(place.getdroit().getdroit());
				}
			}
			else if(camp==2) {
				if(place.getgauche().getgauche()!=null) {
					rang.add(place.getgauche().getgauche());
				}
			}
		}
		
	}
	public class Armure  extends Skill implements Skill.Defensif{
		{	
			name="Seconde Peau";
			description="Applique Renfort pour 3 tours à toutes les unités dans un rayon de 2 cases";
			sub_description="";
			cout=7;
			cdmax=6;
		}
		@Override
		public void use(Hexagone n) {
			// TODO Auto-generated method stub
			LinkedList<Hexagone> aa=new LinkedList<Hexagone>();
			aa.add(n);	
			for(int i=0;i<2;i++) {
				int pop=aa.size();
				for(int o=0;o<pop;o++) {
					aa=aa.get(o).add_autour_withunits(aa);
				}
			}
			aa.remove(place);
			aa.removeAll(others);
			for(Hexagone op:aa) {
				if(op.getunite()!=null && op.getunite().camp==camp) {
					op.getunite().add_Statut(new Renforcement(3,op.getunite()));
				}
			}
		}

		@Override
		public void setrange() {
			// TODO Auto-generated method stub
			rang=new LinkedList<Hexagone>();
			rang.add(place);
		}
		
	}
	public class Assimilation extends Skill implements Skill.SelfHeal, Skill.Attaque{
		{	
			name="Assimilation";
			description="Inflige 12 dégats magiques à une unité si ce coup lui est fatal, alors le lanceur augmente ses PvMax de 50 et augmente son armure physique et magique de 15";
			sub_description="";
			cout=4;
			cdmax=3;
		}

		@Override
		public void use(Hexagone n) {
			// TODO Auto-generated method stub
			if(n.getunite()!=null) {
				Unite unity=n.getunite();
				n.getunite().inflige(12,0);
				if(unity.estmort()) {
					add_PvMax(25);
					add_Pv(25);
					add_defensearmore(15);
					add_defensemagique(15);
					nb_absorber++;
				}
				cd=cdmax;
				actualisation();
				actualise();
			}
		}

		@Override
		public void setrange() {
			// TODO Auto-generated method stub
			LinkedList<Hexagone> aa=new LinkedList<Hexagone>();
			aa.add(place);
			for(int i=0;i<2;i++) {
				int pop=aa.size();
				for(int o=0;o<pop;o++) {
					aa=aa.get(o).add_autour_withunits(aa);
				}
			}
			aa.remove(place);
			aa.removeAll(others);
			rang=aa;
		}
	}
	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige "+attaque+" de dégat(s) physique à la cible";
	}
	@Override
	protected String passif() {
		return "Aucun passif";
	}
	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "Le blob est un tank de deux cases qui peut infliger des cc et buff ses alliés autour de lui, il peut aussi augmenter ses pvmax et ses stats défensives en achevant les unités";
	}
}
