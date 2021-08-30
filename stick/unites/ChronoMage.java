package unites;

import java.util.LinkedList;
import java.util.Random;

import application.Fx_Unite;
import controleur.Game;
import carte.Hexagone;

public class ChronoMage extends Unite implements Unite.Healer{

	private static final long serialVersionUID = 1L;
	private int deptype=(int)(Math.random() * 4 + 1);
	private boolean cast=false;
	int tempscast=0;
	public ChronoMage( Hexagone place,Fx_Unite Unite, int camp) {
		super("ChronoMage", 4, 150, 15, 10, 30, 3, place, Unite, camp);
		// TODO Auto-generated constructor stub
		set_Skill(new skill1(),new skill2(),new skill3());
	}
	public ChronoMage() {
		this(null,null,-1);
	}
	public void setplace(Hexagone n) {
		if(cast) {	
	    		cast=false;
	    		
	    		tempscast=0;
	    		castable=true;
	    		attaquable=true;
	    		movable=true;
		}
		this.place=n;
	}
	public void set_attaquable(boolean n){
	    if(cast) {
	    	if(!n && !insensible) {
	    		cast=false;
	    		
	    		tempscast=0;
	    		castable=true;
	    		attaquable=true;
	    		movable=true;
	    	}
	    	else {
	    		return;
	    	}
	    }
		this.attaquable=n;
	    actualise(); 
	}
	public void set_movable(boolean n){
		if(cast) {
	    	if(!n && !insensible) {
	    		cast=false;
	    	
	    		tempscast=0;
	    		castable=true;
	    		attaquable=true;
	    		movable=true;
	    	}
	    	else {
	    		return;
	    	}
	    }
		this.movable=n;
	    actualise(); 
	}	
	public void set_castable(boolean b){
		if(cast) {
	    	if(!b && !insensible) {
	    		cast=false;
	    	
	    		tempscast=0;
	    		castable=true;
	    		attaquable=true;
	    		movable=true;
	    	}
	    	else {
	    		return;
	    	}
	    }
		this.castable=b;
	    actualise(); 
	}
	@Override
	protected void degats_adversaire(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null) {			
			n.getunite().inflige(attaque,2);//20 pV en moins en comptant l'armure
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
		switch(deptype) {
			case 4:normalcase();break;
			case 1:star1();break;
			case 2:star2();break;
			case 3:fullstar();break;
		}
	}

	private void normalcase() {
		// TODO Auto-generated method stub
		deplacement=new LinkedList<Hexagone>();
		deplacement.add(place);	
		for(int i=0;i<dep;i++) {
			int pop=deplacement.size();
			for(int o=0;o<pop;o++) {
				deplacement=deplacement.get(o).add_autour(deplacement);
			}
		}
		deplacement.remove(place);
	}
	private void fullstar() {
		// TODO Auto-generated method stub
				LinkedList<Hexagone> r=new LinkedList<Hexagone>();
				Hexagone lolg=place;
				int ind=0;
				while(lolg.getgauche()!=null && ind<dep && lolg.getgauche().getunite()==null) {
					r.add(lolg.getgauche());
					
					lolg=lolg.getgauche();
					ind++;
				}
				Hexagone lold=place;
				int ind2=0;
				while(lold.getdroit()!=null && ind2<dep && lold.getdroit().getunite()==null) {
					r.add(lold.getdroit());
					
					lold=lold.getdroit();
					ind2++;
				}
				Hexagone loldh=place;
				int ind3=0;
				while(loldh.gethautdroit()!=null && ind3<dep && loldh.gethautdroit().getunite()==null) {
					r.add(loldh.gethautdroit());
					
					loldh=loldh.gethautdroit();
					ind3++;
				}
				Hexagone loldb=place;
				int ind4=0;
				while(loldb.getbasdroit()!=null && ind4<dep && loldb.getbasdroit().getunite()==null) {
					r.add(loldb.getbasdroit());
					
					loldb=loldb.getbasdroit();
					ind4++;
				}
				Hexagone lolgh=place;
				int ind5=0;
				while(lolgh.gethautgauche()!=null && ind5<dep && lolgh.gethautgauche().getunite()==null) {
					r.add(lolgh.gethautgauche());
					
					lolgh=lolgh.gethautgauche();
					ind5++;
				}
				Hexagone lolgb=place;
				int ind6=0;
				while(lolgb.getbasgauche()!=null && ind6<dep && lolgb.getbasgauche().getunite()==null) {
					r.add(lolgb.getbasgauche());			
					lolgb=lolgb.getbasgauche();
					ind6++;
				}
				deplacement=r;
	}

