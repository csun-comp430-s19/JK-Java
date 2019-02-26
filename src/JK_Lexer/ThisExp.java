package JK_Lexer;

public class ThisExp implements Exp{
	public final String variable; 
	
	public ThisExp(final String variable) {
		this.variable=variable;
	}
	public int hashCode() {
		return variable.hashCode(); 
	}
	public boolean equals(final Object other) {
		if(other instanceof ThisExp) {
			final ThisExp otherExp=(ThisExp)other; 
			return otherExp.variable.equals(variable);
		}
		else {
			return false; 
		}
	}
}
