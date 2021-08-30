package carte;

import java.io.Serializable;
import java.util.LinkedList;


import application.Fx_Unite;
import application.HexagonalGrid;
import application.Observable_selected;


public class Plateau implements Serializable{
	private static final long serialVersionUID = -3245927312431272933L;
	
	private String name;
	private final int height,width;//Taille et Largeur du plateau
	private final Hexagone[][] plateau; //Plateau d'hexagone pour pouvoir pointer les hexagones avec leurs coordonees
	transient private HexagonalGrid Fx_plateau;//Grille de Fx_Hexagon (grille version graphique)
	private LinkedList<Hexagone> departp1=new LinkedList<Hexagone>();//Liste des hexagones de depart du joueur 1
	private LinkedList<Hexagone> departp2=new LinkedList<Hexagone>();//Liste des hexagones de depart du joueur 2
	private LinkedList<Terrain> terrains=new LinkedList<Terrain>();//Liste des terrains present dans le plateau
	//Donnees
	private String id=" ";
	private int money;
	public Plateau(int width,int height,HexagonalGrid hx_grid){
		this.name="Plateau";
		//longueur et largeur du plateau
		this.width=width;
		this.height=height;
		//Initialisation d'un tableau d'hexagones
		plateau=new Hexagone[width][height];
		for(int i=0;i<width;i++) {
			for(int j=0;j<height;j++) {
				plateau[i][j]=new Hexagone(i,j,this);
			}
		}
		if(hx_grid!=null)set_HexagonalGrid(hx_grid);

		boolean pair=true;//les hexagones ont des pointeurs different en fonction de la ligne du tableau surlequel ils sont
		//parcoure le tableau pour attriuer les pointeurs
		for(int y=0;y<height;y++) {
			for(int i=0;i<width;i++) {
				if(pair) {// pour les ligne 0,2,4,6,8 ect
					//set des deux superieurs
					if(y!=0) {//s'il ne sagit pas de la 1ere ligne
						plateau[i][y].sethautdroit(plateau[i][y-1]);
						if(i!=0) {//et s'il ne sagit pas de la 1er colonne
							plateau[i][y].sethautgauche(plateau[i-1][y-1]);
						}
					}
					//set des deux milleux
					if(i<width-1) {//si il ne s'agit pas du dernier de la ligne
						plateau[i][y].setdroit(plateau[i+1][y]);
					}
					if(i!=0) {//s'il ne s'agit pas du 1er de la ligne
						plateau[i][y].setgauche(plateau[i-1][y]);
					}
					//set des deux du bas
					if(y<height-1) {
 						if(i!=0) {
 							plateau[i][y].setbasgauche(plateau[i-1][y+1]);
						}
 						plateau[i][y].setbasdroit(plateau[i][y+1]);
					}
				}
				if(!pair) {//pour les lignes 1,3,5,6 ect
					//set des deux superieurs
					if(y!=0) {
						plateau[i][y].sethautgauche(plateau[i][y-1]);
						if(i+1<width) {
							plateau[i][y].sethautdroit(plateau[i+1][y-1]);
						}
					}
					//set des deux milleux
					if(i<width-1) {
						plateau[i][y].setdroit(plateau[i+1][y]);
					}
					if(i!=0) {
						plateau[i][y].setgauche(plateau[i-1][y]);
					}
					//set des deux du bas
					if(y<height-1) {
						if(i<width-1) {
							plateau[i][y].setbasdroit(plateau[i+1][y+1]);
						}
						plateau[i][y].setbasgauche(plateau[i][y+1]);
					}
				}
			}
			pair=!pair;//le statut de la ligne change d'une ligne a l'autre
		}
	}
	//Getter
	//- Plateau
	public String getID() {
		return this.id;
	}
	public String getNom() {
		return this.name;
	}
	public String getName() {
		return name;
	}
	public int getwidth() {//getter de la largeur du plateau
		return this.width;
	}
	public int getheight() {//getter du la hauteur du plateau
		return this.height;
	}
	public int getMoney() {
		return this.money;
	}
	public LinkedList<Terrain> getterrains() {
		return terrains;
	}
	public Hexagone[][] getplateau(){//getter du plateau d'hexagone en lui meme
		return this.plateau;
	}
	public Hexagone at(int x,int y) {//retourne l'hexagone au coordonnee mis en ordonnee 
		return this.plateau[x][y];
	}
	
	//- HexagonalGrid
	public HexagonalGrid get_HexagonalGrid() {
		return Fx_plateau;
	}
	public Observable_selected getVue() {
		if(Fx_plateau==null)return null;
		return Fx_plateau.getvue();
	}
	public LinkedList<Hexagone> getDepartPlayer(int b){//Renvoi les cases de depart du joueur 1 ou 2 (true/false)
		if(b==1) return this.departp1;
		else return departp2;
	}
	
