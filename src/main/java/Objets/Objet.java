package Objets;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public interface Objet {
	public int getEtat();
	  public void erase();
	  public void modifAction(String e);
	  public boolean existe();
	  public void setExiste(boolean e);
	  public int getH();
	  public int getW();
	  public void addAccelModifiers(Objet o);
	  public void addVitesseModifiers(Objet o);
	  public float getVAngle();
	  public void setVAngle(float i);
	  public float getx();
	  public float getVx();
	  public void setx(float i);
	  public void setVx(float i);
	  public float gety();
	  public float getVy();
	  public void sety(float i);
	  public void setVy(float i);
	  public float getCoefGravite();
	  public BufferedImage getSprite();
	  public boolean estActif();
	  public void threadStart();
	  public void threadResetPause();
	  public void paintAll(Graphics2D g);
	  public PaireXY accelModif(Objet o);
	  public void majEffetsDiscontinusModif(Objet o);
	  public PaireXY accel();
	  public void majEffetsDiscontinus();
}
