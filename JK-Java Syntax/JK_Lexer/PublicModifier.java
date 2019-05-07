package JK_Lexer;

public class PublicModifier implements Modifier{
	public int hashCode() { return 0; }
	public boolean equals(final Object other) {
		return other instanceof PublicModifier; 
	}
	public String toString() {
		return "public";
	}
}
