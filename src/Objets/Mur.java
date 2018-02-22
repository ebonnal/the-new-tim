package Objets;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import TIM.FenetreTIM;

public class Mur extends MouseAdapter implements Objet {
	public boolean estFixe;
	public int etat;// 0: à droite,1:en haut,2:à gauche,3:en bas
	public ArrayList<Objet> accelModifiers;
	public ArrayList<Objet> vitesseModifiers;
	public BufferedImage sprite;
	public boolean estActif;
	public float coefGravite;
	public float x, y;
	public float angle, vangle;
	public float vx, vy;
	public float ax, ay;
	public float xi, yi;
	int xdrag, ydrag;
	public float xii, yii;
	public float anglei, vanglei;
	public float vxi, vyi;
	public float axi, ayi;
	public boolean isDragged;
	public boolean existe;
	FenetreTIM f;
	public ThreadMur thread;

	public Mur(float x, float y, float vx, float vy, int etat, FenetreTIM f, boolean estFixe) {
		existe = true;
		estActif = false;
		coefGravite = 0;
		this.etat = etat;
		this.estFixe = estFixe;
		f.pJeu.addMouseListener(this);
		f.modif.addMouseListener(this);
		f.pJeu.addMouseMotionListener(this);
		accelModifiers = new ArrayList<Objet>();
		vitesseModifiers = new ArrayList<Objet>();
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
		this.xii = x;
		this.yii = y;
		this.vxi = vx;
		this.vyi = vy;
		this.axi = 0;
		this.ayi = 0;
		this.angle = 0;
		this.anglei = 0;
		this.vangle = 0;
		this.vanglei = vangle;
		this.f = f;
		thread = new ThreadMur(f, this);
		if (estFixe)
			thread.threadState = 0;

		if ((f.annuaire).containsKey("Murs")) {
			f.annuaire.get("Murs").add((Objet) this);
		} else {
			f.annuaire.put("Murs", new ArrayList<Objet>());
			f.annuaire.get("Murs").add((Objet) this);
		}

		sprite=f.sprites.get("Mur");
	}

	public void resetMur(float x, float y, float vx, float vy, int etat, FenetreTIM f, boolean estFixe) {
		
		existe = true;
		estActif = false;
		coefGravite = 0;
		this.etat = etat;
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
		this.xii = x;
		this.yii = y;
		this.vxi = vx;
		this.vyi = vy;
		this.axi = 0;
		this.ayi = 0;
		this.angle = 0;
		this.anglei = 0;
		this.vangle = 0;
		this.vanglei = vangle;
		this.f = f;
		
		if(estFixe && thread.threadState==-2)
		{
			thread.threadState=0;
		}
		if ((f.annuaire).containsKey("Murs")) {

			f.annuaire.get("Murs").add((Objet) this);
		} else {
			f.annuaire.put("Murs", new ArrayList<Objet>());
			f.annuaire.get("Murs").add((Objet) this);
		}
		if ((f.annuaireReserve).containsKey("Murs")) {
			f.annuaireReserve.get("Murs").remove((Objet) this);
		} else {
		}

		
	}
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
		if (!f.isRunning && !estFixe) {
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
		if (!f.isRunning && !estFixe) {
			if (isDragged && x + getW() < 700 && x > 0 && y + getH() < 700 && y > 0 && !estPasUnePosAutorisee()) {
				if (thread.threadState == -2)
					thread.threadState = 0;

				xi = x;
				yi = y;
			} else if (isDragged && x < 700) {
				x = xi;
				y = yi;
			} else if (isDragged) {
				xii = x;
				yii = y;
				xi = x;
				yi = y;
			}
			isDragged = false;
			f.dragging = false;
		}
	}

