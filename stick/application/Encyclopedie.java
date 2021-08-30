package application;

import java.io.File;
import java.net.MalformedURLException;


import application.Fx_Unite.Fx_Skill;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Encyclopedie extends Pane {
	private Menu_J menu_parent;//Parent
	public static String PATH="src/Ressources/Encyclopedie";
	private Color fond_principal=Color.rgb(244, 244,244);//Couleur principal
	private Color bordures=Color.DARKGOLDENROD;//Couleur des bordures
	
	//private Rectangle fond;//Rectangle de fond (de couleur)
	private Head head;//Titre
	private Page units;//Page des unites
	private Page fields;//Page des terrains
	private Page status;//Page des statuts
	
	private Retour retour;//Bouton retour
	//
	Card[] card_unite_list;
	Card[] card_field_list;
	Card[] card_status_list;
	//Donnees
	private String[] units_names= {//Noms des unites a instancier
		"Guerrier","Archer",
		"Chasseur","Druide",
		"Batisseur","Fee",
		"Faucheuse","Malade",
		"Pretresse","ChronoMage",
		"BomberMan","Blob","Conteur",
		"Assassin"
	};
	private String[] field_names = {//Nom des terrains a instancier
		"Buisson","Campement",
		"Goudron","Poison",
		"Trap"
	};
	private String[] status_names = {
			"Brulure","Desarmer","Empoisonnement",
			"Encenser","Fatalite","Hemoragie",
			"Insensibiliter","Renforcement","Root",
			"Silence","Stun","Virus"
	};
	Encyclopedie(Menu_J menu_j,double width,double height) {
		this.menu_parent=menu_j;
		setWidth(width);
		setHeight(height);
		
		//Instanciation
		//Rectangle
		//fond=new Rectangle(width,height);
		ImageView image=null;
        try {
            image=new ImageView(new File("src/Ressources/Jeu/Papier.jgp").toURI().toURL().toString());
            image.setFitHeight(this.getHeight());
            image.setFitWidth(this.getWidth());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        getChildren().add(image);
		head=new Head(getWidth(),getHeight()/9);
		//Liste des "cartes"
		card_unite_list=new Card[units_names.length];
		card_field_list=new Card[field_names.length];
		card_status_list=new Card[status_names.length];
		
		for(int i=0;i<card_unite_list.length;i++)card_unite_list[i]=new Card(new Card_Unite(units_names[i]),getWidth()/4.4,getHeight()/2);
		for(int i=0;i<card_field_list.length;i++)card_field_list[i]=new Card(new Card_Terrain(field_names[i]),getWidth()/4.4*1.5,getHeight()/4);
		for(int i=0;i<card_status_list.length;i++)card_status_list[i]=new Card(new Card_Statut(status_names[i]),getWidth()/4.4*1.5,getHeight()/4);

		//Page contenant les listes de "cartes" correspondant aux unites/terrains
		units = new Page(3,card_unite_list);
		fields = new Page(2,card_field_list);
		status = new Page(2,card_status_list);
		//Retour
		retour=new Retour(50,50);
		retour.setLayoutX(getWidth()-retour.getWidth()-5);
		retour.setLayoutY(5);
		
		TabPane parent = new TabPane();//Possedera la liste des objets a poser sur le plateau
		Tab unites = new Tab();//Unites
		Tab terrains = new Tab();//Terrains
		Tab statuts = new Tab();//Statuts
		//Affichage,format et position
		//Rectangle
		//fond.setFill(fond_principal);
		
		//TabPane
		parent.setStyle("-fx-border-color:DARKGOLDENROD;");
		parent.setLayoutX(width/2-card_unite_list[0].getWidth()*3/2);
		parent.setLayoutY(head.getHeight());
		parent.setPrefSize((card_unite_list[0].getWidth())*3, getHeight()-getHeight()/9);

		//Attributions des valeurs aux differents Tab
		//Unites
		unites.setText("Unites");
		unites.setContent(units);//Scroll des "cartes" unites
		//Terrains
		terrains.setText("Terrains");
		terrains.setContent(fields);//Scroll des "cartes" terrain
		//Terrains
		statuts.setText("Statuts");
		statuts.setContent(status);
		//Ajout dans le Parent
		parent.getTabs().addAll(unites,terrains,statuts);
		parent.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		getChildren().addAll(/*fond,*/head,parent,retour);
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
			Text t=new Text("Encyclopedie");
			t.setFont(font);
			
			g.setFont(font);
			g.setStroke(Color.BLACK);
			g.strokeText(t.getText(), width/2-t.getBoundsInLocal().getWidth()/2,t.getBoundsInLocal().getHeight());
			Polygon p=new Polygon();
			double h=getHeight()-(getHeight()-t.getBoundsInLocal().getHeight())/2;
			double w=width/20;
			//p.setOpacity(0.5);
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
	private class Page extends ScrollPane {//Panel de scroll qui contiendra toutes les cartes d'une categorie (unites ou terrains)
		private VBox vbox_p=new VBox(1);
		
		Page(double node_per_row,Card ...card){
			if(node_per_row<=0)return;
			int number=(int) Math.ceil(card.length/node_per_row);

			for(int i=0;i<number;i++) {
				HBox t=new HBox(0.5);
				for(int j=0;j<node_per_row;j++)if(card.length>j+(i*node_per_row))t.getChildren().add(card[(int) (j+(i*node_per_row))]);
				vbox_p.getChildren().add(t);
			}
			setContent(vbox_p);
			this.setPrefHeight(Encyclopedie.this.getHeight()-Encyclopedie.this.getHeight()/9);
			this.setPrefWidth(card[0].getWidth()*node_per_row+5*node_per_row);
			this.setStyle("-fx-focus-color: rgba(0, 100, 100, 0.2);"+" -fx-faint-focus-color: #ff000022;");//Couleur du ScrollPane quand il est "focused"
		}
	}
	private interface Flip {
		Node front(double width,double height);
		Node back(double width,double height);
	}
	private class Card extends Pane {//Carte generique pouvant contenir des classes de type Flip (qui ont des donnees graphique cote Recto et Verso
		private BooleanProperty showFront;
		private ParallelTransition rot;
		//
		private Node front;
		private Node back;
		
		private Rectangle rectangle;
		private Arc[] arcs = new Arc[4];
		Card(Flip flip,double width,double height){
			front=flip.front(width,height);
			back=flip.back(width,height);
			
			setWidth(width);
			setHeight(height);
			initialisation();
			add_children();
		}
		private void initialisation() {
	        showFront = new SimpleBooleanProperty(true);
			rot=createRotator(this,showFront);//Important pour permettre l'animation des "cartes"
			//Rectangle
			rectangle = new Rectangle (getWidth(),getHeight());
			//Arc test
			for(int i=0;i<arcs.length;i++)arcs[i]=new Arc();
			arcs[0].setCenterX(getWidth()/2);
			arcs[0].setCenterY(getWidth()/2);
			arcs[0].setRadiusX(90);
			arcs[0].setRadiusY(90);
			arcs[0].setStartAngle(45);
			arcs[0].setLength(getWidth()/4.5);
			arcs[0].setType(ArcType.OPEN);

			//Event
			this.setOnMouseClicked(e->{
				rot.play();
			});
			affichage();
		}
		private void affichage() {
			rectangle.setFill(fond_principal);
			rectangle.setStroke(bordures);
			rectangle.setStrokeWidth(1);
		}
		private void add_children() {
			getChildren().add(rectangle);
			getChildren().add(front);
		}
		
	}
	//Debut classe interne Card_Unite, implemente Flip pour l'affichage des node (panels/GUI) de la face recto et verso d'une carte
	private class Card_Unite implements Flip{
		private String name;
		private String UNITE_IMAGE;
		private Image image;
		//Front
		Pane front=new Pane();
		//
		private ImageView view;//Image 
		private StackPane stack_name;//Contiendra un rectangle en fond et le nom de l'unite
		private Rectangle r_fond;//Rectangle en fond
		private Label text_name;//Nom de l'unite (version graphique)
		//Back
		Pane back=new Pane();
			//Conteneurs
		private double espY=10;
		private VBox parent=new VBox(espY);
		private HBox hbox_principal;//Contiendra l'ensemble des stats avec leurs images
		private VBox vbox1,vbox2;//Contiendront les HBox (3 chacun)
		private HBox[] hboxs = new HBox[6];//Contiendra l'image du stat concerne, et sa valeur (epee,coeur... - attaque,defense,portee...)
			//
		private ImageView[] stats_image = new ImageView[6];//Image correspondat a une statistique de l'unite
		private Label[] stats_label=new Label[6];//Texte version graphique des stats de l'unite
		private StackPane[] stack_skills=new StackPane[4];//Contiendra un rectangle et l'image du skill
		private TextArea skill_description=new TextArea();//Contiendra la description de la competence
			//Donnes brutes
		private String[] stats = new String[6];//Valeur numerique de la statistique de l'unite (attaque,def,...)
		private Fx_Skill[] skills_name = new Fx_Skill[4];//Nom du Skill
		private String[] image_names = {
				"Coeur","Epee",
				"Bouclier","BouclierMagique",
				"Boots","Oeil"
		};
		private double espaceY=0;
		private double width=0;
		Card_Unite(String name){
			this.name=name;
			UNITE_IMAGE=name+".png";
			try {
				image=new Image(new File(Fx_Unite.PATH+"/"+UNITE_IMAGE).toURI().toURL().toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			menu_parent.setDataUnite(name, stats , skills_name);
		}
		//Initialisation des nodes du cote pile (face recto)
		private void initialisation(double width,double height) {
			view=new ImageView(image);
			view.setFitWidth(width);
			view.setFitHeight(height);
			//StackPane
			stack_name=new StackPane();
			stack_name.setLayoutY(height-height/7);
			//Rectangle
			r_fond=new Rectangle(width,height/7);
			//Label
			text_name=new Label(name);
			text_name.setFont(new Font(22));
			
			StackPane.setAlignment(text_name, Pos.CENTER);
			//
			r_fond.setOpacity(0.4);

			stack_name.getChildren().addAll(r_fond,text_name);
		}
		//Initialisation des nodes du cote face (face verso)
		private void initialisation2(double width,double height) {
			this.width=width;

			parent.setAlignment(Pos.CENTER);
			Label label=new Label("Statistiques");
			label.setFont(new Font(20));

			hbox_principal=new HBox(height/10);
			hbox_principal.setAlignment(Pos.CENTER);
			for(int i=0;i<hboxs.length;i++)hboxs[i]=new HBox(5);//Contiendra l'image et la valeur d'une statistique
			vbox1=new VBox(10,hboxs[0],hboxs[2],hboxs[4]);//Affichage vertical des donnees (de la HBox ci dessus)
			vbox2=new VBox(10,hboxs[1],hboxs[3],hboxs[5]);//Affichage vertical des donnees (de la HBox ci dessus)
			for(int i=0;i<stats_image.length;i++) {
				Image image=null; //Ici on va instancier les images associes aux statistiques (coeur,epee,bouclier etc...)
				try {
					image = new Image(new File(Encyclopedie.PATH+"/"+image_names[i]+".png").toURI().toURL().toString());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				//ImageView
				stats_image[i]=new ImageView(image);
				stats_image[i].setFitWidth(height/9);//Taille attribue en longueur
				stats_image[i].setFitHeight(height/9);//Taille attribue en hauteur
				//Label
				stats_label[i]=new Label(stats[i]);//Affichage graphique de la donne de la statistique
				stats_label[i].setFont(new Font(20));//Taille de la police
				stats_label[i].setStyle("-fx-font-weight: bold");//Style , "gras"
				stats_label[i].setPrefSize(height/9, height/9);//Taille preferentiel
				//add en childrens
				hboxs[i].getChildren().addAll(stats_image[i],stats_label[i]);//Contient l'image + l'affichage graphique de stat concerne
			}
			Label label2=new Label("Skills");
			label2.setFont(new Font(20));
			HBox competences=new HBox(height/10);
			competences.setAlignment(Pos.CENTER);
			
			for(int i=0;i<skills_name.length;i++)if(skills_name[i]!=null) {
				final int x=i;

				Rectangle r=new Rectangle(height/7,height/7);
				r.setFill(Color.TRANSPARENT);
				r.setStrokeWidth(3);
				r.setStroke(bordures.darker());
				ImageView tmp=new ImageView(skills_name[i].getImage());
				tmp.setFitHeight(height/7);
				tmp.setFitWidth(height/7);
				stack_skills[i]=new StackPane(r,tmp);
				stack_skills[i].setOnMouseEntered(e->{
					setDescription(skills_name[x].getskillName(),"",skills_name[x].description());
				});
				stack_skills[i].setOnMouseExited(e->{
					revertDescription();
				});
				competences.getChildren().add(stack_skills[i]);
			}
			//
			Line line=new Line(0,0,width,0);
			line.setVisible(false);
			Line line2=new Line(0,0,width,0);
			line2.setVisible(false);
			//
			skill_description.setWrapText(true);
			skill_description.setEditable(false);
			espaceY+=stats_image[0].getFitHeight()*3+height/10;
			skill_description.setPrefSize(width,espaceY);
			skill_description.setStyle("-fx-background-color: transparent;");
			skill_description.setLayoutY(stats_image[0].getFitHeight());
			skill_description.setOpacity(0);
			hbox_principal.getChildren().addAll(vbox1,vbox2);
			parent.getChildren().addAll(line,label,hbox_principal,line2,label2,competences);
			back.getChildren().add(skill_description);
		}
		private void onFrontOver() {//Animation qui se deroule lorsque l'on passe sa souris sur le carte face recto (front)
	    	FillTransition fill=new FillTransition(Duration.millis(200),r_fond,Color.BLACK,bordures);
	    	TranslateTransition trans=new TranslateTransition(Duration.millis(150),r_fond);
	    	TranslateTransition trans1=new TranslateTransition(Duration.millis(150),text_name);
	    	trans.setToY(-r_fond.getHeight()/3);
	    	trans1.setToY(-r_fond.getHeight()/3);
	    	ScaleTransition scale=new ScaleTransition(Duration.millis(150),r_fond);//Animation qui change la taille du r_fond 
	    	scale.setToY(1.5);//L'aggrandie de 1,5 fois sa taille
	    	//Animation "inverse" lorsque l'utilisateur sortira de la "carte" 
			FillTransition fill2=new FillTransition(Duration.millis(200),r_fond,bordures,Color.BLACK);//Transition de changement de couleur progressive 
	    	TranslateTransition trans2=new TranslateTransition(Duration.millis(150),r_fond);//Animation de transition d'un point a au b (animation de deplacement)
	    	TranslateTransition trans3=new TranslateTransition(Duration.millis(150),text_name);//Animation de transition d'un point a au b (animation de deplacement)
	    	trans2.setToY(0);
	    	trans3.setToY(0);
	    	ScaleTransition scale2=new ScaleTransition(Duration.millis(150),r_fond);
	    	scale2.setToY(1);
	    	ParallelTransition animation=new ParallelTransition(front,fill,trans,trans1,scale);
	    	ParallelTransition animation_revert=new ParallelTransition(front,fill2,trans2,trans3,scale2);

	    	front.setOnMouseEntered(e->{
				animation.play();
			});
	    	front.setOnMouseExited(e->{
				animation_revert.play();
			});
		}
        private void setDescription(String title,String sub_text,String description) {//Animation pour l'affichage du texte descriptif 
        	skill_description.setText(title+"\n\n"+description);
	    	TranslateTransition transition_out=new TranslateTransition(Duration.millis(150),hbox_principal);//Animation de deplacement utilise translateX,translateY
	    	transition_out.setToX(width);//Pour faire sortir le panel descriptif des stats
	    	TranslateTransition transition_in=new TranslateTransition(Duration.millis(150),skill_description);//
	    	transition_in.setToX(0);//Amenent aux coordonnes 0 (pour ramener le panel descriptif des competences)
	    	//Fade transition (transparence)
	    	FadeTransition mi_temps=new FadeTransition(transition_out.getTotalDuration().multiply(0.75),hbox_principal);//Animation qui joue sur l'opacite/transparence
	    	mi_temps.setToValue(0);//Rend progressivement invisible le panel des stats
	    	FadeTransition mi_temps2=new FadeTransition(transition_in.getTotalDuration().multiply(0.75),skill_description);
	    	mi_temps2.setToValue(1);//Rend progressivement visible le panel descriptif des competences
	    	//ParallelTransition , pour jouer toutes les animations en meme temps
	    	ParallelTransition animation=new ParallelTransition(transition_out,transition_in,mi_temps,mi_temps2);//Utilise pour "actionner" toutes les animations en meme temps
	    	animation.play();//On joue la transition
        }
        private void revertDescription() {//Animation pour reafficher les stats
	    	TranslateTransition transition_in=new TranslateTransition(Duration.millis(150),hbox_principal);//Animation de deplacement utilise translateX,translateY
	    	transition_in.setToX(parent.getLayoutX());//L'amene a la position relatif en X de son parent (probablement 0)
	    	TranslateTransition transition_out=new TranslateTransition(Duration.millis(150),skill_description);//
	    	transition_out.setToX(getWidth());//Met le panel descriptif des competences en dehors 
	    	FadeTransition mi_temps=new FadeTransition(transition_in.getTotalDuration().multiply(0.75),hbox_principal);//Opacite (Temps de duration,Panel)
	    	mi_temps.setToValue(1);
	    	FadeTransition mi_temps2=new FadeTransition(transition_out.getTotalDuration().multiply(0.75),skill_description);//Opacite (Temps de duration,Panel)
	    	mi_temps2.setToValue(0);
	    	ParallelTransition animation_revert=new ParallelTransition(transition_in,transition_out,mi_temps,mi_temps2);//Transition actives de manieres "synchrones"
	    	animation_revert.play();//On joue la transition
        }
		public Node front(double width,double height) {
			initialisation(width,height);
			onFrontOver();
			//
			front.getChildren().addAll(view,stack_name);		
			return front;
		}
		public Node back(double width,double height) {
			back.setPrefSize(width, height);
			back.getChildren().add(parent);
			initialisation2(width,height);

			return back;
		}
	}//Fin Card_Unite
	
	//Debut class Card_Terrain
	private class Card_Terrain implements Flip {
		private String name;
		private String TERRAIN_IMAGE;
		private Image image;
		//Front
		private Pane front=new Pane();
		//
		private ImageView view;//Image 
		private StackPane stack_name;//Contiendra un rectangle en fond et le nom de l'unite
		private Rectangle r_fond;//Rectangle en fond
		private Label text_name;//Nom de l'unite (version graphique)
		//Back
		private Pane back=new Pane();
			//Conteneurs
		private VBox parent=new VBox();
		private Label sous_description_label=new Label();
		private Label description_label=new Label();//Texte version graphique des stats de l'unite
			//Donnes
		private String[] descriptions = new String[2];
		Card_Terrain(String name){
			this.name=name;
			TERRAIN_IMAGE=name+".png";
			try {
				image=new Image(new File(Fx_Terrain.PATH+"/"+TERRAIN_IMAGE).toURI().toURL().toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			menu_parent.setDataTerrain(name, descriptions);
		}
		private void onFrontOver() {//Animation qui se deroule lorsque l'on passe sa souris sur le carte face recto (front)
	    	FillTransition fill=new FillTransition(Duration.millis(200),r_fond,Color.BLACK,Color.CORAL);
	    	TranslateTransition trans=new TranslateTransition(Duration.millis(150),r_fond);
	    	TranslateTransition trans1=new TranslateTransition(Duration.millis(150),text_name);
	    	trans.setToY(-r_fond.getHeight()/3);
	    	trans1.setToY(-r_fond.getHeight()/3);
	    	ScaleTransition scale=new ScaleTransition(Duration.millis(150),r_fond);//Animation qui change la taille du r_fond 
	    	scale.setToY(1.5);//L'aggrandie de 1,5 fois sa taille
	    	//Animation "inverse" lorsque l'utilisateur sortira de la "carte" 
			FillTransition fill2=new FillTransition(Duration.millis(200),r_fond,Color.CORAL,Color.BLACK);//Transition de changement de couleur progressive 
	    	TranslateTransition trans2=new TranslateTransition(Duration.millis(150),r_fond);//Animation de transition d'un point a au b (animation de deplacement)
	    	TranslateTransition trans3=new TranslateTransition(Duration.millis(150),text_name);//Animation de transition d'un point a au b (animation de deplacement)
	    	trans2.setToY(0);
	    	trans3.setToY(0);
	    	ScaleTransition scale2=new ScaleTransition(Duration.millis(150),r_fond);
	    	scale2.setToY(1);
	    	ParallelTransition animation=new ParallelTransition(front,fill,trans,trans1,scale);
	    	ParallelTransition animation_revert=new ParallelTransition(front,fill2,trans2,trans3,scale2);

	    	front.setOnMouseEntered(e->{
				animation.play();
			});
	    	front.setOnMouseExited(e->{
				animation_revert.play();
			});
		}
		@Override
		public Node front(double width,double height) {
			view=new ImageView(image);
			view.setLayoutX(width/2-width/4);
			view.setFitWidth(width/2);
			view.setFitHeight(height-height/4.5);
			//StackPane
			stack_name=new StackPane();
			stack_name.setLayoutY(height-height/4.5);
			//Rectangle
			r_fond=new Rectangle(width,height/4.5);
			//Label
			text_name=new Label(name);
			text_name.setFont(new Font(22));
			
			StackPane.setAlignment(text_name, Pos.CENTER);
			//
			r_fond.setOpacity(0.4);

			stack_name.getChildren().addAll(r_fond,text_name);
			//
			onFrontOver();
			front.getChildren().addAll(view,stack_name);
			return front;
		}
		@Override
		public Node back(double width,double height) {
			parent.setAlignment(Pos.CENTER);
			Label label=new Label("Description");
			label.setFont(new Font(20));
			label.setStyle("-fx-font-weight: bold");//Style , "gras"
			
			description_label.setText(descriptions[1]);
			description_label.setPrefWidth(width);
			description_label.setWrapText(true);
			description_label.setFont(new Font(14));

			parent.getChildren().addAll(label,sous_description_label,description_label);
			back.getChildren().add(parent);
			return back;
		}
	}//Fin classe interne Card_Terrain
	//class interne Card_Statut
	private class Card_Statut implements Flip {
		private String name;
		private String STATUT_IMAGE;
		private Image image;
		//Front
		private Pane front=new Pane();
		//
		private ImageView view;//Image 
		private StackPane stack_name;//Contiendra un rectangle en fond et le nom de l'unite
		private Rectangle r_fond;//Rectangle en fond
		private Label text_name;//Nom de l'unite (version graphique)
		//Back
		private Pane back=new Pane();
			//Conteneurs
		private VBox parent=new VBox();
		private Label sous_description_label=new Label();
		private Label description_label=new Label();//Texte version graphique des stats de l'unite
			//Donnes
		private String[] descriptions = new String[2];
		Card_Statut(String name){
			this.name=name;
			menu_parent.setDataStatut(name, descriptions);

			STATUT_IMAGE=descriptions[0]+".png";
			
			try {
				image=new Image(new File(Fx_Statut.PATH+"/"+STATUT_IMAGE).toURI().toURL().toString());
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		private void onFrontOver() {//Animation qui se deroule lorsque l'on passe sa souris sur le carte face recto (front)
	    	FillTransition fill=new FillTransition(Duration.millis(200),r_fond,Color.BLACK,Color.CORAL);
	    	TranslateTransition trans=new TranslateTransition(Duration.millis(150),r_fond);
	    	TranslateTransition trans1=new TranslateTransition(Duration.millis(150),text_name);
	    	trans.setToY(-r_fond.getHeight()/3);
	    	trans1.setToY(-r_fond.getHeight()/3);
	    	ScaleTransition scale=new ScaleTransition(Duration.millis(150),r_fond);//Animation qui change la taille du r_fond 
	    	scale.setToY(1.5);//L'aggrandie de 1,5 fois sa taille
	    	//Animation "inverse" lorsque l'utilisateur sortira de la "carte" 
			FillTransition fill2=new FillTransition(Duration.millis(200),r_fond,Color.CORAL,Color.BLACK);//Transition de changement de couleur progressive 
	    	TranslateTransition trans2=new TranslateTransition(Duration.millis(150),r_fond);//Animation de transition d'un point a au b (animation de deplacement)
	    	TranslateTransition trans3=new TranslateTransition(Duration.millis(150),text_name);//Animation de transition d'un point a au b (animation de deplacement)
	    	trans2.setToY(0);
	    	trans3.setToY(0);
	    	ScaleTransition scale2=new ScaleTransition(Duration.millis(150),r_fond);
	    	scale2.setToY(1);
	    	ParallelTransition animation=new ParallelTransition(front,fill,trans,trans1,scale);
	    	ParallelTransition animation_revert=new ParallelTransition(front,fill2,trans2,trans3,scale2);

	    	front.setOnMouseEntered(e->{
				animation.play();
			});
	    	front.setOnMouseExited(e->{
				animation_revert.play();
			});
		}
		@Override
		public Node front(double width,double height) {
			view=new ImageView(image);
			view.setLayoutX(width/2-width/4);
			view.setFitWidth(width/2);
			view.setFitHeight(height-height/4.5);
			//StackPane
			stack_name=new StackPane();
			stack_name.setLayoutY(height-height/4.5);
			//Rectangle
			r_fond=new Rectangle(width,height/4.5);
			//Label
			text_name=new Label(name);
			text_name.setFont(new Font(22));
			
			StackPane.setAlignment(text_name, Pos.CENTER);
			//
			r_fond.setOpacity(0.4);

			stack_name.getChildren().addAll(r_fond,text_name);
			//
			onFrontOver();
			front.getChildren().addAll(view,stack_name);
			return front;
		}
		@Override
		public Node back(double width,double height) {
			parent.setAlignment(Pos.CENTER);
			Label label=new Label("Description");
			label.setFont(new Font(20));
			label.setStyle("-fx-font-weight: bold");//Style , "gras"
			
			description_label.setText(descriptions[1]);
			description_label.setPrefWidth(width);
			description_label.setWrapText(true);
			description_label.setFont(new Font(14));

			parent.getChildren().addAll(label,sous_description_label,description_label);
			back.getChildren().add(parent);
			return back;
		}
	}
	//Fin class interne Card_Statut
	//Debut classe interne Retour
	public class Retour extends Pane { //Pas le temps de faire un bouton retour generique (sans le redefinir dans chaque classe concerne)
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
				menu_parent.back();
			});
			getChildren().addAll(r,p);
		}
	}//Fin classe interne Retour
	
	//Methode permettant de creer l'animation des cartes lors d'un clique (pour le mettre en face recto/verso)
    private ParallelTransition createRotator(Card card,BooleanProperty b) {
        RotateTransition rotator = new RotateTransition(Duration.millis(400), card);
        rotator.setAxis(Rotate.Y_AXIS);
    	card.back.setScaleX(-1);

        if(b.get()) {
        	rotator.setFromAngle(0);
        	rotator.setToAngle(180);
        } else {
        	rotator.setFromAngle(180);
        	rotator.setToAngle(360);
        }
        rotator.setOnFinished(e->{
        	if(b.get()) {
        		rotator.setFromAngle(0);
        		rotator.setToAngle(180);
        	} else {	
        		rotator.setFromAngle(180);
            	rotator.setToAngle(360);
            }
        });
        PauseTransition pause=new PauseTransition(rotator.getCycleDuration().multiply(0.5));
        //Transition utilise pour changer le node/la partie graphique de la carte a affiche a mi-chemin de l'animation
        pause.setOnFinished(z->{
        	b.set(!b.get());
        	if(b.get()) {
        		card.getChildren().remove(card.back);
        		card.getChildren().add(card.front);
        	} else {
        		card.getChildren().remove(card.front);
        		card.getChildren().add(card.back);
        	}
        });
    	ParallelTransition animation=new ParallelTransition(card,rotator,pause);

        rotator.setInterpolator(Interpolator.LINEAR);
        rotator.setCycleCount(1);//Nombre de fois que l'animation se produit (facultatif)
        return animation;
    }
}
