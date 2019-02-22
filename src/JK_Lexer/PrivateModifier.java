package JK_Lexer;

public class PrivateModifier implements Modifier{
	public int hashCode() { return 1; }
	public boolean equals(final Object other) {
		return other instanceof PrivateModifier; 
	}
	public String toString() {
		return "private"; 
	}
}
