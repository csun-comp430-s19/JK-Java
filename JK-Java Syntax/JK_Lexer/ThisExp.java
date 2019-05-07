package JK_Lexer;

public class ThisExp implements Exp{
	public final Exp variable; 
	
	public ThisExp(final Exp variable) {
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
