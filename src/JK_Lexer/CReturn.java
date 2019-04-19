package JK_Lexer;

public class CReturn implements CStatement{
	
	public final Exp e;
	
	public CReturn(final Exp e) {
		this.e=e;
	}
	
	public CReturn() {
		e = null;
	}
	
	public int hashCode() {
		return (e.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof CReturn) {
			final CReturn otherExp=(CReturn)other; 
			return (otherExp.e.equals(e));
		}
		else {
			return false; 
		}
	}
	
	public String toString() {
		return "return " + e.toString() +";";
	}
}
