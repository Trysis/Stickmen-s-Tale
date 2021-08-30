package application;

import java.io.File;
import java.net.MalformedURLException;
import java.util.LinkedList;

import application.Fx_Unite.Fx_Skill;
import carte.Plateau;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;

import javafx.scene.paint.Stop;

import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javafx.scene.transform.Transform;
import javafx.util.Duration;

public class Menu_J extends Pane{
	private Rectangle fond_principal;//Fond

	private Color fond2=Color.rgb(244, 244, 244);
	
	private String title_name;
	
	private Selection_map selection_map;
	
	private Selection_Mode mode;
	private String p1;
	private String p2;
	
	private Pre_Editor pre_editeur;
	private Map_Editor editeur;
	private Encyclopedie encyclopedie;
	
	private Parent accueil;
	private  Box vbox;
	private LinkedList<Menobserver> obs=new LinkedList<Menobserver>();
	
	public Menu_J(String name){//Possede initialement les boutons de commencement de la partie et autre
		title_name=name;
		accueil = box_creation();
	}
	public void set(double WIDTH,double HEIGHT) {
		fond_principal=new Rectangle(WIDTH,HEIGHT);
		ImageView image=null;
		try {
			image=new ImageView(new File("src/Ressources/Jeu/Fond.PNG").toURI().toURL().toString());
			image.setFitHeight(this.getHeight());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getChildren().add(image);
		//fond_principal.setFill(fond);
		getChildren().addAll(accueil);
	}
	private  Parent box_creation() {
		Pane root = new Pane();
		
		Titre title = new Titre (title_name);
		title.setTranslateX(50);
		title.setTranslateY(300);
		
		vbox = new Box(25,new Struct("NOUVELLE PARTIE",1),new Struct("NOUVELLE CARTE",2),new Struct("ENCYCLOPEDIE",3));
		vbox.setTranslateX(100);
		vbox.setTranslateY(400);

		root.getChildren().addAll(title,vbox);
		return root;
	}
	
	//Class interne Titre
	private  class Titre extends StackPane{
		public Titre(String name) {
			Rectangle rec = new Rectangle(375, 60);
			rec.setStroke(Color.WHITE);
			rec.setStrokeWidth(3);
			rec.setFill(null);
			
			Text text = new Text(name);
			text.setFill(Color.RED);
			text.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 37));
			
			setAlignment(Pos.CENTER);
			getChildren().addAll(rec,text);
		}
	}//Fin class interne Titre
	//Classe interne Box 
	private  class Box extends VBox{
		public Box(double y,Struct...x) {
			super(y);
			for(Struct item : x)getChildren().addAll(item);	
		}
	}
	//Fin class interne Box
	//Classe interne Struct
	private  class Struct extends StackPane{
		//private String name;
		private int id;
		public Struct(String name, int id) {
			//this.name=name;//Nom du StackPane
			this.id=id;//Id du StackPane

			LinearGradient degrade = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop[] { new Stop(0, Color.RED), new Stop(0.1, Color.BLACK), new Stop(0.9, Color.BLACK), new Stop(1, Color.RED)});

			Rectangle rec = new Rectangle(200,30);
			rec.setOpacity(0.4);
			
			Text text = new Text(name);
			text.setFill(Color.DARKGREY);
			text.setFont(Font.font("Arial", FontWeight.SEMI_BOLD,20));
			
			setAlignment(Pos.CENTER);
			getChildren().addAll(rec, text);
			setOnMouseEntered(event -> {
				rec.setFill(degrade);
				if(this.id>3)text.setFill(Color.DARKRED);
				else text.setFill(Color.WHITE);
			});
			
			setOnMouseExited(event -> {
				rec.setFill(Color.BLACK);
				text.setFill(Color.DARKGREY);
			});
			setOnMousePressed(event -> {
				rec.setFill(Color.DARKRED);
			});
			setOnMouseReleased(event -> {
				rec.setFill(degrade);
			});
			setOnMouseClicked(e->{
				if(e.getClickCount()==1) {
					if(id==1)setNewVue();
					if(id==2)pre_Editeur();
					if(id==3)Encyclopedie();
				}
			});
			
		}
		
	}//Fin classe interne Struct
	//Debut classe interne Selection_Mode
			public class Selection_Mode extends Pane{
				//Debut classe interne Titre
				private Rectangle rect = new Rectangle(550,600);
				private Retour bouton_retour = new Retour(50,50);

				public class Titre extends Pane{
					private Rectangle sous_rect = new Rectangle(300,75);		

					Titre(){		
						Image image=null;
						try {
							image=new Image(new File("src/Ressources/Jeu/Paper.jpg").toURI().toURL().toString());
							ImagePattern m=new ImagePattern(image);
							sous_rect.setFill(m);//Couleur selectionne du Fill
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//Canvas
						Canvas c=new Canvas(300,75);
						GraphicsContext g=c.getGraphicsContext2D();
						g.setStroke(Color.DARKGOLDENROD);				
						//Texte et police du Canvas
						Font font=new Font(30);
						Text te=new Text("Choix du mode de jeu");
						te.setFont(font);
						g.setFont(font);
						g.strokeText(te.getText(),5, 50);
						this.setLayoutX(Menu_J.this.getWidth()/2 - sous_rect.getWidth()/2);
						this.setLayoutY(this.getLayoutY()+100);
						
						getChildren().addAll(sous_rect,c);
					}
				}
				//Debut classe interne Mode
				public class Mode extends Pane{
					private Rectangle sous_rect = new Rectangle(200,50);		
					Mode(double height, double width, String t, double layY){
						//Couleur selectionne du Fill
						//Bordures
						Image image=null;
						try {
							image=new Image(new File("src/Ressources/Jeu/Paper.jpg").toURI().toURL().toString());
							ImagePattern m=new ImagePattern(image);
							sous_rect.setFill(m);//Couleur selectionne du Fill
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						sous_rect.setStroke(Color.DARKGOLDENROD);//Couleur selectionne de la bordure
						sous_rect.setStrokeWidth(2);//Longueur de la bordure
						//Canvas
						Canvas c=new Canvas(width,height);
						GraphicsContext g=c.getGraphicsContext2D();
						g.setStroke(Color.rgb(52, 73, 94));				
						//Texte et police du Canvas
						Font font=new Font(20);
						Text te=new Text(t);
						te.setFont(font);
						g.setFont(font);
						g.strokeText(te.getText(),width/22, height/1.5);
						//
						/*this.setOnMouseEntered(e->{
							sous_rect.setFill(((Color)sous_rect.getFill()).darker());
						});
						this.setOnMouseExited(e->{
							sous_rect.setFill(((Color)sous_rect.getFill()).brighter());
						});*/	
						this.setLayoutX(Menu_J.this.getWidth()/2 - sous_rect.getWidth()/2);
						this.setLayoutY(layY);
						getChildren().addAll(sous_rect);
	
						getChildren().add(c);
					}
				}
				
				private Rectangle fond_mode;
				private Titre titre = new Titre();
				
				private Mode jvj = new Mode(50,200,"Joueur contre Joueur",(Menu_J.this.getHeight()/4) *1.5);
				private Mode rvj = new Mode(50,200,"Robot contre Joueur",(Menu_J.this.getHeight()/4) *2.0);
				private Mode rvr = new Mode(50,200,"Robot contre Robot",(Menu_J.this.getHeight()/4) *2.5);


				Selection_Mode(){	
					rect.setStroke(Color.DARKGOLDENROD);
					rect.setStrokeWidth(10);
					Image image=null;
					try {
						image=new Image(new File("src/Ressources/Jeu/Paper.jpg").toURI().toURL().toString());
						ImagePattern image2=new ImagePattern(image);
						rect.setFill(image2);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					rect.setLayoutX(Menu_J.this.getWidth()/4);
					rect.setLayoutY(50);
					fond_mode=new Rectangle(Menu_J.this.getWidth(),Menu_J.this.getHeight());
					try {
						image=new Image(new File("src/Ressources/Jeu/table.jpg").toURI().toURL().toString());
						ImagePattern image2=new ImagePattern(image,0,0,fond_mode.getWidth(),fond_mode.getHeight(),false);
						fond_mode.setFill(image2);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					jvj.setOnMouseClicked(e->{
						selection_map = new Selection_map();
						p1="Joueur";
						p2="Joueur";
						getChildren().add(selection_map);
					});
					rvj.setOnMouseClicked(e->{
						selection_map = new Selection_map();
						p1="Robot";
						p2="Joueur";
						getChildren().add(selection_map);

					});
					rvr.setOnMouseClicked(e->{
						selection_map = new Selection_map();
						p1="Robot";
						p2="Robot";
						getChildren().add(selection_map);

					});
					bouton_retour.setLayoutX(Menu_J.this.getWidth()-bouton_retour.getWidth()-5);
					bouton_retour.setLayoutY(this.getLayoutY()+5);
					bouton_retour.setOnMouseClicked(e->{
						back();				
					});
					getChildren().addAll(fond_mode,rect,titre,bouton_retour,jvj,rvj,rvr);
				}
				public void back() {				
					this.setVisible(false);
					Menu_J.this.setVisible(true);
					accueil.setVisible(true);
					
				}
			}//Fin classe interne Selection_Mode
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
					r.setStroke(Color.DARKGOLDENROD);//Couleur selectionne de la bordure
					r.setStrokeWidth(2);//Longueur de la bordure
					//Polygon
					Polygon p=new Polygon();//Bouton retour
					p.setStroke(Color.DARKGOLDENROD);
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
					
					getChildren().addAll(r,p);
				}
			}//Fin classe interne Retour
	//Debut classe interne Selection_map 
	public class Selection_map extends Pane{//Contiendra tout l'affichage pour la selection de la carte
		//Donnees graphique et indice du niveau selectionne
		private int page=0;//Indicedu niveau sur lequel le joueur est
		private double esp_haut=75;//Espace delimitant les HexagonalGrid du haut et du bas
		private double esp_gauche_droite=75;//Espace delimitant les HexagonalGrid de la gauche et de la droite
		//Liste des niveaux et des boutiques associes
		private LinkedList<HexagonalGrid> lvl_list=new LinkedList<>();
		//Partie graphique
		private Rectangle fond_principal;//Fond
		private Head head;//Nom de la page ou l'on se situe 
		private Arrow precedent;//Bouton precedent
		private Arrow suivant;//Bouton suivant
		private Retour bouton_retour;//Bouton de retour a la page precedente
		private Start start_button;//Bouton pour demarrer la partie
		private LinkedList<Map_List> map_list=new LinkedList<>();//Panels affichant les niveaux (HexagonalGrid)

		//Donnees selectionnees pour lancer la partie
		private HexagonalGrid lvl_selected;//Niveau selectionne
		//Couleurs generales
		private Color fond=Color.rgb(244,244,244);//Gris leger de fond
		private Color bordures_color= Color.DARKGOLDENROD;//Bordures generales des panels
		Selection_map(){
			init_level();
			getChildren().addAll(fond_principal,head,precedent,suivant,bouton_retour,start_button);//Bouton precedent et suivant
			getChildren().addAll(map_list);
		}
		public void init() {
			double esp=5;
			fond_principal=new Rectangle(Menu_J.this.getWidth(),Menu_J.this.getHeight());
			fond_principal.setFill(fond);
			
			try {
				Image image=new Image(new File("src/Ressources/Jeu/table.jpg").toURI().toURL().toString());
				ImagePattern image2=new ImagePattern(image,0,0,fond_principal.getWidth(),fond_principal.getHeight(),false);
				fond_principal.setFill(image2);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Instanciation des panels
			head=new Head(Menu_J.this.getWidth(),esp_haut);//Head
			precedent=new Arrow(esp_gauche_droite-esp*2,Menu_J.this.getHeight()/10);//Panel precedent
			suivant=new Arrow(esp_gauche_droite-esp*2,Menu_J.this.getHeight()/10);//Panel suivant
			bouton_retour=new Retour(50,50);//Panel de retour
			start_button=new Start(Menu_J.this.getWidth()/10,esp_haut-esp*2);//Panel start
			//Set les positions
			//Panel "Head"
			head.setLayoutX(0);//Position en X sur l'ecran de "head"
			head.setLayoutY(0);
			//Panel precedent
			precedent.setLayoutX(esp);
			precedent.setLayoutY(Menu_J.this.getHeight()/2-precedent.getHeight()/2);
			//Panel suivant
			suivant.setLayoutX(Menu_J.this.getWidth()-precedent.getWidth()-esp);
			suivant.setLayoutY(Menu_J.this.getHeight()/2-suivant.getHeight()/2);
			suivant.setRotate(180);//Le bouton suivant est le mirroir du precedent
			//Panel retour
			bouton_retour.setLayoutX(Menu_J.this.getWidth()-bouton_retour.getWidth()-esp);
			bouton_retour.setLayoutY(esp);
			//Panel start
			start_button.setLayoutX(Menu_J.this.getWidth()/2-start_button.getWidth()/2);
			start_button.setLayoutY(Menu_J.this.getHeight()-start_button.getHeight()-esp/2);
			//
			precedent.setOnMouseClicked(e->{
				if(page>0) {
					map_list.get(page).setVisible(false);
					page--;
					map_list.get(page).setVisible(true);
					if(!(page>0))precedent.setVisible(false);
					if(page<lvl_list.size()-1)suivant.setVisible(true);
				}
			});
			suivant.setOnMouseClicked(e->{
				if(page<lvl_list.size()-1) {
					map_list.get(page).setVisible(false);
					page++;
					map_list.get(page).setVisible(true);
					if(page+1<lvl_list.size()) {
						if(!map_list.get(page+1).set) {
								for(Menobserver t: obs)t.setHexagonal(lvl_list.get(page+1),page+1);
								map_list.get(page+1).init();
						}
					}
					if(!(page<lvl_list.size()-1))suivant.setVisible(false);
					if(page>0)precedent.setVisible(true);
				}
			});
			//Action du bouton retour 
			bouton_retour.setOnMouseClicked(e->{
				selection_map.back();
			});
		}
		public void init_level() {//Initialise la liste des niveaux (lvl_list) et les panels qui leurs sont associes
			init();
			for(Menobserver tmp: obs)tmp.update_menu(lvl_list);
			for(int i=0;i<lvl_list.size();i++)map_list.add(new Map_List(i));
			if(lvl_list.size()>0) {
				for(Menobserver t: obs)t.setHexagonal(lvl_list.get(0),0);
				map_list.get(0).init();
				if(lvl_list.size()>1) {
					for(Menobserver t: obs)t.setHexagonal(lvl_list.get(1),1);
					map_list.get(1).init();
				}
			}
			if(!(page>0))precedent.setVisible(false);
			if(!(page<lvl_list.size()-1))suivant.setVisible(false);
		}


		//Debut class interne Head
		public class Head extends Pane {
			Head(double width,double height){
				setWidth(width);
				setHeight(height);
				Canvas c=new Canvas(getWidth(),getHeight());
				GraphicsContext g=c.getGraphicsContext2D();
				//
				Font font=new Font(50);
				
				//
				Text t=new Text("Selection du niveau");
				t.setFont(font);
			
				g.setFont(font);
				g.setStroke(bordures_color);
				g.setFill(Color.DARKGREY);
				g.fillText(t.getText(), width/2-t.getBoundsInLocal().getWidth()/2, height/2+t.getBoundsInLocal().getHeight()/4);
				g.strokeText(t.getText(), width/2-t.getBoundsInLocal().getWidth()/2, height/2+t.getBoundsInLocal().getHeight()/4);
				Polygon p=new Polygon();
				getChildren().addAll(c,p);
			}
		}//Debut classe interne Head
		//Debut classe interne Arrow (panel)
		public class Arrow extends Pane {//Panel contenant une fleche pointant vers la gauche
			private double ecart_haut_bas=5;
			private double ecart_gauche_droite=5;
			Arrow(double width,double height){
				setWidth(width);
				setHeight(height);
				//Rectangle
				Rectangle r= new Rectangle(this.getWidth(),this.getHeight());
				r.setFill(Color.WHITE);//Couleur selectionne du fond 
				r.setStrokeWidth(2);
				r.setStroke(bordures_color);//Couleur des bordures
				//Polygon
				Polygon arrow=new Polygon();//Polygon
				arrow.setStrokeWidth(2);//Longueur des bordures
				arrow.setStroke(bordures_color);//Couleur des bordures du polygone
				arrow.getPoints().addAll(new Double[] {
						ecart_gauche_droite,this.getHeight()/2,
						this.getWidth()-ecart_gauche_droite,this.getHeight()-ecart_haut_bas,
						this.getWidth()-ecart_gauche_droite,ecart_haut_bas
				});
				this.setOnMouseEntered(e->{
					r.setFill(((Color)r.getFill()).darker());
				});
				this.setOnMouseExited(e->{
					r.setFill(((Color)r.getFill()).brighter());
				});
				getChildren().addAll(r,arrow);
			}
		}//Fin classe interne Arrow

		//Classe interne Map_List (de Selection_map)
		public class Map_List extends Pane {
			private boolean set=false;//Dit si l'HexagonalGrid a ete charge
			private int page_map;
			private double width;//Longueur d'un rectangle
			private double height;//Hauteur d'un rectangle
			double posX=esp_gauche_droite;
			double posY=esp_haut+5;
			private double et=50;//Bordure entre le rectangle et l'HexagonalGrid pour le dessin de l'HexagonalGrid
			public Map_List(int y) {
				page_map=y;
				width=(Menu_J.this.getWidth()-esp_gauche_droite*2);
				height=Menu_J.this.getHeight()-esp_haut*2-2.5;
				this.setLayoutX(posX);
				this.setLayoutY(posY);
				}
			private void init() {//Initialise l'image centrale contenant l'image d'un HexagonalGrid
				set=true;
				HexagonalGrid tmp = lvl_list.get(page_map);//
					
			    SnapshotParameters sp = new SnapshotParameters();//Est utilise pour les parametres de l'image a dessine
			    sp.setFill(Color.TRANSPARENT);//Prend en compte la transparence
			    sp.setTransform(Transform.scale(2, 2));//Defini la "nettete" de l'image
			    
				WritableImage snapshot=tmp.snapshot(sp, null);//
				ImageView hex_img=new ImageView(snapshot);//Prend la snapshot pour l'avoir en tant qu'image
				//Rectangle 
				Rectangle r=new Rectangle(width,height);//Rectangle de fond sur lequel sera dessine l'HexagonalGrid

				r.setFill(Color.WHITE);//Couleur que l'on selectionne
				r.setStrokeWidth(3);//Taille en longueur de la bordure du Rectangle
				r.setStroke(bordures_color);//Couleur de la bordure

				//Canvas IMG du plateau (HexagonalGrid)
				Canvas img_plateau=new Canvas(width-r.getStrokeWidth()*2-et*2,height-r.getStrokeWidth()*2-et*2);//Taille du canvas
				img_plateau.setLayoutX(et+r.getStrokeWidth());//Position en x du canvas
				img_plateau.setLayoutY(et/2+r.getStrokeWidth());//position en y du canvas
				
				GraphicsContext g=img_plateau.getGraphicsContext2D();//Permettra de dessiner sur le canvas
				g.drawImage(hex_img.getImage(), 0, 0,img_plateau.getWidth(),img_plateau.getHeight());//Dessin de l'image de l'HexagonalGrid
				
				//Canvas leger fond noir semi transparent
				double portion=8;//Taille divise par la portion pour savoir quel part le canvas c2 prendra
				Canvas c2=new Canvas(width,(height)/portion);//Canvas c2, bande noir
				c2.setLayoutX(0);//Set la position en X relative a son parent
				c2.setLayoutY(((portion-1)*height)/portion);//Set la position en y
				
				g=c2.getGraphicsContext2D();
				g.setFill(Color.BLACK);//Couleur selectionne
				g.setGlobalAlpha(0.2);//Opacite (Transparence 0 == transparent et 1 == visible a 100%)
				g.fillRect(0, 0, c2.getWidth(), c2.getHeight());//On "colorie" dans le canvas x,y,width,height
				//Canvas
				Canvas c3=new Canvas(width,(height)/portion);//Canvas c3 affichera le nom de la carte
				c3.setLayoutX(0);//Position en x
				c3.setLayoutY(((portion-1)*height)/portion);//Position en y
				
				g=c3.getGraphicsContext2D();
				Font font=new Font(44);//Police du texte
				Text x=new Text(tmp.getName());//Panel Text permettant ensuite de determiner la taille du texte en longueur et en hauteur
				x.setFont(font);//On applique la police

				g.setFill(Color.BLACK);//Couleur selectionne
				g.setGlobalAlpha(0.7);//Opacite (Transparence)
				g.setFont(font);//On applique encore la police
				g.fillText(tmp.getName(), c3.getWidth()/2-(x.getBoundsInLocal().getWidth()/2), c3.getHeight()/2+x.getBoundsInLocal().getHeight()/4);//On dessine le texte au centre

				getChildren().addAll(r,img_plateau,c2,c3);//On ajoute chaque enfants dans le Panel principal (Map_List)
				if(page_map!=page)this.setVisible(false);//Seul le Panel correspondant a la page est visible au depart
			}
		}//Fin classe interne Map_List	
		//Debut classe interne Start
		public class Start extends Pane {
			Start(double width,double height){
				setWidth(width);
				setHeight(height);
				//Rectangle
				Rectangle r=new Rectangle(width,height);
				r.setFill(Color.WHITE);//Couleur selectionne du Fill
				//Bordures
				r.setStroke(bordures_color);//Couleur selectionne de la bordure
				r.setStrokeWidth(2);//Longueur de la bordure
				
				//Canvas
				Canvas c=new Canvas(width,height);
				GraphicsContext g=c.getGraphicsContext2D();
				g.setStroke(bordures_color);
				//g.strokeRect(0, 0, width, height);
				
				//Texte et police du Canvas
				Font font=new Font(30);
				Text t=new Text("Play");
				t.setFont(font);
				
				g.setFont(font);
				g.strokeText(t.getText(), width/2-t.getBoundsInLocal().getWidth()/2, height/2+t.getBoundsInLocal().getHeight()/4);
				//
				this.setOnMouseEntered(e->{
					r.setFill(((Color)r.getFill()).darker());
				});
				this.setOnMouseExited(e->{
					r.setFill(((Color)r.getFill()).brighter());
				});
				//Action du bouton start
				this.setOnMouseClicked(e->{
					selection_map.start();
				});
				getChildren().addAll(r,c);
			}
		}//Fin classe interne Start
		
		public void start() {
			lvl_selected=lvl_list.get(page);//HexagonalGrid selectionne
			for(Menobserver tmp: obs)tmp.start(lvl_selected,p1,p2);
			mode.setVisible(false);
			this.setVisible(false);
		}
		public void back() {
			for(Menobserver tmp: obs)tmp.back();
		}
	}//Fin classe interne Selection_map
	//Debut classe Pre_Editor
	public class Pre_Editor extends Pane {
		private TextField x1;//Zone de texte pour la valeur x (longueur du plateau)
		private TextField y1;//Zone de texte pour la valeur en y (hauteur du plateau)
		private Button accepter,annuler;
		private Label erreur;//Message d'erreur
		//Timeline - duration du message d'erreur
		final Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2),
				new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				erreur.setText("");
			}
		}));
		//Donnes min et max du plateau
		public Pre_Editor(double width,double height) {
			setWidth(width);
			setHeight(height);
			//Rectangle de fond principal et semi-transparent
			Rectangle r=new Rectangle(width,height);
			r.setFill(Color.WHITE);
			r.setOpacity(0.3);
			//Second rectangle ou se situeront les components
			Rectangle r2=new Rectangle(width/4,height);
			r2.setHeight(r2.getHeight()+r2.getStrokeWidth());
			r2.setLayoutX(3*getWidth()/4+r2.getStrokeWidth());
			//
			r2.setOpacity(0.9);
			r2.setStroke(Color.RED);
			//VBox 
			VBox vbox=new VBox(5);
			vbox.setTranslateX(r2.getLayoutX());
			vbox.setTranslateY(getHeight()/2);
			//HBox
			HBox h=new HBox();//Pour le label x et son textField
			HBox h2=new HBox();//Pour le label y et son textField
			HBox h3=new HBox();//Pour les boutons valider et annuler
			//Label
			Label text=new Label("Taille du plateau avec 6<=y<=15 et y<x<=25");
			text.setWrapText(true);
			text.setTextFill(Color.WHITE);

			Label x=new Label("x :");
			Label y=new Label("y :");
			x.setTextFill(Color.WHITE);
			y.setTextFill(Color.WHITE);
			
			//TextField
			x1=new TextField();//N'accepte que des valeurs numeriques
			y1=new TextField();//N'accepte que des valeurs numeriques
			x1.textProperty().addListener(new ChangeListener<String>() {//On accepte que les entiers (chez x)
			    @Override
			    public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
			        if (!newValue.matches("\\d*"))x1.setText(newValue.replaceAll("[^\\d]", ""));
			    }
			});
			y1.textProperty().addListener(new ChangeListener<String>() {//On accepte  que les entiers (chez y)
			    @Override
			    public void changed(ObservableValue<? extends String> observable, String oldValue,String newValue) {
			        if (!newValue.matches("\\d*"))y1.setText(newValue.replaceAll("[^\\d]", ""));
			    }
			});
			//Label erreur
			erreur=new Label("");
			erreur.setTextFill(Color.RED);
			//Button
			accepter=new Button("Valider");//Bouton pour valider 
			annuler=new Button("Annuler");//Bouton pour annuler la requete
			accepter.setTextFill(Color.WHITE);
			accepter.setStyle("-fx-border-color:DARKGOLDENROD;");
			accepter.setStyle("-fx-background-color: green;");//Background en vert
			accepter.setOnMouseEntered(e->{
				accepter.setStyle("-fx-background-color: lightgreen;");//Background en vert
			});
			accepter.setOnMouseExited(e->{
				accepter.setStyle("-fx-background-color: green;");//Background en vert
			});
			accepter.setOnMouseClicked(e->{
				int x_bis=-1;
				int y_bis=-1;
				if(x1.getText().length()>0 && y1.getText().length()>0) {
					x_bis=Integer.parseInt(x1.getText());
					y_bis=Integer.parseInt(y1.getText());
				}
				if(6<=y_bis && y_bis<=15 && y_bis<x_bis && x_bis<=25) {
					for(Menobserver tmp: obs)tmp.back();
					Editeur();
				} else {
					erreur.setText("x ou y incorrect");
					timeline.playFrom(Duration.ZERO);
				}
			});
			annuler.setTextFill(Color.WHITE);
			annuler.setStyle("-fx-border-color:DARKGOLDENROD;");
			annuler.setStyle("-fx-background-color: red;");//Background en rouge
			annuler.setOnMouseEntered(e->{
				annuler.setStyle("-fx-background-color: lightcoral;");//Background en rouge
			});
			annuler.setOnMouseExited(e->{
				annuler.setStyle("-fx-background-color: red;");//Background en rouge
			});
			annuler.setOnMouseClicked(e->{
				x1.setText("");
				y1.setText("");
				for(Menobserver tmp: obs)tmp.back();
			});
			//Alignement
			h.setAlignment(Pos.CENTER);
			h2.setAlignment(Pos.CENTER);
			h3.setAlignment(Pos.CENTER);
			vbox.setAlignment(Pos.BASELINE_CENTER);
			//Ajouts des Children
			h.getChildren().addAll(x,x1);
			h2.getChildren().addAll(y,y1);
			h3.getChildren().addAll(accepter,annuler);

			vbox.getChildren().addAll(text,h,h2,erreur,h3);
			getChildren().addAll(r,r2,vbox);
		}
	}//Fin classe interne Pre_Editor
	
	//Methode d'instanciation des differents panel lors d'un clique sur le bouton correspondant
	private void setNewVue() {	
		for(Menobserver a: obs)a.init_menu();
	}
	private void pre_Editeur() {//Affichage d'un panel demandant a l'utilisateur d'entre des valeurs de x et y
	
		pre_editeur=new Pre_Editor(getWidth(),getHeight());
		TranslateTransition trans = new TranslateTransition(Duration.seconds(0.5),pre_editeur);
		trans.setFromX(getWidth());
		trans.setToX(0);
		trans.play();
		getChildren().add(pre_editeur);
	}
	private void Editeur() {
		for(Menobserver a:obs)a.init_editeur(Integer.parseInt(pre_editeur.x1.getText()),Integer.parseInt(pre_editeur.y1.getText()));
	}
	private void Encyclopedie() {/////
		for(Menobserver a:obs)a.init_encyclopedie();
	}	
	public void start_selection() {
		accueil.setVisible(false);
		if(mode==null) {
			mode = new Selection_Mode();
			getChildren().add(mode);
		}
		mode.setVisible(true);
	}
	public void start_editeur(int x,int y) {
		accueil.setVisible(false);
		if(editeur==null) {
			editeur=new Map_Editor(this,x,y);
			getChildren().add(editeur);
		} else {
			editeur.setDim(x, y);
		}
		editeur.setVisible(true);
	}
	public void start_encyclopedie() {
		accueil.setVisible(false);
		
		if(encyclopedie==null) {
			encyclopedie=new Encyclopedie(this,getWidth(),getHeight());
			getChildren().add(encyclopedie);
		}
		encyclopedie.setVisible(true);
	}
	public void back() {//On revient a l'accueil
		if(selection_map!=null)selection_map.setVisible(false);
		if(editeur!=null)editeur.setVisible(false);
		if(encyclopedie!=null)encyclopedie.setVisible(false);
		if(pre_editeur!=null) {
			TranslateTransition trans = new TranslateTransition(Duration.seconds(0.35),pre_editeur);
			trans.setFromX(pre_editeur.getTranslateX());
			trans.setToX(getWidth());
			trans.play();
		}
		fond_principal.setFill(Color.BLACK);
		fond_principal.setVisible(true);
        accueil.setVisible(true);
	}
	public void backAbandon() {
		mode.setVisible(true);
	}
	
	public void start(Vue vue) {//Debut d'une partie
		fond_principal.setFill(fond2);
		getChildren().add(vue);
	}
	public void save_edit(String nom,String path,int money,HexagonalGrid grid) {
		for(Menobserver a: obs) {
			a.cree_plateau(nom, path, money, grid);
		}
	}
	public void addObserver(Menobserver observer) {
		this.obs.add(observer);
	}
	
