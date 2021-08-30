package application;

import java.io.File;
import java.util.LinkedList;


import controleur.Game;
import javafx.scene.Node;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;

import javafx.scene.image.Image;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class Vue extends Pane implements Observable_selected{//GUI Principal
	private static String PATH="src/Ressources/Map";
	private Canvas fond;
	private LinkedList<Observer_selected> observer_selected;
	//
	private VBox root;//Contiendra tous les elements : HexagonalGrid et bloc
	private HBox bloc;//Bloc du bas qui contiendra differents affichages// Comme la boutique, passer Tour etc..
	//
	private LinkedList<Fx_Hexagon> deplac=null;
	private Selection selection_allie;//Interface de l'unite selectionne
	private Selection_Ennemie selection_ennemie;//Interface de l'unite ennemie selectionne
	private Description_Box description;//Panel correspondant a la bulle de texte descriptif d'un skill/statut/autre
	private Description_Attaque description_attaque;//panel pour l'affichage de l'attaque légerement différent de description_box
	private Tour tour;//Bouton tour
	private Abandon abandon;//Bouton abandon

	//Ajout ici :
	private Fx_Unite uniteActuelle = null; //a voir si GUI_Unite
	private Fx_Unite uniteSelectionnee=null;
	private Fx_Hexagon cible=null;
	//
	private int camp=1;//Camp correspondant normalement au joueur actuel
	private int selection_type=-1;//ajout d'un selection type qui indique qu'elle hexagoen sont colorie en rouge
									//-1=rien, 0=dep, 1=attaque, 2=sort1 3=sort2 4=sort4
	//Affichage du texte "Tour du joueur ..."
	private Text joueur;
	private Text PA;//Texte affichant les PA du joueur actuel
	private int int_PA;//PA du joueur actuel
	private boolean rvr=false;//true quand mode robot contre robot
	private LinkedList<Fx_Hexagon.Node> graph;
	private boolean rob1 = false;//quand l'adversaire est un robot, sert à empecher de cliquer sur ses attaques/sorts
	
	public Vue(double Width,double Height){
		setWidth(Width);
		setHeight(Height);
		fond=new Canvas(this.getWidth(),this.getHeight());
		root=new VBox(5);
		bloc=new HBox(5);
		abandon=new Abandon();
		
		observer_selected=new LinkedList<>();
		getChildren().add(fond);
		getChildren().add(abandon);
		getChildren().add(root);
	}
	public LinkedList<Fx_Hexagon> getdeplac(){
		return deplac;
	}
	public void setdeplace(LinkedList<Fx_Hexagon> m) {
		 deplac=m;
	}
	//DEBUT INNER CLASS SELECTION
	public class Selection extends Pane implements Observable {//Panel de l'unite allie selectionne
		private Rectangle r_selected;//Rectangle sur lequel le canvas affichera l'Unite selectionne
		private Rectangle r_stats;//Rectangle sur lequel le canvas affichera les stats de l'unite selectionne
		public Rectangle r_skills[];//Sous rectangle sur lequel le Canvas affichera les Rectangles
		
		private Canvas container;//	1.0/5.0 de l'espace //c1 
		private Canvas stats;//	2.0/5.0 //c2
		private Skill_slots[] skills;//	2.0/5.0 //c3

		private HBox statuts = new HBox();//Contiendra l'affichage des statuts
		
		private double posX;//Positionnement en X pour poser les Rectangle 
		private double bords_haut=20;//Espace en hauteur
		
		private LinkedList<Observer> observers = new LinkedList<>();

		public Selection(double width,double height){
			this.setWidth(width);
			this.setHeight(height);
			
			int nb_comp_max=4;//A peut-etre changer, c'est juste le nombre de slots de skills max
			
			double hauteur=height-bords_haut*2;//Tous les rectangles secondaires auront cette hauteur
			double longueur=getWidth();//Longueur sur laquelle seront les canvas
				
			double longueur_c1=Math.max(longueur/5.0,hauteur/5.0);//Des mathematiques pour la taille du premier canvas
			if(longueur_c1>hauteur)longueur_c1=hauteur;//Au cas ou, toujours pour la taille du canvas
			double longueur_c23 = (longueur*2.0)/5.0;//Longueur des canvas stats et skills
			
			posX=(getWidth()-longueur)/2;
			if(longueur_c1<longueur/5.0)posX+=longueur_c1/10.0;
			
			//Instanciations des canvas
			container=new Canvas(longueur_c1,hauteur);
			stats=new Canvas(longueur_c23,hauteur);
			skills=new Skill_slots[nb_comp_max];//
			//
			double longueur_c3_bis=Math.round(longueur_c23/skills.length);//
			double hauteur_c3_bis=hauteur/2;//Les cases seront situes sur deux lignes
			double cote_c3=Math.max(longueur_c3_bis, hauteur_c3_bis);//Des maths

			double cote_c3_0=cote_c3/2;
			double cote_c3_123=cote_c3+cote_c3_0/skills.length-1;
			for(int i=0;i<skills.length;i++){
				if(i==0) {
					skills[i]=new Skill_slots(cote_c3_0,hauteur,i+1);
				continue;
				}
				skills[i]=new Skill_slots(cote_c3_123,cote_c3_123,i+1);	
			}
			//
			//RECTANGLE
			Rectangle r=new Rectangle(width,height);//Contenant principal
			
			r_selected=new Rectangle(container.getWidth(),container.getHeight());//c1 //Contiendra l'aaffichage du Fx_Unite
			r_stats=new Rectangle(stats.getWidth(),stats.getHeight());//c2 //Contiendra l'affichage des stats
			r_skills=new Rectangle[nb_comp_max];//c3 //Contiendra les differents slots de skills
			for(int i=0;i<r_skills.length;i++){
				r_skills[i]=new Rectangle(skills[i].getWidth(),skills[i].getHeight());	
			}
			
			setPrincipalRect(r);
			set_Rect(r_selected);
			set_Rect(r_stats);
			for(int i=0;i<r_skills.length;i++) {
			if(i%2==0)set_Rect_Skills(r_skills[i],false);
			else set_Rect_Skills(r_skills[i],true);
			}
			
			//Ajouts			
			getChildren().addAll(r,r_selected,r_stats);//Ajout des Rectangles
			getChildren().addAll(r_skills);
			getChildren().add(statuts);
		}

		public void setPrincipalRect(Rectangle r) {//Rectangle en background 
			//Cotes arrondies
			r.setArcHeight(10);
			r.setArcWidth(10);
			//Remplissage
			r.setFill(Color.ANTIQUEWHITE);
			//Bordures
			r.setStrokeWidth(1);
			r.setStroke(Color.DARKGREEN);
		}
		public void set_Rect(Rectangle r) {//Set le rectangle (Couleur,Bordure et leger changement de position)
			r.setWidth(r.getWidth()-5);
			r.setLayoutX(posX+5);
			r.setLayoutY(bords_haut);
			//Cotes arrondies
			r.setArcHeight(5);
			r.setArcWidth(5);
			//Remplissage
			r.setFill(Color.WHITE);
			//Bordures
			r.setStrokeWidth(1);
			r.setStroke(Color.BLACK);
			//
			posX+=r.getWidth()+5;
		}
		private void set_Rect_Skills(Rectangle r, boolean b) {//Set les positions/affichages des Rectangles associes aux skills
			r.setWidth(r.getWidth()-5);
			r.setHeight(r.getHeight()-0.5);
			r.setLayoutX(posX+5);
			r.setLayoutY(bords_haut);
			//if(b)r.setLayoutY(bords_haut+r.getHeight()+1);
			r.setLayoutY(getHeight()/2-r.getHeight()/2);
			//Remplissage
			r.setFill(Color.WHITE);
			//Bordures
			r.setStrokeWidth(1);
			r.setStroke(Color.BLACK);
			//
			//if(b)posX+=r.getWidth()+0.5;
			posX+=r.getWidth()+2.5;
		}
		//
		private void dessine_stats() {
			// TODO Auto-generated method stub
			GraphicsContext g=stats.getGraphicsContext2D();
			g.clearRect(0, 0, this.getWidth(), this.getHeight());
			g.setFill(Color.BLACK);
			
			String [] s=Vue.this.uniteActuelle.getStats();//
			Text t=new Text(s[0]);//
			
			double posX_c2=5;
			double posY_c2=t.getLayoutBounds().getHeight();
			for(int i=0;i<s.length;i++) {
				if(i%2==1) {
					posX_c2=5;
					posY_c2+=t.getLayoutBounds().getHeight()+5;
				}
				g.fillText(s[i],posX_c2,posY_c2,stats.getWidth()/3);
				posX_c2+=stats.getWidth()/2;
			}
		}
		private void dessine_skill() {//Dessine les skills
			for(Skill_slots tmp: skills)tmp.dessine_skill();
		}
		public void set_skill() {//ICI pour set les actions d'un skill
			for(Skill_slots tmp: skills)tmp.set_skill();
		}
		public void dessine_statuts() {
			for(Fx_Statut t: uniteActuelle.get_Statuts()) {
				if(t.getToursRestants()>0) {
					t.getIcon().setOnMouseEntered(e->{
						Vue.this.description.setDescription((Image)t.getIcon().getImage(),
								t.toString(),
								"Duration : "+t.getToursRestants()+" tours",
								t.getDescription());

						Vue.this.description.entered(t.getIcon().getLayoutX(),Vue.this.bloc.getLayoutY()+t.getIcon().getFitHeight()+18,t.getIcon().getFitWidth()/2);
					});
					t.getIcon().setOnMouseExited(e->{
						Vue.this.description.exited();
					});
					statuts.getChildren().addAll(t.getIcon());			
				}
			}
		}
		//
		public void select() {//Lors de selection d'une unite - Canvas comportant l'affichage du Fx_Unite
			GraphicsContext c=container.getGraphicsContext2D();
			GraphicsContext s=stats.getGraphicsContext2D();
			
			c.clearRect(0,0,container.getWidth(),container.getHeight());//Enleve le dessin effectue sur le Canvas selectionne //c1
			s.clearRect(0,0,stats.getWidth(),stats.getHeight());//Enleve le dessin effectuer sur le Canvas des stats //c2
			for(Skill_slots tmp: skills) {//Enleve les dessins effectues sur les slots de skills //c3
				tmp.clearCanvas();
			}
			
			getChildren().removeAll(container,stats);
			getChildren().removeAll(skills);
			statuts.getChildren().clear();	//supprime les statuts affiches de l'unite d'"avant"

			if(Vue.this.uniteActuelle!=null) {
			//Instanciation et initialisation des positions des Canvas
			container=new Fx_Unite(Vue.this.uniteActuelle);//c1
			
			stats=new Canvas(r_stats.getWidth(),r_stats.getHeight());//c2
			
			container.setLayoutX(r_selected.getLayoutX()+(r_selected.getWidth()-container.getWidth())/2);//Set la position en X
			container.setLayoutY(r_selected.getLayoutY()+(r_selected.getHeight()-container.getHeight())/2);//Set la position en Y
			
			stats.setLayoutX(r_stats.getLayoutX()+(r_stats.getWidth()-stats.getWidth())/2);//Set la position en X
			stats.setLayoutY(r_stats.getLayoutY()+(r_stats.getWidth()-stats.getWidth())/2);//Set la position en Y
			
			skills=new Skill_slots[1+uniteActuelle.getSkill().size()];
			for(int i=0;i<1+uniteActuelle.getSkill().size();i++) {
				skills[i]=new Skill_slots(r_skills[i].getWidth(),r_skills[i].getHeight(),i+1);//c3
				skills[i].setLayoutX(r_skills[i].getLayoutX()+(r_skills[i].getWidth()-skills[i].getWidth())/2);//Set la position en X
				skills[i].setLayoutY(r_skills[i].getLayoutY()+(r_skills[i].getWidth()-skills[i].getWidth())/2);//Set la position en Y
			}
			
			//On dessine selon l'unite selectionne et on set les attaques et skills
			dessine_stats();
			dessine_skill();
			set_skill();
			
			//Ajout du Fx_unite, des stats, et skills
			getChildren().addAll(container,stats);
			getChildren().addAll(skills);
			dessine_statuts();//Ajoute les statuts de l'unite selectionnee
			}
		}		
		//DEBUT INNER INNER CLASS SKILL_SLOTS dans Selection
		public class Skill_slots extends Pane {
			private Canvas skill;// Image/description du skill
			private Canvas unusable;//Canvas utilise lorsque les conditions d'utilisation de l'attaque ou de la competence
			private Canvas cd;//Affiche le temps de recuperation de la competence
			private int slot_indice;//Indice du skill 
			private int slot;//numero du skill //et selection_type de l'attaque ou skill
			private LinkedList<Fx_Hexagon> cibles;//Cases ou la competence peut faire effet
			private Color selection_color;
			private boolean condition;//Si true l'attaque ou le skill est faisable
			private boolean condition_cooldown;//Si true n'est pas en cooldown
			Skill_slots(double width,double height,int slot){
				this.setWidth(width);
				this.setHeight(height);
				this.slot=slot;
				slot_indice=slot-1;
				selection_color=Color.CADETBLUE;
				//
				skill=new Canvas(width,height);
				unusable=new Canvas(width,height);
				cd=new Canvas(width,height);
				if(isRvr() || (isRob1() && camp==1))this.setDisable(true);
				getChildren().addAll(skill,unusable,cd);
			}
			public void set_Cibles() {//Set les Fx_Hexagon associes a l'attaque ou aux competences
				if(slot==1)cibles=uniteActuelle.getRange();
				else cibles=uniteActuelle.getSkill().get(slot_indice-1).getCibles();
			}
			public void show_Cibles() {//Colore les Fx_Hexagon du skill/attaque actuel
				for(Fx_Hexagon fx: cibles)fx.getPolygone().setFill(selection_color);
			}
			public void set_skill() {//Partie "model" des skills (competences)
				Color tmp=(Color) r_skills[slot_indice].getFill();
				Color tmp_b=Color.ORANGERED;
					
				//Si condition==true alors l'attaque ou la competence est utilisable
				if(uniteActuelle!=null && slot_indice!=0 && int_PA<uniteActuelle.getSkill().get(slot_indice-1).getCost())condition=false;//Selon les couts du sort que l'on veut utiliser
				//Dans le cas ou l'on veut faire une attaque de base on verifie attaquable et PA //Cout des PA pour les attaques de bases modulables ? //Si oui, a changer
				else if(slot==1)condition=(uniteActuelle.getattaquable() && !uniteActuelle.getattdone() && int_PA>=2);
				else if(slot>1 && slot<5)condition=uniteActuelle.getcastable() &&  uniteActuelle.getSkill().get(slot_indice-1).available();//Dans le cas ou c'est un sort
				if(slot_indice==0) {
					skills[slot_indice].setOnMouseEntered(e->{
						Vue.this.description_attaque.setDescription(uniteActuelle.get_des_attaque());						
						Vue.this.description_attaque.entered(r_skills[slot_indice].getLayoutX()-description_attaque.container.getWidth()/2+18,Vue.this.bloc.getLayoutY()+r_skills[slot_indice].getLayoutY()-30);
					});

				}
				
				if(slot_indice>0) {
					skills[slot_indice].setOnMouseEntered(e->{
					Vue.this.description.setDescription(uniteActuelle.getSkill().get(slot_indice-1).getImage(),
							uniteActuelle.getSkill().get(slot_indice-1).getskillName(),
							"Usage : "+uniteActuelle.getSkill().get(slot_indice-1).getCost(),
							uniteActuelle.getSkill().get(slot_indice-1).description());

					Vue.this.description.entered(r_skills[slot_indice].getLayoutX(),Vue.this.bloc.getLayoutY()+r_skills[slot_indice].getHeight()+18,r_skills[slot_indice].getWidth()/2);

					});
				}
				if(slot_indice==0) {
					skills[slot_indice].setOnMouseExited(e->{
						Vue.this.description_attaque.exited();
					});
				}
				else{
					skills[slot_indice].setOnMouseExited(e->{
						r_skills[slot_indice].setFill(tmp);
						Vue.this.description.exited();
					});
				}				
				skills[slot_indice].setOnMousePressed(e->{
					if(condition)r_skills[slot_indice].setFill(tmp_b);
				});
				skills[slot_indice].setOnMouseReleased(e->{
					if(condition)r_skills[slot_indice].setFill(tmp);
				});
				
				skills[slot_indice].setOnMouseClicked(e->{
					if(condition) {
						deselection();
						uniteSelectionnee=null;//Ennemie
						setselection_type(slot);//
						set_Cibles();//set les cases de l'attaque ou le sort 
						show_Cibles();//Affiche les cases (les colores)
					}
				});
				
			}
			public void dessine_skill() {//Concerne la partie graphique des skills (competences)
				//verification cooldown
				if(slot==1)condition_cooldown=true;
				else if(slot>1 && slot<5)condition_cooldown=uniteActuelle.getSkill().get(slot_indice-1).getCd()==0;
				
				if(uniteActuelle!=null && slot_indice!=0 && int_PA<uniteActuelle.getSkill().get(slot_indice-1).getCost())condition=false;//Selon les couts du sort que l'on veut utiliser
				else if(slot==1)condition=(uniteActuelle.getattaquable() && !uniteActuelle.getattdone() && int_PA>=2);
				else if(slot>1 && slot<5)condition=uniteActuelle.getcastable() && uniteActuelle.getSkill().get(slot_indice-1).available();
				
				GraphicsContext g=skill.getGraphicsContext2D();
				g.clearRect(0, 0, skill.getWidth(), skill.getHeight());
				if(slot==1) {//le slot 1 correspond a l'attaque de base
					double h_bas=skill.getHeight()/4.0;
					double forme=((skill.getHeight())*9.0/16.0);
					double x=2.5;
					double y=0.5;
					double[] pointsX=new double[] {
							skill.getWidth()/2,
						x,
						skill.getWidth()/2,
						x,
						skill.getWidth()/2,
						skill.getWidth()/2,
						skill.getWidth()/2,
						skill.getWidth()-x,
						skill.getWidth()/2,
						skill.getWidth()-x,				
				};
				
				double[] pointsY=new double[] {
						y,
						forme,
						skill.getHeight()-h_bas-y,
						skill.getHeight()-h_bas-y,
						skill.getHeight()-h_bas-y,
						skill.getHeight()-y,
						skill.getHeight()-h_bas-y,
						skill.getHeight()-h_bas-y,
						skill.getHeight()-h_bas-y,
						forme
				};
				g.setFill(Color.BISQUE);
				g.fillPolygon(pointsX,pointsY,10/*nb de points a poses*/);
				g.setFill(Color.BLACK);
				g.strokePolygon(pointsX, pointsY, 10);
				} else if (slot>1 && slot<5) {//les slots de 2 a 4 sont les competences
					Text t=new Text(uniteActuelle.getSkill().get(slot_indice-1/*Sans compter l'attaque de base*/).getskillName());
					t.setFont(Font.getDefault());
					//
					g.setFont(t.getFont());
					g.fillText(t.getText(), 0, skill.getHeight()/2-t.getBoundsInLocal().getHeight()/4,skill.getWidth());
					//
					t.setText("Usage : "+uniteActuelle.getSkill().get(slot_indice-1).getCost());
					g.fillText(t.getText(),0, skill.getHeight()-t.getBoundsInLocal().getHeight()/4,skill.getWidth());
					
					if(uniteActuelle.getSkill().get(slot_indice-1).getImage()!=null) {
						g.drawImage(uniteActuelle.getSkill().get(slot_indice-1).getImage(), 0, 0,skill.getWidth(),skill.getHeight());
					}
				}
				if(!condition) {
					unusable.setOpacity(0.2);
					GraphicsContext bis=unusable.getGraphicsContext2D();
					bis.fillRect(0, 0, unusable.getWidth(), unusable.getHeight());
					if(slot==1)return;
					//
					Text t=null;
					
					if(uniteActuelle.getSkill().get(slot_indice-1).available()==false) {
						t=new Text(uniteActuelle.getSkill().get(slot_indice-1).getCd()+"");
						t.setFont(new Font(35));
						bis=cd.getGraphicsContext2D();
						bis.setFont(t.getFont());
						bis.setFill(Color.PAPAYAWHIP);
						bis.setStroke(Color.BLACK);
						if(!condition_cooldown)bis.strokeText(t.getText(), skill.getWidth()/2-t.getBoundsInLocal().getWidth()/2, skill.getHeight()/2+t.getBoundsInLocal().getHeight()/4);
						if(!condition_cooldown)bis.fillText(t.getText(), skill.getWidth()/2-t.getBoundsInLocal().getWidth()/2, skill.getHeight()/2+t.getBoundsInLocal().getHeight()/4);
					}
					else {
						if(uniteActuelle.getcastable()==false) {
							t=new Text("Sort \n Incastable");
							t.setFont(new Font(12));
							bis=cd.getGraphicsContext2D();
							bis.setFont(t.getFont());
							bis.setFill(Color.RED);
							bis.setStroke(Color.BLACK);
							bis.strokeText(t.getText(), skill.getWidth()/2-t.getBoundsInLocal().getWidth()/2, skill.getHeight()/2+t.getBoundsInLocal().getHeight()/4);
							bis.fillText(t.getText(), skill.getWidth()/2-t.getBoundsInLocal().getWidth()/2, skill.getHeight()/2+t.getBoundsInLocal().getHeight()/4);
						}
						else {
							t=new Text("PA \n Insuffisant");
							t.setFont(new Font(12));
							bis=cd.getGraphicsContext2D();
							bis.setFont(t.getFont());
							bis.setFill(Color.RED);
							bis.setStroke(Color.BLACK);
							bis.strokeText(t.getText(), skill.getWidth()/2-t.getBoundsInLocal().getWidth()/2, skill.getHeight()/2+t.getBoundsInLocal().getHeight()/4);
							bis.fillText(t.getText(), skill.getWidth()/2-t.getBoundsInLocal().getWidth()/2, skill.getHeight()/2+t.getBoundsInLocal().getHeight()/4);
						}
					}
					
					//
					
				}
			}
			public void clearCanvas() {//Enleve les dessins fait dans les canvas (skill et unusable)
				GraphicsContext g=skill.getGraphicsContext2D();
				g.clearRect(0, 0, skill.getWidth(), skill.getHeight());
				
				g=unusable.getGraphicsContext2D();
				g.clearRect(0, 0, unusable.getWidth(), unusable.getHeight());
				
				g=cd.getGraphicsContext2D();
				g.clearRect(0, 0, cd.getWidth(), cd.getHeight());
			}
		}//FIN INNER INNER CLASS SKILL_SLOTS dans Selection
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
			if(Vue.this.getselection_type()>1 && Vue.this.getselection_type()<5 && !isRvr())//sorts 2 - 3 - 4 
				for(Observer o:observers)o.updateSort(uniteActuelle, selection_type-2, cible.getX(), cible.getY());
			else if(!isRvr())
			for(Observer o : observers)o.updateAttaque(uniteActuelle,cible.getX(),cible.getY());
		
		}

	}//FIN INNER CLASS
	
	//DEBUT INNER CLASS SELECTION_ENNEMIE
		public class Selection_Ennemie extends Pane {//Panel de l'unite ennemie selectionne
			private Canvas container;//	1.0/2.0 	//c1 
			private Canvas stats;//	1.0/2.0 		//c2
			private HBox statuts = new HBox();
			private Affichage_Statuts aff_statuts = new Affichage_Statuts();
			
			private Rectangle r_selected;//Rectangle sur lequel le canvas affichera l'Unite selectionne
			private Rectangle r_stats;//Rectangle sur lequel le canvas affichera les stats de l'unite selectionne
			
			private double posX;//Positionnement en X pour poser les Rectangle 
			private double bords_haut=20;//Espace en hauteur
		
			
			public Selection_Ennemie(double width,double height){
				this.setWidth(width);
				this.setHeight(height);
							
				double hauteur=height-bords_haut*2;//Tous les rectangles secondaires auront cette hauteur
				double longueur=getWidth();//Longueur sur laquelle seront les canvas
					
				double longueur_c1=Math.max(longueur/2.0,hauteur/2.0);//Des mathematiques pour la taille du premier canvas
				if(longueur_c1>hauteur)longueur_c1=hauteur;//Au cas ou, toujours pour la taille du canvas
				double longueur_c2 = longueur/2.0;//Longueur des canvas stats
				
				posX=(getWidth()-longueur)/2;
				if(longueur_c1<longueur/2.0)posX+=longueur_c1/10.0;
				
				//Instanciations des canvas
				stats=new Canvas(longueur_c2,hauteur);			
				container=new Canvas(longueur_c1,hauteur);
				//
				//RECTANGLE
				Rectangle r=new Rectangle(width,height);//Contenant principal
				
				r_stats=new Rectangle(stats.getWidth(),stats.getHeight());//c2 //Contiendra l'affichage des stats
				r_selected=new Rectangle(container.getWidth(),container.getHeight());//c1 //Contiendra l'affichage du Fx_Unite
				
				setPrincipalRect(r);
				set_Rect(r_stats);
				set_Rect(r_selected);
				
				//Ajouts			
				getChildren().addAll(r,r_stats,r_selected);//Ajout des Rectangles
				getChildren().add(statuts);
			}

			public void setPrincipalRect(Rectangle r) {//Rectangle en background 
				//Cotes arrondies
				r.setArcHeight(10);
				r.setArcWidth(10);
				//Remplissage
				r.setFill(Color.INDIANRED);
				//Bordures
				r.setStrokeWidth(1);
				r.setStroke(Color.DARKRED);
			}
			public void set_Rect(Rectangle r) {//Set le rectangle (Couleur,Bordure et leger changement de position)
				r.setWidth(r.getWidth()-5);
				r.setLayoutX(posX+5);
				r.setLayoutY(bords_haut);
				//Cotes arrondies
				r.setArcHeight(5);
				r.setArcWidth(5);
				//Remplissage
				r.setFill(Color.WHITE);
				//Bordures
				r.setStrokeWidth(1);
				r.setStroke(Color.BLACK);
				//
				posX+=r.getWidth()+5;
			}

			private void dessine_stats() {
				GraphicsContext g=stats.getGraphicsContext2D();
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				g.setFill(Color.BLACK);
				
				String [] s=Vue.this.uniteSelectionnee.getStats();//
				Text t=new Text(s[0]);//
				
				double posX_c2=5;
				double posY_c2=0;
				for(int i=0;i<s.length;i++) {
					if(i>3 && i!=6)continue;
					posX_c2=5;
					posY_c2+=t.getLayoutBounds().getHeight()+2;
					
					g.fillText(s[i],posX_c2,posY_c2,stats.getWidth());
				}
			}
			
			//
			public void select() {//Canvas comportant l'affichage du Fx_Unite
				
				GraphicsContext c=container.getGraphicsContext2D();
				GraphicsContext s=stats.getGraphicsContext2D();
				
				c.clearRect(0,0,container.getWidth(),container.getHeight());//Enleve le dessin effectue sur le Canvas selectionne //c1
				s.clearRect(0,0,stats.getWidth(),stats.getHeight());//Enleve le dessin effectuer sur le Canvas des stats //c2
				aff_statuts.container_statuts.getChildren().clear();
				aff_statuts.limite = 4;
				aff_statuts.depart = 0;
				getChildren().removeAll(container,stats,aff_statuts);
				statuts.getChildren().clear();//supprime les statuts affiches de l'unite d'"avant"

				if(Vue.this.uniteSelectionnee!=null) {
				//Instanciation et initialisation des positions des Canvas
				container=new Fx_Unite(Vue.this.uniteSelectionnee);//c1
				stats=new Canvas(r_stats.getWidth(),r_stats.getHeight());//c2
				
				container.setLayoutX(r_selected.getLayoutX()+(r_selected.getWidth()-container.getWidth())/2);//Set la position en X
				container.setLayoutY(r_selected.getLayoutY()+(r_selected.getHeight()-container.getHeight())/2);//Set la position en Y
				
				stats.setLayoutX(r_stats.getLayoutX()+(r_stats.getWidth()-stats.getWidth())/2);//Set la position en X
				stats.setLayoutY(r_stats.getLayoutY()+(r_stats.getWidth()-stats.getWidth())/2);//Set la position en Y
				
				//On dessine les stats de l'unite selectionne
				dessine_stats();
				//Ajout du Fx_unite, des stats, et skills
				
				getChildren().addAll(container,stats,aff_statuts);
				aff_statuts.dessine_statuts();
				}
			}
			public class Affichage_Statuts extends Pane{
				//DEBUT INNER CLASS PANNEAU_STATUTS dans Selection
				private Fleche droite = new Fleche(1);
				private Fleche gauche = new Fleche(2);
				private Rectangle r_statuts = new Rectangle(80,20);				
				private HBox container_statuts = new HBox();
				int limite = 4;
				int depart = 0;
				public Affichage_Statuts() {
					this.setLayoutX(50);
					r_statuts.setLayoutX(r_statuts.getLayoutX()+20);
					r_statuts.setFill(Color.INDIANRED);
					droite.setLayoutX(r_statuts.getLayoutX()+r_statuts.getWidth());
					this.setWidth(Selection_Ennemie.this.getWidth()-30);
					container_statuts.setLayoutX(r_statuts.getLayoutX());
					droite.setOnMouseClicked(e->{
						next();
					});
					gauche.setOnMouseClicked(e->{
						prev();
					});
					getChildren().addAll(gauche,r_statuts,container_statuts,droite);
					
				}
				public class Fleche extends Pane {
					private Rectangle container;
					private Arrow fleche;
					
					public Fleche(int ori) {		//1 si droit 2 si gauche	
						container = new Rectangle(20,20);
						fleche = new Arrow(20,20,ori);
						this.setWidth(20);
						this.setHeight(20);
						container.setFill(Color.INDIANRED);
						
						getChildren().addAll(container,fleche);
					}

					
			}
			
				

				//FIN INNER CLASS Fleche_Statuts dans Selection
				public void next() {
					
					if(depart+4<=uniteSelectionnee.get_Statuts().size()) {
						depart = limite;
						limite += 4;
						container_statuts.getChildren().clear();
						supp_statuts();
						dessine_statuts();
					}
				}
				public void prev() {
					limite -= 4;
					depart = limite-4;
					container_statuts.getChildren().clear();
					supp_statuts();	
					dessine_statuts();
				}
				
				public int reste_a_afficher() {
					int c = 0;
					if(uniteSelectionnee!=null)for (int i = depart; i < uniteSelectionnee.get_Statuts().size() ; i++)c++;
					return c;
				}
				
				
			
				public void dessine_statuts() {
					if(uniteSelectionnee!=null) {
							int min = Math.min(limite, uniteSelectionnee.get_Statuts().size());
							if(depart==0)gauche.setDisable(true);
							else gauche.setDisable(false);
							for(int i = depart; i<min ;i++) {
								if( i>=0 && i<uniteSelectionnee.get_Statuts().size()) {
									if(uniteSelectionnee.get_Statuts().get(i).getToursRestants()>0) {
										final int tmp = i;
										uniteSelectionnee.get_Statuts().get(i).getIcon().setOnMouseEntered(e->{
											if(uniteSelectionnee.get_Statuts().get(tmp).getIcon().getImage()!=null) {
												Vue.this.description.setDescription((Image)uniteSelectionnee.get_Statuts().get(tmp).getIcon().getImage(),
														uniteSelectionnee.get_Statuts().get(tmp).toString(),
														"Duration : "+uniteSelectionnee.get_Statuts().get(tmp).getToursRestants()+" tours",
														uniteSelectionnee.get_Statuts().get(tmp).getDescription());
											}
											

											Vue.this.description.entered(uniteSelectionnee.get_Statuts().get(tmp).getIcon().getLayoutX()+selection_allie.getWidth()+aff_statuts.getWidth()/3 + 35,Vue.this.bloc.getLayoutY()+uniteSelectionnee.get_Statuts().get(tmp).getIcon().getFitHeight()+18,uniteSelectionnee.get_Statuts().get(tmp).getIcon().getFitWidth()/2);
										});
										uniteSelectionnee.get_Statuts().get(i).getIcon().setOnMouseExited(e->{
											Vue.this.description.exited();
										});
										container_statuts.getChildren().addAll(uniteSelectionnee.get_Statuts().get(i).getIcon());			
									}
									 droite.setDisable(false);

								}	
								else {
									droite.setDisable(true);
									break;	
								}

							}
						}					
				}
				public void supp_statuts() {
					if(uniteSelectionnee!=null) {
						for(Fx_Statut t: uniteSelectionnee.get_Statuts()) {
							if(t.getToursRestants()>0) {
								container_statuts.getChildren().remove(t.getIcon());			
							}
						}
					}
					
				}			
			}		
			
			public class Arrow extends Pane {//Panel contenant une fleche pointant vers la gauche
				Arrow(double width,double height,int ori){					
					//Polygon
					Polygon arrow=new Polygon();//Polygon
					arrow.setStrokeWidth(2);//Longueur des bordures
					arrow.setStroke(Color.BLACK);//Couleur des bordures du polygone
					arrow.getPoints().addAll(new Double[] {
							5.0,this.getHeight()/2,
							this.getWidth()-5,this.getHeight()-5,
							this.getWidth()-5,5.0
					});
					
					if(ori==1) {
						arrow.setLayoutX(arrow.getLayoutX()+10);
						arrow.setLayoutY(arrow.getLayoutY()+11);
					}
					if(ori==2) {
						this.setRotate(180);
						arrow.setLayoutX(arrow.getLayoutX()+4);
						arrow.setLayoutY(arrow.getLayoutY()+4);
					}
					getChildren().addAll(arrow);
				}
			}//Fin classe interne Arrow

		}//FIN INNER CLASS SELECTION_ENNEMIE
		//DEBUT INNER CLASS Description_Attaque
		public class Description_Attaque extends Pane{
			private Rectangle container;
			private Label label_description = new Label();
			private String description;
			
			public Description_Attaque() {
				container = new Rectangle(180,50);//
				container.setFill(Color.NAVAJOWHITE);
				container.setStroke(Color.BLACK);
				container.setStrokeWidth(1);
				label_description.setTextAlignment(TextAlignment.JUSTIFY);
				label_description.setWrapText(true);
	            label_description.setMaxWidth(container.getWidth()-5);
	            label_description.setLayoutX(label_description.getLayoutX()+5);
				getChildren().addAll(container,label_description);
				this.setVisible(false);
			}
			
			public void setDescription(String d) {
				description = d;
			}
			

			public void entered(double X, double Y) {
				this.setLayoutX(X);
				this.setLayoutY(Y);
				this.label_description.setText(description);
				this.setVisible(true);
			}

			public void exited() {
				this.setVisible(false);
			}

		}		
		//FIN INNER CLASS Description_Attaque
    //DEBUT INNER CLASS Description_Box
    public class Description_Box extends Pane{
    	//Partie GUI
    	//Forme du panel
    	private Polygon text_bubble;
    	//Image du skill/statut/autre . . .
    	private Rectangle r_image; //c1
    	private Canvas c_image;
        private Image image;
        // Mini descriptif , nom , skill usage (si skill)
        private Rectangle r_title_sub_text; // c2 - c3
        private Canvas c_title_sub_text;
        //Description
        private Label description_panel;//Panel contenant la description
        //Partie "data" 
        //
        private String title;//Titre, nom du statut/competence/autre
        private String sub_text;//Mini descriptif sur par exemple le cout d'usage d'un skill
        private String description;//Description du statut/competence/autre
        //
        private double target_x_center;
        private double c_hauteur;
        private double c1_width, c2_width, c3_width;//taille des composants,
    	double espacement_bas=10;
    	
    	private double width;
    	private double height;
        Description_Box(double width){
            this.width=width;

            c1_width=width/5;//double
            c2_width=width/5;
            c3_width=width-c1_width-c2_width;
            c_hauteur=c1_width+2;//double
            //Polygon initialisation
            text_bubble=new Polygon();//Polygone
            //Image 
        	r_image=new Rectangle(c1_width,c1_width);//Rectangle
        	c_image=new Canvas(r_image.getWidth(),r_image.getHeight());//Canvas
        	//
        	r_title_sub_text=new Rectangle(c2_width+c3_width,c1_width);//Taille en width = c2+c3 (titre+mini description (s'il y a))
        	c_title_sub_text=new Canvas(r_title_sub_text.getWidth(),r_title_sub_text.getHeight());
        	//Panel descriptif
            description_panel=new Label();
        	description_panel.setWrapText(true);
            description_panel.setMaxWidth(width);
            //
            setRect();
            //
        	this.setWidth(width);
        	this.setPrefWidth(width);
            //
        	this.getChildren().addAll(text_bubble,r_image,r_title_sub_text);//Ajout des Rectangles
        	this.setVisible(false);
        }
        public void setDescription(Image image,String title,String sub_text,String description) {
        	this.image=image;
        	this.title=title;
        	this.sub_text=sub_text;
        	this.description=description;   
        }
        public void entered(double x,double y, double target_x_center) {//Position relative au skill/statut ou autre sur le quel on pointera
        	this.target_x_center=target_x_center;       	
        	//
        	GraphicsContext gz=c_image.getGraphicsContext2D();
        	gz.drawImage(image, 0, 2,r_image.getWidth(),r_image.getHeight()-2);
        	//
        	GraphicsContext g=c_title_sub_text.getGraphicsContext2D();
        	c_title_sub_text.setLayoutX(r_title_sub_text.getLayoutX());
        	Text r=new Text(sub_text);
        	
        	g.fillText(title, 2, 15);
        	g.fillText(sub_text, r_title_sub_text.getWidth()-r.getBoundsInLocal().getWidth()-2, r_title_sub_text.getHeight()-10);
        	//Panel descriptif
        	description_panel.setText(description);
        	//
        	description_panel.applyCss();
        	description_panel.layout();
        	height=c_hauteur+100;//Pour le moment, en attendant de calaculer la hauteur d'une zone de texte correctement
        	//height=150;
        	//Layouts
        	if(x+target_x_center-width/2<0)this.setLayoutX(x);//On verifie s'il y a assez d'espace au bord gauche (pour pas que le panel soit en dehors de l'ecran)
        	else if(x+target_x_center+width/2>Vue.this.getWidth()) {
        		this.setLayoutX(Vue.this.getWidth()-this.getWidth());//De meme pour le bord droit
        		this.target_x_center=Vue.this.getWidth()-this.getWidth();
        	}
        	else {
        		this.setLayoutX(x+target_x_center-this.getWidth()/2);
        		this.target_x_center=width/2;
        	}
        	this.setLayoutY(y-height);
        	
        	//Polygone
        	setPolygone();
        	if(y-height>0) {
            	text_bubble.setScaleY(1);
            	int tr=0;
        		r_image.setTranslateY(tr);
        		r_title_sub_text.setTranslateY(tr);
        		
            	c_image.setTranslateY(tr);
            	c_title_sub_text.setTranslateY(tr);
            	description_panel.setTranslateY(tr);
            	this.setLayoutY(y-height);
        	} else {
        		int tr=11;
        		text_bubble.setScaleY(-1);
        		r_image.setTranslateY(tr);
        		r_title_sub_text.setTranslateY(tr);
        		
            	c_image.setTranslateY(tr);
            	c_title_sub_text.setTranslateY(tr);
            	description_panel.setTranslateY(tr);
        		this.setLayoutY(y+height);
        	}
        	this.getChildren().addAll(c_image,c_title_sub_text,description_panel);//Ajout des Canvas et panel
        	this.setVisible(true);
        }
        public void exited() {//On reinitialise chaque composants (on enleve leurs contenu)
        	this.setVisible(false);

        	//Descriptif
        	description_panel.setText("");
        	//Mini descriptif (canvas)
        	GraphicsContext g1=c_title_sub_text.getGraphicsContext2D();// c2 - c3
        	g1.clearRect(0,0, c_title_sub_text.getWidth(), c_title_sub_text.getHeight());

        	//On clear l'image dessine par le canvas
        	GraphicsContext g=c_image.getGraphicsContext2D();
        	g.clearRect(0, 0, c_image.getWidth(), c_image.getHeight());//On enleve
        	//Image - on enleve l'image si associe
        	if(image!=null)image.cancel();//c1
        	image=null;//c1
        	this.getChildren().removeAll(c_image,c_title_sub_text,description_panel);//On enleve les Canvas et panel lie a l'affichage
        }
        private void setPolygone() {//Set la forme du polygone associe au panel
        	double epaisseur=5;

        	text_bubble.getPoints().clear();
        	text_bubble.getPoints().addAll(new Double[] {
        			0.0, 0.0,
        			this.getWidth(),0.0,
        			this.getWidth(),height-espacement_bas,
        			
        			target_x_center+epaisseur,height-espacement_bas,
        			target_x_center,height,
        			target_x_center-epaisseur,height-espacement_bas,

        			0.0,height-espacement_bas,
        	});

        	text_bubble.setFill(Color.NAVAJOWHITE);
        	text_bubble.setStroke(Color.BLACK);
        }
        private void setRect() {
        	r_image.setWidth(c1_width-4);
        	r_image.setHeight(c1_width-4);
        	r_title_sub_text.setWidth(c2_width+c3_width-8);//r_title_sub_text.getWidth()-10
        	r_title_sub_text.setHeight(c1_width-4);
        	description_panel.setMaxWidth(description_panel.getMaxWidth()-4);
        	//
        	r_image.setLayoutX(2);
        	r_image.setLayoutY(2);
        	r_title_sub_text.setLayoutX(r_image.getLayoutX()+c1_width+2);
        	r_title_sub_text.setLayoutY(2);
        	
        	description_panel.setLayoutX(2);
        	description_panel.setLayoutY(r_image.getHeight()+2);//r_image.getHeight+5
        	//
        	r_image.setFill(Color.NAVAJOWHITE);
        	r_title_sub_text.setFill(Color.NAVAJOWHITE);
        	
        	r_image.setStroke(Color.BLACK);
        	r_title_sub_text.setStroke(Color.BLACK);
        }
        
    }
    //FIN INNER CLASS Description_Box
	//DEBUT INNER CLASS Tour
	public class Tour extends Pane implements Observable{
		LinkedList<Observer> observers=new LinkedList<>();
		private Color color = Color.CORNSILK;
		Tour(double width,double height){
			setWidth(width);
			setHeight(height);
			Rectangle r=new Rectangle(width,height);
			r.setFill(color);
			r.setStroke(color.darker().darker());
			r.setStrokeWidth(1);
			Text text=new Text("Fin de tour");
			text.setFont(new Font(25));
			getChildren().addAll(r,text);
			text.setLayoutX(r.getWidth()/5);
			text.setLayoutY(r.getHeight()/1.5);

			this.setOnMouseEntered(e->{//Lorsqu'on entre dans le Panel
				r.setFill(color.darker());
			});
			this.setOnMouseClicked(e->{//Lorsqu'on clique sur le Panel
				Notify();			
			});
			this.setOnMouseExited(e->{//Lorsqu'on part du Panel
				r.setFill(color.brighter());
			});
		}
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
			for(Observer x: observers) {
				x.updateTours();
			}
		}
		
	}//FIN INNER CLASS Tour.java
	//DEBUT INNER CLASS Abandon
			public class Abandon extends Pane implements Observable{
				private Canvas croix;
				private Rectangle container = new Rectangle(50,50);
				public LinkedList<Observer> obs = new LinkedList<>();
				
				public Abandon() {
					container.setFill(Color.WHITE);
					container.setStroke(Color.GOLDENROD);
					container.setStrokeWidth(2);
					croix = new Canvas(50,50);
					GraphicsContext gc = croix.getGraphicsContext2D();
					gc.setLineWidth(6);
					gc.setStroke(Color.RED);
					gc.strokeLine(40, 10, 10, 40);
					gc.strokeLine(10, 10, 40, 40);
				
					this.setOnMouseClicked(e->{
						Notify();
					});
					this.setOnMouseEntered(e->{
						container.setFill(((Color)container.getFill()).darker());
					});
					this.setOnMouseExited(e->{
						container.setFill(((Color)container.getFill()).brighter());
					});
					this.setLayoutX(Vue.this.getWidth()-container.getWidth()-4);
					this.setLayoutY(this.getLayoutY()+2);

					getChildren().addAll(container,croix);
					
				}
				
				@Override
				public void addObserver(Observer o) {
					obs.add(o);
				}
				@Override
				public void removeObserver(Observer o) {
					obs.remove(o);
				}
				@Override
				public void Notify() {
					for(Observer o:obs) {
						o.updateAbandon();				
					}
				}
				
			}
			
			//FIN INNER CLASS Abandon

	public void setImage(String NOM_IMAGE) {
		NOM_IMAGE+=".png";
		Image i;
		GraphicsContext g=fond.getGraphicsContext2D();
		try {
			i=new Image(new File(PATH+"/"+NOM_IMAGE).toURI().toURL().toString());
			g.drawImage(i, 0, 0, fond.getWidth(), fond.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void addPane_VBox(Node ... x) {//pour ajouter des panels dans la VBox de vue
		//Version test changeable
		for(Node node: x) {
		if(root.getChildren().size()<1) {//Le premier ajout se fait dans VBox
			root.getChildren().add(node);
			bloc.setTranslateX(2);
			root.getChildren().add(bloc);
			if(node instanceof HexagonalGrid)setImage(((HexagonalGrid)node).getId());
		} else {//Les secondes dans bloc
			bloc.getChildren().add(node);
		}
		}
	}
	public void remove_In_VBox(Node x) {//Pour remove un panel dans la VBox de Vue
		if(root.getChildren().contains(x))root.getChildren().remove(x);
		else if(bloc.getChildren().contains(x))bloc.getChildren().remove(x);
	}
	public void setLayoutY_VBox(double y) {
		this.root.setLayoutY(y);
	}
	//Set la classe interne Selection - ... et l'ajoute normalement dans bloc
	private void setSelection(Game g) {
		if(this.selection_allie==null) {
		//	selection_allie=new Selection(600,bloc.getHeight()-5);
			selection_allie=new Selection(600,135.666666);

		}
		if(this.selection_ennemie==null) {
		//	selection_ennemie=new Selection_Ennemie(225,bloc.getHeight()-5);
			selection_ennemie = new Selection_Ennemie(225,135.66666);

		}
		addPane_VBox(selection_allie,selection_ennemie);
		selection_allie.addObserver(g);
	}
	private void setPA(Game g) {
		if(this.PA==null) {
			PA=new Text();
			PA.setFont(new Font(22));
			setPA(g.getPa());
		}
		addPane_VBox(PA);
	}
	//Set la classe interne Tour - tour==new Tour(...); et l'ajoute normalement dans bloc
	private void setTour(Game g) {
		VBox vbox=new VBox(5);
		if(joueur==null) {//Panel affichant quel est le tour du joueur actuel
			joueur=new Text(10,50,"");//A futurement changer selon les proportions en taille
			joueur.setFont(new Font(22));
			joueur.setFill(Color.TOMATO);
		}
		if(tour==null) {//Affiche le bouton pour passer son tour
			tour=new Tour(175,50);//A futurement changer selon les proportions en taille
			tour.addObserver(g);
		}
		vbox.getChildren().addAll(joueur,tour);
		vbox.setTranslateX(0);//A modifier
		addPane_VBox(vbox);
	}
	private void setAbandon(Game g) {
		if(abandon!=null) {
			abandon.addObserver(g);
		}
	}
	private void setCamp() {
		if(joueur==null)return;
		if(camp==1)joueur.setText("Tour du Joueur 1");//Camp==true
		else joueur.setText("Tour du Joueur 2");//Camp==false
	}
	private void setDescription() {
		if(this.description==null) {
			description=new Description_Box(250);
			description_attaque = new Description_Attaque();
			this.getChildren().addAll(description,description_attaque);
		}
	}
	public void set_EnJeu(Game game,int camp) {
	
		setSelection(game);//Panel de la selection d'une unite allie
		setPA(game);//Initialise le Panel et variable correspondant aux PA
		setTour(game);//Panel des boutons passer son tour et l'affichage du tour du joueur actuel
		setAbandon(game);//Bouton abandon
		setDescription();//Initialise le panel description (class Description_Box)
		setcamp(camp);//Set le camp actuel
	}

	//Observer_selected
	@Override
	public void addObserver(Observer_selected o) {
		observer_selected.add(o);
	}
	public LinkedList<Observer_selected> getObserver() {
		return this.observer_selected;
	}
	@Override
	public void removeObserver(Observer_selected o) {
		observer_selected.remove(o);
	}
	@Override
	public void Notify(int GAME_STATE) {
		for(Observer_selected e: observer_selected) {
			e.update(GAME_STATE);
		}
	}
	//
	//Unite selectionne allie actuelle
	public void setUniteActuelle(Fx_Unite current) {
		uniteActuelle = current;
		if(selection_allie!=null)selection_allie.select();
		if(current==null) {
			setselection_type(-1);
		}
	}
	public void setUniteEnnemieActuelle(Fx_Unite ennemie) {
		this.uniteSelectionnee=ennemie;
	
		if(selection_ennemie!=null)selection_ennemie.select();
	}
	public void setTerrainActuelle(Fx_Terrain terrain,double LayoutX,double LayoutY) {
		if(terrain!=null) {
			Vue.this.description.setDescription(terrain.getImage(),
					terrain.toString(),
					terrain.getSub_text(),
					terrain.getDescription());
			Vue.this.description.entered(LayoutX,LayoutY,terrain.getWidth()/2);
		} else {
			Vue.this.description.exited();
		}
	}
	public Fx_Unite getUniteActuelle() {
		return this.uniteActuelle;
	}
	//Unite selectionne ennemi actuelle
	public void setUniteAttaquee(Fx_Hexagon unite) {
		cible = unite;

		if(uniteActuelle!=null && cible!=null) {
			selection_allie.Notify();
			if(unite.getUnite()!=null && unite.getUnite().getestmort())setUniteEnnemieActuelle(null);
			else if(unite.getUnite()!=null && unite.getUnite().getcamp()!=this.camp)setUniteEnnemieActuelle(unite.getUnite());
		}
		if(this.getUniteActuelle()!=null) {
			selection_allie.select();
		}
		if(this.getuniteSelectionnee()!=null) {
			this.selection_ennemie.select();
		}
		if(unite==null) {
		deselection();
		}
	}
	public Fx_Unite getuniteSelectionnee() {
		return this.uniteSelectionnee;
	}
	public Fx_Hexagon getcible() {
		return this.cible;
	}
	//PA
	public void setPA(int PA) {
		int_PA=PA;
		this.PA.setText("PA: "+int_PA);
	}
	//Camps
	public void setcamp(int b){//Set le camp du joueur
		this.camp=b;
		setCamp();//Set le text du Panel "joueur"
	}
	public int getcamp() {
		return this.camp;
	}
	public int getselection_type() {
		return selection_type;
	}
	public void setselection_type(int i) {
		if(selection_type==0 && i!=0) {
			deplac=null;
		}
		this.selection_type=i;
	}
	
	public Tour getTour() {
		return this.tour;
	}
	
	public Abandon getAbandon() {
		return this.abandon;
	}
	
	public void deselection() {
		
		if(getUniteActuelle()!=null) {
			
			int i=selection_type;
			
			if(i==0) {//deplacement
				for (int m = 0; m < getUniteActuelle().getDeplacement().size(); m++) {
					getUniteActuelle().getDeplacement().get(m).getPolygone().setFill(Fx_Hexagon.getColorC());
				}
			}
			else if(i==1) {
				for (int m = 0; m < getUniteActuelle().getRange().size(); m++) {
					getUniteActuelle().getRange().get(m).getPolygone().setFill(Fx_Hexagon.getColorC());
				}
			}
			else if(i>1 && i<5) {//le i-2 correspond a son indice dans la LinkedList skill de l'uniteActuelle
				for(int m=0;m<getUniteActuelle().getSkill().get(i-2).getCibles().size();m++) {
					getUniteActuelle().getSkill().get(i-2).getCibles().get(m).getPolygone().setFill(Fx_Hexagon.getColorC());
				}
			}

		}
	}
	//Graph
	public void setGraph(LinkedList<Fx_Hexagon.Node> linkedList) {
		graph=linkedList;
	}
	public LinkedList<Fx_Hexagon.Node> getGraph(){
		return graph;
	}
	public boolean isRvr() {
		return rvr;
	}
	public void setRvr(boolean rvr) {
		this.rvr = rvr;
	}
	public boolean isRob1() {
		return rob1;
	}
	public void setRob1(boolean b) {
		rob1=b;
	}
}
