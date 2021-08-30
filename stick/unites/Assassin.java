package unites;

import java.util.LinkedList;

import Statut.Fatalite;
import Statut.Insensibiliter;
import application.Fx_Unite;
import carte.Hexagone;

public class Assassin extends Unite implements Unite.Cac {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Assassin(Hexagone place,Fx_Unite fx,int camp) {
		super("Assassin",3,190,24,30, 25, 1, place,fx,camp);
		set_Skill(new Dash(),new Recul(), new Insensible());
	}
	public Assassin() {
		this(null,null,-1);
	}
	
	public class Dash extends Skill implements Skill.Offensif{
		LinkedList<Hexagone> et=new LinkedList<Hexagone>();
		{	
			name="Dash";
			description="Choisis une Unité et se téléporte derriere elle, infligeant la moitié de son attaque";
			sub_description="";
			cout=5;
			cdmax=4;
		}
		@Override
		public void use(Hexagone n) {
			// TODO Auto-generated method stub
			if(n.getunite()!=null) {
				if(n.getunite().getcamp()==1 && n.getunite().getplace()!=null && n.getunite().getplace().getgauche()!=null && n.getunite().getplace().getgauche().getunite()==null) {
					n.getunite().inflige(attaque,0);
					deplacementf(n.getunite().getplace().getgauche());
				}
				else if(n.getunite().getcamp()==2 && n.getunite().getplace()!=null && n.getunite().getplace().getdroit()!=null && n.getunite().getplace().getdroit().getunite()==null) {
					n.getunite().inflige(attaque,0);
					deplacementf(n.getunite().getplace().getdroit());
				}
			}
			cd=cdmax;
		}

		@Override
		public void setrange() {
			LinkedList<Hexagone> et=new LinkedList<Hexagone>();
			et.add(place);	
			for(int i=0;i<6;i++) {
				int pop=et.size();
				for(int o=0;o<pop;o++) {
					et=et.get(o).add_autour_withunits(et);
				}
			}
			rang=new LinkedList<Hexagone>();
			for(Hexagone m:et) {
				if(m.getunite()!=null) {
					rang.add(m);
				}
			}
			rang.remove(place);
		}
	}
	
	public class Recul extends Skill implements Skill.cc, Skill.Attaque{
		int trs=3;
		int c=0;
		boolean t=false;
		
		{	
			name="Coup Fatal";
			description="Inflige le statut Fatalité à une unité";
			sub_description="";
			cout=3;
			cdmax=4;
		}
	
		@Override
		public void use(Hexagone n) {
			// TODO Auto-generated method stub
			if(n.getunite()!=null && !(n.getunite() instanceof Obstacle)) {
				n.getunite().add_Statut(new Fatalite(3,n.getunite()));
				cd=cdmax;
			}
		}

		@Override
		public void setrange() {
			// TODO Auto-generated method stub
			rang=new LinkedList<Hexagone>();
			if(place!=null) {
				place.add_autour_withunits(rang);
			}
		}
	}
	
	public class Insensible extends Skill implements Skill.Buff{
		
		{	
			name="Ecran de fumée";
			description="Devient insensible durant 2 tours";
			sub_description="";
			cout=3;
			cdmax=4;
		}
	
		@Override
		public void use(Hexagone n) {
			// TODO Auto-generated method stub
			if(n.getunite()!=null) {
				n.getunite().add_Statut(new Insensibiliter(2,n.getunite()));
			}
		}

		@Override
		public void setrange() {
			// TODO Auto-generated method stub
			rang=new LinkedList<Hexagone>();
			if(place!=null) {
				rang.add(place);
			}
		}
	}
	
	@Override
	protected void degats_adversaire(Hexagone n) {
		// TODO Auto-generated method stub
		n.getunite().inflige(attaque,0);	
		if(n.getunite()!=null) {
			if((n.getunite().getcamp()==1 && place==n.getgauche()) || (n.getunite().getcamp()==2 && place==n.getdroit())) {
				n.getunite().inflige(attaque,0);
			}
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
	

	public static  int get_Prix() {
		 return 150;
	 }


	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige "+attaque+" degat(s) physique(s), attaque deux fois si l'attaque set fait dans le dos de la cible";
	}


	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "L'assasin est une bonne unité pour se ruer rapidement sur les unités fragiles et les tuer rapidement";
	}

	@Override
	protected String passif() {
		return "Aucun passif";
	}
}

