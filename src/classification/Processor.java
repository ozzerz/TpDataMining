package classification;

import java.util.ArrayList;

import model.CallStack;
import model.Frame;

/**
 * Compare two CallStacks, calculate similarity.
 * 
 * @author pepe
 *
 */
public class Processor {

    /**
     * Coefficient de similarite. Definie par l'utilisateur. Correspond au poids
     * de la frame selon sa hauteur dans la stack.
     */
    private int coefficient = 0;

    public Processor() {
        // empty
    }

    public double similarity(CallStack c1, CallStack c2) {

        // plus petite valeur entre distanceTop, et la taille de chacune des
        // deux stacks
        // this.distanceTop = Math.min(distanceTop,
        // Math.min(c1.getCallStack().size(), c2.getCallStack().size()));

        // coefficient top frame
        int c = 1;

        // coefficient offset
        int o = 1;

        // instancier la matrice de similarites
        ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>(
                c1.getCallStack().size());

        // valeur de matrix(i-1, j-1)
        double previous_value_ij = 0;
        // valeur de matrix(i-1, j)
        double previous_value_i = 0;
        // valeur de matrix(i, j-1)
        double previous_value_j = 0;
        // v

        // parcours des frames de c1
        for (int i = 0; i < c1.getCallStack().size(); i++) {
            matrix.add(new ArrayList<Integer>());
            // parcours des frames de c2
            for (int j = 0; j < c2.getCallStack().size(); j++) {
                // reinitialisation
                previous_value_ij = 0;
                previous_value_i = 0;
                previous_value_j = 0;

                // calcule des precedentes valeurs de matrix
                if (i > 0) {
                    previous_value_i = matrix.get(i-1).get(j);
                }
                if (j > 0) {
                    previous_value_j = matrix.get(i).get(j-1);
                }
                if ((i > 0) && (j > 0)) {
                    previous_value_ij = matrix.get(i-1).get(j-1);
                }

                // valeur de matrix(i-1, j-1) + le poids actuel des frames i,j
                previous_value_ij = previous_value_ij
                        + cost(c1.getCallStack().get(i),
                                c2.getCallStack().get(j));

                // get max
                double val = Math.max(previous_value_ij,
                        Math.max(previous_value_i, previous_value_j));
                matrix.get(i).add(j);
            }
            System.out.println("i = " + i + " ==> " + matrix.get(i));
        }

        // ------------------------------- A MODIFIER
        // ---------------------------------
        /*
         * int taille = c1.getCallStack().size() * c2.getCallStack().size();
         * 
         * double frameSimilaritiesSum = 0.0; for (int mx = 0; mx <
         * matrix.size(); mx++) { for (int my = 0; my < matrix.get(mx).size();
         * my++) { double localSimilarity =
         * Math.exp(-(this.coefficient*matrix.get(mx).get(my)));
         * 
         * frameSimilaritiesSum += localSimilarity; } }
         * 
         * if (frameSimilaritiesSum == 0) { return 0.0; }
         * 
         * double similarity = taille / frameSimilaritiesSum;
         */
        double similarity = 0.0;
        return similarity;
    }

    /**
     * calculer le poids entre i et j. Ce sont les indices de la matrices en
     * cours de traitement dans la methode similarity
     * 
     * @param i
     *            numero de frame i de la premiere callstack
     * @param j
     *            numere de frame j de la seconde callstack
     * @return
     */
    private double cost(Frame i, Frame j) {
        double cost = 0;

        /*
         * if (matrix.get(i == j) {
         * 
         * }
         */
        return cost;

    }

}
