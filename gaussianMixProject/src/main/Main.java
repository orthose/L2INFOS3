package main;

import partitionnement.*;
import image.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * @apiNote Classe de lancement des méthodes associées aux questions du projet.
 *          La plupart des questions consistent à déterminer les meilleurs
 *          paramètres à utiliser pour un jeu de données. Pour ce faire, on ne
 *          fera varier qu'un paramètre à la fois dans chacune de ces questions.
 *          C'est pourquoi nous avons dû écrire des méthodes supplémentaires.
 */
public class Main {

	/**
	 * @apiNote Lancement des questions une à une en commentant et décommentant.
	 */
	public static void main(String[] args) throws IOException {
		// question1_centre();
		// question1_deviation();
		// question1_density();
		// question1_iteration();
		question3();
		// compression();
	}

	/**
	 * @apiNote Question 1 dont le but est de faire tourner l'algorithme avec 10
	 *          gaussiennes et dans 10 conditions initiales différentes. Le calcul
	 *          du score permettra de déterminer à quel intervalle d'incertitude on
	 *          s'expose lorsque l'on choisit d'initialiser les centres
	 *          aléatoirement. Ici, on fixe les méta-paramètres et on ne fait varier
	 *          que les positions initiales des centres entre chaque apprentissage.
	 */
	public static void question1_centre() throws IOException {
		// Chemin de l'image à analyser
		String path = "./src/image/";
		String imageMMS = path + "mms.png";

		// Fichier de sauvegarde des scores à interpréter avec gnuplot
		FileWriter fw = new FileWriter("question1_centre.d");

		System.out.println("Initialisation des données");

		// Chargement de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		// Récupération d'un tableau de couleur
		Color[] tabColor = LoadSavePNG.loadPNG(bui);

		// Extraction de couleur sous forme de donnée normalisée
		double[][] normalizedColor = LoadSavePNG.normaliseColor(tabColor, false);

		// On est à 3 dimensions avec 10 gaussiennes
		int K = 10; // Dix centres
		double[][] data = normalizedColor;

		System.out.println("Construction d'un kmoyenne de mixture de gaussiennes");
		KmeansGaussianMix kmoyenne = new KmeansGaussianMix(data, K);

		// Initialisation des paramètres de l'algorithme
		System.out.println("Initialisation de la mixture de gaussiennes");
		// Ordre de grandeur pour la variance
		kmoyenne.setOrderOfMagnitude(0.5);
		// Initialisation des centres avec aléas et des autres paramètres
		// Cette initialisation automatique suit les recommendations du sujet
		kmoyenne.initialise();
		// Nombre d'itérations de l'apprentissage
		int maxIteration = 20;

		// Dix apprentissages consécutifs
		for (int numberLearning = 0; numberLearning < 10; numberLearning++) {

			System.out.println("Apprentissage " + numberLearning + " lancé");
			kmoyenne.runLearning(maxIteration);

			System.out.println("Fin d'apprentissage");

			// Affichage pur
			for (int k = 0; k < K; k++) {
				System.out.println("Centre " + k + ": " + "  rouge: " + kmoyenne.getCentre()[k][0] + "  vert: " + kmoyenne.getCentre()[k][1] + "  bleu: " + kmoyenne.getCentre()[k][2]);
			}

			System.out.println("\n" + "Affichage dénormalisé");

			// Affichage dénormalisé
			for (int k = 0; k < K; k++) {
				Color c = LoadSavePNG.denormaliseColor(kmoyenne.getCentre()[k]);
				System.out.println("Centre " + k + ": " + "  rouge: " + c.getRed() + "  vert: " + c.getGreen() + "  bleu: " + c.getBlue());
			}

			// Calcul du score total et enregistrement dans fichier
			fw.write(numberLearning + " " + kmoyenne.score() + "\n");

			// Réinitialisation de l'algorithme pour le prochain apprentissage
			kmoyenne.initialise();

			// Saut de ligne affichage console
			System.out.print("\n");
		}
		fw.close();
	}

