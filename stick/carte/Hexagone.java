package carte;

import java.io.Serializable;
import java.util.LinkedList;

import application.Fx_Hexagon;
import application.Fx_Terrain;
import unites.Unite;

public class Hexagone implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final private int x,y; //Coordonnee de la case
	private Plateau plat;
	private Hexagone gauche,droit,hautgauche,hautdroit,basgauche,basdroit;//Tous les hexagones voisins
	private Unite unite; //Unite place sur la carte
	private Terrain terrain; //Terrain de la case

	public boolean posable;
	transient private Fx_Hexagon gui;
	
	
	public Hexagone(int x,int y,Plateau p) {
		//Coordonnes x,y
		this.x=x;
		this.y=y;
		//Voisins
		this.gauche=null;
		this.hautgauche=null;
		this.droit=null;
		this.hautdroit=null;
		this.basgauche=null;
		this.basdroit=null;
		//
		this.unite=null;//Unite
		this.terrain=null;//Terrain
		this.plat=p;//Plateau
	}
	public Hexagone() {
		x=-1;y=-1;
	}
	public boolean isAutour(Hexagone n) {
		return (gauche==n || hautgauche==n || droit==n || hautdroit==n || basgauche==n || basdroit==n);
	}
	public LinkedList<Hexagone> add_autour(LinkedList<Hexagone> range){//ajoute a "range" toutes les voisins de l'hexagone
		
		if(gethautgauche()!=null && !range.contains(gethautgauche()) && gethautgauche().getunite()==null) {
			range.add(gethautgauche());
			
		}
		if(getgauche()!=null && !range.contains(getgauche()) && getgauche().getunite()==null) {
			range.add(getgauche());
		
		}
		if(gethautdroit()!=null && !range.contains(gethautdroit()) && gethautdroit().getunite()==null) {
			range.add(gethautdroit());
		
		}
		if(getdroit()!=null && !range.contains(getdroit()) && getdroit().getunite()==null) {
			range.add(getdroit());
			
		}
		if(getbasdroit()!=null && !range.contains(getbasdroit()) && getbasdroit().getunite()==null) {
			range.add(getbasdroit());
			
		}
		if(getbasgauche()!=null && !range.contains(getbasgauche()) && getbasgauche().getunite()==null) {
			range.add(getbasgauche());
			
		}
		
		return range;
	}
	public LinkedList<Hexagone> add_autour_withunits(LinkedList<Hexagone> range){//ajoute a "range" toutes les voisins de l'hexagone
		
		if(gethautgauche()!=null && !range.contains(gethautgauche()) ) {
			range.add(gethautgauche());
			
		}
		if(getgauche()!=null && !range.contains(getgauche())) {
			range.add(getgauche());
		
		}
		if(gethautdroit()!=null && !range.contains(gethautdroit()) ) {
			range.add(gethautdroit());
		
		}
		if(getdroit()!=null && !range.contains(getdroit()) ) {
			range.add(getdroit());
			
		}
		if(getbasdroit()!=null && !range.contains(getbasdroit())) {
			range.add(getbasdroit());
			
		}
		if(getbasgauche()!=null && !range.contains(getbasgauche())) {
			range.add(getbasgauche());
			
		}
		
		return range;
	}
	////GETTER
	public int getx() {
		return this.x;
	}
	public int gety() {
		return this.y;
	}
	public Hexagone getgauche() {
		return this.gauche;
	}
	
	public Hexagone getdroit() {
		return this.droit;
	}
	
	public Hexagone gethautdroit() {
		return this.hautdroit;
	}
	
	public Hexagone gethautgauche() {
		return this.hautgauche;
	}
	
	public Hexagone getbasdroit() {
		return this.basdroit;
	}
	
	public Hexagone getbasgauche() {
		return this.basgauche;
	}
	public boolean getPosable() {
		return this.posable;
	}
	public Unite getunite() {
		return this.unite;
	}
	public Terrain getterrain() {
		return this.terrain;
	}
	public Fx_Hexagon getFx() {
		return this.gui;
	}
	/////SETTER
	public void setgauche(Hexagone g) {
		this.gauche=g;
	}
	public void setdroit(Hexagone g) {
		this.droit=g;
	}
	
	public void sethautdroit(Hexagone g) {
		this.hautdroit=g;
	}
	
	public void sethautgauche(Hexagone g) {
		this.hautgauche=g;
	}
	
	public void setbasdroit(Hexagone g) {
		this.basdroit=g;
	}
	
	public void setbasgauche(Hexagone g) {
		this.basgauche=g;
	}
	public void setPosable(boolean b) {
		this.posable=b;
	}
	public void setunite(Unite unite) {
		this.unite=unite;
		if(terrain!=null && unite!=null)terrain.effect(unite);
	}
	public void setterrain(Terrain terrain) {
		this.terrain=terrain;
	}
	public void setTerrain(Terrain t) {
		this.terrain=t;
		if(terrain!=null)terrain.sethexa(this);
		if(gui!=null && this.terrain!=null) {
			plat.getterrains().add(t);
			this.gui.setFxTerrain(new Fx_Terrain(terrain.toString()));
			this.gui.getTerrain().setDescription(terrain.getDescription());
			this.gui.getTerrain().setSubText(terrain.getSub_Text());
			if(unite!=null)terrain.effect(unite);
		}
	}
	
	public void setFX(Fx_Hexagon hexagon) {
		this.gui=hexagon;
	}
	public void removeterrain() {
		// TODO Auto-generated method stub
		plat.getterrains().remove(terrain);
		gui.removeFxTerrain();
		setTerrain(null);
	}
	public Plateau getpla() {
		return this.plat;
	}
	public String toString() {
		return x+";"+y;
	}
	public LinkedList<Hexagone> add_autour_withgauche(LinkedList<Hexagone> range) {

		if(gethautgauche()!=null && !range.contains(gethautgauche()) && gethautgauche().getunite()==null && gethautgauche().getgauche()!=null && gethautgauche().getgauche().getunite()==null) {
			range.add(gethautgauche());
			
		}
		if(getgauche()!=null && !range.contains(getgauche()) && getgauche().getunite()==null && getgauche().getgauche()!=null && getgauche().getgauche().getunite()==null) {
			range.add(getgauche());
		
		}
		if(gethautdroit()!=null && !range.contains(gethautdroit()) && gethautdroit().getunite()==null && gethautdroit().getgauche()!=null && gethautdroit().getgauche().getunite()==null) {
			range.add(gethautdroit());
		
		}
		if(getdroit()!=null && !range.contains(getdroit()) && getdroit().getunite()==null && getdroit().getgauche()!=null && getdroit().getgauche().getunite()==null) {
			range.add(getdroit());
			
		}
		if(getbasdroit()!=null && !range.contains(getbasdroit()) && getbasdroit().getunite()==null && getbasdroit().getgauche()!=null && getbasdroit().getgauche().getunite()==null) {
			range.add(getbasdroit());
			
		}
		if(getbasgauche()!=null && !range.contains(getbasgauche()) && getbasgauche().getunite()==null && getbasgauche().getgauche()!=null && getbasgauche().getgauche().getunite()==null) {
			range.add(getbasgauche());
			
		}
		
		return range;
	}
	public LinkedList<Hexagone> add_autour_withdroit(LinkedList<Hexagone> range) {

		if(gethautgauche()!=null && !range.contains(gethautgauche()) && gethautgauche().getunite()==null && gethautgauche().getdroit()!=null && gethautgauche().getdroit().getunite()==null) {
			range.add(gethautgauche());
			
		}
		if(getgauche()!=null && !range.contains(getgauche()) && getgauche().getunite()==null && getgauche().getdroit()!=null && getgauche().getdroit().getunite()==null) {
			range.add(getgauche());
		
		}
		if(gethautdroit()!=null && !range.contains(gethautdroit()) && gethautdroit().getunite()==null && gethautdroit().getdroit()!=null && gethautdroit().getdroit().getunite()==null) {
			range.add(gethautdroit());
		
		}
		if(getdroit()!=null && !range.contains(getdroit()) && getdroit().getunite()==null && getdroit().getdroit()!=null && getdroit().getdroit().getunite()==null) {
			range.add(getdroit());
			
		}
		if(getbasdroit()!=null && !range.contains(getbasdroit()) && getbasdroit().getunite()==null && getbasdroit().getdroit()!=null && getbasdroit().getdroit().getunite()==null) {
			range.add(getbasdroit());
			
		}
		if(getbasgauche()!=null && !range.contains(getbasgauche()) && getbasgauche().getunite()==null && getbasgauche().getdroit()!=null && getbasgauche().getdroit().getunite()==null) {
			range.add(getbasgauche());
			
		}
		
		return range;
	}
	public LinkedList<Hexagone> add_autour_withhautdroit(LinkedList<Hexagone> range) {
		// TODO Auto-generated method stub
		if(gethautgauche()!=null && !range.contains(gethautgauche()) && gethautgauche().getunite()==null && gethautgauche().gethautdroit()!=null && gethautgauche().gethautdroit().getunite()==null) {
			range.add(gethautgauche());
			
		}
		if(getgauche()!=null && !range.contains(getgauche()) && getgauche().getunite()==null && getgauche().gethautdroit()!=null && getgauche().gethautdroit().getunite()==null) {
			range.add(getgauche());
		
		}
		if(gethautdroit()!=null && !range.contains(gethautdroit()) && gethautdroit().getunite()==null && gethautdroit().gethautdroit()!=null && gethautdroit().gethautdroit().getunite()==null) {
			range.add(gethautdroit());
		
		}
		if(getdroit()!=null && !range.contains(getdroit()) && getdroit().getunite()==null && getdroit().gethautdroit()!=null && getdroit().gethautdroit().getunite()==null) {
			range.add(getdroit());
			
		}
		if(getbasdroit()!=null && !range.contains(getbasdroit()) && getbasdroit().getunite()==null && getbasdroit().gethautdroit()!=null && getbasdroit().gethautdroit().getunite()==null) {
			range.add(getbasdroit());
			
		}
		if(getbasgauche()!=null && !range.contains(getbasgauche()) && getbasgauche().getunite()==null && getbasgauche().gethautdroit()!=null && getbasgauche().gethautdroit().getunite()==null) {
			range.add(getbasgauche());
			
		}
		
		return range;
	}
	public LinkedList<Hexagone> add_autour_withhautgauche(LinkedList<Hexagone> range) {
		// TODO Auto-generated method stub
		if(gethautgauche()!=null && !range.contains(gethautgauche()) && gethautgauche().getunite()==null && gethautgauche().gethautgauche()!=null && gethautgauche().gethautgauche().getunite()==null) {
			range.add(gethautgauche());
			
		}
		if(getgauche()!=null && !range.contains(getgauche()) && getgauche().getunite()==null && getgauche().gethautgauche()!=null && getgauche().gethautgauche().getunite()==null) {
			range.add(getgauche());
		
		}
		if(gethautdroit()!=null && !range.contains(gethautdroit()) && gethautdroit().getunite()==null && gethautdroit().gethautgauche()!=null && gethautdroit().gethautgauche().getunite()==null) {
			range.add(gethautdroit());
		
		}
		if(getdroit()!=null && !range.contains(getdroit()) && getdroit().getunite()==null && getdroit().gethautgauche()!=null && getdroit().gethautgauche().getunite()==null) {
			range.add(getdroit());
			
		}
		if(getbasdroit()!=null && !range.contains(getbasdroit()) && getbasdroit().getunite()==null && getbasdroit().gethautgauche()!=null && getbasdroit().gethautgauche().getunite()==null) {
			range.add(getbasdroit());
			
		}
		if(getbasgauche()!=null && !range.contains(getbasgauche()) && getbasgauche().getunite()==null && getbasgauche().gethautgauche()!=null && getbasgauche().gethautgauche().getunite()==null) {
			range.add(getbasgauche());
			
		}
		
		return range;
	}
}

