package com.bonnalenzo.tntim.items;

import com.bonnalenzo.tntim.util.Vector;

import java.awt.Graphics2D;

public interface Objet {
    int getEtat();

    void erase();

    void modifAction(String e);

    boolean existe();

    void setExiste(boolean e);

    int getH();

    int getW();

    void addAccelModifiers(Objet o);

    void addVitesseModifiers(Objet o);

    float getVAngle();

    void setVAngle(float i);

    float getx();

    float getVx();

    void setx(float i);

    void setVx(float i);

    float gety();

    float getVy();

    void sety(float i);

    void setVy(float i);

    float getCoefGravite();

    boolean estActif();

    void threadStart();

    void threadResetPause();

    void paintAll(Graphics2D g);

    Vector accelModif(Objet o);

    void majEffetsDiscontinusModif(Objet o);

    Vector accel();

    void majEffetsDiscontinus();
}
