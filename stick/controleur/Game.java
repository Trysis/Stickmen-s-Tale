package controleur;

import java.util.LinkedList;

import application.Fx_Boutique;
import application.Fx_Hexagon;
import application.Fx_Unite;
import application.HexagonalGrid;
import application.Menu_J;
import application.Observable_selected;
import application.Observer;
import application.Vue;
import carte.Hexagone;
import carte.Plateau;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;

import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import joueur.*;
import unites.*;

	
public class Game implements Observer {
	private LinkedList<ParallelTransition> Trans=new LinkedList<>();
	private LinkedList<Fx_Hexagon> Cases=new LinkedList<>();
	
	private Vue vue;
	private HexagonalGrid hexa_grid;
	private static Plateau p;
	private static Joueur p1;
	private static Joueur p2;
	private static Menu_J menu_j;
	private Joueur current;
	private Fx_Boutique bout;

	Game(Plateau p,Fx_Boutique bout,String player1, String player2,Menu_J menu_j){
		vue=(Vue)p.getVue();
		Game.p=p;
		Game.menu_j=menu_j;
		
		this.hexa_grid=p.get_HexagonalGrid();
		switch(player1) {
		case "Joueur":p1 = new Joueur(1,p.getMoney(),15);break;
		case "Robot":p1 = new Robot(1,p.getMoney(),15, p, this);break;
		}	
		switch(player2) {
		case "Joueur":p2 = new Joueur(2,p.getMoney(),15);break;
		case "Robot":p2 = new Robot(2,p.getMoney(),15,p,this);break;
		}

		current=p1;	        
		this.bout=bout;	        
		hexa_grid.setObserver(this);	        
		bout.addObserver(this);
		vue.getAbandon().addObserver(this);

			
		vue.Notify(Observable_selected.EN_BOUTIQUE);
		if(p1 instanceof Robot) {
			this.vue.setRob1(true);
			((Robot) p1).set_adversaire(p2);
		}
		if(p2 instanceof Robot)((Robot) p2).set_adversaire(p1);
		if(p1 instanceof Robot && p2 instanceof Robot) {
			setRobots();
		}else if (current instanceof Robot)((Robot) current).instancie();//Cas ou le Robot est joueur 1   	}
	}
	public void setRobots() {
			((Robot) p1).instancie();
			((Robot) p2).instancie();
			this.getVue().setRvr(true);
		}
	public void subPA(int PA) {//Enleve les PA au joueur et met a jour le GUI (interface graphique) PA
		current.sub_PA(PA);//Reduction au joueur
		vue.setPA(current.getPA());//Application sur le GUI
	}
	public int getPa() {
		return current.getPA();
	}
	    
	@Override
	public void updateTours() {
		vue.deselection();
		if(current==p1) {
			current=p2;
		}
		else {
			current=p1;
		}
		current.resetPA();
		p.tourterrain(current.getcamp());
		vue.setcamp(current.getcamp());
		current.applystatus();
		vue.setPA(current.getPA());
		vue.setUniteActuelle(null);
		vue.setUniteEnnemieActuelle(null);
		for(Unite e: current.getUnits()) {
			if(!e.estmort()) {
				e.set_movedone(false);
				e.set_attdone(false);
				e.passtours();
				e.actualisation();
				e.actualise();
			}	
		}
		
		if(current instanceof Robot)((Robot) current).choix();
	}
	   
	  public static void lose() {
  		if(p1.alldead()) {
  			fin(true);
  		}
  
  		if(p2.alldead()) {
  			fin(false);
  		}
  }
  
	    
	    public static void fin(boolean b){
            ///////REALISATION DE LA FIN DU JEU
	    	Pane pane = menu_j.fin(b,p);
	    	((Vue)p.getVue()).getChildren().addAll(pane);
	    }
	   
