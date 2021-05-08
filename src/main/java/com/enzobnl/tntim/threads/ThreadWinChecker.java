package com.enzobnl.tntim.threads;


import com.enzobnl.tntim.FenetreTIM;

public class ThreadWinChecker extends Thread {

    private FenetreTIM f;
    private long t;
    public boolean estSuppr = false;
    public int threadState = -2;

    public ThreadWinChecker(FenetreTIM f) {
        this.f = f;
        t = System.currentTimeMillis();
    }

    public void run() {
        while (f.b && !estSuppr) {
            while (f.b && threadState == 1 && !estSuppr) {
                if (System.currentTimeMillis() - t > 1000) {
                    // Le thread regarde toutes les secondes si le niveau est r�ussi :
                    //cad si le niveau contient des Instances de Ballons et qu'elles sont toutes soit en �tat "n'existe plus" soit inactives :
                    t = System.currentTimeMillis();
                    if (f.annuaire.containsKey("Ballons")) {
                        int i = 0;
                        while (i < f.annuaire.get("Ballons").size() && (!(f.annuaire.get("Ballons").get(i).estActif()) || !(f.annuaire.get("Ballons").get(i).existe()))) {
                            i++;
                        }
                        if (i == f.annuaire.get("Ballons").size()) {
                            f.win.setVisible(true); // Lance la victoire en rendant visible (et cliquable) le boutons g�ant win
                        }
                    }

                }
                Thread.yield();
            }
            while (threadState == -1) {
                Thread.yield();
            }
        }

    }
}
