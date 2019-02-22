package JK_Lexer;

public class ObjectType implements Type{
	public int hashcode() { return 3; }
	public boolean equals(final Object other) {
		return other instanceof ObjectType; 
	}
	public String toString() {
		return "Object";
	}
}