	    @Override
		   public void updateBoutique(LinkedList<Fx_Unite> l) {
			   if(l.isEmpty()) {
				   this.bout.error(current.getMoney());
				   return;
			   }
			   
			   int currency=0;
			   boolean valide=true;
			   for(Fx_Unite m: l) {
				   m.setX(m.getHexagone().getX());
				   m.setY(m.getHexagone().getY());		   

				   if(!bienplace(m)) {
					   valide=false;
					   break;
				   }
				   int i=getprix(m);
				   if(i==0) {
					   valide=false;
					   break;
				   }
				   else {
					   
					   currency+=i;
					   if(currency>p.getMoney()) {
						   valide=false;
					   }
				   }
			   }
			   
			   LinkedList<Unite> m=null;
			   if(valide)m=instanciation(l);
			   
			   if(m==null) {
				   valide=false;
			   }
			   if(valide) {
				   
				   current.setunites(m);
				   if(current==p1) {
			    		current=p2;
			    		bout.resetboutique(p.getMoney());
						p.setActive_Range_Colonne(current.getcamp());//Met en true les cases posables correspondant au camp du joueur actuel
						vue.Notify(Observable_selected.EN_BOUTIQUE);//Remet a jour les setDrag etc.. des Fx_Hexagon
						if (current instanceof Robot)((Robot) current).instancie();//Cas ou le Robot est joueur 2
				   }
			    	else {
			    		current=p1;
			    		for(Unite la :p1.getUnits()) {
			    			la.actualisation();
			    			la.actualise();
			    		}
			    		for(Unite la :p2.getUnits()) {
			    			la.actualisation();
			    			la.actualise();
			    		}
			    		//Changement du mode de jeu , tous les joueurs ont poses leurs unites
			    		vue.Notify(Observable_selected.EN_JEU);
			    		
			    		//On enleve la boutique
			    		vue.remove_In_VBox(bout);
			    		bout.setVisible(false);//Cache la boutique //N'est deja plus visible apres l'avoir enleve mais on sait jamais
			    		
			    		//Set les nouveaux Panel a ajoute
			    		vue.set_EnJeu(this,current.getcamp());
			    		if(current instanceof Robot)((Robot) current).choix();
			    	}
				   }
			   else {
				   bout.error(current.getMoney());
			   }
		   }
	   public LinkedList<Unite> instanciation(LinkedList<Fx_Unite> op){
		   LinkedList<Unite> army=new LinkedList<Unite>();
		   for(Fx_Unite l: op) {
			   Fx_Hexagon hexagone = l.getHexagone();
			   
			   if(p.at(hexagone.getX(),hexagone.getY())!=null && 
					   p.at(hexagone.getX(),hexagone.getY()).getunite()==null) {
				   try {
					   String r="unites."+l.getnom();
					   
					   Class c=Class.forName(r);	
					   
					   Class[] clas= {Hexagone.class,Fx_Unite.class,int.class};
					   
					   Unite o= (Unite) c.getConstructor(clas).newInstance( p.at(hexagone.getX(),hexagone.getY()), l, current.getcamp());
					   
					   army.add(o);
				   } 
				   catch (Exception e) {
					   e.printStackTrace();
					   return null; 
				   }
			   }
			   else {
				   return null;
			   }
		   }
		   return army;
	   }

	   private boolean bienplace(Fx_Unite a) {
		   Fx_Hexagon hexagone = a.getHexagone();
		   if(hexagone!=null && p.getDepartPlayer(current.getcamp()).contains(p.at(hexagone.getX(),hexagone.getY()))) {
			   return true;
		   }
		   return false;  
	   }
	
	   public int getprix(Fx_Unite l) {
			switch(l.getnom()) {
				case "Guerrier":return Guerrier.get_Prix();
				case "Archer":return Archer.get_Prix();
				case "Chasseur":return Chasseur.get_Prix();
				case "Malade":return Malade.get_Prix();
				case "Fee": return Fee.get_Prix();
				case "BomberMan": return BomberMan.get_Prix();
				case "Faucheuse": return Faucheuse.get_Prix();
				case "Batisseur":return Batisseur.get_Prix();
				case "Druide":return Druide.get_Prix();
				case "Pretresse":return Pretresse.get_Prix();
				case "ChronoMage":return ChronoMage.get_Prix();
				case "Blob":return Blob.get_Prix();	
				case "Conteur":return Conteur.get_Prix();
				case "Assassin":return Assassin.get_Prix();
				default: return 0;
			}
	 }

