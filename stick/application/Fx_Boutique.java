package application;

import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.EventHandler;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;

import javafx.scene.input.ClipboardContent;

import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class Fx_Boutique extends Pane implements Observable{

//Fx_Unites en vente, achetes , possedes
private LinkedList<Fx_Unite> unitesEnVente = new LinkedList<>();
protected static LinkedList<Fx_Unite> unitesAchetees = new LinkedList<>();
protected static LinkedList<Fx_Unite> unitesPose = new LinkedList<>();
//ObservableList
private ObservableList<Fx_Unite> En_vente;
protected static ObservableList<Fx_Unite> Dans_equipe;
//Listes
protected static ListView<Fx_Unite> inventaire = new ListView<>();
private ListView<Fx_Unite> view = new ListView<>();

//Observer
private LinkedList<Observer>observers = new LinkedList<>();

//
protected static Fx_Unite selected;
//Gui
private Text text;
private Text error;
//Donnes
private int money;
private int prixbase;

public Fx_Boutique(double Width,double Height,int i) {
	this.setWidth(Width);
	this.setHeight(Height-10);
	money=i;
	prixbase=i;
	
	/////
	selected=null;
	if(unitesAchetees!=null)unitesAchetees.clear();
	if(unitesPose!=null)unitesPose.clear();
	if(Dans_equipe!=null)Dans_equipe.clear();
	//
	
	unitesEnVente.add(new Fx_Unite("Guerrier",150));
	unitesEnVente.add(new Fx_Unite("Archer",150));
	unitesEnVente.add(new Fx_Unite("Chasseur",150));
	unitesEnVente.add(new Fx_Unite("Malade",175));
	unitesEnVente.add(new Fx_Unite("Fee",200));
	unitesEnVente.add(new Fx_Unite("Faucheuse",200));
	unitesEnVente.add(new Fx_Unite("BomberMan",125));
	unitesEnVente.add(new Fx_Unite("Batisseur",150));
	unitesEnVente.add(new Fx_Unite("Druide",195));
	unitesEnVente.add(new Fx_Unite("Pretresse",180));
	unitesEnVente.add(new Fx_Unite("ChronoMage",175));
	int[] a= {1,0,0,0,0,0};
	unitesEnVente.add(new Fx_Unite("Blob",175,a));
	int[] b= {0,1,0,0,0,0};
	unitesEnVente.add(new Fx_Unite("Conteur",180,b));
	for(Fx_Unite tmp: unitesEnVente)tmp.setcamp(1);
	unitesEnVente.add(new Fx_Unite("Assassin",150));
	En_vente=FXCollections.observableArrayList(unitesEnVente);
	Dans_equipe=FXCollections.observableArrayList(unitesAchetees);
	
	//Affichage graphique de l'Argent actuelle
	text=new Text("Argent :"+money);
	text.setFont(new Font(22));
	
	Boutons pret=new Boutons("Pret",200,50,30,Color.CORNSILK);
	Boutons reset=new Boutons("Reset",75,37.5,20,Color.CORNSILK);
	
	//Text
	Text pasmoney=new Text("Fond requis insuffisant");
	pasmoney.setFont(new Font(22));
    pasmoney.setFill(Color.RED);
    pasmoney.setVisible(false);
    
    error=new Text("Mauvais positionnement");
    error.setFont(new Font(30));
    error.setFill(Color.RED);
    error.setVisible(false);
    //
    
	view.setOnMouseClicked(new EventHandler<MouseEvent>() {        
		@Override
		public void handle(MouseEvent event) {       	
			if(event.getButton().equals(MouseButton.PRIMARY)){
				if(view.getSelectionModel().getSelectedItem()!=null) {//Si on selectionne quelque chose dans la liste
					if(event.getClickCount() == 2 && money-view.getSelectionModel().getSelectedItem().getPrix()>=0){
						Dans_equipe.add(view.getSelectionModel().getSelectedItem().clone());
						money-=view.getSelectionModel().getSelectedItem().getPrix();
						//Affichage graphique
						text.setText("Argent :"+money);
						pasmoney.setVisible(false);
					}
					else {
						if(money-view.getSelectionModel().getSelectedItem().getPrix()<0) {
							//Affichage graphique lorsque le joueur n'a pas assez d'argent
							pasmoney.setVisible(true);
						}
					}
				}
            }            
        }
	});
	
	inventaire.setOnMouseClicked(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {       	
        	if(event.getButton().equals(MouseButton.SECONDARY)){
                if(event.getClickCount() == 1 && inventaire.getSelectionModel().getSelectedItem()!=null){
                	money+=inventaire.getSelectionModel().getSelectedItem().getPrix();
                	Dans_equipe.remove(inventaire.getSelectionModel().getSelectedItem());
                	//Affichage graphique
                	text.setText("Argent :"+money);
                	pasmoney.setVisible(false);
                }
            }            
        }
	});
	
	inventaire.setOnDragDetected(new EventHandler<MouseEvent>() {//Pour Drag une unite (la prendre avec la souris)
		@Override
		public void handle(MouseEvent event) {//Voir doc JavaFx sur les drag and drop event
			Dragboard db = inventaire.startDragAndDrop(TransferMode.ANY);        
	        ClipboardContent content = new ClipboardContent();
	        if(inventaire.getSelectionModel().getSelectedItem()!=null) {
	        	content.putString(inventaire.getSelectionModel().getSelectedItem().getnom());
		        db.setContent(content);
		        selected =inventaire.getSelectionModel().getSelectedItem();	  
	        }
	        event.consume();				
		}			
	});	
	
	//Mise en page et ajouts a la fenetre :
	HBox layoutBas = new HBox(5);//Contient l'ensemble des Node
	VBox caseBas1 = new VBox();//Contientiendra la liste boutique
	VBox caseBas2 = new VBox();//Contientiendra la liste inventaire
	VBox caseBas3 = new VBox();//Contiendra l'argent actuel et le bouton reset
	VBox caseBas4 = new VBox(2);//Contiendra l'affichage des erreurs et le bouton pret
	
	
	caseBas1.getChildren().addAll(new Label("Boutique"),boutique());
	caseBas2.getChildren().addAll(new Label("Inventaire"),inventaire());
	caseBas3.getChildren().addAll(text,pasmoney);
	caseBas3.getChildren().addAll(reset);
	caseBas4.getChildren().addAll(pret,error);
	
	double h_h=new Text("BI").getBoundsInLocal().getHeight();
	caseBas1.setPrefHeight(getHeight()-h_h);
	caseBas2.setPrefHeight(getHeight()-h_h);
	caseBas3.setPrefHeight(getHeight()-h_h);
	caseBas4.setPrefHeight(getHeight()-h_h);
	
	layoutBas.getChildren().addAll(caseBas1,caseBas2,caseBas3,caseBas4);
	getChildren().add(layoutBas);
	
	pret.setOnMouseClicked(new EventHandler<MouseEvent>() {//Bouton pret
	    @Override
	    public void handle(MouseEvent event) {
	        Notify(); 
	    }
	});
	//Bouton Reset
	reset.setOnMouseClicked(new EventHandler<MouseEvent>() {	 
	    @Override
	    public void handle(MouseEvent event) {
	        reset(prixbase);
	    }
	});
}


