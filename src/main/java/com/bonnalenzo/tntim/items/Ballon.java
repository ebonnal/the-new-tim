package com.bonnalenzo.tntim.items;

import com.bonnalenzo.tntim.FenetreTIM;
import com.bonnalenzo.tntim.threads.ThreadBallon;
import com.bonnalenzo.tntim.util.Vector;

import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

import static com.bonnalenzo.tntim.util.Utils.norme;
import static com.bonnalenzo.tntim.util.Utils.sinus;

public class Ballon extends MouseAdapter implements Objet {

    public boolean estFixe; // indique si l'objet est modifiable
    public int etat = 0; // etat inutile pour le ballon
    public ArrayList<Objet> accelModifiers; // Liste des Instances qui modifient
    // l'accel�rationde notre Instance
    public ArrayList<Objet> vitesseModifiers; // Liste des Instances qui
    // modifient la vitesse de notre
    // Instance
    public BufferedImage sprite; // image
    public boolean estActif; // indique si l'Instance est en �tat de participer �
    // une simulation qui se lancerait
    public float coefGravite = 1; // coef d'interaction gravitationnelle (n�gatif
    // pour simuler un ballon gonfl� � l'helium
    // par exemple)
    public float x, y; // position courante
    public float angle, vangle; // angle et vitesse angulaire
    public float vx, vy; // vitesse
    public float ax, ay; // accel�ration
    // sauvegardes de la derni�re position autoris�e (car drag and drop �
    // certains endroits est interdit) :
    public float xi, yi;
    int xdrag, ydrag;
    public float anglei, vanglei;
    public float vxi, vyi;
    public float axi, ayi;
    public boolean isDragged; // indique si l'Instance est en train de se faire
    // drag and drop
    FenetreTIM f; // R�f�rence vers la JFrame
    public ThreadBallon thread; // thread associ�e

    // indique si l'objet "existe" (les ballons sont "d�truit" au cours d'une simulation
    // (mang�s par les sauts) mais ce n'est pas l'Instance qui doit l'�tre bienentendu.
    public boolean existe;

