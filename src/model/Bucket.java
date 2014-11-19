package model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * design for a bucket
 *
 * @author Ozzerz
 *
 */
public class Bucket {
    
	/**
	 * the name of our bucket
	 */
	private String name;
	/**
	 * the list of all callStack
	 */
	private ArrayList<CallStack> callStacks ;

	private Bucket(String name)
	{
		this.name=name;
		this.callStacks=new ArrayList<CallStack>();
	}

	public Bucket()
	{
		this.callStacks=new ArrayList<CallStack>();
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
	 * add a callStack to this bucket
	 * @param callStack the callStack we want to add
	 */
	public void addCallStack(CallStack callStack)
	{
		this.callStacks.add(callStack);
	}
	/**
	 *  to know it the callStack belong to this bucket
	 * @param callStack
	 * @return true if the callStack belong to this bucket , false neither
	 */
	public boolean isOnBucket(CallStack callStack){
		return callStack.getGroupId().equals(name);

	}
	/**
	 * create the bucket FOR EVALUATION
	 * @param allCallStacks
	 * @return
	 */
	public ArrayList<Bucket> creationAllBucket(ArrayList<CallStack> allCallStacks){
		ArrayList<Bucket> allBucket = new ArrayList<Bucket>();
		boolean verif =false;
		for (int call=0;call<allCallStacks.size();call++)
		{verif=false;
			for(int buck=0;buck<allBucket.size();buck++){
				if(allBucket.get(buck).isOnBucket(allCallStacks.get(call))){
					allBucket.get(buck).addCallStack(allCallStacks.get(call));
					verif =true;
				}


			}
			if(!verif){
				
				if(allCallStacks.get(call).getDuplicateId().equals("-1"))
				{
				Bucket bu=new Bucket(allCallStacks.get(call).getGroupId());
				bu.addCallStack((allCallStacks.get(call)));
				allBucket.add(bu);
				verif=false;
				}
				else
				{
					Bucket bu=new Bucket(allCallStacks.get(call).getDuplicateId());
					bu.addCallStack((allCallStacks.get(call)));
					allBucket.add(bu);
					verif=false;
				}
				}

		}
		return allBucket;
	}

	/**
	 * create a bucket file on the directory (need to exist for work)
	 * @param directory
	 * @throws IOException
	 */
	public void createFile(String directory) throws IOException
	{
		String nomFichier=directory+"/"+this.name+".txt";
		System.out.println(nomFichier);
		File file = new File (nomFichier);
		file.createNewFile();
		FileWriter writer = null;
		String texte = this.toString();
		try{
		     writer = new FileWriter(nomFichier, true);
		     writer.write(texte,0,texte.length());
		}catch(IOException ex){
		    ex.printStackTrace();
		}finally{
		  if(writer != null){
		     writer.close();
		  }
		}

	}

	public String toString(){
		String str="";
		for(int i=0;i<callStacks.size();i++)
		{
			if (i + 1 < callStacks.size()) {
			str=str+callStacks.get(i).getFilename()+System.getProperty("line.separator");
			}
			else
			{
				str=str+callStacks.get(i).getFilename();
			}
		}


		return str;
	}
}
