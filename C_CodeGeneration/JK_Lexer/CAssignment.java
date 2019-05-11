package JK_Lexer;

public class CAssignment implements CStatement{
	public final CVariableExp v;
	public final CExp e;
	
	public CAssignment(final CVariableExp v, CExp e) {
		this.v=v;
		this.e=e;
	}
	public int hashCode() {
		return (v.hashCode()+
				e.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof CAssignment) {
			final CAssignment otherExp=(CAssignment)other; 
			return (otherExp.v.equals(v) &&
					otherExp.e.equals(e));
		}
		else {
			return false; 
		}
	}
	
	public String toString() {
		if(e instanceof CStringExp) {
			return v.toString()+ " = "+ "\""+e.toString()+"\"";
		}
		else {
			return v.toString() + " = " + e.toString();
		}
	}
}
