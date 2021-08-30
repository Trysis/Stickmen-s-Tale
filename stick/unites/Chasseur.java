package unites;

import java.util.LinkedList;

import Statut.Hemoragie;
import application.Fx_Unite;
import controleur.Game;
import carte.*;

public class Chasseur extends Unite implements Unite.Cac {
	private static final long serialVersionUID = 1L;
	private Unite target=null;
	private Marque marque=null;
	public Chasseur(Hexagone place,Fx_Unite fx,int camp){
		super("Chasseur", 3, 220, 24, 20 , 15, 2, place, fx, camp);
		marque=new Marque();
		set_Skill(marque,new Hook(), new Piege());		
	}
	public Chasseur() {
		this(null,null,-1);
	}
	public String[] getStats() {
		if(target==null) {
		stats[6]="Cible: aucune ";
		}
		else {
			if(target.getplace()!=null) {
			stats[6]="Cible : "+target.get_Nom()+" ("+target.getplace().getx()+";"+target.getplace().gety()+")";
			}
			else {
				stats[6]="Cible : introuvable";
			}
		}
		return super.getStats();
	}
	public class Marque extends Skill implements Skill.Offensif{
		{
			name="Avis de recherche";
			description="Cible une unité et devient la cible du Chasseur de prime."
					+ " Le Chasseur infligera 2 fois plus de degats à sa cible et certaines attaques et sorts seront plus efficaces sur celle-ci";
			sub_description="";
			cout=4;
			cdmax=3;
		}
		@Override
		public void use(Hexagone n) {
			if(available()) {
				if(n.getunite()!=null) {
					target=n.getunite();
					cd=cdmax;
				}
			}
			
		}

		@Override
		public void setrange() {
			LinkedList<Hexagone> rang=new LinkedList<Hexagone>();
			for(Unite v:Game.getarmy(1)) {
				if(v.mort!=true && v.place!=null) {
					rang.add( v.getplace());
					rang.addAll(v.others);
				}
			}
			for(Unite v:Game.getarmy(2)) {
				if(v.mort!=true && v.place!=null) {
					rang.add( v.getplace());
					rang.addAll(v.others);
				}
			}
			this.rang=rang;
		}
	
	}
	public class Hook extends Skill implements Skill.cc,Skill.Offensif{
		private int degats=20;
		private int tours=3;
		{
			name="Grappin";
			description="\"Accroche une unite, lui inflige "+degats+" de degats et la ramene sur l'une des trois cases devant lui."
					+ " Inflige à l'unité Hémorragie pendant "+tours+" tours si l'unite est la cible du Chasseur"
					+ " Si aucune case n'est disponible alors le Grappin se brise";
			sub_description="";
			cout=5;
			cdmax=6;
		}

