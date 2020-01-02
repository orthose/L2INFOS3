package partitionnement;

import java.util.Arrays;

/**
 * @apiNote Algorithme des k-moyennes
 * amélioré par la mixture de gaussiennes.
 * 
 * @author Maxime Vincent
 * @version 30/12/19
 */
public class KmeansGaussianMix extends Kmeans {
	
	// Constantes
	private final double orderOfMagnitude = 0.5; // Ordre de grandeur des données à modifier en fonction du jeu de données
	
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
		this.mean = super.centre; // On renomme le tableau par sourci de clarté
		this.deviation = new double[numberCentre][super.dimension];
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
	
	// Setters
	@Override
	/**
	 * @apiNote Permet de modifier les coordonnées des centres
	 * de chaque gaussienne (c'est-à-dire leur moyenne).
	 * 
	 * @param centre: Tableau de K centres des gaussiennes
	 * contenant des tableaux de coordonnées de taille D
	 */
	public void setCentre(double[][] centre) {
		if (centre.length != super.numberCentre || centre[0].length != super.dimension) {
			throw new IllegalCallerException("Paramètres de setCentre() invalides");
		}
		// Les centres correspondent à la moyenne de chaque gaussienne
		this.mean = centre;
	}
	
	/**
	 * @param deviation: Tableau de K éléments contenant
	 * des tableaux de D coordonnées et représentant les
	 * écart-types ou la variance de chaque gaussienne
	 */
	public void setDeviation(double[][] deviation) {
		if (deviation.length != super.numberCentre || deviation[0].length != super.dimension) {
			throw new IllegalCallerException("Paramètres de setCentre() invalides");
		}
		this.deviation = deviation;
	}
	
