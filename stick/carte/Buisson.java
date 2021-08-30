package carte;

import unites.Unite;

public class Buisson extends Terrain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	{
		nom=toString();
		description="Permet d'augmenter l'attaque, la defense magique et l'armure de l'unité qui est dessus de 15";
		sub_text="Immediat";
	}
	public Buisson() {
		super();
	}
	public Buisson(int t,int camp) {
		super(t,camp);
	}

	//les buissons permet d'augmenter sa défense et son attaque car l'unité est caché dedans
	@Override
	public void effect(Unite n) {
		n.add_defensemagique(15);//valeur numérique modifiable
		n.add_attaque(15);
		n.add_defensearmore(15);
	}

	@Override
	public void stopeffect(Unite n) {
		n.sub_defensemagique(15);
		n.sub_attaque(15);
		n.sub_defensearmore(15);
	}

	@Override
	public String toString() {
		return "Buisson";
	}

	

}