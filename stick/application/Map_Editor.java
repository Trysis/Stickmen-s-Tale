package application;

import java.io.File;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.Map_Editor.Editable.Modifiable_Hexagonal;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

public class Map_Editor extends Pane{
	private Menu_J selection_map;//Parent
	private Color fond_principal=Color.rgb(244, 244,244);//Couleur principal
	private Color bordures=Color.DARKGOLDENROD;//Couleur des bordures
	private Font font_principal=new Font(15);
	private int x, y;//Valeur x et y
	//
	private Confirmer confirmer;
	//
	private Rectangle fond;//Rectangle de fond (de couleur)
	private Head head;//Titre
	private Editable visual;//Contenu avec HexagonalGrid
	private Items items;//Objets utilisable sur l'HexagonalGrid
	private Request request;//Boutons de validation de carte et autres
	//
	private Retour retour;//Bouton retour
	//
	private String PATH="src/Ressources/Editeur";
Map_Editor(Menu_J parent, int x,int y){//
	this.selection_map=parent;
	setWidth(parent.getWidth());
	setHeight(parent.getHeight());
	this.x=x;
	this.y=y;

	init();
	getChildren().add(fond);
	getChildren().addAll(head,visual,items,request);
	getChildren().addAll(retour);
}
public void init() {//Initialisation des differentes classes principales avec leurs positions
	double divX=9;
	double divY=7;
	double h13=0.75;//hauteur h13 pour l'hexagonal grid et head
	double esp=0.1;//portion de l'espace entre l'HexagonalGrid et Items
	double w2=1.75,w1=divX-w2-esp*2;//longueur w1 pour l'hexagonalGrid , w2 pour items
	/////
	double espace=esp*getWidth()/divX;//espace cacule
	/////
	//Fond (pour la couleur)
	fond=new Rectangle(getWidth(),getHeight());
	fond.setFill(fond_principal);
	//Head , titre
	head=new Head(getWidth(),h13*getHeight()/divY);
	//Visual affichage de l'HexagonalGrid
	visual=new Editable(w1*getWidth()/divX,getHeight()-head.getHeight()-espace/2);
	visual.setLayoutX(espace/2);
	visual.setLayoutY(head.getHeight()+head.getLayoutY());//Position en Y
	//Items affichages des objets utilisables a utiliser sur l'HexagonalGrid
	items=new Items(w2*getWidth()/divX,getHeight()-head.getHeight()-espace-(w2*getWidth()/divX)/3);
	items.setLayoutX(visual.getLayoutX()+visual.getWidth()+espace/2);//Position en X
	items.setLayoutY(head.getHeight()+head.getLayoutY());//Position en Y
	//Request
	request=new Request(w2*getWidth()/divX,(w2*getWidth()/divX));
	request.setLayoutX(visual.getLayoutX()+visual.getWidth()+espace/2);//Position en X
	request.setLayoutY(items.getHeight()+items.getLayoutY()+5);//Position en Y
	//Retour
	retour=new Retour(50,50);
	retour.setLayoutX(getWidth()-retour.getWidth()-espace);
	retour.setLayoutY(5);
}
public void confirme(Modulable m) {//page de confirmation des choix
	if(confirmer!=null)getChildren().remove(confirmer);
	if(m==null)return;
	this.confirmer=new Confirmer(m,getWidth(),getHeight());
	getChildren().add(confirmer);
}
//Debut class interne Head
public class Head extends Pane {
	private double ecart_haut_bas=5;
	private double ecart_gauche_droite=5;
	Head(double width,double height){
		setWidth(width);
		setHeight(height);
		
		Canvas c=new Canvas(getWidth(),getHeight());
		GraphicsContext g=c.getGraphicsContext2D();
		//
		Font font=new Font(40);
		//
		Text t=new Text("Map Editor");
		t.setFont(font);
		
		g.setFont(font);
		g.setStroke(bordures);
		g.strokeText(t.getText(), width/2-t.getBoundsInLocal().getWidth()/2,t.getBoundsInLocal().getHeight());
		Polygon p=new Polygon();
		double h=getHeight()-(getHeight()-t.getBoundsInLocal().getHeight())/2;
		double w=width/20;
		p.setOpacity(0.5);
		p.setFill(bordures);
		p.setStroke(Color.BROWN);
		p.getPoints().addAll(new Double[] {
				ecart_gauche_droite,h,
				ecart_gauche_droite+w/2,h-ecart_haut_bas,
				ecart_gauche_droite+w,h-1,
				
				width-ecart_gauche_droite-w,h-1,
				width-ecart_gauche_droite-w/2,h-ecart_haut_bas,
				width-ecart_gauche_droite,h,
				
				width-ecart_gauche_droite-w/2,h+ecart_haut_bas,
				width-ecart_gauche_droite-w,h,
				
				ecart_gauche_droite+w,h,
				ecart_gauche_droite+w/2,h+ecart_haut_bas,
		});
		getChildren().addAll(c,p);
	}
}//Fin classe interne Head

//Classe interne Editable (de Selection_map)
public class Editable extends Pane {
	private Rectangle fond_2;//Rectangle de fond
	private Modifiable_Hexagonal editable_grid;//HexagonalGrid avec de legeres proprietes differentes
	private Canvas informations;//Canvas , information telle que les coordonnes du Fx_Hexagon sur lequel la souris pointe par exemple
	private Canvas bande;//Canvas
	private TextField nom_du_plateau;//Zone de texte pour donner un nom a une carte
	//
	//On fera des verifications pour savoir si le joueur a changer de nom de cartes
	private String nom_carte="Nom_du_plateau";//Nom actuel choisi par le joueur
	private double et=50;//Bordure entre le rectangle et l'HexagonalGrid pour le dessin de l'HexagonalGrid
	public Editable(double width,double height) {
		setWidth(width);//On assigne la longueur
		setHeight(height);//On assigne la hauteur
		init_2();
		getChildren().addAll(fond_2,informations,bande,editable_grid,nom_du_plateau);
	}
	public boolean setNomCarte(String n) {//Pour attribuer un nom a la carte
		//Pattern "aucun caracteres ni chiffre" on verifiera qu'il n'y a pas de caracteres speciaux
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(n);
		if(!(m.find()) && n.length()>0) {//Si on ne trouve pas de caracteres speciaux, et que la taille du mot est superieur a 0 on valide
			nom_carte=n;
			return true;
		}
		//Sinon on refuse le nom
		return false;
	}
	private void init_2() {//Initialise l'HexagonalGrid, et les autres panels 
		
		//Rectangle 
		fond_2=new Rectangle(getWidth(),getHeight());//Rectangle de fond sur lequel sera dessine l'HexagonalGrid

		fond_2.setFill(Color.WHITE);//Couleur que l'on selectionne
		fond_2.setStrokeWidth(3);//Taille en longueur de la bordure du Rectangle
		fond_2.setStroke(bordures);//Couleur de la bordure
		//HexagonalGrid
		//
		editable_grid=new Modifiable_Hexagonal(getWidth()-et*2,getHeight());//HexagonalGrid
		editable_grid.setLayoutX(et);//Set la position en X
		editable_grid.setLayoutY(et);//Set la position en Y
		//Canvas
		//- informations
		double portion=8;//Taille divise par la portion pour savoir quel part le canvas c2 prendra
		informations=new Canvas(getWidth()/portion,getHeight()-getHeight()/portion);//Canvas c2, bande noir
		informations.setLayoutX(0);//Set la position en X relative a son parent
		informations.setLayoutY(0);//Set la position en y
		
		GraphicsContext g=informations.getGraphicsContext2D();//GraphicsContext permettra de dessiner
		
		g.setFill(Color.BLACK);//Couleur selectionne
		g.setGlobalAlpha(0.2);//Opacite (Transparence 0 == transparent et 1 == visible a 100%)
		g.fillRect(0, 0, informations.getWidth(), informations.getHeight());//On "colorie" dans le canvas x,y,width,height
		//Canvas
		//- bande -> leger fond noir semi transparent
		bande=new Canvas(getWidth(),getHeight()/portion);//Canvas c3 correspondera au fond noir
		bande.setLayoutX(0);//Position en x
		bande.setLayoutY(((portion-1)*getHeight())/portion);//Position en y
		
		g=bande.getGraphicsContext2D();

		g.setFill(Color.BLACK);//Couleur selectionne
		g.setGlobalAlpha(0.4);//Opacite (Transparence 0 == transparent et 1 == visible a 100%)
		g.fillRect(0, 0, bande.getWidth(), bande.getHeight());//On "colorie" dans le canvas x,y,width,height
		//TextField
		//Texte a remplir par l'utilisateur
		nom_du_plateau=new TextField(nom_carte);
		//Position et taille
		nom_du_plateau.setLayoutX(0+fond_2.getStrokeWidth());//Position en x
		nom_du_plateau.setLayoutY(((portion-1)*getHeight())/portion+2);//Position en y
		nom_du_plateau.setPrefWidth(getWidth()-fond_2.getStrokeWidth()*2);//Longueur de preference
		nom_du_plateau.setPrefHeight(getHeight()/portion-4);//Hauteur de preference
		//Css
		nom_du_plateau.setAlignment(Pos.CENTER);
		nom_du_plateau.setFont(new Font(40));
		nom_du_plateau.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");//Background en transparent
		nom_du_plateau.setOnKeyPressed(e->{
			if(e.getCode() == KeyCode.ENTER)Map_Editor.this.requestFocus();//Lorsque l'on appuie sur Entree (on "sort" du TextField)
		});
		nom_du_plateau.setTextFormatter(new TextFormatter<>(change -> {//On n'accepte pas d'espace ni de caracteres speciaux
		    if (change.getText().equals(" ") || !change.getText().matches("[\\w-]+") || nom_du_plateau.getText().length()>30){
		        change.setText("");
		    }
		    return change;
		}));
		nom_du_plateau.textProperty().addListener(new ChangeListener<String>() {
		    @Override
		    public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
		    	nom_carte=newValue;
		    }
		});
	}
	//Classe interne Modifiable_Hexagonal
	public class Modifiable_Hexagonal extends HexagonalGrid {
		private LinkedList<Fx_Hexagon_2> to_erase=new LinkedList<>();
		private int posable_j1=0;
		private int posable_j2=0;
		public Modifiable_Hexagonal(double width, double height) {
			super(width, height);
			this.set_dimension(x, y);
			this.setGrid();
			setEvent();//Attributions des evenements des Fx_Hexagon_2 du plateau
		}
		public void decrementer_j1() {
			posable_j1--;
			if(!(posable_j1>0))request.valider.setDisable2(true);//Active le bouton valider lorsqu'il y a au moins 1 cases pour chaque camp des joueurs
		}
		public void decrementer_j2() {
			posable_j2--;
			if(!(posable_j2>0))request.valider.setDisable2(true);//Active le bouton valider lorsqu'il y a au moins 1 cases pour chaque camp des joueurs
		}
		public void incrementer_j1() {
			posable_j1++;
			if(posable_j1>0 && posable_j2>0)request.valider.setDisable2(false);//Active le bouton valider lorsqu'il y a au moins 1 cases pour chaque camp des joueurs
		}
		public void incrementer_j2() {
			posable_j2++;
			if(posable_j1>0 && posable_j2>0)request.valider.setDisable2(false);//Active le bouton valider lorsqu'il y a au moins 1 cases pour chaque camp des joueurs
		}
		public void setEvent() {//attribue les differents events sur les Fx_Hexagon_2
			for(int i=0;i<this.getGrille().length;i++) {
				for(int j=0;j<this.getGrille()[i].length;j++) {
					final int x=i;
					final int y=j;
					final Fx_Hexagon_2 tmp=(Fx_Hexagon_2) getHexagon(i,j);
					//Event
					tmp.setOnMouseClicked(new EventHandler<MouseEvent>() { //On clique
						@Override 
						public void handle(MouseEvent e) {
							if(e.getButton().equals(MouseButton.PRIMARY)) {//Bouton gauche de la souris
								if(items.selected!=null) {//On regarde si l'utilisateur a selectionne un objet a pose
									items.selected.use((Fx_Hexagon_2)getHexagon(x, y));//Si c'est le cas on l'utilise sur le Fx_Hexagon sur lequel on a clique
								}
							} else if(e.getButton().equals(MouseButton.SECONDARY)) {//Clique droit
								//On vide le Fx_Hexagon
								tmp.setSupprimer(false);//Permet de reinitialiser une case
							}
						}
					});
					tmp.setOnMouseEntered(e->{//
						if(items.selected!=null) {
							items.selected.Show_transparency(this, tmp);//Affichage de la transparence des objets
						}
					});
					tmp.setOnMouseExited(e->{
							if(items.selected!=null) {
								getChildren().removeAll(items.selected.used());//On enleve l'objet transparent lorsque l'on sort de la case
							}
					});
				}
			}
		}
		@Override
		protected void setGrid_FxHexagon() {//Redefinition, la grille possede maintenant des Fx_Hexagon_2
			if(grille!=null) {
				for(int i=0;i<grille.length;i++) {
					for(int j=0;j<grille[i].length;j++) {
						getChildren().remove(grille[i][j]);
					}
				}
			}
			grille=new Fx_Hexagon_2[larg][haut];//Initialisation grille d'hexagones (graphique)
			for(int i=0;i<larg;i++) {
				for(int j=0;j<haut;j++) {
					grille[i][j]=new Fx_Hexagon_2(ref.getColor_Bordure());
					this.getChildren().add(grille[i][j]); //Sans texte
				}
			}
		}
	}//Fin Modifiable_Hexagonal
}//Fin classe interne Editable
//Debut Fx_Hexagon_2
public class Fx_Hexagon_2 extends Fx_Hexagon {//Sera dans le plateau
	private boolean supprimer=false;//Dit si cette case est a supprimer ou non
	public Fx_Hexagon_2(Color Border_Color) {
		super(Border_Color);
	}
	public void setSupprimer(boolean b) {//
		supprimer=b;
		this.reset();
		if(b) {//Lorsque c'est a supprimer
			this.getPolygone().setOpacity(0.05);
			if(!visual.editable_grid.to_erase.contains(this))visual.editable_grid.to_erase.add(this);
		}
		else if(visual.editable_grid.to_erase.contains(this)) {//Sinon
			visual.editable_grid.to_erase.remove(this);
		}
	}
	public boolean getSupprimer() {
		return supprimer;
	}
	public void reset_camp() {
		if(this.posable) {
			if(this.camp)visual.editable_grid.decrementer_j1();
			else visual.editable_grid.decrementer_j2();
		}
		this.setPosable(false);
		this.setCamp(false);
		this.getPolygone().setFill(couleurC);
	}
	public void reset() {//Enleve tout objet dansle Fx_Hexagon, lui donne sa couleur originelle et son opacite
		if(this.posable) {
			if(this.camp)visual.editable_grid.decrementer_j1();
			else visual.editable_grid.decrementer_j2();
		}
		this.removeFx_Unite();
		this.removeFxTerrain();
		this.setPosable(false);
		this.setCamp(false);
		this.getPolygone().setFill(couleurC);
		this.getPolygone().setOpacity(0.7);
	}
}//Fin Fx_Hexagon_2

