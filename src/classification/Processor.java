package classification;

import java.util.ArrayList;

import model.CallStack;
import model.Frame;

/**
 * Compare deux objets CallStack entre eux, pour obtenir la similarite.
 * Se base sur l'article de Windows pour le calcul de la similarite.
 * 
 * @author pepe
 *
 */
public class Processor {

    /**
     * "coefficient to the top frame"
     * "c"
     */
    private int coefficient = 1;
    
    /**
     * "coefficient for the alignement offset"
     * "o"
     */
    private int offset;

    public Processor() {
        // empty
    }
    
    /**
     * Choisir le coefficient et le offset intervenant dans le calcul de similarite
     * Plus d'informations dans l'article de Windows
     * @param coefficient 
     * @param offset
     */
    public Processor(int coefficient, int offset) {
        this.coefficient = coefficient;
        this.offset = offset;
    }

    public double similarity(CallStack c1, CallStack c2) {
        
        double[][] matrix = new double[c1.getCallStack().size()][c2.getCallStack().size()];

        // parcours de c1
        for (int i = 0; i < matrix.length; i++) {
            
            // parcours de c2
            for (int j = 0; j < matrix[i].length; j++) {
                
                double previous_ij, previous_i, previous_j, cost;
                previous_ij = previous_i = previous_j = cost = 0.0;
                
                // deux frames auront une similarite de 0 au minimun 
                matrix[i][j] = cost;
                
                // si les deux frames sont identiques le poids est calcule
                if (c1.getCallStack().get(i).equals(c2.getCallStack().get(j))) {
                    matrix[i][j] = cost(i, j);
                }
                
                if ((i > 0) && (j > 0)) {
                    previous_ij = matrix[i-1][j-1] + cost;
                } else if (i > 0) {
                    previous_i = matrix[i-1][j];
                } else if (j > 0) {
                    previous_i = matrix[i][j-1];
                }
                
                /*
                 * chercher la plus grande valeur entre :
                 * le poids,
                 * ou le poids + matrix[i-1][j-1],
                 * ou matrix[i-1][j],
                 * ou matrix[i][j-1]
                 */
                double max_value = Math.max(cost,
                        Math.max(previous_ij + cost,
                                Math.max(previous_i, previous_j)));
                
                // enregistrement
                matrix[i][j] = max_value;
                
            }

                
        }
            //System.out.println("i = " + i + " ==> " + matrix.get(i));
        double similarity = 0.0;
        return similarity;
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

        //top frame
        double top = Math.exp(-this.coefficient * Math.min(i, j));
        
        // alignement offset
        double offset = Math.exp(-this.offset * Math.abs(i-j));
        
        System.out.println(" >>>>>>>>>> cost = "+top*offset);
        return top * offset;

    }

}
