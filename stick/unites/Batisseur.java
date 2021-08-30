package unites;

import java.util.LinkedList;
import Statut.Root;
import Statut.Stun;
import application.Fx_Unite;
import carte.*;

public class Batisseur extends Unite implements Unite.Range{
	LinkedList<Terrain> creation=new LinkedList<Terrain>();
	LinkedList<Unite> wall=new LinkedList<Unite>();
	public Batisseur( Hexagone place, Fx_Unite Unite,int camp) {
		super("Batisseur", 4, 200, 15, 25,25, 3, place, Unite, camp);
		// TODO Auto-generated constructor stub
		set_Skill(new Terraforming(),new Wall(), new Fracture_sismique());
	}
	public Batisseur() {
		this(null,null,-1);
	}
	private static final long serialVersionUID = 1L;
	public String[] getStats() {
		
		stats[6]="Nombres de Terrains: "+creation.size();
		return super.getStats();
	}
	class Terraforming extends Skill implements Skill.NouveauTerrain{
		String type;//Type de terrain selectionne
		int rand=(int) Math.floor(Math.random()*4);
		{
			switch(rand){
			case 0:type="Buisson";break;
			case 1:type="Campement";break;
			case 2:type="Goudron";break;
			case 3:type="Poison";break;		
			default:type="Poison"; break;
			}
			name="Teraforming";
			description="Crée un terrain du type indiqué qui dure 3 tours, il change à chaque tour. "
					+ "Les cases possédant déjà un terrain seront tout de même modifiés (3 terrain maximum)[ Terrain actuel :"+type+" ]";
			sub_description="";
			cout=3;
			cdmax=0;
		}
		@Override
		public String getName() {
			if(creation.size()<3)return name;
			return "Teraforming Indisponible";
		}
		@Override
		public String description() {
			if(creation.size()<3)return "Crée un terrain du type indiqué qui dure 3 tours, il change à chaque tours. "
					+ "Les cases possedant déjà un terrain seront tout de même modifiés (3 terrain maximum)[ Terrain actuel :"+type+" ]";;
			return "Tous les terrains de se batisseur ont été placés, attendez qu'ils se detruisent";
		}
		
		@Override
		public void use(Hexagone n) {
			if(!available())return;
			if(creation.size()<3) {
				if(n.getterrain()!=null) {
				
					if(n.getterrain().getcamp()==1 || n.getterrain().getcamp()==2) {
						n.getpla().getterrains().remove(n.getterrain());
					}
					n.getterrain().sethexa(null);
					n.getFx().removeFxTerrain();
					n.setTerrain(null);
					Terrain t=null;
					switch(type) {
					case "Buisson":t=new Buisson(3, camp);break;
					case "Campement":t=new Campement( 3, camp);break;
					case "Goudron":t=new Goudron( 3, camp);break;
					case "Poison":t=new Poison( 3, camp);break;
					default:t=new Buisson( 3, camp);break;
					}
					n.setTerrain(t);
					creation.add(t);
				
				}
				else {
					
					Terrain t=null;
					switch(type) {
					case "Buisson":t=new Buisson( 3, camp);break;
					case "Campement":t=new Campement( 3, camp);break;
					case "Goudron":t=new Goudron( 3, camp);break;
					case "Poison":t=new Poison( 3, camp);break;
					default:t=new Buisson( 3, camp);break;
					}
					n.setTerrain(t);
					creation.add(t);
				}
				
			}
		}
		@Override
		public boolean available() {
			return creation.size()<3 && cd==0;
		}
		@Override
		public void tours() {
			for(int i=0;i<creation.size();i++) {
				if(creation.get(i).gethexa()==null) {
					creation.remove(i);
					i--;
				}
			}
			rand=(int) Math.floor(Math.random()*4);
			
			switch(rand){
				case 0:type="Buisson";break;
				case 1:type="Campement";break;
				case 2:type="Goudron";break;
				case 3:type="Poison";break;		
				default:type="Poison"; break;
			}
			if(cd>0)cd--;//Au cas ou il y aurait des competences augmentant le cd
			setrange();
			actualisation();
			actualise();
		}

