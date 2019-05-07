package JK_Lexer;

public class CustomType implements Type{
	public final String name; 
	public CustomType(String name) {
		this.name=name; 
	}
	public int hashCode() { return name.hashCode(); }
	public boolean equals (final Object other) {
		return (other instanceof CustomType && 
				((CustomType)other).name.equals(name));
	}
	public String toString() {
		return name; 
	}
}
