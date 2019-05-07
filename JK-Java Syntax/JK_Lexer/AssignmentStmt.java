package JK_Lexer;

import java.util.ArrayList;

public class AssignmentStmt implements Statement{
	public final VariableExp v;
	public final Exp e;
	
	public AssignmentStmt(final VariableExp v, Exp e) {
		this.v=v;
		this.e=e;
	}
	public int hashCode() {
		return (v.hashCode()+
				e.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof AssignmentStmt) {
			final AssignmentStmt otherExp=(AssignmentStmt)other; 
			return (otherExp.v.equals(v) &&
					otherExp.e.equals(e));
		}
		else {
			return false; 
		}
	}
	
	public String toString() {
		String s = "Variable: " + v.toString() + "\n"+
				   "Assign to: " + e.toString();
		return s;
	}
}