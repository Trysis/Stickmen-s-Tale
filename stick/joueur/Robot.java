package joueur;

import java.util.LinkedList;
import java.util.Random;
import application.Fx_Hexagon;
import application.Fx_Unite;
import controleur.Game;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import carte.Hexagone;
import carte.Plateau;
import unites.*;
import unites.Skill.Degat_autour;
import unites.Skill.Heal_autour;
import unites.Skill.Neutre;


public class Robot extends Joueur{
	
	private Plateau p;
	private Game g;
	private Joueur adversaire;
	private int dep = 0;
	private LinkedList<Fx_Unite> fx = new LinkedList<Fx_Unite>();
	private int moneyBis =0;
	private LinkedList<Unite> invocatrices = new LinkedList<>();//on les traite a part pour que l'ajout dans la liste d'unites ne pose pas probleme
	public boolean advRobot = false;
	
	public Robot(int b, int m, int pa,Plateau p,Game g) {
		super(b, m, pa);
		this.p = p;
		this.g=g;
		moneyBis = getMoney();
	}			
	
	private boolean achat() {
		return moneyBis>=50;
	}
	
	public void instancie() {	//instanciation des unites
		String s [] = {"Archer",
				"BomberMan","Chasseur",
				"Faucheuse","Fee",
				"Guerrier","Malade","Assassin"				
		
				}; 
		int alea = 0;	
		int inc = 2;//en fonction de la taille de la zone de depart, pour une répartition homogène
		if(p.getDepartPlayer(getcamp()).size()<15)inc=1;
		if(getcamp()==1) {
			for (int i =  this.p.getDepartPlayer(this.getcamp()).size()-1 ; i>=0;i=i-inc ) {
				alea = new Random().nextInt(s.length);
				if(achat() && this.p.getDepartPlayer(this.getcamp()).get(i).getunite()==null && this.p.getDepartPlayer(this.getcamp()).get(i).getterrain()==null) {
					Fx_Unite tmp = new Fx_Unite(this.p.getDepartPlayer(this.getcamp()).get(i).getFx());
					tmp.setNom(s[alea]);
					tmp.setprix(g.getprix(tmp));
					if(moneyBis - tmp.getPrix()>=0) {
						moneyBis-=tmp.getPrix();
						tmp.setcamp(this.getcamp());
						tmp.setX(this.p.getDepartPlayer(this.getcamp()).get(i).getx());
						tmp.setY(this.p.getDepartPlayer(getcamp()).get(i).gety());
						tmp.dessineImage(false);
						fx.add(tmp);
					}else {
						tmp.getHexagone().removeFx_Unite();
						tmp.setHexagone(null);
					}
				}	
			}
		}
		else if (getcamp()==2) {
            for (int i =  0 ; i<this.p.getDepartPlayer(getcamp()).size();i=i+inc ) {
                alea = new Random().nextInt(s.length);
                if(achat() && this.p.getDepartPlayer(getcamp()).get(i).getunite()==null && this.p.getDepartPlayer(getcamp()).get(i).getterrain()==null) {
                    Fx_Unite tmp = new Fx_Unite(this.p.getDepartPlayer(getcamp()).get(i).getFx());
                    tmp.setNom(s[alea]);
                    tmp.setprix(g.getprix(tmp));
                    if(moneyBis - tmp.getPrix()>=0) {
                        tmp.setcamp(getcamp());
                        tmp.setX(this.p.getDepartPlayer(getcamp()).get(i).getx());
                        tmp.setY(this.p.getDepartPlayer(getcamp()).get(i).gety());
                        moneyBis-=tmp.getPrix();
                        tmp.dessineImage(false);
                        fx.add(tmp);
                    }else {
                        tmp.getHexagone().removeFx_Unite();
                        tmp.setHexagone(null);
                    }
                }
            }
        }
			g.updateBoutique(fx);
			if(getcamp()==2) {
				reset_liste();
			}
			if(adversaire instanceof Robot)advRobot=true;
	}
	

