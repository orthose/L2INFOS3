package partitionnement;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TasGaussien {
	
	public static double[][] histogramme(double xmin, double xmax, int nbCases, double[] data) {
		
	    // On creer le tableau qui va contenir 
	    //  0: les abcisses des cases de l'histogramme
	    //  1: les valeurs pour chaque case
	    double[][] histo = new double[2][nbCases];
	    
	    // TODO: Calcule de la taille d'une case
	    double delta = (xmax - xmin) / (double)nbCases;

	    // TODO: Calcule des abcisses Histo[0][...]
	    for (int n = 0; n < nbCases; n++) {
	    	histo[0][n] = xmin + (n * delta);
	    }

	    for(int d=0; d < data.length; d++) {
	        // TODO: pour chaque valeur: trouver a quelle case elle appartient et incrementer de un l'histogramme
	    	int caseHisto = (int)((data[d] - xmin) / delta);
	    	if (caseHisto != nbCases) {
	    		System.out.println(caseHisto);
	    		histo[1][caseHisto]++;
	    	}

	    }
	    return histo;
	}
	
	public static double[][] dataGaussian2D(int N) {
		if (N < 0) {
			throw new IllegalCallerException("N doit être supérieur à 0");
		}
		
		double[][] res = new double[N][2];
		Random r = new Random();
		
		for (int n = 0; n < N; n++) {
			if (n < N / 4) {
				res[n][0] = r.nextGaussian() - 1;
				res[n][1] = r.nextGaussian() + 1;
			}
			else if (n < 2 * N / 4) {
				res[n][0] = r.nextGaussian() + 5;
				res[n][1] = r.nextGaussian() + 5;
			}
			else if (n < 3 * N / 4){
				res[n][0] = r.nextGaussian() - 5;
				res[n][1] = r.nextGaussian() + 5;
			}
			else {
				res[n][0] = r.nextGaussian();
				res[n][1] = r.nextGaussian() + 9;
			}
		}
		return res;
	}
	
	public static void main(String[] args) throws IOException {
		
		int N = 10000;
		FileWriter fw = new FileWriter("histo3.d");
		Random r = new Random();
		double[] data = new double[N];
		double moyenne = 0;
		data[0] = r.nextGaussian();
		double xmax = data[0], xmin = data[0];
		
		for (int i = 1; i < N; i++) {
			//data[i] = r.nextDouble();
			// On centre en -3
			if (i < N / 2) {
				data[i] = r.nextGaussian() - 3;
			}
			// On centre en 3
			else {
				data[i] = r.nextGaussian() + 3;
			}
			if (data[i] > xmax) {
				xmax = data[i];
			}
			if (data[i] < xmin) {
				xmin = data[i];
			}
			moyenne += data[i];
		}
		moyenne /= (double)N;
		System.out.println("Moyenne=" + moyenne);
		
		double ecartType = 0;
		for (int i = 0; i < N; i++) {
			ecartType += (data[i] - moyenne) * (data[i] - moyenne);
		}
		ecartType /= (double)N;
		ecartType = Math.sqrt(ecartType);
		System.out.println("Ecart Type=" + ecartType);
		
		double histo[][] = histogramme(xmin, xmax, 100, data);
		for (int h = 0; h < 100; h++) {
			System.out.print(histo[0][h] + " ");
			System.out.print(histo[1][h] + "\n");
			fw.write(histo[0][h] + " " + histo[1][h] + "\n");
		}
		fw.close();
	}
}
