package JK_Lexer;

public class InstanceDecExp {
	public final Modifier mod;
	public final VariableDecExp var;
	
	public InstanceDecExp(final Modifier mod,
						  final VariableDecExp var) {
		this.mod=mod;
		this.var=var;
	}
	public int hashCode() {
		return (mod.hashCode()+
				var.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof InstanceDecExp) {
			final InstanceDecExp otherExp=(InstanceDecExp)other; 
			return (otherExp.mod.equals(mod) &&
					otherExp.var.equals(var));
		}
		else {
			return false; 
		}
	}
	public String toString() {
		String s = "Modifier: " + mod.toString() + "\n"+
				   "Variable :" + var.toString();
		return s;
	}
}