	/**
	 * @apiNote On effectue 10 apprentissages en faisant varier l'écart-type initial
	 *          des gaussiennes dans [0.05 ; 0.50]. Le but est de déterminer la
	 *          meilleure valeur d'écart-type pour le jeu de données en calculant le
	 *          score de l'algorithme à chaque apprentissage.
	 */
	public static void question1_deviation() throws IOException {
		// Chemin de l'image à analyser
		String path = "./src/image/";
		String imageMMS = path + "mms.png";

		// Fichier de sauvegarde des scores à interpréter avec gnuplot
		FileWriter fw = new FileWriter("question1_deviation.d");

		System.out.println("Initialisation des données");

		// Chargement de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		// Récupération d'un tableau de couleur
		Color[] tabColor = LoadSavePNG.loadPNG(bui);

		// Extraction de couleur sous forme de donnée normalisée
		double[][] normalizedColor = LoadSavePNG.normaliseColor(tabColor, false);

		int D = 3; // Trois dimensions
		int K = 10; // Dix centres
		double[][] data = normalizedColor;

		System.out.println("Construction d'un kmoyenne de mixture de gaussiennes");
		KmeansGaussianMix kmoyenne = new KmeansGaussianMix(data, K);

		// Initialisation des paramètres de l'algorithme
		System.out.println("Initialisation de la mixture de gaussiennes");
		// Initialisation des centres avec aléas (on gardera ces centres
		// pour réinitialiser l'algorithme)
		kmoyenne.initialise();
		double[][] centre = new double[K][D];
		for (int k = 0; k < K; k++) {
			centre[k] = Arrays.copyOf(kmoyenne.getCentre()[k], kmoyenne.getCentre()[k].length);
		}
		// Initialisation de la variance à 0.05
		double deviation = 0.05;
		kmoyenne.setDeviation(deviation);
		// Nombre d'itérations de l'apprentissage
		int maxIteration = 20;

		// Dix apprentissages consécutifs
		for (int numberLearning = 0; numberLearning < 10; numberLearning++) {

			System.out.println("Apprentissage " + numberLearning + " lancé");
			kmoyenne.runLearning(maxIteration);

			System.out.println("Fin d'apprentissage");

			// Affichage pur
			for (int indexCentre = 0; indexCentre < K; indexCentre++) {
				System.out.println("Centre " + indexCentre + ": " + "  rouge: " + kmoyenne.getCentre()[indexCentre][0] + "  vert: " + kmoyenne.getCentre()[indexCentre][1] + "  bleu: " + kmoyenne.getCentre()[indexCentre][2]);
			}

			System.out.println("\n" + "Affichage dénormalisé");

			// Affichage dénormalisé
			for (int indexCentre = 0; indexCentre < K; indexCentre++) {
				Color c = LoadSavePNG.denormaliseColor(kmoyenne.getCentre()[indexCentre]);
				System.out.println("Centre " + indexCentre + ": " + "  rouge: " + c.getRed() + "  vert: " + c.getGreen() + "  bleu: " + c.getBlue());
			}

			// Calcul du score total et enregistrement dans fichier
			fw.write(deviation + " " + kmoyenne.score() + "\n");

			// Réinitialisation de l'algorithme pour le prochain apprentissage
			// On reprend les positions des centres de la première initialisation
			double[][] temp = new double[K][D];
			for (int k = 0; k < K; k++) {
				temp[k] = Arrays.copyOf(centre[k], centre[k].length);
			}
			kmoyenne.setCentre(temp);

			// On réinitialise le tableau d'assignement
			kmoyenne.initialiseDataGaussian();
			// On réinitialise la densité
			kmoyenne.setDensity(1. / K);
			// On incrémente 0.05 la variance
			deviation += 0.05;
			kmoyenne.setDeviation(deviation);

			// Saut de ligne affichage console
			System.out.print("\n");
		}
		fw.close();
	}

