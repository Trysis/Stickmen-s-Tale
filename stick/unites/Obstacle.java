package unites;

import Statut.Statut;
import application.Fx_Unite;
import carte.Hexagone;

public class Obstacle extends Unite {
	public Obstacle( String nom,int pv,int def,int defmagique,Hexagone place, Fx_Unite Unite) {
		super(nom, 0, pv, 0, def,defmagique, 0, place, Unite, -1);
		// TODO Auto-generated constructor stub
		attaquable=false;
		movable=false;
		castable=false;
	}
	public Obstacle() {
		this("",0,0,0,null,null);
	}
	private static final long serialVersionUID = 1L;

	@Override
	protected void degats_adversaire(Hexagone n) {
		// TODO Auto-generated method stub

	}
	public String[] getStats() {
		stats[0]=nom;
		stats[1]="pdv : "+pv+"/"+pdvMax;
		stats[2]="armure : "+defensearmor;
		stats[3]="ar. magique: "+defmagique;
		stats[4]="";
		stats[5]="";
		
		return stats;
	}
	@Override
	public void actu_range() {
		// TODO Auto-generated method stub

	}

	@Override
	void actu_dep(int dep) {
		// TODO Auto-generated method stub

	}
	public void set_attaquable(boolean n){

	}
	
	public void set_movable(boolean n){

	}
	
	public void set_castable(boolean b){
	
	}

	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected String passif() {
		return "Aucun passif";
	}
	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return null;
	}
	public void inflige(int i,int type) {//Degats prenant en compte l'armure //Voir sub_Pv pour ignorer l'armure
		double reduc=0.0;
		if(type==0) {
			reduc=100.0/(double)(100+2*this.defensearmor-20);
		}
		else {
			reduc=100.0/(double)(100+2*this.defmagique-20);
		}
	 	this.sub_Pv((int)Math.round(reduc*i));
	}
	
	public boolean deplacementf(Hexagone x) { 
	  return true;
	}
	public void add_Statut(Statut n){
    	
    }
}
