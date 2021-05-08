package com.bonnalenzo.tntim;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Random;

public class PanelJeu extends JPanel {
    Font popo = new Font("Tahoma", Font.BOLD, 30);
    public Image bg;
    public Color c;
    public long t;
    public FenetreTIM f;

    public Random r = new Random();


    public PanelJeu(int l, int h, FenetreTIM f) {
        try {

            bg = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("data/fond.png"));
        } catch (Exception e) {
        }

        t = System.currentTimeMillis();
        this.f = f;
    }

    public void paintComponent(Graphics g2) {

        Graphics2D g = (Graphics2D) g2;
        super.paintComponent(g);
        //g.scale(0.5,0.5);

        g.drawImage(bg, 0, 0, this);

        g.setColor(c);
        g.setFont(popo);
        if (f.isRunning) {
            g.drawString("   " + String.valueOf((System.currentTimeMillis() - t) / 1000), 1020, 650);

        } else if (f.editing) {
            g.drawString("EDITING", 1000, 650);
        } else if (f.currLvl > 0) {
            g.drawString("Niveau " + f.currLvl, 980, 650);
        } else if (f.currLvl < 0) {
            g.drawString("Solution" + (-f.currLvl), 980, 650);

        } else {
            g.drawString(f.currImport, 1000, 650);
        }

        try {
            paintAllObjets(g);
        } catch (Exception e) {
        }


    }

    void paintAllObjets(Graphics2D g) {
        Iterator<String> entrees = f.annuaire.keySet().iterator();
        String currEntree;

        while (entrees.hasNext()) {

            currEntree = entrees.next();
            try {
                f.annuaire.get(currEntree).get(0).paintAll(g);
            } catch (Exception e) {
            }

        }


    }

    public void drawImageMaison(Graphics2D g, BufferedImage img, int x, int y, float angledeg, float scalex, float scaley, boolean estFlipped) {
        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.translate(img.getWidth() / 2 * scalex, img.getHeight() / 2 * scaley);
        at.scale(scalex, scaley);
        if (estFlipped) at.scale(-1, 1);
        at.rotate(-angledeg / 180.0 * Math.PI);

        at.translate(-img.getWidth() / 2, -img.getHeight() / 2);
        g.drawImage(img, at, null);
    }
}