	private void reset_liste() {		
		LinkedList<Unite> nouvelleListe = new LinkedList<>();
		for (Unite unite : units) {
			nouvelleListe.addFirst(unite);
		}
		this.units=nouvelleListe;
	}

	public void choix() {		
		sauvetage();//sauve les units entrain de mourir
		deplacement();
		attaque();
}	
	
	
	/*	 			Fonctions pour l'attaque			*/
	
	private void attaque() {
		for (Unite unite : units) {
			Hexagone choix = choixAttaque(unite);
			if(choix!=null && this.getPA()>0) {
				updateAttaque(unite, choix.getx(), choix.gety());
			}
		}
	}

	private Hexagone choixAttaque(Unite unite) {
		Hexagone choix = null;
		//sorts:
		if(!unite.estmort() && this.getPA()>0 && unite.getcastable()) {
			Skill s = choixSort(unite);
			if(s!=null) {
				choix = choixCible(unite,s);
				if(choix!=null)updateSort(unite, unite.getSkillList().indexOf(s), choix.getFx().getX(), choix.getFx().getY());
			}	
		//Sinon, attaque basique : 
		Unite victime = getUneAdv(unite);
		if(victime !=null) {
			choix = victime.getplace();
		}
		}
		return choix;
	}
	
	private Hexagone choixCible(Unite unite, Skill s) {
		Hexagone cible = null;
		for (Hexagone h : s.getrange()) {
			if(h!=null) {
					if(s instanceof Skill.Attaque) {
						if(h.getunite()!=null && h.getunite().getcamp()!=unite.getcamp() && h.getunite().getcamp()!=-1) {	
							if(h.getunite().get_defensemagique()<h.getunite().get_defensearmor()) {
								cible = h;//si def magique faible
							}
							break;//sinon on sort pour l'attaquer "basiquement"
							}
						}
						if((s instanceof Degat_autour ||  s instanceof Heal_autour) && unite.getplace()!=null) {
							cible=unite.getplace();
							break;
						}
						if(s instanceof Skill.Offensif &&  h.getunite()!= null && h.getunite().getcamp()!=unite.getcamp() && h.getunite().getcamp()!=-1) {
							cible = h;
							break;
						}
						
						else if(s instanceof Skill.Defensif && h.getunite()!=null && h.getunite().getcamp()==getcamp()) {
							cible = h;
							break;
						}
						else if (!(s instanceof Skill.Offensif) && !(s instanceof Skill.Defensif)) {
							 cible = h;
							 break;
						 }		
					else if(s instanceof Skill.Heal && h.getunite()!=null && h.getunite().getcamp()==unite.getcamp() &&  h.getunite().get_pv()<h.getunite().get_pvMax()/2) {
						cible=h; 
						break;
					}
					else if(s instanceof Skill.cc && h.getunite()!=null && h.getunite().getcamp()!=unite.getcamp() &&  (h instanceof Unite.Healer || h instanceof Unite.Range)) {
						cible=h; 
						break;
					}
					else if(s instanceof Skill.Buff && h.getunite()!=null && h.getunite().getcamp()==unite.getcamp()) {
						cible=h; 
						break;
					}
					else if(s instanceof Skill.Neutre && h.getunite()==null && (h.getunite()==null) ||  (h.getunite()!=null && h.getunite().getcamp()!=-1)) {
						 cible = h;
						 break;
						
					}	
			}		
		}	
		return cible;
	}
	
