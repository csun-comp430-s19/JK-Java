package JK_Lexer;

public class Cint implements CType{
	public int hashCode() { return 0; }
	public boolean equals (final Object other) {
		return other instanceof Cint;
	}
	public String toString() {
		return "int";
	}
}