	public boolean estPasUnePosAutorisee() {

		if ((f.annuaire).containsKey("Ballons")) {

			for (int i = 0; i < (f.annuaire.get("Ballons").size()); i++) {
				if (((Ballon) ((f.annuaire).get("Ballons").get(i))).existe()) {
					Ballon b = (Ballon) ((f.annuaire).get("Ballons").get(i));
					float xc = b.getx() + b.getW() / 2.0f;
					float yc = b.gety() + b.getH() / 2.0f;
					Mur m = this;
					if (m.y < yc && yc < m.y + m.getH() && (m.x - b.getW() / 2.0f) < xc
							&& xc < (m.x + m.getW() + b.getW() / 2.0f)) {
						return true;
					}
					if (m.x < xc && xc < m.x + m.getW() && (m.y - b.getH() / 2.0f) < yc
							&& yc < (m.y + m.getH() + b.getH() / 2.0f)) {
						return true;
					}
					if (Math.pow(xc - m.x, 2) + Math.pow(yc - m.y, 2) <= Math.pow((b.getH() / 2), 2)) {
						return true;

					}
					if (Math.pow(xc - (m.x + m.getW()), 2) + Math.pow(yc - (m.y), 2) <= Math.pow((b.getH() / 2), 2)) {
						return true;

					}
					if (Math.pow(xc - (m.x + m.getW()), 2) + Math.pow(yc - (m.y + m.getH()), 2) <= Math
							.pow((b.getH() / 2), 2)) {
						return true;

					}
					if (Math.pow(xc - (m.x), 2) + Math.pow(yc - (m.y + m.getH()), 2) <= Math.pow((b.getH() / 2), 2)) {
						return true;

					}

				}
			}
		}
		if ((f.annuaire).containsKey("Ventilos")) {

			for (int i = 0; i < (f.annuaire.get("Ventilos").size()); i++) {
				if (((Ventilo) ((f.annuaire).get("Ventilos").get(i))).existe()) {
					Ventilo b = (Ventilo) ((f.annuaire).get("Ventilos").get(i));
					float xc = b.getx() + b.getW() / 2.0f;
					float yc = b.gety() + b.getH() / 2.0f;
					Mur m = this;
					if (m.y < yc && yc < m.y + m.getH() && (m.x - b.getW() / 2.0f) < xc
							&& xc < (m.x + m.getW() + b.getW() / 2.0f)) {
						return true;
					}
					if (m.x < xc && xc < m.x + m.getW() && (m.y - b.getH() / 2.0f) < yc
							&& yc < (m.y + m.getH() + b.getH() / 2.0f)) {
						return true;
					}
					if (Math.pow(xc - m.x, 2) + Math.pow(yc - m.y, 2) <= Math.pow((b.getH() / 2), 2)) {
						return true;

					}
					if (Math.pow(xc - (m.x + m.getW()), 2) + Math.pow(yc - (m.y), 2) <= Math.pow((b.getH() / 2), 2)) {
						return true;

					}
					if (Math.pow(xc - (m.x + m.getW()), 2) + Math.pow(yc - (m.y + m.getH()), 2) <= Math
							.pow((b.getH() / 2), 2)) {
						return true;

					}
					if (Math.pow(xc - (m.x), 2) + Math.pow(yc - (m.y + m.getH()), 2) <= Math.pow((b.getH() / 2), 2)) {
						return true;

					}

				}
			}
		}

		return false;

	}

	public void addAccelModifiers(Objet o) {
		accelModifiers.add(o);
	}

	public void addVitesseModifiers(Objet o) {
		vitesseModifiers.add(o);
	}

	public boolean existe() {
		return existe;
	}

	public void setExiste(boolean e) {
		existe = e;
	}

	public int getH() {
		if (etat > 0)
			return sprite.getHeight() * 2;
		return sprite.getHeight() * 2 * (-etat);

	}

	public int getW() {
		if (etat < 0)
			return sprite.getWidth() * 2;
		return sprite.getWidth() * 2 * (etat);
	}

