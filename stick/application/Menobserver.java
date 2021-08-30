package application;

import java.util.LinkedList;

import application.Fx_Unite.Fx_Skill;

public interface Menobserver {
	public void init_menu();
	public void init_editeur(int x,int y);
	public void init_encyclopedie();
	//
	public void update_menu(LinkedList<HexagonalGrid> list);
	public boolean cree_plateau(String nom,String path,int money,HexagonalGrid grid);
	public void setHexagonal(HexagonalGrid grid,int indice);
	//
	public void setDataUnite(String name, String[] stats, Fx_Skill[] skills_name);
	public void setDataTerrain(String name, String[] descriptions);
	public void setDataStatut(String name, String[] descriptions);
	//
	public void start(HexagonalGrid grid,String p1,String p2);
	public void back();
}
