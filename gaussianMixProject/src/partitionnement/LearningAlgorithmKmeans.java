package partitionnement;

/***
 * @apiNote Interface représentant
 * les méthodes nécessaires à un
 * algorithme d'apprentissage 
 * non-supervisé du type k-moyennes.
 * 
 * @author Maxime Vincent 
 */
public interface LearningAlgorithmKmeans {
	
	// Getters / Setters
	/**
	 * @apiNote Permet d'obtenir le jeu de données
	 * entré dans l'algorithme sous la forme d'un
	 * tableau à 2 dimensions, c'est-à-dire un
	 * tableau contenant N données correspondant
	 * à N tableaux chacun de dimension D.
	 * 
	 * @return tableau 2D de réels
	 */
	public double[][] getData();
	
	/**
	 * @apiNote Permet d'obtenir le tableau
	 * associant à chaque donnée un numéro
	 * de centre dans [0 ; K[ avec K un
	 * méta-paramètre à fixer en début
	 * d'apprentissage. Ce tableau est donc
	 * de taille N correspondant au nombre
	 * de données.
	 * 
	 * @return tableau 1D d'entiers
	 */
	public int[] getDataCentre();
	
	/**
	 * @apiNote Permet de modifier le jeu de
	 * données entré dans l'algorithme.
	 * 
	 * @param data: Tableau 2D de réels de
	 * N données à D dimensions.
	 */
	public void setData(double[][] data);
	
	/**
	 * @apiNote Permet d'obtenir les positions
	 * des centres sous la forme d'un tableau
	 * à deux dimensions de taille K centres
	 * et composé de tableaux de taille D dimensions.
	 * 
	 * @return tableau 2D de réels
	 */
	public double[][] getCentre();
	
	/**
	 * @apiNote Permet de modifier la position
	 * des centres.
	 * 
	 * @param centre: Tableau 2D de réels de 
	 * K centres à D dimensions
	 */
	public void setCentre(double[][] centre);
	
	/**
	 * @apiNote Méthode d'assignation d'un centre
	 * à chaque donnée, nécessaire à l'apprentissage.
	 * On assigne à une donnée son centre le plus proche.
	 */
	private void assignCentre() {
	}
	
	/**
	 * @apiNote Méthode de déplacement des centres,
	 * nécessaire à l'apprentissage.
	 * 
	 * @return réel mesurant la ditance totale de
	 * déplacement des centres
	 */
	private double moveCentre() {
		return 0;
	}
	
	/**
	 * @apiNote Lance l'apprentissage automatiquement.
	 * 
	 * @param minMoveDistance: Distance minimale de déplacement des centres.
	 * Lorsque la distance renseignée est inférieure à la distance
	 * de déplacement des centres, l'apprentissage prend fin.
	 * 
	 * @param maxIteration: Nombre d'itérations maximum au-delà duquel
	 * l'algorithme prend fin.
	 */
	public void runLearning(double minMoveDistance, int maxIteration);
	
	/**
	 * @apiNote Permet d'initialiser les centres
	 * avant apprentissage.
	 */
	public void initialiseCentre();
}
