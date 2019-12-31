package partitionnement;

import java.io.FileWriter;
import java.io.IOException;

public class KmeansTest {
	
	public static void simpleLearning() {
		// Creation d'un jeu de donnes simples pour tester l'algo
		System.out.println("Intialisation des données");

		int D = 2; // Deux dimensions
		int k = 2; // Deux centres
		double[][] X = new double[6][D]; // 6 points en D dimensions
		double[][] centre = new double[k][D];

		centre[0][0] = -1;
		centre[1][0] = 1;
		centre[0][1] = 0;
		centre[1][1] = 0;

		// Position des donnees
		X[0][0] = -3;
		X[0][1] = 1;
		X[1][0] = -2.5;
		X[1][1] = -0.5;
		X[2][0] = -4;
		X[2][1] = 0;
		X[3][0] = 2;
		X[3][1] = 2;
		X[4][0] = 2.5;
		X[4][1] = -0.5;
		X[5][0] = 1.5;
		X[5][1] = -1;

		System.out.println("Construction d'un kmoyenne");
		Kmeans kmoyenne = new Kmeans(X, k);

		System.out.println(kmoyenne);

		System.out.println("Enregistrement des centres de départ");
		kmoyenne.setCentre(centre);

		System.out.println(kmoyenne);

		System.out.println("Apprentissage lancé");
		double epsilon = 0.001;
		kmoyenne.runLearning(epsilon);

		System.out.println("Fin d'apprentissage");
		// Verification
		for (int i = 0; i < X.length; i++) {
			System.out.println("Point " + i + " assigné à " + kmoyenne.getDataCentre()[i]);
		}
		for (int i = 0; i < centre.length; i++) {
			System.out.println("Position centre " + i + ": " + kmoyenne.getCentre()[i][0] + " " + kmoyenne.getCentre()[i][1]);
		}
	}
	
	public static void simpleGaussianMixLearning() {
		// Creation d'un jeu de donnes simples pour tester l'algo
		System.out.println("Intialisation des données");

		int D = 2; // deux dimensions
		int K = 2; // deux centres
		double[][] X = new double[6][D]; // 6 points en D dimensions
		double[][] centre = new double[K][D]; // Centres ou moyenne des gaussiennes
		double[][] deviation = new double[K][D]; // Variance des gaussiennes
		double[] density = new double[K]; // Densité des gaussiennes
		
		// Initialisation des centres
		centre[0][0] = -1;
		centre[1][0] = 1;
		centre[0][1] = 0;
		centre[1][1] = 0;
		
		// Initialisation de la variance
		deviation[0][0] = 1.5;
		deviation[0][1] = 1.5;
		deviation[1][0] = 1.5;
		deviation[1][1] = 1.5;
		
		// Initialisation de la densité
		density[0] = 1. / (double)K;
		density[1] = 1. / (double)K;
		
		// Position des donnees
		X[0][0] = -3;
		X[0][1] = 1;
		X[1][0] = -2.5;
		X[1][1] = -0.5;
		X[2][0] = -4;
		X[2][1] = 0;
		X[3][0] = 2;
		X[3][1] = 2;
		X[4][0] = 2.5;
		X[4][1] = -0.5;
		X[5][0] = 1.5;
		X[5][1] = -1;

		System.out.println("Construction d'un kmoyenne de mixture de gaussiennes");
		KmeansGaussianMix kmoyenne = new KmeansGaussianMix(X, K);

		System.out.println(kmoyenne + "\n");

		System.out.println("Initialisation de la mixture de gaussiennes");
		kmoyenne.setCentre(centre);
		kmoyenne.setDensity(density);
		kmoyenne.setDeviation(deviation);
		kmoyenne.initialiseDataGaussian();

		System.out.println(kmoyenne + "\n");

		System.out.println("Apprentissage lancé");
		int maxIteration = 10;
		kmoyenne.runLearning(maxIteration);

		System.out.println("Fin d'apprentissage");

		// Verification
		System.out.println(kmoyenne);

		// verification
		System.out.println(kmoyenne + "\n");

		for (int i = 0; i < X.length; i++) {
			System.out.println("Point " + i + " assigné à " + kmoyenne.answer(i));
		}
	}
	
	public static void gaussianLearning() throws IOException {
		// Creation d'un jeu de donnes simples pour tester l'algo
		System.out.println("Intialisation des données");
		
		int k = 4; // deux centres
		int N = 10000;
		double[][] data = TasGaussien.dataGaussian2D(N);
		
		// Enregistrement dans un fichier
		FileWriter fw = new FileWriter("gaussianLearning.d");
		for (int n = 0; n < N; n++) {
			fw.write(data[n][0] + " " + data[n][1] + "\n");
		}
		
		System.out.println("Construction d'un kmoyenne");
		Kmeans kmoyenne = new Kmeans(data, k);
		
		System.out.println(kmoyenne);
		
		System.out.println("Initialisation des centres");
		kmoyenne.initialise();
		
		// Cela ne dérange pas apparemment
		double[][] temp = kmoyenne.getCentre();
		temp[0][0] = 100;
		temp[0][1] = 4;
		kmoyenne.setCentre(temp);
		
		System.out.println("Apprentissage lancé");
		double epsilon = 0.001;
		kmoyenne.runLearning(epsilon);
		
		System.out.println("Fin d'apprentissage");
		// Verification
		for (int i = 0; i < N; i++) {
			System.out.println("Point " + i + " assigné à " + kmoyenne.getDataCentre()[i]);
		}
		fw.write("\n\n");
		for (int i = 0; i < k; i++) {
			fw.write(kmoyenne.getCentre()[i][0] + " " + kmoyenne.getCentre()[i][1] + "\n");
			System.out.println("Position centre " + i + ": " + kmoyenne.getCentre()[i][0] + " " + kmoyenne.getCentre()[i][1]);
		}
		
		fw.close();
		
	}
	
	

	public static void main(String[] args) throws IOException {
		//simpleLearning();
		//gaussianLearning();
		simpleGaussianMixLearning();
	}
}
