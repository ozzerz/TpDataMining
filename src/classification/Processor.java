package classification;

import java.util.ArrayList;
import model.Bucket;
import model.CallStack;

/**
 * Compare deux objets CallStack entre eux, pour obtenir la similarite. Se base
 * sur l'article de Windows pour le calcul de la similarite.
 *
 * @author pepe
 *
 */
public class Processor {
	/**
	 * "coefficient to the top frame" "c"
	 */
	private int coefficient = 1;
	/**
	 * "coefficient for the alignement offset" "o"
	 */
	private int offset = 1;
	// matrice dynamique pour deux callstacks en cours de comparaison
	private double matrix[][];
	// c1 et c2 sont les deux callstacks a comparer
	private CallStack c1;
	private CallStack c2;

	public Processor() {
		// empty
	}

	/**
	 * Choisir le coefficient et le offset intervenant dans le calcul de
	 * similarite. Plus d'informations dans l'article de Windows. Les resultats
	 * de similarite entre deux callstacks changent en fonction de ses
	 * parametres.
	 *
	 * @param coefficient
	 * @param offset
	 */
	public Processor(int coefficient, int offset) {
		this.coefficient = coefficient;
		this.offset = offset;
	}

	/**
	 * Faire les clusters en reunissant les buckets entre eux. La distance entre
	 * les cluster correspond à la plus mauvaise similarite entre une callstack
	 * d'un premier bucket, et une callstack d'un second. Les deux clusters qui
	 * ont la meilleurs similarite parmis tous seront merges uniquement si leur
	 * similarite est superieur a minSimilarity. Si aucun cluster ne
	 * satisfassent entre eux une similarite meilleur a minSimilarity alors le
	 * bucketing stop.
	 *
	 * @param buckets
	 *            collection de buckets a merger
	 * @param minSimilarity
	 *            similarite minimale qu'au moins deux clusters doivent
	 *            satisfaire parmis la collection de clusters
	 * @return les buckets finaux
	 */
	public ArrayList<Bucket> bucketize(ArrayList<Bucket> buckets,
			double minSimilarity) {
		// le clustering s'arrete lorsque cela devient vrai
		// i.e plus aucune similarite sont suffisante pour continuer le
		// clustering
		boolean tooLowSimilarities = false;
		// clusteriser tant que des similarites entre buckets sont encore
		// superieur a minSimilarity
		while (!tooLowSimilarities) {
			// naivement minSimilarity non atteinte (repasse a false apres si
			// une similarity superieur est obtenu entre deux buckets)
			tooLowSimilarities = true;
			// parcourir les buckets
			for (int i = 0; i < buckets.size(); i++) {
				// similarite la plus elevee
				double bestSimilarity = 0.0;
				// indice du bucket avec la similarite la plus elevee
				int bestSimilarityIndex = 0;
				// calcule de la similarite du bucket i avec tout les bukets j
				for (int j = 0; j < buckets.size(); j++) {
					// ne pas comparer le bucket avec lui meme
					if (j != i) {
						double sim = similarity(buckets.get(i), buckets.get(j));
						// memoriser la similarite la plus elevee et l'indice du
						// bucket correspondant
						if (sim > bestSimilarity) {
							bestSimilarity = sim;
							bestSimilarityIndex = j;
						}
					}
				} // for j
					// la similarite doit depasser le seuil minimale
				if (bestSimilarity >= minSimilarity) {
					// au moins une similarite est donc superieur au seuil
					// minSimilarity
					tooLowSimilarities = false;
					// ****************************************************
					// merge le bucket i avec le bucket j le plus similaire
					// ****************************************************
					buckets.get(i)
							.mergeBucket(buckets.get(bestSimilarityIndex));
					// le bucket merge n'a plus lieu d'etre present tout seul
					// dans la collection de buckets
					buckets.remove(bestSimilarityIndex);
				}
			} // for i
		} // while
		return buckets;
	}

	/**
	 * La similarite entre deux clusters correspond a la similarite minimale
	 * entre deux callstacks provenant du premier bucket, et du second. Les
	 * similarites entre chacune des callstacks des buckets doivent etre
	 * comparees.
	 *
	 * @param b1
	 *            premier bucket
	 * @param b2
	 *            second bucket a comparer
	 * @return similarite entre les deux buckets
	 */
	public double similarity(Bucket b1, Bucket b2) {
		double minSimilarity = 1.0;
		for (int i = 0; i < b1.getCallStacks().size(); i++) {
			for (int j = 0; j < b2.getCallStacks().size(); j++) {
				double similarity = similarity(b1.getCallStacks().get(i), b2
						.getCallStacks().get(j));
				minSimilarity = Math.min(minSimilarity, similarity);
			}
		}
		return minSimilarity;
	}
/**
 * Calcul la similarité entre deux pile d'appel
 * @param c1 la premiere pile
 * @param c2 la seconde pile
 * @return la similarité entre ces 2 piles
 */
	public double similarity(CallStack c1, CallStack c2) {
		this.c1 = c1;
		this.c2 = c2;
		this.matrix = new double[c1.getCallStack().size()][c2.getCallStack()
				.size()];
		// nombre de frames de c1 et c2 identiques (appelee "l" dans l'article
		// Windows
		int identic_frames = 0;
		// parcours de c1
		for (int i = 0; i < this.matrix.length; i++) {
			// parcours de c2
			for (int j = 0; j < this.matrix[i].length; j++) {
				double previous_ij_cost, previous_i, previous_j, cost;
				previous_ij_cost = previous_i = previous_j = cost = 0.0;
				cost = cost(i, j);
				if ((i > 0) && (j > 0)) {
					previous_ij_cost = this.matrix[i - 1][j - 1] + cost;
				} else if (i > 0) {
					previous_i = this.matrix[i - 1][j];
				} else if (j > 0) {
					previous_i = this.matrix[i][j - 1];
				}
				/*
				 * chercher la plus grande valeur entre : le poids, ou le poids
				 * + matrix[i-1][j-1], ou matrix[i-1][j], ou matrix[i][j-1]
				 */
				double max_value = Math.max(
						cost,
						Math.max(previous_ij_cost,
								Math.max(previous_i, previous_j)));
				// enregistrement dans la matrice
				// System.out.println(max_value);
				this.matrix[i][j] = max_value;
			} // fin j parcours de c2
		} // fin i parcours de c1
		double sum = 0.0;
		for (int l = 0; l < (Math.min(this.c1.getCallStack().size(), this.c2
				.getCallStack().size())); l++) {
			sum = sum + Math.exp(-this.coefficient * l);
		}
		// sim (4) dans l'article Windows
		return this.matrix[this.matrix.length - 1][this.matrix[this.matrix.length - 1].length - 1]
				/ sum;
	}

	/**
	 * calculer le poids entre i et j. Ce sont les indices de la matrice en
	 * cours de traitement dans la methode similarity
	 *
	 * @param i
	 *            (indice) numero de frame i de la premiere callstack
	 * @param j
	 *            (indice) numere de frame j de la seconde callstack
	 * @return
	 */
	protected double cost(int i, int j) {
		// top frame
		if (this.c1.getCallStack().get(i).equals(this.c2.getCallStack().get(j))) {
			double top = Math.exp(-this.coefficient * Math.min(i, j));
			// alignement offset
			double offset = Math.exp(-this.offset * Math.abs(i - j));
			return top * offset;
		}
		return 0.0;
	}
}