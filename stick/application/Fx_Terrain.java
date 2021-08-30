package application;

import java.io.File;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Fx_Terrain extends Canvas {
	protected static String PATH="src/Ressources/Terrains";
	protected String nom;
	protected String description;
	protected String sub_text;
	protected int x,y;
	protected int type;
	
	protected Color couleur;//ou la texture apres
	protected Image image;
	
	public Fx_Terrain(String n) {
		this.nom=n;
		switch(nom) {
		case "Poison":couleur = Color.DARKMAGENTA;break;
		case "Campement":couleur = Color.PINK;break;
		case "Buisson":couleur = Color.LIMEGREEN;break;
		case "Goudron":couleur = Color.DODGERBLUE;break;
		case "Trap":couleur = Color.GREY;break;
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Color getCouleur() {
		return couleur;
	}

	public void setPoints() {
		this.setOpacity(1);
		
		setImage(nom);
	}
	//
	public void setNom(String n) {//Nom de l'unite
		this.nom=n;
	}
	public void setDescription(String description) {
		this.description=description;
	}
	public void setSubText(String sub_text) {
		this.sub_text=sub_text;
	}
	//
	public void setImage(Image image) {
		this.image=image;
	}
	public void setImage(String NOM_IMAGE) {
        NOM_IMAGE+=".png";
        GraphicsContext g=this.getGraphicsContext2D();
        try {
            image=new Image(new File(PATH+"/"+NOM_IMAGE).toURI().toURL().toString());
            g.drawImage(image,0,0, this.getWidth(), this.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	@Override
	public String toString() {
		return this.nom;
	}
	public String getDescription() {
		return this.description;
	}
	public String getSub_text() {
		return this.sub_text;
	}
	public Image getImage() {
		return image;
	}
	

}
