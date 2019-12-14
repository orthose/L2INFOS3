package partitionnement;

import java.util.Arrays;

/**
 * @apiNote Algorithme des k-moyennes
 * amélioré par la mixture de gaussiennes.
 * 
 * @author Maxime Vincent
 * @version 14/11/19
 */
public class KmeansGaussianMix extends Kmeans {
	
	// Attributs
	private double[][] dataGaussian; // Probabilité d'appartenir à une gaussienne pour chaque donnée
	private double[][] mean; // Moyenne
	private double[][] deviation; // Ecart-Type
	private double[] density; // Densité
	private double[] probabilitySumGaussian; // Somme des probabilités
	
	// Constructeurs
	public KmeansGaussianMix(double[][] data, int numberCentre) {
		super(data, numberCentre);
		// Le tableau d'assignation de la classe mère ne sera pas utilisé
		super.dataCentre = null;
		// Initialisation des attributs spécifiques
		this.dataGaussian = new double[super.numberData][numberCentre];
		this.mean = new double[super.dimension][numberCentre];
		this.deviation = new double[super.dimension][numberCentre];
		this.density = new double[numberCentre];
		this.probabilitySumGaussian = new double[numberCentre];
	}
	
	// Getter
	/**
	 * @apiNote Permet d'obtenir le tableau
	 * d'assignation des gaussiennes aux données.
	 * Tableau de N données auxquelles on
	 * associe des tableaux de K gaussiennes
	 * contenant la probabilité d'appartenir
	 * à une gaussienne en particulier.
	 * 
	 * @return tableau 2D de réels
	 */
	public double[][] getDataGaussian() {
		return this.dataGaussian;
	}
	
	/**
	 * @param indexGaussian: Indice de la gaussienne
	 * dans [0 ; K[
	 * 
	 * @return somme des probabilités d'appartenir
	 * à une gaussienne en particulier sur toutes
	 * les données
	 */
	private double probabilitySumGaussian(int indexGaussian) {
		double resultat = 0;
		// Parcours des données
		for (int indexData = 0; indexData < super.numberData; indexData++) {
			resultat += this.dataGaussian[indexData][indexGaussian];
		}
		return resultat;
	}
	
	/**
	 * @param indexCoordinate: Indice de la coordonnée
	 * dans [0 ; D[
	 * 
	 * @param indexGaussian: Indice de la gaussienne dans
	 * [0 ; K[
	 * 
	 * @return moyenne pondérée d'une coordonnée pour 
	 * une gaussienne spécifique sur toutes les données 
	 */
	private double mean(int indexGaussian, int indexCoordinate) {
		double somme = 0;
		// Parcours des données
		for (int indexData = 0; indexData < super.numberData; indexData++) {
			somme += this.dataGaussian[indexData][indexGaussian] * super.data[indexData][indexCoordinate];
		}
		return (double)somme / (double)this.probabilitySumGaussian[indexGaussian];
	}
	
	/**
	 * @param indexCoordinate: Indice de la coordonnée
	 * dans [0 ; D[
	 * 
	 * @param indexGaussian: Indice de la gaussienne dans
	 * [0 ; K[
	 * 
	 * @return écart-type à la moyenne pour une coordonnée et
	 * une gaussienne sur toutes les données
	 */
	private double deviation(int indexGaussian, int indexCoordinate) {
		double somme = 0;
		// Parcours des données
		for (int indexData = 0; indexData < super.numberData; indexData++) {
			somme += this.dataGaussian[indexData][indexGaussian] * Math.pow(super.data[indexData][indexCoordinate] - this.mean[indexCoordinate][indexGaussian], 2);
		}
		return (double)somme / (double)this.probabilitySumGaussian[indexGaussian];
	}
	
	/**
	 * @param indexGaussian: Indice de la gaussienne dans
	 * [0 ; K[
	 * 
	 * @return densité associée à une gaussienne
	 */
	private double density(int indexGaussian) {
		return (double)this.probabilitySumGaussian[indexGaussian] / (double)super.numberData;
	}
	
