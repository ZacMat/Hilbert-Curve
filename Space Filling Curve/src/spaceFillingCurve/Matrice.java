package spaceFillingCurve;

import java.awt.Point;

public class Matrice {

	Point[] matrice;
	int maxX;
	int minX;
	int maxY;
	int minY;

	int etendueX;
	int etendueY;

	public Matrice() {

	}

	public Matrice(int[][] m) {
		matrice = new Point[m.length];
		for (int i = 0; i < m.length; i++) {
			Point p = new Point(m[i][0], m[i][1]);
			matrice[i] = p;
		}
		setupMinMax();
	}

	public Matrice(int longueur) {
		matrice = new Point[longueur];
		for (int i = 0; i < matrice.length; i++) {
			matrice[i] = new Point();
		}
	}

	public void setupMinMax() {
		if (matrice.length > 0) {
			maxX = 0;
			minX = 0;
			maxY = 0;
			minY = 0;
			for (int i = 0; i < matrice.length; i++) {
				if (matrice[i].x > maxX) {
					maxX = matrice[i].x;
				}
				if (matrice[i].x < minX) {
					minX = matrice[i].x;
				}
				if (matrice[i].y > maxY) {
					maxY = matrice[i].y;
				}
				if (matrice[i].y < minY) {
					minY = matrice[i].y;
				}
			}
			etendueX = maxX - minX;
			etendueY = maxY - minY;
			if (maxX < 1) maxX = 1;
			if (minX < 1) minX = 1;
			if (maxY < 1) maxY = 1;
			if (minY < 1) minY = 1;
		}
	}

	public Matrice getNextIteration() {
		Matrice m1 = getMatTournee(90);
		m1 = m1.ajouterValeur(etendueX, etendueY + 1);
		m1.inverserY();
		Matrice m3 = this.ajouterValeur(etendueX + 1, 0);
		Matrice m4 = getMatTournee(270);
		m4 = m4.ajouterValeur(etendueX + 1, (etendueY * 2) + 1);
		m4.inverserY();

		Matrice mF1 = m1.ajouter(this).ajouter(m3).ajouter(m4);
		mF1.setupMinMax();
		return mF1;
	}

	public Matrice getMatTournee(int angle) {
		int sinA = 0;
		int cosA = 0;
		if (angle == 90) {
			sinA = 1;
			cosA = 0;
		} else if (angle == 270) {
			sinA = -1;
			cosA = 0;
		}
		return multMat(new Matrice(new int[][] { { cosA, sinA }, { -sinA, cosA } }), this);
	}

	public static Matrice multMat(Matrice m1, Matrice m2) {
		Matrice retour = new Matrice(m2.matrice.length);
		for (int i = 0; i < m2.matrice.length; i++) {
			retour.matrice[i].x = (m1.matrice[0].x * m2.matrice[i].x) + (m1.matrice[1].x * m2.matrice[i].y);
			retour.matrice[i].y = (m1.matrice[0].y * m2.matrice[i].x) + (m1.matrice[1].y * m2.matrice[i].y);

//			System.out.println("Mult Mat : " + (i + 1) + "/" + m2.matrice.length);
		}
		return retour;
	}

	public Matrice ajouterValeur(int x, int y) {
		Matrice retour = this.getCopie();
		for (int i = 0; i < matrice.length; i++) {
			retour.matrice[i].x += x;
			retour.matrice[i].y += y;
		}
		return retour;
	}

	public Matrice ajouter(Matrice m) {
		Matrice retour = new Matrice(this.matrice.length + m.matrice.length);
		for (int i = 0; i < matrice.length; i++) {
			retour.matrice[i] = matrice[i];
		}
		for (int i = 0; i < m.matrice.length; i++) {
			retour.matrice[i + matrice.length] = m.matrice[i];
		}
		return retour;
	}

	public Matrice getCopie() {
		Matrice retour = new Matrice(matrice.length);
		for (int i = 0; i < retour.matrice.length; i++) {
			retour.matrice[i] = new Point(matrice[i].x, matrice[i].y);
		}
		retour.setupMinMax();
		return retour;
	}

	public Matrice getCopiePartielle(float val) {
		Matrice retour = new Matrice(Math.round(matrice.length * val));
		for (int i = 0; i < retour.matrice.length; i++) {
			retour.matrice[i] = new Point(matrice[i].x, matrice[i].y);
		}
		retour.setupMinMax();
		retour.minX = minX;
		retour.maxX = maxX;
		retour.minY = minY;
		retour.maxY = maxY;
		retour.etendueX = etendueX;
		retour.etendueY = etendueY;
		return retour;
	}

	public void inverserY() {
		for (int i = 0; i < (matrice.length) / 2; i++) {
			Point p = matrice[i];
			matrice[i] = matrice[matrice.length - 1 - i];
			matrice[matrice.length - 1 - i] = p;
		}
	}
}