	/**
	 * On effectue 10 apprentissages en faisant varier la densité initiale des 10
	 * gaussiennes dans [1 / 10 ; 1 / 1]. Le but est de déterminer la meilleure
	 * valeur de la densité pour le jeu de données en calculant le score de
	 * l'algorithme à chaque apprentissage.
	 */
	public static void question1_density() throws IOException {
		// Chemin de l'image à analyser
		String path = "./src/image/";
		String imageMMS = path + "mms.png";

		// Fichier de sauvegarde des scores à interpréter avec gnuplot
		FileWriter fw = new FileWriter("question1_density.d");

		System.out.println("Initialisation des données");

		// Chargement de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		// Récupération d'un tableau de couleur
		Color[] tabColor = LoadSavePNG.loadPNG(bui);

		// Extraction de couleur sous forme de donnée normalisée
		double[][] normalizedColor = LoadSavePNG.normaliseColor(tabColor, false);

		int D = 3; // Trois dimensions
		int K = 10; // Dix centres
		double[][] data = normalizedColor;

		System.out.println("Construction d'un kmoyenne de mixture de gaussiennes");
		KmeansGaussianMix kmoyenne = new KmeansGaussianMix(data, K);

		// Initialisation des paramètres de l'algorithme
		System.out.println("Initialisation de la mixture de gaussiennes");
		// Initialisation des centres avec aléas (on gardera ces centres
		// pour réinitialiser l'algorithme)
		kmoyenne.initialise();
		double[][] centre = new double[K][D];
		for (int k = 0; k < K; k++) {
			centre[k] = Arrays.copyOf(kmoyenne.getCentre()[k], kmoyenne.getCentre()[k].length);
		}
		// Initialisation de la densité à 1 / K
		double density = 1. / (double) K;
		kmoyenne.setDensity(density);
		// Nombre d'itérations de l'apprentissage
		int maxIteration = 20;

		// Dix apprentissages consécutifs
		for (int numberLearning = 0; numberLearning < 10; numberLearning++) {

			System.out.println("Apprentissage " + numberLearning + " lancé");
			kmoyenne.runLearning(maxIteration);

			System.out.println("Fin d'apprentissage");

			// Affichage pur
			for (int indexCentre = 0; indexCentre < K; indexCentre++) {
				System.out.println("Centre " + indexCentre + ": " + "  rouge: " + kmoyenne.getCentre()[indexCentre][0] + "  vert: " + kmoyenne.getCentre()[indexCentre][1] + "  bleu: " + kmoyenne.getCentre()[indexCentre][2]);
			}

			System.out.println("\n" + "Affichage dénormalisé");

			// Affichage dénormalisé
			for (int indexCentre = 0; indexCentre < K; indexCentre++) {
				Color c = LoadSavePNG.denormaliseColor(kmoyenne.getCentre()[indexCentre]);
				System.out.println("Centre " + indexCentre + ": " + "  rouge: " + c.getRed() + "  vert: " + c.getGreen() + "  bleu: " + c.getBlue());
			}

			// Calcul du score total et enregistrement dans fichier
			fw.write(density + " " + kmoyenne.score() + "\n");

			// Réinitialisation de l'algorithme pour le prochain apprentissage
			// On reprend les positions des centres de la première initialisation
			double[][] temp = new double[K][D];
			for (int k = 0; k < K; k++) {
				temp[k] = Arrays.copyOf(centre[k], centre[k].length);
			}
			kmoyenne.setCentre(temp);

			// On réinitialise le tableau d'assignement
			kmoyenne.initialiseDataGaussian();
			// On réinitialise la variance à 0.5
			kmoyenne.setDeviation(0.5);
			// On fait diminuer le dénominateur de 1
			density = 1. / (double) (K - (numberLearning + 1));
			kmoyenne.setDensity(density);

			// Saut de ligne affichage console
			System.out.print("\n");
		}
		fw.close();
	}

