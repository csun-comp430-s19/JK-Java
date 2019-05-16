package JK_Lexer;

public class CVoid implements CType{
	final public boolean isPointer;
	public CVoid(boolean isPointer) {
		this.isPointer = isPointer;
	}
	public int hashCode() { if(isPointer) return 1; else return 4;}
	public boolean equals(final Object other) {
		return other instanceof VoidType && ((CVoid)other).isPointer == isPointer;
	}
	public String toString() {
		if(isPointer) return "void*";
		else return "void"; 
	}
}
