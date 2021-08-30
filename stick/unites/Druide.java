package unites;

import java.util.LinkedList;

import Statut.*;
import application.Fx_Unite;
import controleur.Game;
import carte.Hexagone;

public class Druide extends Unite implements Unite.Range{
	private static final long serialVersionUID = 1L;
	private LinkedList<Unite> invoc;
	public Druide( Hexagone place, Fx_Unite Unite,int camp) {
		super("Druide", 2, 160, 15,10, 20, 4, place, Unite, camp);
		set_Skill(new Elementaire1(),new Elementaire2(), new Elementaire3());
		invoc=new LinkedList<Unite>();
	}
	public Druide() {
		this(null,null,-1);
	}
	public String[] getStats() {
		if(invoc!=null) {
			stats[6]="Elementaire(s) invoqué(s): "+invoc.size();
		}
		return super.getStats();
	}
	/////////////////////////////////////////////////
	public class Elementaire1 extends Skill implements Skill.Invocateur{
		{
			name="Elementaire de Roche";
			description="Crée un élementaire de roche (100 PV, 20 Armures) qui peut stun les unités, se déplacer et attaquer, les soins sur l'élémentaire sont deux fois moins efficaces";
			sub_description="";
			cout=6;
			cdmax=9;
		}
		//classe interne Elementaire
		public class Elemen1 extends Unite implements Skill.cc{
			public Elemen1( Hexagone place,Fx_Unite Unite, int camp) {
				super("Elementaire de Roche", 2, 100, 10,20, 20, 2, place, Unite, camp);
				set_Skill(new Eboulement());
			}

			private static final long serialVersionUID = 1L;
			
			public class Eboulement extends Skill{
				{
					name="Eboulement";
					description="Stun durant 1 tours la cible, infligeant 20 de dégâts à lui même";
					sub_description="";
					cout=3;
					cdmax=3;
				}
				@Override
				public void use(Hexagone n) {
					if(n.getunite()!=null) {
						n.getunite().add_Statut(new Stun(1,n.getunite()));
					}
					Elemen1.this.inflige(10,0);
					cd=cdmax;
				}

				@Override
				public void setrange() {
					// TODO Auto-generated method stub
					LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
					depla.add(place);	
					for(int i=0;i<2;i++) {
						int pop=depla.size();
						for(int o=0;o<pop;o++) {
							depla=depla.get(o).add_autour_withunits(depla);
						}
					}
					depla.remove(place);
					rang= depla;
				}
				
			}
			@Override
			protected void degats_adversaire(Hexagone n) {
				// TODO Auto-generated method stub
				if(n.getunite()!=null) {
					n.getunite().inflige(attaque,0);
				}
			}
			void dcd() {
				if(!estmort()) {
					this.mort=true;
					this.place.setunite(null);
					this.place=null;
					invoc.remove(this);
				}
				actualise();
			}
			public void add_Pv(int c){
		        pv+=c/2;
		        if(pv>pdvMax)pv=pdvMax;
		        actualise(); 
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
			@Override
			protected String description_attaque() {
				// TODO Auto-generated method stub
				return "Inflige "+attaque+" physique(s) à la cible";
			}
			@Override
			protected String passif() {
				return "Les soins reçus sont divisés par 2";
			}
			@Override
			protected String description_encyclopedie() {
				// TODO Auto-generated method stub
				return "Invocation du Druide";
			}
			
		}//Fin classe interne

		@Override
		public void use(Hexagone n) {
			// TODO Auto-generated method stub
			if(n.getunite()==null) {
				Fx_Unite m=new Fx_Unite(n.getFx());
				Elemen1 a=new Elemen1(n,m,camp);
				m.setNom("Elementaire de Roche");
				m.setcamp(camp);
				m.dessineImage(false);
				Game.getarmy(camp).add(a);
				invoc.add(a);
				cd=cdmax;
			}
		}

		@Override
		public void setrange() {
			// TODO Auto-generated method stub
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			for(int i=0;i<3;i++) {
				int pop=depla.size();
				for(int o=0;o<pop;o++) {
					depla=depla.get(o).add_autour(depla);
				}
			}
			depla.remove(place);
			rang= depla;
		}
		
	}
	
