package JK_Lexer;

public class StringExp implements Exp{
	public final String fullstring; 
	
	public StringExp(final String fullstring) {
		this.fullstring=fullstring; 
	}
	public int hashCode() { return fullstring.hashCode(); }
	public boolean equals(final Object other) {
		return (other instanceof StringExp &&
				((StringExp)other).fullstring.equals(fullstring)); 
	}
	 public String toString() {
	        return fullstring;
	}
}
