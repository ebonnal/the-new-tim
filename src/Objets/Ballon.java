package Objets;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import TIM.FenetreTIM;

public class Ballon extends MouseAdapter implements Objet {

	public boolean estFixe;// indique si l'objet est modifiable
	public int etat = 0;// etat inutile pour le ballon
	public ArrayList<Objet> accelModifiers;// Liste des Instances qui modifient
											// l'accelérationde notre Instance
	public ArrayList<Objet> vitesseModifiers;// Liste des Instances qui
												// modifient la vitesse de notre
												// Instance
	public BufferedImage sprite;// image
	public boolean estActif;// indique si l'Instance est en état de participer à
							// une simulation qui se lancerait
	public float coefGravite = 1;// coef d'interaction gravitationnelle (négatif
									// pour simuler un ballon gonflé à l'helium
									// par exemple)
	public float x, y;// position courante
	public float angle, vangle;// angle et vitesse angulaire
	public float vx, vy;// vitesse
	public float ax, ay;// accelération
	// sauvegardes de la dernière position autorisée (car drag and drop à
	// certains endroits est interdit) :
	public float xi, yi;
	int xdrag, ydrag;
	public float anglei, vanglei;
	public float vxi, vyi;
	public float axi, ayi;
	public boolean isDragged;// indique si l'Instance est en train de se faire
								// drag and drop
	FenetreTIM f;// Référence vers la JFrame
	public ThreadBallon thread;// thread associée
	public boolean existe;// indique si l'objet "existe" (les ballons sont
							// "détruit" au cours d'une simulation
	// (mangés par les sauts) mais ce n'est pas l'Instance qui doit l'être bien
	// entendu.

	public Ballon(float x, float y, float vx, float vy, float vangle, FenetreTIM f, boolean estFixe) {
		// constructeur
		estActif = false;
		etat = 0;
		existe = true;
		this.estFixe = estFixe;
		f.pJeu.addMouseListener(this);// On récupère les évènement souris du
										// panel de jeu
		f.pJeu.addMouseMotionListener(this);
		accelModifiers = new ArrayList<Objet>();
		vitesseModifiers = new ArrayList<Objet>();
		accelModifiers.add(f);
		vitesseModifiers.add(f);

		Iterator<String> entrees = f.annuaire.keySet().iterator();
		while (entrees.hasNext()) {
			// On indique notre présence aux autres objets en s'inscrivant dans
			// leurs
			// listes d'accelModifiers et de vitesseModifiers
			// et on remplit les notres :
			String currEntrees = entrees.next();
			for (int i = 0; i < f.annuaire.get(currEntrees).size(); i++) {
				if (f.annuaire.get(currEntrees).get(i) instanceof Ballon) {
					f.annuaire.get(currEntrees).get(i).addVitesseModifiers(this);
					f.annuaire.get(currEntrees).get(i).addAccelModifiers(this);
					accelModifiers.add(f.annuaire.get(currEntrees).get(i));
					vitesseModifiers.add(f.annuaire.get(currEntrees).get(i));

				} else {
					f.annuaire.get(currEntrees).get(i).addVitesseModifiers(this);
					f.annuaire.get(currEntrees).get(i).addAccelModifiers(this);
					accelModifiers.add(f.annuaire.get(currEntrees).get(i));
					vitesseModifiers.add(f.annuaire.get(currEntrees).get(i));
				}
			}
		}
		this.isDragged = false;
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.ax = 0;
		this.ay = 0;
		this.xi = x;
		this.yi = y;
		this.vxi = vx;
		this.vyi = vy;
		this.axi = 0;
		this.ayi = 0;
		this.angle = 0;
		this.anglei = 0;
		this.vangle = vangle;
		this.vanglei = vangle;
		this.f = f;
		// Création du thread associé prêt à être lancé
		thread = new ThreadBallon(f, this);
		if (estFixe)
			thread.threadState = 0;

		// on s'inscrit dans l'annuaire :
		if ((f.annuaire).containsKey("Ballons")) {
			f.annuaire.get("Ballons").add((Objet) this);
		} else {
			f.annuaire.put("Ballons", new ArrayList<Objet>());
			f.annuaire.get("Ballons").add((Objet) this);
		}
		sprite=f.sprites.get("Ballon");
		
	}