	//Setter
	public void setName(String name) {
		this.name = name;
	}
	public void setId(String nom) {
		this.id=nom;
	}
	public void setMoney(int money) {
		this.money=money;
	}
	//Deduit le nombre de tour de tous les terrains possedant une limite de tour
	public void tourterrain(int camp) {
		LinkedList<Terrain> asuppr=new LinkedList<Terrain>();

		for(Terrain t:terrains) {
				if(t.getours()!=-1 && t.getcamp()==camp) {		
					t.subtours(1);
					if(t.getours()==0) {
						t.Endeffect();
						asuppr.add(t);
					}
				}
		}
		for(Terrain t: asuppr) {
			if(t.gethexa().getFx()!=null) {
			t.gethexa().getFx().removeFxTerrain();
			}
			t.gethexa().setTerrain(null);
			t.sethexa(null);
			getterrains().remove(t);
		}
		asuppr.clear();
		
	}
	//HexagonalGrid setter
    public void set_HexagonalGrid(HexagonalGrid hx_grid) {
        Fx_plateau=hx_grid;
        Fx_plateau.setName(this.getName());
        Fx_plateau.setId(id);
        if(hx_grid.getX()<=10 || hx_grid.getY()<=10) {
        	Fx_plateau.set_dimension(width, height);//Attribue la longueur et largeur du plateau
        	Fx_plateau.setGrid();//Initialise et set la taille des Fx_Hexagon
        }
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                plateau[i][j].setFX(Fx_plateau.getHexagon(i, j));//Attribution de la partie graphique aux Hexagones attribues
                if(plateau[i][j].getunite() !=null) {
                    plateau[i][j].getunite().getplace().setFX(Fx_plateau.getHexagon(i, j));
                    plateau[i][j].getunite().setGUIUnite(new Fx_Unite(plateau[i][j].getunite().get_Nom(),plateau[i][j].getunite().get_Prix()));
                    plateau[i][j].getunite().getFx_Unite().dessineImage(false);
                    plateau[i][j].getunite().getFx_Unite().setHexagone(Fx_plateau.getHexagon(i, j));
                    Fx_plateau.getHexagon(i, j).setFx_Unite(plateau[i][j].getunite().getFx_Unite());
                }
            }
        }
        //
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                if(plateau[i][j].getunite()!=null) {
                    plateau[i][j].getunite().actualisation();
                    plateau[i][j].getunite().actualise();
                }
            }
        }
        //
        for(int i=0;i<width;i++) {
            for(int j=0;j<height;j++) {
                if(plateau[i][j].getterrain()!=null)plateau[i][j].setTerrain(plateau[i][j].getterrain());
            }
        }
        setActive_Range_Colonne();
    }
	//
	public void setActive_Range_Colonne(int camp) {
		if(camp==1) {//Joueur 1
			for(Hexagone e : this.departp1) {
				e.setPosable(true);//Set les attributs posable des Fx_Hexagon du joueur 1 en true
			}
			for(Hexagone e : this.departp2) {
				e.setPosable(false);//Set les attributs posable des Fx_Hexagon du joueur 2 en false
			}
		}
		else {
			for(Hexagone e : this.departp2) {
				e.setPosable(true);//Set les attributs posable des Fx_Hexagon du joueur 2 en true
			}
			for(Hexagone e : this.departp1) {
				e.setPosable(false);//Set les attributs posable des Fx_Hexagon du joueur 1 en false
			}
		}
		if(Fx_plateau!=null)setActive_Range_Colonne();
	}
	public void setActive_Range_Colonne() {//Applique les modifications faites sur les Hexagones du model a ceux de l'interface graphique
			for(Hexagone e : this.departp1) {
				e.getFx().setPosable(e.getPosable());//Set les attributs posable des Fx_Hexagon du joueur 1 en true
			}
			for(Hexagone e : this.departp2) {
				e.getFx().setPosable(e.getPosable());//Set les attributs posable des Fx_Hexagon du joueur 2 en false
			}
	}
	
	public void setdepart(int x1,int y1,int x2,int y2,int i1,int j1,int i2,int j2) {
		for(int m=x1;m<x2;m++) {
			for(int n=y1;n<y2;n++) {
				departp1.add(this.at(m,n));
			}
		}
		for(int m=i1;m<i2;m++) {
			for(int n=j1;n<j2;n++) {
				departp2.add(this.at(m,n));
			}
		}
	}

}