	private Skill choixSort(Unite unite) {//que les sorts d'attaque ou de terrains les sorts de soins sont dans save()
		Skill choix = null;
		if(unite.getSkillList().size()>0) {
			int alea = new Random().nextInt(unite.getSkillList().size());
				if(unite.getSkillList().get(alea) instanceof Skill.Attaque || unite.getSkillList().get(alea) instanceof Skill.Offensif) {
					if(contientUneAdvSort(unite.getSkillList().get(alea))) {
						if(this.getPA()-unite.getSkillList().get(alea).cost()>=0)choix = unite.getSkillList().get(alea);
					}
				}
				else if(unite.getSkillList().get(alea) instanceof Skill.Degat_autour && contientdesgensproches(unite,unite.getSkillList().get(alea))) {//regarde si une unité a besoin de soin 
					return unite.getSkillList().get(alea);
				}
				else if(unite.getSkillList().get(alea) instanceof Skill.Heal && contientUnAmiLow(unite,unite.getSkillList().get(alea))) {//regarde si une unité a besoin de soin 
					return unite.getSkillList().get(alea);
				}
				else if(unite.getSkillList().get(alea) instanceof Skill.Heal_autour && contientUnAmiLowautour(unite,unite.getSkillList().get(alea))) {//regarde si une unité a besoin de soin 
					return unite.getSkillList().get(alea);
				}
				else if(unite.getSkillList().get(alea) instanceof Skill.cc && contientUneVictime(unite,unite.getSkillList().get(alea))) {//regarde si une unité est une healer ou un range en range pour le cc
					choix = unite.getSkillList().get(alea);
				}
				
				else if(unite.getSkillList().get(alea) instanceof Skill.Buff && contientUnAmi(unite,unite.getSkillList().get(alea))) {//regarde si on peu buff une unité
					choix = unite.getSkillList().get(alea);
				}
				else if(unite.getSkillList().get(alea) instanceof Neutre) {
					choix=unite.getSkillList().get(alea);
				}
		}
		return choix;		
	}
	
	private boolean contientUnAmiLowautour(Unite unite, Skill skill) {
		// TODO Auto-generated method stub
		LinkedList<Hexagone> deplacement=new LinkedList<Hexagone>();
		deplacement.add(unite.getplace());	
		for(int i=0;i<1;i++) {
			int pop=deplacement.size();
			for(int o=0;o<pop;o++) {
				deplacement=deplacement.get(o).add_autour(deplacement);
			}
		}
		int worth=0;
		deplacement.remove(unite.getplace());
		for(Hexagone  m:deplacement) {
			if(m.getunite()!=null && m.getunite().getcamp()==unite.getcamp() && m.getunite().get_pv()==m.getunite().get_pvMax()/2) {
				worth++;
				if(worth==2) {
					return true;
				}
			}
		
		}
		return false;
	}

	private boolean contientdesgensproches(Unite unite, Skill skill) {
		LinkedList<Hexagone> deplacement=new LinkedList<Hexagone>();
		deplacement.add(unite.getplace());	
		for(int i=0;i<1;i++) {
			int pop=deplacement.size();
			for(int o=0;o<pop;o++) {
				deplacement=deplacement.get(o).add_autour(deplacement);
			}
		}
		int worth=0;
		deplacement.remove(unite.getplace());
		for(Hexagone  m:deplacement) {
			if(m.getunite()!=null && m.getunite().getcamp()!=unite.getcamp()) {
				worth++;
				if(worth==2) {
					return true;
				}
			}
		
		}
		return false;
	}

	private boolean contientUnAmi(Unite unite, Skill skill) {
		// TODO Auto-generated method stub
		for(Hexagone n:skill.getrange()) {
			if(n.getunite()!=null && n.getunite().getcamp()!=unite.getcamp()) {
				return true;
			}
		}
		return false;
	}

	private boolean contientUneVictime(Unite unite, Skill skill) {
		// TODO Auto-generated method stub
		for(Hexagone n:skill.getrange()) {
			if(n.getunite()!=null && n.getunite().getcamp()!=unite.getcamp() && n.getunite() instanceof Unite.Range || n.getunite() instanceof Unite.Healer) {
				return true;
			}
		}
		return false;
	}