	   public void updateAttaque(Fx_Unite attaquante,int x, int y) {
		   if(current.getPA()>=2 && attaquante!=null && p.at(x,y)!=null && attaquante.getHexagone()!=null && p.at(attaquante.getHexagone().getX(),attaquante.getHexagone().getY()).getunite()!=null) {

			   if(attaquante==p.at(attaquante.getHexagone().getX(),attaquante.getHexagone().getY()).getunite().getFx_Unite() && p.at(attaquante.getHexagone().getX(),attaquante.getHexagone().getY()).getunite().getcamp()==current.getcamp()) {
				   if(p.at(attaquante.getX(), attaquante.getY()).getunite().attaquer(p.at(x,y)))subPA(2);//Enleve les PA au joueur et met a jour le GUI PA
				   vue.setUniteAttaquee(null);
				   
				   for(Unite u: current.getUnits()) {
					   if(!u.estmort()) {
						   u.actualisation();
						   u.actualise();
					   }
				   }			   
			   }
		   }
	   }
	   
	   public void updateSort(Fx_Unite attaquante,int type,int x,int y) {
		   if(attaquante!=null && p.at(x,y)!=null && attaquante.getHexagone()!=null && p.at(attaquante.getHexagone().getX(),attaquante.getHexagone().getY()).getunite()!=null) {

			   if(attaquante==p.at(attaquante.getHexagone().getX(),attaquante.getHexagone().getY()).getunite().getFx_Unite() && p.at(attaquante.getHexagone().getX(),attaquante.getHexagone().getY()).getunite().getcamp()==current.getcamp()) {				   				

				   int m=p.at(attaquante.getHexagone().getX(),attaquante.getHexagone().getY()).getunite().getSkillList().get(type).cost();//Cout de la competence en PA
				   boolean b=p.at(attaquante.getHexagone().getX(),attaquante.getHexagone().getY()).getunite().getSkillList().get(type).available();//Temps de recuperation de la competence
				   if(current.getPA()-m>=0 && b)
					   if(p.at(attaquante.getHexagone().getX(),attaquante.getHexagone().getY()).getunite().caster(type,p.at(x,y)))subPA(m);				   
				   vue.setUniteAttaquee(null);
				   
				   for(Unite u: current.getUnits()) {
					    if(!u.estmort()) {
						   u.actualisation();
						   u.actualise();		  
					   }
				   }
				   
			   }
			   
		   }
	   }
	   
