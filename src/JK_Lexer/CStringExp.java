package JK_Lexer;

public class CStringExp implements CExp{
	public final String fullstring; 
	
	public CStringExp(final String fullstring) {
		this.fullstring=fullstring; 
	}
	public int hashCode() { return fullstring.hashCode(); }
	public boolean equals(final Object other) {
		return (other instanceof CStringExp &&
				((CStringExp)other).fullstring.equals(fullstring)); 
	}
	 public String toString() {
	        return fullstring;
	}
}