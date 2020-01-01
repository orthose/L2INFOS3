package partitionnement;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Main {

	public static void main(String[] args) throws IOException {
		// question1();
		compression();
	}

	public static void question1() throws IOException {
		String path = "./src/";
		String imageMMS = path + "mms.png";
		Random rand = new Random();

		// Chargement de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		// Récupération d'un tableau de couleur
		Color[] tabColor = LoadSavePNG.loadPNG(bui);

		// Extraction de couleur sous forme de donnée normalisé
		double[][] normalizedColor = LoadSavePNG.normaliseColor(tabColor, false);

		System.out.println("Intialisation des données");

		int D = 3; // Trois dimensions
		int K = 10; // Dix centres
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

		// Initialisation de la variance aléatoire
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < D; j++) {
				deviation[i][j] = rand.nextFloat();
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

		System.out.println("Fin d'apprentissage");

		// Affichage pure
		for (int i = 0; i < K; i++) {
			System.out.println("Centre " + i + ": " + "  rouge: " + centre[i][0] + "  vert: " + centre[i][1] + "  bleu: " + centre[i][2]);
		}

		System.out.println("\n" + "Affichage dénormalisé");

		// Affichage dénormalisé
		for (int i = 0; i < K; i++) {
			Color c = LoadSavePNG.denormaliseColor(centre[i]);
			System.out.println("Centre " + i + ": " + "  rouge: " + c.getRed() + "  vert: " + c.getGreen() + "  bleu: " + c.getBlue());

		}

		// TODO CALCUL DE SCORE POUR CHAQUE GAUSSIENNE

	}

	public static void compression() throws IOException {
		String path = "./src/";
		String imageMMS = path + "mms.png";
		Random rand = new Random();

		// Chargement de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		// Récupération d'un tableau de couleur
		Color[] tabColor = LoadSavePNG.loadPNG(bui);

		// Extraction de couleur sous forme de donnée normalisé
		double[][] normalizedColor = LoadSavePNG.normaliseColor(tabColor, false);

		System.out.println("Intialisation des données");

		int D = 3; // Trois dimensions
		int K = 10; // Dix centres
		double[][] data = normalizedColor;
		double[][] centre = new double[K][D]; // Centres ou moyenne des gaussiennes
		double[][] deviation = new double[K][D]; // Variance des gaussiennes
		double[] density = new double[K]; // Densité des gaussiennes

		// Initialisation des centres semi-aléatoirement
		// Rouge
		centre[0][0] = 1;
		centre[0][1] = 0;
		centre[0][2] = 0;
		// Vert
		centre[1][0] = 0;
		centre[1][1] = 1;
		centre[1][2] = 0;
		// Bleu
		centre[2][0] = 0;
		centre[2][1] = 0;
		centre[2][2] = 1;
		// Jaune
		centre[3][0] = 1;
		centre[3][1] = 1;
		centre[3][2] = 0;
		// Orange
		centre[4][0] = 1;
		centre[4][1] = 0.5;
		centre[4][2] = 0;
		// Reste des centres aléatoires
		for (int c = 5; c < K; c++) {
			for (int d = 0; d < D; d++) {
				centre[c][d] = rand.nextFloat();
			}
		}

		// Initialisation de la variance aléatoire
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
		int maxIteration = 10;
		kmoyenne.runLearning(maxIteration);

		System.out.println("Fin d'apprentissage");

		/*
		 * // Affichage pure for (int i = 0; i < K; i++) { System.out.println("Centre "
		 * + i + ": " + "  rouge: " + centre[i][0] + "  vert: " + centre[i][1] +
		 * "  bleu: " + centre[i][2]); }
		 * 
		 * System.out.println("\n" + "Affichage dénormalisé");
		 * 
		 * // Affichage dénormalisé for (int i = 0; i < K; i++) { Color c =
		 * LoadSavePNG.denormaliseColor(centre[i]); System.out.println("Centre " + i +
		 * ": " + "  rouge: " + c.getRed() + "  vert: " + c.getGreen() + "  bleu: " +
		 * c.getBlue());
		 * 
		 * }
		 */

		/** sauvegarde de l'image **/
		BufferedImage bui_out = new BufferedImage(bui.getWidth(), bui.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < bui.getHeight(); i++) {
			for (int j = 0; j < bui.getWidth(); j++) {
				int centreIndex = kmoyenne.answer(j + i * bui.getWidth());

				/*
				 * // double[] centerColor[3] coord d'un centre du pixel (i * width + j)
				 * double[] centerColor = new double[3]; // = Focntion qui renvoie els coord
				 * normalisé d'un centre
				 */
				bui_out.setRGB(j, i, LoadSavePNG.denormaliseColor(centre[centreIndex]).getRGB());
			}
		}
		ImageIO.write(bui_out, "PNG", new File(path + "compression_" + K + "centres.png"));
	}

}
