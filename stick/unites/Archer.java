package unites;

import java.util.LinkedList;

import Statut.Brulure;
import Statut.Empoisonnement;

import application.Fx_Unite;
import carte.Hexagone;


public class Archer extends Unite implements Unite.Range{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Recharge recharge=new Recharge();
	private int fleches;
	private int nbFleches;
	
	public Archer(Hexagone place,Fx_Unite fx,int camp) {
		super("archer",3,150, 30, 15,15, 5, place,fx,camp);
		this.fleches=3;//A peut-etre modifie
		this.nbFleches=fleches;
		set_Skill(new Fleches_Poison(),new Fleches_2(),new Fleches_3());
	}
	public Archer() {
		this(null,null,-1);
	}
	public String[] getStats() {
		stats[6]="fleches: "+fleches;
		return super.getStats();
	}
	public void recharge_fleche() {
		fleches=nbFleches;
	}
	public class Recharge extends Skill implements Skill.Attaque,Skill.Offensif{
		{
			name="Recharge";
			description="Récupère toutes ses fleches, 1 utilisation par tour max";
			sub_description="";
			cout=1;
			cdmax=1;
		}
		@Override
		public void use(Hexagone n) {
			recharge_fleche();
			cd=cdmax;
		}
		@Override
		public void setrange() {
			LinkedList<Hexagone> lo=new LinkedList<>();//
			lo.add(place);
			rang=lo;
		}
	}

	public class Fleches_Poison extends Skill implements Skill.Attaque,Skill.Offensif{
		private int degats=20;
		private int tour=3;
		private int nb_fleche=1;
		{
			name="Flèche empoisonnée";
			description="Décoche une flèche empoisonnée infligeant "+degats+" de dégats et empoisonnement pendant "+tour+" tours a l'adversaire"
					+ ", lorsqu'il n'y a pas assez de flèches disponibles pour utiliser la compétence devient la compétence "+recharge.getName();
			sub_description="";
			cout=4;
			cdmax=1;
		}

		@Override
		public String getName() {
			if(fleches<nb_fleche)return recharge.getName();
			return name;
		}
		@Override
		public String description() {
			if(fleches<nb_fleche)return recharge.description();
			return description;
		}

		@Override
		public int cost() {
			if(fleches<nb_fleche)return recharge.cost();
			return cout;
		}
		@Override
		public int getCd() {
			if(fleches<nb_fleche)return recharge.getCd();
			return cout;
		}
		@Override
		public void use(Hexagone n) {
			if(!available())return;
			Unite u=n.getunite();
			if(u!=null && n.equals(place) && getrange().contains(u.getplace()) && fleches<nb_fleche) {
				recharge.use(n);
				return;
			}
			if(u!=null && getrange().contains(u.getplace())) {
				u.add_Statut(new Empoisonnement(tour,u));
				u.inflige(degats,1);
			}
			cd=cdmax;
			fleches-=nb_fleche;
		}
		@Override
		public void setrange() {
			if(fleches<nb_fleche) {
				recharge.setrange();
				rang=recharge.getrange();
				return;
			}
			rang=range;
		}
		@Override
		public boolean available(){
			if(fleches<nb_fleche)return recharge.available();
			return cd==0 && fleches>=nb_fleche;
		}
		public void tours() {
			if(cd>0)cd--;
			setrange();
			recharge.tours();//Inacessible autrement ?
		}
	}
	
	public class Fleches_2 extends Skill implements Skill.Attaque{
		private int degats=20;
		private int nb_fleche=2;
		{
			name="Double Flèches";
			description="Lance "+nb_fleche+" flèches, pour infliger "+degats*nb_fleche+" point(s) de degat(s), lorsque non utilisable devient la compétence "+recharge.getName()+"";
			sub_description="";
			cout=4;
			cdmax=0;
		}

		@Override
		public String getName() {
			if(fleches<nb_fleche)return recharge.getName();
			return name;
		}
		@Override
		public String description() {
			if(fleches<nb_fleche)return recharge.description();
			return description;
		}
		@Override
		public int cost() {
			if(fleches<nb_fleche)return recharge.cout;
			return cout;
		}
		@Override
		public int getCd() {
			if(fleches<nb_fleche)return recharge.getCd();
			return cout;
		}
		@Override
		public boolean available(){//Est techniquement toujours "available"
			if(fleches<nb_fleche)return recharge.available();
			return fleches>=nb_fleche && cd==0;
		}
		@Override
		public void setrange() {
			if(fleches<nb_fleche) {
				recharge.setrange();
				rang=recharge.getrange();
				return;
			}
			rang=range;
		}
		@Override
		public void use(Hexagone n) {
			if(!available())return;
			Unite u=n.getunite();
			if(u!=null && n.equals(place) && getrange().contains(u.getplace()) && fleches<nb_fleche) {
				recharge.use(n);
				return;
			}
			if(u!=null && getrange().contains(u.getplace())) {
				if(fleches>=nb_fleche) {//2 fleches ou plus
					u.inflige(degats,0);
				}
			}

			if(fleches>=nb_fleche)fleches-=nb_fleche;
			cd=cdmax;
		}
	}
	
