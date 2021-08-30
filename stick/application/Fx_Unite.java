package application;

import java.io.*;
import java.net.MalformedURLException;
import java.util.LinkedList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class Fx_Unite extends Canvas implements Cloneable{
	public static String PATH="src/Ressources/Unites";
	private static String UNITE_IMAGE="stick1.png";
	private Image image;
	private String nom;
	private int PA;

	private Fx_Hexagon caseHex=null;
	private int x,y;
	private int camp;
	private boolean estmort=false;
	private int attaque,defensearmor,defmagique,portee,prix,pv,pdvMax,dep;
	private boolean movable,attaquable,castable=true;
	private boolean inList=false;
	private LinkedList<Fx_Skill> skillList=new LinkedList<>();//Liste de skill (Fx_Skill)
	private LinkedList<Fx_Hexagon> range=new LinkedList<Fx_Hexagon>();
	private LinkedList<Fx_Hexagon> deplacement=new LinkedList<Fx_Hexagon>();
	private LinkedList<Fx_Statut> statuts = new LinkedList<>();
	private LinkedList<Fx_Unite> others=new LinkedList<>();
	private boolean movedone;
	private int[] placement= {0,0,0,0,0,0};//gauche|hautgauche|basgauche|droit|hautdroit|basdroit pour camp 1 et sinon //droit|hautdroit|basdroit|gauche|hautgauche|basgauche
	private String[] stats=new String[6];
	private int number=0;
	private Fx_Unite principal=null;
	private String des_attaque;
	private boolean attdone;
public Fx_Unite(String n,int price){
	super(100,50);
	this.nom = n;

	prix=price;
	dessineImage(true);
}
public Fx_Unite(String nom,Fx_Unite principal) {
	super(45,45);
	this.nom=nom;
	this.principal=principal;
}
public Fx_Unite(String n,int price,int[] placement){
	super(100,50);
	this.nom = n;
	this.placement=placement;
	prix=price;
	dessineImage(true);
}
//ajout ici :
public Fx_Unite(Fx_Hexagon hex) {
	super(45,45);
	caseHex = hex;
	caseHex.setGUIUnite(this);
	
	x=hex.getX();
	y=hex.getY();
	
	dessineImage(false);
}

public Fx_Unite(Fx_Hexagon hex,LinkedList<Fx_Hexagon> oth) {
	super(45,45);
	caseHex = hex;
	caseHex.setGUIUnite(this);
	
	x=hex.getX();
	y=hex.getY();
	dessineImage(false);
	for(Fx_Hexagon p:oth) {
		others.add(new Fx_Unite(p));
	}
}
public Fx_Unite(Fx_Unite u) {
	this.nom=u.nom;
	//Case
	this.caseHex=u.caseHex;
	this.x=u.x;
	this.y=u.y;
	this.placement=u.placement;
	//Stats
	this.pdvMax=u.pdvMax;
	this.pv=u.pv;
	this.attaque=u.attaque;
	this.defensearmor=u.defensearmor;
	this.defmagique=u.defmagique;
	this.portee=u.portee;
	this.prix=u.prix;
	this.PA=u.PA;
	this.dep=u.dep;
	this.camp=u.camp;
	this.skillList=new LinkedList<>(u.skillList);
	//Affichage graphique
	this.inList=u.inList;
	this.setWidth(u.getWidth());
	this.setHeight(u.getHeight());

	this.image=u.image;
	for(int i=0;i<placement.length;i++) {
		if(placement[i]!=0) {
			Fx_Unite a=new Fx_Unite(nom,this);
			a.camp=camp;
			a.number=placement[i];
			others.add(a);
		}
	}
	dessineImage(inList);
}

//DEBUT INNER CLASS FX_SKILL
public static class Fx_Skill {//
	private Image image;
	private String PATH="src/Ressources/Skill";
	private String SKILL_IMAGE;
	//
	private String name;//Nom de la competence
	private String description;//Description de la competence
	private int cost;//cout d'usage de la competence (nb de PA)
	private int cd;//delai de recuperation de la competence (nb de tour avant de pouvoir utiliser la competence)
	private int cdMax;//delai de recuperation maximal
	private boolean available;
	//
	private LinkedList<Fx_Hexagon> cibles=new LinkedList<Fx_Hexagon>();
	public Fx_Skill(String name,String description,int cost,int cd,int cdMax,boolean available){
		this.name=name;
		this.SKILL_IMAGE=name.replaceAll(" ", "_");
		this.description=description;
		this.cost=cost;
		this.cd=cd;
		this.cdMax=cdMax;
		this.available=available;
		try {	
			image=new Image(new File(PATH+"/"+SKILL_IMAGE+".png").toURI().toURL().toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	//Getter
	public String getskillName() {
		return name;
	}
	public String description() {
		return description;
	}
	public int getCost() {//cout
		return cost;
	}
	public int getCd() {//CD
		return cd;
	}
	public int getCdMax() {//CD max
		return cdMax;
	}
	public boolean available() {
		return available;
	}
	public Image getImage() {
		  return image;
	}
	public LinkedList<Fx_Hexagon> getCibles(){
		return cibles;
	}
	//Setteur
	public void setName(String name) {
		this.name=name;
	}
	public void setDescription(String description) {
		this.description=description;
	}
	public void setCost(int cost) {
		this.cost=cost;
	}
	public void setCd(int cd) {
		this.cd=cd;
	}
	public void setCdMax(int cdMax) {
		this.cdMax=cdMax;
	}
	public void setAvailable(boolean available) {
		this.available=available;
	}
	public void setCibles(LinkedList<Fx_Hexagon>skill_cible_list) {
		cibles=skill_cible_list;
	}
}
//FIN INNER CLASS FX_SKILL
public void setNom(String name) {
	nom=name;
	for(Fx_Unite n:others) {
		n.nom=name;
	}
}

public void setImage() {
	if(number==0) {
	UNITE_IMAGE=nom+".png";
	}
	else{
		
		UNITE_IMAGE=nom+number+".png";	
	}
	try {
		image=new Image(new File(PATH+"/"+UNITE_IMAGE).toURI().toURL().toString());
	} catch (Exception e) {
		e.printStackTrace();
	}
}

public void dessineImage(boolean inList) {//Dessine l'unite selon qu'il soit dans une liste ou non
	this.inList=inList;
	GraphicsContext g=this.getGraphicsContext2D();
	g.clearRect(0, 0, this.getWidth(), this.getHeight());
	if(estmort==false) {
		
		setImage();
		this.setWidth(50);
		this.setHeight(50);
		if(image!=null) {
		
			if(image.getWidth()>getWidth() && image.getHeight()>getHeight()) {
				g.drawImage(image, 0, 0,getWidth(),getHeight());
				
				}
				else if(image.getWidth()>getWidth()) {
					g.drawImage(image, 0, 0,getWidth()-10,image.getHeight());
				
				}
				else {
					g.drawImage(image, getWidth()/2-image.getWidth()+10, 0, image.getWidth()-30, getHeight()-10);
		
				}
			if(!inList) {
				for(Fx_Unite n:others) {
				
					n.dessineImage(inList);
				}
			}
		}
		else {
		g.setFill(Color.BLUEVIOLET);
		g.fillOval(0, 0, 50, 50);
		}
		if(inList) {
			this.setWidth(200);
			this.setHeight(50);
			GraphicsContext gc = this.getGraphicsContext2D();
			gc.fillText(this.nom, 50+2, getHeight()/2);
			Text t =new Text("Prix :"+this.prix);
			gc.fillText(t.getText(), this.getWidth()-t.getBoundsInLocal().getWidth()-2, this.getHeight()/2+t.getBoundsInLocal().getHeight()/4);
			gc.setFont(Font.getDefault());
		}else {

			if(camp==2)setScaleX(-1);
		}
	}
}

public void dessine_selected() {//A futurement changer quand il y aura l'utilisation des images
	GraphicsContext g=this.getGraphicsContext2D();
	g.clearRect(0, 0, this.getWidth(), this.getHeight());
	g.setFill(Color.BLUEVIOLET);
	g.fillOval(0, 0, getWidth(), getHeight());
}

//Getter
public String getnom() {
	return nom;
}
public int getPdvMax() {
	return pdvMax;
}
public int getPv() {
	return pv;
}
public int getAttaque() {
	return attaque;
}
public int getDefensea() {
	return defensearmor;
}
public int getDefensema() {
	return defmagique;
}
public int getPortee() {
	return portee;
}
public int getPrix() {
	return prix;
}
public int getX() {
	return x;
}	
public int getY() {
	return y;
}
public int getPA() {
	return PA;
}
public int getdep(){
	return dep;
}
public LinkedList<Fx_Unite> getOthers(){
	return this.others;
}
public LinkedList<Fx_Skill> getSkill(){
	return this.skillList;
}
public int getcamp() {
	return this.camp;
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
//
public Image getImage() {
	return image;
}
public Fx_Hexagon getHexagone(){
    return this.caseHex;
}
public LinkedList<Fx_Hexagon> getDeplacement() {
	return deplacement;
}
public LinkedList<Fx_Hexagon> getRange() {
	return range;
}
public LinkedList<Fx_Statut> get_Statuts() {
	return this.statuts;
}
public String get_des_attaque() {
	return des_attaque;
}
public void setStats(String[] stats) {
	this.stats=stats;
	for(Fx_Unite n:others) {
		n.stats=stats;
	}
}
public String[] getStats() {
	return stats;
}
//Setteur
public void setPdvMax(int pdvMax) {
	this.pdvMax = pdvMax;
	for(Fx_Unite n:others) {
		n.pdvMax=pdvMax;
	}
}
public void setpv(int pv){
	this.pv=pv;
	for(Fx_Unite n:others) {
		n.pv=pv;
	}
}
public void setattaque(int attaque){
	this.attaque=attaque;
	for(Fx_Unite n:others) {
		n.attaque=attaque;
	}
}
public void setarmor(int defense){
	this.defensearmor=defense;
	for(Fx_Unite n:others) {
		n.defensearmor=defense;
	}
}
public void setmagique(int defense){
	this.defmagique=defense;
	for(Fx_Unite n:others) {
		n.defmagique=defense;
	}
}

public void setportee(int portee){
	this.portee=portee;
	for(Fx_Unite n:others) {
		n.portee=portee;
	}
}
public void setprix(int prix){
	this.prix=prix;
	for(Fx_Unite n:others) {
		n.prix=prix;
	}
}
public void setcamp(int camp){
	this.camp=camp;
	for(Fx_Unite n:others) {
		n.camp=camp;
	}
}
public void setdep(int dep) {
	this.dep=dep;for
	(Fx_Unite n:others) {
		n.dep=dep;
	}
}
public void setHexagone(Fx_Hexagon e){//gauche|hautgauche|basgauche|droit|basdroit|hautdroit
    if(e!=null) {
    	this.caseHex=e;
	    int i=0;
	    if(camp==2) {
	    	if(placement[0]!=0) {
	    		if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
			    	others.get(i).caseHex.removeFx_Unite();
			    	}
		    	others.get(i).caseHex=e.getgauche();
		    	e.getgauche().setFx_Unite(others.get(i));
		    	i++;
		    }
		    if(placement[1]!=0) {
		    	if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
			    	others.get(i).caseHex.removeFx_Unite();
			    	}
		    	others.get(i).caseHex=e.gethautgauche();
		    	e.gethautgauche().setFx_Unite(others.get(i));
		    	i++;
		    }
		    if(placement[2]!=0) {
		    	if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
			    	others.get(i).caseHex.removeFx_Unite();
			    	}
		    	others.get(i).caseHex=e.getbasgauche();
		    	e.getbasgauche().setFx_Unite(others.get(i));
		    	i++;
		    }
		    if(placement[3]!=0) {
		    	if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
		    	others.get(i).caseHex.removeFx_Unite();
		    	}
		    	others.get(i).caseHex=e.getdroit();
		    	e.getdroit().setFx_Unite(others.get(i));
		    	i++;
		    }
		    if(placement[4]!=0) {
		    	if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
			    	others.get(i).caseHex.removeFx_Unite();
			    	}
		    	others.get(i).caseHex=e.getbasdroit();
		    	e.getbasdroit().setFx_Unite(others.get(i));
		    	i++;
		    }
		    if(placement[5]!=0) {
		    	if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
			    	others.get(i).caseHex.removeFx_Unite();
			    	}
		    	others.get(i).caseHex=e.gethautdroit();
		    	e.gethautdroit().setFx_Unite(others.get(i));
		    	i++;
		    }
	    }
	    else {
	    	if(placement[3]!=0) {
	    		if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
			    	others.get(i).caseHex.removeFx_Unite();
			    	}
		    	others.get(i).caseHex=e.getgauche();
		    	e.getgauche().setFx_Unite(others.get(i));
		    	i++;
		    }
		    if(placement[4]!=0) {
		    	if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
			    	others.get(i).caseHex.removeFx_Unite();
			    	}
		    	others.get(i).caseHex=e.gethautgauche();
		    	e.gethautgauche().setFx_Unite(others.get(i));
		    	i++;
		    }
		    if(placement[5]!=0) {
		    	if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
			    	others.get(i).caseHex.removeFx_Unite();
			    	}
		    	others.get(i).caseHex=e.getbasgauche();
		    	e.getbasgauche().setFx_Unite(others.get(i));
		    	i++;
		    }
		    if(placement[0]!=0) {
		    	if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
		    	
		    	others.get(i).caseHex.removeFx_Unite();
		    	}
		    	others.get(i).caseHex=e.getdroit();
		    	e.getdroit().setFx_Unite(others.get(i));
		    	i++;
		    }
		    if(placement[2]!=0) {
		    	if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
			    	others.get(i).caseHex.removeFx_Unite();
			    	}
		    	others.get(i).caseHex=e.getbasdroit();
		    	e.getbasdroit().setFx_Unite(others.get(i));
		    	i++;
		    }
		    if(placement[1]!=0) {
		    	if(others.get(i).caseHex!=null && others.get(i).caseHex.getCanvas()!=this) {
			    	others.get(i).caseHex.removeFx_Unite();
			    	}
		    	others.get(i).caseHex=e.gethautdroit();
		    	e.gethautdroit().setFx_Unite(others.get(i));
		    	i++;
		    }
	    }
    }
    else {
    	this.caseHex=null;
    	for(Fx_Unite n:others) {
    		n.caseHex=null;
    	}
    }
}