	private boolean contientUnAmiLow(Unite unite, Skill skill) {
		// TODO Auto-generated method stub
		for(Hexagone n:skill.getrange()) {
			if(n.getunite()!=null && n.getunite().getcamp()==unite.getcamp() && n.getunite().get_pv()<n.getunite().get_pvMax()/2) {
				return true;
			}
		}
		return false;
	}

	public void invocations() {
		set_invocatrices();
		if(invocatrices!=null) {
			for (Unite u : invocatrices) {
				for (Skill s : u.getSkillList()) {
					if(s instanceof Skill.Invocateur) {
						if(this.getPA()/3-s.cost()>=0) {
							Hexagone place = choixInvocation(u,s);	
							if(u!=null && u.getSkillList()!=null && place!=null)updateSort(u, u.getSkillList().indexOf(s), place.getx(), place.gety());
						}
					}
				}
			}
		}	
}

private void set_invocatrices() { //mieux diviser les PA pour en laisser aux autres sorts
	for (Unite unite : units) {
		for (Skill s : unite.getSkillList()) {
			if(s instanceof Skill.Invocateur && !invocatrices.contains(unite))invocatrices.add(unite);
		}
	}
}

private Hexagone choixInvocation(Unite u, Skill s) {
	Hexagone r = null;
	for (Hexagone h : s.getrange()) {
		if(h.getunite()==null && h.getterrain()==null)r = h;break;
	}
	return r;
}




/* 				Fonctions pour le sauvetage 		*/
	
	private void sauvetage() {
		for (Unite unite : units) {
			if(!unite.estmort() && unite.get_pv()<unite.get_pvMax()/1.5) {//Il faut sauver l'unite
				save(unite);	
			}
		}		
	}
	private void save(Unite unite) {	//ou alors un rappel à save ou à sa derniere partie apres les deplacements 
			//Si elle a des skills qui lui permettent de se soigner :
			for(Skill l : unite.getSkillList()) {
				if(l instanceof Skill.SelfHeal || l instanceof Skill.Buff && this.getPA()-l.cost()>=0) {
					if(!unite.estmort()) {
						updateSort(unite, unite.getSkillList().indexOf(l), unite.getplace().getx(), unite.getplace().gety());//se soigne elle meme
					}
				}
			}
			for (Unite u : units) {//recherche d'units de son camp qui pourrait la soigner à proximite
				if(!u.equals(unite) && !u.estmort()) {
					for (Skill s : u.getSkillList()) {
						if(s instanceof Skill.Heal || s instanceof Skill.Buff) {
							if(s.getrange().contains(unite.getplace()) && this.getPA()-s.cost()>=0) {
								updateSort(u, u.getSkillList().indexOf(s), unite.getplace().getx(), unite.getplace().gety());;//PA/3 
							}
						}
					}
				}
			}
		
	}
	
	
	/*	 			Fonctions pour les deplacements		*/
	