//Debut INTERFACE Selectable pour les objets utilisable sur le plateau
public interface Selectable {//Interface pour les objets selectionne et permettre une interaction avec L'hexagonalGrid affiche
	public LinkedList<Node> trash = new LinkedList<>();//Liste des objets "jetables"
	public void use(Fx_Hexagon_2 f);//"use" pour definir l'action qui s'execute lorsque l'on clique sur l'HexagonalGrid avec un objet selectionne
	public void Show_transparency(Modifiable_Hexagonal grid,  Fx_Hexagon_2 f);//Affichage de la transparence sur l'HexagonalGrid de l'objet selectionne
	public default LinkedList<Node> used(){//Pour enlever l'objet "transparent" affiche sur le plateau apres que celui est ete mis dans used puis le jette
		LinkedList<Node> tmp = new LinkedList<>(trash);
		trash.clear();
		return tmp;
	}
}//FIN INTERFACE Selectable

//Debut classe interne Items
public class Items extends TabPane {//Possedera la liste des objets a poser sur le plateau
	private Selectable selected=null;//Objet selectionne

	//Observable liste - prendra les LinkedList
	private ObservableList<Selectable> observable_utilitaires;//ex: Pose des cases posables du joueur 1 ou 2
	private ObservableList<Selectable> observable_terrains;//Fx_Terrains
	private ObservableList<Selectable> observable_entites;//Fx_Unite ou autres
	//ListView
	private ListView<Selectable> view_utilitaires = new ListView<>();
	private ListView<Selectable> view_terrains = new ListView<>();
	private ListView<Selectable> view_entites = new ListView<>();
	/////
	private ScrollPane scroll_utilitaires;
	private ScrollPane scroll_terrains;
	private ScrollPane scroll_entites;
	//Listes "brut"
	private LinkedList<Selectable> utilitaires=new LinkedList<>();
	private LinkedList<Selectable> terrains=new LinkedList<>();
	private LinkedList<Selectable> entites=new LinkedList<>();

