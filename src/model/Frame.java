package model;

/**
 * our design for a Frame
 *
 * @author Ozzerz
 *
 */
public class Frame {
	/**
	 * the source ,example: TypeDeclaration.java:839
	 */
	private String source;
	/**
	 * the method who raise an error example :
	 * org.eclipse.jdt.internal.compiler.ast.TypeDeclaration.parseMethod
	 */
	private String method;

	public Frame(String source, String method) {
		super();
		this.source = source;
		this.method = method;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String toString() {
		return "source: " + source + " method: " + method;

	}
	
	@Override
	public boolean equals(Object frame) {
	    if (frame == null) return false;
	    if (frame == this) return true;
	    if (!(frame instanceof Frame)) return false;
	    
	    Frame frameClass = (Frame) frame;
	    return (this.method.equals(frameClass.method) &&
	            this.source.equals(frameClass.source));
	}

}