	private void star1() {
		LinkedList<Hexagone> r=new LinkedList<Hexagone>();
		Hexagone lolg=place;
		int ind=0;
		while(lolg.getgauche()!=null && ind<dep && lolg.getgauche().getunite()==null) {
			r.add(lolg.getgauche());
			
			lolg=lolg.getgauche();
			ind++;
		}
		
		Hexagone loldh=place;
		int ind3=0;
		while(loldh.gethautdroit()!=null && ind3<dep && loldh.gethautdroit().getunite()==null ) {
			r.add(loldh.gethautdroit());
			
			loldh=loldh.gethautdroit();
			ind3++;
		}
		Hexagone loldb=place;
		int ind4=0;
		while(loldb.getbasdroit()!=null && ind4<dep && loldb.getbasdroit().getunite()==null) {
			r.add(loldb.getbasdroit());
			
			loldb=loldb.getbasdroit();
			ind4++;
		}
		deplacement=r;
	}
	private void star2() {
		LinkedList<Hexagone> r=new LinkedList<Hexagone>();
		Hexagone lold=place;
		int ind2=0;
		while(lold.getdroit()!=null && ind2<dep && lold.getdroit().getunite()==null) {
			r.add(lold.getdroit());
			
			lold=lold.getdroit();
			ind2++;
		}
		Hexagone lolgh=place;
		int ind5=0;
		while(lolgh.gethautgauche()!=null && ind5<dep && lolgh.gethautgauche().getunite()==null ) {
			r.add(lolgh.gethautgauche());
			
			lolgh=lolgh.gethautgauche();
			ind5++;
		}
		Hexagone lolgb=place;
		int ind6=0;
		while(lolgb.getbasgauche()!=null && ind6<dep && lolgb.getbasgauche().getunite()==null) {
			r.add(lolgb.getbasgauche());			
			lolgb=lolgb.getbasgauche();
			ind6++;
		}
		deplacement=r;
		
		
	}


public class skill1 extends Skill implements Skill.Buff{
	{
		name="Acceleration du temps";
		description="Diminue le temp de recharge d'une unité de moitié";
		sub_description="";
		cout=5;
		cdmax=4;
	}
	@Override
	public void use(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null) {
			for(Skill s:n.getunite().getSkillList()) {
				if(s.getCd()>=0) {
				s.setcd(s.getCd()/2);
				}
			}
			n.getunite().actualise();
		}
	}

	@Override
	public void setrange() {
		LinkedList<Hexagone> rang=new LinkedList<Hexagone>();
		LinkedList<Unite> n= Game.getarmy(1);
		LinkedList<Unite> m= Game.getarmy(2);
		for(Unite v:n) {
			if(v.mort!=true && v.place!=null) {
				rang.add( v.getplace());
				rang.addAll(v.others);
			}
		}
		for(Unite v:m) {
			if(v.mort!=true && v.place!=null) {
				rang.add( v.getplace());
				rang.addAll(v.others);
			}
		}
		this.rang=rang;
	}
	
	
}


public class skill2 extends Skill implements Skill.Defensif{
	boolean rappel=false;
	Unite p=null;
	Hexagone a=null;
	{	
		name="Rembobinage";
		description="Cible une unité, c'est sur cette unité que le rappel sera appliqué";
		sub_description="";
		cout=4;
		cdmax=7;
	}
	@Override
	public String getName() {
		if(!rappel)return name;
		return "Rappel";
	}
	@Override
	public String description() {
		if(!rappel)return description;
		return "Renvoie l'unité sur lequel Rembobinage a été lancé ("+p.toString()+" ) à la position où elle était au moment du cast ("+a.toString()+" ), si la place de l'unité est prise alors le sort est inefficace";
	}
	@Override
	public void use(Hexagone n) {
		if(!rappel) {
			if(n.getunite()!=null) {
				rappel=true;
				p=n.getunite();
				a=n;
			}
		}
		else {
			rappel=false;
			if(p.obstruction(a) && a!=null && p!=null && !p.estmort()) {
				p.deplacementf(a);
			
				p.actualisation();
				p.actualise();
			}
			a=null;
			p=null;
			cd=cdmax;
		}
		
	}

