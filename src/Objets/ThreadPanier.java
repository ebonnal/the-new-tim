package Objets;

import TIM.FenetreTIM;

public class ThreadPanier extends Thread {

	FenetreTIM f;
	Panier bal;
	long t;
	public int threadState = -2;// 0:jamais encore lancé mais lancable 1:
								// lancé -1: en pause -2:pas lancable(objet
								// dans menu)

	public ThreadPanier(FenetreTIM f, Panier bal) {

		this.f = f;
		this.bal = bal;

		t = System.currentTimeMillis();
	}

	public void run() {
		int ms = (int) (1000.0 / (f.fps));
		float coefAction = ms / 1000.0f;
		while (f.b) {

			

			while (f.b && threadState == 1 ) {
				if (bal.estActif()) {
					if (System.currentTimeMillis() - t > ms / f.speed && bal.existe) {
						t = System.currentTimeMillis();
						PaireXY accel = bal.accel();
						bal.ay = accel.y;
						bal.ax = accel.x;
						bal.vx += bal.ax * (coefAction);
						bal.x += bal.vx * (coefAction);
						bal.vy += bal.ay * (coefAction);
						bal.y += bal.vy * (coefAction);
						bal.majEffetsDiscontinus();

					}

				}

				Thread.yield();
			}
			while (threadState == -1) {
				synchronized(bal)
				{
				try {
							bal.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}							// pause (-1)
			
				}
			}
		}
	}
}
