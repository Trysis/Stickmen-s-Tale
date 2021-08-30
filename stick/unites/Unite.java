package unites;

import java.io.Serializable;
import java.util.LinkedList;

import Statut.Statut;
import application.Fx_Hexagon;
import application.Fx_Statut;
import application.Fx_Unite;
import application.Fx_Unite.Fx_Skill;
import controleur.Game;
import carte.Hexagone;

public abstract class Unite implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private static int static_actu=0; //utilise pour compter le nombre d'appel de certaines methodes
	protected String nom;//Nom de l'unite
	protected int pdvMax, pv, attaque, defensearmor,defmagique, portee; // la portee a relier avec le terrain peut-être
	protected boolean mort=false;
	//protected int PA;//PA - point d'action qu'utilise une unite en attaquant (attaque de base)
	protected int prix;//Prix de l'unite pour la boutique
	protected LinkedList<Skill> skillList;
	protected LinkedList<Statut> statuts;
	protected LinkedList<Statut> toremove;
	protected LinkedList<Hexagone> deplacement = new LinkedList<>();//touts les hexagones dans lequelle l'unité peux se déplacer
	protected LinkedList<Hexagone> range= new LinkedList<>();//hexagones sur lequel l'unite peut faire une attaque de base
	protected boolean movedone=false,attdone=false;
	protected Hexagone place;
	protected int dep;//deplacement en max range de l'unité
	transient protected Fx_Unite guiunite;
	protected int camp;
	protected String[] stats=new String[7];
	protected LinkedList<Hexagone> others=new LinkedList<Hexagone>();
	
	
	protected boolean movable=true,attaquable=true,castable=true,insensible=false;//Permet de savoir si l'unité peux bouger, attaquer, caster
	public Unite(String nom,int dep,int pv,int attaque,int armor,int magique,int portee,Hexagone place,Fx_Unite Unite,int camp){
		this.nom=nom;
		this.pdvMax=pv;
		this.pv=pv;
		//this.PA=PA;
		this.attaque=attaque;
		this.defensearmor=armor;
		this.defmagique=magique;
		this.portee=portee;
		this.place=place;
		this.statuts=new LinkedList<Statut>();
		this.toremove=new LinkedList<Statut>();
		this.skillList=new LinkedList<Skill>();
		this.guiunite=Unite;
		if(guiunite!=null)this.prix=guiunite.getPrix();
		this.dep=dep;
		this.camp=camp;
		if(place!=null)place.setunite(this);	
	}
	public Unite() {
		this("",-1,-1,-1,-1,-1,-1,null,null,-1);
	}
	public boolean attaquer(Hexagone n) {//fonction principale de l'attaque		
		if(attaquable && !attdone) {
			if(range.contains(n) && attaquable!=false) {//check si l'unité est en range
				degats_adversaire(n);//lui inflige des dégats
				attdone=true;
				return true;
			}
		}
		return false;
	}
	public boolean caster(int type, Hexagone n) {
		if(castable) {
			if(type<skillList.size() && skillList.get(type)!=null && skillList.get(type).getrange().contains(n)) {
				 skillList.get(type).use(n);
				 return true;
			}
		}
		return false;
	}

	//Methodes abstraites
	protected abstract String description_attaque();
	protected abstract String description_encyclopedie();
	protected abstract void degats_adversaire(Hexagone n);
	protected abstract String passif();
	//Getter
	public String get_Nom() {
		return nom;
	}
	public int get_pvMax() {
		return pdvMax;
	}
	public int get_pv() {
		return pv;
	}
	public int get_attaque() {
		return attaque;
	}
	public int get_defensearmor() {
		return defensearmor;
	}
	public int get_defensemagique() {
		return defmagique;
	}
	public int get_portee() {
		return portee;
	}
	/*public int get_PA() {
		return PA;
	}
	*/
	public int getdep() {
		return dep;
	}
	public Hexagone getplace() {
		return this.place;
	}
	public String[] getStats() {
		stats[0]=nom;
		stats[1]="pdv : "+pv+"/"+pdvMax;
		stats[2]="attaque : "+attaque;
		stats[3]="armure : "+defensearmor;
		stats[4]="armure magique : "+defmagique;
		stats[5]="portee : "+portee;
		
		return stats;
	}
	public void setGUIUnite(Fx_Unite u) {
		this.guiunite=u;
	}
	public LinkedList<Skill> get_competences() {
		return skillList;
	}
	public int ratio() {//Ratio pv/attaque ? **
		return pv/attaque;
	}
	public boolean estmort(){
	    return this.mort;
	}
	
	//Setter
	public void set_pv(int pv) {
		this.pv=pv;
		if(this.pv==0) {
			dcd();
		}
		actualise(); 
	}
	public void set_attaque(int a) {
		attaque=a ;
		actualise(); 
	}
	public void set_defensearmor(int d) {
		defensearmor=d;
		actualise(); 
	}
	public void set_movedone(boolean b) {
		movedone=b;
	}
	public void set_attdone(boolean b) {
		this.attdone=b;
	}
	public void set_Skill(Skill ... skill) {
		for(Skill tmp: skill)skillList.add(tmp);
		if(guiunite!=null) {
			for(Skill tmp: skillList)guiunite.getSkill().add(new Fx_Skill(tmp.getName(),tmp.description(),tmp.cost(),tmp.getCd(),tmp.getCdMax(),tmp.available()));
			guiunite.set_des_attaque(this.description_attaque());
		}
		//set l'affichage de l'attaque : 
	}
	public void set_insensible(boolean n) {
		insensible=n;
		actualise();
	}
	public void set_attaquable(boolean n){
	    if(insensible && !n) {
	    	return;
	    }
		this.attaquable=n;
	    actualise(); 
	}
	
	public void set_movable(boolean n){
		if(insensible && !n) {
	    	return;
	    }
		this.movable=n;
	    actualise(); 
	}
	
	public void set_castable(boolean b){
		if(insensible && !b) {
	    	return;
	    }
		this.castable=b;
	    actualise(); 
	}
	//Incrementation et decrementation des stats
    public void add_defensearmore(int c){
        defensearmor+=c;
        actualise(); 
    }
    
    public void sub_defensearmore(int c){
        defensearmor-=c;
        actualise(); 
    }
    public void add_defensemagique(int c){
        defmagique+=c;
        actualise(); 
    }
    
    public void sub_defensemagique(int c){
        defmagique-=c;
        actualise(); 
    }
     public void add_attaque(int c){
        attaque+=c;
        actualise(); 
    }
    
    public void sub_attaque(int c){
        attaque-=c;
        actualise(); 
    }
    
    public void add_Pv(int c){
        pv+=c;
        if(pv>pdvMax)pv=pdvMax;
        actualise(); 
    }
    
    public void sub_Pv(int c){//Degats "brut" en ignorant l'armure de la cible //voir inflige(int c) pour prendre en compte l'armure
        pv-=c;
        if(this.pv<=0) {
			dcd();
		}
        actualise(); 
    }
    public void sub_PvMax(int c) {
    	this.pdvMax-=c;
    	if(pv>pdvMax)pv=pdvMax;
    	if(pv<=0)dcd();
    	actualise();
    }
    public void add_PvMax(int c) {
    	this.pdvMax+=c;
    	actualise();
    }
    

