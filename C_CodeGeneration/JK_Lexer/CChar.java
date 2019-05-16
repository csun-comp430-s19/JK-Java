package JK_Lexer;

public class CChar implements CType{
	final public boolean isPointer;
	public CChar(boolean isPointer) {
		this.isPointer = isPointer;
	}
	public int hashCode() { if(isPointer) return 2; else return 3;}
	public boolean equals(final Object other) {
		return other instanceof CChar && ((CChar)other).isPointer == isPointer; 
	}
	public String toString() {
		if(isPointer) return "char*";
		else return "char"; 
	}
}
