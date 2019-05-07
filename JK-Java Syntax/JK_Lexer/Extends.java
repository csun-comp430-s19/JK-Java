package JK_Lexer;

public class Extends {
	String name;
	
	public Extends(String name) {
		this.name = name;
	}
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals(final Object other) {
		if(other instanceof Extends) {
			return ((Extends)other).name.equals(this.name);
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		return "Extends: " + name.toString();
	}
}
