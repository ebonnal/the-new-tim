package Objets;

import java.awt.image.BufferedImage;

import TIM.FenetreTIM;

public class ThreadVentilo extends Thread {

	FenetreTIM f;
	Ventilo bal;
	long t,tanim;
	public int threadState = -2;// 0:jamais encore lancé mais lancable 1:
								// lancé -1: en pause -2:pas lancable(objet
								// dans menu)

	public ThreadVentilo(FenetreTIM f, Ventilo bal) {

		this.f = f;
		this.bal = bal;

		t = System.currentTimeMillis();
		tanim = System.currentTimeMillis();
	}

	public void run() {
		int ms = (int) (1000.0 / (f.fps));
		float coefAction = ms / 1000.0f;
		int scms=(int)(20*Math.log(f.speed+1));
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
					if (System.currentTimeMillis() - tanim >(int)(20*Math.log(17.7+1))-scms+30  && bal.existe && f.speed!=0) {
						tanim = System.currentTimeMillis();

						scms=(int)(20*Math.log(f.speed+1));						

						BufferedImage temp = bal.sprite;
						bal.sprite = bal.sprite2;
						bal.sprite2 = temp;
							

					}

				}

				Thread.yield();
			}
			while (threadState == -1 ) {
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