public void setPos(double x,double y) {
	this.setLayoutX(x);
	this.setLayoutY(y);
}
public void setX(int x) {
	this.x=x;
}
public void setY(int y) {
	this.y=y;
}
//Lie a certains status
public void setattaquable(boolean attaquable){
	this.attaquable=attaquable;
	for (Fx_Unite n:others) {
		n.attaquable=attaquable;
	}
}
public void setmovable(boolean movable){
	this.movable=movable;
	for (Fx_Unite n:others) {
		n.movable=movable;
	}
}
public void setcastable(boolean castable){
	this.castable=castable;
	for (Fx_Unite n:others) {
		n.castable=castable;
	}
}
//Lie aux mouvements
public void setrange(LinkedList<Fx_Hexagon> range){
	this.range=range;
	for (Fx_Unite n:others) {
		n.range=range;
	}
}
public void setdeplacement(LinkedList<Fx_Hexagon> deplacement){
	  	
	this.deplacement=deplacement;
	for (Fx_Unite n:others) {
		n.deplacement=deplacement;
	}
}

//Changeurs d'attributs
public void setestmort(boolean mort) {
	this.estmort=mort;
	for (Fx_Unite n:others) {
		n.estmort=mort;
	}
}
public void set_des_attaque(String d) {
	des_attaque = d;
}
	
/*public String toString() {
	return nom;		
}*/
	
@Override
protected Fx_Unite clone() {
	return new Fx_Unite(this);
}

public boolean getestmort() {
	return this.estmort;
}


public void setmove(boolean movedone) {
	this.movedone=movedone;	
	for (Fx_Unite n:others) {
		n.movedone=movedone;
	}
}

public boolean getmovedone() {
	return movedone;
}
public void add_statut(Fx_Statut t) {
	this.statuts.add(t);
	for (Fx_Unite n:others) {
		n.statuts.add(t);
	}
}
public Fx_Unite principal() {
	if(number==0) {
		return this;
	}
	else {
		return principal;
	}
}
public int[] getplacement() {
	// TODO Auto-generated method stub
	return placement;
}
public void setAttdone(boolean attdone) {
	// TODO Auto-generated method stub
	this.attdone=attdone;
}
public boolean getattdone() {
	// TODO Auto-generated method stub
	return attdone;
}
	
}
