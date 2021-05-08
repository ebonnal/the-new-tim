package com.bonnalenzo.tntim.threads;

import com.bonnalenzo.tntim.util.Vector;
import com.bonnalenzo.tntim.FenetreTIM;
import com.bonnalenzo.tntim.items.Panier;

public class ThreadPanier extends Thread {

    FenetreTIM f;
    Panier panier;
    long t;

    // 0:jamais encore lancé mais lancable 1:
    // lancé -1: en pause -2:pas lancable(objet
    // dans menu)
    public int threadState = -2;

    public ThreadPanier(FenetreTIM f, Panier bal) {

        this.f = f;
        this.panier = bal;

        t = System.currentTimeMillis();
    }

    public void run() {
        int ms = (int) (1000.0 / (f.fps));
        float coefAction = ms / 1000.0f;
        while (f.b) {


            while (f.b && threadState == 1) {
                if (panier.estActif()) {
                    if (System.currentTimeMillis() - t > ms / f.speed && panier.existe) {
                        t = System.currentTimeMillis();
                        Vector accel = panier.accel();
                        panier.ay = accel.getY();
                        panier.ax = accel.getX();
                        panier.vx += panier.ax * (coefAction);
                        panier.x += panier.vx * (coefAction);
                        panier.vy += panier.ay * (coefAction);
                        panier.y += panier.vy * (coefAction);
                        panier.majEffetsDiscontinus();

                    }

                }

                Thread.yield();
            }
            while (threadState == -1) {
                synchronized (panier) {
                    try {
                        panier.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // pause (-1)

                }
            }
        }
    }
}