		@Override
		public void use(Hexagone n) {
			if(available()) {
			Unite u=n.getunite();
			if(u!=null && getrange().contains(n) && !u.insensible) {
				u.inflige(0,0);
				if(u==target) {
					u.add_Statut(new Hemoragie(3,u));
				}
				if(camp==1) {
					if(u.others.isEmpty()) {
						if( place.getdroit()!=null && u.obstruction(place.getdroit())) {
							u.deplacementf(place.getdroit());
						}
						else {
							if(place.gethautdroit()!=null && u.obstruction(place.gethautdroit())) {
								u.deplacementf(place.gethautdroit());
							}
							else {
								if(place.getbasdroit()!=null && u.obstruction(place.getbasdroit())) {
									u.deplacementf(place.getbasdroit());
								}
							}
						}
					}
					else {
						if(u.camp==camp) {
							if( place.getdroit()!=null && u.obstruction(place.getdroit())) {
								
								u.deplacementf(place.getdroit());
							}
							else {
								if(place.gethautdroit()!=null && u.obstruction(place.gethautdroit())) {
									u.deplacementf(place.gethautdroit());
								}
								else {
									if(place.getbasdroit()!=null && u.obstruction(place.getbasdroit())) {
										u.deplacementf(place.getbasdroit());
									}
								}
							}
						}
						else {
							if(place.getdroit()!=null && place.getdroit().getdroit()!=null && u.obstruction(place.getdroit().getdroit())) {
								u.deplacementf(place.getdroit().getdroit());
							}
							else {
								if(place.gethautdroit()!=null && u.obstruction(place.gethautdroit())) {
									u.deplacementf(place.gethautdroit());
								}
								else {
									if(place.getbasdroit()!=null && u.obstruction(place.getbasdroit())) {
										u.deplacementf(place.getbasdroit());
									}
								}
							}
						}
					}
				}
				else {
					if(place.getgauche()!=null && u.obstruction(place.getgauche())) {
						u.deplacementf(place.getgauche());
					}
					else {
						if(place.gethautgauche()!=null && u.obstruction(place.gethautgauche())) {
							u.deplacementf(place.gethautgauche());
						}
						else {
							if(place.getbasgauche()!=null && u.obstruction(place.getbasgauche())) {
								u.deplacementf(place.getbasgauche());
							}
						}
					}
				}
				if(!u.estmort()) {
				u.getplace().getFx().setFx_Unite(u.getFx_Unite());
				u.getFx_Unite().setHexagone(u.getplace().getFx());
				}
				for(Unite o:Game.getarmy(camp)) {
					if(!o.estmort()) {
					o.actualise();
					o.actualisation();
					}
				}
				cd=cdmax;
				if(!u.estmort()) {
					u.actualise();
					u.actualisation();
				}
			}
			}
		}
		@Override
		public void setrange() {
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			for(int i=0;i<4;i++) {
				int pop=depla.size();
				for(int o=0;o<pop;o++) {
					depla=depla.get(o).add_autour_withunits(depla);
				}
			}
			depla.remove(place);
			rang=depla;
		}
	}
	
	public class Piege extends Skill implements Skill.NouveauTerrain, Skill.cc{
		private int tours_normal=1;
		private int tours_cible=3;
		{
			name="Piege a Ours";
			description="Place un piège sur une case ne possedant pas de terrains, celui-ci appliquera l'effet Root à l'unite qui est dessus pendant "+tours_normal+" tour(s),"
					+ " et "+tours_cible+" tour(s) si cette unité est la cible du chasseur";
			sub_description="";
			cout=4;
			cdmax=4;
		}

		@Override
		public void use(Hexagone n) {
			if(available()) {
			if(n.getterrain()==null) {
				n.setTerrain(new Trap(Chasseur.this,-1,getcamp()));
				cd=cdmax;
			}
			}
		}

		@Override
		public void setrange() {
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			for(int i=0;i<3;i++) {
				int pop=depla.size();
				for(int o=0;o<pop;o++) {
					depla=depla.get(o).add_autour_withunits(depla);
				}
			}
			depla.remove(place);
			rang= depla;
		}
	}
	
	protected void degats_adversaire(Hexagone n) {
		if(n.getunite()!=null) {
			if(n.getunite()!=target) {
			n.getunite().inflige(attaque,0);//20 pV en moins en comptant l'armure
			}
			else {
				n.getunite().inflige(attaque*2,0);
				if(n.getunite()!=null && n.getunite().estmort()) {
					marque.cd=0;
				}
			}
		}

	}

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
	
	public static int get_Prix() {
		 return 150;
	 }
	public Unite gettarget() {
		// TODO Auto-generated method stub
		return target;
	}
	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige "+attaque+" dégat(s) physique à l'unité,"
				+ "le double ("+attaque*2+") si l'unité est la cible du Chasseur";
	}
	@Override
	protected String passif() {
		return "Des effets supplémentaire s'appliquent lorsque le Chasseur attaque/lance des sorts sur sa cible";
	}
	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "Le chasseur est une unité qui peut facilement infliger de lourds degats et contrôler les autres unités, s'il focus sa cible";
	}
	
}
