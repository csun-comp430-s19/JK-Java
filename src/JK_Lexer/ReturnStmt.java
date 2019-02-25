package JK_Lexer;

public class ReturnStmt implements Statement{
	public final Exp e;
	
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
			final ReturnStmt otherExp=(ReturnStmt)other; 
			return (otherExp.e.equals(e));
		}
		else {
			return false; 
		}
	}
}
