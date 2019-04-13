package Objets;

import TIM.FenetreTIM;

public class ThreadMur extends Thread {

	FenetreTIM f;
	Mur bal;
	long t;
	public int threadState = -2;// 0:jamais encore lancé mais lancable 1:
								// lancé -1: en pause -2:pas lancable(objet
								// dans menu)

	public ThreadMur(FenetreTIM f, Mur bal) {

		this.f = f;
		this.bal = bal;

		t = System.currentTimeMillis();
	}

	public void run() {
		int ms = (int) (1000.0 / (f.fps));
		while (f.b ) {
			

			while (f.b && threadState == 1 ) {
				if (bal.estActif()) {
					if (System.currentTimeMillis() - t > ms / f.speed && bal.existe) {

						t = System.currentTimeMillis();

					}

				}

				Thread.yield();
			}
			while (threadState == -1  ) {
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
