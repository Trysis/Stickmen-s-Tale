package application;

public interface Observable {

public void addObserver(Observer o);
public void removeObserver(Observer o);
void Notify();

}