	public void resetBallon(float x, float y, float vx, float vy, float vangle, FenetreTIM f, boolean estFixe) {
		// Méthode très proche du constructeur qui permet de recycler une
		// Instance en la réinitialisant :
		

		estActif = false;
		etat = 0;
		existe = true;
		this.estFixe = estFixe;

		accelModifiers.clear();
		vitesseModifiers.clear();
		accelModifiers.add(f);
		vitesseModifiers.add(f);

		Iterator<String> entrees = f.annuaire.keySet().iterator();
		while (entrees.hasNext()) {
			String currEntrees = entrees.next();
			for (int i = 0; i < f.annuaire.get(currEntrees).size(); i++) {
				if (f.annuaire.get(currEntrees).get(i) instanceof Ballon) {
					f.annuaire.get(currEntrees).get(i).addVitesseModifiers(this);
					f.annuaire.get(currEntrees).get(i).addAccelModifiers(this);
					accelModifiers.add(f.annuaire.get(currEntrees).get(i));
					vitesseModifiers.add(f.annuaire.get(currEntrees).get(i));

				} else {
					f.annuaire.get(currEntrees).get(i).addVitesseModifiers(this);
					f.annuaire.get(currEntrees).get(i).addAccelModifiers(this);
					accelModifiers.add(f.annuaire.get(currEntrees).get(i));
					vitesseModifiers.add(f.annuaire.get(currEntrees).get(i));
				}
			}
		}
		this.isDragged = false;
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.ax = 0;
		this.ay = 0;
		this.xi = x;
		this.yi = y;
		this.vxi = vx;
		this.vyi = vy;
		this.axi = 0;
		this.ayi = 0;
		this.angle = 0;
		this.anglei = 0;
		this.vangle = vangle;
		this.vanglei = vangle;
		this.f = f;
		if(estFixe && thread.threadState==-2)
		{
			thread.threadState=0;
		}
		

		if ((f.annuaire).containsKey("Ballons")) {
			f.annuaire.get("Ballons").add((Objet) this);
		} else {
			f.annuaire.put("Ballons", new ArrayList<Objet>());
			f.annuaire.get("Ballons").add((Objet) this);
		}

		// Principale différence avec le constructeur :
		// Comme on a été pioché dans l'annuaireReserve, il faut s'en
		// désinscrire !
		if ((f.annuaireReserve).containsKey("Ballons")) {
			f.annuaireReserve.get("Ballons").remove((Objet) this);
		} else {
		}

		
	}

