package com.enzobnl.tntim.items;

import com.enzobnl.tntim.FenetreTIM;
import com.enzobnl.tntim.threads.ThreadPanier;
import com.enzobnl.tntim.util.Vector;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import static com.enzobnl.tntim.util.Utils.norme;


public class Panier extends MouseAdapter implements Objet {
    public boolean estFixe;
    public int etat = 0; // 0: à droite,1:en haut,2:à gauche,3:en bas
    public ArrayList<Objet> accelModifiers;
    public ArrayList<Objet> vitesseModifiers;
    public BufferedImage sprite;
    public boolean estActif;
    public float coefGravite = 0;
    public float x, y;
    public float angle, vangle;
    public float vx, vy;
    public float ax, ay;
    public float xi, yi;
    int xdrag, ydrag;
    public float anglei, vanglei;
    public float vxi, vyi;
    public float axi, ayi;
    public boolean isDragged;
    public boolean existe;
    FenetreTIM f;
    public ThreadPanier thread;

    public Panier(float x, float y, float vx, float vy, int etat, FenetreTIM f, boolean estFixe) {
        this.etat = etat;
        estActif = false;
        existe = true;
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
        this.vxi = vx;
        this.vyi = vy;
        this.axi = 0;
        this.ayi = 0;
        this.vangle = 0;
        this.vanglei = 0;
        this.angle = 0;
        this.anglei = angle;
        this.f = f;
        thread = new ThreadPanier(f, this);
        if (estFixe)
            thread.threadState = 0;

        if ((f.annuaire).containsKey("Paniers")) {
            f.annuaire.get("Paniers").add((Objet) this);
        } else {
            f.annuaire.put("Paniers", new ArrayList<Objet>());
            f.annuaire.get("Paniers").add((Objet) this);
        }

        sprite = f.sprites.get("Panier");
    }

    public void resetPanier(float x, float y, float vx, float vy, int etat, FenetreTIM f, boolean estFixe) {

        this.etat = etat;
        estActif = false;
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
        this.vangle = 0;
        this.vanglei = 0;
        this.angle = 0;
        this.anglei = angle;
        this.f = f;


        if (estFixe && thread.threadState == -2) {
            thread.threadState = 0;
        }
        if ((f.annuaire).containsKey("Paniers")) {
            f.annuaire.get("Paniers").add((Objet) this);
        } else {
            f.annuaire.put("Paniers", new ArrayList<Objet>());
            f.annuaire.get("Paniers").add((Objet) this);
        }
        if ((f.annuaireReserve).containsKey("Paniers")) {
            f.annuaireReserve.get("Paniers").remove((Objet) this);
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
            if (isDragged && x + getW() <= 700 && x >= 0 && y + getH() <= 700 && y >= 0 && !estPasUnePosAutorisee()) {
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
                    if (m.x + m.getW() / 2 == x + getW() / 2 && m.y + m.getH() / 2 == y + getH() / 2)
                        return true;
                }
            }
        }
        if ((f.annuaire).containsKey("Paniers")) {

            for (int i = 0; i < (f.annuaire.get("Paniers").size()); i++) {
                if (((Panier) ((f.annuaire).get("Paniers").get(i))).existe()) {
                    Panier m = (Panier) ((f.annuaire).get("Paniers").get(i));
                    if (m != this) {
                        if (m.x + m.getW() / 2 == x + getW() / 2 && m.y + m.getH() / 2 == y + getH() / 2)
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
        if (etat == 0 || etat == 2)
            return sprite.getHeight() * 2;
        return sprite.getWidth() * 2;
    }

    public int getW() {
        if (etat == 0 || etat == 2)
            return sprite.getWidth() * 2;
        return sprite.getHeight() * 2;
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
            thread.start();
        }
        if (thread.threadState == -1) {
            xi = x;
            yi = y;
            thread.threadState = 1;
            synchronized (this) {
                this.notify();
            }
        }
    }

    public Vector accel() {
        Vector accel = new Vector(0, 0);
        for (int i = 0; i < accelModifiers.size(); i++) {
            if (accelModifiers.get(i).estActif()) {
                if (accelModifiers.get(i).existe()) {
                    accel.addVector(accelModifiers.get(i).accelModif(this));
                }
            }
        }
        return accel;
    }

    public Vector accelModif(Objet o) {
        if (o instanceof Ballon) {
            float x1 = x + getW() / 2;
            float x2 = o.getx() + o.getW() / 2;
            float y1 = y + getH() / 2;
            float y2 = o.gety() + o.getH() / 2;

            if (Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) < Math.pow((o.getH() / 2 + (getH() + getW()) / 3.5f), 2)) {
                float normx = (x2 - x1) / ((getH() + getW()) / 3.5f + o.getH() / 2);
                float normy = (y2 - y1) / ((getH() + getW()) / 3.5f + o.getH() / 2);
                float normnorm = norme(normx, normy);
                normx = normx / normnorm;
                normy = normy / normnorm;
                float temp = 1000 * ((o.getH() / 2 + (getH() + getW()) / 3.5f) - norme(x1 - x2, y1 - y2));

                if (etat == 0) {
                    if (y2 < y1) {
                        o.setExiste(false);
                    }
                } else if (etat == 2) {
                    if (y2 > y1) {

                        o.setExiste(false);

                    }
                } else if (etat == 1) {
                    if (x2 < x1) {

                        o.setExiste(false);

                    }
                } else if (etat == 3) {
                    if (x2 > x1) {

                        o.setExiste(false);
                    }
                }
                o.setVx(0.95f * o.getVx());
                o.setVy(0.95f * o.getVy());
                return new Vector(temp * normx, temp * normy);

            }

        }
        if (o instanceof Ventilo) {

            float x1 = x + getW() / 2;
            float x2 = o.getx() + o.getW() / 2;
            float y1 = y + getH() / 2;
            float y2 = o.gety() + o.getH() / 2;

            if (Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) < Math.pow((o.getH() / 2 + (getH() + getW()) / 3.5f), 2)) {
                float normx = (x2 - x1) / ((getH() + getW()) / 3.5f + o.getH() / 2);
                float normy = (y2 - y1) / ((getH() + getW()) / 3.5f + o.getH() / 2);
                float normnorm = norme(normx, normy);
                normx = normx / normnorm;
                normy = normy / normnorm;
                o.setVx(0.95f * o.getVx());
                o.setVy(0.95f * o.getVy());
                float temp = 1000 * ((o.getH() / 2 + (getH() + getW()) / 3.5f) - norme(x1 - x2, y1 - y2));
                return new Vector(temp * normx, temp * normy);
            }

        }
        return new Vector(0, 0);
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
        if (existe) {

        }
    }

