package JK_Lexer;

public class VoidType implements Type{
	public int hashCode() { return 1; }
	public boolean equals(final Object other) {
		return other instanceof VoidType;
	}
	public String toString() {
		return "void"; 
	}
}