	// Gestion des évènements souris :
	public void mousePressed(MouseEvent e) {
		if (!f.isRunning && !estFixe) {
			
			if (x + getW() > e.getX() && e.getX() > x && y + getH() > e.getY() && e.getY() > y && !isDragged
					&& !f.dragging) {

				xdrag = e.getX();
				ydrag = e.getY();
				f.objetFocused = this;
				isDragged = true;
				f.dragging = true;

			}
		}
	}
	public void mouseDragged(MouseEvent e) {

		if (!estFixe && !f.isRunning) {
			if (x + getW() > e.getX() && e.getX() > x && y + getH() > e.getY() && e.getY() > y && !isDragged
					&& !f.dragging) {
				xdrag = e.getX();
				ydrag = e.getY();
				f.objetFocused = this;
				isDragged = true;
				f.dragging = true;

			}
			if (isDragged && f.objetFocused == this) {
				x += e.getX() - xdrag;
				y += e.getY() - ydrag;
				xdrag = e.getX();
				ydrag = e.getY();
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		// fin potentielle de drag and drop
		// -> détermine si la position d'arrivé est convenable à l'aide de la
		// méthode : estPasUnePosAutorisee()
		if (!f.isRunning && !estFixe) {
			if (isDragged && x + getW() < 700 && x > 0 && y + getH() < 700 && y > 0 && !estPasUnePosAutorisee()) {
				if (thread.threadState == -2)
					thread.threadState = 0;
			} else if (isDragged && x < 700) {
				x = xi;
				y = yi;
			} else {
				xi = x;
				yi = y;
			}
			isDragged = false;
			f.dragging = false;
		}
	}

	public boolean estPasUnePosAutorisee() {
		// détermine si la position est autorisé :
		// Règles :LES VENTILATEURS ET BALLONS NE PEUVENT PAS CHEVAUCHER LES
		// MURS.
		// LES ITEMS NE PEUVENT PAS DEPASSER LES REBORDS DE L'ECRAN
		// et deux panier/entilateur/ballon ne peuvent pas avoir le même centre
		// (car on ne peut pas déterminer dans quelle direction ils se
		// repoussent dans ce cas là)
		float xc = x + getW() / 2.0f;
		float yc = y + getH() / 2.0f;
		if ((f.annuaire).containsKey("Murs")) {

			for (int i = 0; i < (f.annuaire.get("Murs").size()); i++) {
				if (((Mur) ((f.annuaire).get("Murs").get(i))).existe()) {
					Mur m = (Mur) ((f.annuaire).get("Murs").get(i));

					if (m.y < yc && yc < m.y + m.getH() && (m.x - getW() / 2.0f) < xc
							&& xc < (m.x + m.getW() + getW() / 2.0f)) {
						return true;
					}
					if (m.x < xc && xc < m.x + m.getW() && (m.y - getH() / 2.0f) < yc
							&& yc < (m.y + m.getH() + getH() / 2.0f)) {
						return true;
					}
					if (Math.pow(xc - m.x, 2) + Math.pow(yc - m.y, 2) <= Math.pow((getH() / 2), 2)) {
						return true;

					}
					if (Math.pow(xc - (m.x + m.getW()), 2) + Math.pow(yc - (m.y), 2) <= Math.pow((getH() / 2), 2)) {
						return true;

					}
					if (Math.pow(xc - (m.x + m.getW()), 2) + Math.pow(yc - (m.y + m.getH()), 2) <= Math
							.pow((getH() / 2), 2)) {
						return true;

					}
					if (Math.pow(xc - (m.x), 2) + Math.pow(yc - (m.y + m.getH()), 2) <= Math.pow((getH() / 2), 2)) {
						return true;

					}

				}
			}
		}
		if ((f.annuaire).containsKey("Ventilos")) {

			for (int i = 0; i < (f.annuaire.get("Ventilos").size()); i++) {
				if (((Ventilo) ((f.annuaire).get("Ventilos").get(i))).existe()) {
					Ventilo m = (Ventilo) ((f.annuaire).get("Ventilos").get(i));
					if (m.x + m.getW() / 2 == x + getW() / 2 && m.y + m.getH() / 2 == y + getH() / 2)
						return true;
				}
			}
		}
		if ((f.annuaire).containsKey("Ballons")) {

			for (int i = 0; i < (f.annuaire.get("Ballons").size()); i++) {
				if (((Ballon) ((f.annuaire).get("Ballons").get(i))).existe()) {
					Ballon m = (Ballon) ((f.annuaire).get("Ballons").get(i));
					if (m != this) {
						if (m.x + m.getW() / 2 == x + getW() / 2 && m.y + m.getH() / 2 == y + getH() / 2)
							return true;
					}
				}
			}
		}
		if ((f.annuaire).containsKey("Paniers")) {

			for (int i = 0; i < (f.annuaire.get("Paniers").size()); i++) {
				if (((Panier) ((f.annuaire).get("Paniers").get(i))).existe()) {
					Panier m = (Panier) ((f.annuaire).get("Paniers").get(i));
					if (m.x + m.getW() / 2 == x + getW() / 2 && m.y + m.getH() / 2 == y + getH() / 2)
						return true;
				}
			}
		}
		return false;

	}

	// Méthodes banales :
	public void addAccelModifiers(Objet o) {
		accelModifiers.add(o);
	}

	public void addVitesseModifiers(Objet o) {
		vitesseModifiers.add(o);
	}

	public int getEtat() {
		return etat;
	}

	public boolean existe() {
		return existe;
	}

	public void setExiste(boolean e) {
		existe = e;
	}

	public void modifAction(String e) {
	}

	public int getH() {
		return sprite.getHeight();
	}

	public int getW() {
		return sprite.getWidth();
	}

	public float getVAngle() {
		
		return vangle;
	}

	public void setVAngle(float i) {
		
		vangle = i;
	}

	public float getx() {
		return x;
	}

	public float getVx() {
		return vx;
	}

	public void setx(float nx) {
		x = nx;
	}

	public void setVx(float nvx) {
		vx = nvx;
	}

	public float gety() {
		return y;
	}

	public float getVy() {
		return vy;
	}

	public void sety(float ny) {
		y = ny;
	}

	public void setVy(float nvy) {
		vy = nvy;
	}

	public float getCoefGravite() {
		return coefGravite;
	}

	public boolean estActif() {
		return estActif;
	}

	public BufferedImage getSprite() {
		return sprite;
	}

	// FIN METHODES BANALES

	public void threadResetPause() {// Mise en pause du thread et mises à jour
									// de position car cete méthode est appellée
									// en fin de simulation.
		estActif = false;
		x = xi;
		y = yi;
		vx = vxi;
		vy = vyi;

		ax = axi;
		ay = ayi;
		angle = anglei;
		vangle = vanglei;
		if (thread.threadState == 1)
			thread.threadState = -1;
			
	}

	public void threadStart() {// Démarrage de la thread ou sortie de Pause

		if (x + getW() <= 700 && x >= 0 && y + getH() <= 700 && y >= 0) {
			estActif = true;
			if (thread.threadState == -2) {
				thread.threadState = 0;
			}

		} else {
			estActif = false;
		}
		if (thread.threadState == 0) {
			xi = x;
			yi = y;
			thread.threadState = 1;
			thread.start();
		}
		if (thread.threadState == -1) {
			xi = x;
			yi = y;
			thread.threadState = 1;
			synchronized(this)
			{
			this.notify();
			}
		}
	}

	public PaireXY accel() {
		// Calcul de notre accélération actuelle à partir des modification que
		// l'on reçoit des ".accelModif()" des éléments de notre catalogue
		// d'accelModifiers
		PaireXY accel = new PaireXY(0, 0);
		for (int i = 0; i < accelModifiers.size(); i++) {
			if (accelModifiers.get(i).estActif()) {
				if (accelModifiers.get(i).existe()) {

					accel.addXY(accelModifiers.get(i).accelModif(this));
				}
			}
		}
		return accel;
	}

	public PaireXY accelModif(Objet o) {
		// méthode que les Objets qui nous ont dans leur catalogue
		// d'accelModifiers vont appeler pour savoir comment on modifie leur
		// accelération
		if (estActif && existe) {
			if (o instanceof Ballon) {
				// Ballon Contre Ballon :

				float x1 = x + getW() / 2;
				float x2 = o.getx() + o.getW() / 2;
				float y1 = y + getH() / 2;
				float y2 = o.gety() + o.getH() / 2;

				if (Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) < Math.pow((getH()), 2)) {
					float normx = (x2 - x1) / getH();
					float normy = (y2 - y1) / getH();
					float normnorm = norme(normx, normy);
					normx = normx / normnorm;
					normy = normy / normnorm;
					float temp = 1000 * (getH() - norme(x1 - x2, y1 - y2));// force
																			// centrale
																			// de
																			// rappel
																			// élastique
					// calcul de la nouvelle vitesse angulaire à partir des
					// vitesses des deux ballons et
					// du vecteur unitaire (normx,normy) qui va du centre de
					// notre Instance vers celui de l'autre

					o.setVAngle((o.getVAngle()-getVAngle())/2+((float) (360
							* (sinus(normx, normy, vx, vy) * norme(vx, vy)
									+ sinus(-normx, -normy, o.getVx(), o.getVy()) * norme(o.getVx(), o.getVy()))
							/ Math.PI / getH()))/1.2f);
					
					setVAngle((getVAngle()-o.getVAngle())/2+((float) (360
							* (sinus(-normx, -normy, o.getVx(), o.getVy()) * norme(o.getVx(), o.getVy())
									+ sinus(normx, normy, getVx(), getVy()) * norme(getVx(), getVy()))
							/ Math.PI / getH()))/1.2f);

					o.setVx(0.95f * o.getVx());// légère perte de
												// vitesse/énergie
					o.setVy(0.95f * o.getVy());
					return new PaireXY(temp * normx, temp * normy);
				}
			}
			if (o instanceof Ventilo) {
				// Même principe en plus simple car pas de rotation géré entre
				// Ballon et ventilateur :
				float x1 = x + getW() / 2;
				float x2 = o.getx() + o.getW() / 2;
				float y1 = y + getH() / 2;
				float y2 = o.gety() + o.getH() / 2;

				if (Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) < Math.pow((getH() / 2 + o.getH() / 2), 2)) {
					float normx = (x2 - x1) / getH();
					float normy = (y2 - y1) / getH();
					float normnorm = norme(normx, normy);
					normx = normx / normnorm;
					normy = normy / normnorm;
					float temp = 200 * (getH() / 2 + o.getH() / 2 - norme(x1 - x2, y1 - y2));
					o.setVx(0.95f * o.getVx());
					o.setVy(0.95f * o.getVy());
					return new PaireXY(temp * normx, temp * normy);
				}
			}
		}
		return new PaireXY(0, 0);

	}

