package model;

import java.util.ArrayList;

/**
 * design Pour une CallStack
 *
 * @author Ozzerz
 *
 */
public class CallStack {
    /**
     *Une liste contenant chaque Frame
     */
    private ArrayList<Frame> callStack;
    /**
     * L'erreur de la pile d'appel
     */
    private String error;
    /**
     * le nom du fichier d'ou est issue la callStack
     */
    private String filename;
    /**
     * Le groupId SEULEMENT POUR EVALUTATION
     */
    private String groupId;
    /**
     * le duplicateID SEULEMENT POUR EVALUTATION
     */
    private String duplicateId;

    public String getDuplicateId() {
		return duplicateId;
	}

	public void setDuplicateId(String duplicateId) {
		this.duplicateId = duplicateId;
	}

	public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public CallStack(String filename) {
        super();
        this.filename = filename;
        callStack = new ArrayList<Frame>();
    }

    public CallStack() {
        super();
        this.error = "";
        callStack = new ArrayList<Frame>();
    }

    public ArrayList<Frame> getCallStack() {
        return callStack;
    }

    public void setCallStack(ArrayList<Frame> callStack) {
        this.callStack = callStack;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void addFrame(Frame frame) {
        this.callStack.add(frame);

    }

    public String toString() {
        String allFrame = "filename : " + filename
                + System.getProperty("line.separator");
        allFrame = allFrame + "exceptionType: " + error
                + System.getProperty("line.separator");
        for (int i = 0; i < callStack.size(); i++) {
            if (i + 1 < callStack.size()) {
                allFrame = allFrame + callStack.get(i).toString()
                        + System.getProperty("line.separator");
            } else {
                allFrame = allFrame + callStack.get(i).toString();
            }
        }

        return allFrame;
    }

}
