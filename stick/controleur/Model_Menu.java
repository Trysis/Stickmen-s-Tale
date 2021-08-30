package controleur;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;

import Statut.Statut;
import application.Fx_Boutique;
import application.Fx_Unite;
import application.Fx_Unite.Fx_Skill;
import application.HexagonalGrid;
import application.Menobserver;
import application.Menu_J;
import application.Vue;
import carte.Hexagone;
import carte.Plateau;
import carte.Terrain;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import unites.Obstacle;
import unites.Unite;

public class Model_Menu extends Scene implements Menobserver{
	private static String PATH="src/Ressources/Carte";
	private static String PATH_MAP="src/Ressources/Map";
	private double bords_haut=50;
	private Menu_J men;
	private LinkedList<Plateau> lvl=new LinkedList<>();

	public Model_Menu(Menu_J men,double WIDTH,double HEIGHT){
		super(men,WIDTH,HEIGHT);
		this.men=men;
		men.set(WIDTH,HEIGHT);
		men.addObserver(this);
	}
	@Override
	public void init_menu(){//
		new File(PATH).mkdir();
		men.start_selection();
	}
	@Override
	public void init_editeur(int x,int y) {
		men.start_editeur(x, y);
	}
	@Override
	public void init_encyclopedie() {
		men.start_encyclopedie();
	}
	@Override
	public void back() {
		lvl.clear();
		men.back();
	}
	@Override
	public void update_menu(LinkedList<HexagonalGrid> list) {
		//Niveau de base
		//create(0);
		deserialise();
		
		//Ajoute dans la liste des niveaux tous les plateaux serialises
		for(int i=0;i<lvl.size();i++) {
			HexagonalGrid grille=new HexagonalGrid(getWidth(),getHeight()-bords_haut);
			list.add(grille);
		}
	}
	@Override
	public void setHexagonal(HexagonalGrid grid,int indice) {
		lvl.get(indice).set_HexagonalGrid(grid);
	}
	public void create(String id) {//Id == valeur de l'image attribue au plateau
		
	
	}
	public static void serialise(Plateau p) {//Ecrase les fichiers dont les plateaux sont de noms identiques
		File source=new File(PATH);
		
		if(!source.exists())return;
		try {
	         FileOutputStream fileOut = new FileOutputStream(PATH+"/"+p.getName());//Prend le nom du plateau
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(p);
	         out.close();
	         fileOut.close();
	      } catch (IOException i) {
	         i.printStackTrace();
	      }
	}

