package com.enzobnl.tntim.threads;

import com.enzobnl.tntim.FenetreTIM;
import com.enzobnl.tntim.items.Ventilo;
import com.enzobnl.tntim.util.Vector;

import java.awt.image.BufferedImage;

public class ThreadVentilo extends Thread {

    FenetreTIM f;
    Ventilo ventilo;
    long t, tanim;

    // 0:jamais encore lancé mais lancable 1:
    // lancé -1: en pause -2:pas lancable(objet
    // dans menu)
    public int threadState = -2;

    public ThreadVentilo(FenetreTIM f, Ventilo bal) {

        this.f = f;
        this.ventilo = bal;

        t = System.currentTimeMillis();
        tanim = System.currentTimeMillis();
    }

    public void run() {
        int ms = (int) (1000.0 / (f.fps));
        float coefAction = ms / 1000.0f;
        int scms = (int) (20 * Math.log(f.speed + 1));
        while (f.b) {


            while (f.b && threadState == 1) {
                if (ventilo.estActif()) {
                    if (System.currentTimeMillis() - t > ms / f.speed && ventilo.existe) {

                        t = System.currentTimeMillis();
                        Vector accel = ventilo.accel();
                        ventilo.ay = accel.getY();
                        ventilo.ax = accel.getX();
                        ventilo.vx += ventilo.ax * (coefAction);
                        ventilo.x += ventilo.vx * (coefAction);
                        ventilo.vy += ventilo.ay * (coefAction);
                        ventilo.y += ventilo.vy * (coefAction);
                        ventilo.majEffetsDiscontinus();

                    }
                    if (System.currentTimeMillis() - tanim > (int) (20 * Math.log(17.7 + 1)) - scms + 30 && ventilo.existe && f.speed != 0) {
                        tanim = System.currentTimeMillis();

                        scms = (int) (20 * Math.log(f.speed + 1));

                        BufferedImage temp = ventilo.sprite;
                        ventilo.sprite = ventilo.sprite2;
                        ventilo.sprite2 = temp;


                    }

                }

                Thread.yield();
            }
            while (threadState == -1) {
                synchronized (ventilo) {
                    try {
                        ventilo.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }                            // pause (-1)

                }
            }
        }
    }
}
