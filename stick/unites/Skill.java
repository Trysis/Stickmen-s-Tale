package unites;

import java.util.LinkedList;
import carte.Hexagone;

public abstract class Skill{		
	protected String name;//Nom de la competence
	protected String description;//Description de la competence
	protected String sub_description;//Sous description ex: "Coute x PA"
		
	protected int cout;//Cout de la competence en PA
	protected int cd;//CD == Cooldown , c'est le temps de recuperation en nombre de tour restant de la competence
	protected int cdmax;//C'est la valeur maximale du temps de recuperation
	protected boolean used=false;//Peut etre utilise avec use pour limite un nombre d'utilisation de la competence si celle-ci a deja ete utilise
	protected LinkedList<Hexagone> rang=new LinkedList<Hexagone>();//Liste des hexagones ou la competence est utilisable
	protected LinkedList<Hexagone> cibles=new LinkedList<Hexagone>();//Liste des hexagones correspondant a la zone d'effet de la competence
	//
	public String getName() {//Nom de la competence
		return name;
	}
	public String description() {//Descriptionde la competence
		return description;
	}
	public String sub_description() {//Sous_description
		return sub_description;
	}
	//
	public int cost() {//Cout d'usage en PA de la competence
		return cout;
	}
	public int getCd() {//CD
		return cd;
	}
	public int getCdMax() {//CD max
		return cdmax;
	}
	public boolean available() {//Informe sur la disponibilite de la competence, Par defaut, est disponible lorsque le cd vaut 0
		return cd==0;
	}
	//
	public LinkedList<Hexagone> getrange() {//return la position de l'unite (le sort etant personnel)
		return rang;
	}
	public LinkedList<Hexagone> getcibles(){
		return cibles;
	}
	public void tours() {
		if(cd>0)cd--;
		setrange();
	}
	public void setcd(int a) {
		this.cd=a;
	}
	//Methodes abstraitesa imperativement definir
	public abstract void use(Hexagone n);//Utilisation de la competence, le int retourner est le nombre de PA utilise
	public abstract void setrange();//Defini les cases ou le sort se lance
	
	public interface Heal {}
	public interface Attaque{}
	public interface SelfHeal{}
	public interface NouveauTerrain {}
	public interface Invocateur{}
	public interface Offensif{}
	public interface Defensif{}
	public interface Neutre{}
	public interface cc{}
	public interface Buff{}
	public interface Degat_autour{}
	public interface Heal_autour{}
}