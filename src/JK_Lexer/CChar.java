package JK_Lexer;

public class CChar implements CType{
	public int hashCode() { return 2; }
	public boolean equals(final Object other) {
		return other instanceof CChar; 
	}
	public String toString() {
		return "char"; 
	}
}
