package JK_Lexer;

public class VariableDecExp implements Statement{
	public final Type type;
	public final VariableExp var; 
	
	public VariableDecExp(final Type type,
						  final VariableExp var) {
		this.type=type;
		this.var=var;
	}
	public int hashCode() {
		return (type.hashCode()+
				var.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof VariableDecExp) {
			final VariableDecExp otherExp=(VariableDecExp)other; 
			return (otherExp.type.equals(type) &&
					otherExp.var.equals(var));
		}
		else {
			return false; 
		}
	}
	public String toString() {
		String s = "Type: " + type.toString() + "\n"+
				   "Variable: " + var.toString();
		return s;
	}
}
