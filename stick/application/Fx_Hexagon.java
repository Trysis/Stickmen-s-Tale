package application;

import java.util.LinkedList;

import javafx.event.EventHandler;

import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Fx_Hexagon extends StackPane implements Observable,Observer_selected{ //Avant en Observer
protected static Observer_selected selected;//Case selectionne (a voir)
protected Polygon polygone;
protected Fx_Unite canvas;
	
protected Color Border_Color;//Couleur principale des contours de l'hexagone
protected Color couleurB;//Couleur secondaire de l'hexagone
protected static Color couleurC;//Couleur de fond de base //staituc
protected Color couleurD;//Couleur pour representer une case selectionne

protected double width;//Longueur de l'hexagone
protected double height;//Hauteur de l'hexagone
protected int x=-1,y=-1;//Coordonnes x et y dans le tableau
protected boolean posable;//Specifie si l'on peut poser une unite de la boutique sur cette case
protected boolean camp;//Specifie le camp d'une case posable==true
protected static boolean drag=false;

protected Fx_Terrain terrain = null;//par défaut 
//Ajout ici:
protected Vue principal;
protected LinkedList<Observer> observers = new LinkedList<Observer>();
//Pointeur
protected Fx_Hexagon gauche,droit,hautgauche,hautdroit,basgauche,basdroit;

public Fx_Hexagon(int width,int height,Color color){
	this(color);	
	this.width=width;
	this.height=height;
}
public Fx_Hexagon(Color Border_Color){
	polygone=new Polygon();
	canvas=null;

	this.Border_Color=Border_Color;
	couleurB=Color.YELLOWGREEN;
	couleurC=Color.TRANSPARENT;
	couleurD=Color.CORAL;
	
	getPolygone().setFill(couleurC);
	
	getPolygone().setOpacity(0.7);//Transparence varie entre 0 et 1 (Invisible --> Visible) 
	getPolygone().setStroke(Border_Color);//Defini la couleur des bordures
	getPolygone().strokeWidthProperty().set(1);//Defini la largeur des bordures
	
	getChildren().addAll(getPolygone());//Ajout du Polygone (Hexagone) dans this (StackPane)
	
}

//Getter
//Coordonnees sur plateau
public int getX() {//get coordonnee dans la grille en  de cases en x
	return x;
}
public int getY() {//get coordonnee dans la grille de cases en y
	return y;
}
/*getLayoutX() - getLayoutY() pour connaitre le positionnement a l'ecran
 *getWidth() - getHeight() deja defini
 */
//Couleurs 
public Color getColor_Bordure() {
	return Border_Color;
}
public Color getColor2() {
	return couleurB;
}
//Observable
public Vue getVue() {
	return principal;
}
//Posable
public boolean getCamp() {
	return camp;
}
public boolean getPosable() {	
        return posable;	
}
//FIN GETTER
//Setteur

//Fx_Hexagones
public void setgauche(Fx_Hexagon gauche) {//set le Fx_Hexagon a sa gauche
	this.gauche=gauche;
}
public void setdroit(Fx_Hexagon droit) {//set le Fx_Hexagon a sa droite
	this.droit=droit;
}
public void sethautdroit(Fx_Hexagon haut_droit) {//set le Fx_Hexagon en haut a droite
	this.hautdroit=haut_droit;
}
public void sethautgauche(Fx_Hexagon haut_gauche) {//set le Fx_Hexagon en haut a gauche
	this.hautgauche=haut_gauche;
}
public void setbasdroit(Fx_Hexagon bas_droit) {//set le Fx_Hexagon en bas a droite
	this.basdroit=bas_droit;
}
public void setbasgauche(Fx_Hexagon bas_gauche) {//set le Fx_Hexagon en bas gauche
	this.basgauche=bas_gauche;
}
public Fx_Hexagon getgauche() {//set le Fx_Hexagon a sa gauche
	return gauche;
}
public Fx_Hexagon gethautgauche() {//set le Fx_Hexagon a sa gauche
	return hautgauche;
}
public Fx_Hexagon getbasgauche() {//set le Fx_Hexagon a sa gauche
	return basgauche;
}
public Fx_Hexagon getdroit() {//set le Fx_Hexagon a sa gauche
	return droit;
}
public Fx_Hexagon gethautdroit() {//set le Fx_Hexagon a sa gauche
	return hautdroit;
}
public Fx_Hexagon getbasdroit() {//set le Fx_Hexagon a sa gauche
	return basdroit;
}
//Dimensions
public void setWidth(double width) {//Set la longueur
	super.setWidth(width);
	this.width=width;
}
public void setHeight(double height) {//Set la hauteur
	super.setHeight(height);
	this.height=height;
}
public void setHexagone() {//Remet a jour le positionnement du polygon, et sa longueur et hauteur
	polygone.getPoints().clear();
	polygone.getPoints().addAll(new Double[] {
			getLayoutX(), getLayoutY()+height/4,
			getLayoutX()+width/2, getLayoutY(),
			getLayoutX()+width-1, getLayoutY()+height/4,

			getLayoutX()+width-1, getLayoutY()+height-height/4,
			getLayoutX()+width/2, getLayoutY()+height,
			getLayoutX(), getLayoutY()+height-height/4,
	});
	if(terrain!=null) {
		terrain.setWidth(width);
		terrain.setHeight(height);
	}
}
//Couleur
public void setColor_Bordure(Color color) {//Set la couleur principale
	this.Border_Color=color;
}
public void setColor2(Color color) {//Set la couleur secondaire
	this.couleurB=color;
}
//Observable
public void setVue(Observable_selected vue) {
	this.principal=  (Vue) vue;
}
//Pour la pose d'une unite
public void setPosable(boolean b) {
	this.posable=b;
}
public void setCamp(boolean b) {
	this.camp=b;
}
//
public String toString() {
	return ("Position x="+this.getX()+" y="+this.getY());
	//return("Position x;y ="+getLayoutX()+";"+getLayoutY()+"\n Width="+width+" - Height="+height);
}
//FIN SETTER

//This.Observer
@Override
public void update(int GAME_STATE) {
	// TODO Auto-generated method stub
	switch(GAME_STATE) {
	case Observable_selected.EN_BOUTIQUE:		 						
		DroppedUnite(posable);			
		break;
	case Observable_selected.EN_JEU:
		DroppedUnite(false);
		Selected();
		break;
	}
}
public void Selected() { 
	Color init=(Color) getPolygone().getFill();
	double init2=getPolygone().getStrokeWidth();

	
	this.setOnMouseEntered(e->{
		if(principal.getUniteActuelle()!=null && principal.getUniteActuelle().getmovable() &&! principal.getUniteActuelle().getmovedone() && principal.getselection_type()==0 && principal.getUniteActuelle().getDeplacement().contains(this)) {
			principal.setdeplace(dij(this));
			for(Fx_Hexagon p: principal.getdeplac()) {
				p.getPolygone().setFill(Color.GREEN);
			}
		}
		if(terrain!=null)principal.setTerrainActuelle(terrain,this.getLayoutX(),this.getLayoutY());
	});
	
	this.setOnMouseExited(e->{
		if(principal.getdeplac()!=null) {
			for(Fx_Hexagon p: principal.getdeplac()) {
				p.getPolygone().setFill(couleurD);
			}
		}//Ici enelever description terrain
		if(terrain!=null)principal.setTerrainActuelle(null,this.getLayoutX(),this.getLayoutY());
	});

	this.setOnMouseClicked(e->{
	
		if(selected!=null) {//si on a deja clique sur qqchose avant 
			((Fx_Hexagon)selected).getPolygone().setFill(init);
			((Fx_Hexagon)selected).getPolygone().strokeWidthProperty().set(init2);		
		}
		getPolygone().setFill(couleurB);
		getPolygone().strokeWidthProperty().set(init2+3);
		selected=this;
		principal.setTerrainActuelle(null,this.getLayoutX(),this.getLayoutY());

		if(!hasFx_Unit() || (hasFx_Unit() && principal.getUniteActuelle()!=null && principal.getUniteActuelle().principal()==canvas.principal() && principal.getUniteActuelle()!=canvas)) {//Si le Fx_Hexagon ne possede pas de canvas
			if(principal.getUniteActuelle()!=null && principal.getUniteActuelle().getmovable() && principal.getselection_type()==0) {//On verifie si principal possede un canvas
					principal.getdeplac().addFirst(principal.getUniteActuelle().getHexagone());
					if(principal.isRvr()) {
						principal.deselection();
					}
					else {
					Notify();	
					}
					principal.setUniteActuelle(null);
			}
			if(principal.getuniteSelectionnee()!=null) {
				principal.setUniteEnnemieActuelle(null);
			}
		} 
		else {
			if(principal.getUniteActuelle()!=null) {//
				if(principal.getUniteActuelle().equals(getUnite()) && principal.getselection_type()<1) {//meme unite alors deselection	
					principal.deselection();
					principal.setselection_type(-1);
					principal.setUniteActuelle(null);							
				}else if(principal.getcamp()!=canvas.getcamp()) {//unite clique du camp adverse
					if(principal.getselection_type()<1) {
					//Sinon selectionne l'unite ennemie si pas deja selectionne (la deselectionne sinon)
						if(principal.getuniteSelectionnee()!=null && principal.getuniteSelectionnee().equals(getUnite()))principal.setUniteEnnemieActuelle(null);
						else principal.setUniteEnnemieActuelle(getUnite());
						}
					}
					else if(principal.getcamp()==canvas.getcamp() && principal.getselection_type()>=-1 && principal.getselection_type()<=0 ) {//Verifie si le Fx_Hexagon sur laquelle on clique est le meme que la Vue
						
						for (int i = 0; i < principal.getUniteActuelle().getDeplacement().size(); i++) {
							principal.getUniteActuelle().getDeplacement().get(i).getPolygone().setFill(couleurC);
						}
						principal.setUniteActuelle(getUnite());//set l'unite de principal si on clique sur une nouvelle
						principal.setGraph(getGraph(principal.getUniteActuelle().getHexagone()));
						principal.setdeplace(null);
						principal.setselection_type(0);
					
						if(getUnite().getmovable() && !getUnite().getmovedone()) {
							for (int i = 0; i < getUnite().getDeplacement().size(); i++) {
								getUnite().getDeplacement().get(i).getPolygone().setFill(couleurD);
							}						
						}
						else {
							principal.setselection_type(-1);
						}
					}
			}
			else if(principal.getcamp()==canvas.getcamp()) {//Verifie si le Fx_Hexagon sur laquelle on clique est le meme que la Vue	
				
				principal.setUniteActuelle(getUnite());//set l'unite de principal si on clique sur une nouvelle	
				principal.setGraph(getGraph(principal.getUniteActuelle().getHexagone()));
				principal.setselection_type(0);
				if(principal.getUniteActuelle().getmovable() && !principal.getUniteActuelle().getmovedone()) {
					for (int i = 0; i < principal.getUniteActuelle().getDeplacement().size(); i++) {
						principal.getUniteActuelle().getDeplacement().get(i).getPolygone().setFill(couleurD);
					}
				} else {
					for (int i = 0; i < getUnite().getDeplacement().size(); i++) {
						getUnite().getDeplacement().get(i).getPolygone().setFill(couleurC);
					}
					principal.setselection_type(-1);
				}
			} 
			else if(principal.getuniteSelectionnee()==null || !principal.getuniteSelectionnee().equals(getUnite())) {//Selection ennemie
				principal.setUniteEnnemieActuelle(getUnite());
			}
			else principal.setUniteEnnemieActuelle(null);
		}
		if(principal.getUniteActuelle()!=null && principal.getselection_type()>0 && principal.getselection_type()<5) {
			principal.deselection();
			principal.setUniteAttaquee(this);
			principal.setselection_type(-1);
			}
	}
	);
	
	this.setOnDragDetected(e->{	
		if(this.principal.getUniteActuelle()!=null && !principal.getUniteActuelle().getmovedone()) {
			Dragboard db = startDragAndDrop(TransferMode.ANY);
			ClipboardContent content = new ClipboardContent();	  
			content.putString(" ");
			db.setContent(content);
			if(principal.getUniteActuelle()!=null && principal.getUniteActuelle()==this.getUnite()) {		
				principal.setdeplace(new LinkedList<Fx_Hexagon>());
				ajoute();//Ajout du premier Fx_Hexagon dans la liste des deplacements de principal
				drag=true;//Specifie le mode "drag"
			}
		}
	});
	
	this.setOnDragOver(new EventHandler<DragEvent>() {
		
		public void handle(DragEvent event) {
			if (event.getGestureSource() != this && event.getDragboard().hasString()) {
				if(principal.getUniteActuelle().getDeplacement().contains(Fx_Hexagon.this))
					event.acceptTransferModes(TransferMode.ANY);
			}
			event.consume();
		
			if(drag && principal.getUniteActuelle()!=null && principal.getselection_type()==0 && principal.getdeplac()!=null && (contient() || first())) {	            		        
				LinkedList<Fx_Hexagon> op=principal.getdeplac();
				if(contient2()) {
					for(int i=op.size()-1;i>dans();i-- ) {	                    		                	
						op.get(i).getPolygone().setFill(couleurD);
						op.remove(i);
					}                   
				}
				else { 
					if(op.size()<=principal.getUniteActuelle().getdep() && contient() && isAutour(principal.getdeplac().getLast())) {
						getPolygone().setFill(Color.GREEN);
						ajoute();
					}
				}
			}
			
		}
	});
	this.setOnDragDone(new EventHandler<DragEvent>(){		
		public void handle(DragEvent event) {	
			if (event.getGestureSource() != this && event.getDragboard().hasString() ) {
			
				if(drag==true && principal.getdeplac()!=null && principal.getdeplac().size()>1) {					
					for(Fx_Hexagon p: principal.getdeplac()) {
						p.getPolygone().setFill(couleurC);
					}
					Notify();
					principal.setdeplace(null);
				}
			}
			drag=false;
			event.consume();
		}
	});
}
public boolean debut() {//Verifie si le Fx_Hexagon est le premier de la liste
	return principal.getdeplac().getFirst()==this;
}
public void ajoute() {//Ajoute le Fx_Hexagon a la liste des deplacements
	principal.getdeplac().add(this);
}
public boolean contient() {
	return principal.getUniteActuelle().getDeplacement().contains(this) && principal.getdeplac().getLast()!=this;
}
public boolean first() {
	return principal.getdeplac().get(0)==this;
}
public boolean contient2() {
	return principal.getdeplac().contains(this);
}
public int dans() {//
	return principal.getdeplac().lastIndexOf(this);
}
public boolean isAutour(Fx_Hexagon n) {//Renvoi true si n est autour du Fx_Hexagon actuel (this)
	return (gauche==n || hautgauche==n || droit==n || hautdroit==n || basgauche==n || basdroit==n);
}
public void DroppedUnite(boolean active) {
	getPolygone().setFill(couleurB);
	Color init=(Color) getPolygone().getFill();
	
	if(active) {
		setOnDragEntered(new EventHandler<DragEvent>() { //
			public void handle(DragEvent event) {
				
				if (event.getGestureSource() != this && event.getDragboard().hasString()) {
					getPolygone().setFill(couleurD);
				}		        
				event.consume();
			}
		});
		setOnDragExited(new EventHandler<DragEvent>() { //
			public void handle(DragEvent event) {	
				if (event.getGestureSource() != this && event.getDragboard().hasString()) {
					getPolygone().setFill(init);
				}		        
				event.consume();
			}
		});	
		setOnDragOver(new EventHandler<DragEvent>() { //
			public void handle(DragEvent event) {	
				if (event.getGestureSource() != this && event.getDragboard().hasString()) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				}		 
				event.consume();
			}
		});	
		this.setOnDragDropped(new EventHandler<DragEvent>(){
			public void handle(DragEvent event) {
				boolean success=false;			
				if (event.getGestureSource() != this && Fx_Boutique.selected!=null && (canvas==null || canvas.principal()==Fx_Boutique.selected) && obstruction(Fx_Boutique.selected) && event.getDragboard().hasString() ) {		        	
					success=true;
					if(Fx_Boutique.selected.getParent() instanceof Fx_Hexagon) {
						
						((Fx_Hexagon)Fx_Boutique.selected.getParent()).removeFx_Unite();
						Fx_Boutique.unitesPose.remove(Fx_Boutique.selected);
					}		        
									
					//canvas.setHexagone(Fx_Hexagon.this);
					setFx_Unite(Fx_Boutique.selected);
					
					setCanvas(canvas,true);
					Fx_Boutique.unitesPose.add(canvas);
					canvas.setHexagone(Fx_Hexagon.this);
					Fx_Boutique.selected.dessineImage(false);
					Fx_Boutique.Dans_equipe.remove(Fx_Boutique.selected);			        		        	      
				}
				Fx_Boutique.selected=null;
				event.setDropCompleted(success);
				event.consume();
			}
		});
		
		this.setOnMouseClicked((e)-> {
			if(e.getButton().equals(MouseButton.SECONDARY) && hasFx_Unit()) {//suprrime unite sur le plateau
				enleverUnitePlateau();
			}
			
		});
		
		
	}else {//Lorsque active==false reinitialise chaque set et la couleur initial
		getPolygone().setFill(couleurC);
		if(canvas!=null)setCanvas(canvas,false);
		drag_deactivate();
		mouse_deactivate();
	}
	
}
//Desactive "les" setOnMouse
public void mouse_deactivate() {
	setOnMouseClicked((e)->{});
}
public boolean obstruction(Fx_Unite n) {
	int[] placement=n.getplacement();
		if(Fx_Boutique.selected.getcamp()==2) {
		    if(placement[0]!=0)  {
				   if(gauche==null) {return false;}
			    	if(gauche.canvas!=null) {
				    	if(gauche.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
		    if(placement[1]!=0)  {
				   if(hautgauche==null) {return false;}
			    	if(hautgauche.canvas!=null) {
				    	if(hautgauche.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
		    if(placement[2]!=0)  {
				   if(basgauche==null) {return false;}
			    	if(basgauche.canvas!=null) {
				    	if(basgauche.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
		    if(placement[3]!=0)  {
				   if(droit==null) {return false;}
			    	if(droit.canvas!=null) {
				    	if(droit.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
		    if(placement[4]!=0)  {
				   if(basdroit==null) {return false;}
			    	if(basdroit.canvas!=null) {
				    	if(basdroit.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
		    if(placement[5]!=0) {
				   if(hautdroit==null) {return false;}
			    	if(hautdroit.canvas!=null) {
				    	if(hautdroit.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
	    }
	    else {
	    	 if(placement[3]!=0)  {
				   if(gauche==null) {return false;}
			    	if(gauche.canvas!=null) {
				    	if(gauche.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
			if(placement[4]!=0)  {
				   if(hautgauche==null) {return false;}
			    	if(hautgauche.canvas!=null) {
				    	if(hautgauche.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
			if(placement[5]!=0)  {
				   if(basgauche==null) {return false;}
			    	if(basgauche.canvas!=null) {
				    	if(basgauche.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
			if(placement[0]!=0 )  {
				   if(droit==null) {return false;}
			    	if(droit.canvas!=null) {
				    	if(droit.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
			if(placement[2]!=0)  {
				   if(basdroit==null) {return false;}
			    	if(basdroit.canvas!=null) {
				    	if(basdroit.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
			if(placement[1]!=0) {
			   if(hautdroit==null) {return false;}
			    	if(hautdroit.canvas!=null) {
				    	if(hautdroit.canvas.principal()!=n) {
				    		return false;
				    	}
				    }
			    }
	    }
	 return true;
}
//Desactive "les" setOnDrag
public void drag_deactivate() {//Desactive ces
	setOnDragEntered(e->{});
	setOnDragExited(e->{});
	setOnDragOver(e->{});
	setOnDragDone(e->{});
	setOnDragDropped(e->{});
}
//This.Observable
@Override
public void addObserver(Observer o) {
	observers.add(o);
}
@Override
public void removeObserver(Observer o) {
	observers.remove(o);
}

@Override
public void Notify() {	//appele quand un hexagone avec une unite est clique => l'unite stockee dans la vue	
	//ajout ici :
	for(Observer o: observers) {		
		if(!principal.isRvr())o.updateDeplacement(this.principal.getUniteActuelle(),principal.getdeplac()); 
	}
}

public boolean hasFx_Unit() {
	return this.canvas!=null;
}
public void setcanvas(Fx_Unite u) {
	canvas=u;
}
public void setFx_Unite(Fx_Unite u) {
	if(u==null) {
		return;
	}
	removeFx_Unite();
	
	this.canvas=u;
	getChildren().add(canvas);
}
public void removeFx_Unite() {
	getChildren().remove(canvas);
	canvas=null;
}
public void removeFxTerrain() {
	getChildren().removeAll(terrain);
	this.terrain=null;
}
public Fx_Unite getUnite() {
	if(canvas==null) {
		return null;
	}
	return canvas.principal();
}
public Fx_Unite getCanvas() {
	return this.canvas;
}
//Placement des unites

public void enleverUnitePlateau() {//ICIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
	canvas.dessineImage(true);
	Fx_Boutique.Dans_equipe.add(new Fx_Unite(canvas));
	Fx_Boutique.inventaire.setItems(Fx_Boutique.Dans_equipe);
	Fx_Boutique.unitesPose.remove(canvas);
	for(Fx_Unite n: canvas.getOthers()) {
		n.getHexagone().removeFx_Unite();
		n.setHexagone(null);
	}
	removeFx_Unite();
}
public void setCanvas(Fx_Unite x,boolean t) {//Fonction permettant de rendre un Fx_Unite draggable
	if(t) {
		x.setOnDragDetected(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) { 		  
				Dragboard db = x.startDragAndDrop(TransferMode.ANY);        
				ClipboardContent content = new ClipboardContent();
				content.putString(x.getnom());
				db.setContent(content);    		        
				
				Fx_Boutique.selected=x;
				
				event.consume();
			}
		});
	}else {
		x.setOnDragDetected(e->{});
	}
}

public void setGUIUnite(Fx_Unite u) {
	this.canvas	=u;
	getChildren().add(canvas);
}
public void setX(int i) {
	this.x=i;	
}
public void setY(int j) {
	this.y=j;	
}
public Polygon getPolygone() {
	return polygone;
}

public static Color getColorC() {
	return couleurC;
}

public void setFxTerrain(Fx_Terrain t) {
	this.terrain=t;
	afficheTerrain();
}



public Fx_Terrain getTerrain() {
	return terrain;
}
public void afficheTerrain() {
	
	if(this.terrain!=null) {
		terrain.setWidth(width);
		terrain.setHeight(height);
		terrain.setPoints();
		getChildren().addAll(terrain);
		terrain.toBack();
	}
}


public Node search(LinkedList<Node> p,Fx_Hexagon d) {
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
public LinkedList<Node> getGraph(Fx_Hexagon depart) {
	LinkedList<Node> unvisited=new LinkedList<Node>();
	LinkedList<Node> all=new LinkedList<Node>();
	LinkedList<Node> visited=new LinkedList<Node>();
	if(depart.getUnite()!=null) {
	for(Fx_Hexagon b: depart.getUnite().getDeplacement()) {
		all.add(new Node(b));		
	}
	Node dep=new Node(depart);
	unvisited.addFirst(dep);
	dep.distance=0;
	dep.droit=search(all,dep.content.droit);
	dep.hautdroit=search(all,dep.content.hautdroit);
	dep.basdroit=search(all,dep.content.basdroit);
	dep.gauche=search(all,dep.content.gauche);
	dep.hautgauche=search(all,dep.content.hautgauche);
	dep.basgauche=search(all,dep.content.basgauche);
	for(Node j: all) {
		j.droit=search(all,j.content.droit);
		j.hautdroit=search(all,j.content.hautdroit);
		j.basdroit=search(all,j.content.basdroit);
		j.gauche=search(all,j.content.gauche);
		j.hautgauche=search(all,j.content.hautgauche);
		j.basgauche=search(all,j.content.basgauche);
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
	 }		
	return visited;	
}
public class Node{
		public Node shortest;
		int distance;
		public final Fx_Hexagon content;
		Node droit,hautdroit,basdroit,gauche,hautgauche,basgauche;
		
		Node(Fx_Hexagon p){
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

	public LinkedList<Fx_Hexagon> dij(Fx_Hexagon cible){
		LinkedList<Fx_Hexagon> res=new LinkedList<Fx_Hexagon>();
		Node m= search(principal.getGraph(),cible);
		Node source=search(principal.getGraph(),principal.getUniteActuelle().getHexagone());
		while(m!=source){
			res.addFirst(m.content);
			m=m.shortest;
		}
	
		return res;
	}
}