package classification;

import java.util.ArrayList;
import java.util.Arrays;

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
    
    private double matrix[][];
    
    private CallStack c1;
    
    private CallStack c2;

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
        this.c1 = c1;
        this.c2 = c2;
        this.matrix = new double[c1.getCallStack().size()][c2.getCallStack().size()];
        
        // nombre de frames de c1 et c2 identiques (appelee "l" dans l'article Windows
        int identic_frames = 0;
        
        // parcours de c1
        for (int i = 0; i < this.matrix.length; i++) {
            
            // parcours de c2
            for (int j = 0; j < this.matrix[i].length; j++) {
                
                double previous_ij_cost, previous_i, previous_j, cost;
                previous_ij_cost = previous_i = previous_j = cost = 0.0;
                
                cost = cost(i, j);

                if ((i > 0) && (j > 0)) {
                    previous_ij_cost = this.matrix[i-1][j-1] + cost;
                } else if (i > 0) {
                    previous_i = this.matrix[i-1][j];
                } else if (j > 0) {
                    previous_i = this.matrix[i][j-1];
                }
                
                /*
                 * chercher la plus grande valeur entre :
                 * le poids,
                 * ou le poids + matrix[i-1][j-1],
                 * ou matrix[i-1][j],
                 * ou matrix[i][j-1]
                 */
                double max_value = Math.max(cost,
                        Math.max(previous_ij_cost,
                                Math.max(previous_i, previous_j)));
                
                // enregistrement dans la matrice
                //System.out.println(max_value);
                this.matrix[i][j] = max_value;
                
            } // fin j parcours de c2
            System.out.println(Arrays.toString(this.matrix[i]));
        } // fin i parcours de c1
        
        // similarite vaut 0 si aucune frames en commun (a verifier!) ==> evite d'avoir une similarite "Infinity" qui pourrait mal interprete
        if (identic_frames == 0) return 0;
        
        double sum = 0.0;
        for (int l = 0; (l < identic_frames) && (l < c2.getCallStack().size()); l++) {
            sum = sum + Math.exp(-this.coefficient * l);
        }        
        // sim (4) dans l'article Windows
        return this.matrix[this.matrix.length-1][this.matrix[this.matrix.length-1].length-1];
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
        if (this.c1.getCallStack().get(i) == this.c2.getCallStack().get(j)) {
            double top = Math.exp(-this.coefficient * Math.min(i, j));
            System.out.println("top : "+top);
            // alignement offset
            double offset = Math.exp(-this.offset * Math.abs(i-j));
            System.out.println("offset : "+offset);
            
            return top * offset;
        }
        
        return 0.0;

    }

}
