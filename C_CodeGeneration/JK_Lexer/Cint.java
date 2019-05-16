package JK_Lexer;

public class Cint implements CType{
	final public boolean isPointer;
	public Cint(boolean isPointer) {
		this.isPointer = isPointer;
	}
	public int hashCode() { if(isPointer) return 0; else return 5;}
	public boolean equals(final Object other) {
		return other instanceof Cint && ((Cint)other).isPointer == isPointer;
	}
	public String toString() {
		if(isPointer) return "int*";
		else return "int"; 
	}
}
