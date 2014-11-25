package code;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import classification.Processor;
import model.Bucket;
import model.CallStack;

public class Main {
    

    public static void main(String[] args) {
        
        Parser p = new Parser();
        ArrayList<Bucket> oneFileOneBucket = p.oneFileOneBucket("mini-lib");
        
        Processor processor = new Processor();
        System.out.println("----> Nombre de buckets avant clusterisation : " + oneFileOneBucket.size());
        ArrayList<Bucket> bucketization = processor.bucketize(oneFileOneBucket, 0.8);
        System.out.println("----> Nombre de buckets apres clusterisation : " + bucketization.size());
        
        /* MAX TES SYSOUT PRENNENT TROP DE PLACE
        System.out.println("----------callStack--------------");
        System.out.println(callStack.toString());
        System.out.println("----------callStack--------------");
        System.out.println(c2.toString());
        //p.addGroupId(callStack, "lib/206001.json"); <==Euh ouais mais POURQUOI
        //p.addGroupId(c2, "lib/206001.json"); <==Même question
         */
        
        /*
        
        String directory = "lib";
        Parser p = new Parser();
        CallStack callStack = p.read("lib/206001.json");
        CallStack c2 = p.read("lib/206001.json");
        Processor processor = new Processor();
        
        CallStack c3 = p.read("lib/49499.json");
        CallStack c4 = p.read("lib/48643.json");
        CallStack c5 = p.read("lib/49608.json");
        CallStack c6 = p.read("lib/51358.json");
        CallStack c7 = p.read("lib/49406.json");
        
        Processor processor = new Processor();
        System.out.println("----------- Similarity ------------");
        System.out.println("-------- callstacks identiques :");
        System.out.println(processor.similarity(callStack, c2));
        
        System.out.println("-------- callstacks identiques :");
        System.out.println(processor.similarity(c2, c2));
        
        System.out.println("-------- callstacks identiques :");
        System.out.println(processor.similarity(c3, c3));
        
        System.out.println("-------- callstacks identiques :");
        System.out.println(processor.similarity(c6, c6));
        
        System.out.println("-------- callstacks differentes :");
        System.out.println(processor.similarity(callStack, c4));
        
        System.out.println("-------- callstacks du meme bucket :");
        System.out.println(processor.similarity(c3, c4));
        
        System.out.println("-------- callstacks du meme bucket :");
        System.out.println(processor.similarity(c3, c5));
        
        System.out.println("-------- callstacks du meme bucket :");
        System.out.println(processor.similarity(c6, c7));
        */
        
        /*
        //CREATION BUCKET 
        ArrayList<CallStack> allCallStack =    p.readAll(directory); //add all id 
          for (int i = 0; i <allCallStack.size(); i++) { 
        	  
        	  p.addGroupIdAndDuplicateId(allCallStack.get(i),directory+"/"+allCallStack.get(i).getFilename());
        	  }
          	System.out.println("--------------on  a tout les id"); 
          	Bucket bucket = new Bucket();
          	ArrayList<Bucket>allBuck=bucket.creationAllBucket(allCallStack);
          	System.out.println("-----------------on a tout les bucket"); 
          	File  rep=new File("bucket"); rep.mkdir(); 
          	for (int i=0;i<allBuck.size();i++) 
          	{ try 
          	{
          allBuck.get(i).createFile("bucket"); 
          } catch (IOException e) { //
         } }
         */
     }
}