	/**
	 * On effectue 10 apprentissages en faisant varier le nombre de tours de boucle
	 * de l'algorithme dans [5 ; 50]. Le but est de déterminer la meilleure valeur
	 * du nombre d'itérations pour le jeu de données en calculant le score de
	 * l'algorithme à chaque apprentissage.
	 */
	public static void question1_iteration() throws IOException {
		// Chemin de l'image à analyser
		String path = "./src/image/";
		String imageMMS = path + "mms.png";

		// Fichier de sauvegarde des scores à interpréter avec gnuplot
		FileWriter fw = new FileWriter("question1_iteration.d");

		System.out.println("Initialisation des données");

		// Chargement de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		// Récupération d'un tableau de couleur
		Color[] tabColor = LoadSavePNG.loadPNG(bui);

		// Extraction de couleur sous forme de donnée normalisée
		double[][] normalizedColor = LoadSavePNG.normaliseColor(tabColor, false);

		int D = 3; // Trois dimensions
		int K = 10; // Dix centres
		double[][] data = normalizedColor;

		System.out.println("Construction d'un kmoyenne de mixture de gaussiennes");
		KmeansGaussianMix kmoyenne = new KmeansGaussianMix(data, K);

		// Initialisation des paramètres de l'algorithme
		System.out.println("Initialisation de la mixture de gaussiennes");
		// Initialisation des centres avec aléas (on gardera ces centres
		// pour réinitialiser l'algorithme)
		kmoyenne.initialise();
		double[][] centre = new double[K][D];
		for (int k = 0; k < K; k++) {
			centre[k] = Arrays.copyOf(kmoyenne.getCentre()[k], kmoyenne.getCentre()[k].length);
		}
		// Nombre d'itérations de l'apprentissage
		int iteration = 5;

		// Dix apprentissages consécutifs
		for (int numberLearning = 0; numberLearning < 10; numberLearning++) {

			System.out.println("Apprentissage " + numberLearning + " lancé");
			kmoyenne.runLearning(iteration);

			System.out.println("Fin d'apprentissage");

			// Affichage pur
			for (int indexCentre = 0; indexCentre < K; indexCentre++) {
				System.out.println("Centre " + indexCentre + ": " + "  rouge: " + kmoyenne.getCentre()[indexCentre][0] + "  vert: " + kmoyenne.getCentre()[indexCentre][1] + "  bleu: " + kmoyenne.getCentre()[indexCentre][2]);
			}

			System.out.println("\n" + "Affichage dénormalisé");

			// Affichage dénormalisé
			for (int indexCentre = 0; indexCentre < K; indexCentre++) {
				Color c = LoadSavePNG.denormaliseColor(kmoyenne.getCentre()[indexCentre]);
				System.out.println("Centre " + indexCentre + ": " + "  rouge: " + c.getRed() + "  vert: " + c.getGreen() + "  bleu: " + c.getBlue());
			}

			// Calcul du score total et enregistrement dans fichier
			fw.write(iteration + " " + kmoyenne.score() + "\n");

			// Réinitialisation de l'algorithme pour le prochain apprentissage
			// On reprend les positions des centres de la première initialisation
			double[][] temp = new double[K][D];
			for (int k = 0; k < K; k++) {
				temp[k] = Arrays.copyOf(centre[k], centre[k].length);
			}
			kmoyenne.setCentre(temp);

			// On réinitialise le tableau d'assignement
			kmoyenne.initialiseDataGaussian();
			// On réinitialise les autres paramètres
			kmoyenne.setDeviation(0.5);
			kmoyenne.setDensity(1. / (double) K);
			// On incrémente le nombre d'itérations de 5
			iteration += 5;

			// Saut de ligne affichage console
			System.out.print("\n");
		}
		fw.close();
	}

