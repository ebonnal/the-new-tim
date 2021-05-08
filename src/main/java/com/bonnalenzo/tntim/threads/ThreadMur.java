package com.bonnalenzo.tntim.threads;


import com.bonnalenzo.tntim.items.Mur;
import com.bonnalenzo.tntim.FenetreTIM;

public class ThreadMur extends Thread {

    FenetreTIM f;
    Mur mur;
    long t;

    // 0:jamais encore lancé mais lancable 1:
    // lancé -1: en pause -2:pas lancable(objet
    // dans menu)
    public int threadState = -2;

    public ThreadMur(FenetreTIM f, Mur bal) {

        this.f = f;
        this.mur = bal;

        t = System.currentTimeMillis();
    }

    public void run() {
        int ms = (int) (1000.0 / (f.fps));
        while (f.b) {


            while (f.b && threadState == 1) {
                if (mur.estActif()) {
                    if (System.currentTimeMillis() - t > ms / f.speed && mur.existe) {

                        t = System.currentTimeMillis();

                    }

                }

                Thread.yield();
            }
            while (threadState == -1) {
                synchronized (mur) {
                    try {
                        mur.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } // pause (-1)

                }
            }
        }
    }
}
