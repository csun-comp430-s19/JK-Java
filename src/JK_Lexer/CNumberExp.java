package JK_Lexer;

public class CNumberExp implements CExp {
	    public final int number;
	    
	    public CNumberExp(final int number) {
	        this.number = number;
	    }

	    public int hashCode() { return number; }
	    public boolean equals(final Object other) {
	        return (other instanceof CNumberExp &&
	                ((CNumberExp)other).number == number);
	    }
	    public String toString() {
	        return Integer.toString(number);
	    }
	}

