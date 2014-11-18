package model;

import java.util.ArrayList;

/**
 * design for a CallStack
 *
 * @author Ozzerz
 *
 */
public class CallStack {
    /**
     * a list which contains all the frame
     */
    private ArrayList<Frame> callStack;
    /**
     * the error from this callStack
     */
    private String error;
    /**
     * the filename where we got the callStack
     */
    private String filename;
    /**
     * the groupId of the callStack ONLY FOR EVALUATION
     */
    private String groupId;

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