public Pane fin(boolean b,Plateau p) {
		Rectangle rect = new Rectangle(550,600);
    	Pane pane = new Pane();
    	Text j1 = new Text("VICTOIRE : JOUEUR 1");
    	/*Bloom bl = new Bloom();
    	bl.setThreshold(0.9);
        */

    	if(b) {
    		j1.setText("VICTOIRE : JOUEUR 2");
    	}
    	j1.setFill(Color.ANTIQUEWHITE);
    	j1.setFont(new Font(50));
    	//j1.setEffect(bl);
    	
    	
    	Rectangle r=new Rectangle(((Vue)p.getVue()).getWidth(),((Vue)p.getVue()).getHeight());
      try {
			Image image=new Image(new File("src/Ressources/Jeu/table.jpg").toURI().toURL().toString());
			ImagePattern image2=new ImagePattern(image,0,0,fond_principal.getWidth(),fond_principal.getHeight(),false);
			r.setFill(image2);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      rect.setStroke(Color.DARKGOLDENROD);
		rect.setStrokeWidth(10);
		Image image=null;
		try {
			image=new Image(new File("src/Ressources/Jeu/Paper.jpg").toURI().toURL().toString());
			ImagePattern image2=new ImagePattern(image);
			rect.setFill(image2);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rect.setLayoutX(Menu_J.this.getWidth()/4);
		rect.setLayoutY(50);
		 Mode rvj = new Mode(50,200,"           Accueil",(Menu_J.this.getHeight()/4) *1.5);
		 Mode jvj = new Mode(50,200,"              Fin",(Menu_J.this.getHeight()/4) *2.0);     
      pane.getChildren().add(r);  
      pane.getChildren().add(rect);
     
      jvj.setOnMouseClicked(new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
              System.exit(0);
          }
      });
      rvj.setOnMouseClicked(new EventHandler<MouseEvent>() {
          @Override
          public void handle(MouseEvent event) {
        	((Vue) p.getVue()).setVisible(false);
        	back();
          }
      });
     pane.getChildren().add(jvj);
     pane.getChildren().add(rvj);
     Canvas c=new Canvas(300,75);
		GraphicsContext g=c.getGraphicsContext2D();
		g.setStroke(Color.DARKGOLDENROD);				
		//Texte et police du Canvas
		Font font=new Font(30);
		j1.setFont(font);
		g.setFont(font);
		g.strokeText(j1.getText(),5, 50);
		pane.getChildren().add(c);
		c.setLayoutX(400);
		c.setLayoutY(100);
      return pane;
	}
