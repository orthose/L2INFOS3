package partitionnement;

import java.util.Arrays;

/**
 * @apiNote Algorithme des k-moyennes.
 * 
 * @author Maxime Vincent
 * @version 14/11/19
 */
public class Kmeans implements LearningAlgorithmKmeans {
	
	// Attributs
	protected double[][] data; // Données
	protected int[] dataCentre; // Centres associés aux données
	protected double[][] centre; // Centres
	protected int dimension; // Dimension des points
	protected int numberData; // Nombre de données
	protected int numberCentre; // Nombre de centres
	protected boolean learningState; // Etat de l'apprentissage
	
	// Constructeurs
	public Kmeans(double[][] data, int numberCentre) {
		if (data.length == 0 || numberCentre <= 0) {
			throw new IllegalCallerException("Paramètres du constructeur invalides");
		}
		this.data = data;
		this.dimension = data[0].length;
		this.numberData = data.length;
		this.numberCentre = numberCentre;
		this.centre = new double[this.numberCentre][this.dimension];
		this.dataCentre = new int[this.numberData];
		this.learningState = false;
	}
	
	// Getters / Setters
	public double[][] getData() {
		return this.data;
	}
	
	public int[] getDataCentre() {
		return this.dataCentre;
	}

	public void setData(double[][] data) {
		if (data.length == 0 || data[0].length != this.dimension) {
			throw new IllegalCallerException("Paramètres de setData() invalides");
		}
		this.data = data;
		this.numberData = data.length;
	}

	public double[][] getCentre() {
		return this.centre;
	}

	public void setCentre(double[][] centre) {
		if (centre.length != this.numberCentre || centre[0].length != this.dimension) {
			throw new IllegalCallerException("Paramètres de setCentre() invalides");
		}
		this.centre = centre;
		this.numberCentre = centre.length;
	}
	
	// Détruit les positions des centres actuels
	public void setNumberCentre(int numberCentre) {
		if (numberCentre <= 0) {
			throw new IllegalCallerException("Paramètre de setNumberCentre() invalide");
		}
		this.numberCentre = numberCentre;
		this.centre = new double[this.numberCentre][this.dimension];
	}
	
	// Méthodes
	/**
	 * @param point1
	 * @param point2
	 * @return Distance entre point1 et point2
	 */
	private double distance(double[] point1, double[] point2) {
		double resultat = 0;
		// Parcours des coordonnées des deux points
		for (int dim = 0; dim < this.dimension; dim++) {
			resultat += Math.pow((point1[dim] - point2[dim]), 2);
		}
		return Math.sqrt(resultat);
	}
	
	/**
	 * @apiNote Assigne à chaque point son centre le plus proche
	 */
	protected void assignCentre() {
		
		// Parcours de tous les points
		for (int indexData = 0; indexData < this.numberData; indexData++) {
			
			double minimumDistance = this.distance(this.data[indexData], this.centre[0]);
			int assignedCentre = 0;
			
			// Parcours de tous les centres
			for (int indexCentre = 1; indexCentre < this.numberCentre; indexCentre++) {
				
				double distanceToCentre = this.distance(this.data[indexData], this.centre[indexCentre]);
				if (minimumDistance > distanceToCentre) {
					minimumDistance = distanceToCentre;
					assignedCentre = indexCentre;
				}
			}
			
			// Assignement du centre pour le point courant
			this.dataCentre[indexData] = assignedCentre;
		}
	}
	
	/**
	 * @apiNote Déplace les centres
	 * @return Distance de déplacement total des centres
	 */
	private double moveCentre() {
		
		double resultat = 0;
		// Parcours de tous les centres
		for (int indexCentre = 0; indexCentre < this.numberCentre; indexCentre++) {
			
			double[] newCentre = new double[this.dimension];
			double dataClusterCounter = 0;
			
			// Parcours de tous les points
			for (int indexData = 0; indexData < this.numberData; indexData++) {
				
				// Vérification de l'appartenance du point au groupe
				if (this.dataCentre[indexData] == indexCentre) {
					
					dataClusterCounter++;
					// Parcours des coordonnées du point courant
					for (int dim = 0; dim < this.dimension; dim++) {
						newCentre[dim] += this.data[indexData][dim];
					}
				}
			}
			
			// Division de chaque coordonnée par le nombre de points dans le groupe
			if (dataClusterCounter != 0) {
				for (int dim = 0; dim < this.dimension; dim++) {
					newCentre[dim] /= dataClusterCounter;
				}
			}
			
			// Calcul de la distance cumulée par rapport au centre déplacé
			resultat += this.distance(this.centre[indexCentre], newCentre);
			
			// Déplacement du centre courant
			if (dataClusterCounter != 0) {
				this.centre[indexCentre] = Arrays.copyOf(newCentre, this.centre.length);
			}
		}
		return resultat;
	}
	
