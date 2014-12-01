package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import classification.Processor;
import model.Bucket;
import model.CallStack;

public class Main {

    public void createBuckets(String inputDir, String outputDir) {
        String directory="Votre repertoire";
        String resultDirectory="Le repertoire contenant vaut résultat";
        Parser p = new Parser();
        ArrayList<CallStack> allCallStack = p.readAll(inputDir); // add all id
        for (int i = 0; i < allCallStack.size(); i++) {

                p.addGroupIdAndDuplicateId(allCallStack.get(i), directory + "/"
                                + allCallStack.get(i).getFilename());
        }
        Bucket bucket = new Bucket();
        ArrayList<Bucket> allBuck = bucket.creationAllBucket(allCallStack);
        
        File rep = new File(outputDir);//création du répértoire
        rep.mkdir();
        for (int i = 0; i < allBuck.size(); i++) {
                try {
                        allBuck.get(i).createFile(outputDir);
                } catch (IOException e) { //
                }
        }

    }
    

    /**
     * Recuperer seulement les 800 buckets les plus volumineux (les autres n'ont qu'un fichier de rapport)
     * @param repFin
     * @throws FileNotFoundException
     */
    public static void recup800Bucket(String repFin)
            throws FileNotFoundException {
        // on créer le repertoire
        File rep = new File(repFin);// création du répértoire
        if (!rep.exists()) {
            rep.mkdir();
        }

        String ligne;
        String fichier = "sizes.txt";
        ArrayList<String> fichierPile = new ArrayList<String>();// contiendra
                                                                // les nom du
                                                                // fichier avec
                                                                // la pile
        InputStream ips = new FileInputStream(fichier);
        InputStreamReader ipsr = new InputStreamReader(ips);
        BufferedReader br = new BufferedReader(ipsr);

        for (int i = 0; i < 800; i++) {

            try {
                ligne = br.readLine();
                fichierPile.add(ligne.substring(ligne.indexOf(" ")));

            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        try {
            br.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // pour chacun des fichier gardé on va copier le fichier dans le
        // repertoire de destination
        for (int fic = 0; fic < fichierPile.size(); fic++) {
            System.out.println("on essaie de copier le fichier");
            String newFileName = repFin
                    + "/"
                    + (fichierPile.get(fic).substring(fichierPile.get(fic)
                            .indexOf("/") + 1));
            System.out.println(newFileName);
            copyFile(new File(fichierPile.get(fic)), new File(newFileName));
        }

    }

    /**
     * copie le fichier source dans le fichier resultat retourne vrai si cela
     * réussit
     */
    public static boolean copyFile(File source, File dest) {
        try {
            // Declaration et ouverture des flux
            java.io.FileInputStream sourceFile = new java.io.FileInputStream(
                    source);
            dest.createNewFile();
            try {
                java.io.FileOutputStream destinationFile = null;

                try {
                    destinationFile = new FileOutputStream(dest);

                    // Lecture par segment de 0.5Mo
                    byte buffer[] = new byte[512 * 1024];
                    int nbLecture;

                    while ((nbLecture = sourceFile.read(buffer)) != -1) {
                        destinationFile.write(buffer, 0, nbLecture);
                    }
                } finally {
                    destinationFile.close();
                }
            } finally {
                sourceFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false; // Erreur
        }

        return true; // Résultat OK
    }

    public static void main(String[] args) throws FileNotFoundException {
        
   

        Bucket buck = new Bucket();
        Processor processor = new Processor();
        String directory = "mini-lib2";
        String directoryResultat = "results";
        double minSimilarity = 0.6;
        Parser p = new Parser();
        ArrayList<Bucket> buckets = p.oneFileOneBucket(directory);
        buckets = processor.bucketize(buckets, minSimilarity);
        File rep = new File(directoryResultat);// creation du repertoire
        rep.mkdir();
        for (int i = 0; i < buckets.size(); i++) {
            try {
                buckets.get(i).createFile(directoryResultat,
                        buckets.get(i).getCallStacks().get(0).getFilename());
            } catch (IOException e) {
            }
        }

    }
}