	Items (double width, double height){
		this.setWidth(width);
		this.setHeight(height);
		//Scroll
		scroll_terrains=new ScrollPane();
		scroll_utilitaires=new ScrollPane();
		scroll_entites=new ScrollPane();
		//Instanciations des listes
		Liste_terrains();
		Liste_utilitaires();
		List_entites();
		//Instanciations des differents Panel Tab
		Tab utilitaires = new Tab();//Cases J1 et J2, supprimer une case etc
		Tab terrains = new Tab();//Fx_Terrain a pose
		Tab entites = new Tab();//Unites posable sur le terrain ou autre ?
		//Attributions des valeurs aux differents Tab
		//Utilitaires
		utilitaires.setText("Utilitaires");
		utilitaires.setContent(scroll_utilitaires);//Scroll des terrains
		//Terrains
		terrains.setText("Terrains");
		terrains.setContent(scroll_terrains);//Scroll des utilitaires
		//Entites
		entites.setText("Entites");
		entites.setContent(scroll_entites);//Scroll des utilitaires
		
		//Css Style
		this.setStyle("-fx-border-color:DARKGOLDENROD;");
		//utilitaires.setStyle("-fx-border-color:DARKGOLDENROD;");
		//terrains.setStyle("-fx-border-color:DARKGOLDENROD;");
		//entites.setStyle("-fx-border-color:DARKGOLDENROD;");
		//Ajout dans le Parent
		this.getTabs().addAll(utilitaires,terrains,entites);
		this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		this.setPrefSize(width, height);
	}
	private void Liste_utilitaires() {//Instanciation de la liste des objets "utilitaires"
		//Ajout des utilitaires existants
		utilitaires.add(new Fx_Hexagon_Pane(1,getWidth(),50));//1 pour camp "Joueur 1"
		utilitaires.add(new Fx_Hexagon_Pane(2,getWidth(),50));//2 pour camp "Joueur 2"
		//utilitaires.add(new EnleverCase(getWidth(),50));//Pour la suppression d'une case
		//ScrollBar
		scroll_utilitaires.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scroll_utilitaires.setHbarPolicy(ScrollBarPolicy.NEVER);
		//scroll_utilitaires.setPrefSize(getWidth()/4, getHeight());
		//ListView
		view_utilitaires.setPrefHeight(getHeight());
		//Set des contenus
		observable_utilitaires=FXCollections.observableArrayList(utilitaires);
		view_utilitaires.setItems(observable_utilitaires);
		scroll_utilitaires.setContent(view_utilitaires);
		//Action lors d'un clique pour permettre une selection correct
		view_utilitaires.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					if(view_utilitaires.getSelectionModel().getSelectedItem()!=null) {
						if(view_utilitaires.getSelectionModel().getSelectedItem()==selected) {
							view_utilitaires.getSelectionModel().clearSelection();
							selected=null;
						}
						selected=view_utilitaires.getSelectionModel().getSelectedItem();
					}
				}
			}
		});
	}
	private void Liste_terrains() {//Instanciation de la liste des Fx_Terrains
		//Ajout des terrains existants
		terrains.add(new Fx_Terrain_2("Poison",getWidth(),50));
		terrains.add(new Fx_Terrain_2("Campement",getWidth(),50));
		terrains.add(new Fx_Terrain_2("Buisson",getWidth(),50));
		terrains.add(new Fx_Terrain_2("Goudron",getWidth(),50));
		terrains.add(new Fx_Terrain_2("Trap",getWidth(),50));
		//ScrollBar
		scroll_terrains.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scroll_terrains.setHbarPolicy(ScrollBarPolicy.NEVER);
		//scroll_terrains.setPrefSize(getWidth()/4, getHeight());
		//ListView
		view_terrains.setPrefHeight(getHeight());
		//Set des contenus
		observable_terrains=FXCollections.observableArrayList(terrains);

		view_terrains.setItems(observable_terrains);
		scroll_terrains.setContent(view_terrains);
		//Action lors d'un clique pour permettre une selection correct
		view_terrains.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					if(view_terrains.getSelectionModel().getSelectedItem()!=null) {
						if(view_terrains.getSelectionModel().getSelectedItem()==selected) {
							view_terrains.getSelectionModel().clearSelection();
							selected=null;
						}
						selected=view_terrains.getSelectionModel().getSelectedItem();
					}
					
				}
			}
		});
	}
	private void List_entites() {//Instanciation de la liste des entites
		//Ajout des entites existantes
		entites.add(new Fx_Unite_Pane("Arbre",getWidth(),50));
		entites.add(new Fx_Unite_Pane("Roche",getWidth(),50));
		entites.add(new Fx_Unite_Pane("Pillier",getWidth(),50));
		entites.add(new Fx_Unite_Pane("Statue",getWidth(),50));
		entites.add(new Fx_Unite_Pane("Barrière",getWidth(),50));
		//ScrollBar
		scroll_entites.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		scroll_entites.setHbarPolicy(ScrollBarPolicy.NEVER);
		//scroll_entites.setPrefSize(getWidth()/4, getHeight());
		//ListView
		view_entites.setPrefHeight(getHeight());
		//Set des contenus
		observable_entites=FXCollections.observableArrayList(entites);

		view_entites.setItems(observable_entites);
		scroll_entites.setContent(view_entites);
		//Action lors d'un clique pour permettre une selection correct
		view_entites.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(event.getButton().equals(MouseButton.PRIMARY)) {
					if(view_entites.getSelectionModel().getSelectedItem()!=null) {
						if(view_entites.getSelectionModel().getSelectedItem()==selected) {
							view_entites.getSelectionModel().clearSelection();
							selected=null;
						}
						selected=view_entites.getSelectionModel().getSelectedItem();
					}
				}
			}
		});
	}
	/////Objets Selectable
	//Debut Fx_Hexagon_Pane
	public class Fx_Hexagon_Pane extends Pane implements Selectable {
		private Fx_Hexagon_Player fx_hex;
		private Canvas canv;
		private int posable;//0 Pour l'appartenance a aucun joueur //1 pour Joueur 1 //2 pour joueur 2
		public Fx_Hexagon_Pane(int posable,double width,double height) {
			this.posable=posable;//Le joueur pourra t-il poser sur cette case ?
			setWidth(width);
			setHeight(height);
			//
			init();
			getChildren().addAll(fx_hex,canv);
		}
		public void init() {//Instanciation des attributs
			//Node
			canv=new Canvas(getWidth(),getHeight());
			fx_hex=new Fx_Hexagon_Player(posable,getWidth()/5,getHeight()-5);
			//
			GraphicsContext g=canv.getGraphicsContext2D();
			Text text=new Text();
			text.setFont(font_principal);
			//
			String t;//Nom que l'on attribuera selon le type d'hexagone
			if(posable==0)t="Hexagone";
			else if(posable==1)t="Hexagone J1";
			else t="Hexagone J2";
			text.setText(t);
			//
			g.setFont(text.getFont());//Taille de la police
			g.fillText(text.getText(), getWidth()/5+5,getHeight()/2-text.getBoundsInLocal().getHeight()/4);//On dessine le texte et on le met a la bonne position
		}
		//Debut classe interne Fx_Hexagon_Player
		public class Fx_Hexagon_Player extends Fx_Hexagon implements Selectable{//Pour set les cases posables du joueur 1 ou 2
			private int camp;//0==neutre; 1==j1; 2==j2;
			private boolean c;//Boolean camp (true/false)
			public Fx_Hexagon_Player(int posable,double width,double height) {
				super(Color.BLACK);
				this.setWidth(width);
				this.setHeight(height);
				this.camp=posable;
				if(camp==0)getPolygone().setFill(couleurC);
				else if(camp==1) {//Camp du joueur 1
					c=true;
					getPolygone().setFill(couleurB);
				}
				else {//Camp du joueur 2
					c=false;//Pour la clarte (meme si que deja false de base)
					getPolygone().setFill(couleurD);
				}
				this.setHexagone();
			}
			@Override
			public void use(Fx_Hexagon_2 f) {
				if(f.getSupprimer() && camp!=0)return;
				f.reset_camp();
				if(camp!=0) {//Lorsque l'on pose une case ou l'un des joueurs peut poser une unite
					f.removeFx_Unite();///// On ne veut pas d'unite sur une case posable ?
					if(camp==1)visual.editable_grid.incrementer_j1();
					else visual.editable_grid.incrementer_j2();
					
					f.setCamp(c);//camp true pour J1 et false ppour j2
					f.setPosable(true);
				} else {
					f.setCamp(false);
					f.setPosable(false);
				}
				if(f.getSupprimer())f.setSupprimer(false);//Puisque l'on repose l'Hexagone, on l'enleve des Hexagones a supprimer /////
				f.getPolygone().setFill(this.getPolygone().getFill());//On lui attribut sa couleur (selon le camp)
			}
			@Override
			public void Show_transparency(Modifiable_Hexagonal grid, Fx_Hexagon_2 f) {//Affichage de la transparence lorsque la souris survole un Fx_Hexagon_2 (une case du plateau)
				if(f.getSupprimer() && camp!=0)return;//Dans le cas ou une case est supprimer il n'y a que l'Hexagone neutre qui est posable sur le plateau
				double width=f.getPolygone().getBoundsInLocal().getWidth();//Longueur
				double height=f.getPolygone().getBoundsInLocal().getHeight();//Hauteur
				Polygon tmp = new Polygon();//Polygon qui s'affichera en transparent
				trash.add(tmp);//On l'ajoute dans une liste qui sera ensuite jete (ou consommer)
				tmp.setFill(this.getPolygone().getFill());//On lui attribut la meme couleur
				if(camp==0) {
					tmp.setFill(Color.WHITE.darker());//Dans le cas ou l'on pose un Hexagone normal on lui attribut une couleur un peu grise
				}
				tmp.setLayoutX(f.getLayoutX());//Position en X
				tmp.setLayoutY(f.getLayoutY());//Position en y
				tmp.setOpacity(0.4);//Transparence
				tmp.getPoints().addAll(new Double[] {//Points
						getLayoutX(), getLayoutY()+height/4,
						getLayoutX()+width/2, getLayoutY(),
						getLayoutX()+width-1, getLayoutY()+height/4,

						getLayoutX()+width-1, getLayoutY()+height-height/4,
						getLayoutX()+width/2, getLayoutY()+height,
						getLayoutX(), getLayoutY()+height-height/4,
				});
				grid.getChildren().add(tmp);//On l'ajoute au plateau
				tmp.toBack();//On le met en arriere plan pour eviter certaines collisions
			}
		}//Fin classe interne Fx_Hexagon_Player
		@Override
		public void use(Fx_Hexagon_2 f) {
			fx_hex.use(f);
		}

		@Override
		public void Show_transparency(Modifiable_Hexagonal grid, Fx_Hexagon_2 f) {
			fx_hex.Show_transparency(grid, f);
		}
		
	}//Fin Fx_Hexagon_Pane

	//Debut Fx_Terrain_2
	public class Fx_Terrain_2 extends Fx_Terrain implements Selectable{

		public Fx_Terrain_2(String n,double width,double height) {//Instancie un Fx_Terrain et le dessine
			super(n);
			setWidth(width);
			setHeight(height);
			this.setPoints();//Methode presente dans Fx_Terrain
		}
		public void setImage(String NOM_IMAGE) {
	        NOM_IMAGE+=".png";
	        GraphicsContext g=this.getGraphicsContext2D();
	        try {
	            image=new Image(new File(PATH+"/"+NOM_IMAGE).toURI().toURL().toString());
	            g.drawImage(image,0,0, this.getWidth()/5, this.getWidth()/5);
	            g.setFont(font_principal);
	            g.fillText(this.nom, this.getWidth()/5,this.getHeight()/2);/////Utiliser panel Text pour les getBounds
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
		@Override
		public void use(Fx_Hexagon_2 f) {
			if(f.getSupprimer())return;//On ne peut pas poser de cases sur une case supprimer
			f.removeFxTerrain();//Enleve le terrain s'il y en a
			f.setFxTerrain(new Fx_Terrain(this.nom));//Set le terrain dans la case
		}
		@Override
		public void Show_transparency(Modifiable_Hexagonal grid, Fx_Hexagon_2 f) {
			if(f.getSupprimer())return;
			Fx_Terrain tmp = new Fx_Terrain(this.nom);
			double width=f.getPolygone().getBoundsInLocal().getWidth();
			double height=f.getPolygone().getBoundsInLocal().getHeight();
			tmp.setWidth(width);
			tmp.setHeight(height);
			tmp.setPoints();
			//
			trash.add(tmp);
			tmp.setLayoutX(f.getLayoutX());//Position en X
			tmp.setLayoutY(f.getLayoutY());//Position en Y
			tmp.setOpacity(0.4);//Opacite (transparence)
			grid.getChildren().add(tmp);
			tmp.toBack();//Important //Le met en arriere plan des autres Panel/Nodes
		}

	}//Fin Fx_Terrain_2
	//Debut classe interne EnleverCase
	public class EnleverCase extends Pane implements Selectable {
		private Erase erase;
		private Canvas canv;
		private Image i;
		private String NOM_IMAGE="Supprimer";
		EnleverCase(double width,double height){
			setWidth(width);
			setHeight(height);
			init();

			getChildren().addAll(erase,canv);
		}
		public void init() {
			//Node
			canv=new Canvas(getWidth(),getHeight());
			erase=new Erase(getWidth()/5,getHeight());
			NOM_IMAGE+=".png";

			GraphicsContext g=canv.getGraphicsContext2D();
			
			try {
				i=new Image(new File(PATH+"/"+NOM_IMAGE).toURI().toURL().toString());
				g.drawImage(i, 0, 0, erase.getWidth(), erase.getHeight());
			} catch (Exception e) {
				e.printStackTrace();
			}
			Text text=new Text();
			text.setFont(font_principal);
			//
			String t="Supprimer";
			text.setText(t);
			//
			g.setFont(text.getFont());
			g.fillText(text.getText(), getWidth()/5+5,getHeight()/2-text.getBoundsInLocal().getHeight()/4);
			}
		//Debut classe interne Erase
		public class Erase extends Pane implements Selectable {
			Erase(double width,double height){
				setWidth(width);
				setHeight(height);
			}
			@Override
			public void use(Fx_Hexagon_2 f) {
				if(f.getSupprimer())return;//On ne supprime pas une case deja supprimer
				if(!f.getSupprimer())f.setSupprimer(true);//On la "supprime" (pour le moment mise dans une liste)
			}
			@Override
			public void Show_transparency(Modifiable_Hexagonal grid, Fx_Hexagon_2 f) {
				if(f.getSupprimer())return;
				if(i==null)return;
				Canvas tmp = new Canvas();
				double width=f.getPolygone().getBoundsInLocal().getWidth();
				double height=f.getPolygone().getBoundsInLocal().getHeight();
				tmp.setWidth(width);
				tmp.setHeight(height);
				//
				GraphicsContext g=tmp.getGraphicsContext2D();
				g.drawImage(i,0,0, tmp.getWidth(),tmp.getHeight());
				//
				trash.add(tmp);
				tmp.setLayoutX(f.getLayoutX());
				tmp.setLayoutY(f.getLayoutY());
				tmp.setOpacity(0.4);
				grid.getChildren().add(tmp);
				tmp.toBack();//Important
			}
		}//Fin classe interne Erase

		@Override
		public void use(Fx_Hexagon_2 f) {
			erase.use(f);
		}
		@Override
		public void Show_transparency(Modifiable_Hexagonal grid, Fx_Hexagon_2 f) {
			erase.Show_transparency(grid, f);
		}
	}//Fin classe interne EnleverCase
	//debut classe Fx_Unite_Pane
	public class Fx_Unite_Pane extends Pane implements Selectable {
		private String nom;
		private Canvas canv;
		private Fx_Unite_2 fx_unit;
		public Fx_Unite_Pane(String n,double width,double height) {
			this.setWidth(width);
			this.setHeight(height);
			this.nom=n;
			init();
			getChildren().addAll(fx_unit,canv);
		}
		void init() {
			canv=new Canvas(getWidth(),getHeight());
			fx_unit=new Fx_Unite_2(nom,getWidth()/5,getHeight());
			GraphicsContext g=canv.getGraphicsContext2D();
			
			Text text=new Text();
			text.setFont(font_principal);
			//
			String t=nom;
			text.setText(t);
			//
			g.setFont(text.getFont());
			g.fillText(text.getText(), getWidth()/5+5,getHeight()/2-text.getBoundsInLocal().getHeight()/4);
		}
		//Debut classe interne Fx_Unite_2
		public class Fx_Unite_2 extends Fx_Unite implements Selectable {

			public Fx_Unite_2(String n,double width,double height) {
				super(n, -1);
				this.setWidth(width);
				this.setHeight(height);
				dessineImage(false);
			}
			@Override
			public void dessineImage(boolean inList) {//Dessine l'unite selon qu'il soit dans une liste ou non //Redefinition pour l'adapter a notre situation
				GraphicsContext g=this.getGraphicsContext2D();
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				setImage();
				if(this.getImage()!=null) {
					if(this.getImage().getWidth()>getWidth() && this.getImage().getHeight()>getHeight()) {
						g.drawImage(this.getImage(), 0, 0,getWidth(),getHeight());
					}
					else if(this.getImage().getWidth()>getWidth()) {
						g.drawImage(this.getImage(), 0, 0,getWidth()-10,this.getImage().getHeight());
					}
					else {
						g.drawImage(this.getImage(), getWidth()/2-this.getImage().getWidth()+10, 0, this.getImage().getWidth()-30, getHeight()-10);
					}
				}
			}
			@Override
			public void use(Fx_Hexagon_2 f) {
				if(f.getSupprimer())return;
				f.reset_camp();
				double width=f.getPolygone().getBoundsInLocal().getWidth();
				double height=f.getPolygone().getBoundsInLocal().getHeight();
				f.setFx_Unite(new Fx_Unite_2(this.getnom(),width,height));
			}

			@Override
			public void Show_transparency(Modifiable_Hexagonal grid, Fx_Hexagon_2 f) {
				if(f.getSupprimer())return;//On verifie que la Fx_Hexagon n'est pas supprimer
				Canvas tmp = new Canvas();
				double width=f.getPolygone().getBoundsInLocal().getWidth();
				double height=f.getPolygone().getBoundsInLocal().getHeight();
				tmp.setWidth(width);
				tmp.setHeight(height);
				//
				GraphicsContext g=tmp.getGraphicsContext2D();
				g.drawImage(this.getImage(),0,0, tmp.getWidth(),tmp.getHeight());
				//
				trash.add(tmp);
				tmp.setLayoutX(f.getLayoutX());
				tmp.setLayoutY(f.getLayoutY());
				tmp.setOpacity(0.4);
				grid.getChildren().add(tmp);
				tmp.toBack();//Important
			}
		}//Fin classe interne Fx_Unite_2

		@Override
		public void use(Fx_Hexagon_2 f) {
			fx_unit.use(f);
		}

		@Override
		public void Show_transparency(Modifiable_Hexagonal grid, Fx_Hexagon_2 f) {
			fx_unit.Show_transparency(grid,f);
		}
	}//Fin classe interne Fx_Unite_Pane
}//Fin class interne Items
/////Fin des Items et des objets Selectable
//Debut class COnfirmer pour confirmer certaines request de l'utilisateur
public class Confirmer extends Pane {//Page de confirmation
	private Rectangle rect;
	private Choice choice;
	private Modulable node;
	public Confirmer(Modulable node,double width,double height) {
		this.setWidth(width);
		this.setHeight(height);
		this.node=node;
		init_2();
		getChildren().addAll(rect,choice);
	}
	public void init_2() {
		//Rectangle
		rect=new Rectangle(getWidth(),getHeight());
		rect.setFill(Color.BLACK);
		rect.setOpacity(0.2);
		//Choice
		choice=new Choice(node,getWidth(),getHeight()/4);
		choice.setLayoutY(getHeight()/2-choice.getHeight()/2);
	}
	public class Choice extends Pane {
		private Rectangle rect2;
		private Button accept;//Bouton de confirmation
		private Button refuse;//Bouton pour annuler la requete
		private Label erreur;//Message d'erreur
		//Timeline - duration du message d'erreur
		final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2),
				new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				erreur.setText("");
			}
		}));
		public Choice(Modulable node,double width,double height) {
			this.setWidth(width);
			this.setHeight(height);
			//Rectangle
			rect2=new Rectangle(getWidth(),getHeight());
			//Rectangle properties
			rect2.setFill(fond_principal);
			rect2.setStroke(bordures);
			rect2.setStrokeWidth(2);
			//Button
			//Accept - Button
			accept=new Button("Confirmer");
			accept.setTextFill(Color.WHITE);
			accept.setStyle("-fx-border-color:DARKGOLDENROD;");
			accept.setStyle("-fx-background-color: green;");//Background en vert
			accept.setOnMouseEntered(e->{
				accept.setStyle("-fx-background-color: lightgreen;");//Background en vert
			});
			accept.setOnMouseExited(e->{
				accept.setStyle("-fx-background-color: green;");//Background en vert
			});
			accept.setOnMouseClicked(e->{
				if(node.condition()) {
					node.activer();
					confirme(null);
				} else {
					erreur.setText(node.error());
					timeline.playFrom(Duration.ZERO);
				}
			});
			//Refuse - Button
			refuse=new Button("Annuler");
			refuse.setTextFill(Color.WHITE);
			refuse.setStyle("-fx-border-color:DARKGOLDENROD;");
			refuse.setStyle("-fx-background-color: red;");//Background en rouge
			refuse.setOnMouseEntered(e->{
				refuse.setStyle("-fx-background-color: lightcoral;");//Background en rouge
			});
			refuse.setOnMouseExited(e->{
				refuse.setStyle("-fx-background-color: red;");//Background en rouge
			});
			refuse.setOnMouseClicked(e->{
				confirme(null);
			});
			//Label
			erreur=new Label();
			erreur.setTextFill(Color.DARKRED);
			erreur.setAlignment(Pos.CENTER);
			//
			VBox v=new VBox();
			HBox h=new HBox(0.5,accept,refuse);
			h.setAlignment(Pos.CENTER);
			//Node
			Node n=node.node();
			StackPane.setAlignment(n, Pos.CENTER);
			//StackPane qui contiendra le node de Modulable
			StackPane s=new StackPane();
			s.setPrefSize(getWidth(), getHeight()/2);
			s.getChildren().add(n);
			//
			v.setAlignment(Pos.CENTER);
			v.getChildren().addAll(s,erreur,h);
			getChildren().addAll(rect2,v);
			n.setLayoutX(getWidth()/2-n.getBoundsInLocal().getWidth()/2);
		}
	}
}//Fin Confirmer
public interface Modulable {
	Node node();//Panel que l'on revoie et que l'on donne a afficher
	boolean condition();//Condition de validite pour confirmer une requete
	String error();//Texte d'erreur que l'on affiche dans le cas ou la condition() n'est pas remplie
	void activer();//Active l'effect que le Node/panel devait faire
}
//Debut classe interne Request
public class Request extends Pane {//"Valider carte / Tout effacer carte etc..." 
	private HBox hbox;
	private Valider valider;
	private Effacer effacer;
	private Option settings;
	public Request(double width,double height) {
		this.setWidth(width);
		this.setHeight(height);
		init_2();
		getChildren().add(hbox);
	}
	public void init_2() {
		double width=getWidth()/3;
		double height=getHeight()/3;
		hbox=new HBox(0);
		valider=new Valider(width,height);
		effacer=new Effacer(width,height);
		settings=new Option(width,height);
		valider.setDisable2(true);
		hbox.getChildren().addAll(valider,effacer,settings);
	}
	public class Valider extends Pane implements Modulable{//Bouton de validation de la carte
		private String NOM_IMAGE="Valider";
		private Rectangle rect;
		private Canvas canv;
		private Group panel;
		//Donnees a remplir par le joueur
		private TextField plateau;//Nom du plateau 
		private Button openButton;//Button pour ouvrir l'image pour le plateau
		private TextField monnaie;//Montant de monnaie des joueurs
		//
		File image_plateau;//
		Valider(double width,double height){
			this.setWidth(width);
			this.setHeight(height);
			rect=new Rectangle(getWidth(),getHeight());
			canv=new Canvas(getWidth(),getHeight());
			pane();//Instanciation du panel a envoye
			getChildren().addAll(rect,canv);
			//Rectangle
			rect.setFill(fond_principal);
			rect.setStroke(bordures);
			//Canvas
			GraphicsContext g=canv.getGraphicsContext2D();
			NOM_IMAGE+=".png";
			
			try {
				Image i=new Image(new File(PATH+"/"+NOM_IMAGE).toURI().toURL().toString());
				g.drawImage(i, rect.getStrokeWidth(), rect.getStrokeWidth(), canv.getWidth()-rect.getStrokeWidth()*2, canv.getHeight()-rect.getStrokeWidth()*2);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		public void setDisable2(boolean bool) {
			super.setDisable(bool);
			if(bool) {
				rect.setFill(Color.LIGHTGRAY);
			} else {
				rect.setFill(fond_principal);
				this.setOnMouseEntered(e->{
					rect.setFill(fond_principal.darker());
				});
				this.setOnMouseExited(e->{
					rect.setFill(fond_principal.brighter());
				});
			}
			this.setOnMouseClicked(e->{
				this.requestFocus();
				confirme(this);
			});
		}
		private void pane() {
			//Group
			panel=new Group();
			//Label
			Label pane1=new Label("Nom du plateau :");
			Label pane2=new Label("Image pour le plateau :");
			Label pane3=new Label("Monnaie pour les joueurs :");
			//Instanciation des attributs
			//TextField - nom du plateau
			plateau=new TextField(visual.nom_carte);
			plateau.setTextFormatter(new TextFormatter<>(change -> {//Pas d'espace non plus (semblable a visual.nom_du_plateau)
			    if (change.getText().equals(" ") || !change.getText().matches("[\\w-]+") || plateau.getText().length()>30){
			        change.setText("");
			    }
			    return change;
			}));
			plateau.textProperty().addListener(new ChangeListener<String>() {
			    @Override
			    public void changed(ObservableValue<? extends String> observable,String oldValue, String newValue) {
			    	visual.nom_du_plateau.setText(newValue);
			    }
			});
			//Button
			openButton=new Button("Choisissez un fichier");
			/////
			//FileChooser
        	FileChooser chooser=new FileChooser();
        	chooser.setTitle("Selectionner carte");//Nom du FileChooser
        	chooser.setInitialDirectory(new File(System.getProperty("user.home")));//Repertoire de depart
        	chooser.getExtensionFilters().add(new ExtensionFilter("Images Files","*.png"));//On accepte que les images
			//Monnaie
			monnaie=new TextField("500");
			monnaie.textProperty().addListener(new ChangeListener<String>() {//Pour n'accepter que des chiffres
			    @Override
			    public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
			        if (!newValue.matches("\\d*")) {
			        	monnaie.setText(newValue.replaceAll("[^\\d]", ""));
			        }
			    }
			});
			monnaie.focusedProperty().addListener(new ChangeListener<Boolean>()
			{
			    @Override
			    public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			    {
			        if (!newPropertyValue)//Lorsque l'on n'est plus dans le TextField "monnaie" on verifie qu'il y a une somme d'argent raisonnable
			        	if(Integer.parseInt(monnaie.getText())<500)monnaie.setText("500");
			        	else if (Integer.parseInt(monnaie.getText())>10000)monnaie.setText("10000");
			    }
			});
			openButton.setOnAction(new EventHandler<ActionEvent>() {//Selectionnez une image comme carte :
                @Override
                public void handle(final ActionEvent e) {
                	image_plateau=chooser.showOpenDialog(Map_Editor.this.getScene().getWindow());
                	if(image_plateau!=null) {
                		openButton.setText(image_plateau.getAbsolutePath());
                	}
                }
            });
			//HBox
			HBox h1=new HBox(0.75,pane1,plateau);
			HBox h2=new HBox(0.75,pane2,openButton);
			HBox h3=new HBox(0.75,pane3,monnaie);
			//VBox
			VBox v=new VBox(0.75,h1,h2,h3);
			panel.getChildren().add(v);
			return;
		}
		@Override
		public Node node() {
			plateau.setText(visual.nom_carte);
			return panel;//File Chooser avec cartes
		}
		@Override
		public boolean condition() {
			if(plateau.getText().length()<1)return false;
			if(image_plateau==null)return false;
			if(visual.editable_grid.posable_j1<1 || visual.editable_grid.posable_j2<1)return false;
			return true;
		}
		@Override
		public void activer() {
			//On passe l'HexagonalGrid "au model", on sauvegardera la map du joueur dans le bon emplacement avec un bon nom dans le cas ou tout est correct 
			selection_map.save_edit(plateau.getText(),image_plateau.getAbsolutePath(),Integer.parseInt(monnaie.getText()),visual.editable_grid);
		}
		@Override
		public String error() {
			if(plateau.getText().length()<1)return "Nom du plateau incorrect";
			if(image_plateau==null)return "Veuillez selectionner une image";
			if(visual.editable_grid.posable_j1<1 || visual.editable_grid.posable_j2<1)return "Nombre de cases posables pour le joueur 1 ou le joueur 2 incorrect";//N'est pas cense arrive
			return "Veuillez reessayer ulterieurement";
		}

	}

