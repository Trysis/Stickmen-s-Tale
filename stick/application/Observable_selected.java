package application;

public interface Observable_selected {
	public static int EN_BOUTIQUE=5;
	public static int EN_JEU=6;
	
	public void Notify(int GAME_STATE);
	
	public void addObserver(Observer_selected o);
	public void removeObserver(Observer_selected o) ;
}