	public void majEffetsDiscontinus() {
		// Application des modifications discontinues de notre vitesse à
		// partir des ".majEffetsDiscontinusModif()" des éléments de notre
		// catalogue de vitesseModifiers

		for (int i = 0; i < vitesseModifiers.size(); i++) {
			if (vitesseModifiers.get(i).estActif()) {
				if (vitesseModifiers.get(i).existe()) {
					vitesseModifiers.get(i).majEffetsDiscontinusModif(this);

				}
			}
		}

	}

	public void majEffetsDiscontinusModif(Objet o) {
		// méthode vide car les ballons n'ont aucune influence discontinue sur
		// les autres éléments
	}

	// méthodes dont j'avait besoin pour certains calculs :
	public static int signedUnity(float f) {
		if (f > 0) {
			return 1;
		} else if (f == 0f) {
			return 0;
		}
		return -1;
	}

	public static float minAbs(float seuil, float seuiled) {
		if (Math.abs(seuiled) >= seuil)
			return signedUnity(seuiled) * seuil;
		return seuiled;
	}

	public static float norme(float fx, float fy) {
		return ((float) Math.sqrt(Math.pow(fx, 2) + Math.pow(fy, 2)));
	}

	public static float sinus(float x1, float y1, float x2, float y2) {// sinus
																		// entre
																		// deux
																		// vecteurs
																		// 2D
		if (Math.abs(norme(x1, y1)) <= 0.0005 || Math.abs(norme(x2, y2)) <= 0.0005)
			return 0;
		return (x1 * y2 - y1 * x2) / norme(x1, y1) / norme(x2, y2);
	}

