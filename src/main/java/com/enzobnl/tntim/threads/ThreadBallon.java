package com.enzobnl.tntim.threads;

import com.enzobnl.tntim.FenetreTIM;
import com.enzobnl.tntim.items.Ballon;
import com.enzobnl.tntim.util.Vector;

public class ThreadBallon extends Thread {
    FenetreTIM f;
    Ballon ballon;
    long t;
    
	// 0:jamais encore lancé mais lancable 1:
	// lancé -1: en pause -2:pas lancable(objet
	// dans menu)
    public int threadState = -2;

    public ThreadBallon(FenetreTIM f, Ballon ballon) {

        this.f = f;
        this.ballon = ballon; // Instance de Ballon que g�re notre thread
        t = System.currentTimeMillis();
    }

    public void run() {
        int ms = (int) (1000.0 / (f.fps));

        float coefAction = ms / 1000.0f;
        while (f.b) {

            // coef qui pond�re les incr�mentations pour garantir le
            // m�me comportement � toute valeur de f.fps (valable seulement pour
            // f.speed=1)
            while (f.b && threadState == 1) {
                if (ballon.estActif()) {
                    if (System.currentTimeMillis() - t > ms / f.speed && ballon.existe) {
                        t = System.currentTimeMillis();

                        // on effectue les effets
                        // discontinus sur la
                        // vitesse de l'instance

                        Vector accel = ballon.accel(); // On r�cup�re les
                        // modification
                        // d'acc�l�ration que l'on
                        // subit
                        // (Force/masse r�duit � l'acc�l�ration car les deux
                        // sont arbitraires ici)

                        // Simple m�thode d'euler de r�solution de l'�quation
                        // diff�rentielle du mouvement :
                        ballon.ay = accel.getY();
                        ballon.ax = accel.getX();
                        ballon.vx += ballon.ax * (coefAction);
                        ballon.x += ballon.vx * (coefAction);
                        ballon.vy += ballon.ay * (coefAction);
                        ballon.y += ballon.vy * (coefAction);
                        float xi = ballon.x, yi = ballon.y;
                        ballon.majEffetsDiscontinus();
                        // idem pour la rotation :
                        {
                            ballon.angle += ballon.vangle * (coefAction);
                        }

                    }

                }

                Thread.yield();
            }
            while (threadState == -1) {// boucle dans laquelle le
                // thread rentre lorsqu'il
                // est dans son �tat de
                synchronized (ballon) {
                    try {
                        ballon.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }                            // pause (-1)

                }
            }


        }

    }
}