	public class Elementaire2 extends Skill implements Skill.Invocateur{
		{
			name="Elementaire d Eau";
			description="Crée un élémentaire d'eau (75 PV, 10 Armures) qui peut soigner les unités contre des points de vie, cette unité ne peut pas bouger ni être soignée";
			sub_description="";
			cout=6;
			cdmax=9;
		}
		
		public class Elemen2 extends Unite{
			public Elemen2( Hexagone place,Fx_Unite Unite, int camp) {
				super("Elementaire d'eau", 3, 75, 0,10, 10, 0, place, Unite, camp);
				set_Skill(new Aide());
				movable=false;
				attaquable=false;
			}

			private static final long serialVersionUID = 1L;
			
			public class Aide extends Skill implements Skill.Heal{
				{
					name="Aide de la riviere";
					description="Soigne de 25Pv et s'enlève 20Pv";
					sub_description="";
					cout=2;
					cdmax=3;
				}
				@Override
				public void use(Hexagone n) {
					// TODO Auto-generated method stub
					if(n.getunite()!=null) {
						n.getunite().add_Pv(25);
					}
					Elemen2.this.set_pv(Elemen2.this.get_pv()-20);
					cd=cdmax;
				}

				@Override
				public void setrange() {
					// TODO Auto-generated method stub
					LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
					depla.add(place);	
					for(int i=0;i<5;i++) {
						int pop=depla.size();
						for(int o=0;o<pop;o++) {
							depla=depla.get(o).add_autour_withunits(depla);
						}
					}
					depla.remove(place);
					rang= depla;
				}
				
			}
			@Override
			protected void degats_adversaire(Hexagone n) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void actu_range() {

			}
			
			public void set_attaquable(boolean n){
			    
			}
			
			public void set_movable(boolean n){

			}
			
			@Override
			void actu_dep(int dep) {
	
			}
			void dcd() {
				if(!estmort()) {
					this.mort=true;
					this.place.setunite(null);
					this.place=null;
					invoc.remove(this);
				}
				actualise();
			}
			 public void add_Pv(int c){
			    
			 }

			@Override
			protected String description_attaque() {
				// TODO Auto-generated method stub
				return "";
			}
			@Override
			protected String passif() {
				return "Ses soins lui enlève de la vie, et ne peut être soignée. Cette unité est inerte et n'inflige pas de dégâts";
			}
			@Override
			protected String description_encyclopedie() {
				// TODO Auto-generated method stub
				return "Invocation du Druide";
			}
		}
		@Override
		public void use(Hexagone n) {
			// TODO Auto-generated method stub
			if(n.getunite()==null) {
				Fx_Unite m=new Fx_Unite(n.getFx());
				Elemen2 a=new Elemen2(n,m,camp);
				m.setNom("Elementaire d'Eau");
				m.setcamp(camp);
				m.dessineImage(false);
				Game.getarmy(camp).add(a);
				invoc.add(a);
				cd=cdmax;
			}
		}
		
		@Override
		public void setrange() {
			// TODO Auto-generated method stub
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			for(int i=0;i<3;i++) {
				int pop=depla.size();
				for(int o=0;o<pop;o++) {
					depla=depla.get(o).add_autour(depla);
				}
			}
			depla.remove(place);
			rang= depla;
		}
		
	}

	public class Elementaire3 extends Skill implements Skill.Invocateur{
		{
			name="Elementaire de Feu";
			description="Crée un élémentaire de feu (65 PV, 15 Armure) qui peut brûler les unités lors de ses attaques, les soins sur l'élémentaire sont deux fois moins efficaces";
			sub_description="";
			cout=6;
			cdmax=9;
		}

		public class Elemen3 extends Unite{
			public Elemen3( Hexagone place,Fx_Unite Unite, int camp) {
				super("Elementaire de Feu", 3, 65, 25,10, 25, 4, place, Unite, camp);
				set_Skill(new Aura_de_feu());
			}

			private static final long serialVersionUID = 1L;
			