	/**
	 * @param deviation: Réel pour initialiser toutes les coordonnées
	 * de l'écart-type de chaque gaussienne
	 */
	public void setDeviation(double deviation) {
		for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
			for (int indexCoordinate = 0; indexCoordinate < super.dimension; indexCoordinate++) {
				this.deviation[indexGaussian][indexCoordinate] = deviation;
			}
		}
	}
	
	/**
	 * @param density: Tableau de taille K contenant
	 * les densités associées à chaque gaussienne
	 */
	public void setDensity(double[] density) {
		if (density.length != super.numberCentre) {
			throw new IllegalCallerException("Paramètres de setCentre() invalides");
		}
		this.density = density;
	}
	
	/**
	 * @param density: Réel pour initialiser toutes les densités
	 * des gaussiennes
	 */
	public void setDensity(double density) {
		for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
			this.density[indexGaussian] = density;
		}
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
			somme += this.dataGaussian[indexData][indexGaussian] * Math.pow(super.data[indexData][indexCoordinate] - this.mean[indexGaussian][indexCoordinate], 2);
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
			// Parcours des coordonnées
			for (int indexCoordinate = 0; indexCoordinate < super.dimension; indexCoordinate++) {
				// Calcul du produit au dénominateur
				double factor1 = 1. / Math.sqrt(2 * Math.PI * this.deviation[indexGaussianDenominator][indexCoordinate]);
				double factor2 = Math.exp(- Math.pow(super.data[indexData][indexCoordinate] - this.mean[indexGaussianDenominator][indexCoordinate], 2) / (2 * this.deviation[indexGaussianDenominator][indexCoordinate]));
				productDenominator *= (factor1 * factor2); 
				// Calcul du produit au numérateur
				if (indexGaussian == indexGaussianDenominator) {
					productNumerator *= (factor1 * factor2);
				}
			}
			// Calcul de la somme du dénominateur
			sumDenominator += this.density[indexGaussianDenominator] * productDenominator;
			productDenominator = 1;
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
				this.mean[indexGaussian][indexCoordinate] = this.mean(indexGaussian, indexCoordinate);
				this.deviation[indexGaussian][indexCoordinate] = this.deviation(indexGaussian, indexCoordinate);
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
			for (int i = 0; i < maxIteration; i++) {
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
	
	@Override
	public String toString() {
		
		// Données
		String dataString = "data={";
		for (int indexData = 0; indexData < super.numberData; indexData++) {
			dataString += "{";
			for (int dim = 0; dim < super.dimension; dim++) {
				dataString += super.data[indexData][dim];
				if (dim + 1 != super.dimension) {
					dataString += ",";
				}
			}
			dataString += "}";
			if (indexData + 1 != super.numberData) {
				dataString += ",";
			}
		}
		dataString += "}";
		
		// Tableau d'assignement
		String gaussianString = "dataGaussian={";
		for (int indexData = 0; indexData < super.numberData; indexData++) {
			gaussianString += "{";
			for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
				gaussianString += this.dataGaussian[indexData][indexGaussian];
				if (indexGaussian + 1 != super.numberCentre) {
					gaussianString += ",";
				}
			}
			gaussianString += "}";
			if (indexData + 1 != super.numberData) {
				gaussianString += ",";
			}
		}
		gaussianString += "}";
		
		// Position des centres des gaussiennes
		String meanString = "mean={";
		for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
			meanString += "{";
			for (int indexCoordinate = 0; indexCoordinate < super.dimension; indexCoordinate++) {
				meanString += this.mean[indexGaussian][indexCoordinate];
				if (indexCoordinate + 1 != super.dimension) {
					meanString += ",";
				}
			}
			meanString += "}";
			if (indexGaussian + 1 != super.numberCentre) {
				meanString += ",";
			}
		}
		meanString += "}";
		
		// Variance des gaussiennes
		String deviationString = "deviation={";
		for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
			deviationString += "{";
			for (int indexCoordinate = 0; indexCoordinate < super.dimension; indexCoordinate++) {
				deviationString += this.deviation[indexGaussian][indexCoordinate];
				if (indexCoordinate + 1 != super.dimension) {
					deviationString += ",";
				}
			}
			deviationString += "}";
			if (indexGaussian + 1 != super.numberCentre) {
				deviationString += ",";
			}
		}
		deviationString += "}";
		
		// Densité des gaussiennes
		String densityString = "density= {";
		for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
			densityString += this.density[indexGaussian];
			if (indexGaussian + 1 != super.numberCentre) {
				densityString += ",";
			}
		}
		densityString += "}";
		
		String others = "dimension=" + super.dimension + "\n" + "numberData=" + super.numberData + "\n" + "numberGaussian=" + super.numberCentre + "\n" + "learningState=" + super.learningState;
		
		return dataString + "\n" + gaussianString + "\n" + meanString + "\n" + deviationString + "\n" + densityString + "\n" + others;
	}
	
	/**
	 * @apiNote Permet d'obtenir l'indice de la gaussienne
	 * associée à l'indice d'une donnée.
	 *  
	 * @param indexData: Indice de la donnée dans [0 ; D[
	 * 
	 * @returnIndice de la gaussienne dans [0 ; K[
	 */
	public int answer(int indexData) {
		double maximum = this.dataGaussian[indexData][0];
		int resultat = 0;
		for (int indexGaussian = 1; indexGaussian < super.numberCentre; indexGaussian++) {
			if (this.dataGaussian[indexData][indexGaussian] > maximum) {
				maximum = this.dataGaussian[indexData][indexGaussian];
				resultat = indexGaussian;
			}
		}
		return resultat;
	}
	
	/**
	 * @apiNote Initialise le tableau d'assignation
	 * des données aux gaussiennes
	 */
	public void initialiseDataGaussian() {
		// Assignation temporaire de l'ancien tableau d'assignation
		super.dataCentre = new int[super.numberData];
		super.assignCentre();
		// Traduction de ce tableau avec le tableau d'assignaion pour gaussienne
		for (int indexData = 0; indexData < super.numberData; indexData++) {
			this.dataGaussian[indexData][super.dataCentre[indexData]] = 1.0;
		}
		// Suppression de l'ancien tableau
		super.dataCentre = null;
	}
	
	@Override
	public void initialise() {
		// Initialisation des centres de gaussiennes
		super.initialise();
		// Initialisation du tableau d'assignation
		this.initialiseDataGaussian();
		// Parcours des centres
		double density = 1. / (double)super.numberCentre;
		for (int indexCentre = 0; indexCentre < super.numberCentre; indexCentre++) {
			// Initialisation de la densité
			this.density[indexCentre] = density;
			// Initialisation de la variance
			for (int indexCoordinate = 0; indexCoordinate < super.dimension; indexCoordinate++) {
				this.deviation[indexCentre][indexCoordinate] = orderOfMagnitude;
			}
		}
	}
	
	/**
	 * @apiNote Permet d'obtenir le score de l'algorithme
	 * sur une donnée.
	 * 
	 * @param indexData: Indice de la donnée dans [0 ; D[
	 * 
	 * @return Réel correspondant au score
	 */
	public double score(int indexData) {
		double sum = 0;
		// Parcours des gaussiennes
		for (int indexGaussian = 0; indexGaussian < super.numberCentre; indexGaussian++) {
			double product = 1;
			// Parcours des coordonnées
			for (int indexCoordinate = 0; indexCoordinate < super.dimension; indexCoordinate++) {
				double factor1 = 1. / Math.sqrt(2 * Math.PI * this.deviation[indexGaussian][indexCoordinate]);
				double factor2 = Math.exp(- Math.pow(super.data[indexData][indexCoordinate] - this.mean[indexGaussian][indexCoordinate], 2) / (2 * this.deviation[indexGaussian][indexCoordinate]));
				product *= (factor1 * factor2);
			}
			sum += this.density[indexGaussian] * product;
		}
		return Math.log(sum);
	}
	
	/**
	 * @apiNote Permet d'obtenir le score total de l'algorithme
	 * sur l'ensemble des données.
	 * 
	 * @return Réel correspondant au score total
	 */
	public double score() {
		double sum = 0;
		// Parcours des données
		for (int indexData = 0; indexData < super.numberData; indexData++) {
			sum += this.score(indexData);
		}
		return sum / (double)super.numberData;
	}
}










