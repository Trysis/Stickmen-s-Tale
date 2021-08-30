package application;

import java.util.LinkedList;

public interface Observer {

public void updateDeplacement(Fx_Unite n,LinkedList<Fx_Hexagon> m);
public void updateTours();
public void updateBoutique(LinkedList<Fx_Unite> l);
public void updateAttaque(Fx_Unite attaquante,int x,int y);
public void updateSort(Fx_Unite attaquante, int type, int x, int y);
public void updateAbandon();


}
