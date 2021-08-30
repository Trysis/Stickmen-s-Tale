package carte;

import java.io.Serializable;

import application.Fx_Terrain;
import unites.Unite;


public abstract class Terrain implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String nom;//Nom du terrain
	protected String description;//Description du terrain
	protected String sub_text;//Sous texte du Terrain
	protected int time;
	protected int camp; //1 p1, 2 p2 // else neutre // utilisation d'ID ?
	
	transient protected Fx_Terrain fx=null;
	protected Hexagone hexa=null;

	protected Terrain() {//Set automatiquement un Terrain sans limite de tour et sans@Override
	 //camp sans parametres
		time=-1;
		camp=-1;
		this.fx=new Fx_Terrain(toString());
		this.fx.setNom(nom);
		this.fx.setDescription(description);
		this.fx.setSubText(sub_text);
	}
	protected Terrain(Fx_Terrain t) {//Set automatiquement un Terrain sans limite de tour et sans camp sans parametres
		time=-1;
		camp=-1;
		this.fx=t;
		this.fx.setNom(nom);
		this.fx.setDescription(description);
		this.fx.setSubText(sub_text);
	}
	public Terrain(int t, int camp) {
		time=t;
		this.camp=camp;
		this.fx=new Fx_Terrain(toString());
		this.fx.setNom(nom);
		this.fx.setDescription(description);
		this.fx.setSubText(sub_text);
	}
	public abstract void effect(Unite n);//effect affecte l'unité en argument, qui serat l'unité dans en attriubt de l'hexagone qui a pour attribut ce terrain
	public abstract void stopeffect(Unite n);//remets les states en place lorsque le joueur quitte le terrain
	public abstract String toString();//to string, necessaire, serat le nom du terrain (utile pour l'accès au fichier de texture)
	//Getter
	public String getDescription() {
		return this.description;
	}
	public String getSub_Text() {
		return this.sub_text;
	}
	public int getours() {
		return this.time;
	}
	public int getcamp() {
		 return this.camp;
	}
	public Fx_Terrain getFx() {
		return this.fx;
	}
	public Hexagone gethexa() {
		return this.hexa;
	}
	//Setter
	public void sethexa(Hexagone hexa) {
		this.hexa=hexa;
	}
	//Mecanique
	public void subtours(int i) {
		time=time-i;
	}
	public void Endeffect() {
		// TODO Auto-generated method stub
		
	}
}
