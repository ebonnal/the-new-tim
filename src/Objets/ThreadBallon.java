package Objets;

import TIM.FenetreTIM;
import Objets.Ballon;

public class ThreadBallon extends Thread {
	FenetreTIM f;
	Ballon bal;
	long t;

	public int threadState = -2;// 0:jamais encore lancÃ© mais lancable 1:
								// lancÃ© -1: en pause -2:pas lancable(objet
								// dans menu)

	public ThreadBallon(FenetreTIM f, Ballon bal) {

		this.f = f;
		this.bal = bal;// Instance de Ballon que gère notre thread
		t = System.currentTimeMillis();
	}

	public void run() {
		int ms = (int) (1000.0 / (f.fps));

		float coefAction = ms / 1000.0f;
		while (f.b ) {
			
			// coef qui pondère les incrémentations pour garantir le
			// même comportement à toute valeur de f.fps (valable seulement pour
			// f.speed=1)
			while (f.b && threadState == 1 ) {
				if (bal.estActif()) {
					if (System.currentTimeMillis() - t > ms / f.speed && bal.existe) {
						t = System.currentTimeMillis();

						// on effectue les effets
												// discontinus sur la
													// vitesse de l'instance

						PaireXY accel = bal.accel();// On récupère les
													// modification
													// d'accélération que l'on
													// subit
						// (Force/masse réduit à l'accélération car les deux
						// sont arbitraires ici)

						// Simple méthode d'euler de résolution de l'équation
						// différentielle du mouvement :
						bal.ay = accel.y;
						bal.ax = accel.x;
						bal.vx += bal.ax * (coefAction);
						bal.x += bal.vx * (coefAction);
						bal.vy += bal.ay * (coefAction);
						bal.y += bal.vy * (coefAction);
						float xi=bal.x,yi=bal.y;
						bal.majEffetsDiscontinus();	
						// idem pour la rotation :
						{

							bal.angle += bal.vangle * (coefAction);
						} 

					}

				}

				Thread.yield();
			}
			while (threadState == -1 ) {// boucle dans laquelle le
													// thread rentre lorsqu'il
													// est dans son état de
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
