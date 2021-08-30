package joueur;

import java.util.LinkedList;
import unites.*;


public class Joueur{
	
	private int camp;
	private int money;
	protected LinkedList<Unite> units = new LinkedList<Unite>();//liste d'unites possedes par le joueur. 
	private boolean hasChanged;
	private int PA;
	private int PaBase;
	public Joueur(int b, int m,int pa) {
		this.camp=b;
		money=m;
		PA=pa;
		PaBase=pa;
	}
		
	
	public void setMoney(int m) {
		this.money = m;
	}
	//Getter
	public int getMoney() {//Getter de l'argent total du joueur
		return money;
	}
	
	/*public String toString() {
		return this.nom+ " a "+Money+ " PA. \n Ses units sont :"+ units.toString()+ "";
	}
	*/
	public void add_PA(int pa) {
		PA+=pa;
	}
	public void sub_PA(int pa) {
		PA-=pa;
	}
	void add(Unite n){
		units.add(n);
		n.setcamp(this.camp);
	}
	
	public int getcamp() {
		return this.camp;
	}
	
	public boolean alldead(){
		for(int i=0;i<units.size();i++) {
			if(!units.get(i).estmort()) {
				return false;
			}
		}
		return true;
	}
	
	public boolean achatUnite(Unite u) {		
		if(money > 0) {			
			if(money-u.get_Prix()>=0) {
				add(u);
				paiement(u.get_Prix());
				return true;
			}			
		}	
		return false;
	}
	public boolean vendreUnite(Unite u) {
		if(units.contains(u)) {
			units.remove(u);
			paiement(-u.get_Prix());
			return true;
		}
		return false;
	}

	public void paiement(int moins) {
		money-=moins;
	}
	public boolean hasChanged() {
		if(hasChanged) {
			hasChanged=false;
			return true;
		}
		return false;
	}
	public LinkedList<Unite> getUnits() {
		// TODO Auto-generated method stub
		return units;
	}

	public void applystatus() {
		for(Unite n : units) {
			n.applystatus();
		}
	}


	public void setunites(LinkedList<Unite> instanciation) {
		units=instanciation;		
	}


	public void resetPA() {
		PA=PaBase;
	}


	public int getPA() {
		// TODO Auto-generated method stub
		return PA;
	}
	
}