		@Override
		public void setrange() {
			// TODO Auto-generated method stub
			LinkedList<Hexagone> depla=new LinkedList<Hexagone>();
			depla.add(place);	
			for(int i=0;i<5;i++) {
				int pop=depla.size();
				for(int o=0;o<pop;o++) {
					depla=depla.get(o).add_autour_withunits(depla);
				}
			}
			depla.remove(place);
			rang=depla;
		}
	}
	
	
	class Wall extends Skill implements Skill.Defensif{
		LinkedList<Hexagone> coteg=new LinkedList<Hexagone>();
		LinkedList<Hexagone> coted=new LinkedList<Hexagone>();
		LinkedList<Hexagone> cotegh=new LinkedList<Hexagone>();
		LinkedList<Hexagone> cotedh=new LinkedList<Hexagone>();
		LinkedList<Hexagone> cotegb=new LinkedList<Hexagone>();
		LinkedList<Hexagone> cotedb=new LinkedList<Hexagone>();
		//
		private String nom_invocation="Palissade";
		private int sub_cd=3;//cd de la 2eme competence
		private int portee=4;
		private int tours=3;//Nombre de tour de l'application du statut
		{
			name="Rempart de fortune";
			description="Crée un mur de pallissades en V possedant 50 pdv chacun, les ennemis sur lesquels les obstacles sont construits sont repoussés en arrière,"
					+ " s'ils ne peuvent pas ils sont stun pendant "+tours+" tour(s)";
			sub_description="";
			cout=6;
			cdmax=6;
		}
		//Debut classe interne
		class Palissade extends Unite{
			Batisseur bat;
			public Palissade( Hexagone place,Fx_Unite Unite,Batisseur bat) {
				super("Palissade", 0, 50, 0, 15,15, 0, place, Unite, -1);
				this.bat=bat;
				actualise();
			}

			private static final long serialVersionUID = 1L;
			

			@Override
			protected void degats_adversaire(Hexagone n) {
			}

			@Override
			public void actu_range() {
			}

			@Override
			void actu_dep(int dep) {
			}
			void dcd() {
				if(!estmort()) {
					this.mort=true;
					this.place.setunite(null);
					this.place=null;
					bat.getwall().remove(this);
				}
				actualise();
			}

			@Override
			protected String description_attaque() {
				return "";
			}

			@Override
			protected String description_encyclopedie() {
				return "Obstacle";
			}

			@Override
			protected String passif() {
				return "Aucun passif";
			}
		}//Fin classe interne
		@Override
		public String getName() {
			if(getwall().size()!=0)return "Destruction du rempart";
			return name;
		}