	/**
	 * On charge une image quelconque, puis selection des centres par un algorithme
	 * automatique, écriture des résultats dans un fichier .txt et par une image
	 * compressé 
	 * @throws IOException
	 */
	public static void question3() throws IOException {
		String path = "./src/image/";
		String image = path + "img1.jpg";
		FileWriter fw = new FileWriter(path + "question3/" + "question3.txt");

		// Chargement de l'image
		BufferedImage bui = ImageIO.read(new File(image));

		// Récupération d'un tableau de couleur
		Color[] tabColorRGB = LoadSavePNG.loadPNG(bui);
		// Récupération d'un tableau de réel contenant les valeur hsb du tableau de
		// couleur rgb
		float[][] hsbValue = new float[tabColorRGB.length][3];
		for (int c = 0; c < tabColorRGB.length; c++) {
			int red = tabColorRGB[c].getRed();
			int green = tabColorRGB[c].getGreen();
			int blue = tabColorRGB[c].getBlue();
			Color.RGBtoHSB(red, green, blue, hsbValue[c]);
			hsbValue[c][0] = hsbValue[c][0] * 360;
			hsbValue[c][1] = hsbValue[c][1] * 100;
			hsbValue[c][2] = hsbValue[c][2] * 100;
		}

		/*
		 * Ici la couleur est réprésenté par un entier: 0:rouge 1:orange 2:jaune 3:vert
		 * 4:bleu clair 5:bleu foncé 6:violet 7:rose
		 */
		// ColorMap[couleur][saturation][brightness]=nombre pixels correspondant
		int[][][] ColorMap = new int[9][101][101];

		// Remplissage du tableau ColorMap
		for (float[] c : hsbValue) {
			int saturation = (int) c[1];
			int brightness = (int) c[2];
			int color = -1;
			if (saturation != 0 && brightness != 0) {
				if ((c[0] < 15 || c[0] >= 330)) {
					color = 0; // Rouge
				} else if (c[0] >= 15 && c[0] < 45) {
					color = 1; // Orange
				} else if (c[0] >= 45 && c[0] < 90) {
					color = 2; // Jaune
				} else if (c[0] >= 90 && c[0] < 150) {
					color = 3; // Vert
				} else if (c[0] >= 150 && c[0] < 210) {
					color = 4; // Bleu clair
				} else if (c[0] >= 210 && c[0] < 270) {
					color = 5; // Bleu foncé
				} else if (c[0] >= 270 && c[0] < 285) {
					color = 6; // Violet
				} else if (c[0] >= 285 && c[0] < 330) {
					color = 7; // Rose
				}
			} else {
				color = 8; // Teinte de gris
			}
			ColorMap[color][saturation][brightness]++;
		}

		int nbCentre = 0; // Variable du nombre de centre
		int nombreTotalDePixel = tabColorRGB.length; // nombre de pixel sur l'image
		boolean[] couleurPrésente = new boolean[9];
		// Parcours des Couleurs
		for (int i = 0; i < 9; i++) {
			int nbPixelColor = 0;
			for (int s = 0; s <= 100; s++) {
				for (int b = 0; b <= 100; b++) {
					if (ColorMap[i][s][b] > 0) {
						nbPixelColor++;
					}
				}
			}
			// Si une couleur est présente à plus de 1/250 de l'image alors on lui accorde
			// un centre
			if (nbPixelColor > nombreTotalDePixel / 200) {
				nbCentre++;
				couleurPrésente[i] = true;
			}
		}

		fw.write("Couleur présente sur l'image: \n");
		for (int i = 0; i < couleurPrésente.length; i++) {
			if (couleurPrésente[i]) {
				switch (i) {
				case 0: // Rouge
					fw.write("Rouge \n");
					break;
				case 1: // Orange
					fw.write("Orange \n");
					break;
				case 2: // Jaune
					fw.write("Jaune \n");
					break;
				case 3: // Vert
					fw.write("Vert \n");
					break;
				case 4: // Bleu clair
					fw.write("Bleu clair \n");
					break;
				case 5: // Bleu foncé
					fw.write("Bleu foncé \n");
					break;
				case 6: // Violet
					fw.write("Violet \n");
					break;
				case 7: // Rose
					fw.write("Rose \n");
					break;
				case 8: // Teinte de gris
					fw.write("Teinte de gris \n");
					break;
				}
			}
		}
		fw.write("\n" + "Nombre de centres: " + nbCentre);

		// Extraction de couleur sous forme de donnée normalisé
		double[][] normalizedColor = LoadSavePNG.normaliseColor(tabColorRGB, false);

		System.out.println("Intialisation des données");

		int D = 3; // Trois dimensions
		int K = nbCentre;
		double[][] data = normalizedColor;
		double[][] centre = new double[K][D]; // Centres ou moyenne des gaussiennes
		double[][] deviation = new double[K][D]; // Variance des gaussiennes
		double[] density = new double[K]; // Densité des gaussiennes

		// Initialisation des centres
		int indexCentre = 0;
		fw.write("Centres initiaux:" + "\n");
		for (int i = 0; i < 9; i++) {
			if (couleurPrésente[i]) {
				int maxColor = 0;
				int saturation = 0;
				int brightness = 0;
				for (int s = 0; s <= 100; s++) {
					for (int b = 0; b <= 100; b++) {
						// Obtention de la teinte et de la luminosité la plus présente sur les pixel de
						// l'image
						if (ColorMap[i][s][b] > maxColor) {
							maxColor = ColorMap[i][s][b];
							saturation = s;
							brightness = b;
						}
					}
				}
				// Obtention de la couleur
				int hue = -1;
				switch (i) {
				case 0: // Rouge
					hue = 0;
					break;
				case 1: // Orange
					hue = 30;
					break;
				case 2: // Jaune
					hue = 60;
					break;
				case 3: // Vert
					hue = 120;
					break;
				case 4: // Bleu clair
					hue = 175;
					break;
				case 5: // Bleu foncé
					hue = 225;
					break;
				case 6: // Violet
					hue = 280;
					break;
				case 7: // Rose
					hue = 310;
					break;
				case 8: // Teinte de gris
					hue = 0;
					break;
				}
				int colorValue = Color.HSBtoRGB((float) (hue / 360f), (float) (saturation / 100f), (float) (brightness / 100f));
				Color colorRGB = new Color(colorValue);
				centre[indexCentre][0] = colorRGB.getRed() / 255f;
				centre[indexCentre][1] = colorRGB.getGreen() / 255f;
				centre[indexCentre][2] = colorRGB.getBlue() / 255f;
				fw.write("centre " + indexCentre + ":  " + colorRGB.getRed() + " " + colorRGB.getGreen() + " " + colorRGB.getBlue() + "\n");
				indexCentre++;
			}
		}
		fw.write("\n");

		// Initialisation de la variance
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < D; j++) {
				deviation[i][j] = 0.5;
			}
		}

		// Initialisation de la densité
		for (int i = 0; i < K; i++) {
			density[i] = 1. / (double) K;
		}

		System.out.println("Construction d'un kmoyenne de mixture de gaussiennes");
		KmeansGaussianMix kmoyenne = new KmeansGaussianMix(data, K);

		System.out.println("Initialisation de la mixture de gaussiennes");
		kmoyenne.setCentre(centre);
		kmoyenne.setDensity(density);
		kmoyenne.setDeviation(deviation);
		kmoyenne.initialiseDataGaussian();

		System.out.println("Apprentissage lancé");
		int maxIteration = 30;
		kmoyenne.runLearning(maxIteration);

		System.out.println("Fin d'apprentissage");

		fw.write("Centres finaux:" + "\n");
		for (int i = 0; i < centre.length; i++) {
			fw.write("centre " + i + ":  " + (int) (centre[i][0] * 255) + " " + (int) (centre[i][1] * 255) + " " + (int) (centre[i][2] * 255) + "\n");
		}

		/** sauvegarde de l'image **/
		BufferedImage bui_out = new BufferedImage(bui.getWidth(), bui.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < bui.getHeight(); i++) {
			for (int j = 0; j < bui.getWidth(); j++) {
				int centreIndex = kmoyenne.answer(j + i * bui.getWidth());
				bui_out.setRGB(j, i, LoadSavePNG.denormaliseColor(centre[centreIndex]).getRGB());
			}
		}
		ImageIO.write(bui_out, "JPG", new File(path + "question3/" + "question3_img_Compressé_" + K + "centres.jpg"));

		fw.close();
	}

	/**
	 * On fait tourner 4 fois l'algorithme en fessant variée le nombre de centre de
	 * 5 à 20 compris par pas de 5, pour chaque pixel on lui associe la couleur du
	 * centre auquel il est associé, on obtient une image compréssé contenant autant
	 * de couleur que de centre
	 */
	public static void compression() throws IOException {
		String path = "./src/image/";
		String imageMMS = path + "mms.png";
		Random rand = new Random();

		// Chargement de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		// R�cup�ration d'un tableau de couleur
		Color[] tabColor = LoadSavePNG.loadPNG(bui);

		// Extraction de couleur sous forme de donnée normalisé
		double[][] normalizedColor = LoadSavePNG.normaliseColor(tabColor, false);

		// Itération du nombre de centres de 5 à 20 compris
		for (int e = 5; e <= 20; e += 5) {
			System.out.println("Apprantissage " + e / 5);
			System.out.println("Intialisation des données");

			int D = 3; // Trois dimensions
			int K = e;
			double[][] data = normalizedColor;
			double[][] centre = new double[K][D]; // Centres ou moyenne des gaussiennes
			double[][] deviation = new double[K][D]; // Variance des gaussiennes
			double[] density = new double[K]; // Densité des gaussiennes

			// Initialisation des centres aléatoirement
			for (int c = 0; c < K; c++) {
				for (int d = 0; d < D; d++) {
					centre[c][d] = rand.nextFloat();
				}
			}

			// Initialisation de la variance
			for (int i = 0; i < K; i++) {
				for (int j = 0; j < D; j++) {
					deviation[i][j] = 0.5;
				}
			}

			// Initialisation de la densité
			for (int i = 0; i < K; i++) {
				density[i] = 1. / (double) K;
			}

			System.out.println("Construction d'un kmoyenne de mixture de gaussiennes");
			KmeansGaussianMix kmoyenne = new KmeansGaussianMix(data, K);

			System.out.println("Initialisation de la mixture de gaussiennes");
			kmoyenne.setCentre(centre);
			kmoyenne.setDensity(density);
			kmoyenne.setDeviation(deviation);
			kmoyenne.initialiseDataGaussian();

			System.out.println("Apprentissage lancé");
			int maxIteration = 20;
			kmoyenne.runLearning(maxIteration);

			System.out.println("Fin d'apprentissage" + "\n");

			/** sauvegarde de l'image **/
			BufferedImage bui_out = new BufferedImage(bui.getWidth(), bui.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			for (int j = 0; j < bui_out.getHeight(); j++) {
				for (int i = 0; i < bui_out.getWidth(); i++) {
					int centreIndex = kmoyenne.answer(i + j * bui_out.getWidth());
					bui_out.setRGB(i, j, LoadSavePNG.denormaliseColor(centre[centreIndex]).getRGB());
				}
			}
			ImageIO.write(bui_out, "JPG", new File(path + "compression/" + "compression_" + K + "centres.jpg"));

		}
	}

}
