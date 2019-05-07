package JK_Lexer;

public class CVariableExp implements CExp{
	    public final String name;

	    public CVariableExp(final String name) {
	        this.name = name;
	    }

	    public int hashCode() { return name.hashCode(); }
	    public boolean equals(final Object other) {
	        return (other instanceof CVariableExp &&
	                ((CVariableExp)other).name.equals(name));
	    }
	    public String toString() {
	        return name;
	    }
}