package classification;

import java.util.ArrayList;

import model.CallStack;
import model.Frame;


/**
 * Compare two CallStacks, calculate similarity.
 * @author pepe
 *
 */
public class Processor {

    /**
     * Coefficient de similarite. Definie par l'utilisateur.
     * Correspond au poids de la frame selon sa hauteur dans la stack.
     */
    private int coefficient = 0;
    
    public Processor() {
        // empty
    }
    
    
    
    
    public double similarity(CallStack c1, CallStack c2) {
        
        // plus petite valeur entre distanceTop, et la taille de chacune des deux stacks
        //this.distanceTop = Math.min(distanceTop, Math.min(c1.getCallStack().size(), c2.getCallStack().size()));
        
        // matrice de similarite
        ArrayList<ArrayList<Integer>> matrix = 
                new ArrayList<ArrayList<Integer>>(c1.getCallStack().size());

        // i numero de la frame dans c1
        for (int i = 0; i < c1.getCallStack().size(); i++) {
            matrix.add(new ArrayList<Integer>());
            // j numero de la frame dans c2
            for (int j = 0; j < c2.getCallStack().size(); j++) {
                // si frame i et j identiques 
                if (c1.getCallStack().get(i).toString()
                        .equals(c2.getCallStack().get(j).toString())) {
                    // alors ajouter le numero de la frame j dans la liste i
                    matrix.get(i).add(j);
                }
                
            }
        }
        
        // "C" taille des similarites
        int taille = c1.getCallStack().size() *
                c2.getCallStack().size();
        
        double frameSimilaritiesSum = 0.0;
        for (int mx = 0; mx < matrix.size(); mx++) {
            for (int my = 0; my < matrix.get(mx).size(); my++) {
                double localSimilarity = Math.exp(-(this.coefficient*matrix.get(mx).get(my)));
                
                frameSimilaritiesSum +=  localSimilarity;
            }
        }
        
        if (frameSimilaritiesSum == 0) {
            return 0.0;
        } 
        
        double similarity = taille / frameSimilaritiesSum;
        return similarity;
        
        
    }

    
}
