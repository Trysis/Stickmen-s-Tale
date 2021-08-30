package application;

import controleur.Model_Menu;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
			Model_Menu menu = new Model_Menu(new Menu_J("Stickmen's Tale"),1100,715);
			primaryStage.setMinWidth(menu.getWidth());
			primaryStage.setMinHeight(menu.getHeight());
			primaryStage.setMaxWidth(menu.getWidth());
			primaryStage.setMaxHeight(menu.getHeight());
			primaryStage.setResizable(false);
			primaryStage.setTitle("Stickmen's Tale");
			primaryStage.setScene(menu);
			primaryStage.show();	
	}
	
	public static void main(String[] args) {		
		launch(args);		
	}

}