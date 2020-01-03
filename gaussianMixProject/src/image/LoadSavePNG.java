package image;

import partitionnement.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class LoadSavePNG {
	static BufferedImage bi;

	/**
	 * Transforme une image en tableu de Couleur
	 * 
	 * @param bui une image
	 * @return le tableau de couleur de l'image
	 * @throws IOException
	 */
	public static Color[] loadPNG(BufferedImage bui) throws IOException {

		int width = bui.getWidth();
		int height = bui.getHeight();
		System.out.println("Hauteur=" + height);
		System.out.println("Largeur=" + width);

		int[] im_pixels = bui.getRGB(0, 0, width, height, null, 0, width);

		/** Creation du tableau **/
		Color[] tabColor = new Color[im_pixels.length];
		for (int i = 0; i < im_pixels.length; i++) {
			tabColor[i] = new Color(im_pixels[i]);
		}

		return tabColor;
	}

	/**
	 * Normalise un tableau de couleur en entier entre [0, 1]
	 * 
	 * @param tabColor     un tableau de couleurs
	 * @param optimisation un boolean qui décide si le fond de l'image doit être
	 *                     enlever pour ne garder que les mms
	 * @return Un tableau à 2 dimensions qui à chaque pixels associe la couleur RGB
	 *         normalisé
	 */
	public static double[][] normaliseColor(Color[] tabColor, boolean optimisation) {
		Color[] newColorTab;
		if (optimisation) {
			newColorTab = colorOptimisation(tabColor);
		} else {
			newColorTab = tabColor;
		}

		double[][] normalizedColor = new double[newColorTab.length][3];
		for (int i = 0; i < newColorTab.length; i++) {
			normalizedColor[i][0] = newColorTab[i].getRed() / 255f;
			normalizedColor[i][1] = newColorTab[i].getGreen() / 255f;
			normalizedColor[i][2] = newColorTab[i].getBlue() / 255f;
		}

		return normalizedColor;
	}

	/**
	 * Optimise un tableau de couleur en enlevant certains pixels d'une certaines
	 * couleurs cf: le marron de la table
	 * 
	 * @param tabColor un tableu de couleur
	 * @return un tableau de couleur épuré
	 */
	public static Color[] colorOptimisation(Color[] tabColor) {
		Color[] optimizedTabColor;
		int badColor = 0;
		for (Color c : tabColor) {
			int red = c.getRed();
			int green = c.getGreen();
			int blue = c.getBlue();

			if ((((red > 135 && red <= 255) && (green > 90 && green < 230) && (blue > 60 && blue < 190)) && (red > 150 && green > 150 && blue > 150))) {
				badColor++;
			}
		}
		// System.out.println("total pixel: " + tabColor.length);
		// System.out.println("good pixel: " + (tabColor.length - badColor));
		optimizedTabColor = new Color[tabColor.length - badColor];

		int index = 0;
		for (Color c : tabColor) {
			int red = c.getRed();
			int green = c.getGreen();
			int blue = c.getBlue();

			if (!(((red > 135 && red <= 255) && (green > 90 && green < 230) && (blue > 60 && blue < 190)) && (red > 150 && green > 150 && blue > 150))) {
				optimizedTabColor[index] = c;
				index++;
			}
		}

		return optimizedTabColor;
	}

	/**
	 * Dénormalise une couleur et la met entre [0,255]
	 * 
	 * @param normalizedColor un tableau de couleur normalisé à 3 cases
	 * @return une Couleur en RGB
	 */
	public static Color denormaliseColor(double[] normalizedColor) {
		Color denormalizedColor;
		denormalizedColor = new Color((int) (normalizedColor[0] * 255), (int) (normalizedColor[1] * 255), (int) (normalizedColor[2] * 255));
		return denormalizedColor;
	}

	public static void main(String[] args) throws IOException {

		String path = "./src/";
		String imageMMS = path + "mms.png";
		// Lecture de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		// Obtention tableau de couleur
		Color[] tabColor = loadPNG(bui);

		// Normalisation des couleurs
		double[][] normalizedColor = normaliseColor(tabColor, true);

		simpleGaussianMixLearning(normalizedColor);
	}

	public static void simpleGaussianMixLearning(double[][] normalizedColor) {
		// Creation d'un jeu de donnes simples pour tester l'algo
		System.out.println("Intialisation des donn�es");

		int D = 3; // Trois dimensions
		int K = 6; // Six centres
		double[][] data = normalizedColor; /* new double[5][D]; */ // 5 points en D dimensions
		double[][] centre = new double[K][D]; // Centres ou moyenne des gaussiennes
		double[][] deviation = new double[K][D]; // Variance des gaussiennes
		double[] density = new double[K]; // Densit� des gaussiennes

		// Initialisation des centres
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

		// Marron
		centre[5][0] = 0.1;
		centre[5][1] = 0.1;
		centre[5][2] = 0.1;

		// Initialisation de la variance
		for (int i = 0; i < K; i++) {
			for (int j = 0; j < D; j++) {
				deviation[i][j] = 0.5;
			}
		}

		// Initialisation de la densit�
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

		System.out.println("Apprentissage lanc�");
		int maxIteration = 10;
		kmoyenne.runLearning(maxIteration);

		System.out.println("Fin d'apprentissage");

		// Affichage pure
		for (int i = 0; i < K; i++) {
			System.out.println("Centre " + i + ": " + "  rouge: " + centre[i][0] + "  vert: " + centre[i][1] + "  bleu: " + centre[i][2]);
		}

		System.out.println("\n" + "Affichage d�normalis�");

		// Affichage d�normalis�
		for (int i = 0; i < K; i++) {
			Color c = denormaliseColor(centre[i]);
			System.out.println("Centre " + i + ": " + "  rouge: " + c.getRed() + "  vert: " + c.getGreen() + "  bleu: " + c.getBlue());

		}

	}
}