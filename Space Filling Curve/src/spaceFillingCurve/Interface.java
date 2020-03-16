package spaceFillingCurve;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class Interface extends Application {

	public static final Dimension DIMENSION = Toolkit.getDefaultToolkit().getScreenSize();
//	public static final double LARGEUR_INTERFACE = DIMENSION.getWidth();
//	public static final double HAUTEUR_INTERFACE = DIMENSION.getHeight();
	public static final double LARGEUR_INTERFACE = 1000;
	public static final double HAUTEUR_INTERFACE = 1000;
	public static final double MULT_X = 0.9;
	public static final double MULT_Y = 0.9;
	public static final double DECALAGE_X = (LARGEUR_INTERFACE - (LARGEUR_INTERFACE * MULT_X)) / 2;
	public static final double DECALAGE_Y = (HAUTEUR_INTERFACE - (HAUTEUR_INTERFACE * MULT_Y)) / 2;
	public static final int MAX_INT_COULEUR = 1530;
	public static final double LARGEUR_LIGNE = 3;

	public static final int VITESSE_THREAD = 10;
	public static float VITESSE_AFFICHAGE = 0.00256f;

	public static final int ITERATION_BASE = 1;

	Scene scene;

	Canvas can;
	GraphicsContext gc;

	public Thread t;
	public boolean threadVivant = false;

	public float progresAffichage = 0f;

//	Matrice m = new Matrice(new int[][] { { 0, 1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } });
	Matrice m = new Matrice(new int[][] { { 0, 0 } });
	int iteration = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stagePrincipal) throws Exception {
		BorderPane root = new BorderPane();
		can = new Canvas(LARGEUR_INTERFACE, HAUTEUR_INTERFACE);
		root.getChildren().add(can);
		gc = can.getGraphicsContext2D();

		scene = new Scene(root, LARGEUR_INTERFACE, HAUTEUR_INTERFACE);

		scene.setOnScroll(e -> {
			if (e.getDeltaY() >= 0) {
				Scale s = new Scale(2, 2);
				root.getTransforms().addAll(s);
			} else {
				Scale s = new Scale(0.5, 0.5);
				root.getTransforms().addAll(s);
			}
		});

		scene.setOnKeyPressed(e -> toucheAppuillee(stagePrincipal, e));
		stagePrincipal.setScene(scene);
		stagePrincipal.setOnCloseRequest(e -> System.exit(0));
		stagePrincipal.setTitle("Hilbert Curve (Par Zac)");
		stagePrincipal.show();

//		afficherMatriceLignes(m, gc);
		partirThread();
		augmenterInteration(ITERATION_BASE);
	}

	public void partirThread() {
		if (!threadVivant) {
			t = new Thread(() -> {
				boolean marche = true;
				while (marche) {
					Platform.runLater(() -> {
						dessiner();
						faireTourAffichage();
					});
					try {
						Thread.sleep(VITESSE_THREAD);
					} catch (InterruptedException e) {
						marche = false;
					}
				}
			});
			t.start();
			threadVivant = true;
		}
	}

	public void dessiner() {
//		System.out.println(progresAffichage + " " + progresAffichage * m.matrice.length + " -> "
//				+ Math.round(progresAffichage * m.matrice.length));
		afficherMatriceLignes(m.getCopiePartielle(progresAffichage), gc);
	}

	public void faireTourAffichage() {
		if (progresAffichage + ((2 * VITESSE_AFFICHAGE) / iteration) > 1) {
			t.interrupt();
			threadVivant = false;
			progresAffichage = 1;
			dessiner();
		} else {
			progresAffichage += ((2 * VITESSE_AFFICHAGE) / iteration);
		}
	}

	public void toucheAppuillee(Stage stage, KeyEvent e) {
		if (e.getCode() == KeyCode.SPACE) {
			augmenterInteration(1);
		} else if (e.getCode() == KeyCode.P) {
			if (threadVivant) {
				t.interrupt();
				threadVivant = false;
			} else {
				partirThread();
			}
		} else if (e.getCode() == KeyCode.R) {
			progresAffichage = 0;
			dessiner();
		} else if (e.getCode() == KeyCode.S) {
			progresAffichage = 1;
		} else if (e.getCode() == KeyCode.UP) {
			VITESSE_AFFICHAGE *= 2;
			System.out.println(VITESSE_AFFICHAGE);
		} else if (e.getCode() == KeyCode.DOWN) {
			VITESSE_AFFICHAGE /= 2;
			System.out.println(VITESSE_AFFICHAGE);
		} else {
			System.out.println("Pas Space");
		}
	}

	public void augmenterInteration(int nombreFois) {
		for (int i = 0; i < nombreFois; i++) {
			m = m.getNextIteration();
			iteration++;
			System.out.println(iteration);
			if (nombreFois == 1) {
				dessiner();
			}
		}
	}

	public void afficherMatriceLignes(Matrice matrice, GraphicsContext gc) {
		try {
			gc.clearRect(0, 0, LARGEUR_INTERFACE, HAUTEUR_INTERFACE);
			double agrandissementFormeX = ((LARGEUR_INTERFACE) / matrice.maxX);
			double agrandissementFormeY = ((HAUTEUR_INTERFACE) / matrice.maxY);
			for (int i = 0; i < matrice.matrice.length; i++) {
				double indexCourant = i;
				double IndexMax = matrice.matrice.length;
//				ligne.setFill(getIntToColor((indexCourant / IndexMax) * MAX_INT_COULEUR));
				gc.setStroke(getIntToColor((indexCourant / IndexMax) * MAX_INT_COULEUR));
//				gc.setLineWidth(LARGEUR_LIGNE / (2 ^ iteration));
				gc.setLineWidth(LARGEUR_LIGNE);
				gc.strokeLine(matrice.matrice[i].x * agrandissementFormeX * MULT_X + DECALAGE_X,
						matrice.matrice[i].y * agrandissementFormeY * MULT_Y + DECALAGE_Y,
						matrice.matrice[i + 1].x * agrandissementFormeX * MULT_X + DECALAGE_X,
						matrice.matrice[i + 1].y * agrandissementFormeY * MULT_Y + DECALAGE_Y);
			}
		} catch (IndexOutOfBoundsException e) {
//			System.out.println("Affichage Terminé");
//			genImage("" + iteration);
		}
	}

	public Paint getIntToColor(double i) {
		Paint retour = Color.BLACK;
		int opacite = 1;
		if (i <= 255) {
			double index = i - 0;
			retour = new Color(1, index / 255, 0, opacite);
		} else if ((i > 255) && (i <= 510)) {
			double index = i - 255;
			retour = new Color((255 - index) / 255, 1, 0, opacite);
		} else if ((i > 510) && (i <= 765)) {
			double index = i - 510;
			retour = new Color(0, 1, index / 255, opacite);
		} else if ((i > 765) && (i <= 1020)) {
			double index = i - 765;
			retour = new Color(0, (255 - index) / 255, 1, opacite);
		} else if ((i > 1020) && (i <= 1275)) {
			double index = i - 1020;
			retour = new Color(index / 255, 0, 1, opacite);
		} else if ((i > 1275) && (i <= 1530)) {
			double index = i - 1275;
			retour = new Color(1, 0, (255 - index) / 255, opacite);
		}
		return retour;
	}

	public void genImage(String nom) {
		System.out.println("gen image " + nom);
		WritableImage wI = new WritableImage((int) can.getWidth(), (int) can.getHeight());
		wI = can.snapshot(new SnapshotParameters(), wI);
		File f = new File(nom + ".png");
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(wI, null), "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
