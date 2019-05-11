package JK_Lexer;

public class CReturn implements CStatement{
	
	public final CExp e;
	
	public CReturn(final CExp e) {
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
		if(e instanceof CStringExp) {
			return "return " + "\""+e.toString()+"\"";
		}
		else {
			return "return " + e.toString();
		}
	}
}