	/**
	 * @apiNote Démarre l'apprentissage
	 * @param minMoveDistance Distance minimale de déplacement des centres
	 * @param maxIteration Nombre d'itérations maximum
	 */
	public void runLearning(double minMoveDistance, int maxIteration) {
		
		if (! this.learningState) {
			for (int i = 0; i < maxIteration; i++) {
				
				// Assignation des centres aux données
				this.assignCentre();
				//System.out.println(this);
				
				// Déplacement des centres
				if (this.moveCentre() <= minMoveDistance) {
					break;
				}
				//System.out.println(this);
			}
			this.learningState = true;
		}
		else {
			throw new IllegalCallerException("L'apprentissage a déjà été effectué");
		}
	}
	
	/**
	 * @apiNote Démarre l'apprentissage
	 * @param minMoveDistance Distance minimale de déplacement des centres
	 */
	public void runLearning(double minMoveDistance) {
		this.runLearning(minMoveDistance, 1000000);
	}
	
	/**
	 * @apiNote Démarre l'apprentissage
	 * @param maxIteration Nombre d'itérations maximum
	 */
	public void runLearning(int maxIteration) {
		this.runLearning(0.001, maxIteration);
	}
	
	/**
	 * @apiNote Initialise aléatoirement les centres avant un nouvel apprentissage
	 */
	public void initialise() {
		
		// Bornes de l'espace considéré
		double[] maxCoordData = new double[this.dimension];
		double[] minCoordData = new double[this.dimension];
		
		// Initialisation pour la première donnée
		for (int dim = 0; dim < this.dimension; dim++) {
			maxCoordData[dim] = this.data[0][dim];
			minCoordData[dim] = this.data[0][dim];
		}
		
		// Recherche des coordonnées maximales et minimales
		for (int indexData = 1; indexData < this.numberData; indexData++) {
			for (int dim = 0; dim < this.dimension; dim++) {
				if (this.data[indexData][dim] > maxCoordData[dim]) {
					maxCoordData[dim] = this.data[indexData][dim];
				}
				if (this.data[indexData][dim] < maxCoordData[dim]) {
					minCoordData[dim] = this.data[indexData][dim];
				}
			}
		}
		
		// Initialisation aléatoire des centres
		for (int indexCentre = 0; indexCentre < this.numberCentre; indexCentre++) {
			for (int dim = 0; dim < this.dimension; dim++) {
				this.centre[indexCentre][dim] = Math.random() * (maxCoordData[dim] - minCoordData[dim] + 1) + minCoordData[dim];
			}
		}
		this.learningState = false;
	}
	
	@Override
	public String toString() {
		
		String dataString = "data={";
		for (int indexData = 0; indexData < this.numberData; indexData++) {
			dataString += "{";
			for (int dim = 0; dim < this.dimension; dim++) {
				dataString += this.data[indexData][dim];
				if (dim + 1 != this.dimension) {
					dataString += ",";
				}
			}
			dataString += "}";
			if (indexData + 1 != this.numberData) {
				dataString += ",";
			}
		}
		dataString += "}";
		
		String dataCentreString = "dataCentre={";
		for (int centre : this.dataCentre) {
			dataCentreString += centre+",";
		}
		dataCentreString += "}";
		
		String centreString = "centre={";
		for (int indexCentre = 0; indexCentre < this.numberCentre; indexCentre++) {
			centreString += "{";
			for (int dim = 0; dim < this.dimension; dim++) {
				centreString += this.centre[indexCentre][dim];
				if (dim + 1 != this.dimension) {
					centreString += ",";
				}
			}
			centreString += "}";
			if (indexCentre + 1 != this.numberData) {
				centreString += ",";
			}
		}
		centreString += "}";
		
		String others = "dimension=" + this.dimension + "\n" + "numberData=" + this.numberData + "\n" + "numberCentre=" + this.numberCentre + "\n" + "learningState=" + this.learningState;
		
		return "(" + dataString + "\n" + dataCentreString + "\n" + centreString + "\n" + others + ")";
		
		
	}

}
