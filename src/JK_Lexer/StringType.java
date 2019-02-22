package JK_Lexer;

public class StringType implements Type{
	public int hashCode() { return 2; }
	public boolean equals(final Object other) {
		return other instanceof StringType; 
	}
	public String toString() {
		return "String"; 
	}
}
