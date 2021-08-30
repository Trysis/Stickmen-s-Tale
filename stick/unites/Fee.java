package unites;

import java.util.LinkedList;

import application.Fx_Unite;
import carte.Hexagone;

public class Fee extends Unite implements Unite.Healer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Fee(Hexagone place,Fx_Unite fx,int camp) {
		super("fee", 5, 100, 10, 10,22, 2, place, fx, camp);
		set_Skill(new Heal_1(), new Vol_Vie(),new Sacrifice());
	}
	public Fee() {
		this(null,null,-1);
	}
	public class Heal_1 extends Skill implements Skill.Heal_autour{
		private int soin = attaque*3;
		{
			name="Soin";
			description="Soigne les alliés à portée de la Fée de "+soin+" pdv (300% de son attaque)";
			sub_description="";
			cout=3;
			cdmax=4;
		}

		@Override
		public void use(Hexagone n) {
			if(!available())return;
			for(int i=0;i<range.size();i++) {
				if(range.get(i).getunite()!=null) {
					if(range.get(i).getunite().getcamp()==Fee.this.getcamp()) {
						range.get(i).getunite().add_Pv(soin);
					}
				}
				cd=cdmax;
			}
		}
		@Override
		public void setrange() {
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			rang=depla;
			}
		
	}
	
	public class Vol_Vie extends Skill implements Skill.SelfHeal,Skill.Degat_autour{//Trop op sans l'applicationde vol_supplementaire 
		private double ratio=0.5;
		private int vol = (int)Math.floor(attaque*ratio);
		private int vol_supplementaire=(int)Math.floor(vol/2.0);
		{
			name="Vol de Vie";
			description="Vol "+vol+" pdv aux ennemies adjacents plus "+vol_supplementaire+" de pdv pour chaque ennemie adjacent";
			sub_description="";
			cout=4;
			cdmax=4;
		}

		public int pre_use(Hexagone n) {
			int x = 0;
			Unite u;
			if(n.gethautgauche()!=null) {
				u=n.gethautgauche().getunite();
				if(u!=null)x+=vol_supplementaire;
			}
			if(n.gethautdroit()!=null) {
				u=n.gethautdroit().getunite();
				if(u!=null)x+=vol_supplementaire;
			}
			if(n.getgauche()!=null) {
				u=n.getgauche().getunite();
				if(u!=null)x+=vol_supplementaire;
			}
			if(n.getdroit()!=null) {
				u=n.getdroit().getunite();
				if(u!=null)x+=vol_supplementaire;
			}
			if(n.getbasgauche()!=null) {
				u=n.getbasgauche().getunite();
				if(u!=null)x+=vol_supplementaire;
			}
			if(n.getbasdroit()!=null) {
				u=n.getbasdroit().getunite();
				if(u!=null)x+=vol_supplementaire;
			}
			return x;
		}
		
		@Override
		public void use(Hexagone n) {
			if(!n.equals(place))return;
			if(!available())return;
			int x=pre_use(n);
			int i=0;
			Unite u;
			if(n.gethautgauche()!=null) {
				u=n.gethautgauche().getunite();
				if(u!=null) {
					u.inflige(vol+x,1);
					i++;
				}
			}
			if(n.gethautdroit()!=null) {
				u=n.gethautdroit().getunite();
				if(u!=null) {
					u.inflige(vol+x,1);
					i++;
				}
			}
			if(n.getgauche()!=null) {
				u=n.getgauche().getunite();
				if(u!=null) {
					u.inflige(vol+x,1);
					i++;
				}
			}
			if(n.getdroit()!=null) {
				u=n.getdroit().getunite();
				if(u!=null) {
					u.inflige(vol+x,1);
					i++;
				}
			}
			if(n.getbasgauche()!=null) {
				u=n.getbasgauche().getunite();
				if(u!=null) {
					u.inflige(vol+x,1);
					i++;
				}
			}
			if(n.getbasdroit()!=null) {
				u=n.getbasdroit().getunite();
				if(u!=null) {
					u.inflige(vol+x,1);
					i++;
				}
			}
			u=n.getunite();
			if(u!=null && getrange().contains(u.getplace())) {
				u.add_Pv((i*vol_supplementaire+vol));
				cd=cdmax;
			}
		}
		@Override
		public void setrange() {
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			rang=depla;
		}
		
	}
	
	public class Sacrifice extends Skill implements Skill.Heal{
		{
			name="Sacrifice";
			description="Se sacrifie et donne tous ses pdv a un allié";
			sub_description="";
			cout=0;
			cdmax=3;//Est cense etre morte apres utilisation, mais on sait jamais 
		}
		@Override
		public void use(Hexagone n) {
			Unite u = n.getunite();
			if(u!=null && getrange().contains(u.getplace())) {
				if(u.getcamp()==camp) {
					u.add_Pv(Fee.this.pv);//Ajoute a la cible tous les pdv de la Fee
					Fee.this.set_pv(0);//Enleve a la Fee toute sa vie
				}
			}
		}
		
		@Override
		public void setrange() {
			rang=range;
		}
		
	}
	

	@Override
	protected void degats_adversaire(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null && getrange().contains(n.getunite().getplace())) {
			n.getunite().inflige(attaque,1);
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
	
	
	public static int get_Prix() {
		 return 200;
	 }

	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige "+attaque+" de dégâts magiques à la cible";
	}
	@Override
	protected String passif() {
		return "Ses attaques de base infligent des dégats magiques";
	}
	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "La fée est une unité faible mais qui peut faire de gros soins";
	}
}

