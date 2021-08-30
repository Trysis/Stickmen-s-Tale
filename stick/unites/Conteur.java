package unites;

import java.util.LinkedList;

import Statut.Hemoragie;
import Statut.Insensibiliter;
import Statut.Root;
import application.Fx_Unite;
import controleur.Game;
import carte.Hexagone;
import carte.Marque;

public class Conteur extends Unite implements Unite.Range{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int auditoire;
	int bonusattaque;
	public Conteur(Hexagone place,Fx_Unite Unite, int camp) {
		super("Conteur", 3, 175, 30, 15, 15, 5, place, Unite, camp);
		// TODO Auto-generated constructor stub
		set_Skill(new Coupure(),new Terrifiante(),new Apocalypse());
		if(camp==1) {
			others.add(place.gethautdroit());
			place.gethautdroit().setunite(this);
		}
		else if(camp==2) {
			others.add(place.gethautgauche());
			place.gethautgauche().setunite(this);
		}
		if(place==null)return;
		setauditoire();
		actualisation();
	}
	public Conteur() {
		this(null,null,-1);
	}
	
public String[] getStats() {
		
		stats[6]="Auditoire: "+auditoire;
		return super.getStats();
	}
public void setauditoire() {
	auditoire=0;
	sub_attaque(bonusattaque);
	bonusattaque=0;
	LinkedList<Hexagone> et=new LinkedList<Hexagone>();
	et.add(place);	
	for(int i=0;i<2;i++) {
		int pop=et.size();
		for(int o=0;o<pop;o++) {
			et=et.get(o).add_autour_withunits(et);
		}
	}
	et.remove(place);
	for(Hexagone m:et) {
		if(m.getunite()!=null && m.getunite()!=this) {
			auditoire++;
		}
	}
	bonusattaque=5*auditoire;
	add_attaque(bonusattaque);
}
public void actualisation() {//actualise la portée et les déplacements pour les faire coincider avec le pourtour de l'unité
		actu_range();
		actu_dep(dep);
		setauditoire();
		for(Skill e:skillList) {
			e.setrange();
		}
}
	public void placen(Hexagone n) {//Methode utiliser pour redefinir dans les unités double 
		   this.place=n;
		   others=new LinkedList<Hexagone>();
		   if(camp==1) {
				others.add(place.gethautdroit());
			
			}
			else if(camp==2) {
				others.add(place.gethautgauche());
			}
	}
	public boolean obstruction(Hexagone n) {
		if(camp==1) {
		
			
			return n.getunite()==null && n.gethautdroit().getunite()==null;
		}
		else if(camp==2) {

			return n.getunite()==null && n.gethautgauche().getunite()==null;
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
			if(place.gethautgauche().gethautgauche()!=null && place.gethautgauche().gethautgauche().getunite()==null) {
				deplacement.add(place.gethautgauche());
			}
			if(place.getbasdroit()!=null &&  place.getbasdroit().getunite()==null) {
				deplacement.add(place.getbasdroit());
			}
			deplacement=place.add_autour_withhautgauche(deplacement);
			for(int i=0;i<dep-1;i++) {
				int pop=deplacement.size();
				for(int o=0;o<pop;o++) {
					deplacement=deplacement.get(o).add_autour_withhautgauche(deplacement);
				}
			}
			
			deplacement.remove(place);
		}
		else {
			
			deplacement=new LinkedList<Hexagone>();
			if(place.gethautdroit().gethautdroit()!=null && place.gethautdroit().gethautdroit().getunite()==null) {
				deplacement.add(place.gethautdroit());
			}
			if(place.getbasgauche()!=null &&  place.getbasgauche().getunite()==null) {
				deplacement.add(place.getbasgauche());
			}
			deplacement=place.add_autour_withhautdroit(deplacement);
			for(int i=0;i<dep-1;i++) {
				int pop=deplacement.size();
				for(int o=0;o<pop;o++) {
					deplacement=deplacement.get(o).add_autour_withhautdroit(deplacement);
				}
			}
			
			deplacement.remove(place);
		}
		
}
public static int get_Prix() {
	 return 180;
 }

public class Coupure extends Skill implements Skill.Offensif{
	{
		name="Coupure de papier";
		description="Inflige son attaque en dégâts magiques et hémorragie pour 3 tours";
		sub_description="";
		cout=4;
		cdmax=3;
	}
	@Override
	public void use(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null) {
			n.getunite().add_Statut(new Hemoragie(3,n.getunite()));
			n.getunite().inflige(attaque,1);
		}
		cd=cdmax;
	}

