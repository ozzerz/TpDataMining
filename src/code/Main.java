package code;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import classification.Processor;
import model.Bucket;
import model.CallStack;

public class Main {

	public static void main(String[] args)  {


		Bucket buck=new Bucket();
		Processor processor = new Processor();
		String directory = "miniLib";
		String directoryResultat ="result";
		double minSimilarity = 0.6 ;
		Parser p = new Parser();
		ArrayList<Bucket> buckets =p.oneFileOneBucket(directory);
		buckets=processor.bucketize(buckets, minSimilarity);
		File rep = new File(directoryResultat);//création du répértoire
		rep.mkdir();
		for (int i = 0; i < buckets.size(); i++) {
			try {
				buckets.get(i).createFile(directoryResultat,buckets.get(i).getCallStacks().get(0).getFilename());
			} catch (IOException e) {
			}
		}



	}
}