	   public void updateDeplacement(Fx_Unite n, LinkedList<Fx_Hexagon> m) {
		   Hexagone avant=p.at(m.get(0).getX(),m.get(0).getY());
		   if(avant==null)return;//Verifie que la case de depart n'est pas null
		   Unite unity=avant.getunite();//recupe l'unite dans cette case
		   if(unity==null)return;
		   int depbase=unity.getdep();
		   
		   boolean valide=true;
		   if(depbase+1>=m.size() && unity.getFx_Unite()==n && unity.getmovable() && !unity.getmovedone()) {//Verifie que le nombre de deplacement est cohérent, que son Fx_Unite est coherente avec celle en argument, et qu'elle puisse bouger	    		   
			   for(int i=1;i<m.size();i++) {//On parcours la liste pour
				   Fx_Hexagon a= m.get(i);
				   if(a==null)valide= false;//verifier que les Fx_Hexagon de i a m.size() ne soient pas null 
					   
				   Hexagone pla=p.at(a.getX(),a.getY());
					   
				   if(pla==null || pla.getFx()!=a || !pla.isAutour(avant) || !unity.getdeplacement().contains(pla)) {//On verifie que la case est coherente, que les hexagones soit bien relies entre eux et qu'elle est a portee
					   valide=false;
				   }
				   avant=pla;
			   }
			   if(valide) {//SI TOUT BON
				   vue.deselection();
				   avant=p.at(m.get(0).getX(),m.get(0).getY());
				   for(int i=1;i<m.size();i++) {//Deplacement dans le model
					   Fx_Hexagon a= m.get(i);
					   Hexagone pla=p.at(a.getX(),a.getY());
					   
					   avant.getunite().deplacement(pla);       			         	
					   unity.actualise();
					   avant=pla;
				   }
				   ParallelTransition transition_parent=new ParallelTransition();
				   LinkedList<SequentialTransition> transition_list=new LinkedList<>();
				   
				   transition_list.add(new SequentialTransition(n));
				   double xx=0;
				   double yy=0;

				   for(int i=1;i<m.size();i++) {
					   final int counter=i;

					   Fx_Hexagon a= m.get(counter);
					   TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25));
					   
					   xx+=a.getLayoutX()-m.get(counter-1).getLayoutX();
					   yy+=a.getLayoutY()-m.get(counter-1).getLayoutY();
					   tt.setToX(xx);
					   tt.setToY(yy);
					   
					   transition_list.get(0).getChildren().add(tt);	
				   }//fin boucle
				   xx=0;
				   yy=0;		
				   if(n.getOthers()!=null) {
					   for(int j=0;j<n.getOthers().size();j++) {
						   SequentialTransition  transition = new SequentialTransition (n.getOthers().get(j));
						   transition_list.add(transition);
						   for(int i=1;i<m.size();i++) {
							   final int counter=i;

							   Fx_Hexagon a= m.get(counter);
							   TranslateTransition tt = new TranslateTransition(Duration.seconds(0.25));
							   
							   xx+=a.getLayoutX()-m.get(counter-1).getLayoutX();
							   yy+=a.getLayoutY()-m.get(counter-1).getLayoutY();
							   tt.setToX(xx);
							   tt.setToY(yy);
							   
							   transition.getChildren().add(tt);	
						   }//fin boucle
					   }
				   }
				   for(SequentialTransition tmp: transition_list) {
					   transition_parent.getChildren().add(tmp);
				   }
				   transition_parent.play();
				   if(m.getLast().hasFx_Unit() && m.getLast().getUnite().principal()!=n.principal()){
					   transition_parent.pause();

					   Cases.add(m.getLast());
					   Trans.add(transition_parent);			   
				   }
					   //DEBUT FINISHED
				   transition_parent.setOnFinished(e->{
						   n.setTranslateX(0);
						   n.setTranslateY(0);
						   if(n.getOthers()!=null) {
							   for(Fx_Unite tmp: n.getOthers()) {
								   tmp.setTranslateX(0);
								   tmp.setTranslateY(0);
							   }
						   }
						   if(m.get(0).getUnite()!=null && m.get(0).getUnite().equals(n)) {
							   m.get(m.size()-1).setFx_Unite(n);
							   n.setHexagone(m.get(m.size()-1));
							   m.get(0).removeFx_Unite();

							   for(int ah=0;ah<Cases.size();ah++) {
								   if(!Cases.get(ah).hasFx_Unit()) {
									   Trans.get(ah).play();
									   Cases.remove(ah);
									   Trans.remove(ah);
									   ah--;
								   }
							   }
						   }
						   
							   Task<Void> sleeper = new Task<Void>() {
						            @Override
						            protected Void call() throws Exception {
						            	try {
						            		Thread.sleep(3000);
						            		if(Trans.size()==1) {
							            		Trans.get(0).play();
					            				Cases.remove(0);
					            				Trans.remove(0);
						            		}						           	
						            		
						            	} catch (InterruptedException e) {}
						            	return null;
						            }
						        };
						        new Thread(sleeper).start();
						   
						    if(current instanceof Robot) {
		            			if(!Game.this.vue.isRvr())Game.this.updateTours();
						   }
					   });		
				   //Hors setOnFinished
				   for(Unite e: current.getUnits()) {//initialise
					   if(!e.estmort()) {
						   e.actualisation();
						   e.actualise();
					   }
				   }
				   vue.setselection_type(-1);
				   unity.set_movedone(true);
				   unity.actualisation();
				   unity.actualise();
				   return ;
			   }
		   }
		   //Ca n'a pas marche -> on recolore et termine 
		   vue.deselection();
	   }//Fin update deplacement
	   
	@Override
	public void updateAbandon() {
		vue.setVisible(false);
		menu_j.backAbandon();
	}	
	public static LinkedList<Unite> getarmy(int b){
		if(b==1) {
			return p1.getUnits();
		}
		else {
			return p2.getUnits();
		}
	}
	public static LinkedList<Unite> getAll(){
		LinkedList<Unite> lol=new LinkedList<Unite>();
		lol.addAll(p1.getUnits());
		lol.addAll(p2.getUnits());
		return lol;
	}
	public static Joueur getPlayer(int camp) {
		if(camp==1) {
			return p1;
		}
		if(camp==2) {
			return p2;
		}
		return null;
	}
	public Vue getVue() {
		return this.vue;
	}		 
}