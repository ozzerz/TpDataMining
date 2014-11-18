package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import model.CallStack;
import model.Frame;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * contains all method to parse one on many Json File
 *
 * @author Ozzerz
 *
 */
public class Parser {
	/**
	 * get the callStack from a Json File
	 *
	 * @param filename
	 *            Json file name
	 * @return a callStack which contains all the frame
	 * @see CallStack
	 * @see Frame
	 */
	public CallStack read(String filename) {
		JSONParser parser = new JSONParser();
		CallStack callStack = new CallStack(filename.substring(filename.lastIndexOf("/")+1));

		try {

			Object obj = parser.parse(new FileReader(filename));

			JSONObject jsonObject = (JSONObject) obj;
			JSONArray msg = (JSONArray) jsonObject.get("traces");
			Iterator<String> iterator = msg.iterator();
			JSONObject compleateJSon = (JSONObject) msg.get(0);
			// keep the error
			String erreur = compleateJSon.get("exceptionType").toString();
			//String groupId = compleateJSon.get("groupId").toString();
			//System.out.println(groupId);
			callStack.setError(erreur);
			JSONArray callStackArray = (JSONArray) compleateJSon
					.get("elements");
			// save all the Frame
			Iterator<JSONObject> iteratorCallStack = callStackArray.iterator();
			while (iteratorCallStack.hasNext()) {
				JSONObject slide = iteratorCallStack.next();
				callStack.addFrame(new Frame(slide.get("source").toString(),
						slide.get("method").toString()));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return callStack;
	}

	
	/**
	 * add the groupId to a callStack ONLY USE FOR EVALUATION
	 * @param callStack the callStack we will modify
	 * @param filename the file corresponding
	 */
	public void addGroupId(CallStack callStack , String filename)
	{
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(filename));

			JSONObject jsonObject = (JSONObject) obj;
			System.out.println("----------Group--------------");
			String msg = (String) jsonObject.get("groupId");	
			System.out.println(msg);
			System.out.println("----------Group--------------");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * parse all the file from a directory
	 *
	 * @param directoryName
	 * @return a list which contains all the callStack from all the JsonFile
	 */
	public ArrayList<CallStack> readAll(String directoryName) {
		ArrayList<CallStack> bucket = new ArrayList<CallStack>();
		File directory = new File(directoryName);
		String[] allFileName = directory.list();
		for (int i = 0; i < allFileName.length; i++) {
			bucket.add(read(directoryName + "/" + allFileName[i]));

		}

		return bucket;
	}

}
