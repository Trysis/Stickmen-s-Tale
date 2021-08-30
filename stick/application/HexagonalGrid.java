package application;

import javafx.scene.Group;
import javafx.scene.paint.Color;

public class HexagonalGrid extends Group{//Classe representant une grille de cases (version graphique)
	protected String name;
	protected Fx_Hexagon[][] grille; //Version graphique du plateau
	protected Fx_Hexagon ref;//Hexagone de reference
	
	protected Observer obs;//
	protected Observable_selected vue;
	protected double width,height;//Taille version graphique de Panel
	protected int larg,haut;//Taille du plateau en x;y
	
	public HexagonalGrid(double width,double height){//La taille des hexagones dependront de la longueur/hauteur
		name="Carte";
		this.ref=new Fx_Hexagon(Color.BLACK);//Case hexagonal (graphique) de reference
		this.width=width;
		this.height=height;//Fait 3/4 de sa taille (a ete calcule mathematiquement)
	}
	public Fx_Hexagon getHexagon(int i,int j) {
		return getGrille()[i][j];
	}

	public void set_Observable(Observable_selected vue) {
		this.vue=vue;
		for(int i=0;i<getGrille().length;i++) {
			for(int j=0;j<getGrille()[i].length;j++) {
				vue.addObserver(getGrille()[i][j]);
				getGrille()[i][j].setVue(vue);
			}
		}
	}
	public void setObserver(Observer o) {
		this.obs=o;
		for (int i = 0; i < getGrille().length; i++) {
			for (int j = 0; j < getGrille()[i].length; j++) {
				getGrille()[i][j].addObserver(obs);
			}
		}
	}
		//Set
	public void setGrid() {

		boolean pair=true;
		for(int y=0;y<haut;y++) {
			for(int i=0;i<larg;i++) {
				if(pair) {// pour les ligne 0,2,4,6,8 ect
					//set des deux superieurs
					if(y!=0) {//s'il ne sagit pas de la 1ere ligne
						grille[i][y].sethautdroit(grille[i][y-1]);
						if(i!=0) {//et s'il ne sagit pas de la 1er colonne
							grille[i][y].sethautgauche(grille[i-1][y-1]);
						}
					}
					//set des deux milleux
					if(i<larg-1) {//si il ne s'agit pas du dernier de la ligne
						grille[i][y].setdroit(grille[i+1][y]);
					}
					if(i!=0) {//s'il ne s'agit pas du 1er de la ligne
						grille[i][y].setgauche(grille[i-1][y]);
					}
					//set des deux du bas
					if(y<haut-1) {
 						if(i!=0) {
 							grille[i][y].setbasgauche(grille[i-1][y+1]);
						}
 						grille[i][y].setbasdroit(grille[i][y+1]);
					}
				}
				if(!pair) {//pour les lignes 1,3,5,6 ect
					//set des deux superieurs
					if(y!=0) {
						grille[i][y].sethautgauche(grille[i][y-1]);
						if(i+1<larg) {
							grille[i][y].sethautdroit(grille[i+1][y-1]);
						}
					}
					//set des deux milleux
					if(i<larg-1) {
						grille[i][y].setdroit(grille[i+1][y]);
					}
					if(i!=0) {
						grille[i][y].setgauche(grille[i-1][y]);
					}
					//set des deux du bas
					if(y<haut-1) {
						if(i<larg-1) {
							grille[i][y].setbasdroit(grille[i+1][y+1]);
						}
						grille[i][y].setbasgauche(grille[i][y+1]);
					}
				}
			}
			pair=!pair;//le statut de la ligne change d'une ligne a l'autre
		}
		double posX=ref.getLayoutX();//Position en x
		double posY=ref.getLayoutY();//Position en y
		
		double H_width=(width-posX*2)/larg;//Longueur d'un hexagone selon la longueur et le nombre d'hexagones en x
		H_width=(width-H_width/2-posX*2)/larg;
		double H_height=(height-posY)/haut;//Hauteur d'un hexagone selon la hauteur et le nombre d'hexagones en y
		
		//Debut de boucle pour initialiser le plateau version graphique
		for(int i=0;i<larg;i++) {
			for(int j=0;j<haut;j++) {
				if(j%2==1)posX+=H_width/2;//Position en x selon que j pair ou impair
				else posX=ref.getLayoutX()+i*H_width;//Idem -
				
				//Ajout ici :
				//Set chaque case de la grille
				getGrille()[i][j].setX(i);//Valeur x correspondant au plateau
				getGrille()[i][j].setY(j);//Valeur y correspondant au plateau
				
				getGrille()[i][j].setWidth(H_width);//Taille du Fx_Hexagon en longueur
				getGrille()[i][j].setHeight(H_height);//Taille du Fx_Hexagon en hauteur
				
				getGrille()[i][j].setLayoutX(posX);//Positionnement en x sur l'ecran
				getGrille()[i][j].setLayoutY(posY);//Positionnement en y sur l'ecran
				
				getGrille()[i][j].setHexagone();//Redessine le polygone base sur sur sa hauteur/largeur et position
				
				posY+=H_height-H_height/4;//Des maths pour avoir des Hexagones bien positionnees en hauteur
			}
			posY=ref.getLayoutY();
			posX+=H_width;
		}
	}
	//Getter
	public String getName() {
		return name;
	}
	public double getWidth() {//Renvoi la taille du Panel en width
		if(ref!=null)width+=ref.getWidth();
		return width;
	}
	public double getHeight() {//Renvoi la taille du Panel en height
		return (height*3.0)/4.0;//Taille reelle d'un plateau d'hexagones pointant nord
	}
	public int getX() {
		return larg;
	}
	public int getY() {
		return haut;
	}
	public Fx_Hexagon[][] getGrille() {//Renvoi la grille
		return grille;
	}
	public Observable_selected getvue() {
		return vue;
	}
	//Setter
	public void setName(String name) {
		this.name=name;
	}
	public void set_dimension(int x,int y) {//
		this.larg=x;
		this.haut=y;
		setGrid_FxHexagon();
	}
	protected void setGrid_FxHexagon() {
		if(grille!=null) {
			for(int i=0;i<grille.length;i++) {
				for(int j=0;j<grille[i].length;j++) {
					getChildren().remove(grille[i][j]);
				}
			}
		}
		grille=new Fx_Hexagon[larg][haut];//Initialisation grille d'hexagones (graphique)
		for(int i=0;i<larg;i++) {
			for(int j=0;j<haut;j++) {
				grille[i][j]=new Fx_Hexagon(ref.getColor_Bordure());
				this.getChildren().add(grille[i][j]); //Sans texte
			}
		}
	}
	public void set_dimension_gui(double x,double y) {
		this.width=x;
		this.height=y;
	}
	public void setGrille(Fx_Hexagon[][] grille) {//
		this.grille = grille;
	}
}