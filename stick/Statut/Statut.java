package Statut;
import application.Fx_Statut;
import unites.Unite;

public abstract class Statut {
	protected int tours;//Tours de la durée du sort
	protected Unite n;//Unite affecté par le statu
	protected boolean first;//permet de savoir s'il sagit du premier tours ou pas
	protected Fx_Statut fx;
	
	Statut(int tours, Unite n){
		this.tours=tours;
		this.n=n;
		this.first=true;
	}
	
	//getter
	public int get_tours() {
		return this.tours;
	}
	
	public Unite get_unite() {
		return n;
	}
	

	public void set_unite(Unite n) {
		this.n=n;
	}
	
	boolean first() {
		return first;
	}
	
	public Fx_Statut getFx() {
		return fx;
	}
	//Setter
	
	void add_tours(int n) {
		tours+=n;
	}
	
	public void set_first(boolean b) {
		first=b;
	}
	
	public void setFx(Fx_Statut n) {
		fx=n;
	}
	//Tour
	public void tours(){
		if(n!=null) {//regarde si le statut n'est pas mort
			if(tours>0) {//vérifie qu'il reste des tours dispo
				effect();//afflige l'effet à l'unité en argument
				tours--;//enleve un tours
			}
			if(tours<=0) {//si le statu est fini
				stopeffect();//rétablie les stats si necessaire
				n.supprstatue(this);//supprime le statut de la list de statut de l'unité n
				n=null;//set à null le n en argument pour ne plus lui affligé de debuff	
			}
		}
	}
	
	 abstract void effect();//effect affecte l'unité en argument
	
	public abstract void stopeffect();//remets les states en place 
	
	 public abstract void apply();
	public abstract String toString();//to string, necessaire, serat le nom du terrain (utile pour l'accès au fichier de texture)
	
	public abstract String description();
}