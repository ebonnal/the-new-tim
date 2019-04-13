package TIM;//L3Informatique Orsay, projet IHM et Prog Concurrentielle, Enzo BONNAL 2017

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Objets.*;
import Panels.PanelJeu;


import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class FenetreTIM extends JFrame implements Objet {// cf interface Objet

	// j'avoue avoir eu la flemme de faire des .get() et .set() quand je pouvais
	// m'en passer pour gagner du temps (d'où le nombre d'attributs en
	// "public");

	// NOTE : Je n'ai commenté que les classes FentreTIM,ThreadWinChecker Ballon
	// et ThreadBallon
	// car les autres sont très semblables dans leur structure.
	public HashMap<String, BufferedImage> sprites = new HashMap<String, BufferedImage>();
	public String UP = "u", DOWN = "d", LEFT = "l", RIGHT = "r";
	public float speed = 1;// coefficient sur lequel joue le slider pour
							// augmenter ou baisser la vitesse.
	public int currLvl = 1;// Numero Niveau en cours (0 si ce n'est pas un des
							// 14 "niveaux officiels")
	public int lvlMax;// numero du dernier niveau officiel.
	public boolean editing = false;// vaut vrai si l'on est en mode EDITION
	// Tous nos widgets d'interface.
	public JButton modif, go, stop, saveB, importB, win, sol, ld, lu, tuto;
	public JTextField entry;//
	public JSlider speedSlider;
	//
	//
	public boolean dragging;
	public Objet objetFocused = this;// référence vers l'objet
	// qui tiens le
	// focus (dernier objet modifiable sur
	// lequel on a cliqué)
	public float gravite = 550; // gravité : acceleration en pixels.s^-2 selon y
	public boolean b = true; // bool qui rÃ©git les boucles des threads
	public PanelJeu pJeu;// Composant sur lequel on dessine tous sauf les
							// widgets qui sont sur le getContentPane() de la
							// Frame;
	public String currImport = "";// nom du fichier du niveau en cours (sans le
									// .txt ni le dossier "data/" dans lequel
									// ils se trouvent)
	public int w, h;// dimensions de la JFrame et de pJeu
	public int fps;// fps à speed=1;
	int tutoInt = 0;// variable servant pour les images d'introduction;
	boolean estEnSolution = false;// est vrai si le niveau actuel est une
									// solution importé (nom du niveau avec un s
									// devant).
	public long t;// sert de timer pour la boucle principale de repaint de la
					// thread de cette JFrame
	public File fi;// contient la grande image des sprites
	public boolean isRunning = false;
	// Annuaires objets :
	public HashMap<String, ArrayList<Objet>> annuaire;
	// Contient par type (clées de la HashMap)la liste (ArrayList)
	// des Objetsprésents dans le niveau affiché (excepté la JFrame)
	public HashMap<String, ArrayList<Objet>> annuaireReserve;
	// Contient avec la même Organisation que annuaire les objets créé durant
	// les différentes importations mais étant en surplus pour le niveau actuel
	// Cette réserve sert en priorité au lieu d'instancier de nouveaux objets.

	public FenetreTIM() {
		// Constructeur : lance la boucle de la JFrame.
		// Le Thread de la JFrame gère les widgets,les sauvegardes et les
		// importations.
		super("The Incredible Machine");
		BufferedImage sprite = null;
		this.fi = new File("data/sprites.png");
		try {
			sprite = ImageIO.read(fi);
			sprite = sprite.getSubimage(67, 14, 32, 32);
			sprites.put("Ballon", sprite);
		} catch (Exception e) {

		}

		try {
			sprite = ImageIO.read(fi);
			sprite = sprite.getSubimage(192, 1162, 22, 22);
			sprites.put("Mur", sprite);
		} catch (Exception e) {
		}
		try {

			sprite = ImageIO.read(fi);
			sprite = sprite.getSubimage(276, 1234, 32, 32);
			sprites.put("Ventilo1", sprite);

			sprite = ImageIO.read(fi);
			sprite = sprite.getSubimage(235, 1234, 32, 32);
			sprites.put("Ventilo2", sprite);

		} catch (Exception e) {
		}
		try {

			sprite = ImageIO.read(fi);
			sprite = sprite.getSubimage(15, 437, 37, 48);
			sprites.put("Panier", sprite);

		} catch (Exception e) {
		}

		this.setResizable(false);

		lvlMax = lvlMaxSearch();

		System.out.println("nb de niveau : " + lvlMax);
		w = 1200;
		h = 700;
		fps = 120;
		t = System.currentTimeMillis();
		try {
			BufferedImage image = ImageIO.read(this.fi);
			image = image.getSubimage(67, 14, 32, 32);

			this.setIconImage(image);
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File("data/alp.wav"));
			// Get a sound clip resource.
			///Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			///clip.open(audioIn);

			//clip.loop(50);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}/* catch (LineUnavailableException e) {
			e.printStackTrace();
		}*/

		annuaire = new HashMap<String, ArrayList<Objet>>();
		annuaireReserve = new HashMap<String, ArrayList<Objet>>();

		this.setLocation(0, 0);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		//Fullscreen:
		//fullScreen(this);
		// Je n'utilise pas de LayoutManager ici;
		startJeu();// Démarrage du Jeu

	}
	public static void fullScreen(JFrame j)
	  {
		  j.pack();
		  j.setDefaultLookAndFeelDecorated(true);
		  j.setExtendedState(j.MAXIMIZED_BOTH);
	  }
	int lvlMaxSearch() {
		int lvlM = 0;
		boolean bo = true;
		while (bo) {
			try {
				lvlM++;
				Scanner sc = new Scanner(new File("data/lvl" + lvlM + ".txt"));
				sc.close();

			} catch (Exception ee) {
				bo = false;
				lvlM--;
			}

		}
		return lvlM;
	}

	// Une partie des méthodes que je vais appeler les méthodes "physique"
	// (divers calculs pour la physique du jeu)
	// En temps qu'implémentation de la classe Objet, cette JFrame intéragie
	// avec les autres Objets (Ballons, ventilos etc...)
	// Elle modifie leur accélération en se chargeant de la gravité
	// Elle gère aussi les effets de variations des vitesses (selon x, y et
	// vitesse angulaire pour les ballons) discontinus
	// du aux chocs avec les bords de l'écran de jeu
	public PaireXY accelModif(Objet o) {
		// modification d'accélération appliquée aux objets (gravité)
		return new PaireXY(0, o.getCoefGravite() * gravite);
	}

	public void setterxfix(float coef, Objet o, int sign) {
		// une partie des calculs en rapport avec les effets de contact entre
		// Objets et bords de fenêtre :
		// ->perte d'énergie avec un seuil bas de vitesse qui fait décroitrte la
		// vitesse moins vite lorsque les objets roulent presque sur un bord
		// ->effets dus à la vitesse de rotation des ballons sur eux même : à
		// l'origine des "effets retro" nécessaires à la résolution notamment du
		// niveau 14
		if (Math.abs(o.getVy()) > 100) {
			o.setVx((float) (coef * o.getVx()));
		} else {
			o.setVx((float) (0.99 * o.getVx()));
		}
		o.setVy((float) (-coef * o.getVy()));
		if (o instanceof Ballon) {
			// Calcul de l'influence (pour les ballons) de leur vitesse de
			// rotation sur leur vitesse après contact :
			if (Math.abs(o.getVy()) > 100) {
				float coefRetro = Math.max(0, Math.min((100 / Ballon.norme(o.getVx(), o.getVy())) * 0.8f, 0.8f));

				o.setVx(((1 - coefRetro) * (o.getVx())
						+ sign * (coefRetro) * (float) (o.getVAngle() * Math.PI * o.getH() / 360)));
			}
			o.setVAngle(sign * (float) (360 * o.getVx() / Math.PI / o.getH()));
		}
	}

	public void setteryfix(float coef, Objet o, int sign) {
		// idem sur l'autre Axe
		if (Math.abs(o.getVx()) > 100) {
			o.setVy((float) (coef * o.getVy()));
		} else {
			o.setVy((float) (0.99 * o.getVy()));
		}
		o.setVx((float) (-coef * o.getVx()));
		if (o instanceof Ballon) {
			if (Math.abs(o.getVx()) > 100) {
				float coefRetro = Math.max(0, Math.min((100 / Ballon.norme(o.getVx(), o.getVy())) * 0.8f, 0.8f));

				o.setVy(((1 - coefRetro) * (o.getVy())
						+ sign * (coefRetro) * (float) (o.getVAngle() * Math.PI * o.getH() / 360)));
			}

			o.setVAngle(sign * (float) (360 * o.getVy() / Math.PI / o.getH()));

		}
	}

	public void majEffetsDiscontinusModif(Objet o) {
		// application des modifications de vitesse discontinues : rebond direct
		// avec angle "mirroir" et perte d'énergie pour les ventilos
		// et angle spécial pour les ballons (du à la prise en compte par les
		// deux fonctions précédente de leur vitesse angulaire)

		if (o.gety() >= 700 - o.getH() && o.getVy() >= 0) {// bord inférieur
			float coef = 0.8f;

			o.sety(700.0f - o.getH());

			setterxfix(coef, o, -1);
		}
		if (o.gety() <= 0 && o.getVy() <= 0) {// bord supérieur
			float coef = (float) Math.pow(0.5, 0.5);

			o.sety(0);

			setterxfix(coef, o, +1);
		}

		if (o.getx() >= 700 - o.getW() && o.getVx() >= 0) {// bord droit
			float coef = (float) Math.pow(0.5, 0.5);

			o.setx(700.0f - o.getW());

			setteryfix(coef, o, +1);
		}
		if (o.getx() <= 0 && o.getVx() <= 0) {// bord gauche
			float coef = (float) Math.pow(0.5, 0.5);

			o.setx(0);

			setteryfix(coef, o, -1);
		}

	}

	// implémentations inutiles (car la JFrame est un cas particulier d'Objet)
	// mais nécessaires de l'interface Objet:
	public int getEtat() {
		return 1;
	}

	public void erase() {
	}

	public void modifAction(String e) {
	}

	public void setExiste(boolean e) {
	}

	public boolean existe() {
		return true;
	}

	public int getH() {
		return 0;
	}

	public int getW() {
		return 0;
	}

	public void addAccelModifiers(Objet o) {
	}

	public void addVitesseModifiers(Objet o) {
	}

	public float getVAngle() {
		return 0;
	}

	public void setVAngle(float i) {
	}

	public float getx() {
		return 0f;
	}

	public float getVx() {
		return 0f;
	}

	public void setx(float nx) {
	}

	public void setVx(float nvx) {
	}

	public float gety() {
		return 0f;
	}

	public float getVy() {
		return 0f;
	}

	public void sety(float ny) {
	}

	public void setVy(float nvy) {
	}

	public BufferedImage getSprite() {
		return null;
	};

	public float getCoefGravite() {
		return 0;
	}

	public boolean estActif() {
		return true;
	}

	public void majEffetsDiscontinus() {
	}

	public void paintAll(Graphics2D g) {
	}

	public PaireXY accel() {
		return null;
	}

	public void threadStart() {
	}

	public void threadResetPause() {
	}

	public void startJeu() {// Méthode principale du thread JFrame appellée en
							// fin de constructeur:

		this.getContentPane().setPreferredSize(new Dimension(w, h));
		this.pack();
		// Démarrage de la thread qui regarde toutes les secondes si le joueur a
		// gagné le niveau courant :
		ThreadWinChecker threadWin = new ThreadWinChecker(this);
		// Instanciations des widgets :
		tuto = new JButton();
		getContentPane().add(tuto);
		tuto.setSize(1200, 700);
		tuto.setIcon(new ImageIcon("data/tuto1.png"));
		tuto.setLocation(0, 0);
		tuto.setVisible(true);
		tuto.setBorder(BorderFactory.createEmptyBorder());

		win = new JButton();
		getContentPane().add(win);
		win.setSize(1200, 700);
		win.setIcon(new ImageIcon("data/youwin.png"));
		win.setLocation(0, 0);
		win.setVisible(false);
		win.setBorder(BorderFactory.createEmptyBorder());

		speedSlider = new JSlider(JSlider.HORIZONTAL, -10, 10, 0);
		speedSlider.setPaintTicks(false);
		speedSlider.setPaintLabels(false);
		speedSlider.setForeground(Color.red);
		speedSlider.setBackground(Color.yellow);
		getContentPane().add(speedSlider);
		speedSlider.setSize(400, 12);
		speedSlider.setLocation(750, 75);

		entry = new JTextField();
		getContentPane().add(entry);
		entry.setSize(50, 20);
		entry.setLocation(1030, 100);
		entry.setBorder(BorderFactory.createEmptyBorder());
		entry.setBackground(new Color(100, 180, 200));

		go = new JButton();
		getContentPane().add(go);
		go.setSize(50, 50);
		go.setIcon(new ImageIcon("data/go.png"));
		go.setLocation(730, 20);
		go.setBorder(BorderFactory.createEmptyBorder());

		sol = new JButton();
		getContentPane().add(sol);
		sol.setSize(50, 50);
		sol.setIcon(new ImageIcon("data/solution.png"));
		sol.setLocation(730, 630);
		sol.setBorder(BorderFactory.createEmptyBorder());
		sol.setContentAreaFilled(false);

		ld = new JButton();
		getContentPane().add(ld);
		ld.setSize(50, 50);
		ld.setIcon(new ImageIcon("data/lvldown.png"));
		ld.setLocation(800, 630);
		ld.setBorder(BorderFactory.createEmptyBorder());
		ld.setContentAreaFilled(false);

		lu = new JButton();
		getContentPane().add(lu);
		lu.setSize(50, 50);
		lu.setIcon(new ImageIcon("data/lvlup.png"));
		lu.setLocation(860, 630);
		lu.setBorder(BorderFactory.createEmptyBorder());
		lu.setContentAreaFilled(false);

		saveB = new JButton();
		saveB.setContentAreaFilled(false);
		getContentPane().add(saveB);
		saveB.setSize(50, 50);
		saveB.setIcon(new ImageIcon("data/save.png"));
		saveB.setLocation(1030, 20);
		saveB.setBorder(BorderFactory.createEmptyBorder());

		importB = new JButton();
		importB.setContentAreaFilled(false);
		getContentPane().add(importB);
		importB.setSize(50, 50);
		importB.setIcon(new ImageIcon("data/import.png"));
		importB.setLocation(1130, 20);
		importB.setBorder(BorderFactory.createEmptyBorder());

		stop = new JButton();
		getContentPane().add(stop);
		stop.setSize(50, 50);
		stop.setIcon(new ImageIcon("data/stop.png"));
		stop.setLocation(830, 20);
		stop.setBorder(BorderFactory.createEmptyBorder());

		modif = new JButton();
		getContentPane().add(modif);
		modif.setSize(50, 50);
		modif.setIcon(new ImageIcon("data/modif.png"));
		modif.setLocation(930, 20);
		modif.setBorder(BorderFactory.createEmptyBorder());

		pJeu = new PanelJeu(1200, 700, this);
		getContentPane().add(pJeu);
		pJeu.setSize(w, h);
		pJeu.setLocation(0, 0);
		pJeu.setFocusable(true);
		pJeu.requestFocusInWindow();

		//

		// Gestions des Evenements sur nos widgets :

		// mise à jour du coef de vitesse par le slider : (de 0.6 à 1.5) en
		// échelle logarithmique
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				speed = ((int) (10 * Math.pow(10, speedSlider.getValue() / 8.0f))) / 10.0f;
				System.out.println(speed);
			}

		});

		// Bouton de lancement de partie appelle les méthodes .threadStart() des
		// différents Objets
		go.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!isRunning) {
					isRunning = true;
					pJeu.t = System.currentTimeMillis();
					Iterator<String> entrees = annuaire.keySet().iterator();
					while (entrees.hasNext()) {
						String currEntrees = entrees.next();
						for (int i = 0; i < annuaire.get(currEntrees).size(); i++) {
							annuaire.get(currEntrees).get(i).threadStart();
						}
					}
					if (threadWin.threadState == -2) {
						threadWin.start();
						threadWin.threadState = 1;
					} else {
						threadWin.threadState = 1;
					}
				}
				pJeu.requestFocusInWindow();// on redonne le Focus au panneau de
											// jeu pour les events au clavier
			}
		});

		// Bouton de Partie gagné (fait la taille de la fenetre et est au
		// premier plan)
		win.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				win.setVisible(false);
				isRunning = false;
				Iterator<String> entrees = annuaire.keySet().iterator();
				while (entrees.hasNext()) {
					String currEntrees = entrees.next();
					for (int i = 0; i < annuaire.get(currEntrees).size(); i++) {
						annuaire.get(currEntrees).get(i).threadResetPause();// appelle
																			// le
																			// .threadResetPause()
																			// de
																			// chaque
																			// objet
						annuaire.get(currEntrees).get(i).setExiste(true);
					}
				}
				if (threadWin.threadState == 1)
					threadWin.threadState = -1;
				// décide de la suite des évènement après une victoire:
				if (!estEnSolution && !editing)// si ce n'est pas déjà une
												// solution importée alors on
												// enregistre en tant que
												// solution ce que l'on vient de
												// faire
					saveToFile("s" + currImport, true);
				if (currLvl > 0 && !editing) {// si on est sur un niveau
												// officiel
					importFromFile("lvl" + String.valueOf((currLvl) % lvlMax + 1));// on
																					// passe
																					// au
																					// niveau
																					// suivant
				}
				if (currLvl < 0 && !editing) {// si on est sur une solution à un
												// niveau officiel :
					currLvl = -currLvl;// on repasse au niveau en question pour
										// essayer de le réussir cette fois
					importFromFile("lvl" + String.valueOf((currLvl)));
				}
				if (estEnSolution && currLvl == 0 && !editing) {// si on est sur
																// une solution
																// à un niveau
																// non officiel
					String res = "";
					for (int i = 1; i < currImport.length(); i++) {
						res += currImport.charAt(i);
					}
					importFromFile(res);// on repasse au niveau en question en
										// enlevant le 's' du début du nom.

				}
				if (estEnSolution)
					estEnSolution = false;
				pJeu.requestFocusInWindow();// on redonne le Focus au panneau de
											// jeu pour les events au clavier
			}
		});

		// Affichages des images d'intro dans ce bouton géant :
		tuto.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (tutoInt == 0) {
					tutoInt = 1;
					tuto.setIcon(new ImageIcon("data/tuto2.png"));

				} else {
					importFromFile("lvl1");
					// newBallon(300, 300, -200, -100, 3000, tthis(), false);
					// newBallon(400, 300, -200, -100, -3000, tthis(), false);

					tuto.setVisible(false);
				}

			}
		});

		// Le panneau force le focus sur lui (pour le clavier) lorsqu'on lui
		// clique dessus :
		pJeu.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				pJeu.requestFocusInWindow();

			}
		});

		// Gestion évènement au clavier :
		// Modifications des objets avec les flèches
		// et création des objets en mode édition
		pJeu.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (editing) {
					if (e.getKeyChar() == 'm') {

						newMur(800, 500, 0, 0, 1, tthis(), false);
					} else if (e.getKeyChar() == 'b') {
						newBallon(800, 500, 0, 0, 0, tthis(), false);
					} else if (e.getKeyChar() == 'v') {
						newVentilo(800, 500, 0, 0, 0, tthis(), false);
					} else if (e.getKeyChar() == 's') {
						newPanier(800, 500, 0, 0, 0, tthis(), false);
					} else if (e.getKeyChar() == 'd') {
						if (objetFocused instanceof Ballon) {
							annuaire.get("Ballons").remove(objetFocused);
							objetFocused.erase();
						} else if (objetFocused instanceof Ventilo) {
							annuaire.get("Ventilos").remove(objetFocused);
							objetFocused.erase();
						} else if (objetFocused instanceof Mur) {
							annuaire.get("Murs").remove(objetFocused);
							objetFocused.erase();
						} else {
							annuaire.get("Paniers").remove(objetFocused);
							objetFocused.erase();
						}

					}
				}
				// Fleches directionneles :
				if (e.getKeyCode() == e.VK_DOWN) {
					if (!isRunning && objetFocused != null)
						objetFocused.modifAction(DOWN);
				} else if (e.getKeyCode() == e.VK_UP) {
					if (!isRunning && objetFocused != null)
						objetFocused.modifAction(UP);
				} else if (e.getKeyCode() == e.VK_RIGHT) {
					if (!isRunning && objetFocused != null)
						objetFocused.modifAction(RIGHT);
				} else if (e.getKeyCode() == e.VK_LEFT) {
					if (!isRunning && objetFocused != null)
						objetFocused.modifAction(LEFT);
				}
				pJeu.requestFocusInWindow();
			}
		});
		// Bouton de sauvegarde qui se sert du contenu de "entry"
		saveB.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!isRunning && editing)
					saveToFile(entry.getText(), false);
				pJeu.requestFocusInWindow();
			}
		});
		// Bouton d'import qui se sert du contenu de "entry"
		importB.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {

				if (!isRunning) {
					int i = 0;
					try {
						Scanner sc = new Scanner(new File("data/" + entry.getText() + ".txt"));

						sc.close();

					} catch (Exception ee) {
						i = 1;
					}
					if (i == 0) {
						editing = false;
						importFromFile(entry.getText());

					}

				}
				pJeu.requestFocusInWindow();
			}
		}

		);
		// Passer au niveau suivant :
		lu.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (currLvl > 0 && !isRunning) {
					importFromFile("lvl" + String.valueOf((currLvl) % lvlMax + 1));
				}
				pJeu.requestFocusInWindow();
			}
		}

		);
		// Revenir au niveau précédent :
		ld.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (currLvl > 0 && !isRunning) {
					if (currLvl == 1) {
						importFromFile("lvl" + String.valueOf(lvlMax));
					}

					else {
						importFromFile("lvl" + String.valueOf(currLvl - 1));
					}

				}
				pJeu.requestFocusInWindow();
			}
		}

		);

		// Importer une solution au niveau courant si elle existe :
		sol.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!isRunning) {
					try {
						importFromFile("s" + currImport);
						estEnSolution = true;
					} catch (Exception ee) {
						System.out.println("Pas de solution encore trouvée pour " + currImport);
					}
				}
				pJeu.requestFocusInWindow();
			}
		}

		);

		// Stoper la simulation ou revenir au niveau à partir d'une solution qui
		// ne marche plus (ça arrive malheureusement :'( ) :
		stop.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!isRunning && currLvl < 0) {// retour à un niveau officiel
												// depuis une solution foireuse
												// arrêtée
					currLvl = -currLvl;
					importFromFile("lvl" + String.valueOf(currLvl));
					estEnSolution = false;
				} else if (!isRunning && estEnSolution) {// retour à un niveau
															// non officiel
															// depuis une
															// solution foireuse
															// arrêtée
					String res = "";
					for (int i = 1; i < currImport.length(); i++) {
						res += currImport.charAt(i);
					}
					importFromFile(res);
					estEnSolution = false;
				} else if (!isRunning && editing) {
					editing = false;
					saveToFile("temp", false);
					importFromFile(currImport);
				}

				else if (isRunning) {// Fin de la simulation pour tous les
										// autres cas
					isRunning = false;
					Iterator<String> entrees = annuaire.keySet().iterator();
					while (entrees.hasNext()) {
						String currEntrees = entrees.next();
						for (int i = 0; i < annuaire.get(currEntrees).size(); i++) {
							annuaire.get(currEntrees).get(i).threadResetPause();
							annuaire.get(currEntrees).get(i).setExiste(true);
						}
					}
					if (threadWin.threadState == 1)
						threadWin.threadState = -1;
				}
				pJeu.requestFocusInWindow();
			}
		});

		// Bouton de passage entre mode Jeu et mode édition
		modif.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (!isRunning) {
					estEnSolution = false;
					currLvl = 0;
					if (editing && entry.getText().equals("")) {
						editing = false;
						saveToFile("temp", false);
						importFromFile("lvl1");

					} else {
						editing = true;
						currLvl = 0;
						if (entry.getText().equals("")) {
							importFromFile("vide");
						} else {
							try {
								importFromFile(entry.getText());
							} catch (Exception ee) {
								importFromFile("vide");
								System.out
										.println(entry.getText() + " : ce niveau n'existe pas, impossible de l'editer");
							}
						}
					}
				}
				pJeu.requestFocusInWindow();

			}
		});

		this.setVisible(true);

		// Boucle principale de repaint de la thread :
		while (b) {

			if (System.currentTimeMillis() - t > 1000.0 / fps) {

				t = System.currentTimeMillis();
				getContentPane().revalidate();
				getContentPane().repaint();
			}

			Thread.yield();// lache sa priorité comme toutes les autres threads
							// après un tour de boucle;
		}

	}

	public void cleanAll() {
		// nettoie le jeu de tous ses objets en appellant la methode .erase() de
		// chacun d'eux et en effaçant leurs traces dans l'annuaire
		objetFocused = this;
		Iterator<String> entrees = annuaire.keySet().iterator();
		while (entrees.hasNext()) {

			String currEntrees = entrees.next();
			for (int i = 0; i < annuaire.get(currEntrees).size(); i++) {
				annuaire.get(currEntrees).get(i).erase();

			}
			annuaire.get(currEntrees).clear();
		}

	}

	public void saveToFile(String fileName, boolean estSol) {
		// fonction de sauvegarde dans le fichier /data/fileName.txt
		// noms interdit : commençant par un 's' (réservé aux sauvegardes) ou
		// par 'lvl'

		if (!isRunning) {

			boolean bool = false;
			if (entry.getText().length() >= 4)
				bool = (entry.getText().charAt(0) == 'e' && entry.getText().charAt(1) == 'd'
						&& entry.getText().charAt(2) == 'i' && entry.getText().charAt(3) == 't');
			if (bool || (!(fileName.charAt(0) == 'l' && fileName.charAt(1) == 'v' && fileName.charAt(2) == 'l')
					&& (fileName.charAt(0) != 's' || estSol))) {
				String res = "";
				Iterator<String> entrees = annuaire.keySet().iterator();
				while (entrees.hasNext()) {
					String currEntrees = entrees.next();
					for (int i = 0; i < annuaire.get(currEntrees).size(); i++) {
						res += String.valueOf(currEntrees.charAt(0))
								+ String.valueOf((int) annuaire.get(currEntrees).get(i).getx() + ";"
										+ (int) annuaire.get(currEntrees).get(i).gety() + ";"
										+ (int) annuaire.get(currEntrees).get(i).getEtat());
					}
				}
				if (bool && !estSol) {
					fileName = "";
					for (int i = 4; i < entry.getText().length(); i++) {
						fileName += entry.getText().charAt(i);
					}
				}
				try {
					Files.write(Paths.get("data/" + fileName + ".txt"), res.getBytes());
					System.out.println(fileName + " sauvegarde");
				} catch (Exception e) {
					System.out.println("fail save");
				}
			} else {
				System.out.println("impossible de sauvegarder par dessus un des 14 niveaux originels ou\n "
						+ "de choisir un nom commençant par 's' (réservé aux sauvegardes)\n, "
						+ "choisir un autre nom de niveau");
			}
			lvlMax = lvlMaxSearch();

		}
	}

	public void importFromFile(String fileName) {

		// import du fichier /data/fileName.txt

		// Quelques mots sur le format choisi : un .txt très simple avec les
		// items du niveau décrits à la suite et identifiés par :
		// premièreLettre;positionX;positionY;etat;
		lvlMax = lvlMaxSearch();
		String content = "";
		try {
			Scanner sc = new Scanner(new File("data/" + fileName + ".txt"));
			content = sc.useDelimiter("\\Z").next();
			sc.close();

		} catch (Exception e) {
			System.out.println("echec import");

		}
		System.out.println(fileName + " ouvert");

		if (fileName.equals("vide"))// niveau spécial "vide" (importé en début
									// de mode édition)
			cleanAll();

		if (content.length() != 0) {
			cleanAll();

			currImport = fileName;
			if (!editing && fileName.charAt(0) == 'l' && fileName.charAt(1) == 'v' && fileName.charAt(2) == 'l') {
				int lvl = 0;
				for (int i = 3; i < fileName.length(); i++) {
					lvl = 10 * lvl + Integer.parseInt(String.valueOf(fileName.charAt(i)));
				}
				currLvl = lvl;
				if (currLvl > lvlMax)
					lvlMax = currLvl;
			}

			else if (fileName.charAt(0) == 's' && fileName.charAt(1) == 'l' && fileName.charAt(2) == 'v'
					&& fileName.charAt(3) == 'l') {
				currLvl = -currLvl;
			} else {
				currLvl = 0;
			}
			int x = 0, y = 0, etat = 0;

			int i = 0;
			while (i < content.length()) {
				if (content.charAt(i) == 'B') {
					i++;
					x = 0;
					y = 0;
					while (content.charAt(i) != ';') {
						x = x * 10 + Integer.parseInt(String.valueOf(content.charAt(i)));
						i++;
					}
					i++;
					while (content.charAt(i) != ';') {
						y = y * 10 + Integer.parseInt(String.valueOf(content.charAt(i)));
						i++;
					}
					i++;
					// Note : on appelle ici la méthode newBallon (idem pour les
					// autres objets)
					// au lieu de créer une instance avec new Ballon(...) car
					// c'est newBallon
					// qui va géré les réutilisation d'instances stockées dans
					// annuaireReserve
					newBallon(x, y, 0, 0, 0, this, x < 700 && !editing);

				} else if (content.charAt(i) == 'V') {
					i++;
					x = 0;
					y = 0;
					while (content.charAt(i) != ';') {
						x = x * 10 + Integer.parseInt(String.valueOf(content.charAt(i)));
						i++;
					}
					i++;
					while (content.charAt(i) != ';') {
						y = y * 10 + Integer.parseInt(String.valueOf(content.charAt(i)));
						i++;
					}
					i++;
					etat = Integer.parseInt(String.valueOf(content.charAt(i)));
					newVentilo(x, y, 0, 0, etat, this, x < 700 && !editing);

				} else if (content.charAt(i) == 'P') {
					i++;
					x = 0;
					y = 0;
					while (content.charAt(i) != ';') {
						x = x * 10 + Integer.parseInt(String.valueOf(content.charAt(i)));
						i++;
					}
					i++;
					while (content.charAt(i) != ';') {
						y = y * 10 + Integer.parseInt(String.valueOf(content.charAt(i)));
						i++;
					}
					i++;
					etat = Integer.parseInt(String.valueOf(content.charAt(i)));
					newPanier(x, y, 0, 0, etat, this, x < 700 && !editing);

				} else if (content.charAt(i) == 'M') {
					i++;
					x = 0;
					y = 0;
					while (content.charAt(i) != ';') {
						x = x * 10 + Integer.parseInt(String.valueOf(content.charAt(i)));
						i++;
					}
					i++;
					while (content.charAt(i) != ';') {
						y = y * 10 + Integer.parseInt(String.valueOf(content.charAt(i)));
						i++;
					}
					i++;
					if (content.charAt(i) == '-') {
						i++;
						if (content.charAt(i) == '1') {

							try {
								Integer.parseInt(String.valueOf(content.charAt(i + 1)));
								etat = -(10 + Integer.parseInt(String.valueOf(content.charAt(i + 1))));
								i++;
							} catch (Exception e) {
								etat = -(Integer.parseInt(String.valueOf(content.charAt(i))));
							}

						} else {
							etat = -Integer.parseInt(String.valueOf(content.charAt(i)));
						}
					}

					else {
						if (content.charAt(i) == '1') {

							try {
								Integer.parseInt(String.valueOf(content.charAt(i + 1)));
								etat = (10 + Integer.parseInt(String.valueOf(content.charAt(i + 1))));
								i++;
							} catch (Exception e) {
								etat = (Integer.parseInt(String.valueOf(content.charAt(i))));
							}

						} else {
							etat = Integer.parseInt(String.valueOf(content.charAt(i)));
						}

					}
					newMur(x, y, 0, 0, etat, this, (x < 700) && !editing);

				}
				i++;
			}
		}
	}

	// Méthodes qui créent une Instance des Objets seulement si il n'en existe
	// pas en quantité suffisante dans annuaireRéserve.
	public void newMur(int x, int y, int vx, int vy, int etat, FenetreTIM f, boolean estFixe) {

		if (annuaireReserve.containsKey("Murs")) {

			if (annuaireReserve.get("Murs").size() > 0) {

				((Mur) (annuaireReserve.get("Murs").get(0))).resetMur(x, y, vx, vy, etat, this, estFixe);
			} else {
				new Mur(x, y, vx, vy, etat, this, estFixe);

			}
		} else {
			new Mur(x, y, vx, vy, etat, this, estFixe);
		}
	}

	public void newPanier(int x, int y, int vx, int vy, int etat, FenetreTIM f, boolean estFixe) {
		if (annuaireReserve.containsKey("Paniers")) {
			if (annuaireReserve.get("Paniers").size() > 0) {

				((Panier) (annuaireReserve.get("Paniers").get(0))).resetPanier(x, y, vx, vy, etat, this, estFixe);
			} else {
				new Panier(x, y, vx, vy, etat, this, estFixe);

			}
		} else {
			new Panier(x, y, vx, vy, etat, this, estFixe);
		}
	}

	public void newVentilo(int x, int y, int vx, int vy, int etat, FenetreTIM f, boolean estFixe) {
		if (annuaireReserve.containsKey("Ventilos")) {
			if (annuaireReserve.get("Ventilos").size() > 0) {

				((Ventilo) (annuaireReserve.get("Ventilos").get(0))).resetVentilo(x, y, vx, vy, etat, this, estFixe);
			} else {
				new Ventilo(x, y, vx, vy, etat, this, estFixe);

			}
		} else {
			new Ventilo(x, y, vx, vy, etat, this, estFixe);
		}
	}

	public void newBallon(int x, int y, int vx, int vy, float vangle, FenetreTIM f, boolean estFixe) {
		if (annuaireReserve.containsKey("Ballons")) {
			if (annuaireReserve.get("Ballons").size() > 0) {

				((Ballon) (annuaireReserve.get("Ballons").get(0))).resetBallon(x, y, vx, vy, vangle, this, estFixe);
			} else {
				new Ballon(x, y, vx, vy, vangle, this, estFixe);

			}
		} else {
			new Ballon(x, y, vx, vy, vangle, this, estFixe);
		}
	}

	public FenetreTIM tthis() {// utile car dans les extensions des _____Adapter
								// on n'a plus accès "au même this".
		return this;
	}

}
