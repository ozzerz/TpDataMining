package code;

import java.util.ArrayList;

import model.CallStack;

public class Main {

	public static void main(String[] args) {

		Parser p = new Parser();
		CallStack callStack = p.read("lib/100004.json");
		System.out.println("----------callStack--------------");
		System.out.println(callStack.toString());
		System.out.println("----------callStack--------------");
		ArrayList<CallStack> bucket = p.readAll("lib");
		/*System.out.println("----------Bucket--------------");
		for (int i = 0; i < bucket.size(); i++) {
			System.out.println(bucket.get(i).toString());
		}
		System.out.println("----------Bucket--------------");
	*/
	}
}
