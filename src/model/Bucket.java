package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * design pour un Bucket
 *
 * @author Ozzerz
 *
 */
public class Bucket {

    /**
     * Le nom du bucket
     */
    private String name;

    /**
     * La liste des CallStacks
     */
    private ArrayList<CallStack> callStacks;

    private Bucket(String name) {
        this.name = name;
        this.callStacks = new ArrayList<CallStack>();
    }

    public Bucket() {
        this.callStacks = new ArrayList<CallStack>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<CallStack> getCallStacks() {
        return callStacks;
    }

    public void setCallStacks(ArrayList<CallStack> callStacks) {
        this.callStacks = callStacks;
    }

    /**
     * Ajoute cette callStack au bucket
     *
     * @param callStack
     *            La callStack que l'on va ajouter
     */
    public void addCallStack(CallStack callStack) {
        this.callStacks.add(callStack);
    }

    /**
     * Ajouter les callstacks d'un autre bucket dans celui-ci.
     * @param bucket
     */
    public void mergeBucket(Bucket bucket) {
        this.callStacks.addAll(bucket.callStacks);
    }

    /**
     * Pour savoir si une CallStack appartient au bucket
     *
     * @param callStack
     * @return Vraie si elle appartient , faux sinon
     */
    public boolean isOnBucket(CallStack callStack) {
        return callStack.getGroupId().equals(name);
    }

    /**
     * Creer les buckets SEULEMENT POUR EVALUTATION
     *
     * @param allCallStacks
     * @return
     */
    public ArrayList<Bucket> creationAllBucket(
            ArrayList<CallStack> allCallStacks) {
        ArrayList<Bucket> allBucket = new ArrayList<Bucket>();
        boolean verif = false;
        for (int call = 0; call < allCallStacks.size(); call++) {
            verif = false;
            for (int buck = 0; buck < allBucket.size(); buck++) {
                if (allBucket.get(buck).isOnBucket(allCallStacks.get(call))) {
                    allBucket.get(buck).addCallStack(allCallStacks.get(call));
                    verif = true;
                }

            }
            if (!verif) {

                if (allCallStacks.get(call).getDuplicateId().equals("-1")) {
                    Bucket bu = new Bucket(allCallStacks.get(call).getGroupId());
                    bu.addCallStack((allCallStacks.get(call)));
                    allBucket.add(bu);
                    verif = false;
                } else {
                    Bucket bu = new Bucket(allCallStacks.get(call)
                            .getDuplicateId());
                    bu.addCallStack((allCallStacks.get(call)));
                    allBucket.add(bu);
                    verif = false;
                }
            }

        }
        return allBucket;
    }

    /**
     * Creer le fichier du Bucket dans le repertoire (le repertoire doit exister)
     *
     * @param directory
     * @throws IOException
     */
    public void createFile(String directory) throws IOException {
        String nomFichier = directory + "/" + this.name + ".txt";
        System.out.println(nomFichier);
        File file = new File(nomFichier);
        file.createNewFile();
        FileWriter writer = null;
        String texte = this.toString();
        try {
            writer = new FileWriter(nomFichier, true);
            writer.write(texte, 0, texte.length());
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }

    }


   /** Creer le fichier du Bucket dans le repertoire (le repertoire doit exister)
    *
    * @param directory
    * @throws IOException
    */
   public void createFile(String directory,String filename) throws IOException {
       String nomFichier = directory + "/" + filename + ".txt";
       System.out.println(nomFichier);
       File file = new File(nomFichier);
       file.createNewFile();
       FileWriter writer = null;
       String texte = this.toString();
       try {
           writer = new FileWriter(nomFichier, true);
           writer.write(texte, 0, texte.length());
       } catch (IOException ex) {
           ex.printStackTrace();
       } finally {
           if (writer != null) {
               writer.close();
           }
       }

   }

    public String toString() {
        String str = "";
        for (int i = 0; i < callStacks.size(); i++) {
            if (i + 1 < callStacks.size()) {
                str = str + callStacks.get(i).getFilename()
                        + System.getProperty("line.separator");
            } else {
                str = str + callStacks.get(i).getFilename();
            }
        }

        return str;
    }
}
