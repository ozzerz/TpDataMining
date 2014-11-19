package code;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import classification.Processor;
import model.Bucket;
import model.CallStack;

public class Main {

    public static void main(String[] args) {
        String directory = "lib";
        Parser p = new Parser();
        CallStack callStack = p.read("lib/206001.json");
        System.out.println("----------callStack--------------");
        System.out.println(callStack.toString());
        p.addGroupId(callStack, "lib/206001.json");

        CallStack c2 = p.read("lib/206001.json");
        System.out.println("----------callStack--------------");
        System.out.println(c2.toString());
        p.addGroupId(c2, "lib/206001.json");
        
        Processor processor = new Processor(8);
        System.out.println("----------- Similarity ------------");
        System.out.println(processor.similarity(callStack, c2));

        /*
         * //CREATION BUCKET ArrayList<CallStack> allCallStack =
         * p.readAll(directory); //add all id for (int i = 0; i <
         * allCallStack.size(); i++) { p.addGroupId(allCallStack.get(i),
         * directory+"/"+allCallStack.get(i).getFilename()); }
         * System.out.println("--------------on  a tout les id"); Bucket bucket
         * = new Bucket();
         * ArrayList<Bucket>allBuck=bucket.creationAllBucket(allCallStack);
         * System.out.println("-----------------on a tout les bucket"); File
         * rep=new File("bucket"); rep.mkdir(); for (int
         * i=0;i<allBuck.size();i++) { try {
         * 
         * allBuck.get(i).createFile("bucket"); } catch (IOException e) { //
         * TODO Auto-generated catch block e.printStackTrace(); } }
         */}

}