	public static float norme(double fx, double fy) {
		return ((float) Math.pow(Math.pow(fx, 2) + Math.pow(fy, 2), 0.5));
	}

	public void paintAll(Graphics2D g) {// méthode qui permet de peindre toutes
										// les Instancces de Ballons que
										// contient l'annuaire de la JFrame
		// (méthode appelée par le JPanel pJeu)

		if ((f.annuaire).containsKey("Ballons")) {

			for (int i = 0; i < (f.annuaire.get("Ballons").size()); i++) {
				if (((Ballon) ((f.annuaire).get("Ballons").get(i))).existe()) {
					f.pJeu.drawImageMaison(g, ((Ballon) ((f.annuaire).get("Ballons").get(i))).sprite,
							(int) ((Ballon) ((f.annuaire).get("Ballons").get(i))).x,
							(int) ((Ballon) ((f.annuaire).get("Ballons").get(i))).y,
							(int) ((Ballon) ((f.annuaire).get("Ballons").get(i))).angle, 1, 1, false);
				}

			}
		}

	}

	public void erase() {
		// L'instance quitte momentanément le jeu et est placée dans
		// l'annuaireReserve
		if ((f.annuaireReserve).containsKey("Ballons")) {
			f.annuaireReserve.get("Ballons").add((Objet) this);
		} else {
			f.annuaireReserve.put("Ballons", new ArrayList<Objet>());
			f.annuaireReserve.get("Ballons").add((Objet) this);
		}
		isDragged=false;
		estActif = false;
		existe = true;
		if(thread.threadState==0)
		 {
			 thread.threadState=-2;
		 }
		 if(thread.threadState!=-2)
			 {
			 thread.threadState = -1;
			 }
		 // on tue la thread associée en changeant ce
								// booléen
		// (elle devra être recréée lors du prochain recyclage de notre instance
		// avec .resetBallon()

	}

}