	@Override
	public void setrange() {
		// TODO Auto-generated method stub
		if(!rappel) {
			LinkedList<Hexagone> rang=new LinkedList<Hexagone>();
			LinkedList<Unite> n= Game.getarmy(1);
			LinkedList<Unite> m= Game.getarmy(2);
			for(Unite v:n) {
				if(v.place!=null){
				rang.add( v.getplace());
				rang.addAll(v.others);
				}
			}
			for(Unite v:m)
				if(v.place!=null){
				rang.add( v.getplace());
				rang.addAll(v.others);
				}
			this.rang=rang;
		}
		else {
			LinkedList<Hexagone> l=new LinkedList<Hexagone>();
			l.add(place);
			rang=l;
		}
	}
	
}

public class skill3 extends Skill implements Skill.Defensif{
	Unite dead;
	Unite proposer;

	{	
		name="Sauvetage Temporel";
		description="";
		sub_description="";
		cout=9;
		cdmax=13;
	}
	
	@Override
	public String description() {
		if(!cast)
			if(proposer==null) {
				return "Aucune de vos unités allié n'est morte dans les tours précedent pour le moment, vous ne pouvez donc pas en ressusciter";
			}
			else {
				return "Permet de lancer un cast pour ressusciter votre "+proposer.get_Nom()+" mort";
			}
		else {
			
			return "Le chronoMage se concentre et va ressusciter l'unité dans "+tempscast+" tours et la placer avec tous ses pv devant lui, si cette place est prise ou si le cast est interrompu alors"
					+ " le sort est annulé et son temps de récupération est réduit de moitié";
		}
	}
	@Override
	public void use(Hexagone n) {
		// TODO Auto-generated method stub
		if(n==place) {
			cast=true;
			dead=proposer;
			proposer=null;			
			tempscast=2;
			attaquable=false;
			castable=false;
			movable=false;
			actualisation();
			actualise();
			cd=cdmax;
		}
	}

	@Override
	public void setrange() {
		// TODO Auto-generated method stub
		rang=new LinkedList<Hexagone>();
		rang.add(place);
	}
	public boolean available() {//Informe sur la disponibilite de la competence, Par defaut, est disponible lorsque le cd vaut 0
		if(proposer!=null) {
				return cd==0;
			
		}
		return false;
	}
	void setproposer() {
		LinkedList<Unite> de=new LinkedList<Unite>();
		LinkedList<Unite> n=Game.getarmy(camp);
		for(Unite v:n) {
			if(v.estmort()) {
				de.add(v);
			}
		}
		if(de.size()==0) {
			proposer=null;
		}
		else {
			int len = de.size(); 
			Random randomGenerator = new Random();
			int randomInt = randomGenerator.nextInt(len);
			proposer=de.get(randomInt);
		
		}
	}
	public void tours() {
		if(cast) {
			if(tempscast==0) {
				attaquable=true;
				castable=true;
				movable=true;
				
				cast=false;
				if(camp==1) {
					if(place.getdroit()!=null && dead.obstruction(place.getdroit()) && dead.mort) {
						dead.set_pv(dead.pdvMax);
						dead.mort=false;
						dead.guiunite.setestmort(false);
						dead.deplacementf(place.getdroit());
						
						dead.actualise();
						dead.actualisation();
						dead=null;
						setproposer();
					}
					else {
						
						cd=cd/2;
					}
				}
				else if(camp==2) {
					if(place.getgauche()!=null && dead.obstruction(place.getgauche())&& dead.mort) {
						dead.set_pv(dead.pdvMax);
						dead.mort=false;
						dead.guiunite.setestmort(false);
						dead.deplacementf(place.getgauche());
						dead.actualise();
						dead.actualisation();
						dead=null;
						setproposer();
					}
					else {
						cd=cd/2;
					}
				}
			}
			else {
				
				tempscast--;
			}
			
		}
		else {
			if(cd>0)cd--;
			else {
				setproposer();
			}
			setrange();
			}
		}
}
public static int get_Prix() {
	 return 175;
}
	public void passtours(){
		int random = (int)(Math.random() * 4 + 1);
		deptype=random;
		actualisation();
		actualise();
		for(Skill l:skillList) {
			l.tours();
		}
	}
	@Override
	protected String description_attaque() {
		return "Inflige "+attaque+" de dégat(s) physique à la cible";
	}
	@Override
	protected String passif() {
		return "Ses attaques de base infligent des dégats physiques";
	}
	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "Le Chronomage est un support qui est très utile pour sortir les unités des mauvaises situations ou les resuciter si ces derniers sont mortes, sa range de déplacments change aléatoirement à chaque tours";
	}
}
