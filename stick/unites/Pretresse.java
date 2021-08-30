package unites;

import java.util.LinkedList;

import Statut.Encenser;
import Statut.Insensibiliter;
import Statut.Statut;
import application.Fx_Unite;
import controleur.Game;
import carte.Hexagone;

public class Pretresse extends Unite implements Unite.Healer{
	private int PA=0;
	private int nbtours=0;
	private int nbtoursmax=3;
	private boolean priere=false;

	public Pretresse( Hexagone place,Fx_Unite Unite, int camp) {
		super("Pretresse", 3, 125, 15, 10,25, 3, place, Unite, camp);
		// TODO Auto-generated constructor stub
		set_Skill(new Priere(),new Expiation(),new Encens());
	}
	public Pretresse() {
		this(null,null,-1);
	}
	private static final long serialVersionUID = 1L;

	public String[] getStats() {
		if(priere) {
			stats[6]="Prie ("+nbtours+")";
		}
		else {
		stats[6]="Louanges: "+PA;
		}
		return super.getStats();
	}
	public static int get_Prix() {
		 return 180;
	}
	public void setplace(Hexagone n) {
		if(priere) {	
	    		priere=false;
	    		PA=0;
	    		nbtours=0;
	    		castable=true;
	    		attaquable=true;
	    		movable=true;
		}
		this.place=n;
	}
	public void set_attaquable(boolean n){
	    if(priere) {
	    	if(!n && !insensible) {
	    		priere=false;
	    		PA=0;
	    		nbtours=0;
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
		if(priere) {
	    	if(!n && !insensible) {
	    		priere=false;
	    		PA=0;
	    		nbtours=0;
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
		if(priere) {
	    	if(!b && !insensible) {
	    		priere=false;
	    		PA=0;
	    		nbtours=0;
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
	
public class Priere extends Skill implements Skill.Neutre{

		{
			name="Prière";
			description="La pretresse se met à prier durant trois tours, à la fin tous ses louanges sont converties en PA, "
					+ "la pretresse peut être interrompue avec un stun, un silence ou un sort la déplacant ou sa mort";
			sub_description="";
			cout=2;
			cdmax=5;
		}
		@Override
		public void use(Hexagone n) {
			// TODO Auto-generated method stub
			priere=true;
			nbtours=nbtoursmax;
			attaquable=false;
			castable=false;
			movable=false;
			actualisation();
			actualise();
			cd=cdmax;
		}
		@Override
		public void tours() {
			if(priere) {
				if(nbtours!=0) {
					nbtours--;
				}
				if(nbtours==0) {
					Game.getPlayer(camp).add_PA(PA);
					place.getFx().getVue().setPA(Game.getPlayer(camp).getPA());
					PA=0;
					attaquable=true;
					castable=true;
					movable=true;
					priere=false;
					
				}
				setrange();
				actualise();
			}
			else {
				if(cd>0)cd--;
				setrange();
				actualise();
			}
		}
		@Override
		public void setrange() {
			// TODO Auto-generated method stub
			rang=new LinkedList<Hexagone>();
			rang.add(place);
		}
		
	}

public class Expiation extends Skill implements Skill.Defensif,Skill.Buff{
	{
		name="Expiation des péchés";
		description="La pretresse entend la confession d'une unité, lui enlevant tous ses statuts et la rendant insensible pour 1 tour, elle récupere 1+1 louanges par statut oté";
		sub_description="";
		cout=4;
		cdmax=5;
	}
	@Override
	public void use(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null) {
			PA++;
			LinkedList<Statut> p=new LinkedList<Statut>();
			p.addAll(n.getunite().getStatuts());
			for(Statut m:p) {
				n.getunite().removestatut(m);
				PA++;
				actualise();
			}
			n.getunite().add_Statut(new Insensibiliter(2,n.getunite()));
			actualisation();
			actualise();
			cd=cdmax;
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
	}
}


public class Encens	extends Skill implements Skill.SelfHeal,Skill.Heal{
	
	{
		name="Encens";
		description="La pretresse embaume d'encens une cible, cette dernière est encensée durant 3 tours, la pretresse gagne 1 louange +1 par soin provoqué par l'encens";
		sub_description="";
		cout=4;
		cdmax=4;
	}
	@Override
	public void use(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null) {
			n.getunite().add_Statut(new Encenser(3,n.getunite(),Pretresse.this));
			PA+=1;
			actualise();
			n.getunite().actualise();
			cd=cdmax;
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
	@Override
	protected void degats_adversaire(Hexagone n) {
		// TODO Auto-generated method stub
		if(n.getunite()!=null) {
			n.getunite().inflige(attaque,0);//20 pV en moins en comptant l'armure
			if(n.getunite().estmort()) {
				add_louange(2);
			}
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
	public void add_louange(int i) {
		PA+=i;
		actualise();
	}
	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige "+attaque+" de dégâts physique(s) à l'unité, si cette unité est tuée, la pretresse gagne 2 louanges";
	}
	@Override
	protected String passif() {
		return "Tuer une cible lui rapporte des louanges";
	}
	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "La pretresse est un support qui peut grace à ses sorts accumuler des louanges, ces louganes peuvent être converties en PA pour le joueur au bout d'un certain temps";
	}
}