		@Override
		public String description() {
			if(getwall().size()!=0) return "Detruit le mur en place";
			return description;
		}
		@Override
		public int cost() {
			if(getwall().size()!=0)return 2;//
			return cout;
		}
		public void use(Hexagone n) {
			if(available() && rang.contains(n)) {
				if(getwall().size()!=0) {
					while(wall.size()!=0) {
						wall.get(0).set_pv(0);
					}
					cd=sub_cd;
				}
				else {
					if(coteg.contains(n)) {
						if(n.getunite()==null) {
							Fx_Unite m=new Fx_Unite(n.getFx());
							getwall().add(new Palissade(n,m,Batisseur.this));
							m.setNom(nom_invocation);
							m.dessineImage(false);
						}
						else {
							n.getunite().add_Statut(new Stun(tours, n.getunite()));
						}
						if(n.getbasdroit()!=null) {
							if(n.getbasdroit().getunite()==null) {
								Fx_Unite m=new Fx_Unite(n.getbasdroit().getFx());
								getwall().add(new Palissade(n.getbasdroit(),m,Batisseur.this));
								m.setNom(nom_invocation);
								m.dessineImage(false);
							}
							else {
								n.getbasdroit().getunite().add_Statut(new Stun(tours, n.getbasdroit().getunite()));
							}
						}
						if(n.gethautdroit()!=null) {
							if(n.gethautdroit().getunite()==null) {
								Fx_Unite m=new Fx_Unite(n.gethautdroit().getFx());
								getwall().add(new Palissade(n.gethautdroit(),m,Batisseur.this));
								m.setNom(nom_invocation);
								m.dessineImage(false);
							}
							else {
								n.gethautdroit().getunite().add_Statut(new Stun(tours, n.gethautdroit().getunite()));
							}
						}
					}
					else {
						if(coted.contains(n)) {
							if(n.getunite()==null) {
								Fx_Unite m=new Fx_Unite(n.getFx());
	
								getwall().add(new Palissade(n,m,Batisseur.this));
								m.setNom(nom_invocation);
								m.dessineImage(false);
							}
							else {
								n.getunite().add_Statut(new Stun(tours, n.getunite()));
							}
							if(n.getbasgauche()!=null) {
								if(n.getbasgauche().getunite()==null) {
									Fx_Unite m=new Fx_Unite(n.getbasgauche().getFx());
									
									getwall().add(new Palissade(n.getbasgauche(),m,Batisseur.this));
									m.setNom(nom_invocation);
									m.dessineImage(false);
								}
							}
							else {
								n.getbasgauche().getunite().add_Statut(new Stun(tours, n.getbasgauche().getunite()));
							}
							
							if(n.gethautgauche()!=null) {
								if(n.gethautgauche().getunite()==null) {
									Fx_Unite m=new Fx_Unite(n.gethautgauche().getFx());
									
									getwall().add(new Palissade(n.gethautgauche(),m,Batisseur.this));
									m.setNom(nom_invocation);
									m.dessineImage(false);
								}
							}
							else {
								n.gethautgauche().getunite().add_Statut(new Stun(tours, n.gethautgauche().getunite()));
							}
						}
					/////
						else {
							if(cotegh.contains(n)) {
								if(n.getunite()==null) {
									Fx_Unite m=new Fx_Unite(n.getFx());
									
									getwall().add(new Palissade(n,m,Batisseur.this));
									m.setNom(nom_invocation);
									m.dessineImage(false);
								}
								else {
									n.getunite().add_Statut(new Stun(tours, n.getunite()));
								}
								
								
								if(n.getbasgauche()!=null) {
									if(n.getbasgauche().getunite()==null) {
										Fx_Unite m=new Fx_Unite(n.getbasgauche().getFx());
										
										getwall().add(new Palissade(n.getbasgauche(),m,Batisseur.this));
										m.setNom(nom_invocation);
										m.dessineImage(false);
									}
								}
								else {
									n.getbasgauche().getunite().add_Statut(new Stun(tours, n.getbasgauche().getunite()));
								}
								if(n.getdroit()!=null) {
									if(n.getdroit().getunite()==null) {
										Fx_Unite m=new Fx_Unite(n.getdroit().getFx());
										
										getwall().add(new Palissade(n.getdroit(),m,Batisseur.this));
										m.setNom(nom_invocation);
										m.dessineImage(false);
									}
								}
								else {
									n.getdroit().getunite().add_Statut(new Stun(tours, n.getdroit().getunite()));
								}
							}
							else {
								if(cotedh.contains(n)) {
									if(n.getunite()==null) {
										Fx_Unite m=new Fx_Unite(n.getFx());
										
										getwall().add(new Palissade(n,m,Batisseur.this));
										m.setNom(nom_invocation);
										m.dessineImage(false);
									}
									else {
										n.getunite().add_Statut(new Stun(tours, n.getunite()));
									}
									if(n.getbasdroit()!=null) {
										if(n.getbasdroit().getunite()==null) {
											Fx_Unite m=new Fx_Unite(n.getbasdroit().getFx());
										
											getwall().add(new Palissade(n.getbasdroit(),m,Batisseur.this));
											m.setNom(nom_invocation);
											m.dessineImage(false);
										}
									}
									else {
										n.getbasdroit().getunite().add_Statut(new Stun(tours, n.getbasdroit().getunite()));
									}
									if(n.getgauche()!=null) {
										if(n.getgauche().getunite()==null) {
											Fx_Unite m=new Fx_Unite(n.getgauche().getFx());
											
											getwall().add(new Palissade(n.getgauche(),m,Batisseur.this));
											m.setNom(nom_invocation);
											m.dessineImage(false);
										}
										else {
											n.getgauche().getunite().add_Statut(new Stun(tours, n.getgauche().getunite()));
										}
									}
								}
								if(cotegb.contains(n)) {
									if(n.getunite()==null) {
										Fx_Unite m=new Fx_Unite(n.getFx());
										
										getwall().add(new Palissade(n,m,Batisseur.this));
										m.setNom(nom_invocation);
										m.dessineImage(false);
									}
									else {
										n.getunite().add_Statut(new Stun(tours, n.getunite()));
									}
									
									
									if(n.gethautgauche()!=null) {
										if(n.gethautgauche().getunite()==null) {
											Fx_Unite m=new Fx_Unite(n.gethautgauche().getFx());
											
											getwall().add(new Palissade(n.gethautgauche(),m,Batisseur.this));
											m.setNom(nom_invocation);
											m.dessineImage(false);
										}
									}
									else {
										n.gethautgauche().getunite().add_Statut(new Stun(tours, n.gethautgauche().getunite()));
									}
									if(n.getdroit()!=null) {
										if(n.getdroit().getunite()==null) {
											Fx_Unite m=new Fx_Unite(n.getdroit().getFx());
											
											getwall().add(new Palissade(n.getdroit(),m,Batisseur.this));
											m.setNom(nom_invocation);
											m.dessineImage(false);
										}
									}
									else {
										n.getdroit().getunite().add_Statut(new Stun(tours, n.getdroit().getunite()));
									}
								}
								else {
									if(cotedb.contains(n)) {
										if(n.getunite()==null) {
											Fx_Unite m=new Fx_Unite(n.getFx());
											
											getwall().add(new Palissade(n,m,Batisseur.this));
											m.setNom(nom_invocation);
											m.dessineImage(false);
										}
										else {
											n.getunite().add_Statut(new Stun(tours, n.getunite()));
										}
										if(n.gethautdroit()!=null) {
											if(n.gethautdroit().getunite()==null) {
												Fx_Unite m=new Fx_Unite(n.gethautdroit().getFx());
												
												getwall().add(new Palissade(n.gethautdroit(),m,Batisseur.this));
												m.setNom(nom_invocation);
												m.dessineImage(false);
											}
										}
										else {
											n.gethautdroit().getunite().add_Statut(new Stun(tours, n.gethautdroit().getunite()));
										}
										if(n.getgauche()!=null) {
											if(n.getgauche().getunite()==null) {
												Fx_Unite m=new Fx_Unite(n.getgauche().getFx());
												
												getwall().add(new Palissade(n.getgauche(),m,Batisseur.this));
												m.setNom(nom_invocation);
												m.dessineImage(false);
											}
											else {
												n.getgauche().getunite().add_Statut(new Stun(tours, n.getgauche().getunite()));
											}
										}
									}
								}
							}
						}
					}
					cd=cdmax;
				}
				actualisation();
				actualise();
			}
			
		}//Fin use