	public void deserialise(){
		File source=new File(PATH);
		if(!source.exists())return;
		for(int i=0; i<source.list().length;i++) {
		try {
	         FileInputStream fileIn = new FileInputStream(PATH+"/"+source.list()[i]);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         Plateau p = (Plateau) in.readObject();
	         lvl.add(p);
	         in.close();
	         fileIn.close();
	      } catch (IOException io) {
	         io.printStackTrace();
	      } catch (ClassNotFoundException c) {  
	         c.printStackTrace();
	      }
		}
	}
	public Plateau deserialise(String nom){
		File source=new File(PATH);
		if(!source.exists())return null;
		for(int i=0; i<source.list().length;i++) {
		try {
	         FileInputStream fileIn = new FileInputStream(PATH+"/"+source.list()[i]);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         Plateau p = (Plateau) in.readObject();
	         if(p.getNom().equals(nom)) {
		         in.close();
		         fileIn.close();
	        	 return p;
	         }
	         in.close();
	         fileIn.close();
	      } catch (IOException io) {
	         io.printStackTrace();
	      } catch (ClassNotFoundException c) {
	         c.printStackTrace();
	      }
		}
		return null;
	}
	@Override
	public boolean cree_plateau(String nom,String path,int money,HexagonalGrid grid) {
		//Verifications des arguments
		if(!nom.matches("[\\w-]+"))return false;
		if(money<500)return false;
		File file=new File(path);//Path de l'image
		if(!file.isFile())return false;
		//Attribution de l'ID
		int occ=1;int occ_name=0;
		File file_map=new File(PATH_MAP);
		File[] list = file_map.listFiles();
		
		for(File tmp: list) {
			if(tmp.isFile()) {
				String n=tmp.getName().replaceFirst("[.][^.]+$", "");
				if(n.matches("[0-9]+"))occ+=Integer.parseInt(n);
			}
		}
		deserialise();
		for(Plateau tmp: lvl)if(tmp.getName()==nom)occ_name++;
		lvl.clear();
		if(occ_name!=0)nom=nom+"("+occ_name+")";
		//
		//
		int x=grid.getX();
		int y=grid.getY();

		Plateau p=new Plateau(x,y,null);
		for(int i=0;i<x;i++) {
			for(int j=0;j<y;j++) {
				if(grid.getHexagon(i, j).getPosable()) {
					int dep=0;
					if(grid.getHexagon(i, j).getCamp())dep=1;
					else dep=2;
					p.getDepartPlayer(dep).add(p.at(i,j));
				}
				if(grid.getHexagon(i,j).getTerrain()!=null) {
					try {
						String r="carte."+grid.getHexagon(i,j).getTerrain().toString();							   
						Class<?> c=Class.forName(r);
						Class<?>[] cla= new Class[]{};//Constructeur de Terrain
						Constructor<?> ctor= c.getConstructor(cla);
						Terrain t=(Terrain) ctor.newInstance();
						//Veillez à rajouter les version des constructeur dans chaque class Terrain enfant -> a faire
						p.at(i,j).setTerrain(t);
					} 
					catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
				if(grid.getHexagon(i,j).getUnite()!=null) {
					try{
						String unit=grid.getHexagon(i,j).getUnite().toString();
						unit="Obstacle";

						String r="unites."+unit;							   
						Fx_Unite n=grid.getHexagon(i,j).getUnite();
						Class<?> c=Class.forName(r);
						Class<?>[] clas= {String.class,int.class,int.class,int.class,Hexagone.class,Fx_Unite.class};
						Constructor<?> ctor= c.getConstructor(clas);
						switch(n.getnom()){
						case "Arbre":Obstacle t=(Obstacle) ctor.newInstance(n.getnom(),50,10,0,p.at(i,j),n);p.at(i, j).setunite(t);break;
						case "Roche":;Obstacle a=(Obstacle) ctor.newInstance(n.getnom(),75,20,50,p.at(i,j),n);p.at(i, j).setunite(a);break;
						case "Pillier":;Obstacle b=(Obstacle) ctor.newInstance(n.getnom(),40,5,35,p.at(i,j),n);p.at(i, j).setunite(b);break;
						case "Statue":Obstacle d=(Obstacle) ctor.newInstance(n.getnom(),22,20,100,p.at(i,j),n);p.at(i, j).setunite(d);break;
						case "Barrière":Obstacle m=(Obstacle) ctor.newInstance(n.getnom(),10,10,15,p.at(i,j),n);p.at(i, j).setunite(m);break;
						}
					} 
					catch (Exception e) {
						e.printStackTrace();
						return false;					   
					}
				}
			}
		}
		p.setName(nom);
		p.setMoney(money);
		p.setId(occ+"");
		p.set_HexagonalGrid(grid);
		p.setActive_Range_Colonne(1);
		//
		SetFondcarte(path,p);
		Model_Menu.serialise(p);//Pas besoin de creat car id du plateau est set dans le constructeur pas incrémentation d'une variable static
		back();
		return true;
	}
	@Override
	public void setDataUnite(String name,String[] stats,Fx_Skill[] skills) {
		String r="unites."+name;							   
		try {
			Class<?> c=Class.forName(r);
			Class<?>[] clas= {};
			Constructor<?> ctor= c.getConstructor(clas);
			Unite t= (Unite) ctor.newInstance();
			//Initialisation des stats
			stats[0]=Integer.toString(t.get_pvMax());//pv
			stats[1]=Integer.toString(t.get_attaque());//attaque
			stats[2]=Integer.toString(t.get_defensearmor());//armure
			stats[3]=Integer.toString(t.get_defensemagique());//armure magique
			stats[4]=Integer.toString(t.getdep());//points de mouvement
			stats[5]=Integer.toString(t.get_portee());//portee
			//
			for(int i=0;i<t.getSkillList().size();i++) {
				skills[i]=new Fx_Skill(t.getSkillList().get(i).getName(),t.getSkillList().get(i).description(),
						t.getSkillList().get(i).cost(),t.getSkillList().get(i).getCd(),t.getSkillList().get(i).getCdMax(),t.getSkillList().get(i).available());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public void setDataTerrain(String name,String[] descriptions) {
		String r="carte."+name;							   
		try {
			Class<?> c=Class.forName(r);
			Class<?>[] clas= {};
			Constructor<?> ctor= c.getConstructor(clas);
			Terrain t= (Terrain) ctor.newInstance();
			//Initialisation des stats
			descriptions[0]=t.getSub_Text();//sous texte (petits details informatifs)
			descriptions[1]=t.getDescription();//description du terrain (informe sur ses effets)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	public  void setDataStatut(String name,String[] descriptions) {
		String r="Statut."+name;
		try {
			Class<?> c=Class.forName(r);
			Class<?>[] clas= {int.class,Unite.class};
			Constructor<?> ctor= c.getConstructor(clas);
			Statut t= (Statut) ctor.newInstance(-1,null);
			//Initialisation des stats
			descriptions[0]=t.toString();//sous texte (petits details informatifs)
			descriptions[1]=t.description();//description du statut (informe sur ses effets)
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void SetFondcarte(String path,Plateau p) {
		File a=new File(PATH_MAP+"/"+p.getID()+".png");
		File b=new File(path);
		try {
			Files.copy(b.toPath(),a.toPath(),StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			//Fonction qui renvoie erreur dans le chargement d'une image
			e.printStackTrace();
		}
	}
	
	@Override
	public void start(HexagonalGrid grid,String p1,String p2) {
		Plateau tmp=null;

		for(Plateau t: lvl)if(t.get_HexagonalGrid()!=null)if(t.get_HexagonalGrid().equals(grid))tmp=t;
		
		Plateau bis=deserialise(tmp.getNom());
		if(bis==null)bis=tmp;
		
		Vue vue=new Vue(getWidth(),getHeight());
		Fx_Boutique boutique=new Fx_Boutique(getWidth(),getHeight()-(bords_haut+grid.getHeight()),tmp.getMoney());

		men.start(vue);

		vue.setLayoutY_VBox(bords_haut);
		bis.set_HexagonalGrid(new HexagonalGrid(getWidth(),getHeight()-bords_haut));
		bis.get_HexagonalGrid().set_Observable(vue);

		vue.addPane_VBox(bis.get_HexagonalGrid(),boutique);
		new Game(bis,boutique,p1,p2,men);
	}
	public LinkedList<Plateau> getlvl() {
		return lvl;
	}
}