	@Override
	public void setrange() {
		// TODO Auto-generated method stub
		rang.removeAll(rang);
		rang.add(place);	
		for(int i=0;i<4;i++) {
			int pop=rang.size();
			for(int o=0;o<pop;o++) {
				rang=rang.get(o).add_autour_withunits(rang);
			}
		}
	}	
}
public class Terrifiante extends Skill implements Skill.cc,Skill.Attaque{
	{
		name="Histoire Terrifiante";
		description="Inflige son attaque en dégâtq magiqueq et root pendant 1 tour et se sent insensible pendant 2 tours";
		sub_description="";
		cout=4;
		cdmax=4;
	}
	@Override
	public void use(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null) {
			n.getunite().add_Statut(new Root(1,n.getunite()));
			add_Statut(new Insensibiliter(2,Conteur.this));
			n.getunite().inflige(attaque,1);
		}
	}

	@Override
	public void setrange() {
		// TODO Auto-generated method stub
		LinkedList<Hexagone> rang=new LinkedList<Hexagone>();
		LinkedList<Unite> n= Game.getarmy(1);
		LinkedList<Unite> m= Game.getarmy(2);
		for(Unite v:n) {
			if(v.mort!=true) {
				rang.add( v.getplace());
				rang.addAll(v.others);
			}
		}
		for(Unite v:m) {
			if(v.mort!=true) {
				rang.add( v.getplace());
				rang.addAll(v.others);
			}
		}
		this.rang=rang;
	}
	
	}
public class Apocalypse extends Skill implements Skill.Attaque{
	{
		name="Prophétie apocalyptique";
		description="Pose une marque sur la cible et les voisins proches, au bout de deux tours un astre tombe sur les endroits infligeants 60 dégâts magiques,"
				+ " les cases possédant un terrain ou une prophétie ne sont pas affectées par la prophétie";
		sub_description="";
		cout=6;
		cdmax=7;
	}
	@Override
	public void use(Hexagone n) {
		// TODO Auto-generated method stub
		LinkedList<Hexagone>  hexa=new LinkedList<Hexagone>();
		hexa.add(n);
		hexa=n.add_autour_withunits(hexa);
		for(Hexagone a:hexa) {
			if(a.getterrain()==null) {
				a.setTerrain(new Marque(2, camp));
			}
		}
		cd=cdmax;
	}

	@Override
	public void setrange() {
		// TODO Auto-generated method stub
		rang.removeAll(rang);
		rang.add(place);	
		for(int i=0;i<7;i++) {
			int pop=rang.size();
			for(int o=0;o<pop;o++) {
				rang=rang.get(o).add_autour_withunits(rang);
			}
		}
	}
}
@Override
protected String description_attaque() {
	// TODO Auto-generated method stub
	return "Inflige "+attaque+" physique(s) à la cible";
}
@Override
protected String passif() {
	return "Son attaque augmente de 5 pour chaque unité/obstacle à 2 de portée de lui";
}
@Override
protected String description_encyclopedie() {
	// TODO Auto-generated method stub
	return "Le Conteur est une unité qui inflige de lourds dégâts à distance, il augmente son attaque en fonction des personnes qui sont à deux de portée de lui";
}
}