		@Override
		public void setrange() {
			if(!available())return;
			if(wall.size()!=0) {
				LinkedList<Hexagone> r=new LinkedList<Hexagone>();
				r.add(place);
				rang=r;
				actualise();///// necessaire ? cette methode n'agit pas sur l'unite
			}
			else {
				LinkedList<Hexagone> r=new LinkedList<Hexagone>();
				Hexagone lolg=place;
				int ind=0;
				while(lolg.getgauche()!=null && ind<portee) {
					r.add(lolg.getgauche());
					coteg.add(lolg.getgauche());
					lolg=lolg.getgauche();
					ind++;
				}
				Hexagone lold=place;
				int ind2=0;
				while(lold.getdroit()!=null && ind2<portee) {
					r.add(lold.getdroit());
					coted.add(lold.getdroit());
					lold=lold.getdroit();
					ind2++;
				}
				Hexagone loldh=place;
				int ind3=0;
				while(loldh.gethautdroit()!=null && ind3<portee) {
					r.add(loldh.gethautdroit());
					cotedh.add(loldh.gethautdroit());
					loldh=loldh.gethautdroit();
					ind3++;
				}
				Hexagone loldb=place;
				int ind4=0;
				while(loldb.getbasdroit()!=null && ind4<portee) {
					r.add(loldb.getbasdroit());
					cotedb.add(loldb.getbasdroit());
					loldb=loldb.getbasdroit();
					ind4++;
				}
				Hexagone lolgh=place;
				int ind5=0;
				while(lolgh.gethautgauche()!=null && ind5<portee) {
					r.add(lolgh.gethautgauche());
					cotegh.add(lolgh.gethautgauche());
					lolgh=lolgh.gethautgauche();
					ind5++;
				}
				Hexagone lolgb=place;
				int ind6=0;
				while(lolgb.getbasgauche()!=null && ind6<portee) {
					r.add(lolgb.getbasgauche());
					cotegb.add(lolgb.getbasgauche());
					lolgb=lolgb.getbasgauche();
					ind6++;
				}
				rang=r;
			}
		}		
	}
	
	
	class Fracture_sismique extends Skill implements Skill.cc{
		private int tour=creation.size();
		{
			name="Fracture sismique";
			description="Root autour et sur un terrain, la longueur du root ("+tour+") augmente en fonction du nombre de terrains posés actuellement par ce batisseur.";
			sub_description="";
			cout=4;
			cdmax=4;
		}

