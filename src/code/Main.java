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

    public static void createBuckets(String inputDir, String outputDir) {

        Parser p = new Parser();
        ArrayList<CallStack> allCallStack = p.readAll(inputDir); // add all id
        for (int i = 0; i < allCallStack.size(); i++) {

            p.addGroupIdAndDuplicateId(allCallStack.get(i), inputDir + "/"
                    + allCallStack.get(i).getFilename());
        }
        Bucket bucket = new Bucket();
        ArrayList<Bucket> allBuck = bucket.creationAllBucket(allCallStack);

        File rep = new File(outputDir);// création du répértoire
        rep.mkdir();
        for (int i = 0; i < allBuck.size(); i++) {
            try {
                allBuck.get(i).createFile(outputDir);
            } catch (IOException e) { //
            }
        }

    }

    public static void recup800Bucket(String repFin) throws IOException {
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

        EnregistrePile(fichierPile, repFin);

    }

    public static void EnregistrePile(ArrayList<String> buckets, String repFin)
            throws IOException {
        for (int i = 0; i < buckets.size(); i++) {
            File fichier = new File(buckets.get(i).trim());
            System.out.println(fichier.toPath());
            if (fichier.exists()) {
                
                System.out.println("test 2 " + buckets.get(i).trim());
                InputStream ips = new FileInputStream(fichier);
                InputStreamReader ipsr = new InputStreamReader(ips);
                BufferedReader br = new BufferedReader(ipsr);
                String ligne = br.readLine();
                while (ligne != null) {
                    System.out.println(ligne);
                    System.out.println("test  " + "lib/" + ligne);
                    String newFileName = repFin + "/" + ligne;
                    if (new File("lib/" + ligne).exists()) {
                        copyFile(new File("lib/" + ligne),
                                new File(newFileName));
                    }
                    ligne = br.readLine();

                }
            }
        }

    }

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

    public static void main(String[] args) throws IOException {
        //createBuckets("800/", "800bucketed");
        //recup800Bucket("800buketed");

        /*
         * Bucket buck = new Bucket(); Processor processor = new Processor();
         * String directory = "mini-lib2"; String directoryResultat = "results";
         * double minSimilarity = 0.6; Parser p = new Parser();
         * ArrayList<Bucket> buckets = p.oneFileOneBucket(directory); buckets =
         * processor.bucketize(buckets, minSimilarity); File rep = new
         * File(directoryResultat);// creation du repertoire rep.mkdir(); for
         * (int i = 0; i < buckets.size(); i++) { try {
         * buckets.get(i).createFile(directoryResultat,
         * buckets.get(i).getCallStacks().get(0).getFilename()); } catch
         * (IOException e) { } }
         */
    }
}
