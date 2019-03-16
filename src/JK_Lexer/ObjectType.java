package JK_Lexer;

public class ObjectType implements Type{
	public final String className;
	public ObjectType(String className) {
		this.className = className;
	}
	public int hashcode() { return className.hashCode(); }
	public boolean equals(final Object other) {
		return (other instanceof ObjectType && 
				((ObjectType)other).className.equals(className)); 
	}
	public String toString() {
		return className;
	}
}
