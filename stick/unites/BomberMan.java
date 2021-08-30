package unites;

import java.util.LinkedList;

import application.Fx_Unite;
import carte.Hexagone;

public class BomberMan extends Unite  implements Unite.Cac {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BomberMan(Hexagone place,Fx_Unite fx,int camp) {
		super("BomberMan", 3, 75, 2, 0,15, 1, place, fx, camp);
		set_Skill(new Explosion());
	}
	public BomberMan() {
		this(null,null,-1);
	}
	public class Explosion extends Skill implements Skill.Degat_autour{
		private int degats=50;
		{
			name="Bomb Attack";
			description="Attaque suicidaire infligeant jusqu'a "+degats+" de degat(s) physique(s) à tous les adversaires"
					+ " jusqu'à 3 de portée et leurs ôte 10 d'armure physique";
			sub_description="";
			cout=2;
			cdmax=0;
		}

		@Override
		public void use(Hexagone n) {
			//Necessite un algo qui selectionne des cases a des valeurs de portees differentes (pour appliquer les degats a certaines portees correctement)
			//A chaque iteration de i appliquera la reduction des degats mais pas selon la distance mais selon l'ordre de la liste des Hexagones present dans range
			if(!available())return;
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			for(int i=0;i<6;i++) {
				int pop=depla.size();
				for(int o=0;o<pop;o++) {
					depla=depla.get(o).add_autour_withunits(depla);
				}
			}
			depla.remove(place);
			for(Hexagone j:depla) {
				if(j.getunite()!=null) {
					j.getunite().inflige(50,0);
					if(j.getunite()!=null && !j.getunite().estmort())j.getunite().sub_defensearmore(10);
				}
			}
			BomberMan.this.set_pv(0);//L'unite perd toute sa vie
			cd=cdmax;
		}

		@Override
		public void setrange() {
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			rang=depla;
		}
		
	}

	@Override
	protected void degats_adversaire(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null && getrange().contains(n.getunite().getplace())) {
			n.getunite().inflige(attaque,0);
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
		 return 125;
	 }

	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige "+attaque+" de dégats physique à l'unité";
	}
	@Override
	protected String passif() {
		return "Aucun passif";
	}
	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "Le bomberman est une unité Kamikaze qui inflige de lourd dégats aux unités alentours en se sacrifiant";
	}
}