    public void modifAction(String e) {
        boolean etatEstPair = etat % 2 == 0;
        if (e == f.RIGHT) {
            etat = 3;
        } else if (e == f.LEFT) {
            etat = 1;
        } else if (e == f.DOWN) {
            etat = 2;
        } else if (e == f.UP) {
            etat = 0;
        } else {
            etat = (etat + 1) % 4;

        }
        if (etatEstPair) {
            if (etat % 2 != 0) {
                x -= Math.abs(getW() - getH()) / 2;
                y += Math.abs(getW() - getH()) / 2;
            }
        } else {
            if (etat % 2 == 0) {
                x += Math.abs(getW() - getH()) / 2;
                y -= Math.abs(getW() - getH()) / 2;
            }
        }
    }

    public void paintAll(Graphics2D g) {

        if ((f.annuaire).containsKey("Paniers")) {

            for (int i = 0; i < (f.annuaire.get("Paniers").size()); i++) {
                if (((Panier) ((f.annuaire).get("Paniers").get(i))).existe()) {

                    Panier p = ((Panier) ((f.annuaire).get("Paniers").get(i)));
                    float angle = p.angle;
                    int xx = (int) (p.x);
                    int yy = (int) (p.y);

                    if (p.etat % 2 != 0) {
                        xx += Math.abs((p.getW() - p.getH())) / 2;
                        yy -= Math.abs((p.getW() - p.getH())) / 2;
                    }

                    f.pJeu.drawImageMaison(g, ((Panier) ((f.annuaire).get("Paniers").get(i))).sprite, xx, yy,
                            angle + ((Panier) ((f.annuaire).get("Paniers").get(i))).etat * 90, 2, 2,
                            ((Panier) ((f.annuaire).get("Paniers").get(i))).etat == 2);
                }
            }
        }

    }

    public void erase() {
        if ((f.annuaireReserve).containsKey("Paniers")) {
            f.annuaireReserve.get("Paniers").add((Objet) this);
        } else {
            f.annuaireReserve.put("Paniers", new ArrayList<Objet>());
            f.annuaireReserve.get("Paniers").add((Objet) this);

        }
        isDragged = false;
        estActif = false;
        existe = true;
        if (thread.threadState == 0) {
            thread.threadState = -2;
        }
        if (thread.threadState != -2) {
            thread.threadState = -1;
        }/*
         * thread.estSuppr = true; accelModifiers = null; vitesseModifiers =
         * null; thread = null;
         */

    }

}
