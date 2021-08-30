package unites;

import java.util.LinkedList;

import application.Fx_Unite;
import carte.Hexagone;

public class Faucheuse extends Unite implements Unite.Cac{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Faucheuse(Hexagone place,Fx_Unite fx,int camp) {
		super("faucheuse", 3, 170, 30,10, 25, 2, place, fx, camp);
		set_Skill(new Tourbillon(),new La_Mort());
	}
	public Faucheuse() {
		this(null,null,-1);
	}
	public class La_Mort extends Skill implements Skill.Offensif{
		private double pourcentage=5.0/100.0;
		private int portee=3;
		{
			name="Death Time";
			description="Fauche l'adversaire lui infligeant des dégâts equivalent à "+pourcentage*100+"% des pdv actuelles de la cible (traverse l'armure)";
			sub_description="";
			cout=5;
			cdmax=3;
		}
		@Override
		public void use(Hexagone n) {
			if(!available());
			
			Unite u = n.getunite();
			if(u!=null) {
			int x =u.get_pv();
			u.sub_Pv((int)Math.floor(x*pourcentage));
			cd=cdmax;
			}
		}
		@Override
		public void setrange() {
			LinkedList<Hexagone> lo=new LinkedList<>();//
			place.add_autour_withunits(lo);
			for(int i=0;i<portee;i++) {
				int pop=lo.size();
				for(int o=0;o<pop;o++) {
					lo=lo.get(o).add_autour_withunits(lo);
				}
			}
			lo.remove(place);
			rang=lo;
		}
	}
	
	public class Tourbillon extends Skill implements Skill.Degat_autour{
		private int degats=attaque;
		{
			name="Tourbillon";
			description="Découpe tous les ennemies autour de la Faucheuse et leur inflige son attaque ("+degats+")";
			sub_description="";
			cout=4;
			cdmax=3;
		}

		@Override
		public void use(Hexagone n) {//Futurement ajouter une liste des hexagones qui sont cibles
			if(!available())return;
			if(!n.getunite().equals(Faucheuse.this))return;
			if(n.getgauche()!=null && n.getgauche().getunite()!=null)n.getgauche().getunite().inflige(degats,1);
			if(n.gethautgauche()!=null && n.gethautgauche().getunite()!=null)n.gethautgauche().getunite().inflige(degats,1);
			if(n.getbasgauche()!=null && n.getbasgauche().getunite()!=null)n.getbasgauche().getunite().inflige(degats,1);
			if(n.getdroit()!=null && n.getdroit().getunite()!=null)n.getdroit().getunite().inflige(degats,1);
			if(n.getbasdroit()!=null && n.getbasdroit().getunite()!=null)n.getbasdroit().getunite().inflige(degats,1);
			if(n.gethautdroit()!=null && n.gethautdroit().getunite()!=null)n.gethautdroit().getunite().inflige(degats,1);
			cd=cdmax;
		}
		@Override
		public void setrange() {
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			rang= depla;
		}	
	}
	@Override
	protected void degats_adversaire(Hexagone n) {
		if(n.getunite()!=null && getrange().contains(n.getunite().getplace())) {
			n.getunite().inflige(attaque,0);
		}
	}

	@Override
	public void actu_range() {
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
		 return 200;
	 }

	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige "+attaque+" de dégâts physiques à la cible";
	}
	@Override
	protected String passif() {
		return "Aucun passif";
	}
	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "La faucheuse est une unité au corps à corps qui inflige des dégâts proportionels aux pvMax, parfait contre les tanks";
	}

}
