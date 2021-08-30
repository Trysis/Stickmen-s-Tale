package carte;

import java.io.Serializable;

import unites.Unite;


public class Marque extends Terrain implements Serializable{

	{
		nom=toString();
		description="Au bout de 2 tours après que la prophéthie ait été dite, l'unité sur cette case subira 60 dégâts magique";
		sub_text="";
	}
	private static final long serialVersionUID = 1L;
	public Marque(int t, int camp) {
		super(t,camp);
	}
	
	@Override
	public void effect(Unite n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopeffect(Unite n) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Prophétie Apocalyptique";
	}
	public void Endeffect() {
		if(hexa!=null && hexa.getunite()!=null) {
			hexa.getunite().inflige(60,1);
		}
	}
}
