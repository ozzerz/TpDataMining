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
     * Distance to the top of the CallStacks.
     * This distance define how many frames to compare.
     */
    private int distanceTop;
    
    /**
     * Coefficient de similarite. Definie par l'utilisateur.
     * Correspond au poids de la frame selon sa hauteur dans la stack.
     */
    private int coefficient;
    
    public Processor() {
        // vide
    }
    
    public Processor(int distanceTop) {
        this.distanceTop = distanceTop;
    }
    
    
    public void similarity(CallStack c1, CallStack c2) {
        
        // plus petite valeur entre distanceTop, et la taille de chacune des deux stacks
        this.distanceTop = Math.min(distanceTop, Math.min(c1.getCallStack().size(), c2.getCallStack().size()));
        
        // matrice de similarite
        ArrayList<ArrayList<Integer>> matrix = 
                new ArrayList<ArrayList<Integer>>(this.distanceTop);

        // i numero de la frame dans c1
        for (int i = 0; i < this.distanceTop; i++) {
            // j numero de la frame dans c2
            for (int j = 0; i < this.distanceTop; j++) {
                // si frame i et j identiques
                if (c1.getCallStack().get(i)
                        .equals(c2.getCallStack().get(j))) {
                    // alors ajouter le numero de la frame j dans la liste i
                    matrix.get(i).add(j);
                }
                
            }
        }
        
        // calcule de la similarite via la matrice
        int taille = c1.getCallStack().size() *
                c2.getCallStack().size();
        
    }

    
}