	public class Fleches_3 extends Skill implements Skill.Attaque{
		private int degats=34;
		private int nb_fleche=1;
		private int tours=5;
		{
			name="Flèche explosive";
			description="Décoche une flèche, infligeant "+degats+" de dégats à la cible et lui appliquant l'état Brulure et infligeant "+degats+" de dégats aux unités adjacentes";
			sub_description="";
			cout=5;
			cdmax=5;
		}

		@Override
		public String getName() {
			if(fleches<nb_fleche)return recharge.getName();
			return name;
		}
		@Override
		public String description() {
			if(fleches<nb_fleche)return recharge.description();
			return description;
		}
		@Override
		public int cost() {
			if(fleches<nb_fleche)return recharge.cout;
			return cout;
		}
		@Override
		public int getCd() {
			if(fleches<nb_fleche)return recharge.getCd();
			return cd;
		}
		@Override
		public boolean available(){
			if(fleches<nb_fleche)return recharge.available();
			return fleches>=nb_fleche && cd==0;
		}
		@Override
		public void setrange() {
			if(fleches<nb_fleche) {
				recharge.setrange();
				rang=recharge.getrange();
				return;
			}
			rang=range;
		}
		@Override
		public void use(Hexagone n) {
			if(!available())return;
			Unite u=n.getunite();
			if(u!=null && getrange().contains(u.getplace()) && fleches<nb_fleche) {
				recharge.use(n);
				return;
			}
			if(u!=null && getrange().contains(u.getplace())) {
				if(fleches>0) {//3 fleches et plus
					u.inflige(degats,1);
					u.add_Statut(new Brulure(tours,u));//add le statut Root a l'unite pour deux tours
				}
			}
			if(n.gethautgauche()!=null) {
				u=n.gethautgauche().getunite();
				if(u!=null)u.inflige(degats,1);
			}
			if(n.gethautdroit()!=null) {
				u=n.gethautdroit().getunite();
				if(u!=null)u.inflige(degats,1);
			}
			if(n.getgauche()!=null) {
				u=n.getgauche().getunite();
				if(u!=null)u.inflige(degats,1);
			}
			if(n.getdroit()!=null) {
				u=n.getdroit().getunite();
				if(u!=null)u.inflige(degats,1);
			}
			if(n.getbasgauche()!=null) {
				u=n.getbasgauche().getunite();
				if(u!=null)u.inflige(degats,1);
			}
			if(n.getbasdroit()!=null) {
				u=n.getbasdroit().getunite();
				if(u!=null)u.inflige(degats,1);
			}
			cd=cdmax;
			if(fleches>=nb_fleche)fleches-=nb_fleche;
		}
	}
	
	@Override
	protected void degats_adversaire(Hexagone n) {
		// TODO Auto-generated method stub
		if(fleches>0) {
			if(n.getunite()!=null && getrange().contains(n.getunite().getplace()))n.getunite().inflige(attaque,0);
			fleches--;
		}
	}

	@Override
	public void actu_range() {
		range.removeAll(range);
		if(fleches>0) {
		range.add(place);	
		for(int i=0;i<portee;i++) {
			int pop=range.size();
			for(int o=0;o<pop;o++) {
				range=range.get(o).add_autour_withunits(range);
			}
		}
		range.remove(place);
		}
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
	public static int get_Prix() {
		 return 150;
	 }

	@Override
	protected String description_attaque() {
		return "Inflige "+ attaque+" physique et utilise une fleche" ;
	}
	@Override
	protected String passif() {
		return "Ses attaques depenses des fleches, necessaires pour attaquer";
	}
	@Override
	protected String description_encyclopedie() {
		return "L'archer est une unité spécalisé dans les degats à distance, ses sorts et ses attaques utilise des flèches qu'il doit recharger lorsque sa réserve est épuisé";
	}
	

}
