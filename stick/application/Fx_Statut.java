package application;

import java.io.File;
import java.net.MalformedURLException;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class Fx_Statut extends Pane{
	public static String PATH = "src/Ressources/Statut";
	private String chemin;
	private String nom;
	private int nbTour;
	private String description;
	private ImageView icone;
	private Label l;
	
	public Fx_Statut(String n,int nb, String des) {
		this.nom = n;
		this.chemin = PATH+"/"+nom+".png";
		this.nbTour=nb;
		this.description=des;
		
		File file = new File(chemin);
		 
		String localUrl;
		try {
			localUrl = file.toURI().toURL().toString();
			Image image = new Image(localUrl);
			this.icone = new ImageView(image);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		this.icone.setFitHeight(20);
		this.icone.setFitWidth(20);

		l = new Label(des);
		l.setLayoutX(100);
		l.setLayoutY(100);

		this.icone.setOnMouseEntered(e->{
			
			getChildren().add(l);
			
		});
		this.icone.setOnMouseExited(e->{
	
			getChildren().remove(l);

		});
			
	}
	
	public String toString() {
		return this.nom;
	}
	
	public String getDescription() {
		return this.description+ ", reste "+nbTour+" tours.";
	}
	public Label getDescriptionLabel() {
		return new Label(getDescription());
	}
	
	public void set_tours(int n) {
		nbTour=n;
	}
	
	public ImageView getIcon() {
		icone.setCache(true);
		return this.icone;
	}

	public int getToursRestants() {
		return nbTour;
	}
	

}