		@Override
		public void use(Hexagone n) {
			if(!available())return;
			if(rang.contains(n)) {
				LinkedList<Hexagone> autour=new LinkedList<Hexagone>();
				autour.add(n);
				for(int i=0;i<1;i++) {
					int pop=autour.size();
					for(int o=0;o<pop;o++) {
						autour=autour.get(o).add_autour_withunits(autour);
					}
				}
				for(Hexagone a:autour) {
					if(a.getunite()!=null) {
						a.getunite().add_Statut(new Root(creation.size(),a.getunite()));
						a.getunite().actualise();
					}
				}
				cd=cdmax;
			}
		}

		@Override
		public void setrange() {
			LinkedList<Hexagone> r=new LinkedList<Hexagone>();
			for(Terrain t:place.getpla().getterrains()) {
				if(t.gethexa()!=null) {
					r.add(t.gethexa());
				}
			}
			rang=r;
		}
		
	}//
	protected void degats_adversaire(Hexagone n) {
		if(n.getunite()!=null) {
			n.getunite().inflige(attaque,0);//20 pV en moins en comptant l'armure
		}
	}


	 public LinkedList<Unite> getwall() {
		// TODO Auto-generated method stub
		return wall;
	}


	void actu_dep(int dep) {
	        deplacement.removeAll(deplacement);
	        deplacement.add(place);
	        for(int i=0;i<dep;i++) {
	            int pop=deplacement.size();
	            for(int o=0;o<pop;o++) {
	                deplacement=deplacement.get(o).add_autour(deplacement);
	            }
	        }
	        deplacement.remove(place);
	        
	    }
	 

	public void actu_range() {
			// TODO Auto-generated method stub
					range.removeAll(range);
					range.add(place);	
					for(int i=0;i<portee;i++) {
						int pop=range.size();
						for(int o=0;o<pop;o++) {
							range=range.get(o).add_autour_withunits(range);
						}
					}
				range.remove(place);
		}
	 
	 public static int get_Prix() {
		 return 150;
	 }


	@Override
	protected String description_attaque() {
		// TODO Auto-generated method stub
		return "Inflige "+attaque+" degat(s) physique(s) à la cible";
	}
	@Override
	protected String passif() {
		return "Aucun passif";
	}

	@Override
	protected String description_encyclopedie() {
		// TODO Auto-generated method stub
		return "Le Batisseur est un invocateur, il peut créer des murs et des terrains, il peut aussi faire des contrôles de foule puissant";
	}
}