public class Boutons extends Pane{
	Rectangle r=null;
	Boutons(String t,double width,double height,double Font,Color color){
		setWidth(width);
		setHeight(height);
		Text text=new Text(t);
		text.setFont(new Font(Font));

		Rectangle rec_pret = new Rectangle(width,height);
		rec_pret.setStroke(color.darker().darker());
		rec_pret.setStrokeWidth(1);
		r=rec_pret;
		rec_pret.setFill(color);
		r.setOnMouseEntered(e->{
			r.setFill(color.darker());
		});
		r.setOnMouseExited(e->{
			r.setFill(color.brighter());
		});
		text.setLayoutX(rec_pret.getWidth()/4.5);
		text.setLayoutY(rec_pret.getHeight()/1.5);
		StackPane stack = new StackPane(rec_pret,text);
		getChildren().addAll(stack);

	}
	public Rectangle rec_pret() {
		return r;
	}
}

public ScrollPane boutique() {
	ScrollPane scroll=new ScrollPane();
	scroll.setVbarPolicy(ScrollBarPolicy.ALWAYS);
	scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
	scroll.setPrefSize(getWidth()/4, getHeight());	
	
	view.setItems(En_vente);
	scroll.setContent(view);
	return scroll;
}

public ScrollPane inventaire() {
	ScrollPane scroll=new ScrollPane();
	scroll.setVbarPolicy(ScrollBarPolicy.ALWAYS);
	scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
	scroll.setPrefSize(getWidth()/4, getHeight());	
	
	inventaire.setItems(Dans_equipe);
	scroll.setContent(inventaire);
	return scroll;
}
//Observable methodes
@Override
public void addObserver(Observer o) {
	observers.add(o);
}

@Override
public void removeObserver(Observer o) {
	observers.remove(o);
}

@Override
public void Notify() {
	
	for(Observer o : observers) {
		o.updateBoutique(unitesPose);
	}
}
//

public void reset(int i) {//Reinitialise le plateau du joueur, ses unites possedes et son argent
	money=i;
	text.setText("Argent :"+money);
	//unitesAchetees.removeAll(unitesAchetees);
	Dans_equipe.removeAll(Dans_equipe);
	
	for(Fx_Unite e: unitesPose) {
		((Fx_Hexagon) e.getParent()).removeFx_Unite();
		e.setHexagone(null);
	}
	unitesPose.removeAll(unitesPose);
}

public void resetboutique(int i) {
	money=i;
	text.setText("Argent :"+money);
	//unitesAchetees.removeAll(unitesAchetees);
	
	Dans_equipe.removeAll(Dans_equipe);
	unitesPose.removeAll(unitesPose);
	for(Fx_Unite tmp: unitesEnVente)tmp.setcamp(2);//A CHANGER
}

public void error(int i) {
	reset(i);
	error.setVisible(true);
}
public int getmoney() {
	return this.money;
}

}