    public Ballon(float x, float y, float vx, float vy, float vangle, FenetreTIM f, boolean estFixe) {
        // constructeur
        estActif = false;
        etat = 0;
        existe = true;
        this.estFixe = estFixe;
        f.pJeu.addMouseListener(this); // On r�cup�re les �v�nement souris du
        // panel de jeu
        f.pJeu.addMouseMotionListener(this);
        accelModifiers = new ArrayList<Objet>();
        vitesseModifiers = new ArrayList<Objet>();
        accelModifiers.add(f);
        vitesseModifiers.add(f);

        Iterator<String> entrees = f.annuaire.keySet().iterator();
        while (entrees.hasNext()) {
            // On indique notre pr�sence aux autres com.enzobnl.tntim.objets en s'inscrivant dans
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
        // Cr�ation du thread associ� pr�t � �tre lanc�
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
        sprite = f.sprites.get("Ballon");

    }

    public void resetBallon(float x, float y, float vx, float vy, float vangle, FenetreTIM f, boolean estFixe) {
        // M�thode tr�s proche du constructeur qui permet de recycler une
        // Instance en la r�initialisant :


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
        if (estFixe && thread.threadState == -2) {
            thread.threadState = 0;
        }


        if ((f.annuaire).containsKey("Ballons")) {
            f.annuaire.get("Ballons").add((Objet) this);
        } else {
            f.annuaire.put("Ballons", new ArrayList<Objet>());
            f.annuaire.get("Ballons").add((Objet) this);
        }

        // Principale diff�rence avec le constructeur :
        // Comme on a �t� pioch� dans l'annuaireReserve, il faut s'en
        // d�sinscrire !
        if ((f.annuaireReserve).containsKey("Ballons")) {
            f.annuaireReserve.get("Ballons").remove((Objet) this);
        } else {
        }


    }

    // Gestion des �v�nements souris :

    @Override
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

    @Override
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

    @Override
    public void mouseReleased(MouseEvent e) {
        // fin potentielle de drag and drop
        // -> d�termine si la position d'arriv� est convenable � l'aide de la
        // m�thode : estPasUnePosAutorisee()
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

    private boolean estPasUnePosAutorisee() {
        // d�termine si la position est autoris� :
        // R�gles :LES VENTILATEURS ET BALLONS NE PEUVENT PAS CHEVAUCHER LES
        // MURS.
        // LES ITEMS NE PEUVENT PAS DEPASSER LES REBORDS DE L'ECRAN
        // et deux panier/entilateur/ballon ne peuvent pas avoir le m�me centre
        // (car on ne peut pas d�terminer dans quelle direction ils se
        // repoussent dans ce cas l�)
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

    // M�thodes banales :
    @Override
    public void addAccelModifiers(Objet o) {
        accelModifiers.add(o);
    }

    @Override
    public void addVitesseModifiers(Objet o) {
        vitesseModifiers.add(o);
    }

    @Override
    public int getEtat() {
        return etat;
    }

    @Override
    public boolean existe() {
        return existe;
    }

    @Override
    public void setExiste(boolean e) {
        existe = e;
    }

    @Override
    public void modifAction(String e) {
    }

    @Override
    public int getH() {
        return sprite.getHeight();
    }

    @Override
    public int getW() {
        return sprite.getWidth();
    }

    @Override
    public float getVAngle() {
        return vangle;
    }

    @Override
    public void setVAngle(float i) {
        vangle = i;
    }

    @Override
    public float getx() {
        return x;
    }

    @Override
    public float getVx() {
        return vx;
    }

    @Override
    public void setx(float nx) {
        x = nx;
    }

    @Override
    public void setVx(float nvx) {
        vx = nvx;
    }

    @Override
    public float gety() {
        return y;
    }

    @Override
    public float getVy() {
        return vy;
    }

    @Override
    public void sety(float ny) {
        y = ny;
    }

    @Override
    public void setVy(float nvy) {
        vy = nvy;
    }

    @Override
    public float getCoefGravite() {
        return coefGravite;
    }

    @Override
    public boolean estActif() {
        return estActif;
    }

    // FIN METHODES BANALES

    @Override
    public void threadResetPause() {// Mise en pause du thread et mises � jour
        // de position car cete m�thode est appell�e
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

    @Override
    public void threadStart() {// D�marrage de la thread ou sortie de Pause

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

    @Override
    public Vector accel() {
        // Calcul de notre acc�l�ration actuelle � partir des modification que
        // l'on re�oit des ".accelModif()" des �l�ments de notre catalogue
        // d'accelModifiers
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

    @Override
    public Vector accelModif(Objet o) {
        // m�thode que les Objets qui nous ont dans leur catalogue
        // d'accelModifiers vont appeler pour savoir comment on modifie leur
        // accel�ration
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
                    float temp = 1000 * (getH() - norme(x1 - x2, y1 - y2)); // force
                    // centrale
                    // de
                    // rappel
                    // �lastique
                    // calcul de la nouvelle vitesse angulaire � partir des
                    // vitesses des deux ballons et
                    // du vecteur unitaire (normx,normy) qui va du centre de
                    // notre Instance vers celui de l'autre

                    o.setVAngle((o.getVAngle() - getVAngle()) / 2 + ((float) (360
                            * (sinus(normx, normy, vx, vy) * norme(vx, vy)
                            + sinus(-normx, -normy, o.getVx(), o.getVy()) * norme(o.getVx(), o.getVy()))
                            / Math.PI / getH())) / 1.2f);

                    setVAngle((getVAngle() - o.getVAngle()) / 2 + ((float) (360
                            * (sinus(-normx, -normy, o.getVx(), o.getVy()) * norme(o.getVx(), o.getVy())
                            + sinus(normx, normy, getVx(), getVy()) * norme(getVx(), getVy()))
                            / Math.PI / getH())) / 1.2f);

                    o.setVx(0.95f * o.getVx()); // l�g�re perte de
                    // vitesse/�nergie
                    o.setVy(0.95f * o.getVy());
                    return new Vector(temp * normx, temp * normy);
                }
            }
            if (o instanceof Ventilo) {
                // M�me principe en plus simple car pas de rotation g�r� entre
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
                    return new Vector(temp * normx, temp * normy);
                }
            }
        }
        return new Vector(0, 0);

    }

    @Override
    public void majEffetsDiscontinus() {
        // Application des modifications discontinues de notre vitesse �
        // partir des ".majEffetsDiscontinusModif()" des �l�ments de notre
        // catalogue de vitesseModifiers

        for (int i = 0; i < vitesseModifiers.size(); i++) {
            if (vitesseModifiers.get(i).estActif()) {
                if (vitesseModifiers.get(i).existe()) {
                    vitesseModifiers.get(i).majEffetsDiscontinusModif(this);

                }
            }
        }

    }

    @Override
    public void majEffetsDiscontinusModif(Objet o) {
        // m�thode vide car les ballons n'ont aucune influence discontinue sur
        // les autres �l�ments
    }


    @Override
    public void paintAll(Graphics2D g) {// m�thode qui permet de peindre toutes
        // les Instancces de Ballons que
        // contient l'annuaire de la JFrame
        // (m�thode appel�e par le JPanel pJeu)

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

    @Override
    public void erase() {
        // L'instance quitte momentan�ment le jeu et est plac�e dans
        // l'annuaireReserve
        if ((f.annuaireReserve).containsKey("Ballons")) {
            f.annuaireReserve.get("Ballons").add((Objet) this);
        } else {
            f.annuaireReserve.put("Ballons", new ArrayList<Objet>());
            f.annuaireReserve.get("Ballons").add((Objet) this);
        }
        isDragged = false;
        estActif = false;
        existe = true;
        if (thread.threadState == 0) {
            thread.threadState = -2;
        }
        if (thread.threadState != -2) {
            thread.threadState = -1;
        }
        // on tue la thread associ�e en changeant ce
        // bool�en
        // (elle devra �tre recr��e lors du prochain recyclage de notre instance
        // avec .resetBallon()

    }
}