			public class Aura_de_feu extends Skill implements Skill.Offensif{
				{
					name="Aura de Feu";
					description="Inflige brûlure pour trois tours dans une zone autour de la cible, subit 7 dégâts par unité brulée";
					sub_description="";
					cout=4;
					cdmax=3;
				}
				@Override
				public void use(Hexagone n) {
					// TODO Auto-generated method stub
					int op=0;
					if(n.getgauche()!=null && n.getgauche().getunite()!=null) {
						 n.getgauche().getunite().add_Statut(new Brulure(3, n.getgauche().getunite()));
						op++;
					}
					if(n.gethautgauche()!=null && n.gethautgauche().getunite()!=null) {
						 n.gethautgauche().getunite().add_Statut(new Brulure(3, n.gethautgauche().getunite()));
						op++;
					}
					if(n.getbasgauche()!=null && n.getbasgauche().getunite()!=null) {
						 n.getbasgauche().getunite().add_Statut(new Brulure(3, n.getbasgauche().getunite()));
						op++;
					}
					if(n.getdroit()!=null && n.getdroit().getunite()!=null) {
						 n.getdroit().getunite().add_Statut(new Brulure(3, n.getdroit().getunite()));
						op++;
					}
					if(n.gethautdroit()!=null && n.gethautdroit().getunite()!=null) {
						 n.gethautdroit().getunite().add_Statut(new Brulure(3, n.gethautdroit().getunite()));
						op++;
					}
					if(n.getbasdroit()!=null && n.getbasdroit().getunite()!=null) {
						 n.getbasdroit().getunite().add_Statut(new Brulure(3, n.getbasdroit().getunite()));
						op++;
					}
					Elemen3.this.inflige(4*op,1);
					cd=cdmax;
				}

				@Override
				public void setrange() {
					// TODO Auto-generated method stub
					LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
					depla.add(place);	
					for(int i=0;i<5;i++) {
						int pop=depla.size();
						for(int o=0;o<pop;o++) {
							depla=depla.get(o).add_autour_withunits(depla);
						}
					}
					depla.remove(place);
					rang= depla;
				}
				
		
			}
			@Override
			protected void degats_adversaire(Hexagone n) {
				// TODO Auto-generated method stub
				if(n.getunite()!=null) {
					n.getunite().inflige(attaque,1);
				}
			}
			void dcd() {
				if(!estmort()) {
					this.mort=true;
					this.place.setunite(null);
					this.place=null;
					invoc.remove(this);
				}
				actualise();
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
			
			public void add_Pv(int c){
		        pv+=c/2;
		        if(pv>pdvMax)pv=pdvMax;
		        actualise(); 
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
			@Override
			protected String description_attaque() {
				// TODO Auto-generated method stub
				return "Inflige "+attaque+" de dégâts magiques à la cible";
			}
			@Override
			protected String passif() {
				return "Les soins reçus sont divisés par 2";
			}
			@Override
			protected String description_encyclopedie() {
				// TODO Auto-generated method stub
				return "Invocation du Druide";
			}
			
		}
		@Override
		public void use(Hexagone n) {
			// TODO Auto-generated method stub
			if(n.getunite()==null) {
				Fx_Unite m=new Fx_Unite(n.getFx());
				Elemen3 a=new Elemen3(n,m,camp);
				m.setNom("Elementaire de Feu");
				m.setcamp(camp);
				m.dessineImage(false);
				Game.getarmy(camp).add(a);
				invoc.add(a);
				cd=cdmax;
			}
		}

		@Override
		public void setrange() {
			// TODO Auto-generated method stub
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			for(int i=0;i<3;i++) {
				int pop=depla.size();
				for(int o=0;o<pop;o++) {
					depla=depla.get(o).add_autour(depla);
				}
			}
			depla.remove(place);
			rang= depla;
		}
		
	}
	/////////////////////////////////////////////////
	@Override
	protected void degats_adversaire(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null) {			
			n.getunite().inflige(attaque,1);//20 pV en moins en comptant l'armure
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
		 return 195;
	 }

	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige "+attaque+" de dégâts magiques à la cible";
	}
	@Override
	protected String passif() {
		return "Ses attaques de base infligent des dégâts magiques";
	}
	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "Le Druide est une unité qui peut invoquer des élémentaires différents, un tank, un soigneur et un dps. Ces élémentaires sont deux fois moins efficaces aux soins";
	}

}