public class Mode extends Pane{
	private Rectangle sous_rect = new Rectangle(200,50);		
	Mode(double height, double width, String t, double layY){
		//Couleur selectionne du Fill
		//Bordures
		Image image=null;
		try {
			image=new Image(new File("src/Ressources/Jeu/Paper.jpg").toURI().toURL().toString());
			ImagePattern m=new ImagePattern(image);
			sous_rect.setFill(m);//Couleur selectionne du Fill
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sous_rect.setStroke(Color.DARKGOLDENROD);//Couleur selectionne de la bordure
		sous_rect.setStrokeWidth(2);//Longueur de la bordure
		//Canvas
		Canvas c=new Canvas(width,height);
		GraphicsContext g=c.getGraphicsContext2D();
		g.setStroke(Color.rgb(52, 73, 94));				
		//Texte et police du Canvas
		Font font=new Font(20);
		Text te=new Text(t);
		te.setFont(font);
		g.setFont(font);
		g.strokeText(te.getText(),width/22, height/1.5);
		
		this.setLayoutX(Menu_J.this.getWidth()/2 - sous_rect.getWidth()/2);
		this.setLayoutY(layY);
		getChildren().addAll(sous_rect);

		getChildren().add(c);
	}
}

public void setDataUnite(String name, String[] stats, Fx_Skill[] skills_name) {
	for(Menobserver tmp: obs)tmp.setDataUnite(name,stats,skills_name);
}
public void setDataTerrain(String name, String[] descriptions) {
	for(Menobserver tmp: obs)tmp.setDataTerrain(name,descriptions);
}
public void setDataStatut(String name, String[] descriptions) {
	for(Menobserver tmp: obs)tmp.setDataStatut(name,descriptions);	
}
}

