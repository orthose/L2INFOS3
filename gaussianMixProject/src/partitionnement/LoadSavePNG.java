package partitionnement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class LoadSavePNG {
	static BufferedImage bi;

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

	public static Color/* [] */ denormaliseColor(double[]/* [] */ normalizedColor) {
		Color/* [] */ denormalizedColor /* = new Color[normalizedColor.length] */;
		// for (int i = 0; i < normalizedColor.length; i++) {
		denormalizedColor/* [i] */ = new Color((int) (normalizedColor/* [i] */[0] * 255), (int) (normalizedColor/* [i] */[1] * 255), (int) (normalizedColor/* [i] */[2] * 255));
		// }
		return denormalizedColor;
	}

	// TEST
	public static void testImg(int width, int height, String path, Color[] tabColor) throws IOException {
		Color[] tabColorTest = colorTest(tabColor);

		BufferedImage bui_out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++)
				bui_out.setRGB(j, i, tabColorTest[i * width + j].getRGB());
		}
		ImageIO.write(bui_out, "PNG", new File(path + "testImg.png"));
	}

	public static Color[] colorTest(Color[] tabColor) {
		Color[] tabColorTest;
		tabColorTest = new Color[tabColor.length];

		int index = 0;
		for (Color c : tabColor) {
			int red = c.getRed();
			int green = c.getGreen();
			int blue = c.getBlue();
			if (!(((red > 135 && red < 255) && (green > 90 && green < 230) && (blue > 60 && blue < 190)) && (red > 150 && green > 150 && blue > 150))) {
				tabColorTest[index] = c;
			} else {
				tabColorTest[index] = new Color(0, 0, 0);
			}

			index++;
		}

		return tabColorTest;
	}
	// END TEST

	public static void main(String[] args) throws IOException {

		String path = "./src/";
		String imageMMS = path + "mms.png";
		// Lecture de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		Color[] tabColor = loadPNG(bui);

		// testImg(bui.getWidth(), bui.getHeight(), path, tabColor);

		double[][] normalizedColor = normaliseColor(tabColor, true);

		simpleGaussianMixLearning(normalizedColor);

		// Question Bonus: Compression
		// Compression(bui.getWidth(), bui.getHeight(), path);

		/** inversion des couleurs **/
		/*
		 * for(int i=0 ; i<tabColor.length ; i++) tabColor[i]=new
		 * Color(255-tabColor[i].getRed(),255-tabColor[i].getGreen(),255-tabColor[i].
		 * getBlue());
		 */

		/** sauvegarde de l'image **/
		/*
		 * BufferedImage bui_out = new
		 * BufferedImage(bui.getWidth(),bui.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
		 * for(int i=0 ; i<height ; i++) { for(int j=0 ; j<width ; j++)
		 * bui_out.setRGB(j,i,tabColor[i*width+j].getRGB()); } ImageIO.write(bui_out,
		 * "PNG", new File(path+"test.png"));
		 */

	}

	public static void simpleGaussianMixLearning(double[][] normalizedColor) {
		// Creation d'un jeu de donnes simples pour tester l'algo
		System.out.println("Intialisation des données");

		int D = 3; // Trois dimensions
		int K = 6; // Six centres
		double[][] data = normalizedColor; /* new double[5][D]; */ // 5 points en D dimensions
		double[][] centre = new double[K][D]; // Centres ou moyenne des gaussiennes
		double[][] deviation = new double[K][D]; // Variance des gaussiennes
		double[] density = new double[K]; // Densité des gaussiennes

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

		// Initialisation de la densité
		for (int i = 0; i < K; i++) {
			density[i] = 1. / (double) K;
		}

		System.out.println("Construction d'un kmoyenne de mixture de gaussiennes");
		KmeansGaussianMix kmoyenne = new KmeansGaussianMix(data, K);

		// System.out.println(kmoyenne + "\n");

		System.out.println("Initialisation de la mixture de gaussiennes");
		kmoyenne.setCentre(centre);
		kmoyenne.setDensity(density);
		kmoyenne.setDeviation(deviation);
		kmoyenne.initialiseDataGaussian();

		// System.out.println(kmoyenne + "\n");

		System.out.println("Apprentissage lancé");
		int maxIteration = 10;
		kmoyenne.runLearning(maxIteration);

		System.out.println("Fin d'apprentissage");

		// Verification
		// System.out.println(kmoyenne);

		// verification
		// System.out.println(kmoyenne + "\n");

		// Affichage pure
		for (int i = 0; i < K; i++) {
			System.out.println("Centre " + i + ": " + "  rouge: " + centre[i][0] + "  vert: " + centre[i][1] + "  bleu: " + centre[i][2]);
		}

		System.out.println("\n" + "Affichage dénormalisé");

		// Affichage dénormalisé
		for (int i = 0; i < K; i++) {
			Color c = denormaliseColor(centre[i]);
			System.out.println("Centre " + i + ": " + "  rouge: " + c.getRed() + "  vert: " + c.getGreen() + "  bleu: " + c.getBlue());

		}

	}

	// QUESTION COMPRESSION
	public static void Compression(int width, int height, String path) throws IOException {

		/** sauvegarde de l'image **/
		BufferedImage bui_out = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				// double[] centerColor[3] coord d'un centre du pixel (i * width + j)
				double[] centerColor = new double[3]; // = Focntion qui renvoie els coord normalisé d'un centre
				bui_out.setRGB(j, i, denormaliseColor(centerColor).getRGB());
			}
		}
		ImageIO.write(bui_out, "PNG", new File(path + "test.png"));

	}
}