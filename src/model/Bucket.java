package model;

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
		 return false;
		
	}
	
	
}