	public int getEtat() {
		return etat;
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

	public void threadResetPause() {
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

	public void threadStart() {

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
			// thread.start();
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

		return new PaireXY(0, 0);
	}

	public void majEffetsDiscontinus() {
		for (int i = 0; i < vitesseModifiers.size(); i++) {
			if (vitesseModifiers.get(i).estActif()) {
				if (vitesseModifiers.get(i).existe()) {
					vitesseModifiers.get(i).majEffetsDiscontinusModif(this);
				}
			}
		}

	}

	public void majEffetsDiscontinusModif(Objet o) {

		if (existe && !(o instanceof Mur) && !(o instanceof Panier)) {
			float coef = 0.8f;
			float x2 = o.getx() + o.getW() / 2;
			float y2 = o.gety() + o.getH() / 2;
			if (o.getVx() > 0) {
				if (y2 > y && y2 < y + getH()) {
					if (x2 + o.getW() / 2 >= x && x2 + o.getW() / 2 <= x + getW()) {
						o.setx(x - o.getW());

						f.setteryfix(coef, o, 1);
						return;
					}
				}
				if (o.getVy() > 0) {
					if (x2 > x && x2 < x + getW()) {
						if (y2 + o.getH() / 2 >= y && y2 + o.getH() / 2 <= y + getH()) {
							o.sety(y - o.getH());

							f.setterxfix(coef, o, -1);

							return;
						}
					}

				}
				if (o.getVy() < 0) {
					if (x2 > x && x2 < x + getW()) {
						if (o.gety() <= y + getH() && o.gety() >= y) {
							o.sety(y + getH());
							f.setterxfix(coef, o, 1);

							return;
						}
					}

				}

			} else {
				if (y2 > y && y2 < y + getH()) {
					if (x2 - o.getW() / 2 <= x + getW() && x2 - o.getW() / 2 > x) {
						o.setx(x + getW());
						f.setteryfix(coef, o, -1);

						return;
					}
				}
				if (o.getVy() > 0) {
					if (x2 > x && x2 < x + getW()) {
						if (y2 + o.getH() / 2 >= y && y2 + o.getH() / 2 <= y + getH()) {
							o.sety(y - o.getH());
							f.setterxfix(coef, o, -1);

							return;

						}
					}
				}
				if (o.getVy() < 0) {
					if (x2 > x && x2 < x + getW()) {
						if (o.gety() <= y + getH() && o.gety() >= y) {
							o.sety(y + getH());

							f.setterxfix(coef, o, 1);

							return;

						}
					}
				}

			}
			if (Math.pow(x - x2, 2) + Math.pow(y - y2, 2) < Math.pow((o.getH() / 2), 2)) {
				float vxIni=o.getVx();
				float vyIni=o.getVy();

				
				while (Math.pow(x - x2, 2) + Math.pow(y - y2, 2) < Math.pow((o.getH() / 2), 2)) {
					o.setx(o.getx() - 1);
					o.sety(o.gety() - 1);
					x2 = o.getx() + o.getW() / 2;
					y2 = o.gety() + o.getH() / 2;
				}
				
				float mcx=x2-x;
				float mcy=y2-y;
				float k=Math.abs(2*(o.getVx()*mcx+o.getVy()*mcy)/(float)Math.pow(norme(mcx,mcy),2));
				o.setVx((float) (coef *(o.getVx()+k*mcx) ));
				o.setVy((float) (coef*(o.getVy()+k*mcy) ));
				if (o instanceof Ballon) {
					o.setVAngle((float) (signedUnity(sinus(vxIni,vyIni,o.getVx(),o.getVy()))
							*norme(o.getVx(),o.getVy())*360/Math.PI*
							Math.abs(Math.PI-angle(vxIni,vyIni,o.getVx(),o.getVy())) / Math.PI / o.getH()));				}
				return;
			}
			if (Math.pow(x - x2, 2) + Math.pow(y + getH() - y2, 2) < Math.pow((o.getH() / 2), 2)) {
				float vxIni=o.getVx();
				float vyIni=o.getVy();
				while (Math.pow(x - x2, 2) + Math.pow(y + getH() - y2, 2) < Math.pow((o.getH() / 2), 2)) {
					o.setx(o.getx() - 1);
					o.sety(o.gety() + 1);
					x2 = o.getx() + o.getW() / 2;
					y2 = o.gety() + o.getH() / 2;
				}
				float mcx=x2-x;
				float mcy=y2-(y + getH());
				float k=Math.abs(2*(o.getVx()*mcx+o.getVy()*mcy)/(float)Math.pow(norme(mcx,mcy),2));				
				o.setVx((float) (coef *(o.getVx()+k*mcx) ));
				o.setVy((float) (coef*(o.getVy()+k*mcy) ));
				if (o instanceof Ballon) {
					o.setVAngle((float) (signedUnity(sinus(vxIni,vyIni,o.getVx(),o.getVy()))
							*norme(o.getVx(),o.getVy())*360/Math.PI*
							Math.abs(Math.PI-angle(vxIni,vyIni,o.getVx(),o.getVy())) / Math.PI / o.getH()));				}
				return;
			}
			if (Math.pow(x + getW() - x2, 2) + Math.pow(y - y2, 2) < Math.pow((o.getH() / 2), 2)) {
				float vxIni=o.getVx();
				float vyIni=o.getVy();
				
				while (Math.pow(x + getW() - x2, 2) + Math.pow(y - y2, 2) < Math.pow((o.getH() / 2), 2)) {
					o.setx(o.getx() + 1);
					o.sety(o.gety() - 1);
					x2 = o.getx() + o.getW() / 2;
					y2 = o.gety() + o.getH() / 2;
				}
				float mcx=x2-(x + getW());
				float mcy=y2-y;
				float k=Math.abs(2*(o.getVx()*mcx+o.getVy()*mcy)/(float)Math.pow(norme(mcx,mcy),2));				
				o.setVx((float) (coef *(o.getVx()+k*mcx) ));
				o.setVy((float) (coef*(o.getVy()+k*mcy) ));
				if (o instanceof Ballon) {
					o.setVAngle((float) (signedUnity(sinus(vxIni,vyIni,o.getVx(),o.getVy()))
							*norme(o.getVx(),o.getVy())*360/Math.PI*
							Math.abs(Math.PI-angle(vxIni,vyIni,o.getVx(),o.getVy())) / Math.PI / o.getH()));
				}
				return;
			}
			if (Math.pow(x + getW() - x2, 2) + Math.pow(y + getH() - y2, 2) < Math.pow((o.getH() / 2), 2)) {
				float vxIni=o.getVx();
				float vyIni=o.getVy();
				while (Math.pow(x + getW() - x2, 2) + Math.pow(y + getH() - y2, 2) < Math.pow((o.getH() / 2), 2)) {
					o.setx(o.getx() + 1);
					o.sety(o.gety() + 1);
					x2 = o.getx() + o.getW() / 2;
					y2 = o.gety() + o.getH() / 2;
				}
				float mcx=x2-(x + getW());
				float mcy=y2-(y + getH());
				float k=Math.abs(2*(o.getVx()*mcx+o.getVy()*mcy)/(float)Math.pow(norme(mcx,mcy),2));				
				o.setVx((float) (coef *(o.getVx()+k*mcx) ));
				o.setVy((float) (coef*(o.getVy()+k*mcy) ));
				if (o instanceof Ballon) {
					o.setVAngle((float) (signedUnity(sinus(vxIni,vyIni,o.getVx(),o.getVy()))
							*norme(o.getVx(),o.getVy())*360/Math.PI*
							Math.abs(Math.PI-angle(vxIni,vyIni,o.getVx(),o.getVy())) / Math.PI / o.getH()));
				}
			}

		}
	}

	public void modifAction(String e) {
		if (e == f.RIGHT) {
			e = String.valueOf(Math.abs(etat) + 1);
		} else if (e == f.LEFT) {
			e = String.valueOf(Math.abs(etat) - 1);
		} else if (e == f.DOWN) {
			e = String.valueOf(-Math.abs(etat) - 1);
		} else if (e == f.UP) {
			e = String.valueOf(-Math.abs(etat) + 1);
		}
		if (!e.equals("") && Integer.parseInt(e) != 0 && Integer.parseInt(e) < 16 && Integer.parseInt(e) > -16) {
			etat = Integer.parseInt(e);
			if (etat > 0) {
				if ((!(x + getW() < 700 && x > 0 && y + getH() < 700 && y > 0) && x < 700) || estPasUnePosAutorisee()) {
					x = xii;
					y = yii;
					xi = xii;
					yi = yii;
				}
			} else {
				if (etat == -16) {
					etat = 1;
				}
				if ((!(x + getW() < 700 && x > 0 && y + getH() < 700 && y > 0) && x < 700) || estPasUnePosAutorisee()) {
					x = xii;
					y = yii;
					xi = xii;
					yi = yii;
				}
			}

		} else {
			if (etat > 0) {
				etat++;
				if (etat == 16) {
					etat = -1;
				}
				if ((!(x + getW() < 700 && x > 0 && y + getH() < 700 && y > 0) && x < 700) || estPasUnePosAutorisee()) {
					x = xii;
					y = yii;
					xi = xii;
					yi = yii;
				}

			} else {
				etat--;
				if (etat == -16) {
					etat = 1;
				}
				if ((!(x + getW() < 700 && x > 0 && y + getH() < 700 && y > 0) && x < 700) || estPasUnePosAutorisee()) {
					x = xii;
					y = yii;
					xi = xii;
					yi = yii;
				}
			}
		}

	}
/*49740181097962561017193*/
	public static int signedUnity(float f) {
		if (f > 0) {
			return 1;
		} else if (f == 0f) {
			return 0;
		}
		return -1;
	}

	public static float norme(float fx, float fy) {
		return ((float) Math.pow(Math.pow(fx, 2) + Math.pow(fy, 2), 0.5));
	}

	public static float norme(double fx, double fy) {
		return ((float) Math.pow(Math.pow(fx, 2) + Math.pow(fy, 2), 0.5));
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
	public static float cosinus(float x1, float y1, float x2, float y2) {// cosinus
		// entre
		// deux
		// vecteurs
		// 2D
if (Math.abs(norme(x1, y1)) <= 0.0005 || Math.abs(norme(x2, y2)) <= 0.0005)
return 0;
return (x1 * x2 + y1 * y2) / norme(x1, y1) / norme(x2, y2);
}
	public static float angle(float x1, float y1, float x2, float y2)
	{
		return((float)Math.acos(cosinus(x1,y1,x2,y2)));
	}

	public void paintAll(Graphics2D g) {
		
		if ((f.annuaire).containsKey("Murs")) {
			

			for (int i = 0; i < (f.annuaire.get("Murs").size()); i++) {
				if (((Mur) ((f.annuaire).get("Murs").get(i))).existe()) {
					
					int etat = ((Mur) ((f.annuaire).get("Murs").get(i))).etat;
					if (etat > 0) {
						for (int k = 0; k < etat; k++) {
							f.pJeu.drawImageMaison(g, ((Mur) ((f.annuaire).get("Murs").get(i))).sprite,
									(int) ((Mur) ((f.annuaire).get("Murs").get(i))).x + sprite.getWidth() * 2 * k,
									(int) ((Mur) ((f.annuaire).get("Murs").get(i))).y, 0, 2, 2, false);
						}

					} else {
						for (int k = 0; k < -etat; k++) {
							f.pJeu.drawImageMaison(g, ((Mur) ((f.annuaire).get("Murs").get(i))).sprite,
									(int) ((Mur) ((f.annuaire).get("Murs").get(i))).x,
									(int) ((Mur) ((f.annuaire).get("Murs").get(i))).y + sprite.getHeight() * 2 * k, 0,
									2, 2, false);
						}
					}

				}
			}
		}

	}

	public void erase() {
		if ((f.annuaireReserve).containsKey("Murs")) {
			f.annuaireReserve.get("Murs").add((Objet) this);
		} else {
			f.annuaireReserve.put("Murs", new ArrayList<Objet>());
			f.annuaireReserve.get("Murs").add((Objet) this);

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
	}

}
