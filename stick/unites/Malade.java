package unites;

import java.util.LinkedList;


import Statut.*;
import application.Fx_Unite;
import controleur.Game;
import carte.*;

public class Malade extends Unite implements Unite.Range{

	private static final long serialVersionUID = 1L;
	private LinkedList<Virus> victime;
	
	public Malade(Hexagone place, Fx_Unite Unite,int camp) {
		super("Le Malade", 3, 150, 0, 10,10, 5, place, Unite, camp);
		set_Skill(new Infec(),new Explosion(),new Vommissement());
		this.victime=new LinkedList<Virus>();
	}
	public Malade() {
		this(null,null,-1);
	}
	public String[] getStats() {
		if(victime!=null) {
			stats[6]="Infectes: "+victime.size();
		}
		return super.getStats();
	}
	public class Infec extends Skill implements Skill.Offensif{
		private int tour=5;
		private int tour_bis=2;
		{
			name="Projectile Viral";
			description="Infecte la cible du virus pour "+tour+" tours si elle n'est pas déjà infectée, lui affecte un poison pour "+tour_bis+" tour(s) sinon";
			sub_description="";
			cout=4;
			cdmax=3;
		}

		@Override
		public void setrange() {
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

		@Override
		public void use(Hexagone n) {			
			if(!available())return;
			if(n.getunite()!=null && !(n.getunite() instanceof Obstacle)) {
				boolean alone=true;
				for(Statut st:n.getunite().getStatuts()) {
					if(st instanceof Virus)alone=false;
				}
				if(alone)n.getunite().add_Statut(new Virus(tour,n.getunite(),Malade.this));
				else n.getunite().add_Statut(new Empoisonnement(tour_bis,n.getunite()));
				cd=cdmax;
			 	actualise();///// necessaire ? est appele apres updateSort dans Game.java
			}
		}
	}

	public class Explosion extends Skill implements Skill.Attaque{
		private int degats=30;
		private int degats_par_ennemies=5;
		private int auto_inflict=30;
		{
			name="Cluster";
			description="Inflige "+degats+" de dégât(s) +"+degats_par_ennemies+" de dégâts pour chaque unite infectée, à toutes les cibles infectees. S'inflige "+auto_inflict+" dégâts";
			sub_description="";
			cout=6;
			cdmax=3;
		}

		@Override
		public void use(Hexagone n) {
			if(!available())return;
			for(Virus v:victime) {
				v.get_unite().inflige(degats+degats_par_ennemies*victime.size(),1);
				v.get_unite().removestatut(v);
			}
			inflige(auto_inflict,1);
			victime.clear();
			cd=cdmax;
		}
		@Override
		public void setrange() {
			LinkedList<Hexagone> p=new LinkedList<Hexagone>();
			p.add(place);
			rang=p;
		}
		
	}
	
	public class Vommissement extends Skill implements Skill.cc{
		private int tour_flaque = 3;
		private int tour_silence=2;
		private int auto_poison_silence=2;
		{
			name="Vomissement";
			description="La personne infectée vomi, créant une flaque de poison devant elle durant "+tour_flaque+" tour(s),"
					+ " lui infligeant le statut Silence pendant "+tour_silence+" tours et consommant son virus."
							+ " S'inflige les statuts Silence et Empoisonnement durant "+auto_poison_silence+" tour(s)";
			sub_description="";
			cout=5;
			cdmax=4;
		}
		
		@Override
		public void use(Hexagone n) {
			if(!available())return;
			if(n.getunite()!=null) {
				boolean not=true;
				Statut v=null;
				for(Statut st:n.getunite().getStatuts()) {
					if(st instanceof Virus) {
						not=false;
						v=st;
					}
				}
				if(not==false) {
					n.getunite().removestatut(v);
					actualise();
					if(n.getunite().getcamp()==1) {
						if(n.getdroit()!=null && n.getdroit().getterrain()==null) {
							n.getdroit().setTerrain(new Poison(tour_flaque,getcamp()));
						}
					}
					else {
						if(n.getgauche()!=null && n.getgauche().getterrain()==null) {
								n.getgauche().setTerrain(new Poison(tour_flaque,getcamp()));
						}
					}
					n.getunite().add_Statut(new Silence(tour_silence,n.getunite()));
					add_Statut(new Silence(auto_poison_silence,Malade.this));
					add_Statut(new Empoisonnement(auto_poison_silence,Malade.this));
				}
				cd=cdmax;
			}
			
		}

		@Override
		public void setrange() {
			LinkedList<Hexagone> r=new LinkedList<Hexagone>();
			for(Statut n:victime) {
				if(n.get_unite()!=null && n.get_unite().getplace()!=null) {
					r.add(n.get_unite().getplace());
				}
			}
			rang=r;
		}
	}
	@Override
	protected void degats_adversaire(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null) {
			n.getunite().add_Statut(new Empoisonnement(2, n.getunite()));
		}
	}

	@Override
	public void actu_range() {
		// TODO Auto-generated method stub
		LinkedList<Hexagone> et=new LinkedList<Hexagone>();
		et.add(place);	
		for(int i=0;i<portee;i++) {
			int pop=et.size();
			for(int o=0;o<pop;o++) {
				et=et.get(o).add_autour_withunits(et);
			}
		}
		range.removeAll(range);
		for(Hexagone m:et) {
			if(m.getunite()!=null && !(m.getunite() instanceof Obstacle)) {
				range.add(m);
			}
		}
		range.remove(place);
	}

	@Override
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
		 return 175;
	 }

	public LinkedList<Virus> getvictime() {
		return victime;
	}

	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige à la cible le statut Empoisonnement durant 2 tours";
	}
	@Override
	protected String passif() {
		return "Ses attaques n'infligent pas de dégats mais le statut Empoisonnement";
	}
	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "Le malade peut répandre son virus à travers une foule en paque comme une frontlane,"
				+ " il est idéal pour infliger de gros dégâts aux unité rapprochées mais il y sacrifie une partie de ses Pv";
	}
}
