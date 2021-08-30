package unites;

import java.util.LinkedList;

import Statut.Desarmer;
import Statut.Hemoragie;
import Statut.Root;
import application.Fx_Unite;
import carte.Hexagone;

public class Guerrier extends Unite implements Unite.Cac{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Guerrier(Hexagone place,Fx_Unite fx,int camp) {
		super("Guerrier",2,275, 20,25, 15, 2, place,fx,camp);
		set_Skill(new Brise_genou(),new Courage(), new Bond());//Ajout des sorts
	
	}
	public Guerrier() {
		this(null,null,-1);
	}
	//skill 1
		class Brise_genou extends Skill implements Skill.Offensif,Skill.cc{
			private int degats=25;//Degat de la competence
			private int tours=2;//Nombre de tour du statut applique par la competence
			{
				name="Brise Genou";
				description="Brise les genoux de sa cible infligeant "+degats+" dégâts et l'empéchant de bouger durant "+tours+" tours";
				sub_description="";
				cout=3;
				cdmax=5;
			}
			public void use(Hexagone n) {
				if(available()) {
				Unite u=n.getunite();//recupere l'unite de l'hexagone
				if(u!=null && getrange().contains(u.getplace())) {//si le sort n'a pas ete lance dans la vie et que l'unite est a porte
					u.add_Statut(new Root(tours,u));//add le statut Root a l'unite pour deux tours
					u.inflige(degats,0);//degats inflige
				}
				cd=cdmax;
				}
			}
			@Override
			public void setrange() {
				rang=range;
			}
		}
////////////////
//skill 2
class Courage extends Skill implements Skill.SelfHeal{
	private int recuperation=10;//Valeur de recuperation de point de vie de la competence
	{
		name="Courage";
		description="Récupere "+recuperation+" pdv par ennemies à portée d'attaque, le temps de recuperation"
				+ " se réinitialise s'il y a 6 personnes autour de lui lors de son utilisation. Utilisable qu'une fois par tour";
		sub_description="";
		cout=5;
		cdmax=4;
	}
	@Override
	public void use(Hexagone n) {
		if(available()) {
			if(n.equals(place)) {
				int nb=0;
				for(int i=0;i<range.size();i++) {
					
					if(range.get(i).getunite()!=null && range.get(i).getunite().getcamp()!=camp) {//regarde toutes les unites a porte du camp oppose
						add_Pv(recuperation);
						nb++;
					}
				}
				if(nb==6) {
					cd=0;
				}
				else {
					cd=cdmax;
				}
			}
			used=true;
		}
	}
	@Override
	public void setrange() {
		LinkedList<Hexagone> lo=new LinkedList<Hexagone>();//creer une linkedList, ajouer la position de l'unite et la retourne
		lo.add(place);
		rang =lo;
	}
	@Override
	public boolean available() {//Ajout d'une condition "used"
		return cd==0 && !used;
	}
	}
	/////////////Skill 3//////////
	class Bond extends Skill implements Skill.Offensif{
		private int taille_saut=3;//Portee du saut
		private int degats=10;//Degats du saut en zone
		private int tour=2;//Nombre de tour du statut applique par le saut
		{
			name="Bond";
			description="Saute jusqu'à "+taille_saut+" case(s) devant ou derrière lui, infligeant "+degats+" de dégâts aux unites autour de sa case d'arrivée et désarmant le guerrier pour "+tour+" tour(s)";
			sub_description="";
			cout=6;
			cdmax=9;
		}
		@Override
		public void use(Hexagone n) {
			if(available()) {
				if(n.getunite()==null) {
					n.setunite(Guerrier.this);
					place.setunite(null);//Enleve l'unite dans l'ancien Hexagone
					setplace(n);//Set l'Hexagone de l'unite
					place.setunite(Guerrier.this);//L'ajoute au nouveau
		
					n.getFx().setFx_Unite(guiunite);
					guiunite.setHexagone(n.getFx());
					add_Statut(new Desarmer(tour, Guerrier.this));
					if(n.getgauche()!=null && n.getgauche().getunite()!=null) {
						n.getgauche().getunite().inflige(degats,1);
					}
					if(n.gethautgauche()!=null && n.gethautgauche().getunite()!=null) {
						n.gethautgauche().getunite().inflige(degats,1);
					}
					if(n.getbasgauche()!=null && n.getbasgauche().getunite()!=null) {
						n.getbasgauche().getunite().inflige(degats,1);
					}
					if(n.getdroit()!=null && n.getdroit().getunite()!=null) {
						n.getdroit().getunite().inflige(degats,1);
					}
					if(n.getbasdroit()!=null && n.getbasdroit().getunite()!=null) {
						n.getbasdroit().getunite().inflige(degats,1);
					}
					if(n.gethautdroit()!=null && n.gethautdroit().getunite()!=null) {
						n.gethautdroit().getunite().inflige(degats,1);
					}
					cd=cdmax;
					actualisation();
					actualise();
				}
			}
		}
	
		@Override
		public void setrange() {
			LinkedList<Hexagone> res=new LinkedList<Hexagone>();
			Hexagone n=place;
			int indexd=0;
			while(n.getdroit()!=null && indexd<taille_saut) {
				if(n.getdroit().getunite()==null) {
					res.add(n.getdroit());
				}
				n=n.getdroit();
				indexd++;
			}
			n=place;
			int indexg=0;
			while(n.getgauche()!=null && indexg<taille_saut) {
				if(n.getgauche().getunite()==null) {
					res.add(n.getgauche());
				}
				n=n.getgauche();
				indexg++;
			}
			rang= res;
		}
		
	}

	@Override
	protected void degats_adversaire(Hexagone n) {
		if(n.getunite()!=null) {
			n.getunite().inflige(attaque,0);//20 pV en moins en comptant l'armure
			int m=(int)Math.random()*10;//1 chance sur 10 d'infliger Hemoragie
			if(m==10) {
				n.getunite().add_Statut(new Hemoragie(2,n.getunite()));
			}
		}

	}

	
	public void actu_range() {
        range.removeAll(range);
        if(place.gethautgauche()!=null) {
            range.add(place.gethautgauche());

        }
        if(place.getgauche()!=null) {
            range.add(place.getgauche());

        }
        if(place.gethautdroit()!=null) {
            range.add(place.gethautdroit());

        }
        if(place.getdroit()!=null) {
            range.add(place.getdroit());

        }
        if(place.getbasdroit()!=null) {
            range.add(place.getbasdroit());

        }
        if(place.getbasgauche()!=null) {
            range.add(place.getbasgauche());

        }
    }
	 void actu_dep(int dep) {
	        deplacement.removeAll(deplacement);
	        deplacement.add(place);
	        for(int i=0;i<dep;i++) {
	            int pop=deplacement.size();
	            for(int o=0;o<pop;o++) {
	                deplacement=deplacement.get(o).add_autour(deplacement);
	            }
	        }
	        deplacement.remove(place);
	        
	    }

	 public static int get_Prix() {
		 return 150;
	 }


	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige "+attaque+" de dégâts physiques à l'unité et a une chance sur 10 d'infliger hémorragie pendant 2 tours";
	}
	@Override
	protected String passif() {
		return "Ses attaques ont une chance d'infliger l'effet hémorragie (1 chance sur 10)";
	}

	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "Le Guerrier est un tank très polyvalent avec une grande mobilité, du contrôle de foule et un autosoin";
	}
}

