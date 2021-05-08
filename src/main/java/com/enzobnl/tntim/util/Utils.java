package com.enzobnl.tntim.util;

import javax.swing.JFrame;

public class Utils {
    // m�thodes dont j'avait besoin pour certains calculs :
    public static int signedUnity(float f) {
        if (f > 0) {
            return 1;
        } else if (f == 0f) {
            return 0;
        }
        return -1;
    }

    public static float norme(float fx, float fy) {
        return ((float) Math.sqrt(Math.pow(fx, 2) + Math.pow(fy, 2)));
    }

    /**
     * sinus entre deux vecteurs 2D
     */
    public static float sinus(float x1, float y1, float x2, float y2) {
        if (Math.abs(norme(x1, y1)) <= 0.0005 || Math.abs(norme(x2, y2)) <= 0.0005)
            return 0;
        return (x1 * y2 - y1 * x2) / norme(x1, y1) / norme(x2, y2);
    }

    public static float cosinus(float x1, float y1, float x2, float y2) {// cosinus
        // entre
        // deux
        // vecteurs
        // 2D
        if (Math.abs(norme(x1, y1)) <= 0.0005 || Math.abs(norme(x2, y2)) <= 0.0005)
            return 0;
        return (x1 * x2 + y1 * y2) / norme(x1, y1) / norme(x2, y2);
    }

    public static float angle(float x1, float y1, float x2, float y2) {
        return ((float) Math.acos(cosinus(x1, y1, x2, y2)));
    }

    public static void fullScreen(JFrame j) {
        j.pack();
        j.setDefaultLookAndFeelDecorated(true);
        j.setExtendedState(j.MAXIMIZED_BOTH);
    }
}