	/** 
	 * @param indexGaussian: Indice de la gaussienne dans
	 * [0 ; K[
	 * 
	 * @param indexData: Indice de la donnée dans [0 ; D[
	 * 
	 * @return  probabilité dans [0 ; 1] pour la donnée
	 * d'appartenir à la gaussienne
	 */
	private double probabilityGaussian(int indexGaussian, int indexData) {
		double productNumerator = 1;
		double productDenominator = 1;
		double sumDenominator = 0;
		// Parcours des gaussiennes
		for (int indexGaussianDenominator = 0; indexGaussianDenominator < super.numberCentre; indexGaussianDenominator++) {
			// Calcul de la somme du dénominateur
			sumDenominator += this.density[indexGaussianDenominator] * productDenominator;
			productDenominator = 1;
			// Parcours des coordonnées
			for (int indexCoordinate = 0; indexCoordinate < super.dimension; indexCoordinate++) {
				// Calcul du produit au dénominateur
				double factor1 = 1. / Math.sqrt(2 * Math.PI * this.deviation[indexCoordinate][indexGaussianDenominator]);
				double factor2 = Math.exp(- Math.pow(super.data[indexData][indexCoordinate] - this.mean[indexData][indexGaussianDenominator], 2) / (2 * this.deviation[indexCoordinate][indexGaussianDenominator]));
				productDenominator *= (factor1 * factor2); 
				// Calcul du produit au numérateur
				if (indexGaussian == indexGaussianDenominator) {
					productNumerator = productDenominator;
				}
			}
		}
		return (double)(this.density[indexGaussian] * productNumerator) / (double)sumDenominator;
		
	}
	
	/**
	 * @apiNote Modifie les probabilités d'appartenir 
	 * à une gaussienne pour chaque donnée en fonction
	 * des paramètres (moyenne, écart-type et densité).
	 */
	private void assignGaussian() {
		// Parcours du tableau d'assignement
		for (int indexData = 0; indexData < super.numberData; indexData++) {
			for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
				this.dataGaussian[indexData][indexGaussian] = this.probabilityGaussian(indexGaussian, indexData);
			}
		}
	}
	
	/**
	 * @apiNote Met à jour les paramètres de l'algorithme
	 * (moyenne, écart-type et densité), ce qui aura pour
	 * effet de modifier l'emplacement des gaussiennes
	 * à la prochaine itération.
	 */
	private void moveGaussian() {
		// Calcul préalable de la somme des probabilités pour chaque gaussienne
		for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
			this.probabilitySumGaussian[indexGaussian] = this.probabilitySumGaussian(indexGaussian);
		}
		// Mise à jour de la moyenne et de l'écart-type (on peut les calculer en même temps
		// car l'écart-type dépend de la moyenne et les deux paramètres sont stockés dans
		// des tableaux de même format)
		for (int indexCoordinate = 0; indexCoordinate < super.dimension; indexCoordinate++) {
			for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
				this.mean[indexCoordinate][indexGaussian] = this.mean(indexGaussian, indexCoordinate);
				this.deviation[indexCoordinate][indexGaussian] = this.deviation(indexGaussian, indexCoordinate);
			}
		}
		// Mise à jour de la densité
		for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
			this.density[indexGaussian] = this.density(indexGaussian);
		}
	}
	
	@Override
	/**
	 * @apiNote Démarre l'apprentissage
	 * 
	 * @param maxIteration: Nombre d'itérations maximum
	 */
	public void runLearning(int maxIteration) {
		if (! super.learningState) {
			for (int i = 0; i < maxIteration; ) {
				// Assignation des gaussiennes aux données
				this.assignGaussian();
				// Modification des paramètres de l'algorithme
				this.moveGaussian();
			}
			super.learningState = true;
		}
		else {
			throw new IllegalCallerException("L'apprentissage a déjà été effectué");
		}
	}
	
	
}