//Methode pour gerer les status
    public void add_Statut(Statut n){
    	n.apply();
        for(Statut e: statuts){
            if(e.getClass().equals( n.getClass() ) ){
                n.set_first(false);
            }
        }
        statuts.add(n);
        Fx_Statut nfx=new Fx_Statut(n.toString(),n.get_tours(),n.description());
        this.getFx_Unite().get_Statuts().add(nfx);
        n.setFx(nfx);
        actualise(); 
    }
    
    public void supprstatue(Statut n){
    	toremove.add(n);

        this.getFx_Unite().get_Statuts().remove(n.getFx());
        actualise(); 
    }
    public void removestatut(Statut n) {
    	n.stopeffect();
    	statuts.remove(n);
    	n.set_unite(null);  	
        this.getFx_Unite().get_Statuts().remove(n.getFx());
        n.setFx(null);
        actualise(); 
    }
    public void applystatus() {
    	 for(Statut e: statuts){
    		 e.tours();
    	 }
    	 for(Statut e:toremove) {
    		 statuts.remove(e);
    	 }
    	 toremove=new LinkedList<Statut>();
    	 actualise();    	
    }
   public boolean getmovedone() {
	   return movedone;
   }
   public boolean getmovable() {
	   return this.movable;
   }
	public boolean getattaquable() {
		return this.attaquable;
	}
	public boolean getcastable() {
		return this.castable;
	}
	public boolean obstruction(Hexagone n) {
		return n.getunite()==null;
	}
	public boolean deplacementf(Hexagone x) { 
		if(place!=null) {
			if(this.guiunite!=null) {
				getplace().getFx().removeFx_Unite();
				getFx_Unite().setHexagone(null);
			}
			if(this.place.getterrain()!=null) {
		        this.place.getterrain().stopeffect(this);
		      }   
		    this.place.setunite(null);
		}
	      this.place=x;
	      this.place.setunite(this);
	   if(this.guiunite!=null) {
	     guiunite.setX(place.getx());
	     guiunite.setY(place.gety());
	     getplace().getFx().setFx_Unite(getFx_Unite());
	     getFx_Unite().setHexagone(getplace().getFx());
	  }
	  return true;
	}
   public boolean deplacement(Hexagone x) {
		   if(deplacement.contains(x)) {
	        	if(this.place.getterrain()!=null) {
	        		this.place.getterrain().stopeffect(this);
	            }            	
	        	this.place.setunite(null);
	            this.place=x;
	            this.place.setunite(this);
	            if(this.guiunite!=null) {
	            	guiunite.setX(place.getx());
	            	guiunite.setY(place.gety());
	            }
	            return true;
	        }
        return false;
    }
   public void setplace(Hexagone n) {
	   this.place=n;
   }
   public void placen(Hexagone n) {//Methode utiliser pour redefinir dans les unités double 
	   this.place=n;
   }
    public void actualise(){
    	//Pour voir la classe qui appelle cette methode, et la methode de la classe qui appelle cette methode //
    	if(!mort) {
        LinkedList<Fx_Hexagon> move_list=new LinkedList<>();
        for(int i=0;i<deplacement.size();i++){
        	move_list.add(deplacement.get(i).getFx());
        }
        LinkedList<Fx_Hexagon> range_list=new LinkedList<>();
        for(int i=0;i<range.size();i++){
        	range_list.add(range.get(i).getFx());
        }
        if(guiunite!=null) {
        for(int i=0;i<guiunite.getSkill().size();i++) {
        	guiunite.getSkill().get(i).setName(this.skillList.get(i).getName());//Dans le cas ou le nom des skills change entre temps 
        	guiunite.getSkill().get(i).setDescription(this.skillList.get(i).description());//Dans le cas ou le nom des skills change entre temps 
        	guiunite.getSkill().get(i).setCost(this.skillList.get(i).cost());//Dans le cas ou le cout des skills change entre temps
        	guiunite.getSkill().get(i).setCd(this.skillList.get(i).getCd());//Set le cd
        	guiunite.getSkill().get(i).setCdMax(this.skillList.get(i).getCdMax());//Dans des cas speciaux ou le cdmax est modifie
        	guiunite.getSkill().get(i).setAvailable(this.skillList.get(i).available());//Set le available informe si un skill est disponible
        	
        	LinkedList<Fx_Hexagon> skill_cible_list=new LinkedList<Fx_Hexagon>();
        	for(int j=0;j<this.skillList.get(i).getrange().size();j++)
        		skill_cible_list.add(this.skillList.get(i).getrange().get(j).getFx());
        	guiunite.getSkill().get(i).setCibles(skill_cible_list);
        }
		for (int i = 0; i < this.guiunite.get_Statuts().size(); i++) {
			guiunite.get_Statuts().get(i).set_tours(this.statuts.get(i).get_tours());
		}
		guiunite.setAttdone(attdone);
        guiunite.setPdvMax(pdvMax);
        guiunite.setpv(pv);
        guiunite.setcamp(camp);
        guiunite.setattaque(this.attaque);
        guiunite.setarmor(this.defensearmor);
        guiunite.setmagique(this.defmagique);
        guiunite.setportee(this.portee);
        guiunite.setprix(this.prix);
        guiunite.setmovable(this.movable);
        guiunite.setattaquable(this.attaquable);
        guiunite.setcastable(this.castable);
        guiunite.setStats(getStats());
        guiunite.setrange(range_list);
        guiunite.setdeplacement(move_list);
        guiunite.setmove(movedone);
        guiunite.setdep(dep);
        guiunite.setX(this.getplace().getx());
        guiunite.setY(this.getplace().gety());
        }
    	}
    	else {
    		if(guiunite!=null) {
    			if(!guiunite.getestmort()) {
    				
    				guiunite.getHexagone().removeFx_Unite();
    				for(Fx_Unite n: guiunite.getOthers()) {
    					n.getHexagone().removeFx_Unite();
    				}
    			}
    			guiunite.setestmort(mort);
    		}
    	}
    }
    
    public void actualisation() {//actualise la portée et les déplacement pour les faire coincider avec le pourtour de l'unité
		actu_range();
		actu_dep(dep);
		for(Skill e:skillList) {
			e.setrange();
		}
	}
    public LinkedList<Hexagone> getrange(){
    	return this.range;
    }
    public LinkedList<Hexagone> getdeplacement(){
    	return this.deplacement;
    }
    public abstract void actu_range();//actualise la range de l'unité
	abstract void actu_dep(int dep);//actualise les deplacement possible

    public String toString() {
    	//return nom+"/"+pv+"/"+attaque+"/"+defense+"/"+portee+" /cost :"+prix;
    	return this.nom+" ("+place.getx()+";"+place.gety()+")";
    }
	public void sub_deplacement(int i) {
		if(dep>0) {
		this.dep-=1;
		}
	}
	
	public void add_deplacement(int i) {
		this.dep+=1;
	}
	
	public  int getcamp() {
		
		return this.camp;
	}
	
	public static int get_Prix() {
		 return 0;
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

	public void setcamp(int camp2) {
		this.camp=camp2;
	}
	
	void dcd() {
	
		if(!estmort()) {	
			this.mort=true;
			this.place.setunite(null);
			this.setplace(null);
		}
		actualise();
		Game.lose();
	}
	
	public Fx_Unite getFx_Unite() {
		return this.guiunite;
	}

	public LinkedList<Skill> getSkillList() {
		// TODO Auto-generated method stub
		return skillList;
	}
	public LinkedList<Statut> getStatuts() {
		// TODO Auto-generated method stub
		return this.statuts;
	}
	public Unite elle() {
		return this;
	}

	public void passtours() {
		for(Skill l:skillList) {
			l.tours();
		}
		
	}
	public interface Healer{}
	public interface Range{}
	public interface Cac{}
	
	
}