	public class Effacer extends Pane implements Modulable{//Bouton pour enlever tous les elements sur le plateau (reset tout le plateau)
		private String NOM_IMAGE="Effacer";
		private Rectangle rect;
		private Canvas canv;
		private Label panel;
		Effacer(double width,double height){
			this.setWidth(width);
			this.setHeight(height);
			rect=new Rectangle(getWidth(),getHeight());
			canv=new Canvas(getWidth(),getHeight());
			pane();//Instanciation du panel a envoye
			getChildren().addAll(rect,canv);
			//Rectangle
			rect.setFill(fond_principal);
			rect.setStroke(bordures);
			rect.setStrokeWidth(1);

			//Canvas
			GraphicsContext g=canv.getGraphicsContext2D();
			NOM_IMAGE+=".png";
			
			try {
				Image i=new Image(new File(PATH+"/"+NOM_IMAGE).toURI().toURL().toString());
				g.drawImage(i, rect.getStrokeWidth(), rect.getStrokeWidth(), canv.getWidth()-rect.getStrokeWidth()*2, canv.getHeight()-rect.getStrokeWidth()*2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.setOnMouseEntered(e->{
				rect.setFill(fond_principal.darker());
			});
			this.setOnMouseExited(e->{
				rect.setFill(fond_principal.brighter());
			});
			this.setOnMouseClicked(e->{
				this.requestFocus();
				confirme(this);
			});
		}
		private void pane() {
			//Label
			panel=new Label();
			panel.setWrapText(true);
			panel.setText("Etes-vous sur de vouloir effacer toutes les modifications faites sur le plateau d'Hexagones ?");
			return ;
		}
		@Override
		public Node node() {
			return panel;
		}
		@Override
		public boolean condition() {
			return true;
		}
		@Override
		public void activer() {
			for(int i=0;i<visual.editable_grid.getGrille().length;i++) {
				for(int j=0;j<visual.editable_grid.getGrille()[i].length;j++) {
					((Fx_Hexagon_2) visual.editable_grid.getGrille()[i][j]).reset();
				}
			}
		}
		@Override
		public String error() {
			//N'est jamais cense arrive par la mais on sait jamais (car condition() est toujours true
			return "Une erreur est survenue lors de votre tentative de suppression des modifications faites sur votre plateau, veuillez reessayer";
		}
	}
	public class Option extends Pane implements Modulable{
		private String NOM_IMAGE="Option";
		//Affichage du bouton avec les icones
		private Rectangle rect;
		private Canvas canv;
		//Pour la page de confirmation
		private Group pane;//Node que l'on renverra pour la page de confirmation
		private TextField x1;//Zone de texte pour la valeur x (longueur du plateau)
		private TextField y1;//Zone de texte pour la valeur en y (hauteur du plateau)
		//Donnes min et max du plateau

		Option(double width,double height){
			this.setWidth(width);
			this.setHeight(height);
			rect=new Rectangle(getWidth(),getHeight());
			canv=new Canvas(getWidth(),getHeight());
			pane();//Instanciation du panel a envoye
			getChildren().addAll(rect,canv);
			//Rectangle
			rect.setFill(fond_principal);
			rect.setStroke(bordures);
			//Canvas
			GraphicsContext g=canv.getGraphicsContext2D();
			NOM_IMAGE+=".png";
			
			try {
				Image i=new Image(new File(PATH+"/"+NOM_IMAGE).toURI().toURL().toString());
				g.drawImage(i, rect.getStrokeWidth(), rect.getStrokeWidth(), canv.getWidth()-rect.getStrokeWidth()*2, canv.getHeight()-rect.getStrokeWidth()*2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.setOnMouseEntered(e->{
				rect.setFill(fond_principal.darker());
			});
			this.setOnMouseExited(e->{
				rect.setFill(fond_principal.brighter());
			});
			this.setOnMouseClicked(e->{
				this.requestFocus();
				confirme(this);
			});
		}
		private void pane() {
			pane = new Group();
			VBox vbox=new VBox(1);
			//HBox
			HBox h=new HBox();
			HBox h2=new HBox();

			//Label
			Label text=new Label("Changer la taille du plateau x et y avec 6<=y<=15 et y<x<=25");
			text.setWrapText(true);
			Label x=new Label("x :");
			Label y=new Label("y :");

			//TextField
			x1=new TextField();//N'accepte que des valeurs numeriques
			y1=new TextField();//N'accepte que des valeurs numeriques
			x1.textProperty().addListener(new ChangeListener<String>() {//On accepte que les entiers (chez x)
			    @Override
			    public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
			        if (!newValue.matches("\\d*")) {
			        	x1.setText(newValue.replaceAll("[^\\d]", ""));
			        }
			    }
			});
			y1.textProperty().addListener(new ChangeListener<String>() {//On accepte  que les entiers (chez y)
			    @Override
			    public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
			        if (!newValue.matches("\\d*")) {
			        	y1.setText(newValue.replaceAll("[^\\d]", ""));
			        }
			    }
			});
			//
			h.getChildren().addAll(x,x1);
			h2.getChildren().addAll(y,y1);
			h.setAlignment(Pos.CENTER);
			h2.setAlignment(Pos.CENTER);
			vbox.getChildren().addAll(text,h,h2);
			pane.getChildren().add(vbox);
			return ;
		}
		@Override
		public Node node() {
			return pane;
		}
		@Override
		public boolean condition() {
			if(x1.getText().length()<1 || y1.getText().length()<1)return false;
			int x_bis=Integer.parseInt(x1.getText());
			int y_bis=Integer.parseInt(y1.getText());
			return (6<=y_bis && y_bis<=15 && y_bis<x_bis && x_bis<=25);//Si le nouveau x et y sont correctes
		}
		@Override
		public void activer() {
			//Au preable changement des parametres x et y
			int x_bis=Integer.parseInt(x1.getText());
			int y_bis=Integer.parseInt(y1.getText());
			Map_Editor.this.x=x_bis;
			Map_Editor.this.y=y_bis;
			visual.editable_grid.set_dimension(x, y);
			visual.editable_grid.setGrid();
			visual.editable_grid.setEvent();	
		}
		@Override
		public String error() {
			if(x1.getText().length()<1 || y1.getText().length()<1)return "Il vous faut specifier les valeurs de x et y";
			if(6<=y && y<=15 && y<x && x<=25) {
				return "il faut bien que les dimensions soit respecter";
			}
			else {
				return "Une erreur est survenue veuillez réessayer";
			}
		}
	}
}//Fin classe interne Request
public void setDim(int x,int y) {//A n'utiliser qu'avec des proportions correctes
	if(x<=0 || y<=0 || x>=30 || y>=30)return;
	this.x=x;
	this.y=y;
	visual.editable_grid.set_dimension(x, y);
	visual.editable_grid.setGrid();
	visual.editable_grid.setEvent();
}
//Debut classe interne Retour
public class Retour extends Pane {
	private double espX=7;//Espace entre le rectangle et la fleche (Polygon) en x
	private double espY=7;//Espace entre le rectangle et la fleche (Polygon) en y
	Retour(double width,double height){
		setWidth(width);
		setHeight(height);
		//Rectangle
		Rectangle r=new Rectangle(width,height);
		r.setFill(Color.WHITE);//Couleur selectionne du Fill
		//Bordures
		r.setStroke(bordures);//Couleur selectionne de la bordure
		r.setStrokeWidth(2);//Longueur de la bordure
		//Polygon
		Polygon p=new Polygon();//Bouton retour
		p.setStroke(bordures);
		p.setStrokeWidth(1);
		double lg=width-espX*2;
		double ht=height-espY*2;
		double longueur_arrow=lg/1.5;
		double longueur_reste=lg-longueur_arrow;
		double hauteur_arrow=(2*ht)/2;
		double portion_arrow=2.5;
		p.getPoints().addAll(new Double[] {
				espX, ht/2,
				longueur_arrow, espY,
				longueur_arrow, hauteur_arrow/portion_arrow,
				
				longueur_arrow+longueur_reste+espX,hauteur_arrow/portion_arrow,
				longueur_arrow+longueur_reste+espX,ht+espY,
				lg-(2*lg/15),ht+espY,
				
				lg-(2*lg/15),hauteur_arrow-hauteur_arrow/portion_arrow,
				longueur_arrow,hauteur_arrow-hauteur_arrow/portion_arrow,
				longueur_arrow,hauteur_arrow-espY
		});
		//
		this.setOnMouseEntered(e->{
			r.setFill(((Color)r.getFill()).darker());
		});
		this.setOnMouseExited(e->{
			r.setFill(((Color)r.getFill()).brighter());
		});
		//Action du bouton start
		this.setOnMouseClicked(e->{
			selection_map.back();
		});
		getChildren().addAll(r,p);
	}
}//Fin classe interne Retour
}
