package JK_Lexer;

public class ReturnStmt implements Statement{
	public Exp e;
	
	public ReturnStmt(final Exp e) {
		this.e=e;
	}
	
	public ReturnStmt() {
		e = null;
	}
	
	public int hashCode() {
		return (e.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof ReturnStmt) {
			if(e==null) {
				final ReturnStmt otherExp=(ReturnStmt)other; 
				if(otherExp.e==null)
					return true;
				return false; 
			}	
			final ReturnStmt otherExp=(ReturnStmt)other; 
			if(otherExp.e==null)
				return false; 
			return (otherExp.e.equals(e));
		}
		else {
			return false; 
		}
	}
	
	public String toString() {
		if(e==null)
			return "return";
		else
			return "return"+e.toString(); 
	}
}