	private void deplacement() {	
		g.getVue().getTour().setDisable(true);
        Timeline timeline = new Timeline();
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(3000)));
        timeline.play();
        timeline.setOnFinished(e->{
            g.getVue().getTour().setDisable(false);
        });
		for (Unite unite : units) {
			Hexagone choix = deplacementChoix(unite);
			if(choix!=null && choix.getunite()==null && unite.getmovable() && unite.getdeplacement().size()>0) { //la place n'est pas prise
				LinkedList<Hexagone> chemin = dij(unite.getplace(),choix);
				LinkedList<Fx_Hexagon> l = conv(chemin);
				g.updateDeplacement(unite.getFx_Unite(), l);
			}else {
				dep++;	
			}
			if(dep == getUnits().size()) {
					
				   g.getVue().getTour().setDisable(false);
				   dep = 0;
				   if(!advRobot)g.updateTours();
				}
		}
		
	}

	private LinkedList<Fx_Hexagon> conv(LinkedList<Hexagone> chemin) { //convertit la liste de dep d'hexagones en Fx_Hexagon
		LinkedList<Fx_Hexagon> r = new LinkedList<>();
		for (Hexagone h : chemin) r.add(h.getFx());
		return r;
	}
	

	private Hexagone deplacementChoix(Unite unite) {
		Hexagone destination = null;
		boolean b=false;
		if(unite instanceof Unite.Healer) {//Si jamais une unite est à range d'attaque d'un adverse elle va alors fuire, les range ne fuiront uqe les cac		
			for(Unite p :adversaire.units) {
				if(p.getrange().contains(unite.getplace())) {				
					b=true;
					break;
				}
			}
			if(b && !finDeJeu()) {
				destination=fuir(unite);
			}
		}
		else if(unite instanceof Unite.Range) {		
			for(Unite p :adversaire.units) {
				if(p instanceof Unite.Cac && p.getrange().contains(unite.getplace())) {
					b=true;
					break;
				}
			}
			if(b && !finDeJeu()) {
				destination=fuir(unite);
			}
		}
		else if(unite.get_pv()<unite.get_pvMax()/3) {
			destination = fuir(unite);
		}
			
		
		if(!b && !contientUneAdv(unite) && unite.get_pv()>unite.get_pvMax()/4) {//si il a pas deja qqch dans sa range et s'il n'est pas lowhp
			destination = versUniteAdv(unite);
			if(destination == null) {
				if(getcamp()==1) {
					if(unite.getplace() !=null && this.p.getwidth()/2< unite.getplace().getx()) destination = randomDep(unite);
					else destination = plusADroite(unite);
				}
				else if(getcamp()==2) {
					if(unite.getplace()!=null && this.p.getwidth()/2>unite.getplace().getx())destination = randomDep(unite);
					else destination = plusAGauche(unite);
				}
			}
		}
		return destination;
	}
	private Hexagone fuir(Unite unite) {
		Hexagone tmp = null;
		for (int i = 0; i < unite.getdeplacement().size(); i++) {
			if(getcamp()==1) tmp = plusAGauche(unite);
			if(getcamp()==2) tmp = plusADroite(unite);
		}
		return tmp;
	}
	private boolean contientUneAdv(Unite unite) {
		if(unite instanceof Unite.Healer) {//Si c'est un soigneur alors il va regarder si il est a range de ses amis et ne pas bouger sinon
			for (Unite a : units) {
				for(Skill l:unite.getSkillList()) {
					if(l.getrange().contains(a.getplace()))return true;	
				}
			}
		}
		else {
			for (Unite a : adversaire.units) {
				if(unite.getrange().contains(a.getplace()))return true;	
			}
		}
		return false;
	}
	private boolean contientUneAdvSort(Skill s) {
		for (Unite a : adversaire.units) {
			if(s.getrange().contains(a.getplace()))return true;	
		}
		return false;
	}
	
	private Unite getUneAdv(Unite unite) {
		if(contientUneAdv(unite)) {
			for (Unite a : adversaire.units) {
				if(unite.getrange().contains(a.getplace()))return a;	
			}
		}
		for (Hexagone h : unite.getrange()) {
			if(h.getunite()!=null && h.getunite() instanceof Obstacle)return h.getunite();
		}
		return null;		
	}

	private Hexagone versUniteAdv(Unite u) { // Test les deplacements pour une unité
		Hexagone meilleurePlace = null;
			if(!u.estmort() && u.getmovable()) {
				for (Hexagone hexagone : u.getdeplacement()) {
					if(hexagone.getterrain()==null) {//si l'hex n'a pas de terrain pour éviter les effets
						Hexagone anciennePlace = u.getplace();
						u.setplace(hexagone);
						hexagone.setunite(u);
						u.actu_range();
						int value=0;
						int thisone=0;
						for (Unite a :adversaire. units) {
							thisone=0;//Ceci permet de mettre la priorité sur les unités qui sont des Healer ou des range
							if(a instanceof Unite.Healer) {
								thisone=3;
							}
							if(a instanceof Unite.Range) {
								thisone=2;
							}
							if(a instanceof Unite.Cac) {
								thisone=1;
							}
							for (Hexagone h : u.getrange()) {
								if(h.equals(a.getplace()) && thisone>value) {
									value=thisone;
									meilleurePlace = hexagone;
								}
							}			
						}
						u.setplace(anciennePlace);
						u.actu_range();
						hexagone.setunite(null);
					}				
				}
			}
		return meilleurePlace;	//retour du meilleur hexagone pour se deplacer
	}

	private boolean finDeJeu() {
		if(this.units.size()==1)return true;
		return false;
	}
	
	private LinkedList<Hexagone> dij(Hexagone dep,Hexagone cible){
		LinkedList<Hexagone> res=new LinkedList<Hexagone>();
		LinkedList<Node> g=getGraph(dep);
		Node m= search(g,cible);
		Node source=search(g,dep);
		while(m!=source){
			res.addFirst(m.content);
			m=m.shortest;
		}
		res.addFirst(m.content);

		return res;
	}
	
	public Node search(LinkedList<Node> p,Hexagone d) {
		if(d==null) {

			return null;
		}
		for(Node m:p) {
			if(m.content==d) {
				
				return m;
			}
		}
		return null;
	}
	
	Node minimum(LinkedList<Node> p) {
		int min=p.getFirst().distance;
		Node res=p.getFirst();
		for(Node n:p) {
			if(min>n.distance && n.distance!=-1) {
				min=n.distance;
				res=n;
			}
		}
		return res;
	}
	boolean allin(Node n,LinkedList<Node> vis) {
		boolean accept=true;
		if(n.gauche!=null) {
			if(!vis.contains(n.gauche))accept=false;
		}
		if(n.hautgauche!=null) {
			if(!vis.contains(n.hautgauche))accept=false;
		}
		if(n.basgauche!=null) {
			if(!vis.contains(n.basgauche))accept=false;
		}
		if(n.droit!=null) {
			if(!vis.contains(n.droit))accept=false;
		}
		if(n.basdroit!=null) {
			if(!vis.contains(n.basdroit))accept=false;
		}
		if(n.basgauche!=null) {
			if(!vis.contains(n.basgauche))accept=false;
		}
		return accept;
	}
	public LinkedList<Node> getGraph(Hexagone depart) {
		LinkedList<Node> unvisited=new LinkedList<Node>();
		LinkedList<Node> all=new LinkedList<Node>();
		LinkedList<Node> visited=new LinkedList<Node>();
		for(Hexagone b: depart.getunite().getdeplacement()) {
			all.add(new Node(b));		
		}
		Node dep=new Node(depart);
		unvisited.addFirst(dep);
		dep.distance=0;
		dep.droit=search(all,dep.content.getdroit());
		dep.hautdroit=search(all,dep.content.gethautdroit());
		dep.basdroit=search(all,dep.content.getbasdroit());
		dep.gauche=search(all,dep.content.getgauche());
		dep.hautgauche=search(all,dep.content.gethautgauche());
		dep.basgauche=search(all,dep.content.getbasgauche());
		for(Node j: all) {
			j.droit=search(all,j.content.getdroit());
			j.hautdroit=search(all,j.content.gethautdroit());
			j.basdroit=search(all,j.content.getbasdroit());
			j.gauche=search(all,j.content.getgauche());
			j.hautgauche=search(all,j.content.gethautgauche());
			j.basgauche=search(all,j.content.getbasgauche());
		}

		while(unvisited.size()!=0) {
			Node j=minimum(unvisited);
			if(j.hautdroit!=null  && !visited.contains(j.hautdroit)) {
				unvisited.addLast(j.hautdroit);

				if(j.hautdroit.distance==-1 || (j.hautdroit.distance>j.distance && j.hautdroit.distance!=-1)) {
					j.hautdroit.setShort(j);
			}
			}
			if(j.droit!=null && !visited.contains(j.droit)) {						
				unvisited.addLast(j.droit);

				if (j.droit.distance==-1 || (j.droit.distance>j.distance && j.droit.distance!=-1)) {
					j.droit.setShort(j);
			}
			}
			if(j.basdroit!=null  && !visited.contains(j.basdroit)) { 
				unvisited.addLast(j.basdroit);

				if(j.basdroit.distance==-1 || (j.basdroit.distance>j.distance && j.basdroit.distance!=-1)) {
					j.basdroit.setShort(j);				
			}
			}
			if(j.hautgauche!=null  && !visited.contains(j.hautgauche)) { 
				unvisited.addLast(j.hautgauche);

				if(j.hautgauche.distance==-1 ||( j.hautgauche.distance>j.distance && j.hautgauche.distance!=-1 )) {
					j.hautgauche.setShort(j);				
				}
			}
			if(j.gauche!=null  && !visited.contains(j.gauche)) {
					unvisited.addLast(j.gauche);

					if(j.gauche.distance==-1  || (j.gauche.distance>j.distance && j.gauche.distance!=-1)) {
					j.gauche.setShort(j);				
				}
			}
			if(j.basgauche!=null  && !visited.contains(j.basgauche)) {
				unvisited.addLast(j.basgauche);
				if(j.basgauche.distance==-1 || (j.basgauche.distance>j.distance && j.basgauche.distance!=-1)) {
					j.basgauche.setShort(j);				
				}			
			}
			visited.add(j);
			unvisited.remove(j);
			}		
		return visited;	
	}

	public class Node{
			public Node shortest;
			int distance;
			public final Hexagone content;
			Node droit,hautdroit,basdroit,gauche,hautgauche,basgauche;
			
			Node(Hexagone p){
				content=p;
				distance=-1;
				shortest=null;
				
			}
			
			void setShort(Node n) {
				distance=n.distance+1;
				shortest=n;
			}	
			public String toString() {
				return content.toString();
			}
		}
	
	private Hexagone plusADroite(Unite a) {
		if(!a.estmort() && a.getplace().getdroit()!=null) {
				if(a.getplace().getdroit().getunite()==null) {
					Hexagone res =  a.getplace().getdroit();
					if(res!=null) {
						while(res.getdroit()!=null && res.getdroit().getunite()==null && a.getdeplacement().contains(res.getdroit())) { //et que pas de terrain ?
							res = res.getdroit();
						}
					}
					return res;
				}else {
					return contourne(a);
				}
			}		
		return null;
	}

	private Hexagone plusAGauche(Unite a) {//retourne l'hexagone le plus a gauche pour se deplacer
		if(!a.estmort() && a.getplace().getgauche()!=null) {
			if(a.getplace().getgauche().getunite()==null) {
				Hexagone res =  a.getplace().getgauche();
				if(res!=null) {
					while(res.getgauche()!=null && res.getgauche().getunite()==null && a.getdeplacement().contains(res.getgauche())) {
						res = res.getgauche();
					}
				}
				return res;

		}else {
			return contourne(a);
		}			
	}
		return null;
	}
		private Hexagone plusEnHaut(Unite a) {//retourne l'hexagone le plus en haut pour se deplacer
			if(getcamp() == 1) {
				if(!a.estmort() && a.getplace().gethautdroit()!=null) {
					if(a.getplace().gethautdroit().getunite()==null) {
						Hexagone res =  a.getplace().gethautdroit();
						if(res!=null) {
							while(res.gethautdroit()!=null && res.gethautdroit().getunite()==null && a.getdeplacement().contains(res.gethautdroit())) { //et que pas de terrain ?
								res = res.gethautdroit();
							}
						}
						return res;
				}
			}
			}else {		
				if(!a.estmort() && a.getplace().gethautgauche()!=null) {
					if(a.getplace().gethautgauche().getunite()==null) {
						Hexagone res =  a.getplace().gethautgauche();
						if(res!=null) {
							while(res.gethautgauche()!=null && res.gethautgauche().getunite()==null && a.getdeplacement().contains(res.gethautgauche())) { //et que pas de terrain ?
								res = res.gethautgauche();
							}
						}
						return res;
					}				
			}	
		}	
			return null;
	}
		
		private Hexagone plusEnBas(Unite a) {
			if(getcamp()==1) {
				if(!a.estmort() && a.getplace().getbasdroit()!=null) {
					if(a.getplace().getbasdroit().getunite()==null) {
						Hexagone res =  a.getplace().getbasdroit();
						if(res!=null) {
							while(res.getbasdroit()!=null && res.getbasdroit().getunite()==null && a.getdeplacement().contains(res.getbasdroit())) { //et que pas de terrain ?
								res = res.getbasdroit();
							}
						}
						return res;
				}
			}
			}else {		
				if(!a.estmort() && a.getplace().getbasgauche()!=null) {
					if(a.getplace().getbasgauche().getunite()==null) {
						Hexagone res =  a.getplace().getbasgauche();
						if(res!=null) {
							while(res.getbasgauche()!=null && res.getbasgauche().getunite()==null && a.getdeplacement().contains(res.getbasgauche())) { //et que pas de terrain ?
								res = res.getbasgauche();
							}
						}
						return res;
				}			
			}	
		}	
			return null;
	}
	
	private Hexagone randomDep(Unite a) {//if apres milieu plateau
		Hexagone choix = null;
		if(a.getdeplacement().size()>0) {
			int alea = new Random().nextInt(a.getdeplacement().size());
			while(p.at(a.getdeplacement().get(alea).getx(), a.getdeplacement().get(alea).gety()).getunite()!=null) {
				alea = new Random().nextInt(a.getdeplacement().size());
			}
			choix = a.getdeplacement().get(alea);
		}
		
		return choix;
	}
	
	private Hexagone contourne(Unite a) {
		Hexagone destination = null;
		if(a.getplace().getx()<p.getheight()/2) { //l'unité est en haut du plateau donc choisir plutot un des hex du bas
			destination = plusEnBas(a);
		}else {
			destination = plusEnHaut(a);
		}
		return destination;
	}

	public LinkedList<Fx_Unite> fx() {
		return this.fx;
	}

	public void set_adversaire(Joueur a) {
		this.adversaire = a;
	}
	public void updateAttaque(Unite attaquante,int x, int y) {

        if(this.getPA()>=2 && attaquante!=null && p.at(x,y)!=null && attaquante.getplace()!=null && attaquante==p.at(attaquante.getplace().getx(),attaquante.getplace().gety()).getunite()) {
            if( attaquante.getcamp()==getcamp()) {
                if(attaquante.attaquer(p.at(x,y)))sub_PA(2);//Enleve les PA au joueur et met a jour le GUI PA
                for(Unite u: getUnits()) {
                    if(!u.estmort()) {
                        u.actualisation();
                    	u.actualise();

                    }
                }
            }
        }
    }
	
	public void updateSort(Unite attaquante,int type,int x,int y) {
        if(attaquante!=null && p.at(x,y)!=null) {
            if(!attaquante.estmort() && attaquante==p.at(attaquante.getplace().getx(),attaquante.getplace().gety()).getunite() && attaquante.getcamp()==getcamp()) {
            	int m=attaquante.getSkillList().get(type).cost();//Cout de la competence en PA
                boolean b=attaquante.getSkillList().get(type).available();//Temps de recuperation de la competence
                if(getPA()-m>=0 && b)
                    if(attaquante.caster(type,p.at(x,y)))sub_PA(m);
                for(Unite u: getUnits()) {
                    if(!u.estmort()) {
                        u.actualisation();
                    	u.actualise();

                    }
                }

            }

        }
    }
	
}

