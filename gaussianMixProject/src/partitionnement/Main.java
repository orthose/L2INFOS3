package partitionnement;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

/**
 * @apiNote Classe de lancement des méthodes associées
 * aux questions du projet. La plupart des questions
 * consistent à déterminer les meilleurs paramètres
 * à utiliser pour un jeu de données. Pour ce faire,
 * on ne fera varier qu'un paramètre à la fois dans
 * chacune de ces questions. C'est pourquoi nous avons
 * dû écrire des méthodes supplémentaires.
 */
public class Main {

	/**
	 * @apiNote Lancement des questions une à une en
	 * commentant et décommentant.
	 */
	public static void main(String[] args) throws IOException {
		//question1_centre();
		question1_deviation();
		//question1_density();
		//question1_iteration();
		//compression();
	}
	
	/**
	 * @apiNote Question 1 dont le but est de faire tourner 
	 * l'algorithme avec 10 gaussiennes et dans 10 conditions
	 * initiales différentes. Le calcul du score permettra
	 * de déterminer à quel intervalle d'incertitude on
	 * s'expose lorsque l'on choisit d'initialiser les
	 * centres aléatoirement. Ici, on fixe les méta-paramètres
	 * et on ne fait varier que les positions initiales des
	 * centres entre chaque apprentissage.
	 */
	public static void question1_centre() throws IOException {
		// Chemin de l'image à analyser
		String path = "./src/";
		String imageMMS = path + "mms.png";
		
		// Fichier de sauvegarde des scores à interpréter avec gnuplot
		FileWriter fw = new FileWriter("question1_centregyg.d");
		
		System.out.println("Intialisation des données");
		
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
				System.out.println("Centre " + k + ": " + "  rouge: " + kmoyenne.getCentre()[k][0] + "  vert: "
						+ kmoyenne.getCentre()[k][1] + "  bleu: " + kmoyenne.getCentre()[k][2]);
			}

			System.out.println("\n" + "Affichage dénormalisé");

			// Affichage dénormalisé
			for (int k = 0; k < K; k++) {
				Color c = LoadSavePNG.denormaliseColor(kmoyenne.getCentre()[k]);
				System.out.println("Centre " + k + ": " + "  rouge: " + c.getRed() + "  vert: " + c.getGreen()
						+ "  bleu: " + c.getBlue());
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
	 * @apiNote On effectue 10 apprentissages en faisant varier
	 * l'écart-type initial des gaussiennes dans [0.05 ; 0.50].
	 * Le but est de déterminer la meilleure valeur d'écart-type
	 * pour le jeu de données en calculant le score de l'algorithme.
	 */
	public static void question1_deviation() throws IOException {
		// Chemin de l'image à analyser
		String path = "./src/";
		String imageMMS = path + "mms.png";

		// Fichier de sauvegarde des scores à interpréter avec gnuplot
		FileWriter fw = new FileWriter("question1_deviation.d");

		System.out.println("Intialisation des données");

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
				System.out.println("Centre " + indexCentre + ": " + "  rouge: " + kmoyenne.getCentre()[indexCentre][0]
						+ "  vert: " + kmoyenne.getCentre()[indexCentre][1] + "  bleu: "
						+ kmoyenne.getCentre()[indexCentre][2]);
			}

			System.out.println("\n" + "Affichage dénormalisé");

			// Affichage dénormalisé
			for (int indexCentre = 0; indexCentre < K; indexCentre++) {
				Color c = LoadSavePNG.denormaliseColor(kmoyenne.getCentre()[indexCentre]);
				System.out.println("Centre " + indexCentre + ": " + "  rouge: " + c.getRed() + "  vert: " + c.getGreen()
						+ "  bleu: " + c.getBlue());
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
			// On incrémente 0.05 la variance
			deviation += 0.05;
			kmoyenne.setDeviation(deviation);		

			// Saut de ligne affichage console
			System.out.print("\n");
		}
		fw.close();
	}
	
	/**
	 * 
	 */
	public static void  question1_density() {
		
	}
	
	/**
	 * 
	 */
	public static void  question1_iteration() {
		
	}

	public static void compression() throws IOException {
		String path = "./src/";
		String imageMMS = path + "mms.png";
		Random rand = new Random();

		// Chargement de l'image
		BufferedImage bui = ImageIO.read(new File(imageMMS));

		// R�cup�ration d'un tableau de couleur
		Color[] tabColor = LoadSavePNG.loadPNG(bui);

		// Extraction de couleur sous forme de donn�e normalis�
		double[][] normalizedColor = LoadSavePNG.normaliseColor(tabColor, false);

		System.out.println("Intialisation des donn�es");

		int D = 3; // Trois dimensions
		int K = 10; // Dix centres
		double[][] data = normalizedColor;
		double[][] centre = new double[K][D]; // Centres ou moyenne des gaussiennes
		double[][] deviation = new double[K][D]; // Variance des gaussiennes
		double[] density = new double[K]; // Densit� des gaussiennes

		// Initialisation des centres semi-al�atoirement
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
		// Reste des centres al�atoires
		for (int c = 5; c < K; c++) {
			for (int d = 0; d < D; d++) {
				centre[c][d] = rand.nextFloat();
			}
		}

		// Initialisation de la variance al�atoire
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

		/*
		 * // Affichage pure for (int i = 0; i < K; i++) { System.out.println("Centre "
		 * + i + ": " + "  rouge: " + centre[i][0] + "  vert: " + centre[i][1] +
		 * "  bleu: " + centre[i][2]); }
		 * 
		 * System.out.println("\n" + "Affichage d�normalis�");
		 * 
		 * // Affichage d�normalis� for (int i = 0; i < K; i++) { Color c =
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
				 * normalis� d'un centre
				 */
				bui_out.setRGB(j, i, LoadSavePNG.denormaliseColor(centre[centreIndex]).getRGB());
			}
		}
		ImageIO.write(bui_out, "PNG", new File(path + "compression_" + K + "centres.png"));
	}

}
