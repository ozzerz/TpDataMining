package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import model.Bucket;
import model.CallStack;
import model.Frame;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Contiens les méthode pour parser les Json
 *
 * @author Ozzerz
 *
 */
public class Parser {
	/**
	 * Obtenir les callStacks à partir d'un fichier JSon
	 *
	 * @param filename
	 *           le nom du fichier
	 * @return a une CallStack contenant toutes les Frames
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
	 * Ajoute le groupId pour une callStack SEULEMENT POUR EVALUTATION
	 * @param callStack la CallStack que l'on va modifier
	 * @param filename le fichier correspondant
	 */
	public void addGroupIdAndDuplicateId(CallStack callStack , String filename)
	{
		JSONParser parser = new JSONParser();

		try {

			Object obj = parser.parse(new FileReader(filename));

			JSONObject jsonObject = (JSONObject) obj;
			String id = (String) jsonObject.get("groupId");
			callStack.setGroupId(id);

			String dupId = (String) jsonObject.get("duplicateId");
			if (dupId==null)
			{
				callStack.setDuplicateId("-1");
			}
			else
			{
				callStack.setDuplicateId(dupId);
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {

			e.printStackTrace();
		}


	}

	/**
	 * parse tout les fichiers d'un répertoire
	 *
	 * @param directoryName
	 * @return une liste qui contient toute les callStacks
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

	/**
	 * un bucket pour chaque callstack
	 * un unique bucket pourra s'agrandir par la suite : voir Main.clustering
	 * @param directoryName
	 * @return
	 */
	public ArrayList<Bucket> oneFileOneBucket(String directoryName) {
	    try {
	        File directory = new File(directoryName);
	        String[] allFileName = directory.list();
	        int length =  allFileName.length;

	        // chaque callstack est un unique bucket
	        ArrayList<Bucket> allBuckets = new ArrayList<Bucket>(length);

	        for (int i = 0; i < length; i++) {
	            CallStack cs = read(directoryName + "/" + allFileName[i]);
	            Bucket bucket = new Bucket();
	            bucket.addCallStack(cs);

	            allBuckets.add(bucket);

	        }

	        return allBuckets;
	    } catch (NullPointerException e) {
	        System.err.println("Repertoire destination inexistant.");
	        return null;
	    }


	}

}
